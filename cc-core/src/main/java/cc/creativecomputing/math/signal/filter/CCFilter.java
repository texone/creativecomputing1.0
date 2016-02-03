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

/**
 * @author christianriekoff
 *
 */
public class CCFilter<FilterType extends CCFilter<?>> {
	
	protected float _myValue = 0;
	
	public CCFilter() {
		_myValue = 0;
	}

	public void reset() {
		_myValue = 0;
	}
	
	public Object settings(){
		return "";
	}
	
	public void update(float theValue, float theDeltaTime) {
		_myValue = theValue;
	}
	
	public float value() {
		return _myValue;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FilterType clone() {
		return (FilterType)(new CCFilter());
	}
}
