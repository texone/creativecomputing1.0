/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.math.signal;

import java.util.Random;

import cc.creativecomputing.math.CCMath;

public class CCPerlinNoise extends CCSignal{

	private static float fade(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	private static float dfade(float t) {
		return 30.0f * t * t * (t * (t - 2.0f) + 1.0f );
	}

	private static float grad(int hash, float x, float y) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		float u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
		v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private static float grad(int hash, float x, float y, float z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		float u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
		v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private int _mySeed = 0x214;

	private int[] mPerms = new int[512];

	public CCPerlinNoise() {
		initPermutationTable();
	}

	public CCPerlinNoise(int aSeed) {
		_mySeed = aSeed;
		initPermutationTable();
	}

	private void initPermutationTable() {
		Random rand = new Random(_mySeed);
		for (int t = 0; t < 256; ++t) {
			mPerms[t] = mPerms[t + 256] = rand.nextInt() & 255;
		}
	}

	public void seed(int theSeed) {
		_mySeed = theSeed;
		initPermutationTable();
	}

	// / Calculates a single octave of noise
//	public float noiseImpl(float x) {
//		int X = CCMath.floor(x) & 255;
//
//		x -= CCMath.floor(x);
//
//		float u = fade(x);
//
//		int A = mPerms[X];
//		int AA = mPerms[A];
//		int B = mPerms[X + 1];
//		int BA = mPerms[B];
//
//		return (lerp(u, grad(mPerms[AA], x), grad(mPerms[BA], x - 1)) + 1) / 2;
//	}

	@Override
	public float[] signalImpl( float x, float y ) {
		int X = CCMath.floor(x) & 255;
		int Y = CCMath.floor(y) & 255;
		x -= CCMath.floor(x); 
		y -= CCMath.floor(y);
		
		float u = fade(x);
		float v = fade(y);
		
		float du = dfade(x);
		float dv = dfade(y);
		
		int A = mPerms[X  ]+Y, AA = mPerms[A]+0, AB = mPerms[A+1]+0,
			B = mPerms[X+1]+Y, BA = mPerms[B]+0, BB = mPerms[B+1]+0;

		if( du < 0.000001f ) du = 1.0f;
		if( dv < 0.000001f ) dv = 1.0f;

		float a = grad( mPerms[AA], x  , y);
		float b = grad( mPerms[BA], x-1, y);
		float c = grad( mPerms[AB], x  , y - 1);
		float d = grad( mPerms[BB], x-1, y - 1);

		float k0 = a;
	    float k1 = b - a;
	    float k2 = c - a;
	    float k3 = d - c + b - a;
	    float k4 = a - b - c + d;
 
	    return new float[] {
			(k0 + k1 * u + k2 * v + k3 * v * u) * 0.5f + 0.5f,
			du * ( k1 + k4 * v ) * 0.5f + 0.5f, 
			dv * ( k2 + k4 * u ) * 0.5f + 0.5f
	    };
	}

	@Override
	/**
	 * Implementation that returns the noise and its three derivatives in one go based on inigo quilez
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return
	 */
	public float[] signalImpl(float theX, float theY, float theZ) {
		int X = CCMath.floor(theX) & 255;
		int Y = CCMath.floor(theY) & 255;
		int Z = CCMath.floor(theZ) & 255;
		
		theX -= CCMath.floor(theX); 
		theY -= CCMath.floor(theY); 
		theZ -= CCMath.floor(theZ);
		
		float u = fade( theX );
		float v = fade( theY );
		float w = fade( theZ );
		
		float du = dfade( theX );
		float dv = dfade( theY );
		float dw = dfade( theZ );
		
		int A = mPerms[X] + Y;
		int AA = mPerms[A] + Z;
		int AB = mPerms[A+1] + Z;
		
		int B = mPerms[X+1] + Y;
		int BA = mPerms[B] + Z;
		int BB = mPerms[B+1] + Z;

		if( du < 0.000001f ) du = 1.0f;
		if( dv < 0.000001f ) dv = 1.0f;
		if( dw < 0.000001f ) dw = 1.0f;	

		float a = grad( mPerms[AA  ], theX  , theY  , theZ  );
		float b = grad( mPerms[BA  ], theX-1, theY  , theZ  );
		float c = grad( mPerms[AB  ], theX  , theY-1, theZ  );
		float d = grad( mPerms[BB  ], theX-1, theY-1, theZ  );
		float e = grad( mPerms[AA+1], theX  , theY  , theZ-1);
		float f = grad( mPerms[BA+1], theX-1, theY  , theZ-1);
		float g = grad( mPerms[AB+1], theX  , theY-1, theZ-1);
		float h = grad( mPerms[BB+1], theX-1, theY-1, theZ-1);

		float k0 =  a;
	    float k1 =  b - a;
	    float k2 =  c - a;
	    float k3 =  e - a;
	    float k4 =  a - b - c + d;
	    float k5 =  a - c - e + g;
	    float k6 =  a - b - e + f;
	    float k7 = -a + b + c - d + e - f - g + h;

		return new float[] {
			(k0 + k1 * u + k2 * v + k3 * w + k4 * u * v + k5 * v * w + k6 * w * u + k7 * u * v * w) * 0.5f + 0.5f,
			du * (k1 + k4 * v + k6 * w + k7 * v * w) * 0.5f + 0.5f,
			dv * (k2 + k5 * w + k4 * u + k7 * w * u) * 0.5f + 0.5f,
			dw * (k3 + k6 * u + k5 * v + k7 * u * v) * 0.5f + 0.5f
		};
	}

}
