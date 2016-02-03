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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;

/**
 * Axis aligned bounding rectangle similar to a bounding box but for 2D context.
 * @author christian riekoff
 *
 */
@CCPropertyObject(name="bounding_rect")
public class CCAABoundingRectangle{

	/**
	 * minimum corner of the rectangle
	 */
	@CCProperty(name="minimum")
	private CCVector2f _myMinCorner;
	
	/**
	 * maximum corner of the rectangle
	 */
	@CCProperty(name="minimum")
	private CCVector2f _myMaxCorner;
	
	/**
	 * Creates a new rectangle using the given color and coordinates.
	 * @param theColor
	 * @param theX1
	 * @param theY1
	 * @param theX2
	 * @param theY2
	 */
	public CCAABoundingRectangle(final float theX1, final float theY1, final float theX2, final float theY2){
		float myMinX = CCMath.min(theX1, theX2);
		float myMinY = CCMath.min(theY1, theY2);

		float myMaxX = CCMath.max(theX1, theX2);
		float myMaxY = CCMath.max(theY1, theY2);
		
		_myMinCorner = new CCVector2f(myMinX, myMinY);
		_myMaxCorner = new CCVector2f(myMaxX, myMaxY);
	}
	
	/**
	 * Creates a new rectangle using the given coordinates.
	 * @param theColor
	 * @param theMinCorner
	 * @param theMaxCorner
	 */
	public CCAABoundingRectangle(final CCVector2f theMinCorner, final CCVector2f theMaxCorner) {
		this(theMinCorner.x, theMinCorner.y, theMaxCorner.x, theMaxCorner.y);
	}
	
	public CCAABoundingRectangle() {
		this(new CCVector2f(), new CCVector2f());
	}
	
	public void add(final CCAABoundingRectangle theRectangle){
		_myMinCorner.x = CCMath.min(_myMinCorner.x, theRectangle._myMinCorner.x);
		_myMinCorner.y = CCMath.min(_myMinCorner.y, theRectangle._myMinCorner.y);

		_myMaxCorner.x = CCMath.max(_myMaxCorner.x, theRectangle._myMaxCorner.x);
		_myMaxCorner.y = CCMath.max(_myMaxCorner.y, theRectangle._myMaxCorner.y);
	}
	
	/**
	 * Changes the size of the bounding rect so that the given point is inside of it
	 * @param theX x coord of the point
	 * @param theY y coord of the point
	 */
	public void add(final float theX, final float theY){
		_myMinCorner.x = CCMath.min(_myMinCorner.x, theX);
		_myMinCorner.y = CCMath.min(_myMinCorner.y, theY);

		_myMaxCorner.x = CCMath.max(_myMaxCorner.x, theX);
		_myMaxCorner.y = CCMath.max(_myMaxCorner.y, theY);
	}
	
	/**
	 * Changes the size of the bounding rect so that the given point is inside of it
	 * @param thePoint the point
	 */
	public void add(final CCVector2f thePoint){
		add(thePoint.x, thePoint.y);
	}
	
	/**
	 * Checks if the given position is inside this rectangle
	 * @param thePosition the position to check
	 * @return <code>true</code> if the given position is inside the rectangle, otherwise <code>false</code>
	 */
	public boolean isInside(final CCVector2f thePosition) {
		return isInside(thePosition.x, thePosition.y);
	}
	
	/**
	 * Checks if the given position is inside this rectangle
	 * @param theX x coord of the position to check
	 * @param theY y coord of the position to check
	 * @return <code>true</code> if the given position is inside the rectangle, otherwise <code>false</code>
	 */
	public boolean isInside(final float theX, final float theY) {
		return 
			theX > _myMinCorner.x && 
			theX < _myMaxCorner.x &&
			theY > _myMinCorner.y && 
			theY < _myMaxCorner.y;
	}
	
	/**
	 * Checks if the given bounding rect collides with this bounding rect.
	 * @param theBounds
	 * @return
	 */
	public boolean isColliding(final CCAABoundingRectangle theBounds) {
		return 
			theBounds.min().x < max().x &&
			theBounds.max().x > min().x &&
			theBounds.min().y < max().y &&
			theBounds.max().y > min().y;
			
	}
	
