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
package cc.creativecomputing.graphics.texture;

import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCColor;

/**
 * This class is used for fast pixel transfer when reading
 * or writing pixels from or to a texture. You can set the
 * pixels inside a region of a texture, or get all pixels
 * from the texture at once. This is much faster than using
 * the <code>setPixel()</code> and <code>getPixel()</code>
 * methods of the different texture, as they cause a data
 * transfer for every single pixel while with the pixel map
 * you can reduce this to one transfer for reading a texture
 * region or all pixels.
 * @author christian riekoff
 *
 */
public class CCPixelMap {
	
	private int _myWidth;
	private int _myHeight;
	private boolean _myFlipVertically;
	
	private FloatBuffer _myPixelData;

	/**
	 * Creates a new pixel map with the given width and height
	 * @param theWidth
	 * @param theHeight
	 */
	CCPixelMap(final FloatBuffer thePixelData, final int theWidth, final int theHeight, final boolean theFlipVertically) {
		_myPixelData = thePixelData;
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myFlipVertically = theFlipVertically;
	}
	
	/**
	 * Returns the width of this pixel map 
	 * @return the width of the pixel map
	 */
	public int width() {
		return _myWidth;
	}
	
	/**
	 * Returns the height of this pixel map
	 * @return the height of this pixel map
	 */
	public int height() {
		return _myHeight;
	}
	
	public int size(){
		return _myHeight * _myWidth;
	}
	
	/**
	 * Sets the pixel at the given index to the given color.
	 * @param theX position of the pixel from the left side
	 * @param theY position of the pixel from the top side
	 * @param theColor the new color of the pixel
	 */
	public void setPixel(final int theX, final int theY, final CCColor theColor) {
		
		int nPos = (theY * _myWidth + theX) * 4;
		
		_myPixelData.put(nPos, theColor.red());
		_myPixelData.put(nPos + 1, theColor.green());
		_myPixelData.put(nPos + 2, theColor.blue());
		_myPixelData.put(nPos + 3, theColor.alpha());
	}
	
	/**
	 * Returns the color for the pixel at the given index
	 * @param theX position of the pixel from the left side
	 * @param theY position of the pixel from the top
	 * @return the color of the pixel
	 */
	public CCColor getPixel(final int theX, int theY, CCColor theStore) {
		if(theStore == null){
			theStore = new CCColor();
		}
		if(_myFlipVertically) {
			theY = _myHeight - 1 - theY;
		}
		
		int nPos = (theY * _myWidth + theX);
		
		return getPixel(nPos, theStore);
	}
	
	public CCColor getPixel(final int theX, int theY) {
		return getPixel(theX, theY, null);
	}
	
	public CCColor getPixel(final int theID){
		return getPixel(theID, null);
	}
	
	public CCColor getPixel(final int theID, CCColor theStore){
		if(theStore == null){
			theStore = new CCColor();
		}
		return theStore.set(
			_myPixelData.get(theID * 4),
			_myPixelData.get(theID * 4 + 1),
			_myPixelData.get(theID * 4 + 2),
			_myPixelData.get(theID * 4 + 3)
		);
	}
}
