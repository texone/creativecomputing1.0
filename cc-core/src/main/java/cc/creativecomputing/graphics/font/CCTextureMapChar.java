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
package cc.creativecomputing.graphics.font;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;

public class CCTextureMapChar extends CCChar{
	private final CCVector2f _myMin;
	private final CCVector2f _myMax;
	private final float _myBlurRadius;
	private final float _myDrawWidth;
	private final float _myDrawHeight;
	private final float _myXOffset;
	private final float _myYOffset;
	
	protected CCTextureMapChar(
		final char theChar, 
		final int theGlyphCode, 
		final float theX,
		final float theY,
		final float theDrawWidth,
		final float theDrawHeight,
		final float theWidth, 
		final float theHeight, 
		final CCVector2f theMin, 
		final CCVector2f theMax, 
		float theBlurRadius
	){
		super(theChar, theGlyphCode, theWidth, theHeight);
		_myXOffset = theX;
		_myYOffset = theY;
		_myDrawWidth = theDrawWidth;
		_myDrawHeight = theDrawHeight;
		_myMin = theMin;
		_myMax = theMax;
		_myBlurRadius = theBlurRadius;
	}

	@Override
	public float draw(CCGraphics g, float theX, float theY, float theZ, float theSize) {
		final float myBlurRadius = _myBlurRadius * theSize;
		final float myWidth = _myDrawWidth * theSize;
		final float myHeight = _myDrawHeight * theSize;
		g.vertex(theX - myBlurRadius + _myXOffset,			 theY + myBlurRadius + _myYOffset,			   _myMin.x, _myMin.y);
		g.vertex(theX + myBlurRadius + _myXOffset + myWidth, theY + myBlurRadius + _myYOffset,			   _myMax.x, _myMin.y);
		g.vertex(theX + myBlurRadius + _myXOffset + myWidth, theY - myBlurRadius + _myYOffset - myHeight,  _myMax.x, _myMax.y);
		g.vertex(theX - myBlurRadius + _myXOffset,			 theY - myBlurRadius + _myYOffset - myHeight,  _myMin.x, _myMax.y);
		
		return myWidth;
	}
	
	public CCVector2f min(){
		return _myMin;
	}
	
	public CCVector2f max(){
		return _myMax;
	}
}
