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


import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.xml.CCXMLElement;

/**
 * @author christianriekoff
 * 
 */
public class TimedEventPoint extends ControlPoint {
	
	public static interface TimedEventPointContent{
		public CCXMLElement toXML();
		
		public void fromXML(CCXMLElement theXMLElement);
	}

	private HandleControlPoint _myEndPoint;
	
	private long _myID;
	
	private String _myEventType;
	
	private TimedEventPointContent _myContent;

	public TimedEventPoint() {
		super(ControlPointType.TIMED_EVENT);
	}

	public TimedEventPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.TIMED_EVENT);
		_myID = System.currentTimeMillis();
	}
	
	public void eventType(String theEventType) {
		_myEventType = theEventType;
	}
	
	public String eventType(){
		return _myEventType;
	}
	
	public void content(TimedEventPointContent theContent) {
		_myContent = theContent;
	}
	
	public TimedEventPointContent content() {
		return _myContent;
	}
	
	public long id() {
		return _myID;
	}

	public boolean hasHandles() {
		return true;
	}

	public HandleControlPoint endPoint() {
		return _myEndPoint;
	}

	public void endPoint(HandleControlPoint theEndPoint) {
		_myEndPoint = theEndPoint;
	}
	
	public double endTime() {
		return _myEndPoint.time();
	}
	
	public void endTime(double theEndTime) {
		if(_myEndPoint == null) {
			_myEndPoint = new HandleControlPoint(this, HandleType.TIME_END);
		}
		_myEndPoint.time(theEndTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue(double, de.artcom.timeline.model.TrackData)
	 */
	@Override
	public double interpolateValue(double theTime, TrackData theData) {
		return value();
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public TimedEventPoint clone() {
		TimedEventPoint myResult = new TimedEventPoint(_myTime, _myValue);
		myResult.endPoint(_myEndPoint);
		return myResult;
	}

	@Override
	public CCXMLElement toXML(double theStartTime, double theEndTime) {
		CCXMLElement myResult = super.toXML(theStartTime, theEndTime);
		myResult.addAttribute("id", _myID);
		myResult.addAttribute("eventType", _myEventType);
		myResult.addChild(_myEndPoint.toXML(theStartTime, theEndTime));
		myResult.addChild(_myContent.toXML());
		return myResult;
	}
	
	private CCXMLElement _myContentXML;
	
	@Override
	public void fromXML(CCXMLElement theXML) {
		super.fromXML(theXML);
		_myID = theXML.longAttribute("id");
		_myEventType = theXML.attribute("eventType");
		CCXMLElement myInHandleXML = theXML.child(0);
		_myEndPoint = new HandleControlPoint(
			this, 
			HandleType.TIME_END, 
			myInHandleXML.doubleAttribute(TIME_ATTRIBUTE), 
			myInHandleXML.doubleAttribute(VALUE_ATTRIBUTE)
		);
		_myContentXML = theXML.child(1);
	}
	
	public CCXMLElement contentXML() {
		return _myContentXML;
	}
	
	
}
