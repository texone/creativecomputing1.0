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

import java.io.Serializable;
import java.util.Random;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.xml.CCXMLElement;



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
public class CCVector3d implements Cloneable, Serializable{
	
	protected double ALMOST_THRESHOLD = 0.001;
	
	public static CCVector3d UP = new CCVector3d(0, 1, 0);
	public static CCVector3d RIGHT = new CCVector3d(1, 0, 0);
	public static CCVector3d OUT = new CCVector3d(0, 0, 1);
	public static CCVector3d ZERO = new CCVector3d(0, 0, 0);
//	const Vector3 Vector3::X = Vector3(0, 1, 0);
//	const Vector3 Vector3::Y = Vector3(1, 0, 0);
//	const Vector3 Vector3::Z = Vector3(0, 0, 1);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8793451086395145586L;
	
	public double x;
	public double y;
	public double z;
	
	/**
	 * Utility for random 
	 */
	static final Random generator = new Random();
	
	
	/**
	 * 
	 */
	public CCVector3d(){
		this(0,0,0);
	}
	
	public CCVector3d(final CCVector2d theVector){
		this(theVector.x,theVector.y,0);
	}
	
	public CCVector3d(final CCVector2d theVector, final double theZ){
		this(theVector.x,theVector.y,theZ);
	}
	
	public CCVector3d(final CCVector3d theVector){
		this(theVector.x,theVector.y,theVector.z);
	}
	
	public CCVector3d(final CCVector3f theVector){
		this(theVector.x,theVector.y,theVector.z);
	}

	public CCVector3d(final double theX, final double theY, final double theZ){
		x = theX;
		y = theY;
		z = theZ;
	}

	public CCVector3d(final double theX, final double theY){
		this(theX,theY,0);
	}
	
	public CCVector3d(final double...theCoords){
		x = theCoords[0];
		y = theCoords[1];
		z = theCoords[2];
	}

	/**
	 * Returns the xy coordinates of the vector as new 2d vector.
	 * @return xy coordinates of the vector as new 2d vector.
	 */
	public CCVector2d xy(){
		return new CCVector2d(x,y);
	}
	
	/**
	 * Sets the vector to the given coords
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public CCVector3d set(final double theX, final double theY, final double theZ){
		x = theX;
		y = theY;
		z = theZ;
		return this;
	}
	
	/**
	 * Sets the vector to the given vector the Z coord will be
	 * set to zero.
	 * @param theVector CCVector3f: the vector this vector is set to.
	 */
	public CCVector3d set(final CCVector2d theVector){
		x = theVector.x;
		y = theVector.y;
		z = 0;
		return this;
	}
	
	/**
	 * Sets the vector to the given vector
	 * @param theVector CCVector3f: the vector this vector is set to.
	 */
	public CCVector3d set(final CCVector3d theVector){
		x = theVector.x;
		y = theVector.y;
		z = theVector.z;
		return this;
	}
	
	public CCVector3d set(final double[] theCoords){
		x = theCoords[0];
		y = theCoords[1];
		z = theCoords[2];
		return this;
	}
	
