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
 * @author christianriekoff
 *
 */
public class CCDoubleMovingAverageFilter extends CCFilter<CCDoubleMovingAverageFilter>{
	
	public static class CCDoubleMovingAverageFilterSettings{
		@CCControl(name = "buffer size", min = 1, max = 100)
		private int _cBufferSize = 1;
		
		@CCControl(name = "forecast", min = 0, max = 10)
		private float _cForeCast = 0;
	}

	protected float[] _myBuffer;
	protected int _myBufferSize;
	protected int _myUseableBuffer;
	protected int _myIndex = 0;
	
	private float[] _myMovingAverages;
	private float _myAverage;
	private float _myAverageAverage;
	
	@CCControl(name = "double average settings")
	private CCDoubleMovingAverageFilterSettings _mySettings;
	
	public CCDoubleMovingAverageFilter(CCDoubleMovingAverageFilterSettings theSettings) {
		_myBufferSize = theSettings._cBufferSize;
		_mySettings = theSettings;
		reset();
	}
	
	public CCDoubleMovingAverageFilter() {
		this(new CCDoubleMovingAverageFilterSettings());
	}
	
	@Override
	public Object settings() {
		return _mySettings;
	}
	
	@Override
	public void reset() {
		super.reset();
		_myUseableBuffer = 0;
		_myIndex = 0;
		_myBuffer = new float[_myBufferSize];
		_myMovingAverages = new float[_myBufferSize];
	}
	
	@Override
	public void update(float theValue, float theDeltaTime) {
		if(_myBufferSize != _mySettings._cBufferSize){
			_myBufferSize = _mySettings._cBufferSize;
			reset();
		}
		if(_myUseableBuffer < _myBufferSize) {
			_myUseableBuffer++;
		}
		
		_myBuffer[_myIndex] = theValue;
		
		_myAverage = 0;
		for(int i = 0; i < _myUseableBuffer;i++) {
			_myAverage += _myBuffer[i];
		}
		_myAverage /= _myUseableBuffer;
		
		_myMovingAverages[_myIndex] = _myAverage;
		_myAverageAverage = 0;
		for(int i = 0; i < _myUseableBuffer;i++) {
			_myAverageAverage += _myMovingAverages[i];
		}
		_myAverageAverage /= _myUseableBuffer;
		
		_myIndex++;
		_myIndex %= _myBufferSize;
		
		_myValue = 2 * _myAverage - _myAverageAverage;
	}
	
	@Override
	public float value() {
		return _myValue + (2 * _mySettings._cForeCast) / (_myUseableBuffer + 1) * (_myAverage - _myAverageAverage);
	}
	
	@Override
	public CCDoubleMovingAverageFilter clone() {
		return new CCDoubleMovingAverageFilter(_mySettings);
	}
}
