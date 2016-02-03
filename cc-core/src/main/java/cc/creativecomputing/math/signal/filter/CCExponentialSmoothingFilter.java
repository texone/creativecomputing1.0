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
public class CCExponentialSmoothingFilter extends CCFilter<CCExponentialSmoothingFilter>{
	
	public static class CCExponentialSmoothingFilterSettings{
		@CCControl(name = "weight", min = 0, max = 1)
		protected float _cWeight;
		
		@CCControl(name = "skip range", min = 0, max = 50)
		protected float _cSkipRange = Float.MAX_VALUE;
	}
	
	@CCControl(name = "exponential smoothing filter")
	private CCExponentialSmoothingFilterSettings _mySettings;
	
	public CCExponentialSmoothingFilter(CCExponentialSmoothingFilterSettings theSettings) {
		_mySettings = theSettings;
	}
	
	public CCExponentialSmoothingFilter() {
		this(new CCExponentialSmoothingFilterSettings());
	}
	
	@Override
	public Object settings() {
		return _mySettings;
	}
	
	@Override
	public void update(float theValue, float theDeltaTime) {
		if(_myValue == 0) {
			_myValue = theValue;
			return;
		}
		if(CCMath.abs(_myValue - theValue) > _mySettings._cSkipRange && _mySettings._cSkipRange > 0) {
			_myValue = theValue;
			return;
		}
		
		_myValue = _myValue * _mySettings._cWeight + theValue * (1 - _mySettings._cWeight);
	}
	
	@Override
	public CCExponentialSmoothingFilter clone() {
		return new CCExponentialSmoothingFilter(_mySettings);
	}
}
