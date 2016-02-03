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

import java.util.Arrays;

import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;

/**
 * @author christianriekoff
 *
 */
public class CCUIColumnLayoutManager extends CCUILayoutManager{
	
	private float[]_myHeights = new float[100];
	private float _myWidth;
	private float _mySpace;
	private float _myXOffset;
	private float _myYOffset;

	/**
	 * @param theComponent
	 */
	public CCUIColumnLayoutManager(CCUIComponent theComponent, final float theWidth, final float theSpace, final float theXOffset, final float theYOffset) {
		super(theComponent);
		_myWidth = theWidth;
		_mySpace = theSpace;
		_myXOffset = theXOffset;
		_myYOffset = theYOffset;
		
		yOffset(theYOffset);
	}
	
	public void yOffset(final float theYOffset) {
		_myYOffset = theYOffset;
		Arrays.fill(_myHeights, _myYOffset);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.layout.CCUILayoutManager#layout(cc.creativecomputing.control.CCUIElement)
	 */
	@Override
	public void layout(CCUIElement theElement) {
		theElement.position().set(theElement.column() * _myWidth + _myXOffset, _myHeights[theElement.column()]);
		theElement.setupText();
		_myHeights[theElement.column()] += theElement.bounds().height() + _mySpace;
	}
	
	@Override
	public void reset() {
		Arrays.fill(_myHeights, _myYOffset);
	}
}
