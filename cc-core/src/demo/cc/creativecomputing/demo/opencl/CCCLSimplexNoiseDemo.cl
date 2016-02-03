kernel void gamma(
	read_only image2d_t input, 
	write_only image2d_t output,
	const float gamma, 
	const float scale
) { 
	const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP | CLK_FILTER_NEAREST; 
	
	int x = get_global_id(0);
	int y = get_global_id(1);
	
	if(x >= get_image_width(input) || y >= get_image_height(input))return; 
	
	int2 coord = (int2)(x,y); 
	
	float4 temp = read_imagef(input, sampler, coord);
	float4 noise = (float4)(ugnoise2d((float2)(x * 0.01,y * 0.01))) * 255.0f;
	noise.w = 255.0f;
	write_imagef(output, coord, noise); 
} 