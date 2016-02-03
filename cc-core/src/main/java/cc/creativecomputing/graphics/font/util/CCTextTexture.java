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
package cc.creativecomputing.graphics.font.util;

import java.awt.Font;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.util.CCTextRenderer.CCTextData;
import cc.creativecomputing.graphics.texture.CCPlacedTexture;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;


public class CCTextTexture extends CCPlacedTexture{
	private int _mySize;
	
	private float _myWidth;
	private float _myHeight;
	
	private float _myRelativeWidth;
	private float _myRelativeHeight;
	private float _myRelativeAscent;
	private float _myRelativeDescent;
	
	private CCVector2f _myRelativeOffset;
	
	private final CCTextRenderer _myTextRenderer;
	private final Font _myFont;
	private final boolean _myIsAntialised;
	
	private CCTextData _myTextData;

	public CCTextTexture(final CCTextRenderer theTextRenderer, final String theText, final Font theFont, final boolean theIsAntialias){
		super();
		
		_myTextRenderer = theTextRenderer;
		_myFont = theFont;
		_myIsAntialised = theIsAntialias;
		
		setText(theText);
	}
	
	public void setText(final String theText){
		_myTextData = _myTextRenderer.createTexture(theText, _myFont, _myIsAntialised);
		
		_mySize = _myTextData.size;
		_myWidth = _myTextData.width;
		_myHeight = _myTextData.height;
		
		_myRelativeAscent = (float)_myTextData.fontMetrics.getAscent() / _mySize;
		_myRelativeDescent = (float)_myTextData.fontMetrics.getDescent() / _mySize;
		_myRelativeWidth = _myWidth / _mySize;
		_myRelativeHeight = _myHeight / _mySize;
		_myRelativeOffset = _myTextData.offset;
		
		_myRelativeOffset.scale(1f / _mySize);
		
		data(_myTextData.textureData);
		_myTextData.textureData.flush();
	}
	
	public CCTextureData textureData(){
		return _myTextData.textureData;
	}
	
	public void draw(final CCGraphics g, final float theX, final float theY){
		draw(g,_mySize,theX,theY,0);
	}
	
	public void draw(
		final CCGraphics g, final float theSize,
		final float theX, final float theY, final float theZ
	){
		final float myWidth = _myRelativeWidth * theSize;
		final float myHeight = _myRelativeHeight * theSize;
		final float myAscent = _myRelativeAscent * theSize;
		
		final float myOffsetX = _myRelativeOffset.x * theSize;
		final float myOffsetY = _myRelativeOffset.y * theSize + myAscent;
			
		g.texture(this);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(theX + myOffsetX,			theY + myOffsetY ,theZ, 0, 1);
		g.vertex(theX + myOffsetX + myWidth,theY + myOffsetY, theZ, 1, 1);
		g.vertex(theX + myOffsetX + myWidth,theY + myOffsetY - myHeight, theZ, 1, 0);
		g.vertex(theX + myOffsetX,			theY + myOffsetY - myHeight, theZ, 0, 0);
		g.endShape();
		g.noTexture();
	}
	
	public void draw(
		final CCGraphics g, final float theSize,
		final float theX, final float theY, final float theZ,
		final float theXH, final float theYH, final float theZH,
		final float theXV, final float theYV, final float theZV
	){
		float xH = theXH * theSize * _myRelativeWidth;
		float yH = theYH * theSize * _myRelativeWidth;
		float zH = theZH * theSize * _myRelativeWidth;
		
		float xV = theXV * theSize * _myRelativeHeight;
		float yV = theYV * theSize * _myRelativeHeight;
		float zV = theZV * theSize * _myRelativeHeight;
		
		float x2 = theX + xH;
		float y2 = theY + yH;
		float z2 = theZ + zH;
		
		float x3 = theX + xH + xV;
		float y3 = theY + yH + yV;
		float z3 = theZ + zH + zV;
		
		float x4 = theX + xV;
		float y4 = theY + yV;
		float z4 = theZ + zV;
		
		g.texture(this);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(theX,theY,theZ,0,0);
		g.vertex(x2,y2,z2,1,0);
		g.vertex(x3,y3,z3,1,1);
		g.vertex(x4,y4,z4,0,1);
		g.endShape();
		g.noTexture();
	}
	
	public void draw(final CCGraphics g, final float theSize,final CCVector3f thePosition, final CCVector3f theH, final CCVector3f theV){
		draw(
			g,theSize,
			thePosition.x,thePosition.y, thePosition.z,
			theH.x, theH.y, theH.z,
			theV.x, theV.y, theV.z
		);
	}
	
	/**
	 * Returns the ascent of the font taken for the text texture.
	 * If you pass no size you get the ascent calculated by the initial size.
	 * Otherwise the ascent is calculated using the passed size.
	 * @shortdesc Returns the ascent of the font taken for the text texture.
	 * @param theSize the size for calculating the ascent
	 * @return the ascent of the font taken for the text texture.
	 */
	public float ascent(final float theSize){
		return _myRelativeAscent * theSize;
	}
	
	public float ascent(){
		return ascent(_mySize);
	}
	
	/**
	 * Returns the descent of the font taken for the text texture.
	 * If you pass no size you get the descent calculated by the initial size.
	 * Otherwise the descent is calculated using the passed size.
	 * @shortdesc Returns the descent of the font taken for the text texture.
	 * @param theSize the size for calculating the descent
	 * @return the descent of the font taken for the text texture.
	 */
	public float descent(final float theSize){
		return _myRelativeDescent * theSize;
	}
	
	public float descent(){
		return descent(_mySize);
	}
	
	public CCVector2f offset(){
		return new CCVector2f(_myRelativeOffset.x * _mySize,_myRelativeOffset.y * _mySize);
	}
}
