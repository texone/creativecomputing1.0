
uniform float fresnelPower;
uniform float eta;
uniform float f;

varying vec3  reflect;
varying vec3  refract;
varying float ratio;

void main()
{

    vec4 ecPosition  = gl_ModelViewMatrix * gl_Vertex;
    vec3 ecPosition3 = ecPosition.xyz / ecPosition.w;

    vec3 i = normalize(ecPosition3);
    vec3 n = normalize(gl_NormalMatrix * gl_Normal);

    ratio   = f + (1.0 - f) * pow((1.0 - dot(-i, n)), fresnelPower);

    refract = refract(i, n, eta);
    refract = vec3(gl_TextureMatrix[0] * vec4(refract, 1.0));

    reflect = reflect(i, n);
    reflect = vec3(gl_TextureMatrix[0] * vec4(reflect, 1.0));

    gl_Position = ftransform();
}