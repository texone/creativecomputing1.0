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

/**
 * @author christianriekoff
 *
 */
public class CCBox2i {
	
	private int _myMinX;
	private int _myMinY;
	
	private int _myMaxX;
	private int _myMaxY;

	CCBox2i(int x1, int y1, int x2, int y2){
		_myMinX = x1;
		_myMinY = y1;
		
		_myMaxX = x2;
		_myMaxY = y2;
	}
	
	public int minX() {
		return _myMinX;
	}
	
	public int minY() {
		return _myMinY;
	}
	
	public int maxX() {
		return _myMaxX;
	}
	
	public int maxY() {
		return _myMaxY;
	}
}
