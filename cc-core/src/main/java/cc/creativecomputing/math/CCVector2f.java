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

import java.util.Random;

import cc.creativecomputing.animation.CCBlendModifier;
import cc.creativecomputing.animation.CCBlendable;
import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;

/**
 * A Vector2D is a point in a 2D system.
 * @author tex
 *
 */
@CCPropertyObject(name = "vector2f")
public class CCVector2f implements CCBlendable<CCVector2f>{
	
	/**
	 * Utility for random 
	 */
	static final Random generator = new Random();
	
	/**
	 * Minimum possible float value
	 */
	static final float minFloat = Float.MIN_VALUE;
	
	@CCProperty(name="x", node=false)
	public float x;
	
	@CCProperty(name="y", node=false)
	public float y;
	
	/**
	 * Initializes a new Vector by two coordinates
	 * @param theX
	 * @param theY
	 */
	public CCVector2f(final float theX, final float theY){
		x = theX;
		y = theY;
	}
	
	public CCVector2f(final double theX, final double theY){
		x = (float)theX;
		y = (float)theY;
	}


	/**
	 * Initializes a new vector by setting x and y to 0
	 */
	public CCVector2f(){
		this(0, 0);
	}
	
	public CCVector2f(final CCVector2f theVector){
		this(theVector.x, theVector.y);
	}
	
	public CCVector2f(final CCVector2d theVector){
		this(theVector.x, theVector.y);
	}

	/**
	 * Sets the vector to the given vector
	 * @param theVector3f
	 */
	public void set(final CCVector2f theVector){
		x = theVector.x;
		y = theVector.y;
	}

	/**
	 * Sets the coords of this vector to the given coords
	 * @param theX float, new x coord of the vector
	 * @param theY float, new y coord of the vector
	 */
	public CCVector2f set(final float theX, final float theY){
		x = theX;
		y = theY;
		return this;
	}
	
	/**
	 * Sets the vector using values in the polar format.
	 * @param theLength
	 * @param theDirection
	 */
	public void setPolar(final float theLength, final float theDirection){
		x = (float)(theLength*Math.cos(theDirection));
		y = (float)(theLength*Math.sin(theDirection));
	}


	/**
	 * Sets x and y to zero
	 */
	public void setZero(){
		set(0.0F, 0.0F);
	}

	/**
	 * Compares the Vector and the given one
	 * @param theVector Vector2f, vector to compare
	 * @return boolean, true if both vectors are equal 
	 */
	public boolean equals(final CCVector2f theVector){
		return x == theVector.x && y == theVector.y;
	}
	
	public boolean equals(final CCVector2f theVector, final float theTolerance){
		if(CCMath.abs(x - theVector.x) > theTolerance)return false;
		if(CCMath.abs(y - theVector.y) > theTolerance)return false;
		return true;
	}
	
	public boolean isNAN() {
		return Float.isNaN(x) || Float.isNaN(y);
	}
	
	/**
	 * Returns true if both x and y are zero
	 * @return boolean, true if the vector is zero
	 */
	public boolean equalsZero(){
		return lengthSquared() > minFloat;
	}

	/**
	 * Calculates the length of the vector, the length is given by
	 * the magnitude.
	 * @return
	 */
	public float length(){
		return (float) Math.sqrt(lengthSquared());
	}

	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return float, the length of the vector squared
	 */
	public float lengthSquared(){
		return x * x + y * y;
	}

	/**
	 * algorithm for faster calculation of the vector length.
	 * for Vector2d it only calls length, only implemented
	 * for a complete Vector Implementation
	 * @return float, the approximate length of the vector
	 */
	public float approximateLength(){
		return length();
	}
	
	/**
     * Calculate the angle between this and the given Vector, using the dot product
     * @param v2 another vector
     * @return the angle between the vectors
     */ 
    public float angle(final CCVector2f theVector) {
        float dot = dot(theVector);
        float theta = CCMath.acos(dot / (length() * theVector.length()));
        if(theta == Float.NaN)return 0;
        return theta;
    }
    
    public float angle(final float theX, final float theY){
    	return angle(new CCVector2f(theX, theY));
    }

	/**
	 * Returns the dot product of two vectors. The dot
	 * product is the cosinus of the angle between two
	 * vectors
	 * @param theVector, the other vector
	 * @return float, dot product of two vectors 
	 */
	public float dot(final CCVector2f theVector){
		return x * theVector.x + y * theVector.y;
	}

	/**
	 * Returns the cross product of a vector. The
	 * cross product returns a vector standing vertical
	 * on the given vector.
	 * @param theVector the vector
	 * @return the cross product
	 */
	public CCVector2f cross(){
		return new CCVector2f(y, -x);
	}

	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public void randomize(){
		do{
			x = generator.nextFloat() * 2.0F - 1.0F;
			y = generator.nextFloat() * 2.0F - 1.0F;
		}while (lengthSquared() > 1.0F);
	}
	
	/**
	 * Returns the distance between this vector and the one passed as a parameter
	 * @param theVector Vector2f, the vector to get the distance to
	 * @return float, the distance between the vector and the given one
	 */
	public float distance(final CCVector2f theVector){
		float myX = x - theVector.x;
		float myY = y - theVector.y;
		return CCMath.sqrt(myX * myX + myY * myY);
	}
	
	/**
	 * Returns the distance between this vector and th one passed as a parameter
	 * @param theVector Vector2f, the vector to get the distance to
	 * @return float, the distance between the vector and the given one
	 */
	public float distance(final CCVector3f theVector){
		float myX = x - theVector.x;
		float myY = y - theVector.y;
		return CCMath.sqrt(myX * myX + myY * myY);
	}

