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
public interface CCOpenNIGestureListener {
	/**
	 * Callback for the recognition of a gesture
	 * @param theGesture The gesture that was recognized.
	 * @param theIdPosition The position in which the gesture was identified.
	 * @param theEndPosition The position of the hand that performed the gesture at the end of the gesture.
	 */
	void onRecognizeGesture(String theGesture, CCVector3f theIdPosition, CCVector3f theEndPosition);

	/**
	 * Callback for indication that a certain gesture is in progress
	 * @param theGesture The gesture that is on its way to being recognized.
	 * @param thePosition The current position of the hand that is performing the gesture.
	 * @param theProgress The percentage of the gesture that was already performed.
	 */
	void onProgressGesture(String theGesture, CCVector3f thePosition, float theProgress);
}
