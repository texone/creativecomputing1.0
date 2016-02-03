uniform float time;

float surfacefunction(vec3 hitpoint) {
    vec3 z=hitpoint;
    vec3 c=vec3(sin(time/15.0)*0.5,sin(time),sin(time/15.0)*0.1);
    float r=0.0;
    
    for (float count = 0.0; count < 5.0 - 1.0; count += 1.0) {
        r=length(z);
        if (r > 2.0) {
            break;
        }
        //3d mandelbrot formula from "bugman"
        //http://www.bugman123.com/Hypercomplex/index.html
        //{x^2 - y^2 - z^2, 2 x y, 2 x z - 2 y z}
        vec3 z2 = vec3(
            z.x * z.x - z.y * z.y - z.z * z.z,
            2.0 * z.x * z.y,
            2.0 * (z.x - z.y) * z.z
        );
        z = z2 + c;
    }
    return r - 2.0;
}