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
package cc.creativecomputing.control.ui;

import cc.creativecomputing.graphics.CCColor;

public class CCUIColor {

	public CCColor colorBackground = CCColor.createFromInteger(0xff00344D);
	public CCColor colorBackgroundOver = CCColor.createFromInteger(0xff004C73);
	public CCColor colorForeground = CCColor.createFromInteger(0xff006799);
	public CCColor colorForegroundOver = CCColor.createFromInteger(0xff007FBF);
	public CCColor colorActive = CCColor.createFromInteger(0xff0099E5);

	public CCColor colorLabel = CCColor.createFromInteger(0xffffffff);
	public CCColor colorValue = CCColor.createFromInteger(0xffffffff);

	protected void set(CCUIColor theColor) {
		colorBackground = theColor.colorBackground;
		colorBackgroundOver = theColor.colorBackgroundOver;
		colorForeground = theColor.colorForeground;
		colorForegroundOver = theColor.colorForegroundOver;

		colorActive = theColor.colorActive;
		colorLabel = theColor.colorLabel;
		colorValue = theColor.colorValue;
	}

	public CCUIColor() {}

	public CCUIColor(CCUIColor theColor) {
		set(theColor);
	}

	public boolean equals(CCUIColor theColor) {
		if (colorBackground == theColor.colorBackground && colorForeground == theColor.colorForeground && colorActive == theColor.colorActive && colorLabel == theColor.colorLabel
				&& colorValue == theColor.colorValue) {
			return true;
		}
		return false;
	}
}
