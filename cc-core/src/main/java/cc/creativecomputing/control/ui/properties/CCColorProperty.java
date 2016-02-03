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
package cc.creativecomputing.control.ui.properties;

import cc.creativecomputing.graphics.CCColor;

public class CCColorProperty extends CCProperty<CCColor> {

    public CCColorProperty() {
        super();
        _myValue = new CCColor(1f);
    }

	@Override
	public void blend(float theBlend, CCColor theStart, CCColor theEnd) {
        value(CCColor.blend(theStart, theEnd, theBlend));
	}
    

}
