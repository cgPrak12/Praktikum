#define LOCAL_MEM_SIZE 64
#define RADIUS 0.005
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

#define PI 3.14159265
/**
 * http://image.diku.dk/projects/media/kelager.06.pdf p:21
 */
float W(float3 r, float h)
{
    float norm_r = length(r);
    if (norm_r > h)
        return 0.0;
    else
    {
        float w = (h*h - norm_r*norm_r);
        return (315.0/(64*PI*pow(h,9))) * pow(w,3);
    }
}

float3 nW(float3 r, float h)
{
    float norm_r = length(r);
    float w = (pow(h,2) - pow(norm_r,2));

    return -945.0/(32*PI*pow(h,9))*r*pow(w,2);
}

float lW(float3 r, float h)
{
    float norm_r = length(r);
    float w = (pow(h,2) - pow(norm_r,2));

    return -945.0/(32*PI*pow(h,9))*w*(3*pow(h,2) - 7*pow(norm_r,3));
}

//// pressure W-methods ////
float W_pres(float3 r, float h)
{
    float norm_r = length(r);
    if (norm_r > h)
        return 0.0;
    else
    {
        float w = (h - norm_r);
        return (15.0/(PI*pow(h,6))) * pow(w,3);
    }
}

float3 nW_pres(float3 r, float h)
{
    float norm_r = length(r);

    if (norm_r < 0.01)
        return (45.0/(PI*pow(h,6)));

    return -45.0/(PI*pow(h,6)) * (r/norm_r) * (h-norm_r)*(h-norm_r);
}

float lW_pres(float3 r, float h)
{
    float norm_r = length(r);
    float w = (pow(h,2) - pow(norm_r,2));

    return -90.0/(PI*pow(h,6)) * (1.0/norm_r)
        * (h-norm_r)*(h-2*norm_r);
}

float lW_visc(float3 r, float h)
{
    float norm_r = length(r);
    return 45.0/(PI*pow(h,6))*(h-norm_r);
}





#define GROUND_NORM_DAMPING 0.0001
#define GROUND_VELO_DAMPING 0.9995
#define COLLISION_DAMPING -0.000009
#define MASS 1
#define MASS_FACTOR 1
#define K_CONSTANT 3.0
#define H_CONSTANT 2.0*RADIUS
#define MU_CONSTANT 3.5*1.003*10e-3
#define GRAVITY -9.81
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
    global float2* valuebuf) 
{
    grid_t grid = { g_num_cells,
                    g_max_particles,
                    g_counter,
                    g_cells};


    int mygid = get_global_id(0);
    float4 mypos = position[mygid];
    float4 myvel = velos[mygid];
//    float4 height = read_imagef(heightmap, sampler, mypos.s02);
//    float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);


    //////////////////////////////////////////////////////////
    // Computing internal forces                            //
    //////////////////////////////////////////////////////////

    float my_pressure = valuebuf[mygid].s1;
    float my_massdens = valuebuf[mygid].s0;

    float3 f_pressure = (float3)(0);
    float3 f_viscos = (float3)(0);

    for(int i=-1; i<=1; i++){
        for(int j=-1; j<=1; j++){
            for(int k=-1; k<=1; k++){
                cell_id_t cid = g_get_cell_id(&grid, mypos.s012, (int3)(i,j,k));
                if (cid.cnt_id == -1) continue;
                int num = grid.counter[cid.cnt_id];
                for (int m = 0; m < num; m++) {
                    int other_gid = grid.cells[cid.cell_id+m];
                    if (other_gid == mygid) continue;

                    float4 other_pos = position[other_gid];
                    float4 other_vel = velos[other_gid];
                    float other_massdens = valuebuf[other_gid].s0;
                    float other_pressure = valuebuf[other_gid].s1;


                    f_pressure += ((other_pressure + my_pressure)/2.0)
                        * (MASS/other_massdens)
                        * nW_pres(mypos.s012-other_pos.s012,H_CONSTANT);

                    f_viscos += (other_vel.s012-myvel.s012)
                        * (MASS/other_massdens)
                        * lW_visc(mypos.s012-other_pos.s012,H_CONSTANT);
                }
            }
        }
    }
    f_pressure *= (-1);
    f_viscos *= MU_CONSTANT;

    float3 f_internal = f_pressure + f_viscos;

    //// gravity 
    float3 gravity = (float3)(0,GRAVITY,0);
    float3 f_gravity = my_massdens * gravity;

    //mypos.s012 += myvel.s012*4;

    float3 f_sum = f_internal + f_gravity;

    float3 f_acc = f_sum / my_massdens;

   myvel *= 0.00000001;
    mypos = mypos + myvel*dt;
    myvel = myvel + f_acc*dt;

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
    int g_max_particles)
{
    grid_t grid = { g_num_cells,
                    g_max_particles,
                    g_counter,
                    g_cells};

    g_add_particle(&grid, position[get_global_id(0)].s012);
}


//////////////////////////////////////////////////////////
// Calculate particle mass density                      //
//////////////////////////////////////////////////////////
kernel void massdensity_sim
(
    global float4* position,
    global int* g_counter,
    global int* g_cells,
    int g_num_cells,
    int g_max_particles,
    global float2* valuebuf)
{
    int mygid = get_global_id(0);
    grid_t grid = { g_num_cells,
                    g_max_particles,
                    g_counter,
                    g_cells};

    float4 mypos = position[mygid];
    int sum_neighbours = -1;
    for(int i=-1; i<=1; i++){
        for(int j=-1; j<=1; j++){
            for(int k=-1; k<=1; k++){
                cell_id_t cid = g_get_cell_id(&grid, mypos.s012, (int3)(i,j,k));
                if (cid.cnt_id == -1) continue;
                int num = grid.counter[cid.cnt_id];
                for (int m = 0; m < num; m++) {
                    int other_gid = grid.cells[cid.cell_id+m];
                    float4 other_pos = position[other_gid];
                    sum_neighbours += MASS*W(mypos.s012-other_pos.s012,H_CONSTANT);


                }
            }
        }
    }
    float mass_density = MASS_FACTOR * sum_neighbours;
    float pressure = K_CONSTANT *( mass_density - 998.29);

    valuebuf[mygid] = (float2)(mass_density, pressure);
}