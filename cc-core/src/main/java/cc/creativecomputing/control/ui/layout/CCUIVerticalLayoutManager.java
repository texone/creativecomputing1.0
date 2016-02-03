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
package cc.creativecomputing.control.ui.layout;

import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;

/**
 * This layout manager places the added elements in a vertical row
 * @author christian riekoff
 *
 */
public class CCUIVerticalLayoutManager extends CCUILayoutManager{
	
	private float _mySliderOffset = 0;
	private float _mySpace;

	/**
	 * @param theComponent
	 */
	public CCUIVerticalLayoutManager(CCUIComponent theComponent, final float theSpace) {
		super(theComponent);
		_mySpace = theSpace;
	}
	
	@Override
	public void layout(CCUIElement theElement) {
		theElement.position().y = _mySliderOffset;
		theElement.setupText();
		_mySliderOffset += _mySpace + theElement.bounds().height();
	};
	
	public void reset(){
		_mySliderOffset = 0;
	}

}
