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
package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;


public class CCBezierCurve {
	
	private static class BezierSegment{
		final CCVector3f startPoint;
		final CCVector3f startAnchor;
		final CCVector3f endAnchor;
		final CCVector3f endPoint;
		
		private BezierSegment(
			final CCVector3f theStartPoint, final CCVector3f theStartAnchor, 
			final CCVector3f theEndAnchor, final CCVector3f theEndPoint
		){
			startPoint = theStartPoint;
			startAnchor = theStartAnchor;
			endAnchor = theEndAnchor;
			endPoint = theEndPoint;
		}
	}
	
	private final List<BezierSegment> _mySegments = new ArrayList<BezierSegment>();
	private final CCVector3f _myStartPoint;
	
	public CCBezierCurve(
		final CCVector3f theStartPoint, final CCVector3f theStartAnchor, 
		final CCVector3f theEndAnchor, final CCVector3f theEndPoint
	){
		_myStartPoint = theStartPoint;
		_mySegments.add(new BezierSegment(theStartPoint,theStartAnchor,theEndAnchor,theEndPoint));
	}
	
	public CCBezierCurve(final CCVector3f theStartPoint){
		_myStartPoint = theStartPoint;
	}
	
	public void addSegmentToEnd(final CCVector3f theStartAnchor, final CCVector3f theEndAnchor, final CCVector3f theEndPoint){
		if(_mySegments.size() == 0){
			_mySegments.add(
				new BezierSegment(
					_myStartPoint,
					theStartAnchor,
					theEndAnchor,
					theEndPoint
				)
			);
			return;
		}
		_mySegments.add(
			new BezierSegment(
				_mySegments.get(_mySegments.size()-1).endPoint,
				theStartAnchor,
				theEndAnchor,
				theEndPoint
			)
		);
	}
	
	public void addSegmentToBegin(final CCVector3f theStartPoint, final CCVector3f theStartAnchor, final CCVector3f theEndAnchor){
		if(_mySegments.size() == 0){
			_mySegments.add(
				new BezierSegment(
					theStartPoint,
					theStartAnchor,
					theEndAnchor,
					_myStartPoint
				)
			);
			return;
		}
		_mySegments.add(0,
			new BezierSegment(
				theStartPoint,
				theStartAnchor,
				theEndAnchor,
				_mySegments.get(0).startPoint
			)
		);
	}
	
	public void draw(final CCGraphics g){
		float size = _mySegments.size() * g.bezierDetail();
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float i = 0; i <= size;i++){
			g.vertex(bezierPoint(i/size));
		}
		g.endShape();
	}
	
	public void draw(final CCGraphics g, float theBegin, float theEnd){
//		float size = _mySegments.size() * g.bezierDetail();
		
		if(theEnd < theBegin){
			float temp = theEnd;
			theEnd = theBegin;
			theBegin = temp;
		}
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float i = theBegin; i <= theEnd;i+=0.01f){
			g.vertex(bezierPoint(i));
		}
		g.vertex(bezierPoint(theEnd));
		g.endShape();
	}
	
	public float length(int detail){
		float size = _mySegments.size() * detail;
		float myResult = 0;
		
		for(float i = 0; i < size;i++){
			CCVector3f p1 = bezierPoint(i/size);
			CCVector3f p2 = bezierPoint((i+1)/size);
			
			myResult += p1.distance(p2);
		}
		
		return myResult/size;
	}
	
	public CCVector3f bezierPoint(float theBlend){
		if(theBlend >= 1)return _mySegments.get(_mySegments.size()-1).endPoint;
		
		// get the segment for the given blend
		final int mySegmentIndex = (int)(_mySegments.size() * theBlend);
		final BezierSegment mySegment = _mySegments.get(mySegmentIndex);
		
		// recalculate the blend for the chosen segment
		theBlend = theBlend - (float)mySegmentIndex/_mySegments.size();
		theBlend *= _mySegments.size();
		
		return CCVecMath.bezierPoint(
			mySegment.startPoint, 
			mySegment.startAnchor, 
			mySegment.endAnchor, 
			mySegment.endPoint, 
			theBlend
		);
	}
}
