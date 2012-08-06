#define LOCAL_MEM_SIZE 64
#define RADIUS 0.004
#define WITH_COLLISION 1
#define GRIDLEN 100

float xor128() {
  int x = 123456789;
  int y = 362436069;
  int z = 521288629;
  int w = 88675123;
  int t;
 
  t = x ^ (x << 11);
  x = y; y = z; z = w;
  return w = w ^ (w >> 19) ^ (t ^ (t >> 8));
}
constant sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE| CLK_FILTER_LINEAR| CLK_ADDRESS_REPEAT;

// epsilon environment to find the minimum height
constant float minEpsilon = 0.004f;

// OpenGl reflect method (may contain errors)
float4 reflect(float4 normal, float4 einfall) {
	float4 norm = normalize(normal);
	return (float4)(-2*dot(norm.s012,einfall.s012)*norm.s012 + einfall.s012, 0.0f);
}


//////////////////////////////////////////////////////////
// Grid methods                                         //
//////////////////////////////////////////////////////////
typedef struct
{
    int num_per_dim;        // number of cells per dimension
    int max_p;              // max particles per cell
    global int* counter;
    global int* cells;

} grid_t;

typedef struct
{
    int cnt_id;
    int cell_id;
} cell_id_t;

/**
 * Returns the grid id of a given position
 * @param grid  the grid
 * @param pos   the position
 * @param dpos  grid-cell shift
 * @return int-vector (counter array id, index array id)
 */
cell_id_t g_get_cell_id(grid_t *grid, float3 pos, int3 dpos)
{
    int dl = grid->num_per_dim;
    int3 gp = (int3)((int)(pos.s0*dl),
                     (int)(pos.s1*dl),
                     (int)(pos.s2*dl));
    
    int counter_id =    (gp.s0+dpos.s0)
                + (gp.s1+dpos.s1)*dl
                + (gp.s2+dpos.s2)*dl*dl;

    int maxid = dl*dl*dl;
    
    cell_id_t cid;

    if (counter_id >= maxid || counter_id < 0)
    {
        cid.cnt_id = cid.cell_id = -1;
        return cid;
    }
    
    int mp = grid->max_p;
    int index_id =  (gp.s0+dpos.s0)*mp
                    + (gp.s1+dpos.s1)*dl*mp
                    + (gp.s2+dpos.s2)*dl*dl*mp;
    cid.cnt_id = counter_id;
    cid.cell_id = index_id;
    return cid;
}


void g_add_particle(grid_t *grid, float3 pos)
{
    cell_id_t cid = g_get_cell_id(grid, pos, (int3)(0));
    if (cid.cnt_id != -1)
    {
        // valid id
        int old_num = atomic_inc(grid->counter+cid.cnt_id);
        grid->cells[cid.cell_id+old_num] = get_global_id(0);
    }
}


//////////////////////////////////////////////////////////
// Geometric methods                                    //
//////////////////////////////////////////////////////////
float3 getNormal(image2d_t heightmap, float2 pos)
{
	int2 dim = get_image_dim(heightmap);
	float dx = 1.0/(float)dim.x;
	float dy = 1.0/(float)dim.y;


	float h = read_imagef(heightmap,sampler,pos).s0;
	float3 hvec = (float3)(pos.s0,h,pos.s1);

	float v01 = read_imagef(heightmap,sampler,pos+(float2)(-dx,0.0)).s0;
	float v10 = read_imagef(heightmap,sampler,pos+(float2)(0.0,-dy)).s0;
	float v12 = read_imagef(heightmap,sampler,pos+(float2)(0.0,dy)).s0;
	float v21 = read_imagef(heightmap,sampler,pos+(float2)(+dx,0.0)).s0;

    	float3 v1 = (float3)(-dx, v01, 0.0)-hvec; 
    	float3 v4 = (float3)(0.0, v10, -dy)-hvec; 
    	float3 v2 = (float3)(0.0, v12, dy)-hvec; 
    	float3 v3 = (float3)(dx,  v21,  0.0)-hvec; 

	float3 c1 = cross(v1,v2);
	float3 c2 = cross(v2,v3);
	float3 c3 = cross(v3,v4);
	float3 c4 = cross(v4,v1);

	return normalize(c1+c2+c3+c4);
}

#define GROUND_NORM_DAMPING 0.0001
#define GROUND_VELO_DAMPING 0.9
#define COLLISION_DAMPING -0.00001;

//////////////////////////////////////////////////////////
// Particle Kernel                                      //
//////////////////////////////////////////////////////////
kernel void particle_sim
(
    global float4* position, 
    global float4* velos,
    image2d_t heightmap,
    image2d_t normalmap,
    global int* g_counter,
    global int* g_cells,
    int g_num_cells,
    int g_max_particles,
    float dt) 
{
    grid_t grid = { g_num_cells,
                    g_max_particles,
                    g_counter,
                    g_cells};


    int mygid = get_global_id(0);
    float4 mypos = position[mygid];
    float4 myvel = velos[mygid];
    float4 height = read_imagef(heightmap, sampler, mypos.s02);
    float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);


    //////////////////////////////////////////////////////////
    // gravity and ground interaction                       //
    //////////////////////////////////////////////////////////
    float4 gravity = (float4)(0,-0.00001,0,0);
    myvel += gravity;
    float3 dVelo = (float3)(0);  	
    if(mypos.s1 <= height.s0+RADIUS)
    {
        mypos.s1 = height.s0+RADIUS;
        dVelo = normal.s012*GROUND_NORM_DAMPING;
        myvel += (float4)(dVelo,1);
        myvel *= GROUND_VELO_DAMPING;
    }


    // add particle to counter and cell grid
    g_add_particle(&grid, mypos.s012);

    //////////////////////////////////////////////////////////
    // particle-particle interaction                        //
    //////////////////////////////////////////////////////////   
    for(int i=-1; i<=1; i++){
        for(int j=-1; j<=1; j++){
            for(int k=-1; k<=1; k++){
                cell_id_t cid = g_get_cell_id(&grid, mypos.s012, (int3)(i,j,k));
                if (cid.cnt_id == -1) continue;
                int num = grid.counter[cid.cnt_id];
                num = num > grid.max_p ? grid.max_p : num;
                
                for (int m = 0; m < num; m++) {
                    ///////////////////////////////////////////////////////////
                    int other_gid = grid.cells[cid.cell_id+m];
                    if (other_gid == mygid) continue;
                    float4 other_pos = position[other_gid];
                    float4 other_vel = velos[other_gid];                    
                    float4 n = (other_pos - mypos);
                    float distance = length(n.s012);
                    if(distance < RADIUS*2)
                    {
                        if(mypos.s1 > other_pos.s1){
                                myvel-=gravity*0.1;
                        }
                        myvel+=normalize(n)*COLLISION_DAMPING;
                    }
                    ///////////////////////////////////////////////////////////
                }
            }
        }
    }
    
    barrier(CLK_GLOBAL_MEM_FENCE);

    mypos.s012 += myvel.s012*4;


    position[mygid] = mypos;
    velos[mygid] = myvel;


}



//////////////////////////////////////////////////////////
// Countergrid cleaning Kernel                          //
//////////////////////////////////////////////////////////
kernel void gridclear_sim
(
    global int* g_counter) 
{
    g_counter[get_global_id(0)] = 0;
}	