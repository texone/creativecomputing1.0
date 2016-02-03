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

import cc.creativecomputing.math.CCQuaternion;

public class CCQuaternionProperty extends CCProperty<CCQuaternion> {
    
    public CCQuaternionProperty(CCQuaternion theQuaternion) {
        super(theQuaternion);
    }

    @Override
    public void value(CCQuaternion theQuaternion) {
        _myValue.set(theQuaternion);
        notifyChangeListeners();
    }

	@Override
	public void blend(float theBlend, CCQuaternion theStart, CCQuaternion theEnd) {
        // TODO: implement slerp
	}

}
