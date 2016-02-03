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

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.CCFormatUtil;


/**
 * <code>CCMatrix4f</code> defines and maintains a 4x4 matrix in row major order.
 * This matrix is intended for use in a translation and rotational capacity. 
 * It provides convenience methods for creating the matrix from a multitude 
 * of sources.
 * 
 * Matrices are stored assuming column vectors on the right, with the translation
 * in the rightmost column. Element numbering is row,column, so m03 is the zeroth
 * row, third column, which is the "x" translation part. This means that the implicit
 * storage order is column major. However, the get() and set() functions on double
 * arrays default to row major order!
 */
public final class CCMatrix4d {

	public double m00, m01, m02, m03;
	public double m10, m11, m12, m13;
	public double m20, m21, m22, m23;
	public double m30, m31, m32, m33;

	final static int DEFAULT_STACK_DEPTH = 0;
	int maxStackDepth;
	int stackPointer = 0;
	double stack[][];

	// locally allocated version to avoid creating new memory
	static protected CCMatrix4d inverseCopy;

	public CCMatrix4d() {
		set(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		maxStackDepth = DEFAULT_STACK_DEPTH;
	}

	public CCMatrix4d(int stackDepth) {
		set(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		stack = new double[stackDepth][16];
		maxStackDepth = stackDepth;
	}

	public CCMatrix4d(
		double m00, double m01, double m02, double m03, 
		double m10, double m11, double m12, double m13, 
		double m20, double m21, double m22, double m23, 
		double m30, double m31, double m32, double m33
	) {
		set(
			m00, m01, m02, m03, 
			m10, m11, m12, m13, 
			m20, m21, m22, m23, 
			m30, m31, m32, m33
		);
		maxStackDepth = DEFAULT_STACK_DEPTH;
	}

	// Make a copy of a matrix. We copy the stack depth,
	// but we don't make a copy of the stack or the stack pointer.
	public CCMatrix4d(CCMatrix4d src) {
		set(src);
		maxStackDepth = src.maxStackDepth;
		stack = new double[maxStackDepth][16];
	}

	public void reset() {
		set(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public void clearStack() {
		stackPointer = 0;
	}

	public boolean push() {
		if (stackPointer == maxStackDepth)
			return false;

		stack[stackPointer][0] = m00;
		stack[stackPointer][1] = m01;
		stack[stackPointer][2] = m02;
		stack[stackPointer][3] = m03;

		stack[stackPointer][4] = m10;
		stack[stackPointer][5] = m11;
		stack[stackPointer][6] = m12;
		stack[stackPointer][7] = m13;

		stack[stackPointer][8] = m20;
		stack[stackPointer][9] = m21;
		stack[stackPointer][10] = m22;
		stack[stackPointer][11] = m23;

		stack[stackPointer][12] = m30;
		stack[stackPointer][13] = m31;
		stack[stackPointer][14] = m32;
		stack[stackPointer][15] = m33;

		stackPointer++;
		return true;
	}

	public boolean pop() {
		if (stackPointer == 0)
			return false;
		stackPointer--;

		m00 = stack[stackPointer][0];
		m01 = stack[stackPointer][1];
		m02 = stack[stackPointer][2];
		m03 = stack[stackPointer][3];

		m10 = stack[stackPointer][4];
		m11 = stack[stackPointer][5];
		m12 = stack[stackPointer][6];
		m13 = stack[stackPointer][7];

		m20 = stack[stackPointer][8];
		m21 = stack[stackPointer][9];
		m22 = stack[stackPointer][10];
		m23 = stack[stackPointer][11];

		m30 = stack[stackPointer][12];
		m31 = stack[stackPointer][13];
		m32 = stack[stackPointer][14];
		m33 = stack[stackPointer][15];

		return true;
	}

	public void set(CCMatrix4d src) {
		set(
			src.m00, src.m01, src.m02, src.m03, 
			src.m10, src.m11, src.m12, src.m13, 
			src.m20, src.m21, src.m22, src.m23, 
			src.m30, src.m31, src.m32, src.m33
		);
	}

	public void set(
		double m00, double m01, double m02, double m03, 
		double m10, double m11, double m12, double m13, 
		double m20, double m21, double m22, double m23, 
		double m30, double m31, double m32, double m33
	) {
		this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
		this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
		this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
		this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
	}
	
	public void setGL(final double[] theGLMatrix){
		m00 = theGLMatrix[0]; m01 = theGLMatrix[4]; m02 = theGLMatrix[8]; m03 = theGLMatrix[12];
		m10 = theGLMatrix[1]; m11 = theGLMatrix[5]; m12 = theGLMatrix[9]; m13 = theGLMatrix[13];
		m20 = theGLMatrix[2]; m21 = theGLMatrix[6]; m22 = theGLMatrix[10]; m23 = theGLMatrix[14];
		m30 = theGLMatrix[3]; m31 = theGLMatrix[7]; m32 = theGLMatrix[11]; m33 = theGLMatrix[15];
	}

	public void translate(double tx, double ty) {
		translate(tx, ty, 0);
	}

	public void invTranslate(double tx, double ty) {
		invTranslate(tx, ty, 0);
	}

	public void translate(final double theX, final double theY, final double theZ) {
		m03 += theX * m00 + theY * m01 + theZ * m02;
		m13 += theX * m10 + theY * m11 + theZ * m12;
		m23 += theX * m20 + theY * m21 + theZ * m22;
		m33 += theX * m30 + theY * m31 + theZ * m32;
	}
	
	public void translate(final CCVector3d theVector){
		translate(theVector.x, theVector.y, theVector.z);
	}
	
	public void translate(final CCVector2d theVector){
		translate(theVector.x, theVector.y, 0);
	}

	public void invTranslate(double tx, double ty, double tz) {
		preApply(1, 0, 0, -tx, 0, 1, 0, -ty, 0, 0, 1, -tz, 0, 0, 0, 1);
	}

	// OPT could save several multiplies for the 0s and 1s by just
	//     putting the multMatrix code here and removing uneccessary terms

	public void rotateX(final double theAngle) {
		final double c = cos(theAngle);
		final double s = sin(theAngle);
		apply(1, 0, 0, 0, 0, c, -s, 0, 0, s, c, 0, 0, 0, 0, 1);
	}

	public void invRotateX(final double theAngle) {
		final double c = cos(-theAngle);
		final double s = sin(-theAngle);
		preApply(1, 0, 0, 0, 0, c, -s, 0, 0, s, c, 0, 0, 0, 0, 1);
	}

	public void rotateY(final double theAngle) {
		final double c = cos(theAngle);
		final double s = sin(theAngle);
		apply(c, 0, s, 0, 0, 1, 0, 0, -s, 0, c, 0, 0, 0, 0, 1);
	}

	public void invRotateY(final double theAngle) {
		final double c = cos(-theAngle);
		final double s = sin(-theAngle);
		preApply(c, 0, s, 0, 0, 1, 0, 0, -s, 0, c, 0, 0, 0, 0, 1);
	}

	/**
	 * Just calls rotateZ because two dimensional rotation
	 * is the same as rotating along the z-axis.
	 */
	public void rotate(final double theAngle) {
		rotateZ(theAngle);
	}

	public void invRotate(final double theAngle) {
		invRotateZ(theAngle);
	}

	public void rotateZ(final double theAngle) {
		final double c = cos(theAngle);
		final double s = sin(theAngle);
		apply(c, -s, 0, 0, s, c, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public void invRotateZ(final double theAngle) {
		final double c = cos(-theAngle);
		final double s = sin(-theAngle);
		preApply(c, -s, 0, 0, s, c, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public void rotate(final double theAngle, final double v0, final double v1, final double v2) {
		// should be in radians (i think), instead of degrees (gl uses degrees)
		// based on 15-463 code, but similar to opengl ref p.443

		// TODO should make sure this vector is normalized

		final double c = cos(theAngle);
		final double s = sin(theAngle);
		final double t = 1.0f - c;

		apply(
			(t * v0 * v0) + c		, (t * v0 * v1) - (s * v2), (t * v0 * v2) + (s * v1), 0, 
			(t * v0 * v1) + (s * v2), (t * v1 * v1) + c		  , (t * v1 * v2) - (s * v0), 0, 
			(t * v0 * v2) - (s * v1), (t * v1 * v2) + (s * v0), (t * v2 * v2) + c       , 0, 
			0, 0, 0, 1
		);
	}

	public void rotate(final double theAngle, final CCVector3d theVector) {
		rotate(theAngle, theVector.x, theVector.y, theVector.z);
	}

	public void invRotate(double theAngle, double v0, double v1, double v2) {
		// TODO should make sure this vector is normalized

		double c = cos(-theAngle);
		double s = sin(-theAngle);
		double t = 1.0f - c;

		preApply(
			(t * v0 * v0) + c, (t * v0 * v1) - (s * v2), (t * v0 * v2) + (s * v1), 0, 
			(t * v0 * v1) + (s * v2), (t * v1 * v1) + c, (t * v1 * v2) - (s * v0), 0, 
			(t * v0 * v2) - (s * v1), (t * v1 * v2) + (s * v0), (t * v2 * v2) + c, 0, 
			0, 0, 0, 1
		);
	}

	public void scale(double s) {
		apply(
			s, 0, 0, 0, 
			0, s, 0, 0, 
			0, 0, s, 0, 
			0, 0, 0, 1
		);
	}

	public void invScale(double s) {
		preApply(1 / s, 0, 0, 0, 0, 1 / s, 0, 0, 0, 0, 1 / s, 0, 0, 0, 0, 1);
	}

	public void scale(double sx, double sy) {
		apply(sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public void invScale(double sx, double sy) {
		preApply(1 / sx, 0, 0, 0, 0, 1 / sy, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	// OPTIMIZE: same as above
	public void scale(double x, double y, double z) {
		apply(x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1);
	}

	public void invScale(double x, double y, double z) {
		preApply(1 / x, 0, 0, 0, 0, 1 / y, 0, 0, 0, 0, 1 / z, 0, 0, 0, 0, 1);
	}

	/*
	public void transform(double n00, double n01, double n02, double n03,
	                      double n10, double n11, double n12, double n13,
	                      double n20, double n21, double n22, double n23,
	                      double n30, double n31, double n32, double n33) {
	  apply(n00, n01, n02, n03,
	        n10, n11, n12, n13,
	        n20, n21, n22, n23,
	        n30, n31, n32, n33);
	}
	 */

	public void preApply(CCMatrix4d lhs) {
		preApply(
			lhs.m00, lhs.m01, lhs.m02, lhs.m03, 
			lhs.m10, lhs.m11, lhs.m12, lhs.m13, 
			lhs.m20, lhs.m21, lhs.m22, lhs.m23, 
			lhs.m30, lhs.m31, lhs.m32, lhs.m33
		);
	}

	// for inverse operations, like multiplying the matrix on the left
	public void preApply(
		double n00, double n01, double n02, double n03, 
		double n10, double n11, double n12, double n13, 
		double n20, double n21, double n22, double n23, 
		double n30, double n31, double n32, double n33
	) {

		double r00 = n00 * m00 + n01 * m10 + n02 * m20 + n03 * m30;
		double r01 = n00 * m01 + n01 * m11 + n02 * m21 + n03 * m31;
		double r02 = n00 * m02 + n01 * m12 + n02 * m22 + n03 * m32;
		double r03 = n00 * m03 + n01 * m13 + n02 * m23 + n03 * m33;

		double r10 = n10 * m00 + n11 * m10 + n12 * m20 + n13 * m30;
		double r11 = n10 * m01 + n11 * m11 + n12 * m21 + n13 * m31;
		double r12 = n10 * m02 + n11 * m12 + n12 * m22 + n13 * m32;
		double r13 = n10 * m03 + n11 * m13 + n12 * m23 + n13 * m33;

		double r20 = n20 * m00 + n21 * m10 + n22 * m20 + n23 * m30;
		double r21 = n20 * m01 + n21 * m11 + n22 * m21 + n23 * m31;
		double r22 = n20 * m02 + n21 * m12 + n22 * m22 + n23 * m32;
		double r23 = n20 * m03 + n21 * m13 + n22 * m23 + n23 * m33;

		double r30 = n30 * m00 + n31 * m10 + n32 * m20 + n33 * m30;
		double r31 = n30 * m01 + n31 * m11 + n32 * m21 + n33 * m31;
		double r32 = n30 * m02 + n31 * m12 + n32 * m22 + n33 * m32;
		double r33 = n30 * m03 + n31 * m13 + n32 * m23 + n33 * m33;

		m00 = r00;
		m01 = r01;
		m02 = r02;
		m03 = r03;
		m10 = r10;
		m11 = r11;
		m12 = r12;
		m13 = r13;
		m20 = r20;
		m21 = r21;
		m22 = r22;
		m23 = r23;
		m30 = r30;
		m31 = r31;
		m32 = r32;
		m33 = r33;
	}

	public boolean invApply(CCMatrix4d rhs) {
		CCMatrix4d copy = new CCMatrix4d(rhs);
		CCMatrix4d inverse = copy.invert();
		if (inverse == null)
			return false;
		preApply(inverse);
		return true;
	}

	public boolean invApply(
		double n00, double n01, double n02, double n03, 
		double n10, double n11, double n12, double n13, 
		double n20, double n21, double n22, double n23, 
		double n30, double n31, double n32, double n33
	) {
		if (inverseCopy == null) {
			inverseCopy = new CCMatrix4d();
		}
		inverseCopy.set(
			n00, n01, n02, n03, 
			n10, n11, n12, n13, 
			n20, n21, n22, n23, 
			n30, n31, n32, n33
		);
		
		CCMatrix4d inverse = inverseCopy.invert();
		if (inverse == null)
			return false;
		preApply(inverse);
		return true;
	}

	public void apply(CCMatrix4d rhs) {
		apply(
			rhs.m00, rhs.m01, rhs.m02, rhs.m03, 
			rhs.m10, rhs.m11, rhs.m12, rhs.m13, 
			rhs.m20, rhs.m21, rhs.m22, rhs.m23, 
			rhs.m30, rhs.m31, rhs.m32, rhs.m33
		);
	}

	public void apply(
		double n00, double n01, double n02, double n03, 
		double n10, double n11, double n12, double n13, 
		double n20, double n21, double n22, double n23, 
		double n30, double n31, double n32, double n33
	) {

		double r00 = m00 * n00 + m01 * n10 + m02 * n20 + m03 * n30;
		double r01 = m00 * n01 + m01 * n11 + m02 * n21 + m03 * n31;
		double r02 = m00 * n02 + m01 * n12 + m02 * n22 + m03 * n32;
		double r03 = m00 * n03 + m01 * n13 + m02 * n23 + m03 * n33;

		double r10 = m10 * n00 + m11 * n10 + m12 * n20 + m13 * n30;
		double r11 = m10 * n01 + m11 * n11 + m12 * n21 + m13 * n31;
		double r12 = m10 * n02 + m11 * n12 + m12 * n22 + m13 * n32;
		double r13 = m10 * n03 + m11 * n13 + m12 * n23 + m13 * n33;

		double r20 = m20 * n00 + m21 * n10 + m22 * n20 + m23 * n30;
		double r21 = m20 * n01 + m21 * n11 + m22 * n21 + m23 * n31;
		double r22 = m20 * n02 + m21 * n12 + m22 * n22 + m23 * n32;
		double r23 = m20 * n03 + m21 * n13 + m22 * n23 + m23 * n33;

		double r30 = m30 * n00 + m31 * n10 + m32 * n20 + m33 * n30;
		double r31 = m30 * n01 + m31 * n11 + m32 * n21 + m33 * n31;
		double r32 = m30 * n02 + m31 * n12 + m32 * n22 + m33 * n32;
		double r33 = m30 * n03 + m31 * n13 + m32 * n23 + m33 * n33;

		m00 = r00;
		m01 = r01;
		m02 = r02;
		m03 = r03;
		m10 = r10;
		m11 = r11;
		m12 = r12;
		m13 = r13;
		m20 = r20;
		m21 = r21;
		m22 = r22;
		m23 = r23;
		m30 = r30;
		m31 = r31;
		m32 = r32;
		m33 = r33;
	}
	
	public CCVector3d transform(final CCVector3d theInput, CCVector3d theOutput) {
		
		if(theOutput == null)theOutput = new CCVector3d();
		// must use these temp vars because vec may be the same as out
		double tmpx = m00 * theInput.x + m01 * theInput.y + m02 * theInput.z + m03;
		double tmpy = m10 * theInput.x + m11 * theInput.y + m12 * theInput.z + m13;
		double tmpz = m20 * theInput.x + m21 * theInput.y + m22 * theInput.z + m23;

		theOutput.set(tmpx, tmpy, tmpz);
		
		return theOutput;
	}

	public CCVector3d transform(final CCVector3d theVector) {
		return transform(theVector,null);
	}
	
	/**
	 * Applies this matrix to the given triangle, by transforming its three points
	 * @param theTriangle the triangle to transform
	 * @return the transformed triangle
	 */
	public CCTriangle3d transform(CCTriangle3d theTriangle){
		theTriangle.set(0, transform(theTriangle.get(0)));
		theTriangle.set(1, transform(theTriangle.get(1)));
		theTriangle.set(2, transform(theTriangle.get(2)));
		return theTriangle;
	}
	
	public void inverseTransform(final CCVector3d theInput, final CCVector3d theOutput) {
		double tmpx = m00 * theInput.x + m10 * theInput.y + m20 * theInput.z;
		double tmpy = m01 * theInput.x + m11 * theInput.y + m21 * theInput.z;
		double tmpz = m02 * theInput.x + m12 * theInput.y + m22 * theInput.z;
		theOutput.set(tmpx, tmpy, tmpz);
	}

	public void inverseTransform(final CCVector3d theVector) {
		inverseTransform(theVector,theVector);
	}

	public void mult(double vec[], double out[]) {
		// must use these temp vars because vec may be the same as out
		double tmpx = m00 * vec[0] + m01 * vec[1] + m02 * vec[2] + m03 * vec[3];
		double tmpy = m10 * vec[0] + m11 * vec[1] + m12 * vec[2] + m13 * vec[3];
		double tmpz = m20 * vec[0] + m21 * vec[1] + m22 * vec[2] + m23 * vec[3];
		double tmpw = m30 * vec[0] + m31 * vec[1] + m32 * vec[2] + m33 * vec[3];

		out[0] = tmpx;
		out[1] = tmpy;
		out[2] = tmpz;
		out[3] = tmpw;
	}

	/**
	 * @return the determinant of the matrix
	 */
	public double determinant() {
		double f;
		f  = m00 * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32) - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
		f -= m01 * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32) - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
		f += m02 * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31) - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33);
		f -= m03 * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31) - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
		return f;
	}

	/**
	 * Calculate the determinant of a 3x3 matrix
	 * @return result
	 */
	private double determinant3x3(
		double t00, double t01, double t02, 
		double t10, double t11, double t12, 
		double t20, double t21, double t22
	) {
		return
			t00 * (t11 * t22 - t12 * t21) + 
			t01 * (t12 * t20 - t10 * t22) + 
			t02 * (t10 * t21 - t11 * t20);
	}

	public CCMatrix4d transpose() {
		double temp;
		temp = m01; m01 = m10; m10 = temp;
		temp = m02; m02 = m20; m20 = temp;
		temp = m03; m03 = m30; m30 = temp;
		temp = m12; m12 = m21; m21 = temp;
		temp = m13; m13 = m31; m31 = temp;
		temp = m23; m23 = m32; m32 = temp;
		return this;
	}

	/**
	 * Invert this matrix
	 * @return this if successful, null otherwise
	 */
	public CCMatrix4d invert() {

		double determinant = determinant();

		if (determinant != 0) {
			// m00 m01 m02 m03
			// m10 m11 m12 m13
			// m20 m21 m22 m23
			// m30 m31 m32 m33
			double determinant_inv = 1f / determinant;

			// first row
			double t00 = determinant3x3(m11, m12, m13, m21, m22, m23, m31, m32, m33);
			double t01 = -determinant3x3(m10, m12, m13, m20, m22, m23, m30, m32, m33);
			double t02 = determinant3x3(m10, m11, m13, m20, m21, m23, m30, m31, m33);
			double t03 = -determinant3x3(m10, m11, m12, m20, m21, m22, m30, m31, m32);

			// second row
			double t10 = -determinant3x3(m01, m02, m03, m21, m22, m23, m31, m32, m33);
			double t11 = determinant3x3(m00, m02, m03, m20, m22, m23, m30, m32, m33);
			double t12 = -determinant3x3(m00, m01, m03, m20, m21, m23, m30, m31, m33);
			double t13 = determinant3x3(m00, m01, m02, m20, m21, m22, m30, m31, m32);

			// third row
			double t20 = determinant3x3(m01, m02, m03, m11, m12, m13, m31, m32, m33);
			double t21 = -determinant3x3(m00, m02, m03, m10, m12, m13, m30, m32, m33);
			double t22 = determinant3x3(m00, m01, m03, m10, m11, m13, m30, m31, m33);
			double t23 = -determinant3x3(m00, m01, m02, m10, m11, m12, m30, m31, m32);

			// fourth row
			double t30 = -determinant3x3(m01, m02, m03, m11, m12, m13, m21, m22, m23);
			double t31 = determinant3x3(m00, m02, m03, m10, m12, m13, m20, m22, m23);
			double t32 = -determinant3x3(m00, m01, m03, m10, m11, m13, m20, m21, m23);
			double t33 = determinant3x3(m00, m01, m02, m10, m11, m12, m20, m21, m22);

			// transpose and divide by the determinant
			m00 = t00 * determinant_inv;
			m11 = t11 * determinant_inv;
			m22 = t22 * determinant_inv;
			m33 = t33 * determinant_inv;
			m01 = t10 * determinant_inv;
			m10 = t01 * determinant_inv;
			m20 = t02 * determinant_inv;
			m02 = t20 * determinant_inv;
			m12 = t21 * determinant_inv;
			m21 = t12 * determinant_inv;
			m03 = t30 * determinant_inv;
			m30 = t03 * determinant_inv;
			m13 = t31 * determinant_inv;
			m31 = t13 * determinant_inv;
			m32 = t23 * determinant_inv;
			m23 = t32 * determinant_inv;
			return this;
		}
		return null;
	}

	//////////////////////////////////////////////////////////////

	public String toString() {
		int big = (int) maxabs(
			m00, m01, m02, m03,
			m10, m11, m12, m13,
			m20, m21, m22, m23,
			m30, m31, m32, m33
		);

		// avoid infinite loop
		if (Float.isNaN(big) || Float.isInfinite(big)) {
			big = 1000000; // set to something arbitrary
		}

		int d = 1;
		while ((big /= 10) != 0)
			d++; // cheap log()

		final StringBuilder myStringBuilder = new StringBuilder();
		myStringBuilder.append(
			CCFormatUtil.nds(m00, d, 4) + " " + 
			CCFormatUtil.nds(m01, d, 4) + " " + 
			CCFormatUtil.nds(m02, d, 4) + " " + 
			CCFormatUtil.nds(m03, d, 4) + "\n");

		myStringBuilder.append(
			CCFormatUtil.nds(m10, d, 4) + " " + 
			CCFormatUtil.nds(m11, d, 4) + " " + 
			CCFormatUtil.nds(m12, d, 4) + " " + 
			CCFormatUtil.nds(m13, d, 4) + "\n");

		myStringBuilder.append(
			CCFormatUtil.nds(m20, d, 4) + " " + 
			CCFormatUtil.nds(m21, d, 4) + " " + 
			CCFormatUtil.nds(m22, d, 4) + " " + 
			CCFormatUtil.nds(m23, d, 4) + "\n");

		myStringBuilder.append(
			CCFormatUtil.nds(m30, d, 4) + " " + 
			CCFormatUtil.nds(m31, d, 4) + " " + 
			CCFormatUtil.nds(m32, d, 4) + " " + 
			CCFormatUtil.nds(m33, d, 4) + "\n");

		return myStringBuilder.toString();
	}

	//////////////////////////////////////////////////////////////
	
	private final double maxabs(double ... theValue) {
		double result = Float.MIN_VALUE;
		for(double myValue:theValue) {
			result = CCMath.max(CCMath.abs(myValue), result);
		}
		return result;
	}

	private final double sin(double theAngle) {
		return Math.sin(theAngle);
	}

	private final double cos(double theAngle) {
		return Math.cos(theAngle);
	}

	public DoubleBuffer toDoubleBuffer() {
		final DoubleBuffer myResult = DoubleBuffer.allocate(16);

		myResult.put(m00);myResult.put(m10);myResult.put(m20);myResult.put(m30);
		myResult.put(m01);myResult.put(m11);myResult.put(m21);myResult.put(m31);
		myResult.put(m02);myResult.put(m12);myResult.put(m22);myResult.put(m32);
		myResult.put(m03);myResult.put(m13);myResult.put(m23);myResult.put(m33);
		myResult.flip();
		return myResult;
	}
	
	static public CCMatrix4d createFromGLMatrix(final FloatBuffer theGLMatrix){
		theGLMatrix.rewind();
		CCMatrix4d myResult = new CCMatrix4d();
		myResult.m00 = theGLMatrix.get();myResult.m10 = theGLMatrix.get();myResult.m20 = theGLMatrix.get();myResult.m30 = theGLMatrix.get();
		myResult.m01 = theGLMatrix.get();myResult.m11 = theGLMatrix.get();myResult.m21 = theGLMatrix.get();myResult.m31 = theGLMatrix.get();
		myResult.m02 = theGLMatrix.get();myResult.m12 = theGLMatrix.get();myResult.m22 = theGLMatrix.get();myResult.m32 = theGLMatrix.get();
		myResult.m03 = theGLMatrix.get();myResult.m13 = theGLMatrix.get();myResult.m23 = theGLMatrix.get();myResult.m33 = theGLMatrix.get();
		return myResult;
	}
}
