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
package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;


public abstract class CCBuffer <Type>{
	
	protected final int _mySize;
	
	protected List<Type> _myBuffer;
	
	protected int _myIndex = 0;
	
	protected Type _myValue;
	
	public CCBuffer(final int theSize){
		_mySize = theSize;
	}
	
	public Type update(final Type theValue){
		if(_myBuffer == null){
			_myBuffer = new ArrayList<Type>();
			for(int i = 0; i < _mySize;i++){
				_myBuffer.add(theValue);
			}
			return theValue;
		}
		_myBuffer.set(_myIndex, theValue);
		_myIndex++;
		_myIndex %= _mySize;
		_myValue = calculate();
		return _myValue;
	}
	
	public Type value(){
		return _myValue;
	}
	
	protected abstract Type calculate();
	
	public static CCBuffer<Float> floatBuffer(final int theSize){
		return new CCFloatBuffer(theSize);
	}
	
	private static class CCFloatBuffer extends CCBuffer<Float>{

		public CCFloatBuffer(final int theSize) {
			super(theSize);
			_myValue = 0f;
		}

		@Override
		public Float calculate() {
			float myResult = 0;
			
			for(Float myFloat:_myBuffer){
				myResult += myFloat;
			}
			myResult /= _mySize;
			return myResult;
		}
	}
	
	public static CCBuffer<CCVector3f> vector3fBuffer(final int theSize){
		return new CCVector3fBuffer(theSize, new CCVector3f());
	}
	
	private static class CCVector3fBuffer extends CCBuffer<CCVector3f>{

		public CCVector3fBuffer(final int theSize, final CCVector3f theInit) {
			super(theSize);
			_myValue = new CCVector3f();
		}

		@Override
		public CCVector3f calculate() {
			CCVector3f myResult = new CCVector3f();
			for(CCVector3f myVector:_myBuffer){
				myResult.add(myVector);
			}
			myResult.scale(1f/_mySize);
			return myResult;
		}
		
	}
	
	public static CCBuffer<CCVector2f> vector2fBuffer(final int theSize){
		return new CCVector2fBuffer(theSize);
	}
	
	public static class CCVector2fBuffer extends CCBuffer<CCVector2f>{

		public CCVector2fBuffer(final int theSize) {
			super(theSize);
			_myValue = new CCVector2f();
		}

		@Override
		public CCVector2f calculate() {
			CCVector2f myResult = new CCVector2f();
			for(CCVector2f myVector:_myBuffer){
				myResult.add(myVector);
			}
			myResult.scale(1f/_mySize);
			return myResult;
		}
		
	}
}
