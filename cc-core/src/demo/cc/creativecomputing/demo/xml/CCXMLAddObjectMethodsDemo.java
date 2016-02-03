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
package cc.creativecomputing.demo.xml;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLObjectSerializer;

/**
 * 
 * @author christianriekoff
 *
 */
public class CCXMLAddObjectMethodsDemo extends CCApp {
	
	public static enum TestEnum{
		CONST1, CONST2
	}
	
	@CCPropertyObject(name="type1")
	public static class Type1{
		
		private float _myValue1;
		private float _myValue2;
		private boolean _myValue3;
		private TestEnum _myValue4;
		
		public Type1(float theValue1, float theValue2, boolean theValue3, TestEnum theValue4) {
			_myValue1 = theValue1;
			_myValue2 = theValue2;
			_myValue3 = theValue3;
			_myValue4 = theValue4;
		}
		
		@CCProperty(name = "t1_value1")
		public float value1() {
			return _myValue1;
		}
		
		@CCProperty(name = "t1_value1")
		public void value1(float theValue1) {
			_myValue1 = theValue1;
		}
		
		@CCProperty(name = "t1_value2", node=false)
		public float value2() {
			return _myValue2;
		}
		
		@CCProperty(name = "t1_value2", node=false)
		public void value2(float theValue2) {
			_myValue2 = theValue2;
		}
		
		@CCProperty(name = "t1_value3")
		public boolean value3() {
			return _myValue3;
		}
		
		@CCProperty(name = "t1_value3")
		public void value3(boolean theValue3) {
			_myValue3 = theValue3;
		}
		
		@CCProperty(name = "t1_value4")
		public TestEnum value4() {
			return _myValue4;
		}
		
		@CCProperty(name = "t1_value4")
		public void value4(TestEnum theValue4) {
			_myValue4 = theValue4;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TYPE1:" + 
			"\nvalue1:" + _myValue1 + 
			"\nvalue2:" + _myValue2 + 
			"\nvalue3:" + _myValue3 + 
			"\nvalue4:" + _myValue4;
		}
	}

	@CCPropertyObject(name="type2")
	public static class Type2{
		
		
		Type1 _myType1;
		
		public Type2(Type1 theType1) {
			_myType1 = theType1;
		}
		
		@CCProperty(name = "t2_value1")
		public Type1 value1() {
			return _myType1;
		}
		
		@CCProperty(name = "t2_value1")
		public void value1(Type1 theValue1) {
			_myType1 = theValue1;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TYPE2:" + 
			"\nvalue1:" + _myType1;
		}
	}

	
	
	public static void main(String[] args) {
		Type1 myObject1 = new Type1(2.3f,4.3f, true, TestEnum.CONST2);
		
		CCXMLObjectSerializer mySerializer = new CCXMLObjectSerializer();
		
		CCXMLElement myElement = new CCXMLElement("test");
		mySerializer.addChild(myElement, myObject1);
		
		Type2 myObject2 = new Type2(myObject1);
		mySerializer.addChild(myElement, myObject2);
		myElement.print();
		
		Type1 myReadObject1 = mySerializer.toObject(myElement.child("type1"),Type1.class);
		CCLog.info(myReadObject1);
		
		Type2 myReadObject2 = mySerializer.toObject(myElement.child("type2"),Type2.class);
		CCLog.info(myReadObject2);
	}
}

