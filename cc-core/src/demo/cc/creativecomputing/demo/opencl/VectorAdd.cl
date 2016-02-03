// OpenCL Kernel Function for element by element vector addition
/*
__kernel void hello_kernel(
	__global float* result,
	__global const float* a, 
	__global const float* b 
) {

	int gid = get_global_id(0);

	// add the vector elements
	result[gid] = 1;//a[iGID] + b[iGID];
}
*/

__kernel void hello_kernel(
	__global float *result,
	__global const float *a, 
	__global const float *b
){
	int gid = get_local_id(0) * get_global_size(0) + get_global_id(0);

	result[get_global_id(0)] += a[gid];
}