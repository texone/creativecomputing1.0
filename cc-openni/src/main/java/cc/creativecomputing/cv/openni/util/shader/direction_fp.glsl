#version 120

uniform float z;

uniform vec3 indexSpace;

uniform sampler3D cubeSampler;

void main(){
	vec3 texIndex = vec3(gl_TexCoord[0].xy,z);
	
	float alpha = 1.0;
	/*
	if(texCoord.x > 1.0 || texCoord.x < 0.0 || texCoord.y > 1.0 || texCoord.y < 0.0){
		alpha = 0.0;
	}*/
	
	float texX = gl_TexCoord[0].x;
	float texY = gl_TexCoord[0].y;
	float texZ = z;
	
	vec3 direction = vec3(
		abs(texture3D(cubeSampler,vec3(texX + indexSpace.x, texY, z)).x) - abs(texture3D(cubeSampler, vec3(texX - indexSpace.x, texY, z)).x),
		abs(texture3D(cubeSampler,vec3(texX, texY + indexSpace.y, z)).x) - abs(texture3D(cubeSampler, vec3(texX, texY - indexSpace.y, z)).x),
		abs(texture3D(cubeSampler,vec3(texX, texY, z + indexSpace.z)).x) - abs(texture3D(cubeSampler, vec3(texX, texY, z - indexSpace.z)).x)
	) * alpha;
	//direction = normalize(direction);
	//direction = direction * (1 - smoothing) + prevDirection * smoothing;
	
	gl_FragColor = vec4(direction,1.0);
	//gl_FragColor = vec4(direction,density);
	
	//gl_FragColor = vec4(texCoord.xy,0.0,alpha);
	
}