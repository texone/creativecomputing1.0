uniform samplerRECT previous_cells;
uniform samplerRECT current_cells;
uniform samplerRECT wave_break_inner_edges;

uniform float waveInnerEdgesStrength;
uniform float damping;

uniform float startRange;
uniform float range;

uniform float normalHeightScale;

void main(
	in float2 texCoord : TEXCOORD0,
	out float4 color : COLOR0,
	out float4 normal : COLOR1
){
	float s = texCoord.x;
    float t = texCoord.y;

    const float d = 1.0;

    // sum neighbours from current heightfield
    // the values are stored in the 2nd (green or y) channel
    float current_neighbour_sum =
        texRECT( current_cells, float2(s - d, 	t - d)).y +
        texRECT( current_cells, float2(s, 		t - d)).y +
        texRECT( current_cells, float2(s + d, 	t - d)).y +

        texRECT( current_cells, float2(s - d, 	t)).y +
        texRECT( current_cells, float2(s + d, 	t)).y +

        texRECT( current_cells, float2(s - d, 	t + d)).y +
        texRECT( current_cells, float2(s, 		t + d)).y +
        texRECT( current_cells, float2(s + d, 	t + d)).y;
        
         // wave breaks
	float height = texRECT( wave_break_inner_edges, texCoord.xy).r;
    
    float blend = 1 - saturate((height - (startRange + range)) / (1 - (startRange + range)));
        
 	float3 myPos = float3(s - 10, t - 10,texRECT( current_cells, float2(s, t)).y * normalHeightScale * blend);
 	float3 myA = float3(s + 10,t,texRECT( current_cells, float2(s + 1, t)).y * normalHeightScale) - myPos;
 	float3 myB = float3(s,t + 10,texRECT( current_cells, float2(s, t + 1)).y * normalHeightScale) - myPos;
 	float3 normal3 = (normalize(cross(myA,myB)) + 1) / 2;
 	normal = float4(normal3.x,normal3.y,normal3.z,1);
 	

    // fetch previous height at this position
    float previous_height = texRECT( previous_cells, texCoord.xy ).y;

    // fetch splash intensity from the red channel of the current buffer
    float splash = texRECT( current_cells, texCoord.xy).r;

	// wave breaks
	float waveBreakInnerEdges = texRECT( wave_break_inner_edges, texCoord.xy).r;
	waveBreakInnerEdges = saturate((waveBreakInnerEdges - startRange) / range);
    waveBreakInnerEdges =  (1-waveBreakInnerEdges) * waveInnerEdgesStrength;
    float new_height = damping * ( current_neighbour_sum / 4.0 - previous_height )  + splash + waveBreakInnerEdges;

	// clamp	
	new_height = max (new_height, -0.5);
	new_height = min (new_height, 0.5);

    // output new height in the green channel
    color = float4(0, new_height, 0, 1);
    //color = float4(0, waveBreakInnerEdges, 0, 1);
}
