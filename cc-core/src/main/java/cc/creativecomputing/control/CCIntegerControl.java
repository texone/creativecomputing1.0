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
import cc.creativecomputing.control.connect.CCDirectConnector;
import cc.creativecomputing.control.ui.CCUISlider;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

public class CCIntegerControl extends CCValueControl<Integer>{
	
	public CCIntegerControl(String theName, final Integer theDefault, final int theMin, final int theMax,final float theDefaultWidth, final float theDefaultHeight) {
		super(new CCDirectConnector<Integer>(theName, theDefault, theDefault, theMin, theMax), theDefault, theDefaultWidth, theDefaultHeight, 1);
	}
	
	public CCIntegerControl(CCControlConnector<Integer> theConnector, final Integer theDefault, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefault, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCUISlider<Integer> createUIElement(final float theWidth, final float theHeight) {
		CCUISlider<Integer> myResult = new CCUISlider<Integer>(
			_myName, 
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight),
			(int)_myConnector.min(), 
			(int)_myConnector.max()
		);
		myResult.isInteger(true);
		return myResult;
	}

	@Override
	public Integer defaultValue() {
		if(_myDefault == null)return (int)_myConnector.min();
		return (int)CCMath.constrain(_myDefault, _myConnector.min(), _myConnector.max());
	}
	
	public void min(int theMin) {
		((CCUISlider<Integer>)_myUIElement).min(theMin);
	}
	
	public void max(int theMax) {
		((CCUISlider<Integer>)_myUIElement).max(theMax);
	}

	@Override
	protected Integer stringToValue(String theStringValue) {
		return Integer.parseInt(theStringValue);
	}
}
