

uniform sampler2D filterTexture;

void main(
	in float4 iColor : COLOR,
	in float4 iTextureCoords : TEXCOORD0,
	out float4 oColor : COLOR
){
	oColor = iColor * tex2Dproj( filterTexture, iTextureCoords);
	//oColor = float4(1,1,1,1);
}