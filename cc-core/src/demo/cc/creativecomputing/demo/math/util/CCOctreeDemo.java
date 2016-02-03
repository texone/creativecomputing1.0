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
package cc.creativecomputing.demo.math.util;

import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

/**
 * @author info
 * 
 */
public class CCOctreeDemo extends CCApp {

	/**
	 * PointOctree demo showing how to use the structure to efficiently retrieve and isolate particle like objects
	 * within a defined neighbourhood from a much larger set of objects.
	 * 
	 * Key controls:
	 * 
	 * SPACE : add 100 more particles S : choose between sphere and bounding box culling. O : toggle display of the
	 * octree structure - / + : adjust the size of the culling radius
	 */

	// octree dimensions
	float DIM = 500;
	float DIM2 = DIM / 2;
	
	private float _myClipRadius = 100;

	// number of particles to add at once
	int NUM = 100;

	// show octree debug info
	private boolean _myShowOctree = true;

	// use clip sphere or axis aligned bounding box
	private boolean _myUseSphere = false;


	private CCDrawableOctree<CCVector3f> _myOctree;
	private CCVector3f _myCursor = new CCVector3f();

	// start with one particle
	private int _myNumberOfParticles = 1;
	
	private CCArcball _myArcball;

	public void setup() {
		_myArcball = new CCArcball(this);
		// setup empty octree so that it's centered around the world origin
		_myOctree = new CCDrawableOctree<CCVector3f>(new CCVector3f(-1, -1, -1).scale(DIM2), DIM);
		// add an initial particle at the origin
		_myOctree.addElement(new CCVector3f());
		
		g.clearColor(255);
		g.pointSize(4);
	}

	public void draw() {
		g.clear();
		// rotate view on mouse drag
		if (!mousePressed) {
			_myCursor.x = -(width * 0.5f - mouseX) / (width / 2) * DIM2;
			_myCursor.y = -(height * 0.5f - mouseY) / (height / 2) * DIM2;
		}

		g.pushMatrix();
		_myArcball.draw(g);
		// show debug view of tree
		if (_myShowOctree)
			_myOctree.draw(g);
		
		// show crosshair 3D cursor
		g.color(255, 0, 0);
		g.beginShape(CCDrawMode.LINES);
		g.vertex(_myCursor.x, -DIM2, 0);
		g.vertex(_myCursor.x, DIM2, 0);
		g.vertex(-DIM2, _myCursor.y, 0);
		g.vertex(DIM2, _myCursor.y, 0);
		g.endShape();
		
		// show particles within the specific clip radius
		long t0 = System.currentTimeMillis();
		ArrayList<CCVector3f> points = null;
		if (_myUseSphere) {
			points = _myOctree.elementsWithinSphere(_myCursor, _myClipRadius);
		} else {
			points = _myOctree.elementsWithinBox(new CCAABB(_myCursor, new CCVector3f(_myClipRadius, _myClipRadius, _myClipRadius)));
		}
		long dt = System.currentTimeMillis() - t0;
		int numClipped = 0;
		
		if (points != null) {
			numClipped = points.size();
			g.beginShape(CCDrawMode.POINTS);
			for (CCVector3f p:points) {
				g.color(0);
				g.vertex(p);
			}
			g.endShape();
		}
		// show clipping sphere
		g.color(0, 30);
		g.translate(_myCursor.x, _myCursor.y, 0);
		g.sphere(_myClipRadius);
		g.popMatrix();
		g.color(0);
		g.text("total: " + _myNumberOfParticles, -width/2 + 10, -height/2 + 30);
		g.text("clipped: " + numClipped + " (time: " + dt + "ms)", -width/2 +  10,-height/2 +  50);
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_SPACE:
			// add NUM new particles within a sphere of radius DIM2
			for (int i = 0; i < NUM; i++)
				_myOctree.addElement(new CCVector3f(CCMath.random(-DIM/2, DIM/2),CCMath.random(-DIM/2, DIM/2),CCMath.random(-DIM/2, DIM/2)));
			_myNumberOfParticles += NUM;
			break;
		case VK_S:
			_myUseSphere = !_myUseSphere;
			break;
		case VK_O:
			_myShowOctree = !_myShowOctree;
			break;
		case VK_MINUS:
			_myClipRadius = CCMath.max(_myClipRadius - 1, 2);
			break;
		case VK_EQUALS:
			_myClipRadius = CCMath.min(_myClipRadius + 1, DIM);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOctreeDemo.class);
		myManager.settings().size(1024, 768);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
