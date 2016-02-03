/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.timeline.model.points;

import java.util.SortedSet;

import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.timeline.model.util.CubicSolver;
import cc.creativecomputing.xml.CCXMLElement;


/**
 * @author christianriekoff
 * 
 */
public class BezierControlPoint extends ControlPoint {

	private HandleControlPoint _myInHandle;
	private HandleControlPoint _myOutHandle;

	public BezierControlPoint() {
		super(ControlPointType.BEZIER);
	}

	public BezierControlPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.BEZIER);
	}

	public boolean hasHandles() {
		return true;
	}

	public HandleControlPoint inHandle() {
		// TODO make handle adaption work when moving points
//		if(_myPrevious != null && _myPrevious.time() > _myInHandle.time()) {
//			return new HandleControlPoint(this, HandleType.BEZIER_IN_HANDLE, _myPrevious.time(), _myInHandle.value());
//		}
		return _myInHandle;
	}

	public void inHandle(HandleControlPoint theHandle) {
		_myInHandle = theHandle;
	}

	public HandleControlPoint outHandle() {
//		if(_myNext != null && _myNext.time() < _myOutHandle.time()) {
//			return new HandleControlPoint(this, HandleType.BEZIER_OUT_HANDLE, _myNext.time(), _myOutHandle.value());
//		}
		return _myOutHandle;
	}

	public void outHandle(HandleControlPoint theOutHandle) {
		_myOutHandle = theOutHandle;
	}
	
	/**
	 * Returns the bezier blend between 0 and 1 that would result in the given x
	 * @param theTime0 time of the first key
	 * @param theTime1 time of the first control point
	 * @param theTime2 time of the second control point
	 * @param theTime3 time of the second key
	 * @param theTime 
	 * @return bezier blend for the given time
	 */
	private double bezierBlend(double theTime0, double theTime1, double theTime2, double theTime3, double theTime) {
		double a = -theTime0 + 3 * theTime1 - 3 * theTime2 + theTime3;
		double b = 3 * theTime0 - 6 * theTime1 + 3 * theTime2;
		double c = -3 * theTime0 + 3 * theTime1;
		double d = theTime0 - theTime;

		double[] myResult = CubicSolver.solveCubic(a, b, c, d);
		int i = 0;
		while(i < myResult.length - 1 && (myResult[i] < 0 || myResult[i] > 1)) {
			i++;
		}
		return myResult[i];
	}
	
	private double bezierValue(double theValue1, double theValue2, double theValue3, double theValue4, double theBlend) {
		return 
		(-theValue1 + 3 * theValue2 - 3 * theValue3 + theValue4) * theBlend * theBlend * theBlend + 
		(3 * theValue1 - 6 * theValue2 + 3 * theValue3) * theBlend * theBlend	+ 
		(-3 * theValue1 + 3 * theValue2) * theBlend + theValue1;
	}

	public double sampleBezierSegment(ControlPoint p0, ControlPoint p1, ControlPoint p2, ControlPoint p3, double theTime) {
		double myBezierBlend = bezierBlend(p0.time(), p1.time(), p2.time(), p3.time(), theTime);
		return bezierValue(p0.value(), p1.value(), p2.value(), p3.value(), myBezierBlend);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue(double, de.artcom.timeline.model.TrackData)
	 */
	@Override
	public double interpolateValue(double theTime, TrackData theData) {

		ControlPoint mySample = new ControlPoint(theTime, 0);
		SortedSet<ControlPoint> myHeadSet = theData.headSet(mySample, false);

		ControlPoint p1 = null;
		ControlPoint p2 = null;

		if (myHeadSet.size() != 0) {
			p1 = theData.getLastOnSamePosition(myHeadSet.last());
		}
		
		if(p1 instanceof BezierControlPoint) {
			p2 = ((BezierControlPoint)p1)._myOutHandle;
		}else {
			p2 = p1;
		}

		return sampleBezierSegment(p1, p2, _myInHandle, this, theTime);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public BezierControlPoint clone() {
		BezierControlPoint myResult = new BezierControlPoint(_myTime, _myValue);
		myResult.inHandle(_myInHandle.clone());
		myResult.inHandle().parent(myResult);
		
		myResult.outHandle(_myOutHandle.clone());
		myResult.outHandle().parent(myResult);
		
		return myResult;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#setTime(double)
	 */
	@Override
	public void time(double theTime) {
		double myDifference = theTime - _myTime;
		super.time(theTime);
		
		_myInHandle.time(_myInHandle.time() + myDifference);
		_myOutHandle.time(_myOutHandle.time() + myDifference);
	}
	
	@Override
	public CCXMLElement toXML(double theStartTime, double theEndTime) {
		CCXMLElement myResult = super.toXML(theStartTime, theEndTime);
		myResult.addChild(_myInHandle.toXML(theStartTime, theEndTime));
		myResult.addChild(_myOutHandle.toXML(theStartTime, theEndTime));
		return myResult;
	}
	
	@Override
	public void fromXML(CCXMLElement theXML) {
		super.fromXML(theXML);
		CCXMLElement myInHandleXML = theXML.child(0);
		_myInHandle = new HandleControlPoint(
			this, 
			HandleType.BEZIER_IN_HANDLE, 
			myInHandleXML.doubleAttribute(TIME_ATTRIBUTE), 
			myInHandleXML.doubleAttribute(VALUE_ATTRIBUTE)
		);
		
		CCXMLElement myOutHandleXML = theXML.child(1);
		_myOutHandle = new HandleControlPoint(
			this, 
			HandleType.BEZIER_OUT_HANDLE, 
			myOutHandleXML.doubleAttribute(TIME_ATTRIBUTE), 
			myOutHandleXML.doubleAttribute(VALUE_ATTRIBUTE)
		);
	}
}
