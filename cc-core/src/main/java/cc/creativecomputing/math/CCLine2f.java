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
package cc.creativecomputing.math;

import cc.creativecomputing.graphics.CCGraphics;

public class CCLine2f {

	protected CCVector2f _myStart;
	protected CCVector2f _myEnd;
	

	public CCLine2f(final CCVector2f theStart, final CCVector2f theEnd) {
		_myStart = new CCVector2f(theStart);
		_myEnd = new CCVector2f(theEnd);
	}

	public CCLine2f(final float theStartX, final float theStartY, final float theEndX, final float theEndY) {
		this(new CCVector2f(theStartX, theStartY), new CCVector2f(theEndX, theEndY));
	}

	public CCLine2f(final CCLine2f theSegment) {
		this(theSegment._myStart, theSegment._myEnd);
	}

	public CCLine2f() {
		this(0, 0, 0, 0);
	}

	/**
	 * @return the start
	 */
	public CCVector2f start() {
		return _myStart;
	}

	/**
	 * @return the end
	 */
	public CCVector2f end() {
		return _myEnd;
	}

	public float length() {
		return _myStart.distance(_myEnd);
	}
	
	@Override
	public boolean equals(final Object theSegment) {
		if(!(theSegment instanceof CCLine2f))return false;
		
		CCLine2f mySegment = (CCLine2f)theSegment;
		return 
			mySegment.start().equals(start()) && mySegment.end().equals(end()) ||
			mySegment.start().equals(end()) && mySegment.end().equals(start());
	}
	
	public String toString() {
		return "start:"+_myStart+"\nend:"+_myEnd;
	}

	public void draw(CCGraphics g) {
		g.line(_myStart, _myEnd);
	}
}
