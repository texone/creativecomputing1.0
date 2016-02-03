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
package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.math.CCAABoundingRectangle;

/**
 * @author info
 *
 */
public abstract class CCAbstractShape implements CCDrawListener<CCGraphics>{
	
	protected float _myScale = 1;
	protected CCColor _myColor;
	
	protected CCShapeMode _myShapeMode;
	protected CCAABoundingRectangle _myBoundingRectangle;
	
	protected boolean _myIsVisible = true;
	protected boolean _myHasTexture = false;
	
	protected CCAbstractShape(){
		_myShapeMode = CCShapeMode.CORNER;
		_myBoundingRectangle = new CCAABoundingRectangle();
	}
	
	public void draw(CCGraphics g) {
	}

	/**
	 * Moves the shape by the given amount
	 * @param theX
	 * @param theY
	 */
	public void translate(float theX, float theY) {
	}
	
	public abstract void position(final float theX, final float theY);
	
	public void scale(final float theScale){
		_myScale = theScale;
	}
	
	public void shapeMode(final CCShapeMode theShapeMode){
		_myShapeMode = theShapeMode;
	}
	
	/**
	 * Return the bounding rectangle surrounding the shape. The
	 * rectangle is axis aligned. 
	 * @return the rectangle surrounding the shape
	 */
	public CCAABoundingRectangle boundingRect(){
		return _myBoundingRectangle;
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public void isVisible(final boolean theIsVisible){
		_myIsVisible = theIsVisible;
	}
	
	public void hasTexture(final boolean theHasTexture){
		_myHasTexture = theHasTexture;
	}
	
	public boolean hasTexture(){
		return _myHasTexture;
	}
}
