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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCQuad3f {
	private CCVector3f _myLeftUpper;
	private CCVector3f _myLeftBottom;
	private CCVector3f _myRightBottom;
	private CCVector3f _myRightUpper;

	public CCQuad3f(final CCVector3f theLeftUpper, final CCVector3f theLeftBottom, final CCVector3f theRightBottom, final CCVector3f theRightUpper) {
		_myLeftUpper = theLeftUpper;
		_myLeftBottom = theLeftBottom;
		_myRightBottom = theRightBottom;
		_myRightUpper = theRightUpper;
	}

	/**
	 * Returns a point on the quad by blending between the given corner points
	 * @param theX value to blend between the left and right side of the quad
	 * @param theY value to blend between the top and bottom of the quad
	 * @return point on the quad by blending between the given corner points
	 */
	public CCVector3f gridVector(final float theX, final float theY) {
		CCVector3f myX1 = CCVecMath.blend(theX, _myLeftUpper, _myRightUpper);
		CCVector3f myX2 = CCVecMath.blend(theX, _myLeftBottom, _myRightBottom);
		return CCVecMath.blend(theY, myX1, myX2);
	}
	
	/**
	 * Returns a random point on the quad
	 * @return a random point on the quad
	 */
	public CCVector3f randomPoint() {
		return gridVector(CCMath.random(), CCMath.random());
	}
	
	public CCVector3f leftTop(){
		return _myLeftUpper;
	}

	public CCVector3f leftBottom() {
		return _myLeftBottom;
	}

	public void leftBottom(CCVector3f theLeftBottom) {
		_myLeftBottom = theLeftBottom;
	}

	public CCVector3f rightBottom() {
		return _myRightBottom;
	}

	public void rightBottom(CCVector3f theRightBottom) {
		_myRightBottom = theRightBottom;
	}

	public CCVector3f rightTop() {
		return _myRightUpper;
	}

	public void rightUpper(CCVector3f theRightUpper) {
		_myRightUpper = theRightUpper;
	}
}
