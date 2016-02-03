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
import cc.creativecomputing.xml.CCXMLElement;


/**
 * @author christianriekoff
 * 
 */
public class ExponentialControlPoint extends ControlPoint {
	
	private HandleControlPoint _myHandle;

	public ExponentialControlPoint() {
		super(ControlPointType.EXPONENTIAL);
	}

	public ExponentialControlPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.EXPONENTIAL);
	}
	
	public boolean hasHandles() {
		return true;
	}
	
	public HandleControlPoint handle() {
		return _myHandle;
	}
	
	public void handle(HandleControlPoint theHandle) {
		_myHandle = theHandle;
	}

	private double sampleExpSegment(ControlPoint p0, ControlPoint p1, ControlPoint p2, double theTime) {
		double s = (theTime - p0.time()) / (p2.time() - p0.time());
		double p = (((1 - p1.value()) - 0.5));
		return p0.value() + Math.exp(Math.log(s) * Math.exp(Math.PI * Math.E * p)) * (p2.value() - p0.value());
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

		if (myHeadSet.size() != 0) {
			p1 = theData.getLastOnSamePosition(myHeadSet.last());
		}

		return sampleExpSegment(p1, _myHandle, this, theTime);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public ExponentialControlPoint clone() {
		ExponentialControlPoint myResult = new ExponentialControlPoint(_myTime, _myValue);
		myResult.handle(_myHandle.clone());
		myResult.handle().parent(myResult);
		return myResult;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.model.points.ControlPoint#toXML(double, double)
	 */
	@Override
	public CCXMLElement toXML(double theStartTime, double theEndTime) {
		CCXMLElement myResult = super.toXML(theStartTime, theEndTime);
		CCXMLElement myHandleXML = myResult.createChild(CONTROLPOINT_ELEMENT);
		myHandleXML.addAttribute(TIME_ATTRIBUTE, _myHandle.time() - theStartTime);
		myHandleXML.addAttribute(VALUE_ATTRIBUTE, _myHandle.value());
		myHandleXML.addAttribute(CONTROL_POINT_TYPE_ATTRIBUTE, ControlPointType.HANDLE.toString());
		return myResult;
	}
	
	@Override
	public void fromXML(CCXMLElement theXML) {
		super.fromXML(theXML);
		CCXMLElement myHandleXML = theXML.child(0);
		_myHandle = new HandleControlPoint(
			this, 
			HandleType.EXPONENT_HANDLE, 
			myHandleXML.doubleAttribute(TIME_ATTRIBUTE), 
			myHandleXML.doubleAttribute(VALUE_ATTRIBUTE)
			
		);
	}
}
