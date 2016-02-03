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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlHandle;
import cc.creativecomputing.control.CCControlHandle.CCControlHandleListener;
import cc.creativecomputing.events.CCListenerManager;

/**
 * @author christianriekoff
 */
public class CCFilterManager implements CCControlHandleListener{
	
	public static enum CCFilterType{
		NONE,
		SIMPLE_MOVING_AVERAGE,
		DOUBLE_MOVING_AVERAGE,
		EXPONENTIAL_SMOOTHING,
		DOUBLE_EXPONENTIAL_SMOOTHING,
		MEDIAN,
		ONE_EURO
	}
	
	public static interface CCFilterManagerListener{
		
		public void filter(CCFilter<?> theFilter);
		
		@SuppressWarnings("rawtypes")
		public CCFilter filter();
	}
	
	public static class CCFilteredValue implements CCFilterManagerListener{

		private CCFilter<?> _myFilter;
		
		public CCFilteredValue() {
			_myFilter = new CCFilter<CCFilter<?>>();
		}
		
		@Override
		public void filter(CCFilter<?> theFilter) {
			float myValue = theFilter._myValue;
			_myFilter = theFilter;
			_myFilter._myValue = myValue;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public CCFilter filter() {
			return _myFilter;
		}
		
		public void value(float theValue, float theDeltaTime) {
			_myFilter.update(theValue, theDeltaTime);
		}
		
		public float value() {
			return _myFilter.value();
		}
	}
	
	@CCControl(name = "filter type")
	private CCFilterType _cFilterType = CCFilterType.SIMPLE_MOVING_AVERAGE;

	private  CCListenerManager<CCFilterManagerListener> _myEvents = CCListenerManager.create(CCFilterManagerListener.class); 
	
	private CCFilter<?> _myBaseFilter;
	
	private CCApp _myApp;
	
	private CCControlHandle _myManagerHandle;
	
	public CCFilterManager(CCApp theApp, CCFilter<?> theBaseFilter) {
		_myApp = theApp;
		_myBaseFilter = theBaseFilter;
	}
	
	public CCFilterManager(CCApp theApp) {
		this(theApp,new CCFilter<CCFilter<?>>());
	}
	
	@Override
	public void setControlHandle(CCControlHandle theHandle) {
		_myManagerHandle = theHandle;
	}
	
	public void baseFilter(CCFilter<?> theFilter) {
		if(_myManagerHandle == null)return;
		
		_myApp.ui().removeControl(_myManagerHandle.tabname(), "filter settings");
		
		_myBaseFilter = theFilter;
		_myApp.addControls(_myManagerHandle.tabname(), "filter settings", _myManagerHandle.column(), _myBaseFilter.settings());
		for(CCFilterManagerListener myListener:_myEvents) {
			myListener.filter(_myBaseFilter.clone());
		}
	}
	
	public CCFilteredValue createFilteredValue(float theValue){
		CCFilteredValue myFilteredValue = new CCFilteredValue();
		_myEvents.add(myFilteredValue);
		return myFilteredValue;
	}
	
	public CCFilteredValue createFilteredValue(){
		return createFilteredValue(0);
	}
	
	public CCFilter<?> createFilter() {
		return _myBaseFilter.clone();
	}
	
	public CCFilter<?> baseFilter() {
		return _myBaseFilter;
	}
	
	public void filterType(CCFilterType theType){
		_cFilterType = theType;
	}

	private CCFilterType _myLastFilter;
	
	public void update(float theDeltaTime) {
		if(_cFilterType == null)return;
		
		if (_cFilterType != _myLastFilter) {
			switch (_cFilterType) {
			case NONE:
				baseFilter(new CCFilter<>());
			case SIMPLE_MOVING_AVERAGE:
				baseFilter(new CCSimpleAverageFilter());
				break;
			case DOUBLE_MOVING_AVERAGE:
				baseFilter(new CCDoubleMovingAverageFilter());
				break;
			case EXPONENTIAL_SMOOTHING:
				baseFilter(new CCExponentialSmoothingFilter());
				break;
			case DOUBLE_EXPONENTIAL_SMOOTHING:
				baseFilter(new CCDoubleExponentialSmoothingFilter());
				break;
			case MEDIAN:
				baseFilter(new CCMedianFilter());
				break;
			case ONE_EURO:
				baseFilter(new CCOneEuroFilter());
				break;
			}
		}
		_myLastFilter = _cFilterType;
	}
	
	public void addListener(CCFilterManagerListener theListener) {
		theListener.filter(_myBaseFilter.clone());
		_myEvents.add(theListener);
	}
}
