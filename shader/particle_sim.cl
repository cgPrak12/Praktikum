#define LOCAL_MEM_SIZE 64

#define DAMPING 0.25
#define SPRING 4
#define SHEAR 0.12

/**
 * Calculates the acceleration by particle-particle interaction of particle
 * A and B.
 * @param n         vector between A and B
 * @param vi        velocity of particle A
 * @param vj        velocity of particle B
 * @param radius    radius of A and B
 * @return acceleration of particle A
 */
float4 collide(
float4 n, 
float4 vi, 
float4 vj, 
float radius)
{
    // Vektoren 
    float4 norm = normalize(n);
    float distance = length(n.s012);
    float4 relVelo = vj - vi;
    float d = dot(norm, relVelo);
    float4 tanVelo = relVelo - d*norm;
    float s = -SPRING*(2*radius - distance);
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

/** grid structure */
typedef struct
{
    float4 s;                   // start position|corner
    float slen;                 // length of grid in space
    float glen;                 // discrete length|number of cells per dim
    float gmax;                 // max particles per grid cell
    global int *counter;        // the counter grid
    global int *cells;          // the particle grid
} dgrid_t;

/**
 * Returns the grid id of a given position
 * @param grid  the grid
 * @param pos   the position
 * @param dpos  grid-cell shift
 * @return int-vector (counter array id, index array id)
 */
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
    global float* info,
    global float4* start_pos) 
{
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
    float radius = info[SPACE_PARTICLE_RADIUS];



    float4 dVelo = (float4)(0);  
    
    if(mypos.s1 <= height.s0+radius)
    {
        float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);
        mypos.s1 = height.s0+radius;
        dVelo = (float4)(normal.s012*GROUND_NORM_DAMPING,0);
        myvel += dVelo + (float4)(normal.s0,0,normal.s2,0)*GROUND_NORM_DAMPING*2;
        myvel *= GROUND_VELO_DAMPING;
    }


    //////////////////////////////////////////////////////////
    // particle-particle interaction                        //
    //////////////////////////////////////////////////////////  
    
    float4 collide_velo = 0;
    int collided = 0;
   
    if(mypos.s3>0.8)
    {
        for(int i=-1; i<=1; i++){
            for(int j=-1; j<=1; j++){
                for(int k=-1; k<=1; k++){
                    // read cells cellid in grid
                    int cellid = dg_cell_id(&g, mypos, (int4)(i,j,k,0));
                    if (cellid == -1)
                    {
                        // invalid cellid
                        continue;
                    }
                    
                    // read number of particles in this cell and clamp to max
                    int num = g.counter[cellid];
                    num = num > g.gmax ? g.gmax : num;

                    // iterate this cell's particles
                    for (int m = 0; m < num; m++) {
                        // read other particle's global-id
                        int other_gid = g.cells[cellid*(int)g.gmax+m];
                        if (other_gid == mygid)
                        {
                            // other particle is equal to this
                            continue;
                        }
                        // read other particle's position and velocity
                        float4 other_pos = pos_inbuf[other_gid];
                        float4 other_vel = vel_inbuf[other_gid];                    
                        float4 n = (other_pos - mypos);
                        float distance = length(n.s012);

                        if(distance < radius*2)
                        {
                            // collide particles
                            collide_velo += collide(other_pos - mypos,
                                                    myvel,
                                                    other_vel,
                                                    radius)
                                                    *COLLIDE_DAMPING;
                            collided = 1;
                        }
                    }
                }
            }
        }
    }

    //////////////////////////////////////////////////////////
    // gravity and ground interaction                       //
    //////////////////////////////////////////////////////////
    float4 gravity = (float4)(0,-0.00001,0,0);
    if (collided == 0)
    {
        myvel += gravity;
    } else {
        myvel += gravity*0.1;
    }
    
    // add particle-particle-acceleration to velocity
    myvel += collide_velo;

    // multiply velocity and add to position: MOVE
    mypos.s012 += myvel.s012*3;


    //float2 well = (float2)(0.5f,0.2f);
    float2 well = (float2)(0.71f,0.17f);
    float well_height = read_imagef(heightmap, sampler, well).s0;

    if(mypos.s0<0||mypos.s0>1||mypos.s2<0||mypos.s2>1) {mypos.s3=0;}
    if(length(myvel)<0.00001&&mypos.s1>0.03) {mypos.s3=0;}//{mypos.s3-=0.02;}
	
    if(mypos.s3 <= 0)
    {
        // particle's lifetime is non-position => respawn
        mypos = start_pos[mygid];
        myvel = (float4)(0,0,0.0001,0);
    }
	
    // write new position and velocity
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
    dg_add_particle(&g, mypos);   
}	