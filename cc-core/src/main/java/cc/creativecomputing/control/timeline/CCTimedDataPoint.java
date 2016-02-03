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
package cc.creativecomputing.control.timeline;

import cc.creativecomputing.math.CCVector2f;


public class CCTimedDataPoint extends CCVector2f {

	double _myIndex;

	/**
	 * Type of the curve after this point
	 */
	CCTimelineCurveType _myType;
	
	CCTimedDataPoint _myPrevious;
	CCTimedDataPoint _myNext;
	
	public CCTimedDataPoint() {
		this(0, 0, CCTimelineCurveType.LINEAR);
	}

	public CCTimedDataPoint(float theX, float theY) {
		this(theX, theY, CCTimelineCurveType.LINEAR);
	}
	
	public CCTimedDataPoint(float theX, float theY, CCTimelineCurveType theCurveType) {
		super(theX, theY);
		_myType = theCurveType;
		_myIndex = 0;
		_myPrevious = null;
		_myNext = null;
	}

	/**
	 * @return the _myType
	 */
	public CCTimelineCurveType getType() {
		return _myType;
	}

	/**
	 * @param myType the _myType to set
	 */
	public void setType(CCTimelineCurveType myType) {
		_myType = myType;
	}
	
	public CCTimedDataPoint getPrevious() {
		return _myPrevious;
	}
	
	public void setPrevious( CCTimedDataPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		_myPrevious = thePoint;
	}
	
	public CCTimedDataPoint getNext() {
		return _myNext;
	}
	
	public void setNext( CCTimedDataPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		_myNext = thePoint;
	}
	
	public void append( CCTimedDataPoint thePoint) {
		if (thePoint == this) {
			return;
		}
		if (thePoint != null) {
			thePoint.setPrevious(this);
			if (_myNext != null) {
				_myNext.setPrevious(thePoint);
				thePoint.setNext(_myNext);
			}
		}
		_myNext = thePoint;
	}
	
	public void prepend( CCTimedDataPoint thePoint ) {
		if (thePoint == this) {
			return;
		}
		if (thePoint != null) {
			thePoint.setNext(this);
			if (_myPrevious != null) {
				_myPrevious.setNext( thePoint );
				thePoint.setPrevious(_myPrevious);
			}
		}
		_myPrevious = thePoint;
	}
	
	public boolean hasNext() {
		return _myNext != null;
	}
	
	public boolean hasPrevious() {
		return _myPrevious != null;
	}
	
	public boolean isPrevious(CCTimedDataPoint thePoint) {
		if (thePoint.x > x) {
			return true;
		} else if (thePoint.x < x) {
			return false;
		}
		CCTimedDataPoint myCurrent = _myNext;
		while ( myCurrent != null ) {
			if (myCurrent == thePoint) {
				return true;
			}
			myCurrent = myCurrent.getNext();
		}
		return false;
	}
	
	public boolean isNext(CCTimedDataPoint thePoint) {
		if (thePoint.x < x) {
			return true;
		} else if (thePoint.x > x) {
			return false;
		}
		CCTimedDataPoint myCurrent = _myPrevious;
		while( myCurrent != null ) {
			if (myCurrent == thePoint) {
				return true;
			}
			myCurrent = myCurrent.getPrevious();
		}
		return false;
	}
	
	public void cutLoose() {
		_myNext = null;
		_myPrevious = null;
	}
	
	public CCTimedDataPoint clone() {
		CCTimedDataPoint myCopy = new CCTimedDataPoint(x, y);
		myCopy.setType(getType());
		return myCopy;
	}

}
