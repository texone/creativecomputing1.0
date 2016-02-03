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


/**
 * @author christianriekoff
 *
 */
public interface CCExternalController {

	public<ValueType> void addControl(final String theTabName, final String theObjectID, CCValueControl<ValueType> theControl);

//	public void addControl(final String theTabName, final String theObjectID, CCUIValueControl theControl);
	
}
