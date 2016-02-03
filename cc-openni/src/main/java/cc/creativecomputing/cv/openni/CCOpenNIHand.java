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
package cc.creativecomputing.cv.openni;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 * 
 */
public class CCOpenNIHand {

	private int _myID;

	private CCVector3f _myPosition = new CCVector3f();
	private List<CCVector3f> _myHistory = new ArrayList<CCVector3f>();
	private int _myHistorySize;
	private CCMatrix4f _myTransformationMatrix;

	CCOpenNIHand(int theID, CCMatrix4f theTransformationMatrix, int theHistorySize) {
		_myID = theID;
		_myHistorySize = theHistorySize;
		_myTransformationMatrix = theTransformationMatrix;
	}
	
	public int id() {
		return _myID;
	}

	public void draw(CCGraphics g) {
		g.color(255, 0, 0, 200);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for (CCVector3f myPoint : _myHistory) {
			g.vertex(myPoint);
		}

		g.color(255, 0, 0);
		g.strokeWeight(4);
		g.point(_myPosition);
	}

	public CCVector3f position() {
		return _myPosition;
	}
	
	public List<CCVector3f> history(){
		return _myHistory;
	}
	
	public void historySize(int theHistorySize) {
		_myHistorySize = theHistorySize;
		while (_myHistory.size() >= _myHistorySize) { // remove the last point
			_myHistory.remove(_myHistory.size() - 1);
		}
	}
	
	void position(CCVector3f thePosition) {
		_myPosition.set(thePosition);
		_myTransformationMatrix.inverseTransform(thePosition);

		_myHistory.add(0, thePosition);
		while (_myHistory.size() > _myHistorySize) { // remove the last point
			_myHistory.remove(_myHistory.size() - 1);
		}
	}
}
