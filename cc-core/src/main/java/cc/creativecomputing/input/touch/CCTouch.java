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
package cc.creativecomputing.input.touch;

import cc.creativecomputing.math.CCVector2f;

public class CCTouch {

	private double _myTimestamp;
	private int _myFrame;
	private int _myId;
	private float _mySize;
	private float _myAngle;
	private float _myMajorAxis;
	private float _myMinorAxis;
	private CCVector2f _myPosition;
	private CCVector2f _myVelocity;

	public CCTouch(int theID) {
		_myId = theID;
		_myPosition = new CCVector2f();
		_myVelocity = new CCVector2f();
	}

	public int id() {
		return _myId;
	}
	
	public void frame(int theFrame) {
		_myFrame = theFrame;
	}

	public int frame() {
		return _myFrame;
	}
	
	public void timeStamp(double theTimeStamp) {
		_myTimestamp = theTimeStamp;
	}

	public double timestamp() {
		return _myTimestamp;
	}
	
	public void size(float theSize) {
		_mySize = theSize;
	}

	public float size() {
		return _mySize;
	}
	
	public void angle(float theAngle) {
		_myAngle = theAngle;
	}

	public float angle() {
		return _myAngle;
	}

	public int getAngle() {
		return (int) ((_myAngle * 90) / Math.atan2(1, 0));
	} // return in Degrees

	public void majorAxis(float theMajorAxis) {
		_myMajorAxis = theMajorAxis;
	}
	
	public float majorAxis() {
		return _myMajorAxis;
	}

	public void minorAxis(float theMinorAxis) {
		_myMinorAxis = theMinorAxis;
	}

	public float minorAxis() {
		return _myMinorAxis;
	}

	public CCVector2f position() {
		return _myPosition;
	}

	public CCVector2f velocity() {
		return _myVelocity;
	}

}
