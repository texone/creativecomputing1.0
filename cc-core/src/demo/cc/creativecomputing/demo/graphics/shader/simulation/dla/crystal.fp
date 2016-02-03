uniform samplerRECT crystalTexture;
uniform samplerRECT particleTexture;

void main(
	in 	float4 iTexCoord : TEXCOORD0,
	out float4 oColor 	 : COLOR0
){

	oColor = texRECT(crystalTexture,iTexCoord.xy);
	
	if(oColor.x > 0){
		oColor+=float4(0.0025);
	}else{
	float c = texRECT(crystalTexture,iTexCoord.xy + float2(1,0));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(-1,0));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(0,1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(0,-1));
	
	c += texRECT(crystalTexture,iTexCoord.xy + float2(1,1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(1,-1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(-1,-1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(-1,1));
	
	c*=100;
	oColor = saturate(float4(c,c,c,1)) * texRECT(particleTexture,iTexCoord.xy) * 0.1 + oColor;
	}
	/*
	float c = texRECT(crystalTexture,iTexCoord.xy + float2(1,0));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(-1,0));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(0,1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(0,-1));
	
	c += texRECT(crystalTexture,iTexCoord.xy + float2(1,1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(1,-1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(-1,-1));
	c += texRECT(crystalTexture,iTexCoord.xy + float2(-1,1));
	
	c*=100;
	color = saturate(float4(c,c,c,1)) * texRECT(particleTexture,iTexCoord.xy) * 0.1 + texRECT(crystalTexture,iTexCoord.xy);
	*/
}