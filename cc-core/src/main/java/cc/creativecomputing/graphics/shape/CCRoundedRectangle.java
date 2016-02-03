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
package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCMath;

public class CCRoundedRectangle extends CCAbstractShape{
	
	private CCColor _myGradientColor;
	
	private float _myX;
	private float _myY;
	
	private float _myWidth;
	private float _myHeight;
	
	private float _myRadius;
	
	public CCRoundedRectangle() {
		this(0,0,0,0,0,new CCColor());
	}
	
	public CCRoundedRectangle(
		final float theX, final float theY, 
		final float theWidth, final float theHeight,
		final float theRadius,
		final CCColor theColor
	) {
		this(theX, theY, theWidth, theHeight, theRadius, theColor, theColor);
	}
	
	public CCRoundedRectangle(
		final float theX, final float theY, 
		final float theWidth, final float theHeight,
		final float theRadius,
		final CCColor theColor, final CCColor theGradientColor
	){
		super();
		_myX = theX;
		_myY = theY;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myRadius = theRadius;
		
		_myColor = theColor;
		_myGradientColor = theGradientColor;
		
		_myBoundingRectangle = new CCAABoundingRectangle(_myX, _myY, _myX + _myWidth, _myY + _myHeight);
	}
	
	
	
	public void translate(final float theX, final float theY){
		_myX += theX;
		_myY += theY;
	}
	
	private void drawArc(CCGraphics g, final float theCenterX, final float theCenterY, final int theStart, final int theEnd){
		for(int i = theStart; i < theEnd;i++){
			g.color(_myColor);
			g.vertex(theCenterX, theCenterY);
			g.color(_myGradientColor);
			g.vertex(
				theCenterX + CCMath.sin(CCMath.radians(i)) * _myRadius * _myScale, 
				theCenterY + CCMath.cos(CCMath.radians(i)) * _myRadius * _myScale
			);
			g.vertex(
				theCenterX + CCMath.sin(CCMath.radians(i + 1)) * _myRadius * _myScale,
				theCenterY + CCMath.cos(CCMath.radians(i + 1)) * _myRadius * _myScale
			);
		}
	}
	
	public void radius(final float theRadius){
		_myRadius = theRadius;
	}
	
	public CCColor gradientColor(){
		return _myGradientColor;
	}

	public void draw(CCGraphics g){
		if(!_myIsVisible)return;
		
		float myX1 = _myX;
		float myX2 = _myX + _myRadius * _myScale;
		float myX3 = _myX + (_myWidth - _myRadius) * _myScale;
		float myX4 = _myX + _myWidth * _myScale;

		float myY1 = _myY;
		float myY2 = _myY + _myRadius * _myScale;
		float myY3 = _myY + (_myHeight - _myRadius) * _myScale;
		float myY4 = _myY + _myHeight * _myScale;
		
		switch(_myShapeMode){
		case CENTER:
			myX1 -= _myWidth * _myScale / 2;
			myX2 -= _myWidth * _myScale / 2;
			myX3 -= _myWidth * _myScale / 2;
			myX4 -= _myWidth * _myScale / 2;

			myY1 -= _myHeight * _myScale / 2;
			myY2 -= _myHeight * _myScale / 2;
			myY3 -= _myHeight * _myScale / 2;
			myY4 -= _myHeight * _myScale / 2;
			break;
		default:
			break;
		}
		
		if(_myRadius <= 0) {
			g.color(_myColor);
			g.beginShape(CCDrawMode.TRIANGLES);
			g.vertex(myX2, myY2);
			g.vertex(myX3, myY2);
			g.vertex(myX3, myY3);

			g.vertex(myX2, myY2);
			g.vertex(myX3, myY3);
			g.vertex(myX2, myY3);
			
			g.endShape();
			return;
		}
		
		g.beginShape(CCDrawMode.TRIANGLES);
		drawArc(g, myX3, myY3, 0, 90);
		drawArc(g, myX3, myY2, 90, 180);
		drawArc(g, myX2, myY2, 180, 270);
		drawArc(g, myX2, myY3, 270, 360);

		g.color(_myGradientColor);
		g.vertex(myX2, myY1);
		g.vertex(myX3, myY1);
		g.color(_myColor);
		g.vertex(myX3, myY2);

		g.color(_myGradientColor);
		g.vertex(myX2, myY1);
		g.color(_myColor);
		g.vertex(myX3, myY2);
		g.vertex(myX2, myY2);

		g.color(_myColor);
		g.vertex(myX2, myY3);
		g.vertex(myX3, myY3);
		g.color(_myGradientColor);
		g.vertex(myX3, myY4);

		g.color(_myColor);
		g.vertex(myX2, myY3);
		g.color(_myGradientColor);
		g.vertex(myX3, myY4);
		g.vertex(myX2, myY4);

		g.color(_myGradientColor);
		g.vertex(myX1, myY2);
		g.color(_myColor);
		g.vertex(myX2, myY2);
		g.vertex(myX2, myY3);

		g.color(_myGradientColor);
		g.vertex(myX1, myY2);
		g.color(_myColor);
		g.vertex(myX2, myY3);
		g.color(_myGradientColor);
		g.vertex(myX1, myY3);

		g.color(_myColor);
		g.vertex(myX3, myY2);
		g.color(_myGradientColor);
		g.vertex(myX4, myY2);
		g.vertex(myX4, myY3);

		g.color(_myColor);
		g.vertex(myX3, myY2);
		g.color(_myGradientColor);
		g.vertex(myX4, myY3);
		g.color(_myColor);
		g.vertex(myX3, myY3);

		g.vertex(myX2, myY2);
		g.vertex(myX3, myY2);
		g.vertex(myX3, myY3);

		g.vertex(myX2, myY2);
		g.vertex(myX3, myY3);
		g.vertex(myX2, myY3);
		
		g.endShape();
	}



	@Override
	public void position(float theX, float theY) {
		_myX = theX;
		_myY = theY;
	}
	
	public void size(final float theWidth, final float theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
}
