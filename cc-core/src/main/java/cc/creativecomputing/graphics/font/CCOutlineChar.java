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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;


public class CCOutlineChar extends CCChar{

	private final List<List<CCVector2f>>_myContour = new ArrayList<List<CCVector2f>>();
	private List<CCVector2f>_myPath;
	private float _mySize;
	
	/**
	 * @param theChar
	 * @param theWidth
	 */
	CCOutlineChar(char theChar, int theGlyphCode, float theWidth, float theHeight, float theSize) {
		super(theChar, theGlyphCode, theWidth, theHeight);
		_mySize = theSize;
	}
	
	public void beginPath(){
		_myPath = new ArrayList<CCVector2f>();
	}
	
	public void addVertex(final CCVector2f theVector){
		_myPath.add(theVector);
	}
	
	public void endPath(){
		_myContour.add(_myPath);
	}
	
	public List<List<CCVector2f>> contour(){
		return _myContour;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCChar#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public float draw(CCGraphics g, float theX, float theY, float theZ, float theSize) {
		final float myWidth = _myWidth * theSize;
		
		final float myScale = theSize / _mySize;
		
		for(List<CCVector2f> myPath:_myContour){
			g.beginShape(CCDrawMode.LINE_LOOP);
			for(CCVector2f myVertex:myPath){
				g.vertex(
					theX + myVertex.x * myScale, 
					theY - myVertex.y * myScale,
					theZ
				);
			}
			g.endShape();
		}
		
		return myWidth;
	}
}
