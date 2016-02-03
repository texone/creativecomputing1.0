uniform float time;

float surfacefunction(vec3 hitpoint) {
    //iso surface cube: x^6+y^6+z^6
    //return dot(hitpoint * hitpoint * hitpoint, hitpoint * hitpoint * hitpoint) - 1.0;
    vec3 c = vec3(3);
    vec3 q = mod(hitpoint,c)-0.5*c;
    //return primitve( q );
    
    //return length(max(abs(q)-0.2,0.0))-0.6;
    return length(q) - 1.0;
}