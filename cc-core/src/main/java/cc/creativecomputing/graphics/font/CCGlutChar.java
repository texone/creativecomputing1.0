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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author info
 *
 */
public class CCGlutChar extends CCChar{
	
	private final GLUT _myGlut;
	private final int _myFontType;

	/**
	 * @param theChar
	 * @param theWidth
	 */
	CCGlutChar(char theChar, float theWidth, float theHeight, final GLUT theGlut, final int theFontType) {
		super(theChar, -1, theWidth, theHeight);
		_myGlut = theGlut;
		_myFontType = theFontType;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCChar#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public float draw(CCGraphics g, float theX, float theY, float theZ, float theSize) {
		g.gl.glRasterPos3f(theX, theY, theZ);
		_myGlut.glutBitmapCharacter(_myFontType, _myChar);
		return _myGlut.glutBitmapWidth(_myFontType, _myChar);
	}

}
