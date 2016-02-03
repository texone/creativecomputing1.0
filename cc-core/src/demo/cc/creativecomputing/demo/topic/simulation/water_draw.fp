uniform samplerRECT heightMap;
uniform samplerRECT normalMap;
uniform samplerRECT backgroundTexture;

uniform float refraction;

// black on white
const float contrast = 1.0;

void main(
	in float2 texCoord : TEXCOORD0,
	out float4 color : COLOR
) {

	// take absolut value of current height and scale it a bit
    float intensity = contrast * abs( texRECT(heightMap, texCoord.xy).y);

	float shape =  texRECT(heightMap, texCoord.xy).x;
	
	float4 normal = normalize(texRECT(normalMap, texCoord) * 2.0 - 1.0);

    // invert to white on black
    intensity = 1.0 - intensity;   

	color = float4(intensity, intensity, intensity, 1);
    
    // displace texture coordinates
	float2 newUV = texCoord + normal.xy * refraction;// * (1-intensity);

	//newUV = float2(xOff, yOff);
	color = texRECT( backgroundTexture, newUV );
	//color = texRECT( normalMap, texCoord );
	//color = float4(intensity, intensity, intensity, 1);
 }

