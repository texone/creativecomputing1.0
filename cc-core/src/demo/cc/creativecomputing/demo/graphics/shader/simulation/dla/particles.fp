uniform samplerRECT randomTexture;
uniform samplerRECT positionTexture;
uniform samplerRECT crystalTexture;

uniform float2 texOffset;
uniform float2 boundary;

uniform float speed;
uniform float replacement;

void main(
	in 	float2 iTexCoord : TEXCOORD0,
	out float4 oPosition : COLOR0
){
	oPosition = texRECT(positionTexture, iTexCoord);
	float4 crystalColor = texRECT(crystalTexture,oPosition.xy);
	
	if(crystalColor.x > 0){
		speed*= 3;
	}
	
	float4 random = (texRECT(randomTexture, iTexCoord + texOffset) - 0.5) * speed;
	
	oPosition+= float4(random.x, random.y,0,0);
	
	if(oPosition.x > boundary.x)oPosition.x -= boundary.x;
	if(oPosition.x < 0)oPosition.x += boundary.x;
	if(oPosition.y > boundary.y)oPosition.y -= boundary.y;
	if(oPosition.y < 0)oPosition.y += boundary.y;
	
	
	
	
	

	
}