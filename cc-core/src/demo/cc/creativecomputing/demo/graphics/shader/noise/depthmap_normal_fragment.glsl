uniform sampler2D depthMap;
uniform float depthScale;

uniform vec3 lightDir;
uniform vec3 ambient1;
uniform vec3 ambient2;
uniform vec3 diffuse1;
uniform vec3 diffuse2;

uniform float colorDepth1;
uniform float colorDepth2;

void main(void) {
	vec4 color0 = texture2D(depthMap, gl_TexCoord[0].xy);
	vec4 color1 = texture2D(depthMap, gl_TexCoord[0].xy + vec2(0.0,0.001));
	vec4 color2 = texture2D(depthMap, gl_TexCoord[0].xy + vec2(0.001,0.0));
	
	vec3 v1 = vec3(0.0, 1.0, (color1.x - color0.x) * depthScale * 100.0);
	vec3 v2 = vec3(1.0, 0.0, (color2.x - color0.x) * depthScale * 100.0);
	//vec3 v1 = vec3(0.0,1.0,0);
	//vec3 v2 = vec3(1.0,0.0,0);
	vec3 normal1 = normalize(cross(v2,v1));
	
	float NdotL = max(dot(normal1,lightDir),0.0);
	
	float blend = smoothstep(colorDepth1, colorDepth2, color0.x);
	
	vec3 color = mix(ambient1, ambient2, blend);
	
	if (NdotL > 0.0) {
		color += mix(diffuse1, diffuse2, blend) * NdotL;
		/*
		halfV = normalize(halfVector);
		NdotHV = max(dot(n,halfV),0.0);
		color += gl_FrontMaterial.specular *
				gl_LightSource[0].specular *
				pow(NdotHV, gl_FrontMaterial.shininess);*/
	}
	
	float shadow = (color0.x - 0.5) / (0.5);

	gl_FragColor = vec4(color.xyz * color0.y,1.0);
	
	//gl_FragColor = vec4(normal1.xyz,1.0);
	//gl_FragColor = vec4(color0.xxx,1.0);
}
