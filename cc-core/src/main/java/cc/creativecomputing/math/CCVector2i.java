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

import cc.creativecomputing.math.d.CCVector2d;

public class CCVector2i implements Cloneable{
	/**
	 * Utility for random 
	 */
	static final Random generator = new Random();
	
	public int x;
	public int y;
	
	/**
	 * Initializes a new Vector by two coordinates
	 * @param theX
	 * @param theY
	 */
	public CCVector2i(final int theX, final int theY){
		x = theX;
		y = theY;
	}


	/**
	 * Initializes a new vector by setting x and y to 0
	 */
	public CCVector2i(){
		this(0, 0);
	}
	
	public CCVector2i(final CCVector2i theVector){
		this(theVector.x, theVector.y);
	}
	
	public CCVector2i(final CCVector2f theVector){
		this((int)theVector.x, (int)theVector.y);
	}
	
	public CCVector2i(final CCVector2d theVector){
		this((int)theVector.x, (int)theVector.y);
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
	 * Returns the y coordinate of the vector
	 * @return
	 */
	public int y(){
		return y;
	}
	
	/**
	 * Sets the y coordinate of the vector
	 * @param theY
	 */
	public void y(final int theY){
		y = theY;
	}

	/**
	 * Sets the vector to the given vector
	 * @param theVector
	 */
	public void set(final CCVector2i theVector){
		x = theVector.x;
		y = theVector.y;
	}

	/**
	 * Sets the coords of this vector to the given coords
	 * @param theX int, new x coord of the vector
	 * @param theY int, new y coord of the vector
	 */
	public void set(final int theX, final int theY){
		x = theX;
		y = theY;
	}


	/**
	 * Sets x and y to zero
	 */
	public void setZero(){
		set(0, 0);
	}

	/**
	 * Compares the Vector and the given one
	 * @param theVector Vector2f, vector to compare
	 * @return boolean, true if both vectors are equal 
	 */
	public boolean equals(final CCVector2i theVector){
		return x == theVector.x && y == theVector.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CCVector2i))return false;
		else return equals((CCVector2i)obj);
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
	 * @param theY int, y coord to add
	 */
	public void add(final int theX, final int theY){
		x += theX;
		y += theY;
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x and y coordinates.
	 * @param theVector Vector2f, vector to be added
	 */
	public void add(final CCVector2i theVector){
		add(theVector.x, theVector.y);
	}

	/**
	 * Subtracts the given coords to this vector, by subtracting the
	 * x and y coordinates.
	 * @param theX x coord to subtract
	 * @param theY coord to subtract
	 */
	public void subtract(final int theX, final int theY){
		x -= theX;
		y -= theY;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x and y coordinates.
	 * @param theVector vector to subtract
	 */
	public void subtract(final CCVector2i theVector){
		subtract(theVector.x, theVector.y);
	}

	/**
	 * Scales this vector with the given factor.
	 * @param theScale int, factor to scale the vector
	 */
	public void scale(final int theScale){
		x *= theScale;
		y *= theScale;
	}
	
	public void scale(final int xFactor,final int yFactor){
		x *= xFactor;
		y *= yFactor;
	}
	
	/**
	 * given a vector, return a vector perpendicular to it.
	 */
	public CCVector2i normal(){
		return new CCVector2i(-y, x);
	}

	/**
	 * Returns a clone of this Vector
	 */
	public CCVector2i clone(){
		return new CCVector2i(x, y);
	}

	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector2i["+x+":"+y+"]";
	}

	public int hashCode() {
		int result;
		result = x;
		result = 31 * result + y;
		return result;
	}
}
