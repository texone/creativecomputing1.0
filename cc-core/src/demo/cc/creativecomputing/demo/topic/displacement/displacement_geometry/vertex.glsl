uniform sampler2D displacementTexture;
uniform float displacement;

void main(){	
	gl_TexCoord[0]  = gl_MultiTexCoord0;
	vec4 displacementColor = texture2D(displacementTexture, gl_MultiTexCoord0.xy);
	vec4 displaced = gl_Vertex;
	displaced.y += displacementColor.r * displacement;
	gl_Position = displaced;
}