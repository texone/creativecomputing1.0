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

import java.lang.reflect.Method;

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIDropDown;
import cc.creativecomputing.math.CCVector2f;

public class CCEnumControl extends CCValueControl<Enum<?>>{
	
	private Method _myMethod;

	public CCEnumControl(CCControlConnector<Enum<?>> theConnector, final Enum<?> theDefault, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefault, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCUIDropDown createUIElement(final float theWidth, final float theHeight) {
		return new CCUIDropDown(
			_myName, 
			_myConnector.type(),
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight)
		);
	}

	@Override
	protected Enum<?> stringToValue(String theStringValue) {
		try {
			if(_myMethod == null)_myMethod = _myConnector.type().getMethod("valueOf", new Class<?>[] {String.class});
			return (Enum<?>)_myMethod.invoke(null, new Object[] {theStringValue});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
