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
package cc.creativecomputing.demo.font;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.font.text.CCTextContours;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;



public class CCTextContoursDemo extends CCApp{
	
	private class Letter{
		private Path _myPath;
		
		private float _myMax;
		private float _myMin;
		
		private float _myDelay;
		
		public Letter(final Path thePath) {
			_myPath = thePath;
			
			_myMin = CCMath.random(0.9f);
			_myMax = _myMin + CCMath.random(0.1f);
			
			_myDelay = -CCMath.random(5);
		}
		
		public void update(final float theDeltaTime) {
			_myDelay += theDeltaTime;
			
			if(_myDelay < 0)return;
			_myMin -= theDeltaTime * 0.5f;
			_myMin = CCMath.max(0, _myMin);

			_myMax += theDeltaTime * 0.5f;
			_myMax = CCMath.min(1, _myMax);
		}
		
		public void draw(CCGraphics g) {
			_myPath.draw(g, _myMin, _myMax);
		}
	}
	
	private static class Path{
		
		private float _myLength = 0;
		private CCVector2f _myLastPoint;
		
		private List<Float> _myLengths = new ArrayList<Float>();
		private List<Float> _myDistances = new ArrayList<Float>();
		private List<CCVector2f> _myPoints = new ArrayList<CCVector2f>();
		
		public void addPoint(CCVector2f thePoint) {
			if(_myLastPoint == null) {
				_myLengths.add(0f);
			}else {
				float myDistance = _myLastPoint.distance(thePoint);
				_myLength += myDistance;
				_myLengths.add(_myLength);
				_myDistances.add(myDistance);
			}
			_myLastPoint = thePoint;
			_myPoints.add(thePoint);
		}
		
		public int pointIndex(final float theBlend) {
			float myPointLength = theBlend * _myLength;
			
			int myIndex = 0;
			for(float myLength:_myLengths) {
				if(myPointLength < myLength) {
					break;
				}
				myIndex++;
			}
			
			return myIndex;
		}
		
		public CCVector2f point(final float theBlend) {
			float myPointLength = theBlend * _myLength;
			
			if(theBlend == 1f)return _myPoints.get(_myPoints.size()-1);
			if(theBlend == 0f)return _myPoints.get(0);
			
			int myIndex = 0;
			float myPosition = 0;
			for(float myLength:_myLengths) {
				if(myPointLength < myLength) {
					myPosition = myLength - myPointLength;
					break;
				}
				myIndex++;
			}
			float myBlend = myPosition / _myDistances.get(myIndex - 1);

			CCVector2f myV1 = _myPoints.get(myIndex - 1);
			CCVector2f myV2 = _myPoints.get(myIndex);
			
			return CCVecMath.blend(1 - myBlend, myV1, myV2);
		}
		
		public void draw(CCGraphics g, float theMin, float theMax) {
			CCVector2f myStartPoint = point(theMin);
			CCVector2f myEndPoint = point(theMax);
			
			int myStartIndex = pointIndex(theMin);
			int myEndIndex = pointIndex(theMax);
			
			g.beginShape(CCDrawMode.LINE_STRIP);
			g.vertex(myStartPoint);
			for(int i = myStartIndex; i < myEndIndex; i++) {
				CCVector2f myVertex = _myPoints.get(i);
				g.vertex(myVertex);
			}
			g.vertex(myEndPoint);
			g.endShape();
		}
	}

	int nNumPoints = 4;

	private List<Letter> _myLetters;
	
	private CCTextContours _myTextContour;

	public void setup(){
		CCOutlineFont font = CCFontIO.createOutlineFont("Arial",48, 30);
		
		_myTextContour = new CCTextContours(font);
		_myTextContour.align(CCTextAlign.CENTER);
		_myTextContour.text("List<CCVector3f> _myTextPath = font.getPath(myChar, CCTextAlign.CENTER, 50,myX, 0, 0);");
		
		_myLetters = new ArrayList<Letter>();
		for(List<CCVector2f> myContour:_myTextContour.contours()) {
			Path _myPath = new Path();
			for(CCVector2f myPoint:myContour) {
				_myPath.addPoint(myPoint);
			}

			
			_myPath.addPoint(myContour.get(0));
			
			_myLetters.add(new Letter(_myPath));
			
		}
		
		g.clearColor(0.3f);
		
	}
	
	private float _myTime = 0.001f;
	
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 0.1f;
		if(_myTime > 1)_myTime-=1;
		for(Letter myLetter:_myLetters) {
			myLetter.update(theDeltaTime);
		}
	}
	
	public void draw(){
		g.clear();
		for(Letter myLetter:_myLetters) {
			myLetter.draw(g);
		}
	}
	
	public static void main(String[] args){
		final CCApplicationManager myManager = new CCApplicationManager(CCTextContoursDemo.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(1200, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
