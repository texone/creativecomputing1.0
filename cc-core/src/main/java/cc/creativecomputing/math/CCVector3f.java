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

import cc.creativecomputing.animation.CCBlendModifier;
import cc.creativecomputing.animation.CCBlendable;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.math.d.CCVector2d;
import cc.creativecomputing.math.d.CCVector3d;
import cc.creativecomputing.math.util.CCIOctreeElement;
import cc.creativecomputing.property.CCProperty;
import cc.creativecomputing.property.CCPropertyObject;
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
@CCPropertyObject
public class CCVector3f implements Cloneable, Serializable, CCIOctreeElement, CCBlendable<CCVector3f>{
	
	protected float ALMOST_THRESHOLD = 0.001f;

	public static CCVector3f X = new CCVector3f(1, 0, 0);
	public static CCVector3f Y = new CCVector3f(0, 1, 0);
	public static CCVector3f Z = new CCVector3f(0, 0, 1);
	public static CCVector3f ZERO = new CCVector3f(0, 0, 0);
//	const Vector3 Vector3::X = Vector3(0, 1, 0);
//	const Vector3 Vector3::Y = Vector3(1, 0, 0);
//	const Vector3 Vector3::Z = Vector3(0, 0, 1);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8793451086395145586L;
	
	/**
	 * Utility for random 
	 */
	static final Random generator = new Random();
	
	@CCControl(name = "x", min = -2000, max = 2000)
	@CCProperty
	public float x;
	@CCControl(name = "y", min = -2000, max = 2000)
	@CCProperty
	public float y;
	@CCControl(name = "z", min = -2000, max = 2000)
	@CCProperty
	public float z;
	
	/**
	 * 
	 */
	public CCVector3f(){
		this(0,0,0);
	}
	
	public CCVector3f(final CCVector2f theVector){
		this(theVector.x,theVector.y,0);
	}
	
	public CCVector3f(final CCVector2d theVector){
		this(theVector.x,theVector.y,0);
	}
	
	public CCVector3f(final CCVector2f theVector, final float theZ){
		this(theVector.x,theVector.y,theZ);
	}
	
	public CCVector3f(final CCVector3f theVector){
		this(theVector.x,theVector.y,theVector.z);
	}

	public CCVector3f(final float theX, final float theY, final float theZ){
		x = theX;
		y = theY;
		z = theZ;
	}

	
	public CCVector3f(final CCVector3d theVector){
		this(theVector.x,theVector.y,theVector.z);
	}

	public CCVector3f(final double theX, final double theY, final double theZ){
		this((float)theX, (float)theY, (float)theZ);
	}

	public CCVector3f(final float theX, final float theY){
		this(theX,theY,0);
	}
	
	public CCVector3f(final float...theCoords){
		x = theCoords[0];
		y = theCoords[1];
		z = theCoords[2];
	}

	/**
	 * Returns the xy coordinates of the vector as new 2d vector.
	 * @return xy coordinates of the vector as new 2d vector.
	 */
	public CCVector2f xy(){
		return new CCVector2f(x,y);
	}
	
