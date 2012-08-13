#define LOCAL_MEM_SIZE 64
#define RADIUS 0.006
#define WITH_COLLISION 1
#define GRIDLEN 125

#define DAMPING 0.25
#define SPRING 1
#define SHEAR 0.001
// Methode zur Berechnung der neuen Geschwindigkeit nach einer Kollision
float4 collide(
float4 n, 
float4 vi, 
float4 vj, 
float distance)
{
	// Vektoren 
    float4 norm = normalize(n);
    float4 relVelo = vj - vi;
    float d = dot(norm, relVelo);
    float4 tanVelo = relVelo - d*norm;
    float s = -SPRING*(RADIUS + RADIUS - distance);
    // Neue Geschwindigkeit
    return (SHEAR*tanVelo + DAMPING*relVelo + s*norm);
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
    gp = gp + dpos;
    cell_id_t cid;

    if (gp.s0 < 0 || gp.s0 >= dl ||
        gp.s1 < 0 || gp.s1 >= dl ||
        gp.s2 < 0 || gp.s2 >= dl)
    {
        cid.cnt_id = cid.cell_id = -1;
        return cid;
    }

    int counter_id =    (gp.s0)
                + (gp.s1)*dl
                + (gp.s2)*dl*dl;

    int maxid = dl*dl*dl;

    if (counter_id >= maxid || counter_id < 0)
    {
        cid.cnt_id = cid.cell_id = -1;
        return cid;
    }
    
    int mp = grid->max_p;
    cid.cnt_id = counter_id;
    cid.cell_id = counter_id*mp;
    return cid;
}


