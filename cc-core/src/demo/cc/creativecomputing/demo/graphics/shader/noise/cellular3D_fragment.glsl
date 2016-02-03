
varying vec3 vTexCoord3D;

uniform float scale;

void main(void) {
	vec2 F = cellular(vTexCoord3D.xyz * scale);
	float n = F.y-F.x;
	gl_FragColor = vec4(n, n, n, 1.0);
}
