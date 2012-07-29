#define SPHERE_RADIUS 0.1
#define DAMPING 0.025
#define SPRING 4
#define SHEAR 0.12
#define GRAVITY 0.01
#define TIME_SCALE 1
#define LOCAL_MEM_SIZE 64

float4 collide(
float ri,
float rj,
float4 n, 
float4 vi, 
float4 vj, 
float distance) 
{
    float4 norm = normalize(n);
    float4 relVelo = vj - vi;
    float d = dot(norm, relVelo);
    float4 tanVelo = relVelo - d*norm;
    float s = -SPRING*(ri + rj - distance);
    return SHEAR*tanVelo + DAMPING*relVelo + s*norm;
}

float4 orbitCorrection(float4 myPos, float4 myVelo)
{
    float4 r = normalize(-myPos);
    return myVelo - GRAVITY * r * dot(r, myVelo);
} 

__kernel void asteroid_sim(
__global float4* old_points, 
__global float4* new_points,
__global float4* old_velos, 
__global float4* new_velos,
uint count,
float dt)
{   
    //lokales shared memory, hier nicht als Argument wie in der Vorlesung übergeben
    //verhaelt sich identisch
    __local float4 sharedMem[LOCAL_MEM_SIZE];
    
    float4 myPos = old_points[get_global_id(0)];
    float4 myVelo = old_velos[get_global_id(0)];
    float4 myDVelo = 0;
    
    for(int tile=0; tile < get_num_groups(0); ++tile)
    {
        int toCopy = tile * get_local_size(0) + get_local_id(0);
        sharedMem[2 * get_local_id(0) + 0] = old_points[toCopy];
        sharedMem[2 * get_local_id(0) + 1] = old_velos[toCopy];
        barrier(CLK_LOCAL_MEM_FENCE);
        
        for(int i=0; i < get_local_size(0); ++i)
        {
            float4 posToCheck  = sharedMem[2 * i + 0];
            float4 veloToCheck = sharedMem[2 * i + 1];
            float4 n = (float4)(posToCheck.s012 - myPos.s012, 0.0);
            float distance = length(n.s012);
            if(distance < myPos.s3 + posToCheck.s3)
            {
                float4 vi = (float4)(myVelo.s012, 0.0);
                float4 vj = (float4)(veloToCheck.s012, 0.0);
            
                myDVelo += collide(myPos.s3, posToCheck.s3, n, vi, vj, distance);
            }
        }
        
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    
    myVelo += myDVelo;
    myVelo = orbitCorrection((float4)(myPos.s012, 1.0), (float4)(myVelo.s012, 1.0));
    new_velos[get_global_id(0)] = myVelo;
    new_points[get_global_id(0)] = (float4)(myPos.s012 + dt * myVelo.s012, myPos.s3);
}