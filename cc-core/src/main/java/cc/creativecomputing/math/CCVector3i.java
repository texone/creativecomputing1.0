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

import java.io.Serializable;
import java.util.Random;



/**
 * This class represents a vector in 3D Space.
 * Vectors are mathematical constructs used to do 2D and 3D math. 
 * You can describe the vector in two ways mathematically or geometrically.
 * From the mathematic view a vector is nothing more than a list of numbers.
 * In physics vectors are used to describe quantities that have a direction.
 * The numbers a vector have are maintained by its dimension. In 
 * computer graphics you mainly working with 2D and 3D Vectors. Here the
 * numbers of the vector describe the x,y or z coordinate in the corresponding
 * coordinate space. In the geometric interpretation the vector is described by 
 * a direction and a magnitude.
 * <p>
 * Be aware that although a vector can describe a position in a coordinate space
 * it does not have a position. For the first time this seems to be a little
 * tricky but remember a vector can not only describe positions in a coordinate
 * space but also physic quantities. A force for example can be described by its
 * x, y and z direction, without having a position. 
 * </p>
 * @author tex
 *
 */
public class CCVector3i implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8793451086395145586L;
	
	/**
	 * Utility for random 
	 */
	static final Random generator = new Random();
	
	public int x;
	public int y;
	public int z;
	
	
	/**
	 * 
	 */
	public CCVector3i(){
		this(0,0,0);
	}
	
	public CCVector3i(final CCVector2i theVector){
		this(theVector.x,theVector.y(),0);
	}
	
	public CCVector3i(final CCVector3i theVector){
		this(theVector.x,theVector.y(),theVector.z());
	}

	public CCVector3i(
		final int i_x,
		final int i_y,
		final int i_z
	){
		x = i_x;
		y = i_y;
		z = i_z;
	}

	public CCVector3i(
		final int i_x,
		final int i_y
	){
		this(i_x,i_y,0);
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
	 * @param i_x
	 */
	public void x(final int i_x){
		x = i_x;
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
	 * @param i_y
	 */
	public void y(final int i_y){
		y = i_y;
	}
		
	/**
	 * Returns the x coordinate of the vector
	 * @return
	 */
	public int z(){
		return z;
	}
	
	/**
	 * Sets the x coordinate of the vector
	 * @param i_x
	 */
	public void z(final int i_z){
		z = i_z;
	}
	
	/**
	 * Sets the vector to the given coords
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void set(final int theX, final int theY, final int theZ){
		x = theX;
		y = theY;
		z = theZ;
	}
	
	/**
	 * Sets the vector to the given vector
	 * @param theVector CCVector3f: the vector this vector is set to.
	 */
	public void set(final CCVector3i theVector){
		x = theVector.x;
		y = theVector.y();
		z = theVector.z();
	}
	
	/**
	 * Returns the coords of the vector as float array
	 * @return float[]: the coords of the vector
	 */
	public int[] coords(){
		return new int[]{x,y,z};
	}
	
	/**
	 * Use this method to negate a vector. The result of the
	 * negation is vector with the same magnitude but opposite
	 * direction. Mathematically the negation is the additive
	 * inverse of the vector. The sum of a value and its additive
	 * inverse is always zero.
	 * @shortdesc Use this method to negate a vector.
	 * @related scale ( )
	 */
	public void negate(){
		x(x * -1);
		y(y() * -1);
		z(z() * -1);
	}
	
	/**
	 * Returns the cross product of two vectors. The
	 * cross product returns a vector standing vertical
	 * on the two vectors. The cross product is very usefull to 
	 * calculate the normals for lighting.
	 * @param i_vector the other vector
	 * @return the cross product
	 */
	public CCVector3i cross(final CCVector3i i_vector){
		return new CCVector3i(
			y() * i_vector.z() - z() * i_vector.y(), 
			z() * i_vector.x - x * i_vector.z(), 
			x * i_vector.y() - y() * i_vector.x
		);
	}
	
	/**
	 * Returns the dot product of two vectors. The dot
	 * product is the cosinus of the angle between two
	 * vectors
	 * @param i_vector, the other vector
	 * @return float, dot product of two vectors 
	 */
	public float dot(
		final CCVector3i i_vector
	){
		return 
			x * i_vector.x + 
			y * i_vector.y + 
			z * i_vector.z;
	}
	
	/**
     * Calculate the angle between this and the given Vector, using the dot product
     * @param v2 another vector
     * @return the angle between the vectors
     */ 
    public float angle(final CCVector3i theVector) {
        float dot = dot(theVector);
        float theta = CCMath.acos(dot / (length() * theVector.length()));
        if(theta == Float.NaN)return 0;
        return theta;
    }
    
    
	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return float, the length of the vector squared
	 */
	public int lengthSquared(){
		return x * x + y() * y() + z() * z();
	}
	
	/**
	 * Use this method to calculate the length of a vector, the length of a vector is also
	 * known as its magnitude. Vectors have a magnitude and a direction. These values
	 * are not explicitly expressed in the vector so they have to be computed.
	 * @return float: the length of the vector
	 * @shortdesc Calculates the length of the vector.
	 * @related lengthSquared ( )
	 */
	public float length(){
		return CCMath.sqrt(lengthSquared());
	}
	
	/**
	 * Returns the distance between this and the given vector.
	 * 
	 * @param theVector
	 * @return the distance
	 */
	public float distance(final CCVector3i theVector){
		final CCVector3i result = clone();
		result.subtract(theVector);
		return result.length();
	}
	
	public float distance(final float theX, final float theY, final float theZ){
		final CCVector3i result = clone();
		result.subtract(theX, theY, theZ);
		return result.length();
	}
	
	/**
	 * Returns the square of the distance between this and the given 
	 * vector. This often avoid to calculate the square root.
	 * @param theVector
	 * @return
	 */
	public int distanceSquared(final CCVector3i theVector){
		final CCVector3i result = clone();
		result.subtract(theVector);
		return result.lengthSquared();
	}
	
	/**
	 * Adds the given vector to this vector, by adding the
	 * x, y and z coordinates.
	 * @param i_vector Vector3f, vector to be added
	 */
	public CCVector3i add(final CCVector3i i_vector){
		add(i_vector.x,i_vector.y(),i_vector.z());
		return this;
	}
	
	/**
	 * Adds the given coords to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theX float, x coord to add
	 * @param theY float, y coord to add
	 * @param theZ float, z coord to add
	 */
	public CCVector3i add(final float theX,final float theY, final float theZ){
		x += theX;
		y += theY;
		z += theZ;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x, y and z coordinates.
	 * @param theVector Vector3f, vector to subtract
	 */
	public CCVector3i subtract(final CCVector3i theVector){
		subtract(theVector.x,theVector.y(),theVector.z());
		return this;
	}
	
	/**
	 * Subtracts the given coords to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theX float, x coord to subtract
	 * @param theY float, y coord to subtract
	 * @param theZ float, z coord to subtract
	 */
	public CCVector3i subtract(final float theX, final float theY, final float theZ){
		x -= theX;
		y -= theY;
		z -= theZ;
		return this;
	}
	
	/**
	 * Returns a clone of this Vector
	 */
	public CCVector3i clone(){
		return new CCVector3i(x,y,z);
	}
	
	@Override
	public boolean equals(final Object theObject){
		if(!(theObject instanceof CCVector3i))return false;
		
		CCVector3i myVector = (CCVector3i)theObject;
		
		return myVector.x == x && myVector.y() == y() && myVector.z() == z();
	}
	
	public boolean equals(final CCVector3i theVector, final float theTolerance){
		if(CCMath.abs(x - theVector.x) > theTolerance)return false;
		if(CCMath.abs(y() - theVector.y()) > theTolerance)return false;
		if(CCMath.abs(z() - theVector.z()) > theTolerance)return false;
		return true;
	}
	
	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector3f["+x+":"+y+":"+z+"]";
	}
}