	public float get(int theIndex) {
		switch(theIndex) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		}
		return 0;
	}
	
	public void set(int theIndex, float theValue) {
		switch(theIndex) {
		case 0:
			x = theValue;
			break;
		case 1:
			y = theValue;
			break;
		case 2:
			z = theValue;
			break;
		}
	}
	
	/**
	 * Sets the vector to the given coords
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public CCVector3f set(final float theX, final float theY, final float theZ){
		x = theX;
		y = theY;
		z = theZ;
		return this;
	}
	
	/**
	 * Sets the vector to the given coords
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public CCVector3f set(final float theX, final float theY){
		x = theX;
		y = theY;
		z = 0;
		return this;
	}
	
	/**
	 * Sets the vector to the given vector the Z coord will be
	 * set to zero.
	 * @param theVector CCVector3f: the vector this vector is set to.
	 */
	public CCVector3f set(final CCVector2f theVector){
		x = theVector.x;
		y = theVector.y;
		z = 0;
		return this;
	}
	
	/**
	 * Sets the vector to the given vector
	 * @param theVector CCVector3f: the vector this vector is set to.
	 */
	public CCVector3f set(final CCVector3f theVector){
		x = theVector.x;
		y = theVector.y;
		z = theVector.z;
		return this;
	}
	
	public CCVector3f set(final float[] theCoords){
		x = theCoords[0];
		y = theCoords[1];
		z = theCoords[2];
		return this;
	}
	
	public CCVector3f position() {
		return this;
	}
	
	/**
	 * Interpolates between this vector and the given vector
	 * by a given blend value. The blend value has to be between 0
	 * and 1. A blend value 0 would change nothing, a blend value 1
	 * would set this vector to the given one.
	 * @param blend float, blend value for interpolation
	 * @param theVector CCVector3f, other vector for interpolation
	 */
	public void interpolate(final float blend, final CCVector3f theVector){
		x = theVector.x + blend * (x - theVector.x);
		y = theVector.y + blend * (y - theVector.y);
		z = theVector.z + blend * (z - theVector.z);
	}
	
	/**
	 * Use this method to negate a vector. The result of the
	 * negation is vector with the same magnitude but opposite
	 * direction. Mathematically the negation is the additive
	 * inverse of the vector. The sum of a value and its additive
	 * inerse is always zero.
	 * @shortdesc Use this method to negate a vector.
	 * @related scale ( )
	 */
	public CCVector3f negate(){
		scale(-1);
		return this;
	}
	
	public CCVector3f scale(final float theScaleX, final float theScaleY, final float theScaleZ){
		x *= theScaleX;
		y *= theScaleY;
		z *= theScaleZ;
		return this;
	}
	
	/**
	 * Scales this vector with the given factor.
	 * @param theScale float, factor to scale the vector
	 */
	public CCVector3f scale(final float theScale){
		return scale(theScale, theScale, theScale);
	}
	
	public CCVector3f scale(final CCVector3f theV){
		return scale(theV.x, theV.y, theV.z);
	}
	
	public CCVector3f devide(final float theScaleX, final float theScaleY, final float theScaleZ){
		x /= theScaleX;
		y /= theScaleY;
		z /= theScaleZ;
		return this;
	}
	
	/**
	 * Scales this vector with the given factor.
	 * @param theScale float, factor to scale the vector
	 */
	public CCVector3f devide(final float theScale){
		return devide(theScale, theScale, theScale);
	}
	
	public CCVector3f devide(final CCVector3f theV){
		return devide(theV.x, theV.y, theV.z);
	}
	
	/**
	 * If the vector is longer than the given threshold it is truncated to it.
	 * @param theThreshold
	 */
	public void truncate(final float theThreshold){
		float length = length();
	     if(length > theThreshold)
	         scale(theThreshold / length);
	}
	
	/**
	 * Returns the cross product of two vectors. The
	 * cross product returns a vector standing vertical
	 * on the two vectors. The cross product is very useful to 
	 * calculate the normals for lighting.
	 * @param theVector the other vector
	 * @return the cross product
	 */
	public CCVector3f cross(final CCVector3f theVector){
		return cross(theVector.x, theVector.y, theVector.z);
	}
	
	public CCVector3f cross(final float theX, final float theY, final float theZ){
		return new CCVector3f(
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
	public void cross(final CCVector3f theVector1, final CCVector3f theVector2) {
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
	 * @return float, dot product of two vectors 
	 */
	public float dot(final CCVector3f theVector){
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
    public float angle(final CCVector3f theVector) {
        float dot = dot(theVector);
        float theta = CCMath.acos(dot / (length() * theVector.length()));
        if(theta == Float.NaN)return 0;
        return theta;
    }
    
    public float angle(final float theX, final float theY, final float theZ){
    	return angle(new CCVector3f(theX, theY, theZ));
    }
    /**
     * Rotate this vector interpreted as point around the vector (u,v,w)
     * @param theX
     * @param theY
     * @param theZ
     * @param theAngle
     */
    public void rotate(final float theX, final float theY, final float theZ, final float theAngle){
	    float ux = theX * x; float uy = theX * y; float uz = theX * z;
	    float vx = theY * x; float vy = theY * y; float vz = theY * z;
	    float wx = theZ * x; float wy = theZ * y; float wz = theZ * z;
	    
	    float sa = CCMath.sin(theAngle);
	    float ca = CCMath.cos(theAngle);
	    
	    x = theX * (ux + vy + wz) + (x * (theY * theY + theZ * theZ) - theX * (vy + wz)) * ca + (-wy + vz) * sa;
	    y = theY * (ux + vy + wz) + (y * (theX * theX + theZ * theZ) - theY * (ux + wz)) * ca + ( wx - uz) * sa;
	    z = theZ * (ux + vy + wz) + (z * (theX * theX + theY * theY) - theZ * (ux + vy)) * ca + (-vx + uy) * sa;
    }
    
	/**
	 * Returns the length of the vector as square. This often
	 * prevents calculating the squareroot.
	 * @return float, the length of the vector squared
	 */
	public float lengthSquared(){
		return x * x + y * y + z * z;
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
	 * Use this method to calculate the approximate length of a vector, the result is not
	 * completely accurate but much faster avoiding calculating the root. 
	 * @return
	 */
	public float approximateLength() {
		float a, b, c;
		if (x < 0.0F) a = -x;
		else a = x;
		
		if (y < 0.0F) b = -y;
		else b = y;
		
		if (z < 0.0F) c = -z;
		else c = z;
		
		if (a < b) {
			float t = a;
			a = b;
			b = t;
		}
		
		if (a < c) {
			float t = a;
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
	public float distance(final CCVector3f theVector){
		final CCVector3f result = clone();
		result.subtract(theVector);
		return result.length();
	}
	
	public float distance(final float theX, final float theY, final float theZ){
		final CCVector3f result = clone();
		result.subtract(theX, theY, theZ);
		return result.length();
	}
	
	public float approximateDistance(final CCVector3f theVector){
		final CCVector3f result = clone();
		result.subtract(theVector);
		return result.approximateLength();
	}
	
	/**
	 * Returns the square of the distance between this and the given 
	 * vector. This often avoid to calculate the square root.
	 * @param theVector
	 * @return
	 */
	public float distanceSquared(final CCVector3f theVector){
		final CCVector3f result = clone();
		result.subtract(theVector);
		return result.lengthSquared();
	}
	
	/**
	 * Norms the vector to the length of 1
	 *
	 */
	public CCVector3f normalize(){
		float m = length();
		if (m != 0.0F){
			scale(1.0F / m);
		}
		return this;
	}
	
	public CCVector3f approximateNormalize(){
		float m = approximateLength();
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
	public CCVector3f normalize(final float theNewLength){
		float m = length();
		if (m != 0.0F){
			scale(theNewLength / m);
		}
		return this;
	}
	
	public CCVector3f approximateNormalize(final float theNewLength){
		float m = approximateLength();
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
	public void randomize(){
		do{
			x = generator.nextFloat() * 2.0F - 1.0F;
			y = generator.nextFloat() * 2.0F - 1.0F;
			z = generator.nextFloat() * 2.0F - 1.0F;
		}while (lengthSquared() > 1.0F);
	}

	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public void randomize(float radius){
		do{
			x = radius * (generator.nextFloat() * 2.0F - 1.0F);
			y = radius * (generator.nextFloat() * 2.0F - 1.0F);
			z = radius * (generator.nextFloat() * 2.0F - 1.0F);
		}while (lengthSquared() > radius * radius);
	}

	/**
	 * Adds the given vector to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theVector Vector3f, vector to be added
	 */
	public CCVector3f add(final CCVector3f theVector){
		return add(theVector.x,theVector.y,theVector.z);
	}
	

	public CCVector3f add(final CCVector2f theVector){
		add(theVector.x,theVector.y,0);
		return this;
	}
	
	/**
	 * Adds the given coords to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theX float, x coord to add
	 * @param theY float, y coord to add
	 * @param theZ float, z coord to add
	 */
	public CCVector3f add(final float theX,final float theY, final float theZ){
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
	public CCVector3f add(final float theValue) {
		return add(theValue, theValue, theValue);
	}
	
	/**
	 * Adds the given vector to this, scaled by the given amount.
	 * @param theVector
	 * @param theScale
	 * @return
	 */
	public CCVector3f addScaled(final CCVector3f theVector, final float theScale) {
		x += theVector.x * theScale;
		y += theVector.y * theScale;
		z += theVector.z * theScale;
		return this;
	}
	
	public CCVector3f addScaled(final float theX, final float theY, final float theZ, final float theScale) {
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
	public CCVector3f subtract(final CCVector3f theVector){
		subtract(theVector.x,theVector.y,theVector.z);
		return this;
	}
	
	/**
	 * Subtracts the given coords to this vector, by adding the
	 * x, y and z coordinates.
	 * @param theX float, x coord to subtract
	 * @param theY float, y coord to subtract
	 * @param theZ float, z coord to subtract
	 */
	public CCVector3f subtract(final float theX, final float theY, final float theZ){
		x -= theX;
		y -= theY;
		z -= theZ;
		return this;
	}
	
	/**
	 * Returns a clone of this Vector
	 */
	public CCVector3f clone(){
		return new CCVector3f(x,y,z);
	}
	


	/**
	 * given a vector, return a vector perpendicular to it. arbitrarily selects
	 * one of the infinitely many perpendicular vectors. a zero vector maps to
	 * itself, otherwise length is irrelevant (empirically, output length seems
	 * to remain within 20% of input length).
	 */
	public CCVector3f perp(){
		// to be filled in:
		CCVector3f quasiPerp; // a direction which is "almost perpendicular"

		// three mutually perpendicular basis vectors
		final CCVector3f i = new CCVector3f(1, 0, 0);
		final CCVector3f j = new CCVector3f(0, 1, 0);
		final CCVector3f k = new CCVector3f(0, 0, 1);

		// measure the projection of "direction" onto each of the axes
		final float id = i.dot(this);
		final float jd = j.dot(this);
		final float kd = k.dot(this);

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
	public CCVector3f parallelComponent(final CCVector3f theUnitBasis) {
		final float projection = dot(theUnitBasis);
		theUnitBasis.scale(projection);
		return theUnitBasis;
	}

	/**
	 * return component of vector perpendicular to a unit basis vector
	 * (IMPORTANT NOTE: assumes "basis" has unit magnitude (length==1))
	 */
	public CCVector3f perp(final CCVector3f i_vector) {
		final CCVector3f result = new CCVector3f();
		result.set(this);
		result.subtract(parallelComponent(i_vector));
		return result;
	}

    /**
     * Replaces the vector components with their multiplicative inverse.
     *
     * @return itself
     */
    public final CCVector3f reciprocal() {
        x = 1f / x;
        y = 1f / y;
        z = 1f / z;
        return this;
    }
	
	@Override
	public boolean equals(final Object theObject){
		if(!(theObject instanceof CCVector3f))return false;
		
		CCVector3f myVector = (CCVector3f)theObject;
		
		return myVector.x == x && myVector.y == y && myVector.z == z;
	}
	
	public boolean equals(final CCVector3f theVector, final float theTolerance){
		if(CCMath.abs(x - theVector.x) > theTolerance)return false;
		if(CCMath.abs(y - theVector.y) > theTolerance)return false;
		if(CCMath.abs(z - theVector.z) > theTolerance)return false;
		return true;
	}
	
	public boolean almost(float theX, float theY, float theZ) {
        if (CCMath.abs(x - theX) < ALMOST_THRESHOLD && 
        	CCMath.abs(y - theY) < ALMOST_THRESHOLD && 
        	CCMath.abs(z - theZ) < ALMOST_THRESHOLD
        ) {
            return true;
        } else {
            return false;
        }
    }
	
	public boolean almost(CCVector3f theVector) {
		return almost(theVector.x, theVector.y, theVector.z);
	}
	
	public boolean almostZero() {
		return almost(0f, 0f, 0f);
	}
	
	public boolean zero() {
		return x == 0 && y == 0 && z == 0;
	}
	
	/**
	 * Checks if one of the coordinates isNaN.
	 * @return true if one of the coordinates is NaN otherwise false
	 */
	public boolean isNaN() {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z)) {
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
	public static CCVector3f readFromXML(CCXMLElement theBoxXML) {
		return new CCVector3f(
			theBoxXML.floatAttribute("x", 0),
			theBoxXML.floatAttribute("y", 0),
			theBoxXML.floatAttribute("z", 0)
		);
	}

	/**
	 * Input W must be initialized to a nonzero vector, output is {U,V,W}, an orthonormal basis. A hint is
	 * provided about whether or not W is already unit length.
	 * 
	 * @param theU
	 * @param theV
	 * @param theW
	 * @param theUnitLengthW
	 */
	public static void generateOrthonormalBasis(CCVector3f theU, CCVector3f theV, CCVector3f theW, boolean theUnitLengthW) {
		if (!theUnitLengthW) {
			theW.normalize();
		}

		float fInvLength;

		if (CCMath.abs(theW.x) >= CCMath.abs(theW.y)) {
			// W.x or W.z is the largest magnitude component, swap them
			fInvLength = CCMath.sqrt(theW.x * theW.x + theW.z * theW.z);
			theU.x = -theW.z * fInvLength;
			theU.y = 0.0f;
			theU.z = +theW.x * fInvLength;
			theV.x = theW.y * theU.z;
			theV.y = theW.z * theU.x - theW.x * theU.z;
			theV.z = -theW.y * theU.x;
		} else {
			// W.y or W.z is the largest magnitude component, swap them
			fInvLength = CCMath.sqrt(theW.y * theW.y + theW.z * theW.z);
			theU.x = 0.0f;
			theU.y = +theW.z * fInvLength;
			theU.z = -theW.y * fInvLength;
			theV.x = theW.y * theU.z - theW.z * theU.y;
			theV.y = -theW.x * theU.z;
			theV.z = theW.x * theU.y;
		}
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.animation.CCBlendable#blend(float, java.lang.Object)
	 */
	public void blend(float theBlend, CCVector3f theStart, CCVector3f theTarget) {
		x = CCMath.blend(theStart.x, theTarget.x, theBlend);
		y = CCMath.blend(theStart.y, theTarget.y, theBlend);
		z = CCMath.blend(theStart.z, theTarget.z, theBlend);
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
