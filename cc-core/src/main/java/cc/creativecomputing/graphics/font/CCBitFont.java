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

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCBitFont extends CCTextureMapFont{
	
	private CCTextureData _myData;

	public CCBitFont(CCTextureData theData, int theDescent) {
		super(null);
		_myData = theData;
		_myCharCount = 255;
		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myHeight = _mySize = theData.height();
		_myNormalizedHeight = 1;
		
		_myAscent = _myHeight - theDescent;
		_myDescent = theDescent;
//		
		_myNormalizedAscent = (float)_myAscent / _mySize;
		_myNormalizedDescent = (float)_myDescent / _mySize;
		
		_myLeading = _myHeight + 2;
		
		createChars();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#index(char)
	 */
	@Override
	public int index(char theChar) {
		int c = (int) theChar;
		return c - 32;
	}

	protected void createChars() {
		
		_mySize = _myData.height();
			      
		int index = 0;

		int myX = 0;
		
		// array passed to createGylphVector
		
		int myLastX = 0;
		
		for (int x = 0; x < _myData.width(); x++) {

        	myX++;
        	
        	boolean myApplyCut = !_myData.getPixel(x, _myData.height() - 1).equals(CCColor.RED);

        	for(int y = 0; y < _myData.height(); y++) {
        		if(_myData.getPixel(x, y).equals(CCColor.BLACK)) {
        			_myData.setPixel(x, y, CCColor.WHITE);
        		}else {
        			_myData.setPixel(x, y, CCColor.TRANSPARENT);
        		}
        	}
        	
	        if (myApplyCut) {
	        	continue;
	        }
	        
	        
	        
	        float myCharWidth = myX - myLastX;
	        
	        if(index == 0) {
	    		_mySpaceWidth = myCharWidth / _mySize;
	        }
	        
	        char c = (char)(index + 32);
			
			_myCharCodes[index] = c;
			
			
//			_myChars[index] = new CCTextureMapChar(c, -1, myCharWidth / _mySize, height(),
//				new CCVector2f(
//					myLastX / (float)_myData.width(),
//					1f
//				),
//				new CCVector2f(
//					myX / (float)_myData.width(),
//					0
//				),
//				0
//			);
			myLastX = myX;

			index++;
		}
		
		_myCharCount = index;
		
//		_myAscent = _myFontMetrics.getAscent();
//		_myDescent = _myFontMetrics.getDescent();
//		
//		_myLeading = _myFontMetrics.getLeading();
//		_mySpacing = _myFontMetrics.getHeight();
//		
//		_myNormalizedAscent = (float)_myFontMetrics.getAscent() / _mySize;
//		_myNormalizedDescent = (float)_myDescent / _mySize;
		_myFontTexture = new CCTexture2D(_myData);
		_myFontTexture.textureFilter(CCTextureFilter.NEAREST);
	}
	
	
}
