#define LOCAL_MEM_SIZE 64
#define RADIUS 0.007
#define WITH_COLLISION 1
#define GRIDLEN 84

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



float3 getNormal(image2d_t, float2);
void add2GridCounter(global int*, global int4*, float3);
int getCellId(float3 pos, int3 dpos);



kernel void particle_sim(
global float4* position, 
global float4* velos,
image2d_t heightmap,
image2d_t normalmap,
global int* gridCounter,
global int4* gridCells) 
{	
    local float4 sharedMem[LOCAL_MEM_SIZE];
	
    float4 myPos = position[get_global_id(0)];
    float4 myVelo = velos[get_global_id(0)];


    add2GridCounter(gridCounter,gridCells,myPos.s012);

	
	myPos.s012 += myVelo.s012*4;
	
    float4 height = read_imagef(heightmap, sampler, myPos.s02);
    //float4 normal = read_imagef(normalmap, sampler, myPos.s02);
    float4 normal = (float4)(getNormal(heightmap, myPos.s02),0);

    // y-axis accelaration (gravity)  
    float4 gravity = (float4)(0,-0.00004,0,0);

    int gIds[4];
    for(int i=-1; i<=1; i++){
        for(int j=-1; j<=1; j++){
            for(int k=-1; k<=1; k++){
                //if (k == 0 && j == 0 && i == 0) continue;
                int id = getCellId(myPos.s012, (int3)(i,j,k));
                if (id > GRIDLEN*GRIDLEN*GRIDLEN || id < 0) continue;
                int num = gridCounter[id];
                num = num > 4 ? 4 : num;
                
                int4 gids = gridCells[id];
                switch (num) {
                    case 4: gIds[3] = gids.s3;
                    case 3: gIds[2] = gids.s2;
                    case 2: gIds[1] = gids.s1;
                    case 1: gIds[0] = gids.s0;
                }
                
                for (int m = 0; m < num; m++) {
                    ///////////////////////////////////////////////////////////
                    float4 oPos = position[gIds[m]];
                    float4 oVelo = velos[gIds[m]];                    
                    float4 n = (oPos - myPos);
                    float distance = length(n.s012);
                    if(distance < RADIUS*2)
                    {
                        if(myPos.s1 > oPos.s1){
                                myVelo-=gravity*0.1;
                        }
                        myVelo+=normalize(n)*-0.00008;
                    }
                    ///////////////////////////////////////////////////////////
                }
            }
        }
 myVelo *= 0.75;
    }
    
   
    barrier(CLK_GLOBAL_MEM_FENCE);


/*
    if (WITH_COLLISION) {	
    float4 collideVelo;
    for(int tile=0; tile < get_num_groups(0); ++tile)
    {
        int toCopy = tile * get_local_size(0) + get_local_id(0);
        sharedMem[2 * get_local_id(0) + 0] = position[toCopy];
        sharedMem[2 * get_local_id(0) + 1] = velos[toCopy];
        barrier(CLK_LOCAL_MEM_FENCE);
        
        for(int i=0; i < get_local_size(0); ++i)
        {
            float4 posToCheck  = sharedMem[2 * i + 0];
            float4 veloToCheck = sharedMem[2 * i + 1];
            float4 n = (posToCheck - myPos);
            float distance = length(n.s012);
            if(distance < RADIUS*2)
            {
                float4 vi = (float4)(myVelo.s012, 0.0);
                float4 vj = (float4)(veloToCheck.s012, 0.0);
            
            	//collideVelo = -n;
            	if(myPos.s1 > posToCheck.s1){
            		//myVelo += collideVelo*0.0005f + normalize(n)*0.00001 + myDVelo;//(float4)(0,0.00005,0,0);
            		//myPos.s1 = posToCheck.s1 + RADIUS;
            		//myVelo += reflect((float4)(n.s0,0,n.s1,0),n)*0.001;
            		//myVelo += normalize(n)*-0.000001;
            		myVelo-=gravity*0.1;
            	}
            	myVelo+=normalize(n)*-0.00001;
            	//myVelo+=1/(distance)*normalize(n);
            }
        }
        
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    myVelo *= 0.95;
    }
   */ 

	myVelo+=gravity;
        float3 dVelo = (float3)(0);  	
	if(myPos.s1 <= height.s0+RADIUS) {
            // ground contact, terrain<->particle
            myPos.s1 = height.s0+RADIUS;
            dVelo = normal.s012*0.0001;
            myVelo += (float4)(dVelo,1);
            myVelo *= 0.9;

            //DO NOT USE, TESTING ONLY
            //myPos.s1 = height.s0+RADIUS;
            //myVelo = movingDir*0.1 + (float4)(0,0.0001,0,0);
            //myVelo = movingDir*0.1 + normalize(normal)*0.0001;
            //myVelo = normalize(normal)*0.00001;
    }


	float lifetime = myPos.s3-0.8333f;
    //position[get_global_id(0)] = (float4)(myPos.s012 + myVelo.s012*4, lifetime);
    position[get_global_id(0)] = (float4)(myPos.s012, lifetime);
    velos[get_global_id(0)] = myVelo;
}

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

int getCellId(float3 pos, int3 dpos)
{
    int3 gp = (int3)((int)(pos.s0*GRIDLEN),
                     (int)(pos.s1*GRIDLEN),
                     (int)(pos.s2*GRIDLEN));
    return  (gp.s0+dpos.s0) + (gp.s1+dpos.s1)*GRIDLEN + (gp.s2+dpos.s2)*GRIDLEN*GRIDLEN;
}

void add2GridCounter(global int* gridCounter, global int4* gridCells, float3 pos)
{
//    int gx = (int)(pos.s0*GRIDLEN);
//    int gy = (int)(pos.s1*GRIDLEN);
//    int gz = (int)(pos.s2*GRIDLEN);
    int id = getCellId(pos, (int3)(0));
    //int id = gp.s0 + gp.s1*GRIDLEN + gp.s2*GRIDLEN*GRIDLEN;
    if (id > GRIDLEN*GRIDLEN*GRIDLEN || id <0) return;
    int mid = atomic_add(gridCounter+id,1);
    int gid = get_global_id(0);
    switch (mid) {
        case 0: gridCells[id].s0 = gid;
            break;
        case 1: gridCells[id].s1 = gid;
            break;
        case 2: gridCells[id].s2 = gid;
            break;
        case 3: gridCells[id].s3 = gid;
            break;
        default: break;
    }
    
}

kernel void gridclear_sim(
global int* gridCounter) 
{
    gridCounter[get_global_id(0)] = 0;
}	