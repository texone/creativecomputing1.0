#version 110

varying vec3  n;
varying vec3  i;

uniform float fresnelPower;
uniform float eta;
uniform float f;

uniform vec3 lightDir;
uniform float lightAmount;
uniform float alpha;

uniform float fresnelPow;
uniform float specularPow;
uniform float specularBrightPow;

void main(){
    vec3 ppNormal			= normalize(n);
	float ppDiffuse			= abs( dot( ppNormal, lightDir ) );
	float ppFresnel			= pow( ( 1.0 - ppDiffuse ), fresnelPow );
	float ppSpecular		= pow( ppDiffuse, specularPow );
	float ppSpecularBright	= pow( ppDiffuse, specularBrightPow );
	
	

	//vec3 landFinal			= color * landValue + ppSpecularBright * landValue;
	vec3 oceanFinal			= vec3(1,1,1) * ppSpecular + ppSpecularBright;// + ppFresnel * 15.0;
	
	gl_FragColor.rgb		= oceanFinal;
	gl_FragColor.a			= alpha;
}