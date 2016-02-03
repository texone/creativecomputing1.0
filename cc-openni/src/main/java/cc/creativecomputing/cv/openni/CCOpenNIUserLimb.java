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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Each UserLimb object manages a limb in the 3D scene, which involves updating
 * the limb by setting its visibility, position, orientation, and length.
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIUserLimb {

	private CCOpenNIUserJoint _myStartJoint;
	private CCOpenNIUserJoint _myEndJoint;
	private boolean _myIsVisible;

	public CCOpenNIUserLimb(CCOpenNIUserJoint theStartJoint, CCOpenNIUserJoint theEndJoint) {
		_myStartJoint = theStartJoint;
		_myEndJoint = theEndJoint;
		_myIsVisible = true;
	}

	public CCOpenNIUserJoint joint1() {
		return _myStartJoint;
	}

	public CCOpenNIUserJoint joint2() {
		return _myEndJoint;
	}

	public boolean isVisible() {
		return _myIsVisible;
	}

	private static final double MIN_DIST = 0.00001;

	/**
	 * can the limb be updated given these joint positions?
	 * 
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	private boolean isLimbUpdatable(CCVector3f startPos, CCVector3f endPos) {
		if (!_myStartJoint.isVisible() || !_myEndJoint.isVisible()) {
			// return false;
		}

		// very small difference between the (x,z) coordinates of the joints
		// indicates an upcoming error
		if ((CCMath.abs(endPos.x - startPos.x) < MIN_DIST) && (CCMath.abs(endPos.z - startPos.z) < MIN_DIST)) {
			// CCLog.info("Joints too close in: " + startJoint + " -- " +
			// endJoint);
			return false;
		}

		return true;
	}

	/**
	 * update visibility, position, orientation, and length of limb
	 */
	public void update(float theDeltaTime) {
		// get start and end joint positions
		CCVector3f myStartPos = _myStartJoint.position();
		CCVector3f myEndPos = _myEndJoint.position();

		if (!isLimbUpdatable(myStartPos, myEndPos)) { // hide the limb
			_myIsVisible = false;
			return;
		}

		// both joints are ok, so make the limb visible
		_myIsVisible = true;
	}

	public void draw(CCGraphics g) {
		g.vertex(_myStartJoint.position());
		g.vertex(_myEndJoint.position());
	}

}
