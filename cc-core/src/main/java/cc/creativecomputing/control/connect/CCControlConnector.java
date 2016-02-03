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
package cc.creativecomputing.control.connect;

import cc.creativecomputing.control.ui.CCUIValueElement;

/**
 * @author christianriekoff
 *
 */
public abstract class CCControlConnector <ValueType>{

	public abstract String name();
	
	public abstract ValueType defaultValue();
	
	public abstract void onChange(CCUIValueElement<ValueType> theElement);
	
	public abstract void readBack(CCUIValueElement<ValueType> theElement);
	
	public abstract float min();
	
	public abstract float max();
	
	public abstract float minX();
	
	public abstract float maxX();
	
	public abstract float minY();
	
	public abstract float maxY();
	
	public abstract boolean toggle();
	
	public abstract int numberOfEnvelopes();
	
	public abstract Class<?> type();
	
	public abstract boolean external();
	
	public abstract boolean accumulate();
}
