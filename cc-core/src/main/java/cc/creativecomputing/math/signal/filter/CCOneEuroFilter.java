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
package cc.creativecomputing.math.signal.filter;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class CCOneEuroFilter extends CCFilter<CCOneEuroFilter> {
	private class SingleExponentialFilter {

		private float _myLastX;
		private float _myAlpha;
		private float _myLastEstimate;

		private SingleExponentialFilter(float theAlpha) {
			_myAlpha = theAlpha;
			_myLastEstimate = -1;
			_myLastX = -1;
		}

		public float filter(float x) {
			if (_myLastX == -1) {
				_myLastEstimate = x;
			} else {
				_myLastEstimate = lowpass(x);
			}
			_myLastX = x;

			return _myLastEstimate;
		}

		public float lowpass(float x) {
			return _myAlpha * x + (1.0f - _myAlpha) * _myLastEstimate;
		}
	}
	
	public static class CCOneEuroFilterSettings{
		@CCControl(name = "minimum cutoff", min = 0, max = 10)
		private float _cMinimumCutoff = 1f;
		@CCControl(name = "cutoff slope", min = 0, max = 1)
		private float _cBeta = 0.007f;
		@CCControl(name = "derivative cutoff", min = 0, max = 10)
		private float _cDerivativeCutoff = 1f;
	}
	
	private SingleExponentialFilter _myFilter;
	private SingleExponentialFilter _myDerivativeFilter;
	
	private float _myFrequency;
	
	@CCControl(name = "oneeuro filter settings")
	private CCOneEuroFilterSettings _mySettings;

	public CCOneEuroFilter(float theFrequency, CCOneEuroFilterSettings theSettings) {
		_mySettings = theSettings;
		_myFrequency = theFrequency;
		_myFilter = new SingleExponentialFilter(alpha(_mySettings._cMinimumCutoff));
		_myDerivativeFilter = new SingleExponentialFilter(alpha(_mySettings._cDerivativeCutoff));
	}
	
	public CCOneEuroFilter(){
		this(25, new CCOneEuroFilterSettings());
	}
	
	@Override
	public Object settings() {
		return _mySettings;
	}

	private float alpha(float cutoff) {
		float te = 1.0f / _myFrequency;
		float tau = 1.0f / (2 * CCMath.PI * cutoff);
		return 1.0f / (1.0f + tau / te);
	}
	
	@Override
	public void update(float x, float theDeltaTime) {
		_myFrequency = 1.0f / (theDeltaTime);
		
		float previousX = _myFilter._myLastX;
		float dx = (previousX == -1) ? 0 : (x - previousX) * _myFrequency;
		
		_myDerivativeFilter._myAlpha = alpha(_mySettings._cDerivativeCutoff);
		float edx = _myDerivativeFilter.filter(dx);
		float cutoff = _mySettings._cMinimumCutoff + _mySettings._cBeta * Math.abs(edx);
		
		_myFilter._myAlpha = this.alpha(cutoff);
		_myValue = _myFilter.filter(x);
	}
	
	@Override
	public CCOneEuroFilter clone() {
		return new CCOneEuroFilter(_myFrequency, _mySettings);
	}
}
