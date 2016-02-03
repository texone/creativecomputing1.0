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
package cc.creativecomputing.math.d;

import java.util.Random;

import cc.creativecomputing.math.CCVector2f;


/**
 * A Vector2D is a point in a 2D system.
 * @author tex
 *
 */
public class CCVector2d{
	
	/**
	 * Utility for random 
	 */
	static final Random generator = new Random();
	
	/**
	 * Minimum possible double value
	 */
	static final double minFloat = Float.MIN_VALUE;
	
	public double x;
	public double y;
	
	/**
	 * Initializes a new Vector by two coordinates
	 * @param theX
	 * @param theY
	 */
	public CCVector2d(final double theX, final double theY){
		x = theX;
		y = theY;
	}


	/**
	 * Initializes a new vector by setting x and y to 0
	 */
	public CCVector2d(){
		this(0, 0);
	}
	
	public CCVector2d(final CCVector2d theVector){
		this(theVector.x, theVector.y);
	}
	
	public CCVector2d(final CCVector3d theVector){
		this(theVector.x, theVector.y);
	}
	
	public CCVector2d(final CCVector2f theVector){
		this(theVector.x, theVector.y);
	}


	/**
	 * Sets the vector to the given vector
	 * @param theVector3f
	 */
	public void set(final CCVector2d theVector){
		x = theVector.x;
		y = theVector.y;
	}

	/**
	 * Sets the coords of this vector to the given coords
	 * @param theX double, new x coord of the vector
	 * @param theY double, new y coord of the vector
	 */
	public void set(final double theX, final double theY){
		x = theX;
		y = theY;
	}
	
	/**
	 * Sets the vector using values in the polar format.
	 * @param theLength
	 * @param theDirection
	 */
	public void setPolar(final double theLength, final double theDirection){
		x = (theLength*Math.cos(theDirection));
		y = (theLength*Math.sin(theDirection));
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
	public boolean equals(final CCVector2d theVector){
		return x == theVector.x && y == theVector.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CCVector2d))return false;
		else return equals((CCVector2d)obj);
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
	public double length(){
		return  Math.sqrt(lengthSquared());
	}

	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return double, the length of the vector squared
	 */
	public double lengthSquared(){
		return x * x + y * y;
	}

	/**
	 * algorithm for faster calculation of the vector length.
	 * for Vector2d it only calls length, only implemented
	 * for a complete Vector Implementation
	 * @return double, the approximate length of the vector
	 */
	public double approximateLength(){
		return length();
	}

	/**
	 * Returns the dot product of two vectors. The dot
	 * product is the cosinus of the angle between two
	 * vectors
	 * @param theVector, the other vector
	 * @return double, dot product of two vectors 
	 */
	public double dot(final CCVector2d theVector){
		return x * theVector.x + y * theVector.y;
	}

	/**
	 * Returns the cross product of a vector. The
	 * cross product returns a vector standing vertical
	 * on the given vector.
	 * @param theVector the vector
	 * @return the cross product
	 */
	public CCVector2d cross(){
		return new CCVector2d(y, -x);
	}

	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector2d randomize(){
		do{
			x = generator.nextDouble() * 2.0 - 1.0;
			y = generator.nextDouble() * 2.0 - 1.0;
		}while (lengthSquared() > 1.0);
		return this;
	}
	
