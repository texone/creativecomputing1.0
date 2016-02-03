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
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;


public class CCTimedData {
	
	private static class TimelineComparator implements Comparator<CCTimedDataPoint> {
		public int compare( CCTimedDataPoint p1, CCTimedDataPoint p2 ) {
			if (p1.x < p2.x) {
				return -1;
			} else if (p1.x == p2.x) {
				return 0;
			}
			return 1;
		}
	}
	
	TreeSet<CCTimedDataPoint> _myTreeSet;
	
	private int _mySize = 0;
	private boolean _myDirtyFlag = false;
	
	public float tension = 0;
	public float continuity = 0;
	public float bias = 0;
	
	private float _myTime = 0;
	
	public CCTimedData() {
		_myTreeSet = new TreeSet<CCTimedDataPoint>(new TimelineComparator());
	}
	
	private float adaptiveTension(CCVector2f p1, CCVector2f p2, float theTension) {
	   	float myDistance = Math.abs(p2.x - p1.x);
    	if (myDistance < 4.0) {
    		return (1 - myDistance/4f) + theTension;
    	}	
    	return theTension;
	}
	
	/**
	 * @param thePoint
	 */
	public void add(CCTimedDataPoint thePoint) {
		if (_myTreeSet.contains(thePoint)) {
			CCTimedDataPoint myTreeLeaf = _myTreeSet.floor(thePoint);
			if (myTreeLeaf.equals(thePoint)) {
				return;
			}
			if (thePoint.isPrevious(myTreeLeaf)) {
				insertBefore(myTreeLeaf, thePoint);
			} else {
				myTreeLeaf = getLastOnSamePosition(myTreeLeaf);
				// do not insert into the tree, just update the float linked list
				myTreeLeaf.append(thePoint);
			}
		} else { // new leaf
			_myTreeSet.add(thePoint);
			thePoint.cutLoose();
			CCTimedDataPoint myLower = _myTreeSet.lower(thePoint);
			if (myLower != null) {
				myLower = getLastOnSamePosition(myLower);
				myLower.append(thePoint);
			} 
			CCTimedDataPoint myHigher = _myTreeSet.higher(thePoint);
			if (myHigher != null) {
				myHigher.prepend(thePoint);
			}
		}
		_mySize += 1;
		_myDirtyFlag = true;
	}
	
	private CCTimedDataPoint createSamplePoint(float theValue ) {
		return new CCTimedDataPoint(theValue, 0);
	}
	
	public CCTimedDataPoint getFirstPointAt(float theSampleValue ) {
		return _myTreeSet.ceiling(new CCTimedDataPoint( theSampleValue, 0));
	}
	
	private CCTimedDataPoint getLastOnSamePosition( CCTimedDataPoint thePoint ) {
		if (!_myTreeSet.contains(thePoint)) {
			return thePoint;
		} else {
			CCTimedDataPoint myTreeLeaf = _myTreeSet.floor(thePoint);
			while (myTreeLeaf.hasNext()) {
				if (myTreeLeaf.getNext().x != thePoint.x) {
					return myTreeLeaf;
				} else {
					myTreeLeaf = myTreeLeaf.getNext();
				}
			}
			return myTreeLeaf;
		}
	}
	
	public CCTimedDataPoint getLastPointAt( float theSampleValue ) {
		return getLastOnSamePosition(_myTreeSet.ceiling(new CCTimedDataPoint( theSampleValue, 0)));
	}
	
