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
package cc.creativecomputing.control;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIChangeListener;
import cc.creativecomputing.control.ui.CCUIElement;
import cc.creativecomputing.control.ui.CCUIValueElement;
import cc.creativecomputing.events.CCListenerManager;

public abstract class CCValueControl<ValueType> implements CCUIChangeListener{
	
	public static interface CCChangeValueListener<ValueType>{
		public void onChangeValue(ValueType theValue);
	}
	
	protected CCControlConnector<ValueType> _myConnector;
	
	protected ValueType _myDefault;
	protected CCUIValueElement<ValueType> _myUIElement;
	
	@SuppressWarnings("rawtypes")
	protected CCListenerManager<CCChangeValueListener> _myEvents = CCListenerManager.create(CCChangeValueListener.class);
	
	protected String _myName;
	
	/**
	 * @param theMyField
	 * @param theMyObject
	 */
	@SuppressWarnings("unchecked")
	public CCValueControl(CCControlConnector<ValueType> theConnector, final ValueType theDefault, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		_myConnector = theConnector;
		
		_myDefault = theDefault;
		
		_myName = _myConnector.name();
		
		_myUIElement = createUIElement(theDefaultWidth, theDefaultHeight);
		
		numberOfPresets(theNumberOfPresets);
	
		_myUIElement.addChangeListener(this);
		_myUIElement.onChange();
	}
	
	public void defaultValue(ValueType theDefault){
		_myDefault = theDefault;
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCChangeValueListener> events(){
		return _myEvents;
	}
	
	public void numberOfPresets(int theNumberOfPresets) {
		List<ValueType> myValues = new ArrayList<ValueType>();
		for(int i = 0; i < theNumberOfPresets;i++) {
			myValues.add(defaultValue());
		}
		_myUIElement.values(myValues);
	}
	
	public CCUIValueElement<ValueType> element() {
		return _myUIElement;
	}

	protected abstract ValueType stringToValue(final String theStringValue);
	
	ValueType defaultValue() {
		return _myDefault;
	}
	
	public String name() {
		return _myName;
	}
	
	public void name(String theName){
		_myName = theName;
		_myUIElement.label(theName);
	}
	
	@SuppressWarnings("unchecked")
	public void value(ValueType theValue){
		_myUIElement.changeValue(theValue);
		_myEvents.proxy().onChangeValue(theValue);
	}
	
	public ValueType value() {
		return _myUIElement.value();
	}
	
	public boolean external() {
		return _myConnector.external();
	}
	
	public boolean accumulate() {
		return _myConnector.accumulate();
	}
	
	public float min() {
		return _myConnector.min();
	}
	
	public float max() {
		return _myConnector.max();
	}
	
	@SuppressWarnings("rawtypes")
	abstract CCUIValueElement createUIElement(final float theWidth, final float theHeight);
	
	@SuppressWarnings("unchecked")
	public void onChange(CCUIElement theElement){
		CCUIValueElement<ValueType> myValueElement = (CCUIValueElement<ValueType>)theElement;
		_myConnector.onChange(myValueElement);
		_myEvents.proxy().onChangeValue(myValueElement.value());
	}

	public void readBack() {
		_myConnector.readBack(_myUIElement);
	}
}
