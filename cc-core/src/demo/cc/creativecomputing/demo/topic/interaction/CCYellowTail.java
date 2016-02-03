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
package cc.creativecomputing.demo.topic.interaction;

import java.awt.Polygon;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Yellowtail by Golan Levin (www.flong.com).
 * 
 * Click, drag, and release to create a kinetic gesture.
 * 
 * Yellowtail (1998-2000) is an interactive software system for the gestural creation and performance of real-time
 * abstract animation. Yellowtail repeats a user's strokes end-over-end, enabling simultaneous specification of a line's
 * shape and quality of movement. Each line repeats according to its own period, producing an ever-changing and
 * responsive display of lively, worm-like textures. Implementation of golan levin famous <a
 * href="http://www.flong.com/storage/experience/yellowtail/">yellow tail<a>
 * 
 * @author christianriekoff
 * 
 */
public class CCYellowTail extends CCApp {

	Gesture gestureArray[];
	final int nGestures = 36; // Number of gestures
	final int minMove = 3; // Minimum travel for a new point
	int currentGestureID;

	Polygon tempP;
	int tmpXp[];
	int tmpYp[];

	public void setup() {

		currentGestureID = -1;
		gestureArray = new Gesture[nGestures];
		for (int i = 0; i < nGestures; i++) {
			gestureArray[i] = new Gesture(width, height);
		}
		clearGestures();
	}

	public void draw() {
		g.clear();

		updateGeometry();
		g.color(255, 255, 245);
		for (int i = 0; i < nGestures; i++) {
			renderGesture(gestureArray[i], width, height);
		}
	}

	@Override
	public void mousePressed(CCMouseEvent theEvent) {
		currentGestureID = (currentGestureID + 1) % nGestures;
		Gesture myGesture = gestureArray[currentGestureID];
		myGesture.clear();
		myGesture.clearPolys();
		myGesture.addPoint(theEvent.x() - width/2, height / 2 - theEvent.y());
	}

	@Override
	public void mouseDragged(CCMouseEvent theEvent) {
		if (currentGestureID >= 0) {
			Gesture myGesture = gestureArray[currentGestureID];
			if (myGesture.distToLast(theEvent.x() - width/2, height / 2 - theEvent.y()) > minMove) {
				myGesture.addPoint(theEvent.x() - width/2, height / 2 - theEvent.y());
				myGesture.smooth();
				myGesture.compile();
			}
		}
	}

	@Override
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()) {
		case VK_PLUS:
			if (currentGestureID >= 0) {
				float th = gestureArray[currentGestureID].thickness;
				gestureArray[currentGestureID].thickness = CCMath.min(96, th + 1);
				gestureArray[currentGestureID].compile();
			}
			break;
		case VK_MINUS:
			if (currentGestureID >= 0) {
				float th = gestureArray[currentGestureID].thickness;
				gestureArray[currentGestureID].thickness = CCMath.min(96, th + 1);
				gestureArray[currentGestureID].compile();
			}
			break;
		case VK_SPACE:
			clearGestures();
			break;
		default:
		}
	}

	public void renderGesture(Gesture gesture, int w, int h) {
		if (gesture.exists) {
			if (gesture.nPolys > 0) {
				Polygon polygons[] = gesture.polygons;
				int crosses[] = gesture.crosses;

				int xpts[];
				int ypts[];
				Polygon p;
				int cr;

				g.beginShape(CCDrawMode.QUADS);
				int gnp = gesture.nPolys;
				for (int i = 0; i < gnp; i++) {

					p = polygons[i];
					xpts = p.xpoints;
					ypts = p.ypoints;

					g.vertex(xpts[0], ypts[0]);
					g.vertex(xpts[1], ypts[1]);
					g.vertex(xpts[2], ypts[2]);
					g.vertex(xpts[3], ypts[3]);

					if ((cr = crosses[i]) > 0) {
						if ((cr & 3) > 0) {
							g.vertex(xpts[0] + w, ypts[0]);
							g.vertex(xpts[1] + w, ypts[1]);
							g.vertex(xpts[2] + w, ypts[2]);
							g.vertex(xpts[3] + w, ypts[3]);

							g.vertex(xpts[0] - w, ypts[0]);
							g.vertex(xpts[1] - w, ypts[1]);
							g.vertex(xpts[2] - w, ypts[2]);
							g.vertex(xpts[3] - w, ypts[3]);
						}
						if ((cr & 12) > 0) {
							g.vertex(xpts[0], ypts[0] + h);
							g.vertex(xpts[1], ypts[1] + h);
							g.vertex(xpts[2], ypts[2] + h);
							g.vertex(xpts[3], ypts[3] + h);

							g.vertex(xpts[0], ypts[0] - h);
							g.vertex(xpts[1], ypts[1] - h);
							g.vertex(xpts[2], ypts[2] - h);
							g.vertex(xpts[3], ypts[3] - h);
						}

						// I have knowingly retained the small flaw of not
						// completely dealing with the corner conditions
						// (the case in which both of the above are true).
					}
				}
				g.endShape();
			}
		}
	}

	void updateGeometry() {
		Gesture J;
		for (int g = 0; g < nGestures; g++) {
			if ((J = gestureArray[g]).exists) {
				if (g != currentGestureID) {
					advanceGesture(J);
				} else if (!mousePressed) {
					advanceGesture(J);
				}
			}
		}
	}

	public void advanceGesture(Gesture gesture) {
		// Move a Gesture one step
		if (gesture.exists) { // check
			int nPts = gesture.nPoints;
			int nPts1 = nPts - 1;
			CCVector3f path[];
			float jx = gesture.jumpDx;
			float jy = gesture.jumpDy;

			if (nPts > 0) {
				path = gesture.path;
				for (int i = nPts1; i > 0; i--) {
					path[i].x = path[i - 1].x;
					path[i].y = path[i - 1].y;
				}
				path[0].x = path[nPts1].x - jx;
				path[0].y = path[nPts1].y - jy;
				gesture.compile();
			}
		}
	}

	public void clearGestures() {
		for (int i = 0; i < nGestures; i++) {
			gestureArray[i].clear();
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCYellowTail.class);
		myManager.settings().size(1024, 768);
		myManager.start();
	}
}
