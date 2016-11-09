/**
 * This program implements the final step in the fluid simulation.  After 
 * the poisson solver has iterated to find the pressure disturbance caused by
 * the divergence of the velocity field, the gradient of that pressure needs
 * to be subtracted from this divergent velocity to get a divergence-free
 * velocity field:
 *
 * v-zero-divergence = v-divergent -  grad(p)
 *
 * The gradient(p) is defined: 
 *     grad(p) = (partial(p)/partial(x), partial(p)/partial(y))
 *
 * The discrete form of this is:
 *     grad(p) = ((p(i+1,j) - p(i-1,j)) / 2dx, (p(i,j+1)-p(i,j-1)) / 2dy)
 *
 * where dx and dy are the dimensions of a grid cell.
 *
 * This program computes the gradient of the pressure and subtracts it from
 * the velocity to get a divergence free velocity.
 */

// 0.5 / grid scale 
uniform half halfrdx;        
uniform samplerRECT pressureTexture;
uniform samplerRECT velocityTexture;
      
void main(
	in half2 coords : WPOS,  // grid coordinates
	out half4 uNew : COLOR // divergence (output)//hvfFlo IN, 
){
	half pL = h1texRECT(pressureTexture, coords + float2(-1, 0));
	half pR = h1texRECT(pressureTexture, coords + float2( 1, 0));
	half pB = h1texRECT(pressureTexture, coords + float2( 0,-1));
	half pT = h1texRECT(pressureTexture, coords + float2( 0, 1));

  	half2 grad = half2(pR - pL, pT - pB) * halfrdx;
  	

  	uNew = h4texRECT(velocityTexture, coords);
  	uNew.xy -= grad;
} 