float2 windowDimension;
float2 position;

float4 color;

float radius;

samplerRECT baseTexture;

void main(
	in float2 iTexCoord : TEXCOORD0,
	out float4 oColor : COLOR0
){
	float2 pos = position - iTexCoord/windowDimension;
	radius /= windowDimension.x;
	float gaussian = exp(-dot(pos,pos) / radius);
	
	oColor = texRECT(baseTexture, iTexCoord) + color *  gaussian;
}