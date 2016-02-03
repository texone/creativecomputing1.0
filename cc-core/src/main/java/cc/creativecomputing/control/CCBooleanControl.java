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
import cc.creativecomputing.control.ui.CCUIButton;
import cc.creativecomputing.math.CCVector2f;

public class CCBooleanControl extends CCValueControl<Boolean>{

	public CCBooleanControl(CCControlConnector<Boolean> theConnector, final Boolean theDefault, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, theDefault, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCUIButton createUIElement(final float theWidth, final float theHeight) {
		return new CCUIButton(
			_myName, 
			_myConnector.toggle(),
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight)
		);
	}

	@Override
	public Boolean defaultValue() {
		return false;
	}

	@Override
	protected Boolean stringToValue(String theStringValue) {
		return Boolean.parseBoolean(theStringValue);
	}

}
