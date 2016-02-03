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
package cc.creativecomputing.cv.openni;

import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
public interface CCOpenNIHandListener {
	/**
	 * Called when a new hand is created
	 * @param theID hand id
	 * @param thePosition hand position
	 * @param theTime time of call
	 */
	public void onCreateHands(int theID, CCVector3f thePosition, float theTime);

	/**
	 * Called when an existing hand is in a new position.
	 * @param theID hand id
	 * @param thePosition hand position
	 * @param theTime time of call
	 */
	public void onUpdateHands(int theID, CCVector3f thePosition, float theTime);

	/**
	 * Called when an existing hand disappears
	 * @param theID hand id
	 * @param theTime time of call
	 */
	public void onDestroyHands(int theID, float theTime);
}
