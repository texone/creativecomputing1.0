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

import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;

/**
 * A Vector2D is a point in a 2D system.
 * @author tex
 *
 */
@CCPropertyObject(name = "vector1f")
public class CCVector1i{
	
	/**
	 * Minimum possible int value
	 */
	static final int minInt = Integer.MIN_VALUE;
	
	@CCProperty(name="x", node=false)
	public int x;
	
	/**
	 * Initializes a new Vector by two coordinates
	 * @param theX
	 * @param theY
	 */
	public CCVector1i(final int theX){
		x = theX;
	}


	/**
	 * Initializes a new vector by setting x and y to 0
	 */
	public CCVector1i(){
		this(0);
	}
	
	public CCVector1i(final CCVector1i theVector){
		this(theVector.x);
	}
	
	/**
	 * Returns the x coordinate of the vector
	 * @return
	 */
	public int x(){
		return x;
	}
	
	/**
	 * Sets the x coordinate of the vector
	 * @param theX
	 */
	public void x(final int theX){
		x = theX;
	}

	/**
	 * Sets the vector to the given vector
	 * @param theVector3f
	 */
	public void set(final CCVector1i theVector){
		x = theVector.x;
	}

	/**
	 * Sets the coords of this vector to the given coords
	 * @param theX int, new x coord of the vector
	 * @param theY int, new y coord of the vector
	 */
	public void set(final int theX){
		x = theX;
	}


	/**
	 * Sets x and y to zero
	 */
	public void setZero(){
		set(0);
	}

	/**
	 * Compares the Vector and the given one
	 * @param theVector Vector1f, vector to compare
	 * @return boolean, true if both vectors are equal 
	 */
	public boolean equals(final CCVector1i theVector){
		return x == theVector.x;
	}
	
	public boolean equals(final CCVector1i theVector, final int theTolerance){
		if(CCMath.abs(x - theVector.x) > theTolerance)return false;
		return true;
	}

	/**
	 * Calculates the length of the vector, the length is given by
	 * the magnitude.
	 * @return
	 */
	public int length(){
		return x;
	}

	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return int, the length of the vector squared
	 */
	public int lengthSquared(){
		return x() * x();
	}

	/**
	 * algorithm for faster calculation of the vector length.
	 * for Vector2d it only calls length, only implemented
	 * for a complete Vector Implementation
	 * @return int, the approximate length of the vector
	 */
	public int approximateLength(){
		return length();
	}
	
	/**
	 * Returns the distance between this vector and the one passed as a parameter
	 * @param theVector Vector1f, the vector to get the distance to
	 * @return int, the distance between the vector and the given one
	 */
	public int distance(final CCVector1i theVector){
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
	 * @param theX int, x coord to add
	 */
	public CCVector1i add(final int theX){
		x += theX;
		return this;
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x and y coordinates.
	 * @param theVector Vector1f, vector to be added
	 */
	public CCVector1i add(final CCVector1i theVector){
		return add(theVector.x);
	}

	/**
	 * Subtracts the given coords to this vector, by subtracting the
	 * x and y coordinates.
	 * @param theX x coord to subtract
	 */
	public CCVector1i subtract(final int theX){
		x -= theX;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x and y coordinates.
	 * @param theVector Vector1f, vector to subtract
	 */
	public CCVector1i subtract(final CCVector1i theVector){
		return subtract(theVector.x);
	}

	/**
	 * Scales this vector with the given factor.
	 * @param theScale int, factor to scale the vector
	 */
	public CCVector1i scale(final int theScale){
		x *= theScale;
		return this;
	}

	/**
	 * Returns positive 1 if theVector is clockwise of this vector,
	 * minus -1 if anti clockwise (Y axis pointing down, X axis to right)
	 * @param theVector
	 * @return
	 */
	public int sign(final CCVector1i theVector){
		if (x < 0){
			return -1;
		}else{
			return 1;
		}
	}

	/**
	 * Returns a clone of this Vector
	 */
	public CCVector1i clone(){
		return new CCVector1i(x);
	}

	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector1i["+x+"]";
	}
}
