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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.util.CCTriangulator;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;


public class CCOutlineFont extends CCFont<CCOutlineChar> {
	
	private final int _myDetail;

	CCOutlineFont(CCFontSettings theSettings) {
		super(theSettings);
		
		_myDetail = theSettings.detail();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#createCharArray(int)
	 */
	@Override
	protected CCOutlineChar[] createCharArray(int theSize) {
		return new CCOutlineChar[theSize];
	}

	@Override
	protected void createChars(final BufferedImage theCharImage, final Graphics2D theGraphics) {
		
		// six element array received from the Java2D path iterator
		float textPoints[] = new float[6];

		// array passed to createGylphVector
		char textArray[] = new char[1];

		final Graphics2D myGraphics = theGraphics;
		final FontRenderContext frc = myGraphics.getFontRenderContext();

		int index = 0;
		
		for (int i = 0; i < _myCharCount; i++) {
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
			final Shape myShape = myGlyphVector.getOutline();
			final PathIterator myPathIterator = myShape.getPathIterator(null,0.05);
			
			final CCOutlineChar myOutlineChar = new CCOutlineChar(c, myGlyphVector.getGlyphCode(0),charWidth(c), height(), _mySize);

			CCVector2f _myLastPosition = null;

			while (!myPathIterator.isDone()) {
				int type = myPathIterator.currentSegment(textPoints);
				switch (type) {
					case PathIterator.SEG_MOVETO: // 1 point (2 vars) in textPoints
						myOutlineChar.beginPath();
					case PathIterator.SEG_LINETO: // 1 point
						CCVector2f myNewPoint = new CCVector2f(textPoints[0], textPoints[1]  + _myFontMetrics.getAscent());
						
//						if(_myLastPosition != null){
//							float dist = myNewPoint.distance(_myLastPosition);
//							
//							if(dist > myMinSpace){
//								int divisions = CCMath.ceil(dist / myMinSpace);
//								CCVector2f add = CCVecMath.subtract(myNewPoint, _myLastPosition);
//								add.scale(1f / divisions);
//								
//								for(int j = 0; j < divisions - 1;j++){
//									myOutlineChar.addVertex(new CCVector2f(
//										_myLastPosition.x + add.x,
//										_myLastPosition.y() + add.y()
//									));
//								}
//							}
//						}
						
						
						myOutlineChar.addVertex(myNewPoint);

						_myLastPosition = new CCVector2f(textPoints[0], textPoints[1] + _myFontMetrics.getAscent());
						break;
	
					case PathIterator.SEG_QUADTO: // 2 points
						for (int j = 1; j < _myDetail; j++) {
							float t = (float) j / _myDetail;
							myOutlineChar.addVertex(new CCVector2f(
								CCMath.bezierPoint(_myLastPosition.x, textPoints[0], textPoints[2], textPoints[2], t), 
								CCMath.bezierPoint(_myLastPosition.y, textPoints[1], textPoints[3], textPoints[3], t) + _myFontMetrics.getAscent()
							));
						}
	
						_myLastPosition.x = textPoints[2];
						_myLastPosition.y = textPoints[3];
						break;
	
					case PathIterator.SEG_CUBICTO: // 3 points

						for (int j = 1; j < _myDetail; j++) {
							float t = (float) j / _myDetail;
							myOutlineChar.addVertex(new CCVector2f(
								CCMath.bezierPoint(_myLastPosition.x, textPoints[0], textPoints[2], textPoints[4], t), 
								CCMath.bezierPoint(_myLastPosition.y, textPoints[1], textPoints[3], textPoints[5], t) + _myFontMetrics.getAscent()
							));
						}
	
						_myLastPosition.x = textPoints[4];
						_myLastPosition.y = textPoints[5];
						break;
	
					case PathIterator.SEG_CLOSE:
						myOutlineChar.endPath();
						break;
				}

				myPathIterator.next();
			}
			_myChars[index] = myOutlineChar;
			index++;
		}
	}
	
	/**
	 * The outline for a char consists of different paths, for example an "e"
	 * would have to paths one for the letter and one for the hole inside the letter.
	 * This function merges these different paths so that you loose the exact contour
	 * information and returns one List containing all the Points that define the letter.
	 * @param theText text you want to get the path for
	 * @param theTextAlign align of the text, this changes the alignment of the returned points
	 * @param theTextSize size of the text
	 * @param theX x coord for the text output
	 * @param theY y coord for the text output
	 * @param theZ z coord for the text output
	 * @return list with all the points defining the contour of the given text
	 * @example texone.org.cc.test.graphics.font.CCOutlineTextPath
	 */
	public List<CCVector3f> getPath(
		final String theText, final CCTextAlign theTextAlign, final float theTextSize,
		float theX, final float theY, final float theZ
	){
		final char[] myTextBuffer = theText.toCharArray();
		if (theTextAlign == CCTextAlign.CENTER){
			theX -= width(myTextBuffer, 0, myTextBuffer.length) / 2f * theTextSize;

		}else if (theTextAlign == CCTextAlign.RIGHT){
			theX -= width(myTextBuffer, 0, myTextBuffer.length) * theTextSize;
		}

		final List<CCVector3f> myResult = new ArrayList<CCVector3f>();
		
		for (int index = 0; index < myTextBuffer.length; index++){
			final char myChar = myTextBuffer[index];
			switch(myChar){
				case ' ':
					theX += spaceWidth() * theTextSize;
					break;
				default:
					final int myIndex = index(myChar);
					if(myIndex < 0)continue;
				
					final CCOutlineChar myOutlineChar = _myChars[myIndex];
					final float myWidth = myOutlineChar.width() * theTextSize;
				
					final float myScale = theTextSize/_mySize;
				
					final List<List<CCVector2f>> _myContour = myOutlineChar.contour();
					for(List<CCVector2f> myPath:_myContour){
						for(CCVector2f myVertex:myPath){
							myResult.add(new CCVector3f(
								theX + myVertex.x * myScale, 
								theY - myVertex.y * myScale,
								theZ
							));
						}
						CCVector2f myVertex = myPath.get(0);
						myResult.add(new CCVector3f(
								theX + myVertex.x * myScale, 
								theY - myVertex.y * myScale,
								theZ
							));
					}
					
				
					theX +=  myWidth;
			}
		}
		return myResult;
	}
	
	public List<CCVector3f> getPath(
		final char theChar, final CCTextAlign theTextAlign, final float theTextSize, 
		float theX, final float theY, final float theZ
	){
		return getPath(new String(new char[]{theChar}), theTextAlign, theTextSize,theX, theY, theZ);
	}
	
	public List<CCVector3f> getPath(
		final char theChar, 
		float theX, final float theY, final float theZ
	){
		return getPath(theChar, CCTextAlign.LEFT, _mySize, theX, theY, theZ);
	}
	
	public CCMesh createMesh(
		final String theText, final CCTextAlign theTextAlign, final float theTextSize,
		float theX, final float theY, final float theZ
	){
		
		final char[] myTextBuffer = theText.toCharArray();
		if (theTextAlign == CCTextAlign.CENTER){
			theX -= width(myTextBuffer, 0, myTextBuffer.length) / 2f * theTextSize;
		}else if (theTextAlign == CCTextAlign.RIGHT){
			theX -= width(myTextBuffer, 0, myTextBuffer.length) * theTextSize;
		}

		final CCTriangulator myTriangulator = new CCTriangulator();
			
		for (int index = 0; index < myTextBuffer.length; index++){
			final char myChar = myTextBuffer[index];
			switch(myChar){
				case ' ':
					theX += spaceWidth() * theTextSize;
					break;
				default:
					final int myIndex = index(myChar);
					if(myIndex < 0)continue;
					
					final CCOutlineChar myOutlineChar = _myChars[myIndex];
					final float myWidth = width(myIndex) * theTextSize;
					
					final float myScale = theTextSize/_mySize;
					
					final List<List<CCVector2f>> _myContour = myOutlineChar.contour();
					myTriangulator.beginPolygon();
					for(List<CCVector2f> myPath:_myContour){
						myTriangulator.beginContour();
						for(CCVector2f myVertex:myPath){
							myTriangulator.vertex(new CCVector3f(
								theX + myVertex.x * myScale, 
								theY - myVertex.y * myScale,
								theZ
							));
						}
						myTriangulator.endContour();
					}
					myTriangulator.endPolygon();
							
					theX +=  myWidth;
			}
		}
		
		CCMesh myMesh = new CCVBOMesh();
		myMesh.vertices(myTriangulator.vertices());
		return myMesh;
	}
}