	/**
	 * Moves the rectangle to the given position
	 * @param theX 
	 * @param theY
	 */
	public void position(final float theX, final float theY) {
		float myWidth = width();
		float myHeight = height();
		_myMinCorner.set(theX, theY);
		_myMaxCorner.set(theX, theY);
		_myMaxCorner.add(myWidth, myHeight);
	}
	
	/**
	 * Moves the rectangle to the given position
	 * @param theX 
	 * @param theY
	 */
	public void position(final CCVector2f thePosition) {
		position(thePosition.x, thePosition.y);
	}
	
	/**
	 * Returns the x coord of the position.
	 * @return x coord of the position.
	 */
	public float x() {
		return _myMinCorner.x;
	}
	
	/**
	 * Returns the y coord of the position.
	 * @return y coord of the position.
	 */
	public float y() {
		return _myMinCorner.y;
	}
	
	/**
	 * Returns the width of the rectangle
	 * @return
	 */
	public float width() {
		return _myMaxCorner.x - _myMinCorner.x;
	}
	
	/**
	 * Sets the width of the rectangle. Changing the width of the
	 * rectangle causes a change of the maximum (upper right) corner.
	 * @param theWidth the new width
	 */
	public void width(final float theWidth){
		_myMaxCorner.x = _myMinCorner.x;
		_myMaxCorner.add(theWidth,0);
	}
	
	/**
	 * Returns the height of the rectangle
	 * @return
	 */
	public float height() {
		return _myMaxCorner.y - _myMinCorner.y;
	}
	
	/**
	 * Sets the height of the rectangle. Changing the height of the
	 * rectangle causes a change of the maximum (upper right) corner.
	 * @param theHeight the new height
	 */
	public void height(final float theHeight){
		_myMaxCorner.y = _myMinCorner.y;
		_myMaxCorner.add(0, theHeight);
	}
	
	/**
	 * Returns the width and height of the rectangle as vector.
	 * @return width and height of the rectangle
	 */
	public CCVector2f size() {
		return _myMaxCorner.clone().subtract(_myMinCorner);
	}
	
	/**
	 * Returns the minimum corner (lower left) of the rectangle.
	 * @return minimum corner
	 */
	public CCVector2f min() {
		return _myMinCorner;
	}
	
	/**
	 * Returns the maximum corner (upper right) of the rectangle.
	 * @return maximum corner
	 */
	public CCVector2f max() {
		return _myMaxCorner;
	}
	
	/**
	 * Returns the center of the rectangle
	 * @return center of the rectangle
	 */
	public CCVector2f center(){
		return CCVecMath.add(_myMinCorner, _myMaxCorner).scale(0.5f);
	}
	
	/**
	 * Returns a copy of the rectangle
	 */
	public CCAABoundingRectangle clone() {
		return new CCAABoundingRectangle(_myMinCorner.clone(), _myMaxCorner.clone());
	}
	
	/**
	 * Returns a string representation of the rectangle
	 */
	public String toString() {
		StringBuilder myStringBuilder = new StringBuilder("CCRectangle:\n");
		myStringBuilder.append("minCorner:");
		myStringBuilder.append(_myMinCorner.toString());
		myStringBuilder.append("\n");
		myStringBuilder.append("maxCorner:");
		myStringBuilder.append(_myMaxCorner.toString());
		myStringBuilder.append("\n");
		return myStringBuilder.toString();
	}

	/**
	 * moves 
	 */
	public void translate(float theX, float theY) {
		_myMinCorner.add(theX, theY);
		_myMaxCorner.add(theX, theY);
	}
	
	public void scale(float theXscale, float theYscale) {
		_myMinCorner.scale(theXscale, theYscale);
		_myMaxCorner.scale(theXscale, theYscale);
	}

	public void draw(CCGraphics g) {
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(_myMinCorner.x, _myMinCorner.y);
		g.vertex(_myMaxCorner.x, _myMinCorner.y);
		g.vertex(_myMaxCorner.x, _myMaxCorner.y);
		g.vertex(_myMinCorner.x, _myMaxCorner.y);
		g.endShape();
	}
}
