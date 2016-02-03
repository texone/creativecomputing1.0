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

import cc.creativecomputing.math.CCMath;

public class CCFloatProperty extends CCProperty<Float> {

    public CCFloatProperty() {
        super();
        _myValue = 0.0f;
    }
    
    public CCFloatProperty(Float theValue) {
    	super(theValue);
    }

    @Override
    public void blend(float theBlend, Float theStart, Float theEnd) {
        value(CCMath.blend(theStart, theEnd, theBlend));
    }

}
