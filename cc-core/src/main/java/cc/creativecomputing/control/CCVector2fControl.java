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

import cc.creativecomputing.control.connect.CCControlConnector;
import cc.creativecomputing.control.ui.CCUIChangeListener;
import cc.creativecomputing.control.ui.CCUIField;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;


public class CCVector2fControl extends CCValueControl<CCVector2f> implements CCUIChangeListener{

	
	/**
	 * @param theMyField
	 * @param theMyObject
	 */
	public CCVector2fControl(CCControlConnector<CCVector2f> theConnector, CCVector2f theDefault, final float theElementWidth, final float theElementHeight, final int theNumberOfPresets) {
		super(theConnector, theDefault, theElementWidth, theElementHeight, theNumberOfPresets);
	}

	@Override
	public CCUIField createUIElement(final float theWidth, final float theHeight) {
		return new CCUIField(
			_myName, 
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight),
			new CCVector2f(_myConnector.minX(), _myConnector.minY()),
			new CCVector2f(_myConnector.maxX(), _myConnector.maxY())
		);
	}

	@Override
	public CCVector2f defaultValue() {
		if(_myDefault == null) {
			_myDefault = new CCVector2f();
		}
		
		_myDefault.x = CCMath.constrain(_myDefault.x, _myConnector.minX(), _myConnector.maxX());
		_myDefault.y = CCMath.constrain(_myDefault.y, _myConnector.minY(), _myConnector.maxY());
		
		return _myDefault;
	}

	@Override
	protected CCVector2f stringToValue(String theStringValue) {
		return null;
	}
	
}
