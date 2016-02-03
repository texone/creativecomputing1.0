varying vec3  reflect;
varying vec3  refract;
varying float ratio;

uniform samplerCube cubemap;

void main(){
    vec3 refractColor = vec3(textureCube(cubemap, refract));
    vec3 reflectColor = vec3(textureCube(cubemap, reflect));

    vec3 color   = mix(refractColor, reflectColor, ratio);

    gl_FragColor = vec4(color, 1.0);
}