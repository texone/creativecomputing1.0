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

import java.util.ArrayList;
import java.util.List;


abstract public class CCProperty<ValueType extends Object> {

    protected ValueType _myValue;
    private List<CCPropertyChangeListener<ValueType>> _myChangeListeners;
    private String _myName;

    public CCProperty() {
        _myChangeListeners = new ArrayList<CCPropertyChangeListener<ValueType>>();
    }

    public CCProperty(ValueType theValue) {
        _myChangeListeners = new ArrayList<CCPropertyChangeListener<ValueType>>();
        _myValue = theValue;
    }

    public final ValueType value() {
        return _myValue;
    }

    public void value(ValueType theValue) {
        _myValue = theValue;
        notifyChangeListeners();
    }

    public Class<?> valueType() {
        return _myValue.getClass();
    }

    public void addChangeListener(CCPropertyChangeListener<ValueType> theChangeListener) {
        _myChangeListeners.add(theChangeListener);
    }

    public void removeChangeListener(CCPropertyChangeListener<ValueType> theChangeListener) {
        _myChangeListeners.remove(theChangeListener);
    }

    protected void notifyChangeListeners() {
        for (CCPropertyChangeListener<ValueType> myListener : _myChangeListeners) {
            myListener.onChange(_myValue);
        }
    }

    public void name(String theName) {
        _myName = theName;
    }

    public String name() {
        return _myName;
    }

    public abstract void blend(float theBlend, ValueType theStart, ValueType theEnd);

}
