#define LOCAL_MEM_SIZE 64

/**float xor128() {
  float x = 0.123456789f;
  float y = 0.362436069f;
  float z = 0.521288629f;
  float w = 0.88675123f;
  float t;
 
  t = x ^ (x << 11f);
  x = y; y = z; z = w;
  return w = w ^ (w >> 19) ^ (t ^ (t >> 8));
}*/
//constant sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE| CLK_FILTER_NEAREST| CLK_ADDRESS_REPEAT;

kernel void particle_sim(
global float4* position, 
global float4* velos,
global image2d_t heightmap
) 
{
	
	local float4 sharedMem[LOCAL_MEM_SIZE];
	
	float4 myPos = position[get_global_id(0)];
    float4 myVelo = velos[get_global_id(0)];
	sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE| CLK_FILTER_NEAREST| CLK_ADDRESS_REPEAT;
	
	float4 height = read_imagef(heightmap,sampler, myPos.s02);
	
    //float random = xor128();
    float4 myDVelo = (float4)(0,-0.00001+0.00981,0,0);
    
	
    myVelo += myDVelo;
    velos[get_global_id(0)] = myVelo;
	float lifetime = myPos.s3;
	if(myPos.s3 < height.s0) {
		lifetime = 0;
	}
	else {
		lifetime = myPos.s3 -0.8333f;
	}
	
    //position[get_global_id(0)] = (float4)(myPos.s0123);
    position[get_global_id(0)] = (float4)(myPos.s012 + myVelo.s012, lifetime );


}