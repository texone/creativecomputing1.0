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
package cc.creativecomputing.util;


public class CCTuple<Type1, Type2> {

	private Type1 _myFirst;
	private Type2 _mySecond;
	
	
	
	public CCTuple(Type1 theFirst, Type2 theSecond) {
		super();
		_myFirst = theFirst;
		_mySecond = theSecond;
	}

	public Type1 first(){
		return _myFirst;
	}
	
	public Type2 second() {
		return _mySecond;
	}
}
