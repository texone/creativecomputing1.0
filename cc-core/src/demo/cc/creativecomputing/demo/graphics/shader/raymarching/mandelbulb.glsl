uniform float time;

float surfacefunction(vec3 hitpoint) {
    vec3 z = hitpoint;
    vec3 c = vec3(sin(time/15.0)*0.66,sin(time/15.0)*0.1,sin(time/15.0));
    float r = 0.0;
    
    for (float count=0.0; count<5.0-1.0; count+=1.0) {
        vec3 z2 = z*z;
        r = sqrt(dot(z,z));
        if (r>2.0) {
            break;
        }
        float planeXY = sqrt(z2.x+z2.y)+0.0000001;
        r += 0.0000001;
        float sinPhi = z.y/planeXY;
        float cosPhi = z.x/planeXY;
        float sinThe = planeXY/r;
        float cosThe = z.z/r;
        //level 1
        sinPhi = 2.0 * sinPhi * cosPhi;
        cosPhi = 2.0 * cosPhi * cosPhi - 1.0;
        sinThe = 2.0 * sinThe * cosThe;
        cosThe = 2.0 * cosThe * cosThe - 1.0;
        //level 2.
        sinPhi = 2.0 * sinPhi * cosPhi;
        cosPhi = 2.0 * cosPhi * cosPhi - 1.0;
        sinThe = 2.0 * sinThe * cosThe;
        cosThe = 2.0 * cosThe * cosThe - 1.0;
        //level 3.
        sinPhi = 2.0 * sinPhi * cosPhi;
        cosPhi = 2.0 * cosPhi * cosPhi - 1.0;
        sinThe = 2.0 * sinThe * cosThe;
        cosThe = 2.0 * cosThe * cosThe - 1.0;
        
        float rPow = pow(r, 8.0);
        z.x = sinThe * cosPhi;
        z.y = sinThe * sinPhi;
        z.z = cosThe;
        z *= rPow;
        z += c;
    }
    return r - 2.0;
}

