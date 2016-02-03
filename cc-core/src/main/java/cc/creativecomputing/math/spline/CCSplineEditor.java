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
package cc.creativecomputing.math.spline;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCMouseAdapter;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCSplineEditor {

	private List<CCSpline> _mySplines = new ArrayList<CCSpline>();

	private CCVector3f _myChosenPoint;
	private CCSpline _myChosenSpline;

	private CCApp _myApp;

	public CCSplineEditor(CCApp theApp, String thePath) {
		_myApp = theApp;
		
		_myApp.addMouseListener(new CCMouseAdapter() {
		
			@Override
			public void mousePressed(CCMouseEvent theEvent) {
				if(theEvent.isAltDown() && _myChosenPoint != null) {
					_myChosenSpline.removePoint(_myChosenPoint);
					return;
				}
				if(!theEvent.isCtrlDown())return;
				if(_myChosenPoint != null)return;
				
				if(theEvent.isShiftDown()) {
					_mySplines.add(_myChosenSpline = new CCCatmulRomSpline(0.5f, false));
				}
				
				if (_myChosenSpline == null)return;
				
					
				_myChosenSpline.addPoint(_myChosenPoint = new CCVector3f(theEvent.x() - _myApp.width / 2, _myApp.height / 2 - theEvent.y()));
				
			}
			
		});

		_myApp.addMouseMotionListener(new CCMouseAdapter() {

			@Override
			public void mouseMoved(CCMouseEvent theMouseEvent) {
				CCVector3f myMousePosition = mousePosition(theMouseEvent);
				for(CCSpline mySpline:_mySplines) {
					for (CCVector3f myControlPoint : mySpline.points()) {
						if (myMousePosition.distance(myControlPoint) < 5) {
							_myChosenPoint = myControlPoint;
							_myChosenSpline = mySpline;
							return;
						}
					}
				}
				
				_myChosenPoint = null;
			}

			@Override
			public void mouseDragged(CCMouseEvent theMouseEvent) {
				if (_myChosenPoint == null)return;
				CCVector3f myMousePosition = mousePosition(theMouseEvent);
				_myChosenPoint.set(myMousePosition);
			}
		});
	}
	
	public CCSplineEditor(CCApp theApp, CCSpline theSpline, String thePath) {
		this(theApp, thePath);
		_mySplines.add(theSpline);
		_myChosenSpline = theSpline;
	}
	
	public CCSplineEditor(CCApp theApp, CCSpline theSpline) {
		this(theApp, theSpline, null);
	}
	
	public CCSplineEditor(CCApp theApp) {
		this(theApp, null, null);
	}
	
	public void addSpline(CCSpline theSpline) {
		_mySplines.add(theSpline);
	}
	
	public void loadSplines(String thePath) {
		if(thePath == null) return;
		_mySplines.clear();
		CCXMLElement mySplinesXML = CCXMLIO.createXMLElement(thePath);
		if(mySplinesXML == null)return;
		
		for(CCXMLElement mySplineXML:mySplinesXML) {
			CCSpline mySpline = new CCCatmulRomSpline(0.5f, false);
			for(CCXMLElement myPointXML:mySplineXML) {
				mySpline.addPoint(new CCVector3f(
					myPointXML.child("x").floatContent(),
					myPointXML.child("y").floatContent(),
					myPointXML.child("z").floatContent()
				));
			}
			_mySplines.add(mySpline);
		}
	}
	
	public void saveSplines(String thePath) {
		if(thePath == null) return;
		
		CCXMLElement mySplinesXML = new CCXMLElement("splines");
		for(CCSpline mySpline:_mySplines) {
			CCXMLElement mySplineXML = mySplinesXML.createChild("spline");
			for(CCVector3f myControlPoint:mySpline.points()) {
				CCXMLElement myPointXML = mySplineXML.createChild("point");
				myPointXML.createChild("x", myControlPoint.x);
				myPointXML.createChild("y", myControlPoint.y);
				myPointXML.createChild("z", myControlPoint.z);
			}
		}
		CCXMLIO.saveXMLElement(mySplinesXML, thePath);
	}
	
	public void reset() {
		_mySplines.clear();
	}
	
	public List<CCSpline> splines(){
		return _mySplines;
	}

	private CCVector3f mousePosition(CCMouseEvent theEvent) {
		return new CCVector3f(theEvent.x() - _myApp.width / 2, _myApp.height / 2 - theEvent.y());
	}
	
	public void drawSpline(CCGraphics g, CCSpline theSpline) {
		if(theSpline == null)return;
		g.color(255);
		if (theSpline.points().size() <= 0)
			return;
		theSpline.draw(g);


		g.pushAttribute();
		g.pointSize(10);
		g.beginShape(CCDrawMode.POINTS);
		g.color(255, 0, 0);
		g.vertex(theSpline.points().get(0));
		g.color(255);
		for (int i = 1; i < theSpline.points().size() - 1; i++) {
			g.vertex(theSpline.points().get(i));
		}
		g.color(0, 0, 255);
		g.vertex(theSpline.points().get(theSpline.points().size() - 1));
		g.endShape();
		g.popAttribute();
	}

	public void draw(CCGraphics g) {
		for(CCSpline mySpline:_mySplines) {
			if(mySpline == _myChosenSpline) {
				g.color(0,255,0);
			}else {
				g.color(255);
			}
			drawSpline(g, mySpline);
		}
		
		if (_myChosenPoint == null)
			return;
		
		g.color(0, 255, 0);
		g.ellipse(_myChosenPoint, 10);
	}
}
