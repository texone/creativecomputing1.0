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

import java.util.ArrayList;

import cc.creativecomputing.math.CCVector2f;

public class CCZoomControl implements CCZoomable {
	
	private float _myLowerBound;
	private float _myUpperBound;
	private CCVector2f _myDragStart;
	private float _myLastV;
	private float _myLastH;
	
	ArrayList<CCZoomable> _myZoomables;
	
	public CCZoomControl() {
		_myZoomables = new ArrayList<CCZoomable>();
		_myLowerBound = 0;
		_myUpperBound = CCTimedDataUI.DEFAULT_RANGE;
	}
	
	public void addZoomable(CCZoomable theZoomable) {
		_myZoomables.add(theZoomable);
		theZoomable.setRange(_myLowerBound, _myUpperBound);
	}
	
	public void removeZoomable(CCZoomable theZoomable) {
		_myZoomables.remove(theZoomable);
	}
	
	public void startDrag(CCVector2f theViewCoords ) {
		_myDragStart = theViewCoords;
	}
	
	public void endDrag() {
		_myDragStart = null;
		_myLastV = 0;
		_myLastH = 0;
	}
	
	public void performDrag( CCVector2f theViewCoords, float theViewWidth ) {
		if (_myDragStart != null) {
			
			float myVMovement = theViewCoords.y - _myDragStart.y;
			float myHMovement = theViewCoords.x - _myDragStart.x;
			
			float myVDelta = myVMovement - _myLastV;
			float myHDelta = myHMovement - _myLastH;
			
			myVDelta *= 0.01 * (_myUpperBound - _myLowerBound);
			myHDelta = myHDelta / theViewWidth * (_myUpperBound - _myLowerBound);
			
			// zooming should occur around the point where you grab the time line
			float myFixPoint = theViewCoords.x / theViewWidth;
			
			_myLowerBound -= myVDelta * myFixPoint;
			if (_myLowerBound < 0) {
				_myLowerBound = 0;
			}
			_myUpperBound += myVDelta * (1-myFixPoint);
			_myLowerBound -= myHDelta;
			if (_myLowerBound < 0) {
				_myLowerBound = 0;
			} else {
				_myUpperBound -= myHDelta;
			}
			
			_myLastV = myVMovement;
			_myLastH = myHMovement;
			
			updateZoomables();
			
		}
	}
	
	public void setRange(float theLowerBound, float theUpperBound) {
		_myLowerBound = theLowerBound;
		_myUpperBound = theUpperBound;
		updateZoomables();
	}
	
	private void updateZoomables() {
		if (_myLowerBound < _myUpperBound) {
			for (CCZoomable myZoomable : _myZoomables) {
				myZoomable.setRange(_myLowerBound, _myUpperBound);
			}
		}		
	}
	
	public void setLowerBound(float theLowerBound) {
		_myLowerBound = theLowerBound;
		updateZoomables();
	}
	
	public void setUpperBound(float theUpperBound) {
		_myUpperBound = theUpperBound;
		updateZoomables();
	}
	
	public float getLowerBound() {
		return _myLowerBound;
	}
	
	public float getUpperBound() {
		return _myUpperBound;
	}
}