	/**
	 * Interpolates between this vector and the given vector
	 * by a given blend value. The blend value has to be between 0
	 * and 1. A blend value 0 would change nothing, a blend value 1
	 * would set this vector to the given one.
	 * @param blend double, blend value for interpolation
	 * @param theVector CCVector3f, other vector for interpolation
	 */
	public void interpolate(final double blend, final CCVector3d theVector){
		x = theVector.x + blend * (x - theVector.x);
		y = theVector.y + blend * (y - theVector.y);
		z = theVector.z + blend * (z - theVector.z);
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
	public CCVector3d negate(){
		scale(-1);
		return this;
	}
	
	/**
	 * Scales this vector with the given factor.
	 * @param theScale double, factor to scale the vector
	 */
	public CCVector3d scale(final double theScale){
		x *= theScale;
		y *= theScale;
		z *= theScale;
		return this;
	}
	
	public CCVector3d scale(final double theScaleX, final double theScaleY, final double theScaleZ){
		x *= theScaleX;
		y *= theScaleY;
		z *= theScaleZ;
		return this;
	}
	
	/**
	 * If the vector is longer than the given threshold it is truncated to it.
	 * @param theThreshold
	 */
	public void truncate(final double theThreshold){
		double length = length();
	     if(length > theThreshold)
	         scale(theThreshold / length);
	}
	
	/**
	 * Rounds this vector to the given number of digits
	 * @param theDigits
	 */
	public CCVector3d round(final int theDigits) {
		x = CCMath.round(x, theDigits);
		y = CCMath.round(y, theDigits);
		z = CCMath.round(z, theDigits);
		return this;
	}
	
	/**
	 * Returns the cross product of two vectors. The
	 * cross product returns a vector standing vertical
	 * on the two vectors. The cross product is very useful to 
	 * calculate the normals for lighting.
	 * @param theVector the other vector
	 * @return the cross product
	 */
	public CCVector3d cross(final CCVector3d theVector){
		return cross(theVector.x, theVector.y, theVector.z);
	}
	
	public CCVector3d cross(final double theX, final double theY, final double theZ){
		return new CCVector3d(
			y * theZ - z * theY, 
			z * theX - x * theZ, 
			x * theY - y * theX
		);
	}
	
	/**
	 * Sets this vector to the cross product of the to given vectors
	 * @param theVector1
	 * @param theVector2
	 */
	public void cross(final CCVector3d theVector1, final CCVector3d theVector2) {
		set(
			theVector1.y * theVector2.z - theVector1.z * theVector2.y, 
			theVector1.z * theVector2.x - theVector1.x * theVector2.z, 
			theVector1.x * theVector2.y - theVector1.y * theVector2.x	
		);
	}
	
	/**
	 * Returns the dot product of two vectors. The dot
	 * product is the cosinus of the angle between two
	 * vectors
	 * @param i_vector, the other vector
	 * @return double, dot product of two vectors 
	 */
	public double dot(final CCVector3d theVector){
		return 
			x * theVector.x + 
			y * theVector.y + 
			z * theVector.z;
	}
	
	/**
     * Calculate the angle between this and the given Vector, using the dot product
     * @param v2 another vector
     * @return the angle between the vectors
     */ 
    public double angle(final CCVector3d theVector) {
        double dot = dot(theVector);
        double theta = Math.acos(dot / (length() * theVector.length()));
        if(theta == Float.NaN)return 0;
        return theta;
    }
    
    public double angle(final double theX, final double theY, final double theZ){
    	return angle(new CCVector3d(theX, theY, theZ));
    }
    /**
     * Rotate this vector interpreted as point around the vector (u,v,w)
     * @param theX
     * @param theY
     * @param theZ
     * @param theAngle
     */
    public void rotate(final double theX, final double theY, final double theZ, final double theAngle){
	    double ux = theX * x; double uy = theX * y; double uz = theX * z;
	    double vx = theY * x; double vy = theY * y; double vz = theY * z;
	    double wx = theZ * x; double wy = theZ * y; double wz = theZ * z;
	    
	    double sa = Math.sin(theAngle);
	    double ca = Math.cos(theAngle);
	    
	    x = theX * (ux + vy + wz) + (x * (theY * theY + theZ * theZ) - theX * (vy + wz)) * ca + (-wy + vz) * sa;
	    y = theY * (ux + vy + wz) + (y * (theX * theX + theZ * theZ) - theY * (ux + wz)) * ca + ( wx - uz) * sa;
	    z = theZ * (ux + vy + wz) + (z * (theX * theX + theY * theY) - theZ * (ux + vy)) * ca + (-vx + uy) * sa;
    }
    
	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return double, the length of the vector squared
	 */
	public double lengthSquared(){
		return x * x + y * y + z * z;
	}
	
	/**
	 * Use this method to calculate the length of a vector, the length of a vector is also
	 * known as its magnitude. Vectors have a magnitude and a direction. These values
	 * are not explicitly expressed in the vector so they have to be computed.
	 * @return double: the length of the vector
	 * @shortdesc Calculates the length of the vector.
	 * @related lengthSquared ( )
	 */
	public double length(){
		return Math.sqrt(lengthSquared());
	}
	
	/**
	 * Use this method to calculate the approximate length of a vector, the result is not
	 * completely accurate but much faster avoiding calculating the root. 
	 * @return
	 */
	public double approximateLength() {
		double a, b, c;
		if (x < 0.0F) a = -x;
		else a = x;
		
		if (y < 0.0F) b = -y;
		else b = y;
		
		if (z < 0.0F) c = -z;
		else c = z;
		
		if (a < b) {
			double t = a;
			a = b;
			b = t;
		}
		
		if (a < c) {
			double t = a;
			a = c;
			c = t;
		}
		return a * 0.9375F + (b + c) * 0.375F;
	}
	
	/**
	 * Returns the distance between this and the given vector.
	 * 
	 * @param theVector
	 * @return the distance
	 */
	public double distance(final CCVector3d theVector){
		final CCVector3d result = clone();
		result.subtract(theVector);
		return result.length();
	}
	

	public double distance(final CCVector2d theVector){
		return distance(theVector.x, theVector.y,0);
	}
	
	public double distance(final double theX, final double theY, final double theZ){
		final CCVector3d result = clone();
		result.subtract(theX, theY, theZ);
		return result.length();
	}
	
	public double approximateDistance(final CCVector3d theVector){
		final CCVector3d result = clone();
		result.subtract(theVector);
		return result.approximateLength();
	}
	
	/**
	 * Returns the square of the distance between this and the given 
	 * vector. This often avoid to calculate the square root.
	 * @param theVector
	 * @return
	 */
	public double distanceSquared(final CCVector3d theVector){
		final CCVector3d result = clone();
		result.subtract(theVector);
		return result.lengthSquared();
	}
	
	/**
	 * Norms the vector to the length of 1
	 *
	 */
	public CCVector3d normalize(){
		double m = length();
		if (m != 0.0F){
			scale(1.0F / m);
		}
		return this;
	}
	
	public CCVector3d approximateNormalize(){
		double m = approximateLength();
		if (m != 0.0F){
			scale(1.0F / m);
		}
		return this;
	}
	
	/**
	 * Sets the vector to the given length
	 * @param theNewLength
	 * @return
	 */
	public CCVector3d normalize(final double theNewLength){
		double m = length();
		if (m != 0.0F){
			scale(theNewLength / m);
		}
		return this;
	}
	
	public CCVector3d approximateNormalize(final double theNewLength){
		double m = approximateLength();
		if (m != 0.0F){
			scale(theNewLength / m);
		}
		return this;
	}
	
	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector3d randomize(){
		do{
			x = generator.nextDouble() * 2.0 - 1.0;
			y = generator.nextDouble() * 2.0 - 1.0;
			z = generator.nextDouble() * 2.0 - 1.0;
		}while (lengthSquared() > 1.0);
		return this;
	}

	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector3d randomize(double radius){
		do{
			x = radius * (generator.nextDouble() * 2.0 - 1.0);
			y = radius * (generator.nextDouble() * 2.0 - 1.0);
			z = radius * (generator.nextDouble() * 2.0 - 1.0);
		}while (lengthSquared() > radius * radius);
		return this;
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theVector Vector3f, vector to be added
	 */
	public CCVector3d add(final CCVector3d theVector){
		add(theVector.x,theVector.y,theVector.z);
		return this;
	}
	
	/**
	 * Adds the given coords to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theX double, x coord to add
	 * @param theY double, y coord to add
	 * @param theZ double, z coord to add
	 */
	public CCVector3d add(final double theX,final double theY, final double theZ){
		x += theX;
		y += theY;
		z += theZ;
		return this;
	}
	
	/**
	 * Adds the given value to the x, y and z coord of the vector
	 * @param theValue
	 * @return
	 */
	public CCVector3d add(final double theValue) {
		return add(theValue, theValue, theValue);
	}
	
	/**
	 * Adds the given vector to this, scaled by the given amount.
	 * @param theVector
	 * @param theScale
	 * @return
	 */
	public CCVector3d addScaled(final CCVector3d theVector, final double theScale) {
		x += theVector.x * theScale;
		y += theVector.y * theScale;
		z += theVector.z * theScale;
		return this;
	}
	
	public CCVector3d addScaled(final double theX, final double theY, final double theZ, final double theScale) {
		x += theX * theScale;
		y += theY * theScale;
		z += theZ * theScale;
		return this;
	}

	/**
	 * Subtracts the given vector from this vector, by subtracting the
	 * x, y and z coordinates.
	 * @param theVector Vector3f, vector to subtract
	 */
	public CCVector3d subtract(final CCVector3d theVector){
		subtract(theVector.x,theVector.y,theVector.z);
		return this;
	}
	
	/**
	 * Subtracts the given coords to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theX double, x coord to subtract
	 * @param theY double, y coord to subtract
	 * @param theZ double, z coord to subtract
	 */
	public CCVector3d subtract(final double theX, final double theY, final double theZ){
		x -= theX;
		y -= theY;
		z -= theZ;
		return this;
	}
	
	/**
	 * Returns a clone of this Vector
	 */
	public CCVector3d clone(){
		return new CCVector3d(x,y,z);
	}
	


	/**
	 * given a vector, return a vector perpendicular to it. arbitrarily selects
	 * one of the infinitely many perpendicular vectors. a zero vector maps to
	 * itself, otherwise length is irrelevant (empirically, output length seems
	 * to remain within 20% of input length).
	 */
	public CCVector3d perp(){
		// to be filled in:
		CCVector3d quasiPerp; // a direction which is "almost perpendicular"

		// three mutually perpendicular basis vectors
		final CCVector3d i = new CCVector3d(1, 0, 0);
		final CCVector3d j = new CCVector3d(0, 1, 0);
		final CCVector3d k = new CCVector3d(0, 0, 1);

		// measure the projection of "direction" onto each of the axes
		final double id = i.dot(this);
		final double jd = j.dot(this);
		final double kd = k.dot(this);

		// set quasiPerp to the basis which is least parallel to "direction"
		if ((id <= jd) && (id <= kd)){
			//	projection onto i was the smallest
			quasiPerp = i; 
		}else{
			if ((jd <= id) && (jd <= kd)){
				//projection onto j was the smallest
				quasiPerp = j; 
			}else{
				//projection onto k was the smallest
				quasiPerp = k; 
			}
		}

		// return the cross product (direction x quasiPerp)
		// which is guaranteed to be perpendicular to both of them
		return cross(quasiPerp);
	}

	/**
	 * return component of vector parallel to a unit basis vector (IMPORTANT
	 * NOTE: assumes "basis" has unit magnitude (length==1))
	 */
	public CCVector3d parallelComponent(final CCVector3d theUnitBasis) {
		final double projection = dot(theUnitBasis);
		theUnitBasis.scale(projection);
		return theUnitBasis;
	}

	/**
	 * return component of vector perpendicular to a unit basis vector
	 * (IMPORTANT NOTE: assumes "basis" has unit magnitude (length==1))
	 */
	public CCVector3d perp(final CCVector3d i_vector) {
		final CCVector3d result = new CCVector3d();
		result.set(this);
		result.subtract(parallelComponent(i_vector));
		return result;
	}

    /**
     * Replaces the vector components with their multiplicative inverse.
     *
     * @return itself
     */
    public final CCVector3d reciprocal() {
        x = 1f / x;
        y = 1f / y;
        z = 1f / z;
        return this;
    }
	
	@Override
	public boolean equals(final Object theObject){
		if(!(theObject instanceof CCVector3d))return false;
		
		CCVector3d myVector = (CCVector3d)theObject;
		
		return myVector.x == x && myVector.y == y && myVector.z == z;
	}
	
	public boolean equals(final CCVector3d theVector, final double theTolerance){
		if(Math.abs(x - theVector.x) > theTolerance)return false;
		if(Math.abs(y - theVector.y) > theTolerance)return false;
		if(Math.abs(z - theVector.z) > theTolerance)return false;
		return true;
	}
	
	public boolean almost(CCVector3d theVector) {
        if (Math.abs(x) - Math.abs(theVector.x) < ALMOST_THRESHOLD && 
        	Math.abs(y) - Math.abs(theVector.y) < ALMOST_THRESHOLD && 
        	Math.abs(z) - Math.abs(theVector.z) < ALMOST_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }
	
	/**
	 * Checks if one of the coordinates isNaN.
	 * @return true if one of the coordinates is NaN otherwise false
	 */
	public boolean isNaN() {
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
            return true;
        } else {
            return false;
        }
    }
	
	/**
	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCVector3f[ "+x+"  "+y+"  "+z+" ]";
	}

	/**
	 * Reads a vector from a xml node. The xml node needs the attributes
	 * x, y, z. Any missing value will be set to 0.
	 * @param theBoxXML
	 * @return the vector read from xml
	 */
	public static CCVector3d readFromXML(CCXMLElement theBoxXML) {
		return new CCVector3d(
			theBoxXML.floatAttribute("x", 0),
			theBoxXML.floatAttribute("y", 0),
			theBoxXML.floatAttribute("z", 0)
		);
	}
}
