#version 120


uniform sampler2DRect data;
uniform sampler2DRect lookUp;
uniform float threshold;

uniform float red;
uniform float green;
uniform float blue;
uniform int frameMod;

float brightness(vec4 color){
	return red * color.r + green * color.g + blue * color.b;
}

void main() {
   	vec4 ids1 = texture2DRect(lookUp, gl_TexCoord[0].xy);
	vec4 col1 = texture2DRect(data, ids1.xy);
	float b1 = brightness(col1);
	
	if(mod(int(gl_TexCoord[0].y) ,2) == frameMod){
		vec4 ids2 = texture2DRect(lookUp, gl_TexCoord[0].xy + vec2(0.0,1.0));
		vec4 col2 = texture2DRect(data, ids2.xy);
		float b2 = brightness(col2);
		if(b2 > b1 && b2 > threshold && b1 > threshold){
    	 	gl_FragColor = ids2;
     	} else if(b2 < b1 && b2 < threshold && b1 < threshold){
    	 	gl_FragColor = ids2;
     	} else {
    	 	gl_FragColor = ids1;
     	}
     	//gl_FragColor = vec4(1.0,0.0,0.0,1.0);
	} else {
		vec4 ids2 = texture2DRect(lookUp, gl_TexCoord[0].xy - vec2(0.0,1.0));
		vec4 col2 = texture2DRect(data, ids2.xy);
		float b2 = brightness(col2);
		if(b1 > b2 && b2 > threshold && b1 > threshold){
    	 	gl_FragColor = ids2;
     	} else if(b1 < b2 && b2 < threshold && b1 < threshold){
    	 	gl_FragColor = ids2;
     	} else {
    	 	gl_FragColor = ids1;
     	}
     	//gl_FragColor = vec4(0.0,0.0,1.0,1.0);
	}
	
}
