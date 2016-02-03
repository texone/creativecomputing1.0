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
import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;

/**
 * A Vector2D is a point in a 2D system.
 * @author tex
 *
 */
@CCPropertyObject(name = "vector1f")
public class CCVector1f implements CCBlendable<CCVector1f>{
	
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
	
	/**
	 * Initializes a new Vector by two coordinates
	 * @param theX
	 * @param theY
	 */
	public CCVector1f(final float theX){
		x = theX;
	}


	/**
	 * Initializes a new vector by setting x and y to 0
	 */
	public CCVector1f(){
		this(0);
	}
	
	public CCVector1f(final CCVector1f theVector){
		this(theVector.x);
	}
	
	/**
	 * Returns the x coordinate of the vector
	 * @return
	 */
	public float x(){
		return x;
	}
	
	/**
	 * Sets the x coordinate of the vector
	 * @param theX
	 */
	public void x(final float theX){
		x = theX;
	}

	/**
	 * Sets the vector to the given vector
	 * @param theVector3f
	 */
	public void set(final CCVector1f theVector){
		x = theVector.x;
	}

	/**
	 * Sets the coords of this vector to the given coords
	 * @param theX float, new x coord of the vector
	 * @param theY float, new y coord of the vector
	 */
	public void set(final float theX){
		x = theX;
	}


	/**
	 * Sets x and y to zero
	 */
	public void setZero(){
		set(0.0F);
	}

	/**
	 * Compares the Vector and the given one
	 * @param theVector Vector1f, vector to compare
	 * @return boolean, true if both vectors are equal 
	 */
	public boolean equals(final CCVector1f theVector){
		return x == theVector.x;
	}
	
	public boolean equals(final CCVector1f theVector, final float theTolerance){
		if(CCMath.abs(x - theVector.x) > theTolerance)return false;
		return true;
	}
	
	public boolean isNAN() {
		return Float.isNaN(x);
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
		return x;
	}

	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return float, the length of the vector squared
	 */
	public float lengthSquared(){
		return x() * x();
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
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public void randomize(){
		do{
			x = generator.nextFloat() * 2.0F - 1.0F;
		}while (lengthSquared() > 1.0F);
	}
	
	/**
	 * Returns the distance between this vector and the one passed as a parameter
	 * @param theVector Vector1f, the vector to get the distance to
	 * @return float, the distance between the vector and the given one
	 */
	public float distance(final CCVector1f theVector){
		return CCMath.abs(x - theVector.x);
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
	public CCVector1f add(final float theX){
		x += theX;
		return this;
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x and y coordinates.
	 * @param theVector Vector1f, vector to be added
	 */
	public CCVector1f add(final CCVector1f theVector){
		return add(theVector.x);
	}

	/**
	 * Subtracts the given coords to this vector, by subtracting the
	 * x and y coordinates.
	 * @param theX float, x coord to subtract
	 * @param theY float, y coord to subtract
	 */
	public CCVector1f subtract(final float theX){
		x -= theX;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x and y coordinates.
	 * @param theVector Vector1f, vector to subtract
	 */
	public CCVector1f subtract(final CCVector1f theVector){
		return subtract(theVector.x);
	}

	/**
	 * Scales this vector with the given factor.
	 * @param theScale float, factor to scale the vector
	 */
	public CCVector1f scale(final float theScale){
		x *= theScale;
		return this;
	}

	/**
	 * Interpolates between this vector and the given vector
	 * by a given blend value. The blend value has to be between 0
	 * and 1. A blend value 0 would change nothing, a blend value 1
	 * would set this vector to the given one.
	 * @param blend float, blend value for interpolation
	 * @param theVector Vector1f, other vector for interpolation
	 */
	public void interpolate(final float blend, final CCVector1f theVector){
		x = theVector.x + blend * (x - theVector.x);
	}

	/**
	 * Returns positive 1 if theVector is clockwise of this vector,
	 * minus -1 if anti clockwise (Y axis pointing down, X axis to right)
	 * @param theVector
	 * @return
	 */
	public int sign(final CCVector1f theVector){
		if (x < 0){
			return -1;
		}else{
			return 1;
		}
	}

	/**
	 * Returns a clone of this Vector
	 */
	public CCVector1f clone(){
		return new CCVector1f(x);
	}

	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector1f["+x+"]";
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.animation.CCBlendable#blend(float, java.lang.Object)
	 */
	public void blend(float theBlend, CCVector1f theStart, CCVector1f theTarget) {
		x = CCMath.blend(theStart.x, theTarget.x, theBlend);
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
