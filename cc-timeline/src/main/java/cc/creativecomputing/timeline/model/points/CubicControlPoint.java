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


/**
 * @author christianriekoff
 *
 */
public class CubicControlPoint extends ControlPoint{
	
	public static double TENSION = 0;
	public static double BIAS = 0;
	public static double CONTINUITY = 0;
	

	public CubicControlPoint() {
		super(ControlPointType.CUBIC);
	}

	public CubicControlPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.CUBIC);
	}

	private double adaptiveTension(ControlPoint p1, ControlPoint p2, double theTension) {
		double myDistance = Math.abs(p2.time() - p1.time());
		if (myDistance < 4.0) {
			return (1 - myDistance / 4.0) + theTension;
		}
		return theTension;
	}
	
	private ControlPoint sampleTCBSegment(
		ControlPoint p1, ControlPoint p2, ControlPoint p3, ControlPoint p4, 
		double t, double c, double b, 
		ControlPoint theSamplePoint
	) {
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

		double s = (theSamplePoint.time() - p2.time()) / (p3.time() - p2.time());

		t = adaptiveTension(p2, p3, t);

		double h1 = 2 * Math.pow(s, 3.0) - 3 * Math.pow(s, 2.0) + 1;
		double h2 = (-2) * Math.pow(s, 3.0) + 3 * Math.pow(s, 2);
		double h3 = Math.pow(s, 3.0) - 2 * Math.pow(s, 2.0) + s;
		double h4 = Math.pow(s, 3.0) - Math.pow(s, 2);

		double TDix = (1 - t) * (1 + c) * (1 + b) * (p2.time() - p1.time()) / 2.0 + (1 - t) * (1 - c) * (1 - b) * (p3.time() - p2.time()) / 2.0;
		double TDiy = (1 - t) * (1 + c) * (1 + b) * (p2.value() - p1.value()) / 2.0 + (1 - t) * (1 - c) * (1 - b) * (p3.value() - p2.value()) / 2.0;

		double TSix = (1 - t) * (1 - c) * (1 + b) * (p3.time() - p2.time()) / 2.0 + (1 - t) * (1 + c) * (1 - b) * (p4.time() - p3.time()) / 2.0;
		double TSiy = (1 - t) * (1 - c) * (1 + b) * (p3.value() - p2.value()) / 2.0 + (1 - t) * (1 + c) * (1 - b) * (p4.value() - p3.value()) / 2.0;

		double myTime = h1 * p2.time() + h2 * p3.time() + h3 * TDix + h4 * TSix;
		double myValue = h1 * p2.value() + h2 * p3.value() + h3 * TDiy + h4 * TSiy;

		return new ControlPoint(myTime, myValue);

	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue(double, de.artcom.timeline.model.TrackData)
	 */
	@Override
	public double interpolateValue(double theTime, TrackData theData) {
		ControlPoint mySample = new ControlPoint(theTime,0);
		SortedSet<ControlPoint> myHeadSet = theData.headSet(mySample, false);
		theData.lower(mySample);

		ControlPoint p1 = null;
		ControlPoint p2 = null;
		ControlPoint p3 = null;
		ControlPoint p4 = null;

		if (myHeadSet.size() != 0) {
			p2 = theData.getLastOnSamePosition(myHeadSet.last());
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

		ControlPoint myResult = sampleTCBSegment(p1, p2, p3, p4, TENSION, CONTINUITY, BIAS, mySample);
		return myResult.value();
	}
	
	public CubicControlPoint clone() {
		return new CubicControlPoint(_myTime, _myValue);
	}
}
