
#define RADIUS 0.005
#define WITH_COLLISION 1


constant sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE| CLK_FILTER_LINEAR| CLK_ADDRESS_REPEAT;




// GLSL reflect method (may contain errors)
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
 * 
 */
float W(float4 r, float h)
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

float4 nW(float4 r, float h)
{
    float norm_r = length(r);
    float w = (pow(h,2) - pow(norm_r,2));

    return (-r * (945.0/(32*PI*pow(h,9)))) * pow(w,2);
}

float lW(float4 r, float h)
{
    float norm_r = length(r);
    float w = (pow(h,2) - pow(norm_r,2));

    return (945.0/(8*PI*pow(h,9))) *w * (norm_r*norm_r - (3.0/4.0)*w);
}

//// pressure W-method ////


float4 nW_pres(float4 r, float h)
{
	
    float norm_r = length(r);
	

    return -r * (45.0/(PI*pow(h,6) * norm_r)) *(h-norm_r)*(h-norm_r) ;

}


//// viscosity W-method ////
float lW_visc(float4 r, float h)
{
    float norm_r = length(r);
    if(norm_r == h) return 0;
    return (45.0/(PI*pow(h,5)))*(1-(norm_r/h));
}



#define VELDAMP 0.6
#define NORMAL_VELDAMP 0.0000001
#define MASS 0.02
#define K_CONSTANT 1.5
#define H_CONSTANT 2*RADIUS
#define MU_CONSTANT 3.5*1.003*10e-3
#define GRAVITY -0.000005
#define REST_DENS 998.29
#define SURFACE_TENS 0.728
#define F_SURFACEDAMP 1
#define PRESSUREDAMP 0.0085
#define VESCOSITYDAMP 1

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
    float4 height = read_imagef(heightmap, sampler, mypos.s02);
    float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);


    //////////////////////////////////////////////////////////
    // Computing  forces                          	        //
    //////////////////////////////////////////////////////////

    float my_pressure = valuebuf[mygid].s1;
    float my_massdens = valuebuf[mygid].s0;
    

    float4 f_pressure = (float4)(0);
    float4 f_viscos = (float4)(0);
    float4 f_surface = (float4)(0);
    float4 gcolor_field = (float4)(0);
    float4 lcolor_field = (float4)(0);
    

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
                    
					if(length((float4)(mypos.s012,0)-(float4)(other_pos.s012,0)) <= H_CONSTANT)
					{
                    	
                    	float other_massdens = valuebuf[other_gid].s0;
                    	float other_pressure = valuebuf[other_gid].s1;


                   		f_pressure += (my_pressure + other_pressure) / (2*other_massdens)  
                        	  		* MASS
                        	  		* nW_pres((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),H_CONSTANT);


                  		f_viscos += MU_CONSTANT * ((other_vel.s0123-myvel.s0123) / other_massdens)
                        		  * MASS
                        	 	  * lW_visc(mypos.s0123-other_pos.s0123,H_CONSTANT);
                        	 	  
                        gcolor_field = (MASS / other_massdens) * nW((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),H_CONSTANT);
                        
                        lcolor_field = (MASS / other_massdens) * lW((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),H_CONSTANT);
                    }
                    
               	   
                }
            }
        }
    }
    
    float threshold = 0.1;
    float gradient_length = length(gcolor_field);
    
    if(gradient_length >= threshold) 
    {
    	f_surface = -SURFACE_TENS * lcolor_field * gcolor_field / gradient_length;
   	} else
   	  {	
   	  	f_surface = (float4)(0);
   	  }
   	


    //// gravity 
    float4 gravity = (float4)(0,GRAVITY,0,0);
    float4 f_gravity = my_massdens * gravity;
    
    float4 f_sum = -f_pressure * PRESSUREDAMP + f_viscos * VESCOSITYDAMP + f_surface * F_SURFACEDAMP;
	float4 f_acc = f_sum / my_massdens * dt + f_gravity;
    
	myvel = myvel + f_acc*dt;
    //myvel *= VELDAMP;
    
    
    
    
   if(mypos.s1 <= height.s0 + RADIUS +0.001)
   {
   
   		mypos.s1 = height.s0 + RADIUS +0.001;
    	float d = length(height - mypos);
    	//d = d < 0 ? -d : d;
    	//myvel.y = -0.5 * myvel.y;
    	myvel += d*normal*0.00002;
    	
    	
		float cr = 0.0000000001;
		//f_acc = f_acc -(1+cr *(d/(dt*length(myvel))))*(myvel*normalize(normal))*normalize(normal);
    	
		myvel += (myvel-(1+cr *(d/(dt*length(myvel))))*(myvel*normalize(normal))*normalize(normal))*0.00004;
 		//myvel.s0 = normal.s0 *
 		myvel *= 0.795;
 		mypos += myvel*0.01;
   } else {
   		//myvel.y += f_gravity.y;
   		mypos += myvel*0.01;
   	
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
                    if(length(mypos.s012-other_pos.s012) <= H_CONSTANT)
                    {
                    	sum_neighbours += MASS*W((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),H_CONSTANT);
					}

                }
            }
        }
    }
    float mass_density = sum_neighbours ;
    float pressure = K_CONSTANT *( mass_density - REST_DENS);
    
    valuebuf[mygid] = (float2)(mass_density, pressure);
}