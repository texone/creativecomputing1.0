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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.filter.CCGaussianBlur;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.util.CCStopWatch;
import cc.creativecomputing.util.logging.CCLog;


public class CCTextureMapFont extends CCFont<CCTextureMapChar>{
	
	protected CCTexture2D _myFontTexture;
	
	
	CCTextureMapFont(CCFontSettings theFontSettings) {
		super(theFontSettings);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#createCharArray(int)
	 */
	@Override
	protected CCTextureMapChar[] createCharArray(int theSize) {
		return new CCTextureMapChar[theSize];
	}

	@Override
	protected void createChars(BufferedImage theCharImage, Graphics2D theGraphics) {
		float myBlurRadius = _mySettings == null ? 0 : _mySettings.blurRadius();
		
		int myCharsPerRow = (int)(CCMath.sqrt(_myCharCount)*1.5f);
		int index = 0;

		// calculate width and height for the texture
		int myTextureWidth = 0;
		int myTextureHeight = 0;
		int myCurrentLineWidth = 0;
		int mySpaceAdd = (int)(0.2f * _mySize);
		int counter = 0;

		for (int i = 0; i < _myCharCount; i++){
			if(counter >= myCharsPerRow){
				myTextureWidth = CCMath.max(myTextureWidth, myCurrentLineWidth);
				myCurrentLineWidth = 0;
				myTextureHeight += _myHeight * 1.25f + myBlurRadius * 2;
				counter = 0;
			}
			char c = _myCharSet.chars()[i];
			
			if(!_myFont.canDisplay(c) || _myFontMetrics.charWidth(c) <= 0){
				continue;
			}
			
			myCurrentLineWidth += (float)_myFontMetrics.charWidth(c)+mySpaceAdd+ myBlurRadius * 2;
			counter++;
		}
		myTextureHeight += _myHeight;
		
		BufferedImage myCharImage = new BufferedImage(myTextureWidth, myTextureHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D myGraphics = (Graphics2D) myCharImage.getGraphics();
		
		myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, _myIsAntialiase ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		myGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		myGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		myGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		myGraphics.setFont(_myFont);
//		myGraphics.setBackground(Color.cyan);
//		myGraphics.clearRect(0, 0, (int)myTextureWidth, (int)myTextureHeight);
		myGraphics.setColor(Color.white);
		
		int myX = (int)myBlurRadius;
		int myY = (int)myBlurRadius;
		
		// array passed to createGylphVector
		char textArray[] = new char[1];
		final FontRenderContext frc = myGraphics.getFontRenderContext();
		

		counter = 0;
		for (int i = 0; i < _myCharCount; i++){
			if(counter >= myCharsPerRow){
				myX = (int)myBlurRadius;
				counter = 0;
				myY += _myHeight * 1.25f + myBlurRadius * 2;
			}
			char c = _myCharSet.chars()[i];
			
			if(!_myFont.canDisplay(c) || _myFontMetrics.charWidth(c) <= 0){
				continue;
			}

			if (c < 128){
				_myAsciiLookUpTable[c] = index;
			}
			
			_myCharCodes[index] = c;
			
			textArray[0] = c;
			final GlyphVector myGlyphVector = _myFont.createGlyphVector(frc,textArray);
			final Rectangle myPixelBounds = myGlyphVector.getPixelBounds(frc, myX, myY + _myFontMetrics.getAscent());
//			CCLog.info(c);
//			CCLog.info(
//				myPixelBounds.getMinX() + " " + 
//				myPixelBounds.getMinY() + " " + 
//				myPixelBounds.getMaxX() + " " + 
//				myPixelBounds.getMaxY() + " " + 
//				myPixelBounds.getWidth() + " " + 
//				myPixelBounds.getHeight()
//			);
//			CCLog.info(
//				(myX - myBlurRadius) + " " + 
//				(myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f - _myHeight - myBlurRadius) + " " + 
//				(myX + myBlurRadius + (float)_myFontMetrics.charWidth(c)) + " " + 
//				(myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f + myBlurRadius)
//			);
			
			float myXOffset = myX - (float)myPixelBounds.getMinX();
			float myYOffset = (myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f - _myHeight) - (float)myPixelBounds.getMinY();
			
			_myChars[index] = new CCTextureMapChar(
				c, 
				myGlyphVector.getGlyphCode(0), 
				-myXOffset,
				myYOffset,
				(float)myPixelBounds.getWidth() / _mySize, 
				(float)myPixelBounds.getHeight() / _mySize,
				charWidth(c),
				_myHeight,
				new CCVector2f(
					(myPixelBounds.getMinX() - myBlurRadius) / (float)myTextureWidth,
					1 - (myPixelBounds.getMinY() - myBlurRadius) / (float)myTextureHeight),
				new CCVector2f(
					(myPixelBounds.getMaxX() + myBlurRadius) / (float)myTextureWidth,
					1 - (myPixelBounds.getMaxY() + myBlurRadius) / (float)myTextureHeight
				),
				myBlurRadius / _mySize
			);
			myGraphics.drawString(String.valueOf(c), myX, myY + _myFontMetrics.getAscent());
//			myGraphics.drawRect((int)myX, (int)myY, _myFontMetrics.charWidth(c) - 1, _myFontMetrics.getAscent() + _myFontMetrics.getDescent()-1);
			myX += _myFontMetrics.charWidth(c) + mySpaceAdd + myBlurRadius * 2;

			index++;
			counter++;
		}
		
		if(myBlurRadius > 0) {
			CCGaussianBlur myBlur = new CCGaussianBlur(myBlurRadius);
			myCharImage = myBlur.filter(myCharImage);
		}
		
		_myCharCount = index;
		
		_myAscent = _myFontMetrics.getAscent();
		_myDescent = _myFontMetrics.getDescent();
		
		_myLeading = _myFontMetrics.getLeading();
		
		_myNormalizedAscent = (float)_myFontMetrics.getAscent() / _mySize;
		_myNormalizedDescent = (float)_myDescent / _mySize;
		
		try{
			_myFontTexture = new CCTexture2D(CCTextureIO.newTextureData(myCharImage));
			_myFontTexture.textureFilter(CCTextureFilter.LINEAR);
		}catch (Exception theException){
			// TODO Auto-generated catch block
			theException.printStackTrace();
			throw new RuntimeException(theException);
		}

	}
	
	@Override
	public void beginText(CCGraphics g){
		g.texture(_myFontTexture);
		g.beginShape(CCDrawMode.QUADS);
	};
	
	@Override
	public void endText(CCGraphics g){
		g.endShape();
		g.noTexture();
	};

	public CCTextureMapChar[] chars(){
		return _myChars;
	}
	
	public CCTexture texture(){
		return _myFontTexture;
	}
}
