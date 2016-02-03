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
package cc.creativecomputing.protocol.proxymatrix;

import cc.creativecomputing.math.CCVector2f;

public class CCTouch {

	private CCVector2f _myPosition;
	private CCVector2f _myRelativePosition;
	private CCVector2f _myMotion;
	
	private boolean _myIsCorrelated = false;
	private int _myCorrelatedID;

	public CCTouch() {
		_myPosition = new CCVector2f();
		_myRelativePosition = new CCVector2f();
		_myMotion = new CCVector2f();
		_myCorrelatedID = -1;

	}
	
	public CCTouch(CCVector2f thePosition, CCVector2f theRelativePosition) {

		this();
		_myPosition = thePosition;
		_myRelativePosition = theRelativePosition;
	}

	/**
	 * @param thePosition the position to set
	 */
	void position(CCVector2f thePosition) {
		_myPosition = thePosition;
	}

	/**
	 * @return the position
	 */
	public CCVector2f position() {
		return _myPosition;
	}
	
	CCVector2f relativePosition() {
		return _myRelativePosition;
	}

	/**
	 * @param theIsCorrelated the isCorrelated to set
	 */
	void isCorrelated(boolean theIsCorrelated) {
		_myIsCorrelated = theIsCorrelated;
	}

	/**
	 * @return the isCorrelated
	 */
	boolean isCorrelated() {
		return _myIsCorrelated;
	}

	/**
	 * @param theMotion the motion to set
	 */
	void motion(CCVector2f theMotion) {
		_myMotion = theMotion;
	}

	/**
	 * @return the motion
	 */
	public CCVector2f motion() {
		return _myMotion;
	}

	/**
	 * @param correlatedID the correlatedID to set
	 */
	void id(int theCorrelatedID) {
		_myCorrelatedID = theCorrelatedID;
	}

	/**
	 * @return the correlatedID
	 */
	public int id() {
		return _myCorrelatedID;
	}

}
