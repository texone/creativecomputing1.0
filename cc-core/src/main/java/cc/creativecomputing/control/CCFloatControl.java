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

public class CCFloatControl extends CCValueControl<Float>{

	public CCFloatControl(String theName, final Float theDefault, final float theMin, final float theMax,final float theDefaultWidth, final float theDefaultHeight) {
		super(new CCDirectConnector<Float>(theName, theDefault, theDefault, theMin, theMax), theDefault, theDefaultWidth, theDefaultHeight, 1);
	}
	
	public CCFloatControl(CCControlConnector<Float> theConnector, final Float theDefault, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefault, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public CCUISlider createUIElement(final float theWidth, final float theHeight) {
		return new CCUISlider(
			_myName, 
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight),
			_myConnector.min(), 
			_myConnector.max()
		);
	}

	@Override
	public Float defaultValue() {
		if(_myDefault == null)return _myConnector.min();
		return CCMath.constrain(_myDefault, _myConnector.min(), _myConnector.max());
	}
	
	public void min(float theMin) {
		((CCUISlider<Float>)_myUIElement).min(theMin);
	}
	
	public void max(float theMax) {
		((CCUISlider<Float>)_myUIElement).max(theMax);
	}

	@Override
	protected Float stringToValue(String theStringValue) {
		try{
			return Float.parseFloat(theStringValue);
		}catch ( Exception e){
			e.printStackTrace();
			return 0f;
		}
	}
}
