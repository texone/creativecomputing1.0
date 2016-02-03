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

import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author christian riekoff
 *
 */
public class CCUITabbedPane extends CCUIComponent{

	public CCUITabbedPane(String theLabel, CCVector2f thePosition, CCVector2f theDimension) {
		super(theLabel, thePosition, theDimension);
	}

	public CCUITabbedPane(String theLabel, float theX, float theY, float theWidth, float theHeight) {
		super(theLabel, theX, theY, theWidth, theHeight);
	}

	
}
