
varying vec3 vTexCoord3D;

uniform float scale;

void main(void) {
	vec2 F = cellular2x2(vTexCoord3D.xy * scale);	
	float n = 1.0-1.5*F.x;
	gl_FragColor = vec4(n, n, n, 1.0);
}