	public CCTimedDataPoint getLastPoint() {
		try {
			CCTimedDataPoint myLast = _myTreeSet.last();
			return getLastOnSamePosition(myLast);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	public CCTimedDataPoint getFirstPoint() {
		try {
			CCTimedDataPoint myLast = _myTreeSet.first();
			return getLastOnSamePosition(myLast);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	private float getLinearValue(float theTime) {
		
		if (_myTreeSet.size() == 0) {
			return 0;
		}
		
		CCTimedDataPoint mySample = createSamplePoint(theTime);
		CCTimedDataPoint myLower = _myTreeSet.lower(mySample);
		if (myLower != null) {
			myLower = getLastOnSamePosition(myLower);
		}
		CCTimedDataPoint myHigher = _myTreeSet.ceiling(mySample);
		
		if (_myTreeSet.contains(mySample)) {
			return _myTreeSet.tailSet(mySample, true).first().y;
		}
		
		if (myLower == null) {
			return myHigher.y;
		} else if (myHigher == null) {
			return myLower.y;
		}
		
		return myLower.y + (myHigher.y - myLower.y)/(myHigher.x - myLower.x) 
				         		* (theTime - myLower.x);
		
	}
	
	/**
	 * Find the nearest point within a given range on the x-axis
	 * @param theCoords the coordinates to compare with
	 * @param theRange the range on the x-axis. thus find points within -range/2 to range/2 of the sample coordinates
	 * @return the nearest point within that range (euclidean distance)
	 */
	public CCTimedDataPoint getNearestPoint( CCVector2f theCoords, float theRange ) {
		
		CCTimedDataPoint mySampleCoords = new CCTimedDataPoint(theCoords.x, theCoords.y);
		
		CCTimedDataPoint myLowerPoint = _myTreeSet.ceiling(new CCTimedDataPoint(theCoords.x-theRange, 0));
		
		if (myLowerPoint == null) {
			return null;
		}
		float myMinDistance = myLowerPoint.distance(mySampleCoords);
		CCTimedDataPoint myNearestPoint = myLowerPoint;
		while( myLowerPoint.hasNext() && myLowerPoint.x <= theCoords.x+theRange) {
			myLowerPoint = myLowerPoint.getNext();
			float myDistance = myLowerPoint.distance(mySampleCoords);
			if (myDistance < myMinDistance) {
				myMinDistance = myDistance;
				myNearestPoint = myLowerPoint;
			}
		}
		return myNearestPoint;
	}
	
	private float getSplineValue(float theTime) {
		
		if (_myTreeSet.size() == 0) {
			return 0;
		}
		
		CCTimedDataPoint mySample = createSamplePoint(theTime);
		SortedSet<CCTimedDataPoint> myHeadSet = _myTreeSet.headSet(mySample, false);
		
		CCTimedDataPoint p1 = null;
		CCTimedDataPoint p2 = null;
		CCTimedDataPoint p3 = null;
		CCTimedDataPoint p4 = null;
		
		if (myHeadSet.size() != 0) {
			p2 = getLastOnSamePosition(myHeadSet.last());
		} 
		if (p2 != null && p2.hasPrevious()) {
			p1 = p2.getPrevious();
		}
		if (p2.hasNext()) {
			p3 = p2.getNext();
		}
		if (p3 != null && p3.hasNext()) {
			p4 = p3.getNext();
		}
		
		CCVector2f myResult = sampleCurveSegment(p1, p2, p3, p4, tension, continuity, bias, mySample);
		return myResult.y;
		
	}
	
	public void time(final float theTime) {
		_myTime = theTime;
	}
	
	public float time() {
		return _myTime;
	}
	
	public float value() {
		return value(_myTime);
	}
	
	public float value(final float theTime) {
		CCTimedDataPoint mySample = createSamplePoint(theTime);
		CCTimedDataPoint myLower = _myTreeSet.lower(mySample);
		CCTimedDataPoint myCeiling = _myTreeSet.ceiling(mySample);
		
		if (myLower == null) {
			if (myCeiling != null) {
				return myCeiling.y;
			}
			return 0;
		}
		
		myLower = getLastOnSamePosition(myLower);
		
		if (myCeiling != null) {
			if (myCeiling.getType().equals(CCTimelineCurveType.LINEAR)) {
				return getLinearValue(theTime);
			} else if (myCeiling.getType().equals(CCTimelineCurveType.CUBIC)) {
				return getSplineValue(theTime);
			}
		} else {
			if (myLower != null) {
				return myLower.y;
			}
		}
		return 0;
	}

	
	private void insertBefore(CCTimedDataPoint theLocation, CCTimedDataPoint theInsertion) {
		theLocation.prepend(theInsertion);
		// the first element in the float linked list at the same x position
		// becomes the new tree leaf
		if (theLocation.x == theInsertion.x) {
			_myTreeSet.remove(theLocation);
			_myTreeSet.add(theInsertion);
		}
	}
	
	public boolean isEmpty() {
		return _myTreeSet.isEmpty();
	}
	
	public ArrayList<CCTimedDataPoint> rangeList( float theMinValue, float theMaxValue ) {
		ArrayList<CCTimedDataPoint> myRange = new ArrayList<CCTimedDataPoint>();
		CCTimedDataPoint myMinPoint = _myTreeSet.ceiling(new CCTimedDataPoint(theMinValue, 0));
		
		if (myMinPoint == null || myMinPoint.x > theMaxValue) {
			return myRange;
		}
		CCTimedDataPoint myMaxPoint = _myTreeSet.floor(new CCTimedDataPoint(theMaxValue, 0));
		
		myMaxPoint = getLastOnSamePosition(myMaxPoint);
		CCTimedDataPoint myCurrentPoint = myMinPoint;
		
		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			myRange.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.getNext();
		}
		myRange.add(myMaxPoint);
		return myRange;
	}
	
	public ArrayList<CCTimedDataPoint> copyRange( float theMinValue, float theMaxValue ) {
		ArrayList<CCTimedDataPoint> myRange = rangeList( theMinValue, theMaxValue );
		ArrayList<CCTimedDataPoint> myCopy = new ArrayList<CCTimedDataPoint>();
		Iterator<CCTimedDataPoint> it = myRange.iterator();
		
		CCTimedDataPoint myControlPoint = null;
		while (it.hasNext()) {
			CCTimedDataPoint myNext = it.next().clone();
			myNext.cutLoose();
			if (myControlPoint != null) {
				myControlPoint.setNext(myNext);
				myNext.setPrevious(myControlPoint);
			}
			myCopy.add(myNext);
			myControlPoint = myNext;
		}
		
		return myCopy;
		
	}
	
	public ArrayList<CCTimedDataPoint> rangeList( float theMinValue ) {
		ArrayList<CCTimedDataPoint> myRange = new ArrayList<CCTimedDataPoint>();
		CCTimedDataPoint myMinPoint = _myTreeSet.ceiling(new CCTimedDataPoint(theMinValue, 0));
		
		if (myMinPoint == null ) {
			return myRange;
		}
		
		CCTimedDataPoint myCurrentPoint = myMinPoint;
		
		while (myCurrentPoint != null) {
			myRange.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.getNext();
		}
		return myRange;		
	}

	
	public void remove(CCTimedDataPoint thePoint) {
	
		if (_myTreeSet.contains(thePoint)) {   // we have one ore more points at the location
			CCTimedDataPoint myTreeLeaf = _myTreeSet.floor(thePoint); // get the tree leaf
			if (myTreeLeaf == thePoint) {      // we are removing the leaf
				if (myTreeLeaf.hasNext()) {    // maybe add a new leaf
					_myTreeSet.remove(myTreeLeaf);
					_myTreeSet.add(myTreeLeaf.getNext()); // this will replace the old leaf
				} else {
					_myTreeSet.remove(myTreeLeaf);
				}
			} 
		}
		// update float linked list
		if (thePoint.hasPrevious()) {
			thePoint.getPrevious().setNext(thePoint.getNext());	
		}
		if (thePoint.hasNext()) {
			thePoint.getNext().setPrevious(thePoint.getPrevious());
		}
		_mySize -= 1;
		_myDirtyFlag = true;
		assert(_mySize>=0);
	}
	
	public void move(CCTimedDataPoint thePoint, CCVector2f theTargetLocation) {
		boolean rebuild_search_tree = thePoint.x != theTargetLocation.x;
		if (rebuild_search_tree) {
			remove(thePoint);
		}
		thePoint.set(theTargetLocation);
		if (rebuild_search_tree) {
			add(thePoint);
		}
	}

	public void removeAll( float theMinValue, float theMaxValue ) {
		ArrayList<CCTimedDataPoint> myRange = rangeList(theMinValue, theMaxValue);
		if (myRange.size() == 0) {
			return;
		}
		// connect the point before 
		CCTimedDataPoint myLower = _myTreeSet.lower(myRange.get(0));
		CCTimedDataPoint myHigher = _myTreeSet.higher(myRange.get(myRange.size()-1));
		if (myLower != null) {
			myLower = getLastOnSamePosition(myLower);
			myLower.setNext(myHigher);
			if (myHigher != null) {
				myHigher.setPrevious(myLower);
			}
		} else if (myHigher != null) {
			myHigher.setPrevious(null);
		} 
		for (CCTimedDataPoint myCurrentPoint : myRange) {
			remove(myCurrentPoint);
		}
	}
	
	public void replaceAll( float theKey, ArrayList<CCTimedDataPoint> theArray ) {
		if (theArray.size() == 0) {
			return;
		}
		float myRange = theArray.get(theArray.size()-1).x - theArray.get(0).x;
		removeAll(theKey, theKey + myRange);
		for (CCTimedDataPoint myPoint : theArray) {
			myPoint.cutLoose();
			CCTimedDataPoint myClone = myPoint.clone();
			myClone.set(myPoint.x + theKey, myPoint.y);
			add(myClone);
		}
	}
	
	public void insertAll( float theKey, ArrayList<CCTimedDataPoint> theArray ) {
		if (theArray.size() == 0) {
			return;
		}
		float myRange = theArray.get(theArray.size()-1).x - theArray.get(0).x;
		CCTimedDataPoint myCurrentPoint = _myTreeSet.ceiling(new CCTimedDataPoint(theKey, 0));
		ArrayList<CCTimedDataPoint> myTmpList = new ArrayList<CCTimedDataPoint>();
		while (myCurrentPoint != null) {
			CCTimedDataPoint myPoint = myCurrentPoint.clone();
			myPoint.set(myCurrentPoint.x+myRange-theKey, myCurrentPoint.y);
			myTmpList.add(myPoint);
			myCurrentPoint = myCurrentPoint.getNext();
		}
		replaceAll( theKey, myTmpList );
		replaceAll( theKey, theArray );
	}
	
	public void cutRange( float theMinValue, float theMaxValue ) {
		removeAll(theMinValue, theMaxValue);
		float myRange = theMaxValue - theMinValue;
		CCTimedDataPoint myCurrentPoint = _myTreeSet.ceiling(new CCTimedDataPoint(theMaxValue,0));
		while (myCurrentPoint != null) {
			this.move(myCurrentPoint, new CCVector2f(myCurrentPoint.x-myRange, myCurrentPoint.y));
			myCurrentPoint = myCurrentPoint.getNext();
		}
	}

	private CCVector2f sampleCurveSegment(CCVector2f p1, CCVector2f p2, CCVector2f p3, CCVector2f p4, 
									  float t, float c, float b, CCVector2f theSamplePoint) 
	{
		if (p1 == null) {
			p1 = p2;
		}
		if (p2 == null) {
			return p3;
		}
		if (p3 == null) {
			if (p2 != null) {
				p3 = p2;
				if (p4 == null) {
					return p2;
				}
			}
		}
		if (p4 == null) {
			p4 = p3;
		}
		
		float s = (theSamplePoint.x - p2.x)/(p3.x - p2.x);
		
    	t = adaptiveTension(p2, p3, t);
   
        float h1 = 2*CCMath.pow(s, 3) - 3*CCMath.pow(s, 2) + 1;
        float h2 = (-2)*CCMath.pow(s, 3) + 3*CCMath.pow(s, 2);
        float h3 = CCMath.pow(s, 3) - 2*CCMath.pow(s, 2) + s;
        float h4 = CCMath.pow(s, 3) - CCMath.pow(s, 2);

        float TDix = (1-t) * (1+c) * (1+b) * (p2.x - p1.x) / 2f + (1-t) * (1-c) * (1-b) * (p3.x - p2.x) / 2f;
        float TDiy = (1-t) * (1+c) * (1+b) * (p2.y - p1.y) / 2f + (1-t) * (1-c) * (1-b) * (p3.y - p2.y) / 2f;
  
        float TSix = (1-t) * (1-c) * (1+b) * (p3.x - p2.x) / 2f + (1-t) * (1+c) * (1-b) * (p4.x - p3.x) / 2f;
        float TSiy = (1-t) * (1-c) * (1+b) * (p3.y - p2.y) / 2f + (1-t) * (1+c) * (1-b) * (p4.y - p3.y) / 2f;
  
        float myX = h1*p2.x + h2*p3.x + h3*TDix + h4*TSix;
        float myY = h1*p2.y + h2*p3.y + h3*TDiy + h4*TSiy;
        
        return new CCVector2f(myX, myY);
        
	}

	public int size() {
		return _mySize;
	}
	
	public boolean isLeaf(CCTimedDataPoint thePoint) {
		if (_myTreeSet.contains(thePoint)) {
			CCTimedDataPoint myTreeLeaf = _myTreeSet.floor(thePoint);
			if (myTreeLeaf.equals(thePoint)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isDirty() {
		return _myDirtyFlag;
	}
	
	public void setDirty(boolean theFlag) {
		_myDirtyFlag = theFlag;
	}
	
	public CCTimedData clone() {
		CCTimedData myClone = new CCTimedData();
		CCTimedDataPoint myPoint = getFirstPoint();
		CCTimedDataPoint myClonedPoint = myPoint.clone();
		myClonedPoint.setPrevious(null);
		myClone.add(myClonedPoint);
		while (myPoint.hasNext()) {
			myPoint = myPoint.getNext();
			CCTimedDataPoint myNextPoint = myPoint.clone();
			myClonedPoint.append(myNextPoint);
			myClone.add(myNextPoint);
			myClonedPoint = myNextPoint;
		}
		myClonedPoint.setNext(null);
		return myClone;
	}

}
