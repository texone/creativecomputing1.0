
uniform vec3 randAdd;

float rand(vec2 n){
  return fract(sin(dot(n.xy, randAdd.xy))* randAdd.z);
}


void main(void){
  float x = rand(gl_FragCoord.xy);
  gl_FragColor = vec4(x, x, x, 1.0);
}


