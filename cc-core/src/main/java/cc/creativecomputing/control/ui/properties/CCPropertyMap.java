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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.graphics.CCColor;

import cc.creativecomputing.math.CCVector3f;

public class CCPropertyMap {

    Map<String,CCProperty<?>> _myPropertyMap;
    private String _myName = "";

    public CCPropertyMap() {
        _myPropertyMap = new HashMap<String,CCProperty<?>>();
    }

    public void setProperty(String theId, CCProperty<?> theProperty) {
        _myPropertyMap.put(theId, theProperty);
        theProperty.name(_myName + "." + theId);
    }

    public Collection<String> getPropertyNames() {
        return _myPropertyMap.keySet();
    }

    @SuppressWarnings("unchecked")
	public <ValueType> CCProperty<ValueType> getProperty(String theId, Class<ValueType> theType) {
        if (!_myPropertyMap.containsKey(theId)) {
            return null;
        }
        CCProperty<?> myProperty = _myPropertyMap.get(theId);
        if (myProperty.valueType().equals(theType)) {
            return (CCProperty<ValueType>)myProperty;
        } else {
            return null;
        }
    }

    public CCProperty<Float> getFloatProperty(String theId) {
        return getProperty(theId, Float.class);
    }

    public CCProperty<CCVector3f> getVector3fProperty(String theId) {
        return getProperty(theId, CCVector3f.class);
    }

    public CCProperty<CCColor> getColorProperty(String theId) {
        return getProperty(theId, CCColor.class);
    }

    public int size() {
        return _myPropertyMap.size();
    }

    public void name(String theName) {
        _myName = theName;
    }

    public String name() {
        return _myName;
    }

}
