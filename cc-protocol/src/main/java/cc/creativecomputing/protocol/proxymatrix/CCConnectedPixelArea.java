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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;

public class CCConnectedPixelArea {
	private CCConnectedPixelArea _myParent;

	private List<CCConnectedPixelRow> _myConnectedPixelRows = new ArrayList<CCConnectedPixelRow>();
	
	private CCBox2i _myBBox;

	public CCConnectedPixelArea() {}

	public CCConnectedPixelArea(final CCConnectedPixelRow theRows) {
		_myConnectedPixelRows.add(theRows);
	}

	/**
	 * Returns the center of this area
	 * @return
	 */
	public CCVector2f center() {
		CCVector2f d = new CCVector2f(0.0f, 0.0f);
		int c = 0;

		for (CCConnectedPixelRow myPixelRow : _myConnectedPixelRows) {
			d.add(myPixelRow.center());
			c++;
		}
		return d.scale(1f / c);
	}
	
	/**
	 * Returns the maximum value inside this pixel area for the given raster.
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
		float myY = 0;
		
		CCVector2i myTemp = new CCVector2i();
		
		for(CCConnectedPixelRow myRow:_myConnectedPixelRows) {
			float value = myRow.max(theRaster, myTemp);
			
			if(value > myMax) {
				myMax = value;

				myCountMaxs = 1;
				myX = myTemp.x();
				myY = myTemp.y();
				if(thePosition != null)thePosition.set(myTemp);
			}else if(value == myMax) {
				myCountMaxs++;
				myX += myTemp.x();
				myY += myTemp.y();
			}
		}
		
		
		
		if(thePosition != null)thePosition.set((int)(myX / myCountMaxs),(int)(myY / myCountMaxs));
		return myMax;
	}

	/**
	 * Returns the area of this pixel area. This is the number of connected pixels.
	 * @return number of pixels inside this area
	 */
	public int area() {
		int res = 0;
		for (CCConnectedPixelRow myRun : _myConnectedPixelRows) {
			res += myRun.length();
		}
		return res;
	}

	CCBox2i bbox() {
		if(_myBBox == null) {
			int x1 = Integer.MAX_VALUE;
			int y1 = Integer.MAX_VALUE;
	
			int x2 = 0;
			int y2 = 0;
	
			for (CCConnectedPixelRow myRun : _myConnectedPixelRows) {
				x1 = CCMath.min(x1, myRun.startX());
				y1 = CCMath.min(y1, myRun.y());
				x2 = CCMath.max(x2, myRun.endX());
				y2 = CCMath.max(y2, myRun.y());
			}
			_myBBox = new CCBox2i(x1, y1, x2 + 1, y2 + 1);
		}
		return _myBBox;
	}
	
	public boolean hasParent() {
		return _myParent != null;
	}
	
	public CCConnectedPixelArea parent() {
		return _myParent;
	}
	
	public void parent(CCConnectedPixelArea theArea) {
		_myParent = theArea;
	}

	/**
	 * Merges this and the given pixel area, by adding all pixel rows.
	 * @param theOtherArea the area to merge with this one
	 */
	public void merge(CCConnectedPixelArea theOtherArea) {
		_myBBox = null;
		_myConnectedPixelRows.addAll(theOtherArea._myConnectedPixelRows);
	}

	/**
	 * Returns a list with all connected pixel rows defining this area.
	 * @return list with all connected pixel rows defining this area.
	 */
	public List<CCConnectedPixelRow> connectedPixelRows() {
		return _myConnectedPixelRows;
	}
	
	/**
	 * Draws a debugging view of this pixel area
	 * @param g
	 */
	public void draw(CCGraphics g) {
		g.color(255,100);
		for (CCConnectedPixelRow myPixelRow : _myConnectedPixelRows) {
			g.rect(myPixelRow.startX(), myPixelRow.y(), myPixelRow.length(), 1);
		}
		g.polygonMode(CCPolygonMode.LINE);
		g.color(255,0,0);
		CCBox2i myBBox = bbox();
		g.rect(myBBox.minX(), myBBox.minY(), myBBox.maxX() - myBBox.minX(), myBBox.maxY() - myBBox.minY());
		g.polygonMode(CCPolygonMode.FILL);
//		g.color(255,0,0);
//		g.ellipse(center(),5);
	}

};
