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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.ui.CCUIValueElement;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 *
 */
public class CCReflectionConnector<ValueType> extends CCControlConnector<ValueType>{

	protected CCControl _myControl;
	protected final Member _myMember;
	protected final Object _myObject;
	protected Class<?> _myClass;
	
	public CCReflectionConnector(CCControl theControl, Member theMember, Object theObject) {
		_myControl = theControl;
		_myMember = theMember;
		_myObject = theObject;
		
		if(_myMember instanceof Field){
			_myClass = ((Field)_myMember).getType();
		}else{
			Class<?>[] myParameterTypes = ((Method)_myMember).getParameterTypes();
			if(myParameterTypes.length > 0)_myClass = ((Method)_myMember).getParameterTypes()[0];
			else _myClass = null;
		}
	}
	
	public CCControl control() {
		return _myControl;
	}
	
	public void onChange(CCUIValueElement<ValueType> theElement){
		try {
			if(_myMember instanceof Field){
				((Field)_myMember).set(_myObject, theElement.value());
			}else{
				if(_myClass != null)((Method)_myMember).invoke(_myObject, theElement.value());
				else ((Method)_myMember).invoke(_myObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
//		catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} 
	}
	
	@SuppressWarnings("unchecked")
	public ValueType defaultValue() {
		if(_myMember instanceof Field){
			_myClass = ((Field)_myMember).getType();
			try {
				return (ValueType)((Field)_myMember).get(_myObject);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readBack(CCUIValueElement<ValueType> theElement) {
		try {
			if (_myMember instanceof Field) {
				theElement.value((ValueType)((Field)_myMember).get(_myObject));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
		 	e.printStackTrace();
		} catch (ClassCastException e) {
			CCLog.error("warning: could not cast " + _myMember.getName() + " for readBack.");
		}
	}
	
	@Override
	public String name() {
		return _myControl.name().equals("") ? _myMember.getName() : _myControl.name();
	}
	
	@Override
	public float min() {
		return _myControl.min();
	}
	
	@Override
	public float max() {
		return _myControl.max();
	}
	
	@Override
	public float minX() {
		return _myControl.minX();
	}
	
	@Override
	public float maxX() {
		return _myControl.maxX();
	}
	
	@Override
	public float minY() {
		return _myControl.minY();
	}
	
	@Override
	public float maxY() {
		return _myControl.maxY();
	}

	@Override
	public boolean toggle() {
		return _myControl.toggle();
	}
	
	@Override
	public Class<?> type() {
		return _myClass;
	}
	
	@Override
	public int numberOfEnvelopes() {
		return _myControl.numberOfEnvelopes();
	}
	
	@Override
	public boolean external() {
		return _myControl.external();
	}
	
	@Override
	public boolean accumulate() {
		return _myControl.accumulate();
	}
}
