#define LOCAL_MEM_SIZE 64

int xor128() {
  int x = 0.123456789;
  int y = 0.362436069;
  int z = 0.521288629;
  int w = 0.88675123;
  int t;
 
  t = x ^ (x << 11);
  x = y; y = z; z = w;
  return w = w ^ (w >> 19) ^ (t ^ (t >> 8));
}

kernel void particle_sim(
global float4* position, 
global float4* velos) 
{
	local float4 sharedMem[LOCAL_MEM_SIZE];
	
	float4 myPos = position[get_global_id(0)];
    float4 myVelo = velos[get_global_id(0)];
    float random = xor128();
    float4 myDVelo = (float4)(0+random,-0.00001+random,0+random,0);
    
    myVelo += myDVelo;
    velos[get_global_id(0)] = myVelo;
    //position[get_global_id(0)] = (float4)(myPos.s0123);
    position[get_global_id(0)] = (float4)(myPos.s012 + myVelo.s012, myPos.s3 -0.8333f);


}