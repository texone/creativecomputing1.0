
varying vec3 norm, eyeVec;
varying vec4 diffuse0, ambient;
varying vec3 lightDir[1];

void main (void)
{
  vec4 final_color = gl_BackLightModelProduct.sceneColor;
  vec3 N = normalize(norm);

  int i;
  for (i=0; i<1; ++i)
  {
    vec3 L = normalize(lightDir[i]);
    float lambertTerm = dot(N,L);
    if (lambertTerm > 0.0)
    {
      final_color +=
        gl_LightSource[i].diffuse *
        gl_FrontMaterial.diffuse *
        lambertTerm;
      vec3 E = normalize(eyeVec);
      vec3 R = reflect(-L, N);
      float specular = pow(max(dot(R, E), 0.0), gl_FrontMaterial.shininess);
      final_color += gl_LightSource[i].specular * gl_FrontMaterial.specular * specular;
    }
  }
  final_color.w = 1.0;
  gl_FragColor = gl_FrontMaterial.diffuse;
}

