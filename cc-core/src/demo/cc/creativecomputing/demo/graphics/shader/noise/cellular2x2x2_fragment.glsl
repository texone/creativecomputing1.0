varying vec3 vTexCoord3D;

uniform float scale;

void main(void) {
	vec2 F = cellular2x2x2(vTexCoord3D.xyz * scale);
	float n = F.x;//smoothstep(0.4, 0.5, F.x);
	gl_FragColor = vec4(n, n, n, 1.0);
}