	/**
	 * Returns the distance between this vector and th one passed as a parameter
	 * @param theVector Vector2f, the vector to get the distance to
	 * @return double, the distance between the vector and the given one
	 */
	public double distance(final CCVector2d theVector){
		double dx = x - theVector.x;
		double dy = y - theVector.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Returns the distance between this vector and th one passed as a parameter
	 * @param theX the x coord of the vector to get the distance to
	 * @param theY the x coord of the vector to get the distance to
	 * @return double, the distance between the vector and the given one
	 */
	public double distance(final double theX, final double theY){
		double dx = x - theX;
		double dy = y - theY;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Returns the distance between this vector and th one passed as a parameter
	 * @param theVector Vector2f, the vector to get the distance to
	 * @return double, the distance between the vector and the given one
	 */
	public double distance(final CCVector3d theVector){
		double dx = x - theVector.x;
		double dy = y - theVector.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Returns the distance between this vector and th one passed as a parameter
	 * as square. This often prevents calculating the squareroot.
	 * @param theVector Vector2f, the vector to get the distance to
	 * @return double, the distance between the vector and the given one
	 */
	public double distanceSquared(final CCVector2d theVector){
		double dx = x - theVector.x;
		double dy = y - theVector.y;
		return dx * dx + dy * dy;
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
	 * @param theX double, x coord to add
	 * @param theY double, y coord to add
	 */
	public CCVector2d add(final double theX, final double theY){
		x += theX;
		y += theY;
		return this;
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x and y coordinates.
	 * @param theVector Vector2f, vector to be added
	 */
	public CCVector2d add(final CCVector2d theVector){
		return add(theVector.x, theVector.y);
	}

	/**
	 * Subtracts the given coords to this vector, by subtracting the
	 * x and y coordinates.
	 * @param theX double, x coord to subtract
	 * @param theY double, y coord to subtract
	 */
	public CCVector2d subtract(final double theX, final double theY){
		x -= theX;
		y -= theY;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x and y coordinates.
	 * @param theVector Vector2f, vector to subtract
	 */
	public CCVector2d subtract(final CCVector2d theVector){
		return subtract(theVector.x, theVector.y);
	}

	/**
	 * Scales this vector with the given factor.
	 * @param theScale double, factor to scale the vector
	 */
	public CCVector2d scale(final double theScale){
		x *= theScale;
		y *= theScale;
		return this;
	}
	
	public CCVector2d scale(final double theXscale,final double theYscale){
		x *= theXscale;
		y *= theYscale;
		return this;
	}
	
	/**
	 * Norms the vector to the length of 1
	 *
	 */
	public CCVector2d normalize(){
		double m = length();
		if (m != 0.0F)
			scale(1.0F / m);
		return this;
	}

	/**
	 * Interpolates between this vector and the given vector
	 * by a given blend value. The blend value has to be between 0
	 * and 1. A blend value 0 would change nothing, a blend value 1
	 * would set this vector to the given one.
	 * @param blend double, blend value for interpolation
	 * @param theVector Vector2f, other vector for interpolation
	 */
	public void interpolate(final double blend, final CCVector2d theVector){
		x = theVector.x + blend * (x - theVector.x);
		y = theVector.y + blend * (y - theVector.y);
	}
	
	/**
	 * clamps the length of a given vector to maxLength.  If the vector is 
	 * shorter its value is returned unaltered, if the vector is longer
	 * the value returned has length of maxLength and is parallel to the
	 * original input.
	 * @param theTreshhold double, maximum length to vector is set to.
	 */
	public void truncate(final double theTreshhold){
		double length = approximateLength();
		if (length > theTreshhold)
			scale(theTreshhold / length);
	}
	
	/**
	 * given a vector, return a vector perpendicular to it.
	 */
	public CCVector2d normal(){
		return new CCVector2d(-y, x);
	}

	/**
	 * Returns positive 1 if theVector is clockwise of this vector,
	 * minus -1 if anti clockwise (Y axis pointing down, X axis to right)
	 * @param theVector
	 * @return
	 */
	public int sign(final CCVector2d theVector){
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
	public void reflect(final CCVector2d theVector){
		final CCVector2d distTemp = clone();
		double dot = distTemp.dot(theVector);
		distTemp.negate();
		distTemp.scale(2 * dot);
		add(distTemp);
	}
	
	public void rotate(double r){
		double xprev = x;
		double yprev = y;
		
		final double sinR = Math.sin(r);
		final double cosR = Math.cos(r);

		x = cosR * xprev + sinR * yprev;
		y = -sinR * xprev + cosR * yprev;
	}
	
	public double direction(){
		return Math.atan2(y, x);
	}

	/**
	 * Returns a clone of this Vector
	 */
	public CCVector2d clone(){
		return new CCVector2d(x, y);
	}
	
	/**
	 * Returns a float version of this Vector
	 */
	public CCVector2f toFloat(){
		return new CCVector2f(x, y);
	}

	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector2d["+x+":"+y+"]";
	}
}
