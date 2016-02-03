/**
 **   __ __|_  ___________________________________________________________________________  ___|__ __
 **  //    /\                                           _                                  /\    \\  
 ** //____/  \__     __ _____ _____ _____ _____ _____  | |     __ _____ _____ __        __/  \____\\ 
 **  \    \  / /  __|  |     |   __|  _  |     |  _  | | |  __|  |     |   __|  |      /\ \  /    /  
 **   \____\/_/  |  |  |  |  |  |  |     | | | |   __| | | |  |  |  |  |  |  |  |__   "  \_\/____/   
 **  /\    \     |_____|_____|_____|__|__|_|_|_|__|    | | |_____|_____|_____|_____|  _  /    /\     
 ** /  \____\                       http://jogamp.org  |_|                              /____/  \    
 ** \  /   "' _________________________________________________________________________ `"   \  /    
 **  \/____.                                                                             .____\/     
 **
 ** Advanced fragment shader implementing a GPGPU raymarcher to render a 3D isosurface.
 ** 
 	The raymarching process is bound by "marchingmaxraylength". 
 	Raymarching accuracy can be configured with "marchingstepsize" and "marchingaccuracy". 
 	Surface normal approximation can be controlled with "epsilon". 
 	Uses phong based lighting with specular (implicit white). Currently 5 
 ** lightsources are configured in a cube-layout around the worlds origin. Lightsource colors a obtained
 ** from a 1D LUT configured as sampler uniform. The Matrix setup and calculation is done by hand as 
 ** using the buildin OpenGL matrices would be quite inconvenient handling wise. Metaball movement and
 ** initial positions/size/hardness are hardcoded in the surface function.
 **
 ** On Windows consider to disable/reconfigure this "feature" when using a slow GPU: 
 ** http://www.blog-gpgpu.com/ 
 ** http://www.microsoft.com/whdc/device/display/wddm_timeout.mspx
 **
 ** Inspired by the following sources:
 ** http://pouet.net/topic.php?which=7920&page=1&x=19&y=13
 ** IQ's Terrain Raymarching http://iquilezles.org/www/articles/terrainmarching/terrainmarching.htm
 ** Potatro, RayMarching and DistanceFields - A story of SphereTracing: http://code4k.blogspot.com/2009/10/potatro-and-raymarching-story-of.html
 ** Algebraic surfaces: http://www.freigeist.cc/gallery.html
 ** Raymarching discussion on pouet.net: http://www.pouet.net/topic.php?which=6675&page=1&x=13&y=12
 ** Bisection and isosurface normal approximation: http://sizecoding.blogspot.com/2008/08/isosurfaces-in-glsl.html
 ** Raytracing on the GPU (in german): http://www.uninformativ.de/?section=news&ndo=single&newsid=108
 ** WebGL Quaternionic Julia Set raymarching: http://www.iquilezles.org/apps/shadertoy/
 ** Ray Tracing Quaternion Julia Sets on the GPU: http://www.devmaster.net/forums/showthread.php?t=4448
 ** GPU Gems 3 - Chapter 30 - Real-Time Simulation and Rendering of 3D Fluids: http://http.developer.nvidia.com/GPUGems3/gpugems3_ch30.html
 ** GPU Raycasting Tutorial: http://cg.alexandra.dk/2009/04/28/gpu-raycasting-tutorial/
 ** A Simple and Flexible Volume Rendering Framework for Graphics-Hardware-based Raycasting: http://www.vis.uni-stuttgart.de/ger/research/fields/current/spvolren/
 **/

uniform mat4 cameraTransformation;
uniform vec3 cameraPosition;
uniform vec3 cameraTarget;
 
uniform vec2 resolution;

//////////////////////////////////////////////////////////////////////////
// NormalAtPoint
//////////////////////////////////////////////////////////////////////////
vec3 normalAtPoint ( vec3 P){
	vec3 n = vec3(0.0);
	vec3 offset = vec3 ( 0.01, 0, 0 );
	
	n.x = surfacefunction ( P + offset.xyz) -surfacefunction ( P -offset.xyz);
	n.y = surfacefunction ( P + offset.yxz) -surfacefunction ( P -offset.yxz);
	n.z = surfacefunction ( P + offset.yzx) -surfacefunction ( P -offset.yzx);
	
	return normalize ( n );
}

//////////////////////////////////////////////////////////////////////////
// AOAtPoint
//////////////////////////////////////////////////////////////////////////
float AOAtPoint (vec3 P, vec3 N){
	float total = 0.0;
	float weigth = 0.5;
	
	for (float i = 0.0; i < 3.0; i++ ){
		float delta = (i + 1.0) * (i + 1.0) * 0.01 * 17.0;
		total += weigth * ( delta - surfacefunction(P + N * delta));
		weigth *= 0.5;
	}
	
	return 1.0 - clamp(total, 0.0, 1.0);
}

void main(void) {

    //interpolate eye position from billboard fragment coordinates
    vec2 position = -1.0 + 2.0 * gl_FragCoord.xy / resolution.xy;
    position.x *= resolution.x / resolution.y;

    //define camera position and target
    vec3 target = vec3(position,0.0) + vec3(0.0, 0.0,1.5);
    target = vec3(cameraTransformation * vec4(target,1.0));

    vec3 ray = normalize(target - cameraPosition);
    
    float objectDistance = 0.0;
    vec3 checkPosition = cameraPosition;
    float numberOfSteps = 0.0;
    
    //core raymarching loop
    do {
        objectDistance = surfacefunction(checkPosition);
        
        if(objectDistance <= 0.01){
        	vec3 normal = normalAtPoint(checkPosition);
        	float ao = AOAtPoint(checkPosition, normal);
       		gl_FragColor = vec4(normal, 1.0);
        	return;
        }
        checkPosition += objectDistance * ray;
        numberOfSteps+=1.0;
        
    }while (numberOfSteps < 150.0);
    //gl_FragColor = vec4(numberOfSteps / 50.0,numberOfSteps / 50.0,numberOfSteps / 50.0,1.0);
    gl_FragColor = vec4(0.0);
    return;
}