void g_add_particle(grid_t *grid, float3 pos)
{
    cell_id_t cid = g_get_cell_id(grid, pos, (int3)(0));
    if (cid.cnt_id != -1)
    {
        // valid id
        int mp = grid->max_p;
        int old_num = atomic_inc(grid->counter+cid.cnt_id);
        if (old_num < mp) 
        {
            grid->cells[cid.cell_id+old_num] = get_global_id(0);
        }
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

#define GROUND_NORM_DAMPING 0.00004
//#define GROUND_VELO_DAMPING 0.8
#define GROUND_VELO_DAMPING 0.9
#define COLLISION_DAMPING -0.00001;
#define COLLIDE_DAMPING 0.0002;

// gridinfo defines
#define SPACE_PARTICLE_RADIUS 0 
#define SPACE_X 1
#define SPACE_Y 2
#define SPACE_Z 3
#define SPACE_LENGTH 4
#define GRID_LENGTH 5
#define GRID_MAXP 6

//////////////////////////////////////////////////////////
// Dynamic Grid                                         //
//////////////////////////////////////////////////////////
typedef struct
{
    float4 s;
    float slen;
    float glen;
    float gmax;
    global int *counter;
    global int *cells;
} dgrid_t;

int dg_cell_id(dgrid_t *g, float4 pos, int4 dpos)
{
    // calculate pos in cube between (0,0,0) and (1,1,1)
    // => scale and translate
    float4 cube_pos = (pos - g->s) / g->slen;
    
    float glen = g->glen;
    int4 gp = (int4)((int)(cube_pos.s0*glen),
                     (int)(cube_pos.s1*glen),
                     (int)(cube_pos.s2*glen),0);
    gp = gp + dpos;

    if (gp.s0 < 0 || gp.s0 >= glen ||
        gp.s1 < 0 || gp.s1 >= glen ||
        gp.s2 < 0 || gp.s2 >= glen)
    {
        return -1;
    }   
    
    int counter_id =    (gp.s0)
                + ((gp.s1)
                + (gp.s2)*glen)*glen;

    return counter_id;
}


void dg_add_particle(dgrid_t *g, float4 pos)
{
    int cid = dg_cell_id(g, pos, (int4)(0));
    if (cid != -1)
    {
        // valid id
        int mp = g->gmax;
        int old_num = atomic_inc(g->counter+cid);
        if (old_num < mp) 
        {
            g->cells[cid*mp+old_num] = get_global_id(0);
        }
    }



}

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
    float dt,
    float random,
    global float4* pos_inbuf,
    global float4* vel_inbuf,
    global float* info) 
{
    grid_t grid = { g_num_cells,
                    g_max_particles,
                    g_counter,
                    g_cells};

    // dynamic grid
    dgrid_t g = {   (float4)(info[SPACE_X],info[SPACE_Y],info[SPACE_Z],0),
                    info[SPACE_LENGTH],
                    info[GRID_LENGTH],
                    info[GRID_MAXP],
                    g_counter,
                    g_cells};

    int mygid = get_global_id(0);
    float4 mypos = pos_inbuf[mygid];
    float4 myvel = vel_inbuf[mygid];
    float4 height = read_imagef(heightmap, sampler, mypos.s02);
    float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);


    //////////////////////////////////////////////////////////
    // gravity and ground interaction                       //
    //////////////////////////////////////////////////////////
    float4 gravity = (float4)(0,-0.00001,0,0);
    myvel += gravity;
    float4 dVelo = (float4)(0);  
    
    if(mypos.s1 <= height.s0+RADIUS)
    {
        mypos.s1 = height.s0+RADIUS;
        dVelo = (float4)(normal.s012*GROUND_NORM_DAMPING,0);
        myvel += dVelo + (float4)(normal.s0,0,normal.s2,0)*GROUND_NORM_DAMPING*2;
        myvel *= GROUND_VELO_DAMPING;
    }
	
    



    //////////////////////////////////////////////////////////
    // particle-particle interaction                        //
    //////////////////////////////////////////////////////////  
    
    float4 collide_velo = 0;
    int leqneighs = 0;
    if(mypos.s3>0.8){
     
	    for(int i=-1; i<=1; i++){
	        for(int j=-1; j<=1; j++){
	            for(int k=-1; k<=1; k++){
//	                  cell_id_t cid = g_get_cell_id(&grid, mypos.s012, (int3)(i,j,k));
//                        if (cid.cnt_id == -1) continue;
	                int cellid = dg_cell_id(&g, mypos, (int4)(i,j,k,0));
	                int num = g.counter[cellid];
	                num = num > g.gmax ? g.gmax : num;
	                

	                for (int m = 0; m < num; m++) {

	                    ///////////////////////////////////////////////////////////
	                    int other_gid = g.cells[cellid*(int)g.gmax+m];
	                    if (other_gid == mygid) continue;
	                    float4 other_pos = pos_inbuf[other_gid];
	                    float4 other_vel = vel_inbuf[other_gid];                    
	                    float4 n = (other_pos - mypos);
	                    float distance = length(n.s012);

	                    if(distance < RADIUS*2)
	                    {
                                

	                        //collide_velo+=normalize(n)*COLLISION_DAMPING;
	                        collide_velo+=collide(other_pos-mypos, myvel , other_vel, distance)*COLLIDE_DAMPING;
	                        if(mypos.s1 > other_pos.s1){
                                    float d = mypos.s1 - other_pos.s1;
                                    myvel.s1 = 0.0;
                                    collide_velo.s1 = 0.0;
	                        }
                            }
	                    ///////////////////////////////////////////////////////////

	                }
	            }
	        }
	    }

    }
    barrier(CLK_GLOBAL_MEM_FENCE);
    
	myvel+=collide_velo;

  //      if (leqneighs > 3) myvel.s1 = 0.0;
    mypos.s012 += myvel.s012*3;


	//float2 well = (float2)(0.5f,0.2f);
	float2 well = (float2)(0.71f,0.17f);
	float well_height = read_imagef(heightmap, sampler, well).s0;

	//float die_height = read_imagef(heightmap, sampler, (float2)(0.6,0.4)).s0;
	
	if(mypos.s0<0||mypos.s0>1||mypos.s2<0||mypos.s2>1) {mypos.s3=0;}
	//if(length(myvel)<0.001&&mypos.s1>well_height+0.01) {mypos.s3=0;}//{mypos.s3-=0.02;}
	if(length(myvel)<0.00001&&mypos.s1>0.03) {mypos.s3=0;}//{mypos.s3-=0.02;}
	//if(mypos.s1<=die_height+0.005) {mypos.s3=0;}//{mypos.s3-=0.02;}
	//mypos.s3-=0.00001;
	
	if(mypos.s3<=0) {
		mypos=(float4)(well.s0+random*0.001,well_height,well.s1+random*0.001,1.0f);
		//myvel=(float4)(-random*0.0001f,random*0.0001f,random*0.0001f,0);
		myvel = (float4)(-0.0006,0.0003,0.00001*random+0.0001f,0);
	}
	

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





//////////////////////////////////////////////////////////
// Add particles to grid Kernel                         //
//////////////////////////////////////////////////////////
kernel void gridadd_sim
(
    global float4* position, 
    global int* g_counter,
    global int* g_cells,
    int g_num_cells,
    int g_max_particles,
    global float* info) 
{
    grid_t grid = { g_num_cells,
                    g_max_particles,
                    g_counter,
                    g_cells};

    // dynamic grid
    dgrid_t g = {   (float4)(info[SPACE_X],info[SPACE_Y],info[SPACE_Z],0),
                    info[SPACE_LENGTH],
                    info[GRID_LENGTH],
                    info[GRID_MAXP],
                    g_counter,
                    g_cells};

    int mygid = get_global_id(0);
    float4 mypos = position[mygid];

    // add particle to counter and cell grid
    //g_add_particle(&grid, mypos.s012);   
    dg_add_particle(&g, mypos);   
}	