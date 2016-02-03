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
package cc.creativecomputing.math;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;

public class CCQuad2f {
	private CCVector2f _myLeftTop;
	private CCVector2f _myLeftBottom;
	private CCVector2f _myRightBottom;
	private CCVector2f _myRightTop;

	public CCQuad2f(final CCVector2f theLeftUpper, final CCVector2f theLeftBottom, final CCVector2f theRightBottom, final CCVector2f theRightUpper) {
		_myLeftTop = theLeftUpper;
		_myLeftBottom = theLeftBottom;
		_myRightBottom = theRightBottom;
		_myRightTop = theRightUpper;
	}
	
	public CCQuad2f(){
		this(new CCVector2f(), new CCVector2f(), new CCVector2f(), new CCVector2f());
	}

	public CCVector2f gridVector(final float theX, final float theY, CCVector2f theStore) {
		float topX = _myLeftTop.x * (1 - theX) + _myRightTop.x * theX;
		float topY = _myLeftTop.y * (1 - theX) + _myRightTop.y * theX;

		float bottomX = _myLeftBottom.x * (1 - theX) + _myRightBottom.x * theX;
		float bottomY = _myLeftBottom.y * (1 - theX) + _myRightBottom.y * theX;
		
		theStore.x = topX * (1 - theY) + bottomX * theY;
		theStore.y = topY * (1 - theY) + bottomY * theY;
		
		return theStore;
	}
	
	public CCVector2f gridVector(final float theX, final float theY){
		return gridVector(theX, theY, new CCVector2f());
	}
	
	public CCVector2f leftTop(){
		return _myLeftTop;
	}
	
	public void leftTop(CCVector2f theLeftTop){
		_myLeftTop = theLeftTop;
	}

	public CCVector2f leftBottom() {
		return _myLeftBottom;
	}

	public void leftBottom(CCVector2f theLeftBottom) {
		_myLeftBottom = theLeftBottom;
	}

	public CCVector2f rightBottom() {
		return _myRightBottom;
	}

	public void rightBottom(CCVector2f theRightBottom) {
		_myRightBottom = theRightBottom;
	}

	public CCVector2f rightTop() {
		return _myRightTop;
	}

	public void rightTop(CCVector2f theRightUpper) {
		_myRightTop = theRightUpper;
	}
	
	public void draw(CCGraphics g){
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myLeftTop);
		g.vertex(_myLeftBottom);
		g.vertex(_myRightBottom);
		g.vertex(_myRightTop);
		g.endShape();
	}
}
