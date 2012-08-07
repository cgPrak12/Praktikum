#define MASS 0.02
#define K_CONSTANT 3.0
#define H_CONSTANT 0.0457


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
    float mass_density = sum_neighbours;
    //float pressure = K_CONSTANT *( mass_density - REST_DENS);
    float pressure = K_CONSTANT *mass_density ;

    valuebuf[mygid] = (float2)(mass_density, pressure);
}