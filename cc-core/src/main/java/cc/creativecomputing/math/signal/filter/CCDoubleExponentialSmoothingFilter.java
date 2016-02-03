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

/**
 * The double exponential smoothing filter is a popular smoothing filter used in many applications. 
 * Similar to a double moving averaging filter, the double exponential smoothing filter smoothes the 
 * smoothed output by applying a second exponential filter (hence the name double exponential), and it 
 * uses this to account for trends in input data. There are various formulations of double exponential 
 * smoothing filters, with minor differences between them this implementation is based on the most popular one.
 * <p>
 * @author christianriekoff
 *
 */
public class CCDoubleExponentialSmoothingFilter extends CCFilter<CCDoubleExponentialSmoothingFilter>{
	
	public static class CCDoubleExponentialSmoothingFilterSettings{
		@CCControl(name = "weight", min = 0, max = 1)
		protected float _cWeight;

		@CCControl(name = "trend weight", min = 0, max = 1)
		protected float _cTrendWeight;

		@CCControl(name = "forecast", min = 0, max = 10)
		protected float _cForeCast;
	}
	
	@CCControl(name = "double exponential filter")
	private CCDoubleExponentialSmoothingFilterSettings _mySettings;

	private float _myTrend;
	private float _myLastTrend;
	
	public CCDoubleExponentialSmoothingFilter(CCDoubleExponentialSmoothingFilterSettings theSettings) {
		_mySettings = theSettings;
	}
	
	public CCDoubleExponentialSmoothingFilter() {
		this(new CCDoubleExponentialSmoothingFilterSettings());
	}
	
	@Override
	public Object settings() {
		return _mySettings;
	}
	
	@Override
	public void reset() {
		super.reset();
		_myTrend = 0;
		_myLastTrend = 0;
	}
	
	@Override
	public void update(float theValue, float theDeltaTime) {
		_myLastTrend = _myTrend;
		_myTrend = (theValue - _myValue) * _mySettings._cTrendWeight + (1 - _mySettings._cTrendWeight) * _myLastTrend;
		_myValue = _mySettings._cWeight * theValue + (1 - _mySettings._cWeight) * (_myValue + _myLastTrend);
	}
	
	/**
	 * The double exponential smoothing filter fits a line to data and, therefore, forecasts 
	 * the future data as a straight line with a slope equal to trend term bn:
	 */
	@Override
	public float value() {
		return _myValue + _mySettings._cForeCast * _myTrend;
	}
	
	@Override
	public CCDoubleExponentialSmoothingFilter clone() {
		return new CCDoubleExponentialSmoothingFilter(_mySettings);
	}
}
