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

/**
 * A four-element axis angle represented by single-precision floating point x,y,z,angle components. An axis angle is a
 * rotation of angle (radians) about the vector (x,y,z).
 * 
 */
public class CCAxisAngle4f extends CCVector4f {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5904792915995765529L;
	
	final static double EPS = 0.000001;

	/**
	 * Constructs and initializes a AxisAngle4f from the specified xyzw coordinates.
	 * 
	 * @param theX the x coordinate
	 * @param theY the y coordinate
	 * @param theZ the z coordinate
	 * @param theAngle the angle of rotation in radians
	 */
	public CCAxisAngle4f(float theX, float theY, float theZ, float theAngle) {
		super(theX,theY,theZ);
	}

	/**
	 * Constructs and initializes an AxisAngle4f from the array of length 4.
	 * 
	 * @param theA the array of length 4 containing x,y,z,angle in order
	 */
	public CCAxisAngle4f(float[] theA) {
		super(theA);
	}

	/**
	 * Constructs and initializes an AxisAngle4f from the specified AxisAngle4f.
	 * 
	 * @param theA1 the AxisAngle4f containing the initialization x y z angle data
	 */
	public CCAxisAngle4f(CCAxisAngle4f theA1) {
		super(theA1);
	}

	/**
	 * Constructs and initializes an AxisAngle4f from the specified axis and angle.
	 * 
	 * @param theAxis the axis
	 * @param theAngle the angle of rotation in radians
	 * 
	 * @since vecmath 1.2
	 */
	public CCAxisAngle4f(CCVector3f theAxis, float theAngle) {
		x = theAxis.x;
		y = theAxis.y;
		z = theAxis.z;
		w = theAngle;
	}

	/**
	 * Constructs and initializes an AxisAngle4f to (0,0,1,0).
	 */
	public CCAxisAngle4f() {
		x = 0.0f;
		y = 0.0f;
		z = 1.0f;
		w = 0.0f;
	}

	/**
	 * Sets the value of this axis-angle to the specified x,y,z,angle.
	 * 
	 * @param theX the x coordinate
	 * @param theY the y coordinate
	 * @param theZ the z coordinate
	 * @param theAngle the angle of rotation in radians
	 */
	public CCAxisAngle4f set(float theX, float theY, float theZ, float theAngle) {
		super.set(theX,theY,theZ,theAngle);
		return this;
	}

	/**
	 * Sets the value of this axis-angle to the specified values in the array of length 4.
	 * 
	 * @param theA the array of length 4 containing x,y,z,angle in order
	 */
	public CCAxisAngle4f set(float[] theA) {
		super.set(theA);
		return this;
	}

	/**
	 * Sets the value of this axis-angle to the value of axis-angle a1.
	 * 
	 * @param theA1 the axis-angle to be copied
	 */
	public CCAxisAngle4f set(CCAxisAngle4f theA1) {
		super.set(theA1);
		return this;
	}

	/**
	 * Sets the value of this AxisAngle4f to the specified axis and angle.
	 * 
	 * @param theAxis the axis
	 * @param theAngle the angle of rotation in radians
	 * 
	 * @since vecmath 1.2
	 */
	public CCAxisAngle4f set(CCVector3f theAxis, float theAngle) {
		super.set(theAxis.x, theAxis.y, theAxis.z, theAngle);
		return this;
	}

	/**
	 * Copies the value of this axis-angle into the array a.
	 * 
	 * @param theA the array
	 */
	public void get(float[] theA) {
		theA[0] = this.x;
		theA[1] = this.y;
		theA[2] = this.z;
		theA[3] = this.w;
	}

