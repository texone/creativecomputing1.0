uniform vec4 diffuse;
uniform vec4 ambient;
uniform vec4 specular;
uniform float shininess;

uniform vec3 lightDir;

varying vec3 normal, halfVector;
varying float blur;

void main(){
	float NdotL = abs(dot(normalize(normal), lightDir));
	gl_FragData[0] = NdotL * diffuse + ambient;
	gl_FragData[0].xyz = normalize(normal);
	gl_FragData[0].w = 1.0;
}