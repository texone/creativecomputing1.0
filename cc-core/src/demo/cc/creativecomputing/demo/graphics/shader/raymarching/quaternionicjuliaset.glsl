uniform float time;

float surfacefunction(vec3 hitpoint) {
    vec4 z  = vec4(hitpoint,0.0);
    vec4 z2 = vec4(1.0,0.0,0.0,0.0);
    vec4 c = vec4(0.7*cos(0.5*time),0.7*sin(0.3*time),0.7*cos(1.0*time),0.0);
    float n = 0.0;
    float squared_z = 0.0;
    while (n<6.0) {
        z2 = vec4(z[0]*z2[0]-z[1]*z2[1]-z[2]*z2[2]-z[3]*z2[3],
                  z[0]*z2[1]+z[1]*z2[0]+z[2]*z2[3]-z[3]*z2[2],
                  z[0]*z2[2]-z[1]*z2[3]+z[2]*z2[0]+z[3]*z2[1],
                  z[0]*z2[3]+z[1]*z2[2]-z[2]*z2[1]+z[3]*z2[0])*2.0;
        z = vec4(z[0]*z[0]-z[1]*z[1]-z[2]*z[2]-z[3]*z[3],
                 2.0*z[0]*z[1],
                 2.0*z[0]*z[2],
                 2.0*z[0]*z[3])+c;
        squared_z = dot(z, z);
        if (squared_z>=4.0) {
            break;
        }
        n++;
    }
    return squared_z-4.0;
} 

