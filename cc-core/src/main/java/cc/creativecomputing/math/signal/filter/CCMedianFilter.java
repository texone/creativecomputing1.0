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

import java.util.Arrays;

import cc.creativecomputing.control.CCControl;

/**
 * In a median filter (also known as moving median filter), the filter's output is the 
 * median of the last N inputs. Median filters are useful in removing impulsive spike noises, 
 * as shown in Figure 9. Ideally, the filter size N should be selected to be larger than the 
 * duration of the spike noise peaks. However, the filter's latency directly depends on N, and 
 * hence, a larger N adds more latency.
 * @author christianriekoff
 *
 */
public class CCMedianFilter extends CCFilter<CCMedianFilter>{
	
	public static class CCMedianFilterSettings{
		@CCControl(name = "buffer size", min = 1, max = 100)
		private int _cBufferSize = 1;
	}

	protected float[] _myBuffer;
	protected float[] _mySortedValues;
	protected int _myBufferSize;
	protected int _myUseableBuffer;
	protected int _myIndex = 0;
	
	@CCControl(name = "median filter settings")
	private CCMedianFilterSettings _mySettings;
	
	public CCMedianFilter(CCMedianFilterSettings theSettings) {
		_mySettings = theSettings;
		_myBufferSize = _mySettings._cBufferSize;
		reset();
	}
	
	public CCMedianFilter() {
		this(new CCMedianFilterSettings());
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
		_mySortedValues = new float[_myBufferSize];
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
		_myIndex++;
		_myIndex %= _myBufferSize;
		
		System.arraycopy(_myBuffer, 0, _mySortedValues, 0, _myUseableBuffer);
		Arrays.sort(_mySortedValues, 0, _myUseableBuffer);
		
		_myValue = _mySortedValues[_myUseableBuffer / 2];
	}
	
	@Override
	public CCMedianFilter clone() {
		return new CCMedianFilter(_mySettings);
	}
}
