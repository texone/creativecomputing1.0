uniform float amplitude;

const float PI = 3.141592654;

// draw a circle with sine shaped intensity to the red channel
// the circle is centered at texcoords [0.5, 0.5]
// the overall strength is defined by amplitude

void main(
	in float2 texCoord : TEXCOORD0,
	out float4 color : COLOR
){
	float center_distance =  length(2.0 * (texCoord.xy - 0.5));
	float h = amplitude * max(0.0, cos(0.5 * PI * center_distance));
	color = float4(h, 0.0, 0.0, 1.0);
}

