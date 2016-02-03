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
package cc.creativecomputing.math.random;

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSimplexNoise;

/**
 * @author christianriekoff
 * 
 */
public class CCCurlNoise {

	private CCSimplexNoise _mySimplexNoise;

	private float[] _myNoiseLengthScales = new float[3];
	private float[] _myNoiseGains = new float[3];

	public CCCurlNoise() {
		_mySimplexNoise = new CCSimplexNoise();

		_myNoiseLengthScales[0] = 0.4f;
		_myNoiseGains[0] = 1;
		_myNoiseLengthScales[1] = 0.23f;
		_myNoiseGains[1] = 0.5f;
		_myNoiseLengthScales[2] = 0.11f;
		_myNoiseGains[2] = 0.25f;
	}

	private float noise0(float x, float y, float z) {
		return _mySimplexNoise.value(x, y, z);
	}

	private float noise1(float x, float y, float z) {
		return _mySimplexNoise.value(y + 31.416f, z - 47.853f, x + 12.793f);
	}

	private float noise2(float x, float y, float z) {
		return _mySimplexNoise.value(z - 233.145f, x - 113.408f, y - 185.31f);
	}

	public CCVector3f noise(float x, float y, float z) {
		CCVector3f psi = new CCVector3f(0, 0, 0);
		// float d=distance_and_normal(x, y, z, normal);
		// add turbulence octaves that respect boundaries, increasing upwards
		for (int i = 0; i < _myNoiseLengthScales.length; i++) {
			float sx = x / _myNoiseLengthScales[i];
			float sy = y / _myNoiseLengthScales[i];
			float sz = z / _myNoiseLengthScales[i];

			CCVector3f psi_i = new CCVector3f(noise0(sx, sy, sz), noise1(sx, sy, sz), noise2(sx, sy, sz));
			psi.add(psi_i.scale(_myNoiseGains[i]));
		}

		return psi;
	}

}
