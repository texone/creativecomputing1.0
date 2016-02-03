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
import cc.creativecomputing.control.timeline.CCTimedData;
import cc.creativecomputing.control.timeline.CCTimedDataUI;
import cc.creativecomputing.math.CCVector2f;

public class CCEnvelopeControl extends CCValueControl<CCTimedData[]>{

	public CCEnvelopeControl(CCControlConnector<CCTimedData[]> theConnector, final float theDefaultWidth, final float theDefaultHeight, final int theNumberOfPresets) {
		super(theConnector, null, theDefaultWidth, theDefaultHeight, theNumberOfPresets);
	}

	@Override
	public CCTimedDataUI createUIElement(final float theWidth, final float theHeight) {
		return new CCTimedDataUI(
			_myName, 
			new CCVector2f(), 
			new CCVector2f(theWidth, theHeight),
			_myConnector.numberOfEnvelopes()
		);
	}

	@Override
	public CCTimedData[] defaultValue() {
		CCTimedData[] myResult = new CCTimedData[_myConnector.numberOfEnvelopes()];
		for(int i = 0; i < myResult.length; i++) {
			myResult[i] = new CCTimedData();
		}
		return myResult;
	}

	@Override
	protected CCTimedData[] stringToValue(String theStringValue) {
		return null;
	}

}
