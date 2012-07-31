#define LOCAL_MEM_SIZE 64

kernel void particle_sim(
global float4* position, 
global float4* velos) 
{
	local float4 sharedMem[LOCAL_MEM_SIZE];
	
	float4 myPos = position[get_global_id(0)];
    float4 myVelo = velos[get_global_id(0)];
    float4 myDVelo = (float4)(0,-0.0001,0,0);
    
    myVelo += myDVelo;
    velos[get_global_id(0)] = myVelo;
    //position[get_global_id(0)] = (float4)(myPos.s0123);
    position[get_global_id(0)] = (float4)(myPos.s012 + myVelo.s012, myPos.s3);


}