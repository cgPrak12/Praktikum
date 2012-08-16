#define LOCAL_MEM_SIZE 64

#define WITH_COLLISION 1

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




#define NORMAL_VELDAMP 0.00001
#define MASS 0.02
#define K_CONSTANT 1.5
#define MU_CONSTANT 3.5*1.003*10e-3
#define GRAVITY -0.000232
#define REST_DENS 998
#define SURFACE_TENS 1500
#define F_SURFACEDAMP 1
#define PRESSUREDAMP 20
#define VESCOSITYDAMP 500 //0.01
#define FRICTION 1
#define REFLECTDAMP 0.85



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
    global float4* start_pos,
    global float2* valuebuf) 
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
	float h = 2*radius;
	float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);
/*
    //////////////////////////////////////////////////////////
    // gravity and ground interaction                       //
    //////////////////////////////////////////////////////////
    float3 gravity = (float3)(0,-0.00001,0);
    myvel.s012 += gravity;
    float3 dVelo = (float3)(0);  
    
    if(mypos.s1 <= height.s0+RADIUS)
    {
        float4 normal = (float4)(getNormal(heightmap, mypos.s02),0);
        mypos.s1 = height.s0+RADIUS;
        dVelo = (float3)(normal.s012*GROUND_NORM_DAMPING);
        myvel.s012 += dVelo + (float3)(normal.s0,0,normal.s2)*GROUND_NORM_DAMPING*2;
        myvel.s012 *= GROUND_VELO_DAMPING;
    }
	*/
    



    //////////////////////////////////////////////////////////
    // particle-particle interaction                        //
    //////////////////////////////////////////////////////////  
    
    
    float4 f_pressure = (float4)(0);
    float4 f_viscos = (float4)(0);
    float4 f_surface = (float4)(0);
    float4 gcolor_field = (float4)(0);
    float4 lcolor_field = (float4)(0);
 	float my_pressure =  valuebuf[mygid].s1;
   	float my_massdens =  valuebuf[mygid].s0;
   	float4 dVelo = (float4)(0);
	//mypos.s3 = 1.0;
   

    if(mypos.s3>0.1){
	    for(int i=-1; i<=1; i++){
	        for(int j=-1; j<=1; j++){
	            for(int k=-1; k<=1; k++){

	                int cellid = dg_cell_id(&g, mypos, (int4)(i,j,k,0));
	                int num = g.counter[cellid];
	                num = num > g.gmax ? g.gmax : num;
	                
	                if (cellid == -1) continue;

	                for (int m = 0; m < num; m++) {

	                    ///////////////////////////////////////////////////////////
	                    int other_gid = g.cells[cellid*(int)g.gmax+m];
	                    if (other_gid == mygid) continue;
	                    float4 other_pos = pos_inbuf[other_gid];
	                    float4 other_vel = vel_inbuf[other_gid];                    
	                    float4 n = (other_pos - mypos);
	                    float distance = length(n.s012);
				
	                    if(distance <= h)
	                    {
	                    	
                        	float other_massdens = valuebuf[other_gid].s0;
                    		float other_pressure = valuebuf[other_gid].s1;
							
							
							f_pressure += (my_pressure + other_pressure) / (2*other_massdens)  
                        	  		   * MASS
                        	  		   * nW_pres((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),h);


                  			f_viscos += MU_CONSTANT * ((other_vel.s0123-myvel.s0123) / other_massdens)
                        	 	     * MASS
                        	 	     * lW_visc(mypos.s0123-other_pos.s0123,h);
                        	 	  
                        	gcolor_field = (MASS / other_massdens) * nW((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),h);
                        
                       		lcolor_field = (MASS / other_massdens) * lW((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),h);
	                      	
	                      	//mypos.s3 = 2.0;
	                    } 
	                    
                            
	                    ///////////////////////////////////////////////////////////

	                }
	            }
	        }
	    }
	}
    
    
    float threshold = 0.75 * radius;
    float gradient_length = length(gcolor_field);
    
    if(gradient_length >= threshold) 
    {
    	f_surface = -SURFACE_TENS * lcolor_field * gcolor_field / gradient_length;
   	} else
   	  {	
   	  	f_surface = (float4)(0);
   	  }
   	
    dt = 0.016;

    //// gravity 
    float4 gravity = (float4)(0,GRAVITY,0,0);
    float4 f_gravity = my_massdens * gravity;
    
    float4 f_sum = -f_pressure * PRESSUREDAMP; //+ f_viscos * VESCOSITYDAMP + f_surface * F_SURFACEDAMP;
	float4 f_acc = (f_sum / my_massdens) * dt + f_gravity;
	dVelo = f_acc *dt;

    myvel.s012 += dVelo.s012 * dt;
    myvel.s012 *= FRICTION;
 
	if(mypos.s1 <= height.s0 + radius)
   {
		
		float d = length(height - mypos);
		//mypos.s1 = height.s0 + radius;
		//mypos = (float4)(mypos.s0,height.s0,mypos.s2,1) + normalize(normal)*0.00001; 
		myvel.s012 = reflect(normal,myvel).s012*REFLECTDAMP;
		//float cr = 0.1;
		//myvel += (myvel-(1+cr *(d/(dt*length(myvel))))*(myvel*normalize(normal))*normalize(normal));
	} 
   
	mypos.s012 += myvel.s012 *dt;
	

	float4 s = g.s;
	float slen = g.slen;

	if (mypos.s0-radius < s.s0) {
		mypos.s0 = s.s0+radius;
		myvel.s0 = -myvel.s0*0.5; 
	} else if (mypos.s0+radius > s.s0+slen)  {
		mypos.s0 = s.s0+slen-radius;
		myvel.s0 = -myvel.s0*0.5;
	}
	
	if (mypos.s1-radius < s.s1) {
		mypos.s1 = s.s1+radius;
		myvel.s1 = -myvel.s1*0.5;
	} else if (mypos.s1+radius > s.s1+slen) {
		mypos.s1 = s.s1+slen-radius;
		myvel.s1 = -myvel.s1*0.5;
	}
	
	if (mypos.s2-radius < s.s2) {
		mypos.s2 = s.s2+radius;
		myvel.s2 = -myvel.s2*0.5;
	} else if (mypos.s2+radius > s.s2+slen) {
		mypos.s2 = s.s2+slen-radius;
		myvel.s2 = -myvel.s2*0.5;
	}

	mypos.s3+=0.00015;
	
	bool up = (bool)(round(myvel.s3/16));
	
	if(mypos.s3>=1||(mypos.s3>=0 && mypos.s3<0.1)){
		if(up){
			mypos = (float4)(0.45f +radius+((int)(myvel.s3)%8)*2*radius,
							 0.1, 0.12f, 0.1);
		} else {
			mypos = (float4)(0.45f +2*radius+((int)(myvel.s3)%8)*2*radius,
							 0.1+radius*2, 0.12f+radius*2, 0.1);
		}
		
		myvel.s012 = (float3)(0.0f, 0.0f, 0.04f);
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
    global float2* valuebuf,
    global float* info)
{	

	float radius = info[SPACE_PARTICLE_RADIUS];
	float h = 2*radius;
    int mygid = get_global_id(0);
    // dynamic grid
    dgrid_t grid = {   (float4)(info[SPACE_X],info[SPACE_Y],info[SPACE_Z],0),
                    info[SPACE_LENGTH],
                    info[GRID_LENGTH],
                    info[GRID_MAXP],
                    g_counter,
                    g_cells};

    float4 mypos = position[mygid];
    int sum_neighbours = -1;
    for(int i=-1; i<=1; i++){
        for(int j=-1; j<=1; j++){
            for(int k=-1; k<=1; k++){
            	int cellid = dg_cell_id(&grid, mypos, (int4)(i,j,k,0));
            	if (cellid == -1) continue;
	            int num = grid.counter[cellid];
	            num = num > grid.gmax ? grid.gmax : num;
	            
               	int max = (int)grid.gmax; 
                for (int m = 0; m < num; m++) {
                    int other_gid = grid.cells[cellid*max+m];
                    float4 other_pos = position[other_gid];
                    if(length(mypos.s012-other_pos.s012) <= h)
                    {
                    	sum_neighbours += MASS*W((float4)(mypos.s012,0)-(float4)(other_pos.s012,0),h);
					}

                }
            }
        }
    }
    float mass_density = sum_neighbours ;
    float pressure = K_CONSTANT *( mass_density - REST_DENS);
    
    valuebuf[mygid] = (float2)(mass_density, pressure);
}	