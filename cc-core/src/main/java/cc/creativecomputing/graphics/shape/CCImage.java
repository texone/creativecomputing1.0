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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.math.CCAABoundingRectangle;

public class CCImage extends CCRectangle{

	private CCTexture _myTexture;
	
	public CCImage(final CCTexture theTexture, final float theX, final float theY, final float theWidth, final float theHeight){
		super(theX, theY, theWidth, theHeight, new CCColor(255));
		_myTexture = theTexture;
	}
	
	public CCImage(final CCTexture theTexture, final float theX, final float theY){
		super(theX, theY, theTexture.width(), theTexture.height(), new CCColor(255));
		_myTexture = theTexture;
	}
	
	@Override
	public boolean hasTexture() {
		return true;
	}
	
	@Override
	public CCAABoundingRectangle boundingRect() {
		float myX1 = _myX;
		float myY1 = _myY;
		
		switch(_myShapeMode){
		case CENTER:
			myX1 -= _myWidth * _myScale / 2;
			myY1 -= _myHeight * _myScale / 2;
			break;
		default:
			break;
		}
		
		return new CCAABoundingRectangle(myX1, myY1, myX1 + _myWidth * _myScale, myY1 + _myHeight * _myScale);
	}
	
	public void draw(CCGraphics g){
		if(_myColor != null)g.color(_myColor);
		
		float myX1 = _myX;
		float myY1 = _myY;
		
		switch(_myShapeMode){
		case CENTER:
			myX1 -= _myWidth * _myScale / 2;
			myY1 -= _myHeight * _myScale / 2;
			break;
		default:
			break;
		}
		
		g.image(_myTexture, myX1, myY1, _myWidth * _myScale, _myHeight * _myScale);
	}
}
