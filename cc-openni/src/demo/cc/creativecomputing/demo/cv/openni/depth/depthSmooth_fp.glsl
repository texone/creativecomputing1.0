#version 120


uniform sampler2D depthData;
uniform sampler2D prevDepthData;
uniform float smoothing;
uniform float threshold;

#define s2(a, b)				temp = a; a = min(a, b); b = max(temp, b);
#define t2(a, b)								s2(v[a], v[b]);
#define t24(a, b, c, d, e, f, g, h)				t2(a, b); t2(c, d); t2(e, f); t2(g, h); 
#define t25(a, b, c, d, e, f, g, h, i, j)		t24(a, b, c, d, e, f, g, h); t2(i, j);


void main() {

  vec3 v[25];
  
  vec3 av = vec3(0.0);

  // Add the pixels which make up our window to the pixel array.
  for(int dX = -2; dX <= 2; ++dX) {
    for(int dY = -2; dY <= 2; ++dY) {		
      vec2 offset = vec2(float(dX), float(dY));
		    
      // If a pixel in the window is located at (x+dX, y+dY), put it at index (dX + R)(2R + 1) + (dY + R) of the
      // pixel array. This will fill the pixel array, with the top left pixel of the window at pixel[0] and the
      // bottom right pixel of the window at pixel[N-1].
      vec3 depth = texture2D(depthData, gl_TexCoord[0].xy + offset * vec2(1.0/320.0, 1.0/240.0)).xyz;
      v[(dX + 2) * 5 + (dY + 2)] = depth;
      
      av += depth;
    }
  }
  
  av /= 25.0;

  vec3 temp;

  t25(  0,  1,		 3,  4, 	 2,  4,		 2,  3,		 6,  7);
  t25(  5,  7,		 5,  6,		 9,  7,		 1,  7,		 1,  4);
  t25( 12, 13,	 	11, 13,		11, 12,		15, 16,		14, 16);
  t25( 14, 15,	 	18, 19,		17, 19,		17, 18,		21, 22);
  t25( 20, 22,		20, 21,		23, 24,		 2,  5,		 3,  6);
  t25(  0,  6,		 0,  3,		 4,  7,		 1,  7,		 1,  4);
  t25( 11, 14,		 8, 14,		 8, 11,		12, 15,		 9, 15);
  t25(  9, 12,		13, 16,		10, 16,		10, 13,		20, 23);
  t25( 17, 23,		17, 20,		21, 24,		18, 24,		18, 21);
  t25( 19, 22,		 8, 17,		 9, 18,		 0, 18,		 0,  9);
  t25( 10, 19,		 1, 19,		 1, 10,		11, 20,		 2, 20);
  t25(  2, 11,		12, 21,		 3, 21,		 3, 12,		13, 22);
  t25(  4, 22,		 4, 13,		14, 23,		 5, 23,		 5, 14);
  t25( 15, 24,		 6, 24,		 6, 15,		 7, 16,		 7, 19);
  t25(  3, 11,		 5, 17,		11, 17,		 9, 17,		 4, 10);
  t25(  6, 12,		 7, 14,		 4,  6,		 4,  7,		12, 14);
  t25( 10, 14,		 6,  7,		10, 12,		 6, 10,		 6, 17);
  t25( 12, 17,		 7, 17,		 7, 10,		12, 18,		 7, 12);
  t24( 10, 18,		12, 20,		10, 20,		10, 12);
  
  vec3 currentDepth = v[12];//texture2D(depthData, gl_TexCoord[0].xy).xyz;//v[12];
  vec3 prevDepth = texture2D(prevDepthData, gl_TexCoord[0].xy).xyz;

  if(prevDepth.x == 0){
  	gl_FragColor.xyz = currentDepth;
  }else if(currentDepth.x == 0){
  	gl_FragColor.xyz = prevDepth;
  }else if(abs(prevDepth.x - currentDepth.x) > threshold){
  	gl_FragColor.xyz = currentDepth;
  }else{
  	gl_FragColor.xyz = mix(currentDepth, prevDepth, smoothing);
  }
  
}
