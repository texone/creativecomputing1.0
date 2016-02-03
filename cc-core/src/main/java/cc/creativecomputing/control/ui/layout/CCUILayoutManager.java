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
 * The layout manager is used in components to layout the user interface elements.
 * In this default implementation the elements are simply placed according to their position.
 * @author christianriekoff
 *
 */
public class CCUILayoutManager {

	protected CCUIComponent _myComponent;
	
	public CCUILayoutManager(CCUIComponent theComponent) {
		_myComponent = theComponent;
	}
	
	public void layout(CCUIElement theElement) {
		
	}
	
	public void reset(){}
}
