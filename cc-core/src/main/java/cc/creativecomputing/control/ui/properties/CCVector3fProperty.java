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

import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVecMath;

public class CCVector3fProperty extends CCProperty<CCVector3f> {

    public CCVector3fProperty(CCVector3f theVector) {
        super(theVector);
    }

    @Override
    public void value(CCVector3f theVector) {
        _myValue.set(theVector);
        notifyChangeListeners();
    }

    @Override
    public void blend(float theBlend, CCVector3f theStart, CCVector3f theEnd) {
        value(CCVecMath.blend(theBlend,theStart, theEnd));
    }

}
