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
package cc.creativecomputing.protocol.proxymatrix;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;

public class CCConnectedPixelRow {
	private static int _ourLastLabel = 0;

	private int _myY;
	private int _myStartX;
	private int _myEndX;
	private int _myLabel;

	public CCConnectedPixelRow(final int theY, final int theStartX, final int theEndX) {
		_myY = theY;
		_myStartX = theStartX;
		_myEndX = theEndX;

		_myLabel = ++_ourLastLabel;
	}
	
	/**
	 * Checks if this and the given pixel row are connected. Two rows are connected
	 * if they have at least two neighboring pixels.
	 * @param theRow the row to check
	 * @return <code>true</code> if this and the given pixel row are connected
	 */
	public boolean isConnectedWith(CCConnectedPixelRow theRow) {
		boolean res = false;
		if (CCMath.abs(theRow._myY - _myY) != 1)
			return false;
		if (_myStartX > theRow._myStartX) {
			// use > here to do 8-connectivity
			res = theRow._myEndX >= _myStartX;
		} else {
			res = _myEndX >= theRow._myStartX;
		}
		return res;
	}

	/**
	 * Center of this row
	 * @return Center of this row
	 */
	public CCVector2f center() {
		return new CCVector2f(_myStartX + length() * 0.5f, _myY);
	}
	
	/**
	 * Returns the maximum value inside this pixel row for the given raster.
	 * You can optionally get the position of the maximum value by passing a reference
	 * to a vector. If this vector is null it will be ignored.
	 * @param theRaster the raster to check for the maximum value
	 * @param thePosition reference to store the position of the maximum
	 * @return maximum value inside the pixel row
	 */
	public float max(CCPixelRaster theRaster, CCVector2i thePosition) {
		float myMax = 0;
		int myCountMaxs = 0;
		float myX = 0;

		for(int x = _myStartX; x <= _myEndX;x++) {
			float value = theRaster.get(x, _myY);
			if(value > myMax) {
				myMax = value;
				myCountMaxs = 1;
				myX = x;
			}else if(value == myMax) {
				myCountMaxs++;
				myX += x;
			}
		}
		if(thePosition != null)thePosition.set((int)(myX / myCountMaxs),_myY);
		return myMax;
	}

	/**
	 * Number of connected pixels inside this row
	 * @return Number of connected pixels inside this row
	 */
	public int length() {
		return _myEndX - _myStartX + 1;
	}

	public void draw(CCGraphics g) {
		
	}
	
	public int label() {
		return _myLabel;
	}
	
	public int startX() {
		return _myStartX;
	}
	
	public int endX() {
		return _myEndX;
	}
	
	public int y() {
		return _myY;
	}
}
