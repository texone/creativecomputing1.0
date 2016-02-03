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
 * The simple averaging filter is the simplest joint filter, where the filter output is the average of 
 * N recent inputs, which is an MA filter of order N with ai=1/(N+1) for all i.
 * <p>
 * From a statistical point of view, the averaging filter is a naive filter that fits a horizontal line 
 * (that is, a constant) to N recent inputs and uses it as the filter output. Therefore, an averaging 
 * filter is not taking advantage of joint data characteristics or noise statistical distribution, and 
 * it preserves only the first-order moment of data, which is the average. A simple averaging filter doesnt 
 * provide satisfactory results in most cases of filtering
 * <p>
 * An averaging filter using a large N would result in more smoothing than a smaller N, but it would 
 * introduce more filtering delay. The filtering delay can be noted in the output from an averaging filter 
 * in response to inputs from step functions and sinusoidal waveforms , where filtering delay is directly 
 * proportional to N. -->For example, the step function rise time for N=5 and N=10 are about 4.5 frames 
 * (148 msec) and 9 frames (297 msec), respectively. The simple averaging is a linear phase filter, which
 *  means that all frequency components in input are delayed by the same amount [6]. To experience this, 
 *  try different frequencies for sinusoidal input in the spreadsheet, and notice that the output delay is 
 *  the same for all filters.
 * @author christianriekoff
 *
 */
public class CCSimpleAverageFilter extends CCFilter<CCSimpleAverageFilter>{
	
	public static class CCSimpleAverageFilterSettings{
		@CCControl(name = "buffer size", min = 1, max = 100)
		private int _cBufferSize = 1;
	}

	protected float[] _myBuffer;
	protected int _myUseableBuffer;
	protected int _myIndex = 0;
	protected int _myBufferSize;
	
	@CCControl(name = "simple average settings")
	private CCSimpleAverageFilterSettings _mySettings;
	
	public CCSimpleAverageFilter(CCSimpleAverageFilterSettings theSettings) {
		_mySettings = theSettings;
		_myBufferSize = theSettings._cBufferSize;
		reset();
	}
	
	public CCSimpleAverageFilter(){
		this(new CCSimpleAverageFilterSettings());
	}
	
	@Override
	public Object settings() {
		return _mySettings;
	}
	
	@Override
	public void reset() {
		_myUseableBuffer = 0;
		_myIndex = 0;
		_myBuffer = new float[_myBufferSize];
	}
	
	@Override
	public void update(float theValue, float theDeltaTime) {
		if(_myBufferSize != _mySettings._cBufferSize){
			_myBufferSize = _mySettings._cBufferSize;
			reset();
		}
		if(_myUseableBuffer < _mySettings._cBufferSize) {
			_myUseableBuffer++;
		}
		
		_myBuffer[_myIndex] = theValue;
		_myIndex++;
		_myIndex %= _mySettings._cBufferSize;
		
		_myValue = 0f;
		for(int i = 0; i < _myUseableBuffer;i++) {
			_myValue += _myBuffer[i];
		}
		_myValue /= _myUseableBuffer;
	}
	
	@Override
	public CCSimpleAverageFilter clone() {
		return new CCSimpleAverageFilter(_mySettings);
	}
}