	/**
	 * Sets the value of this axis-angle to the rotational equivalent of the passed quaternion. If the specified
	 * quaternion has no rotational component, the value of this AxisAngle4f is set to an angle of 0 about an axis of
	 * (0,1,0).
	 * 
	 * @param theQuaternion the Quat4f
	 */
	public final void set(CCQuaternion theQuaternion) {
		double mag = theQuaternion.x * theQuaternion.x + theQuaternion.y * theQuaternion.y + theQuaternion.z * theQuaternion.z;

		if (mag > EPS) {
			mag = Math.sqrt(mag);
			double invMag = 1.0 / mag;

			x = (float) (theQuaternion.x * invMag);
			y = (float) (theQuaternion.y * invMag);
			z = (float) (theQuaternion.z * invMag);
			w = (float) (2.0 * Math.atan2(mag, theQuaternion.w));
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
			w = 0.0f;
		}
	}

//	/**
//	 * Sets the value of this axis-angle to the rotational component of the passed matrix. If the specified matrix has
//	 * no rotational component, the value of this AxisAngle4f is set to an angle of 0 about an axis of (0,1,0).
//	 * 
//	 * @param m1 the matrix4f
//	 */
//	public final void set(CCMatrix4f m1) {
//		CCMatrix3f m3f = new CCMatrix3f();
//
//		m1.get(m3f);
//
//		x = m3f.m21 - m3f.m12;
//		y = m3f.m02 - m3f.m20;
//		z = m3f.m10 - m3f.m01;
//		double mag = x * x + y * y + z * z;
//
//		if (mag > EPS) {
//			mag = CCMath.sqrt(mag);
//			double sin = 0.5 * mag;
//			double cos = 0.5 * (m3f.m00 + m3f.m11 + m3f.m22 - 1.0);
//
//			w = (float) Math.atan2(sin, cos);
//			double invMag = 1.0 / mag;
//			x = (float) (x * invMag);
//			y = (float) (y * invMag);
//			z = (float) (z * invMag);
//		} else {
//			x = 0.0f;
//			y = 1.0f;
//			z = 0.0f;
//			w = 0.0f;
//		}
//
//	}

	/**
	 * Sets the value of this axis-angle to the rotational component of the passed matrix. If the specified matrix has
	 * no rotational component, the value of this AxisAngle4f is set to an angle of 0 about an axis of (0,1,0).
	 * 
	 * @param m1 the matrix3f
	 */
	public final void set(CCMatrix3f m1) {
		x = (float) (m1.m21 - m1.m12);
		y = (float) (m1.m02 - m1.m20);
		z = (float) (m1.m10 - m1.m01);
		double mag = x * x + y * y + z * z;
		if (mag > EPS) {
			mag = Math.sqrt(mag);
			double sin = 0.5 * mag;
			double cos = 0.5 * (m1.m00 + m1.m11 + m1.m22 - 1.0);

			w = (float) Math.atan2(sin, cos);

			double invMag = 1.0 / mag;
			x = (float) (x * invMag);
			y = (float) (y * invMag);
			z = (float) (z * invMag);
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
			w = 0.0f;
		}

	}

	/**
	 * Returns true if all of the data members of AxisAngle4f a1 are equal to the corresponding data members in this
	 * AxisAngle4f.
	 * 
	 * @param a1 the axis-angle with which the comparison is made
	 * @return true or false
	 */
	public boolean equals(CCAxisAngle4f a1) {
		try {
			return (this.x == a1.x && this.y == a1.y && this.z == a1.z && this.w == a1.w);
		} catch (NullPointerException e2) {
			return false;
		}

	}

	/**
	 * Returns true if the Object o1 is of type AxisAngle4f and all of the data members of o1 are equal to the
	 * corresponding data members in this AxisAngle4f.
	 * 
	 * @param o1 the object with which the comparison is made
	 * @return true or false
	 */
	public boolean equals(Object o1) {
		try {
			CCAxisAngle4f a2 = (CCAxisAngle4f) o1;
			return (this.x == a2.x && this.y == a2.y && this.z == a2.z && this.w == a2.w);
		} catch (NullPointerException e2) {
			return false;
		} catch (ClassCastException e1) {
			return false;
		}

	}

	/**
	 * Returns true if the L-infinite distance between this axis-angle and axis-angle a1 is less than or equal to the
	 * epsilon parameter, otherwise returns false. The L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2),
	 * abs(z1-z2), abs(angle1-angle2)].
	 * 
	 * @param a1 the axis-angle to be compared to this axis-angle
	 * @param epsilon the threshold value
	 */
	public boolean epsilonEquals(CCAxisAngle4f a1, float epsilon) {
		float diff;

		diff = x - a1.x;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		diff = y - a1.y;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		diff = z - a1.z;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		diff = w - a1.w;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		return true;

	}

	/**
	 * Creates a new object of the same class as this object.
	 * 
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since vecmath 1.3
	 */
	public CCAxisAngle4f clone() {
		return new CCAxisAngle4f(this);
	}

	

}
