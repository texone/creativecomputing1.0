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
package cc.creativecomputing.control.connect;

import cc.creativecomputing.control.ui.CCUIValueElement;

/**
 * @author christianriekoff
 *
 */
public class CCDirectConnector<ValueType> extends CCControlConnector<ValueType>{

	private String _myName;
	
//	private ValueType _myValue;
	private ValueType _myDefault;
	private ValueType _myMin;
	private ValueType _myMax;
	
	public CCDirectConnector(
		String theName,
		ValueType theValue, ValueType theDefault, ValueType theMin, ValueType theMax
	) {
		_myName = theName;
//		_myValue = theValue;
		_myDefault = theDefault;
		_myMin = theMin;
		_myMax = theMax;
	}
	
	@Override
	public ValueType defaultValue() {
		return _myDefault;
	}

	@Override
	public float max() {
		if(_myMax instanceof Number) return ((Number)_myMax).floatValue() ;
		return 0;
	}

	@Override
	public float maxX() {
		return 0;
	}

	@Override
	public float maxY() {
		return 0;
	}

	@Override
	public float min() {
		if(_myMin instanceof Number) return ((Number)_myMin).floatValue() ;
		return 0;
	}

	@Override
	public float minX() {
		return 0;
	}

	@Override
	public float minY() {
		return 0;
	}

	@Override
	public String name() {
		return _myName;
	}

	@Override
	public int numberOfEnvelopes() {
		return 0;
	}

	@Override
	public void onChange(CCUIValueElement<ValueType> theElement) {
	}

	@Override
	public void readBack(CCUIValueElement<ValueType> theElement) {
	}

	@Override
	public boolean toggle() {
		return false;
	}

	@Override
	public Class<?> type() {
		return null;
	}

	@Override
	public boolean external() {
		return false;
	}
	
	@Override
	public boolean accumulate() {
		return false;
	}
}