	/**
	 * Returns the distance between this vector and the one passed as a parameter
	 * as square. This often prevents calculating the square root.
	 * @param theVector Vector2f, the vector to get the distance to
	 * @return float, the distance between the vector and the given one
	 */
	public float distanceSquared(final CCVector2f theVector){
		float myX = x - theVector.x;
		float myY = y - theVector.y;
		return myX * myX + myY * myY;
	}

	/**
	 * unary minus operation for all coords of the vector
	 */
	public void negate(){
		scale(-1);
	}

	/**
	 * Adds the given coords to this vector, by adding the
	 * x and y coordinates.
	 * @param theX float, x coord to add
	 * @param theY float, y coord to add
	 */
	public CCVector2f add(final float theX, final float theY){
		x += theX;
		y += theY;
		return this;
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x and y coordinates.
	 * @param theVector Vector2f, vector to be added
	 */
	public CCVector2f add(final CCVector2f theVector){
		return add(theVector.x, theVector.y);
	}

	/**
	 * Subtracts the given coords to this vector, by subtracting the
	 * x and y coordinates.
	 * @param theX float, x coord to subtract
	 * @param theY float, y coord to subtract
	 */
	public CCVector2f subtract(final float theX, final float theY){
		x -= theX;
		y -= theY;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x and y coordinates.
	 * @param theVector Vector2f, vector to subtract
	 */
	public CCVector2f subtract(final CCVector2f theVector){
		return subtract(theVector.x, theVector.y);
	}

	/**
	 * Scales this vector with the given factor.
	 * @param theScale float, factor to scale the vector
	 */
	public CCVector2f scale(final float theScale){
		x *= theScale;
		y *= theScale;
		return this;
	}
	
	public CCVector2f scale(final float theXscale,final float theYscale){
		x *= theXscale;
		y *= theYscale;
		return this;
	}
	
	/**
	 * Norms the vector to the length of 1
	 *
	 */
	public CCVector2f normalize(){
		float m = length();
		if (m != 0.0F)
			scale(1.0F / m);
		return this;
	}
	
	/**
	 * Sets the vector to the given length
	 * @param theNewLength
	 * @return
	 */
	public CCVector2f normalize(final float theNewLength){
		float m = length();
		if (m != 0.0F){
			scale(theNewLength / m);
		}
		return this;
	}

	/**
	 * Interpolates between this vector and the given vector
	 * by a given blend value. The blend value has to be between 0
	 * and 1. A blend value 0 would change nothing, a blend value 1
	 * would set this vector to the given one.
	 * @param blend float, blend value for interpolation
	 * @param theVector Vector2f, other vector for interpolation
	 */
	public void interpolate(final float blend, final CCVector2f theVector){
		x = theVector.x + blend * (x - theVector.x);
		y = theVector.y + blend * (y - theVector.y);
	}
	
	/**
	 * clamps the length of a given vector to maxLength.  If the vector is 
	 * shorter its value is returned unaltered, if the vector is longer
	 * the value returned has length of maxLength and is parallel to the
	 * original input.
	 * @param theTreshhold float, maximum length to vector is set to.
	 */
	public void truncate(final float theTreshhold){
		float length = approximateLength();
		if (length > theTreshhold)
			scale(theTreshhold / length);
	}
	
	/**
	 * given a vector, return a vector perpendicular to it.
	 */
	public CCVector2f normal(){
		return new CCVector2f(-y, x);
	}

	/**
	 * Returns positive 1 if theVector is clockwise of this vector,
	 * minus -1 if anti clockwise (Y axis pointing down, X axis to right)
	 * @param theVector
	 * @return
	 */
	public int sign(final CCVector2f theVector){
		if (y * theVector.x > x * theVector.y){
			return -1;
		}else{
			return 1;
		}
	}
	
	/**
	 * Given a normalized vector this method reflects the vector it
	 * is operating upon. (like the path of a ball bouncing off a wall)
	 * @param theVector
	 */
	public void reflect(final CCVector2f theVector){
		final CCVector2f distTemp = clone();
		float dot = distTemp.dot(theVector);
		distTemp.negate();
		distTemp.scale(2 * dot);
		add(distTemp);
	}
	
	public void rotate(float r){
		float xprev = x;
		float yprev = y;
		
		final float sinR = (float)Math.sin(r);
		final float cosR = (float)Math.cos(r);

		x = cosR * xprev + sinR * yprev;
		y = -sinR * xprev + cosR * yprev;
	}
	
	public float direction(){
		return (float)Math.atan2(y, x);
	}

	/**
	 * Returns a clone of this Vector
	 */
	public CCVector2f clone(){
		return new CCVector2f(x, y);
	}

	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector2f["+x+":"+y+"]";
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.animation.CCBlendable#blend(float, java.lang.Object)
	 */
	public void blend(float theBlend, CCVector2f theStart, CCVector2f theTarget) {
		x = CCMath.blend(theStart.x, theTarget.x, theBlend);
		y = CCMath.blend(theStart.y, theTarget.y, theBlend);
	}
	
	private CCBlendModifier _myModifier;
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.animation.CCBlendable#modifier(cc.creativecomputing.newui.animation.CCBlendModifier)
	 */
	public void modifier(CCBlendModifier theModifier) {
		if(_myModifier != null)_myModifier.onReplace();
		_myModifier = theModifier;
	}
}
