#define LOCAL_MEM_SIZE 64

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
constant float minEpsilon = 0.05f;

float4 getMinFromEnvironment(image2d_t heightmap, float4 pos) {
    float minHeight = INFINITY;
    int myself = 0;
    float4 minP;
    
    // get minimum
    for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
            float4 hp =
                read_imagef(heightmap,sampler,
                    (float2)(pos.s0 + x*minEpsilon,pos.s2 + z*minEpsilon));
            if (hp.s0 < minHeight) {
                minHeight = hp.s0;
                minP = (float4)(pos.s0 + x*minEpsilon,hp.s0,pos.s2 + z*minEpsilon,1);
                if (x == 0 && z == 0) {
                    myself = 1;
                    minP.s3 = 0;
                } else if (myself == 1) {
                    myself = 0;
                }
            }
        }
    }

    return minP;
}




kernel void particle_sim(
global float4* position, 
global float4* velos,
image2d_t heightmap,
image2d_t normalmap) 
{
/*
    sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE| CLK_FILTER_NEAREST| CLK_ADDRESS_REPEAT;
*/
	
    local float4 sharedMem[LOCAL_MEM_SIZE];
	
    float4 myPos = position[get_global_id(0)];
    float4 myVelo = velos[get_global_id(0)];
	
    float4 height = read_imagef(heightmap, sampler, myPos.s02);
    float4 normal = read_imagef(normalmap, sampler, myPos.s02);
	
    float random = xor128()*0.00000000001;

    
    float lifetime = myPos.s3;
    if(myPos.s1 < height.s0+0.001) {
        myPos.s1 = height.s0+0.001;
        float4 minP = getMinFromEnvironment(heightmap, myPos);
        int myself = (int)(minP.s3);
        if (myself != 1) {
            myVelo = (float4)(0);
        } else {
            myVelo = minP - myPos;
            myVelo = normalize(myVelo);
        }
    }
    else {
        lifetime = myPos.s3 -0.8333f;
        // y-axis accelaration (falling rain)
        float4 myDVelo = (float4)(0,-0.00001,0,0);   
        myVelo += myDVelo;
    }

    


    position[get_global_id(0)] = (float4)(myPos.s012 + myVelo.s012, lifetime );
    velos[get_global_id(0)] = myVelo;
}