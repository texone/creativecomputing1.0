kernel void simpleAdd(
	global float * theBufferA,
	global float * theBufferB,
	global float * theResult
){
	unsigned int id = get_global_id(0);
	theResult[id] = sqrt(theBufferA[id] + theBufferB[id]);
}