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

import java.nio.FloatBuffer;

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
 * storage order is column major. However, the get() and set() functions on float
 * arrays default to row major order!
 */
public class CCMatrix4f {

	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;
	
	public static final CCMatrix4f ZERO = new CCMatrix4f(
		0, 0, 0, 0, 
		0, 0, 0, 0, 
		0, 0, 0, 0,
		0, 0, 0, 0
	);
    public static final CCMatrix4f IDENTITY = new CCMatrix4f();

	final static int DEFAULT_STACK_DEPTH = 0;
	int maxStackDepth;
	int stackPointer = 0;
	float stack[][];

	// locally allocated version to avoid creating new memory
	static protected CCMatrix4f inverseCopy;

	public CCMatrix4f() {
		loadIdentity();
		maxStackDepth = DEFAULT_STACK_DEPTH;
	}

	public CCMatrix4f(int stackDepth) {
		set(
			1, 0, 0, 0, 
			0, 1, 0, 0, 
			0, 0, 1, 0, 
			0, 0, 0, 1
		);
		stack = new float[stackDepth][16];
		maxStackDepth = stackDepth;
	}
	
	public CCMatrix4f(CCVector3f theV0, CCVector3f theV1, CCVector3f theV2){
	    m00 = theV0.x; m01 = theV1.x; m02 = theV2.x; m03 = 0.0f;
	    m10 = theV0.y; m11 = theV1.y; m12 = theV2.y; m13 = 0.0f;
	    m20 = theV0.z; m21 = theV1.z; m22 = theV2.z; m23 = 0.0f;
	    m30 = 0.0f;    m31 = 0.0f;    m32 = 0.0f;    m33 = 1.0f;  
	}

	public CCMatrix4f(
		float m00, float m10, float m20, float m30, 
		float m01, float m11, float m21, float m31, 
		float m02, float m12, float m22, float m32, 
		float m03, float m13, float m23, float m33
	) {
		set(
			m00, m10, m20, m30, 
			m01, m11, m21, m31, 
			m02, m12, m22, m32, 
			m03, m13, m23, m33
		);
		maxStackDepth = DEFAULT_STACK_DEPTH;
	}

	/**
     * Copy constructor that creates a new <code>CCMatrix4f</code> object that
     * is the same as the provided matrix.
     * 
     * @param theMatrix the matrix to copy.
     */
	public CCMatrix4f(CCMatrix4f src) {
		set(src);
		maxStackDepth = src.maxStackDepth;
		stack = new float[maxStackDepth][16];
	}
	
	/**
     * <code>loadIdentity</code> sets this matrix to the identity matrix.
     * Where all values are zero except those along the diagonal which are one.
     */
	public void loadIdentity() {
		set(
			1, 0, 0, 0, 
			0, 1, 0, 0, 
			0, 0, 1, 0, 
			0, 0, 0, 1
		);
	}

	public void reset() {
		set(
			1, 0, 0, 0, 
			0, 1, 0, 0, 
			0, 0, 1, 0, 
			0, 0, 0, 1
		);
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

	public void set(CCMatrix4f src) {
		set(
			src.m00, src.m10, src.m20, src.m30, 
			src.m01, src.m11, src.m21, src.m31, 
			src.m02, src.m12, src.m22, src.m32, 
			src.m03, src.m13, src.m23, src.m33
		);
	}
	
	public CCMatrix4f set(CCMatrix3f src) {
		set(
			src.m00, src.m10, src.m20, 0, 
			src.m01, src.m11, src.m21, 0, 
			src.m02, src.m12, src.m22, 0, 
			0,       0,       0,       1
		);
		return this;
	}
	
	public CCMatrix3f matrix3f(CCMatrix3f theMatrix){
		theMatrix.set(
			m00, m10, m20, 
			m01, m11, m21,
			m02, m12, m22
		);
		return theMatrix;
	}
	
	public CCMatrix3f normalMatrix(){
		return this.clone().invert().transpose().matrix3f();
	}
	
	public CCMatrix3f matrix3f(){
		return matrix3f(new CCMatrix3f());
	}

	public void set(
		float m00, float m10, float m20, float m30, 
		float m01, float m11, float m21, float m31, 
		float m02, float m12, float m22, float m32, 
		float m03, float m13, float m23, float m33
	) {
		this.m00 = m00; this.m10 = m10; this.m20 = m20; this.m30 = m30;
		this.m01 = m01; this.m11 = m11; this.m21 = m21; this.m31 = m31;
		this.m02 = m02; this.m12 = m12; this.m22 = m22; this.m32 = m32;
		this.m03 = m03; this.m13 = m13; this.m23 = m23; this.m33 = m33;
	}
	
	public CCMatrix4f setFromColumOrder(final float...theGLMatrix){
		if(theGLMatrix.length != 16)return this;
		m00 = theGLMatrix[0]; m01 = theGLMatrix[4]; m02 = theGLMatrix[8]; m03 = theGLMatrix[12];
		m10 = theGLMatrix[1]; m11 = theGLMatrix[5]; m12 = theGLMatrix[9]; m13 = theGLMatrix[13];
		m20 = theGLMatrix[2]; m21 = theGLMatrix[6]; m22 = theGLMatrix[10]; m23 = theGLMatrix[14];
		m30 = theGLMatrix[3]; m31 = theGLMatrix[7]; m32 = theGLMatrix[11]; m33 = theGLMatrix[15];
		return this;
	}
	
	/**
	 * Sets this matrix by an array in row order
	 * @param theRowOrderMatrix
	 */
	public CCMatrix4f setFromRowOrder(final float...theRowOrderMatrix) {
		if(theRowOrderMatrix.length != 16)return this;
		m00 = theRowOrderMatrix[0]; m01 = theRowOrderMatrix[1]; m02 = theRowOrderMatrix[2]; m03 = theRowOrderMatrix[3];
		m10 = theRowOrderMatrix[4]; m11 = theRowOrderMatrix[5]; m12 = theRowOrderMatrix[6]; m13 = theRowOrderMatrix[7];
		m20 = theRowOrderMatrix[8]; m21 = theRowOrderMatrix[9]; m22 = theRowOrderMatrix[10]; m23 = theRowOrderMatrix[11];
		m30 = theRowOrderMatrix[12]; m31 = theRowOrderMatrix[13]; m32 = theRowOrderMatrix[14]; m33 = theRowOrderMatrix[15];
		return this;
	}

	public void translate(float tx, float ty) {
		translate(tx, ty, 0);
	}

	public void invTranslate(float tx, float ty) {
		invTranslate(tx, ty, 0);
	}

	public CCMatrix4f translate(final float theX, final float theY, final float theZ) {
		m03 += theX * m00 + theY * m01 + theZ * m02;
		m13 += theX * m10 + theY * m11 + theZ * m12;
		m23 += theX * m20 + theY * m21 + theZ * m22;
		m33 += theX * m30 + theY * m31 + theZ * m32;
		
		return this;
	}
	
	public CCMatrix4f translate(final CCVector3f theVector){
		return translate(theVector.x, theVector.y, theVector.z);
	}
	
	public CCMatrix4f translate(final CCVector2f theVector){
		return translate(theVector.x, theVector.y, 0);
	}

	public void invTranslate(float tx, float ty, float tz) {
		preApply(1, 0, 0, -tx, 0, 1, 0, -ty, 0, 0, 1, -tz, 0, 0, 0, 1);
	}
	
	public void shere(float theX, float theY){
		apply(
			1,    0,    0, 0,
			0,    1,    0, 0,
			theX, theY, 1, 0,
			0,    0,    0, 1
		);
	}

	// OPT could save several multiplies for the 0s and 1s by just
	//     putting the multMatrix code here and removing uneccessary terms

	private void rotateX(final float s, final float c) {
		apply(
			1, 0, 0, 0, 
			0, c, -s, 0, 
			0, s, c, 0, 
			0, 0, 0, 1
		);
	}
	
	public void rotateX(final float theAngle) {
		final float c = cos(theAngle);
		final float s = sin(theAngle);
		rotateX(s,c);
	}

	public void invRotateX(final float theAngle) {
		final float c = cos(-theAngle);
		final float s = sin(-theAngle);
		preApply(
			1, 0, 0, 0, 
			0, c, -s, 0, 
			0, s, c, 0, 
			0, 0, 0, 1
		);
	}

	private void rotateY(final float s, final float c) {
		apply(
			c, 0, s, 0, 
			0, 1, 0, 0, 
			-s, 0, c, 0, 
			0, 0, 0, 1
		);
	}
	
	public void rotateY(final float theAngle) {
		final float c = cos(theAngle);
		final float s = sin(theAngle);
		rotateY(s,c);
	}

	public void invRotateY(final float theAngle) {
		final float c = cos(-theAngle);
		final float s = sin(-theAngle);
		preApply(c, 0, s, 0, 0, 1, 0, 0, -s, 0, c, 0, 0, 0, 0, 1);
	}

	/**
	 * Just calls rotateZ because two dimensional rotation
	 * is the same as rotating along the z-axis.
	 */
	public void rotate(final float theAngle) {
		rotateZ(theAngle);
	}

	public void invRotate(final float theAngle) {
		invRotateZ(theAngle);
	}

	public void rotateZ(final float theAngle) {
		final float c = cos(theAngle);
		final float s = sin(theAngle);
		apply(
			c, -s, 0, 0, 
			s, c, 0, 0, 
			0, 0, 1, 0, 
			0, 0, 0, 1
		);
	}

	public void invRotateZ(final float theAngle) {
		final float c = cos(-theAngle);
		final float s = sin(-theAngle);
		preApply(c, -s, 0, 0, s, c, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public void rotate(final CCQuaternion theQuaternion) {
		final CCVector4f myRotation = theQuaternion.getVectorAndAngle();
		rotate(CCMath.degrees(myRotation.w()), myRotation.x, myRotation.y, myRotation.z);
	}

	public void rotate(final float theAngle, final float nx, final float ny, final float nz) {
		// should be in radians (i think), instead of degrees (gl uses degrees)
		// based on 15-463 code, but similar to opengl ref p.443

		// TODO should make sure this vector is normalized

		final float c = cos(theAngle);
		final float s = sin(theAngle);
		final float t = 1.0f - c;

		apply(
			(t * nx * nx) + c		, (t * nx * ny) - (s * nz), (t * nx * nz) + (s * ny), 0, 
			(t * nx * ny) + (s * nz), (t * ny * ny) + c		  , (t * ny * nz) - (s * nx), 0, 
			(t * nx * nz) - (s * ny), (t * ny * nz) + (s * nx), (t * nz * nz) + c       , 0, 
			0, 0, 0, 1
		);
		
		
	}

	public void rotate(final float theAngle, final CCVector3f theVector) {
		rotate(theAngle, theVector.x, theVector.y, theVector.z);
	}

	public void invRotate(float theAngle, float v0, float v1, float v2) {
		// TODO should make sure this vector is normalized

		float c = cos(-theAngle);
		float s = sin(-theAngle);
		float t = 1.0f - c;

		preApply(
			(t * v0 * v0) + c, (t * v0 * v1) - (s * v2), (t * v0 * v2) + (s * v1), 0, 
			(t * v0 * v1) + (s * v2), (t * v1 * v1) + c, (t * v1 * v2) - (s * v0), 0, 
			(t * v0 * v2) - (s * v1), (t * v1 * v2) + (s * v0), (t * v2 * v2) + c, 0, 
			0, 0, 0, 1
		);
	}

	public void scale(float s) {
		apply(
			s, 0, 0, 0, 
			0, s, 0, 0, 
			0, 0, s, 0, 
			0, 0, 0, 1
		);
	}

	public void invScale(float s) {
		preApply(
			1 / s, 0, 0, 0, 
			0, 1 / s, 0, 0, 
			0, 0, 1 / s, 0, 
			0, 0, 0, 1
		);
	}

	public void scale(float sx, float sy) {
		apply(
			sx, 0, 0, 0, 
			0, sy, 0, 0, 
			0, 0, 1, 0, 
			0, 0, 0, 1
		);
	}

	public void invScale(float sx, float sy) {
		preApply(1 / sx, 0, 0, 0, 0, 1 / sy, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	// OPTIMIZE: same as above
	public void scale(float x, float y, float z) {
		apply(
			x, 0, 0, 0, 
			0, y, 0, 0, 
			0, 0, z, 0, 
			0, 0, 0, 1
		);
	}

	public void invScale(float x, float y, float z) {
		preApply(1 / x, 0, 0, 0, 0, 1 / y, 0, 0, 0, 0, 1 / z, 0, 0, 0, 0, 1);
	}
	
	

	public void preApply(CCMatrix4f lhs) {
		preApply(
			lhs.m00, lhs.m01, lhs.m02, lhs.m03, 
			lhs.m10, lhs.m11, lhs.m12, lhs.m13, 
			lhs.m20, lhs.m21, lhs.m22, lhs.m23, 
			lhs.m30, lhs.m31, lhs.m32, lhs.m33
		);
	}

	// for inverse operations, like multiplying the matrix on the left
	public CCMatrix4f preApply(
		float n00, float n01, float n02, float n03, 
		float n10, float n11, float n12, float n13, 
		float n20, float n21, float n22, float n23, 
		float n30, float n31, float n32, float n33
	) {

		float r00 = n00 * m00 + n01 * m10 + n02 * m20 + n03 * m30;
		float r01 = n00 * m01 + n01 * m11 + n02 * m21 + n03 * m31;
		float r02 = n00 * m02 + n01 * m12 + n02 * m22 + n03 * m32;
		float r03 = n00 * m03 + n01 * m13 + n02 * m23 + n03 * m33;

		float r10 = n10 * m00 + n11 * m10 + n12 * m20 + n13 * m30;
		float r11 = n10 * m01 + n11 * m11 + n12 * m21 + n13 * m31;
		float r12 = n10 * m02 + n11 * m12 + n12 * m22 + n13 * m32;
		float r13 = n10 * m03 + n11 * m13 + n12 * m23 + n13 * m33;

		float r20 = n20 * m00 + n21 * m10 + n22 * m20 + n23 * m30;
		float r21 = n20 * m01 + n21 * m11 + n22 * m21 + n23 * m31;
		float r22 = n20 * m02 + n21 * m12 + n22 * m22 + n23 * m32;
		float r23 = n20 * m03 + n21 * m13 + n22 * m23 + n23 * m33;

		float r30 = n30 * m00 + n31 * m10 + n32 * m20 + n33 * m30;
		float r31 = n30 * m01 + n31 * m11 + n32 * m21 + n33 * m31;
		float r32 = n30 * m02 + n31 * m12 + n32 * m22 + n33 * m32;
		float r33 = n30 * m03 + n31 * m13 + n32 * m23 + n33 * m33;

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
		
		return this;
	}

	public boolean invApply(CCMatrix4f rhs) {
		CCMatrix4f copy = new CCMatrix4f(rhs);
		CCMatrix4f inverse = copy.invert();
		if (inverse == null)
			return false;
		preApply(inverse);
		return true;
	}

	public boolean invApply(
		float n00, float n10, float n20, float n30, 
		float n01, float n11, float n21, float n31, 
		float n02, float n12, float n22, float n32, 
		float n03, float n13, float n23, float n33
	) {
		if (inverseCopy == null) {
			inverseCopy = new CCMatrix4f();
		}
		inverseCopy.set(
			n00, n10, n20, n30, 
			n01, n11, n21, n31, 
			n02, n12, n22, n32, 
			n03, n13, n23, n33
		);
		
		CCMatrix4f inverse = inverseCopy.invert();
		if (inverse == null)
			return false;
		preApply(inverse);
		return true;
	}

	/**
	 * Applies the 
	 * @param theMatrix
	 */
	public CCMatrix4f apply(CCMatrix4f theMatrix) {
		return apply(
			theMatrix.m00, theMatrix.m01, theMatrix.m02, theMatrix.m03, 
			theMatrix.m10, theMatrix.m11, theMatrix.m12, theMatrix.m13, 
			theMatrix.m20, theMatrix.m21, theMatrix.m22, theMatrix.m23, 
			theMatrix.m30, theMatrix.m31, theMatrix.m32, theMatrix.m33
		);
	}

	public CCMatrix4f apply(
		float n00, float n01, float n02, float n03, 
		float n10, float n11, float n12, float n13, 
		float n20, float n21, float n22, float n23, 
		float n30, float n31, float n32, float n33
	) {

		float r00 = m00 * n00 + m01 * n10 + m02 * n20 + m03 * n30;
		float r01 = m00 * n01 + m01 * n11 + m02 * n21 + m03 * n31;
		float r02 = m00 * n02 + m01 * n12 + m02 * n22 + m03 * n32;
		float r03 = m00 * n03 + m01 * n13 + m02 * n23 + m03 * n33;

		float r10 = m10 * n00 + m11 * n10 + m12 * n20 + m13 * n30;
		float r11 = m10 * n01 + m11 * n11 + m12 * n21 + m13 * n31;
		float r12 = m10 * n02 + m11 * n12 + m12 * n22 + m13 * n32;
		float r13 = m10 * n03 + m11 * n13 + m12 * n23 + m13 * n33;

		float r20 = m20 * n00 + m21 * n10 + m22 * n20 + m23 * n30;
		float r21 = m20 * n01 + m21 * n11 + m22 * n21 + m23 * n31;
		float r22 = m20 * n02 + m21 * n12 + m22 * n22 + m23 * n32;
		float r23 = m20 * n03 + m21 * n13 + m22 * n23 + m23 * n33;

		float r30 = m30 * n00 + m31 * n10 + m32 * n20 + m33 * n30;
		float r31 = m30 * n01 + m31 * n11 + m32 * n21 + m33 * n31;
		float r32 = m30 * n02 + m31 * n12 + m32 * n22 + m33 * n32;
		float r33 = m30 * n03 + m31 * n13 + m32 * n23 + m33 * n33;

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
		
		return this;
	}
	
	public CCVector3f transform(final CCVector3f theInput, CCVector3f theOutput) {
		
		if(theOutput == null)theOutput = new CCVector3f();
		// must use these temp vars because vec may be the same as out
		float tmpx = m00 * theInput.x + m01 * theInput.y + m02 * theInput.z + m03;
		float tmpy = m10 * theInput.x + m11 * theInput.y + m12 * theInput.z + m13;
		float tmpz = m20 * theInput.x + m21 * theInput.y + m22 * theInput.z + m23;

		theOutput.set(tmpx, tmpy, tmpz);
		
		return theOutput;
	}
	
	public CCVector3f transform(final CCVector3f theInput){
		return transform(theInput,theInput);
	}

	public CCVector4f transform(final CCVector4f theVector) {
		return transform(theVector,null);
	}

	/**
	 * @param theAxis
	 */
	public CCVector3f rotateAxis(CCVector3f theAxis) {
		transform(theAxis, theAxis);
		theAxis.normalize();
		return theAxis;
	}
	
	public void setRotationFromMatrix (CCVector3f theVector) {
		float cosY = CCMath.cos(theVector.y);

		theVector.y = CCMath.asin(m02);

		if ( Math.abs( cosY ) > 0.00001 ) {
			theVector.x = CCMath.atan2( - m12 / cosY, m22 / cosY );
			theVector.z = CCMath.atan2( - m01 / cosY, m00 / cosY );
		} else {
			theVector.x = 0;
			theVector.z = CCMath.atan2(m10, m11 );
		}
	}
	
	public CCVector4f transform(final CCVector4f theInput, CCVector4f theOutput) {
		
		if(theOutput == null)theOutput = new CCVector4f();
		// must use these temp vars because vec may be the same as out
		float tmpx = m00 * theInput.x + m01 * theInput.y + m02 * theInput.z + m03 * theInput.w;
		float tmpy = m10 * theInput.x + m11 * theInput.y + m12 * theInput.z + m13 * theInput.w;
		float tmpz = m20 * theInput.x + m21 * theInput.y + m22 * theInput.z + m23 * theInput.w;
		float tmpw = m30 * theInput.x + m31 * theInput.y + m32 * theInput.z + m33 * theInput.w;

		theOutput.set(tmpx, tmpy, tmpz,tmpw);
		
		return theOutput;
	}

	
	
	public void inverseTransform(final CCVector3f theInput, final CCVector3f theOutput) {
		float inx = theInput.x  - m03;
		float iny = theInput.y  - m13;
		float inz = theInput.z  - m23;
		float tmpx = m00 * inx + m10 * iny + m20 * inz;
		float tmpy = m01 * inx + m11 * iny + m21 * inz;
		float tmpz = m02 * inx + m12 * iny + m22 * inz;
//		System.out.println(m30+":"+m31+":"+m32+"   "+m03+":"+m13+":"+m23);
		theOutput.set(tmpx, tmpy, tmpz);
	}

	public CCVector3f inverseTransform(final CCVector3f theVector) {
		inverseTransform(theVector,theVector);
		return theVector;
	}

	/**
	 * @return the determinant of the matrix
	 */
	public float determinant() {
		float f;
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
	private float determinant3x3(
		float t00, float t01, float t02, 
		float t10, float t11, float t12, 
		float t20, float t21, float t22
	) {
		return
			t00 * (t11 * t22 - t12 * t21) + 
			t01 * (t12 * t20 - t10 * t22) + 
			t02 * (t10 * t21 - t11 * t20);
	}

	public CCMatrix4f transpose() {
		float temp;
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
	public CCMatrix4f invert() {

		float determinant = determinant();

		if (determinant != 0) {
			// m00 m01 m02 m03
			// m10 m11 m12 m13
			// m20 m21 m22 m23
			// m30 m31 m32 m33
			float determinant_inv = 1f / determinant;

			// first row
			float t00 = determinant3x3(m11, m12, m13, m21, m22, m23, m31, m32, m33);
			float t01 = -determinant3x3(m10, m12, m13, m20, m22, m23, m30, m32, m33);
			float t02 = determinant3x3(m10, m11, m13, m20, m21, m23, m30, m31, m33);
			float t03 = -determinant3x3(m10, m11, m12, m20, m21, m22, m30, m31, m32);

			// second row
			float t10 = -determinant3x3(m01, m02, m03, m21, m22, m23, m31, m32, m33);
			float t11 = determinant3x3(m00, m02, m03, m20, m22, m23, m30, m32, m33);
			float t12 = -determinant3x3(m00, m01, m03, m20, m21, m23, m30, m31, m33);
			float t13 = determinant3x3(m00, m01, m02, m20, m21, m22, m30, m31, m32);

			// third row
			float t20 = determinant3x3(m01, m02, m03, m11, m12, m13, m31, m32, m33);
			float t21 = -determinant3x3(m00, m02, m03, m10, m12, m13, m30, m32, m33);
			float t22 = determinant3x3(m00, m01, m03, m10, m11, m13, m30, m31, m33);
			float t23 = -determinant3x3(m00, m01, m02, m10, m11, m12, m30, m31, m32);

			// fourth row
			float t30 = -determinant3x3(m01, m02, m03, m11, m12, m13, m21, m22, m23);
			float t31 = determinant3x3(m00, m02, m03, m10, m12, m13, m20, m22, m23);
			float t32 = -determinant3x3(m00, m01, m03, m10, m11, m13, m20, m21, m23);
			float t33 = determinant3x3(m00, m01, m02, m10, m11, m12, m20, m21, m22);

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
			CCFormatUtil.nfs(m00, d, 4) + " " + 
			CCFormatUtil.nfs(m01, d, 4) + " " + 
			CCFormatUtil.nfs(m02, d, 4) + " " + 
			CCFormatUtil.nfs(m03, d, 4) + "\n");

		myStringBuilder.append(
			CCFormatUtil.nfs(m10, d, 4) + " " + 
			CCFormatUtil.nfs(m11, d, 4) + " " + 
			CCFormatUtil.nfs(m12, d, 4) + " " + 
			CCFormatUtil.nfs(m13, d, 4) + "\n");

		myStringBuilder.append(
			CCFormatUtil.nfs(m20, d, 4) + " " + 
			CCFormatUtil.nfs(m21, d, 4) + " " + 
			CCFormatUtil.nfs(m22, d, 4) + " " + 
			CCFormatUtil.nfs(m23, d, 4) + "\n");

		myStringBuilder.append(
			CCFormatUtil.nfs(m30, d, 4) + " " + 
			CCFormatUtil.nfs(m31, d, 4) + " " + 
			CCFormatUtil.nfs(m32, d, 4) + " " + 
			CCFormatUtil.nfs(m33, d, 4) + "\n");

		return myStringBuilder.toString();
	}
	
	public CCMatrix4f clone() {
		return new CCMatrix4f(this);
	}

	//////////////////////////////////////////////////////////////
	
	private final float maxabs(float ... theValue) {
		float result = Float.MIN_VALUE;
		for(float myValue:theValue) {
			result = CCMath.max(CCMath.abs(myValue), result);
		}
		return result;
	}

	private static final float sin(float theAngle) {
		return (float) Math.sin(theAngle);
	}

	private static final float cos(float theAngle) {
		return (float) Math.cos(theAngle);
	}

	public FloatBuffer toFloatBuffer() {
		final FloatBuffer myResult = FloatBuffer.allocate(16);

		myResult.put(m00);myResult.put(m10);myResult.put(m20);myResult.put(m30);
		myResult.put(m01);myResult.put(m11);myResult.put(m21);myResult.put(m31);
		myResult.put(m02);myResult.put(m12);myResult.put(m22);myResult.put(m32);
		myResult.put(m03);myResult.put(m13);myResult.put(m23);myResult.put(m33);
		myResult.rewind();
		return myResult;
	}
	
	static public CCMatrix4f createFromGLMatrix(final FloatBuffer theGLMatrix){
		theGLMatrix.rewind();
		CCMatrix4f myResult = new CCMatrix4f();
		myResult.m00 = theGLMatrix.get();myResult.m10 = theGLMatrix.get();myResult.m20 = theGLMatrix.get();myResult.m30 = theGLMatrix.get();
		myResult.m01 = theGLMatrix.get();myResult.m11 = theGLMatrix.get();myResult.m21 = theGLMatrix.get();myResult.m31 = theGLMatrix.get();
		myResult.m02 = theGLMatrix.get();myResult.m12 = theGLMatrix.get();myResult.m22 = theGLMatrix.get();myResult.m32 = theGLMatrix.get();
		myResult.m03 = theGLMatrix.get();myResult.m13 = theGLMatrix.get();myResult.m23 = theGLMatrix.get();myResult.m33 = theGLMatrix.get();
		return myResult;
	}
	
	static public CCMatrix4f createFromGLMatrix(final double[] theGLMatrix){
		CCMatrix4f myResult = new CCMatrix4f();
		myResult.m00 = (float)theGLMatrix[ 0];myResult.m10 = (float)theGLMatrix[ 1];myResult.m20 = (float)theGLMatrix[ 2];myResult.m30 = (float)theGLMatrix[ 3];
		myResult.m01 = (float)theGLMatrix[ 4];myResult.m11 = (float)theGLMatrix[ 5];myResult.m21 = (float)theGLMatrix[ 6];myResult.m31 = (float)theGLMatrix[ 7];
		myResult.m02 = (float)theGLMatrix[ 8];myResult.m12 = (float)theGLMatrix[ 9];myResult.m22 = (float)theGLMatrix[10];myResult.m32 = (float)theGLMatrix[11];
		myResult.m03 = (float)theGLMatrix[12];myResult.m13 = (float)theGLMatrix[13];myResult.m23 = (float)theGLMatrix[14];myResult.m33 = (float)theGLMatrix[15];
		return myResult;
	}
	
	static public CCMatrix4f multiply(CCMatrix4f theM1, CCMatrix4f theM2) {
		return new CCMatrix4f(
			theM1.m00 * theM2.m00 + theM1.m01 * theM2.m10 + theM1.m02 * theM2.m20 + theM1.m03 * theM2.m30,
			theM1.m10 * theM2.m00 + theM1.m11 * theM2.m10 + theM1.m12 * theM2.m20 + theM1.m13 * theM2.m30,
			theM1.m20 * theM2.m00 + theM1.m21 * theM2.m10 + theM1.m22 * theM2.m20 + theM1.m23 * theM2.m30,
			theM1.m30 * theM2.m00 + theM1.m31 * theM2.m10 + theM1.m32 * theM2.m20 + theM1.m33 * theM2.m30,
			
			theM1.m00 * theM2.m01 + theM1.m01 * theM2.m11 + theM1.m02 * theM2.m21 + theM1.m03 * theM2.m31,
			theM1.m10 * theM2.m01 + theM1.m11 * theM2.m11 + theM1.m12 * theM2.m21 + theM1.m13 * theM2.m31,
			theM1.m20 * theM2.m01 + theM1.m21 * theM2.m11 + theM1.m22 * theM2.m21 + theM1.m23 * theM2.m31,
			theM1.m30 * theM2.m01 + theM1.m31 * theM2.m11 + theM1.m32 * theM2.m21 + theM1.m33 * theM2.m31,
			
			theM1.m00 * theM2.m02 + theM1.m01 * theM2.m12 + theM1.m02 * theM2.m22 + theM1.m03 * theM2.m32,
			theM1.m10 * theM2.m02 + theM1.m11 * theM2.m12 + theM1.m12 * theM2.m22 + theM1.m13 * theM2.m32,
			theM1.m20 * theM2.m02 + theM1.m21 * theM2.m12 + theM1.m22 * theM2.m22 + theM1.m23 * theM2.m32,
			theM1.m30 * theM2.m02 + theM1.m31 * theM2.m12 + theM1.m32 * theM2.m22 + theM1.m33 * theM2.m32,
			
			theM1.m00 * theM2.m03 + theM1.m01 * theM2.m13 + theM1.m02 * theM2.m23 + theM1.m03 * theM2.m33,
			theM1.m10 * theM2.m03 + theM1.m11 * theM2.m13 + theM1.m12 * theM2.m23 + theM1.m13 * theM2.m33,
			theM1.m20 * theM2.m03 + theM1.m21 * theM2.m13 + theM1.m22 * theM2.m23 + theM1.m23 * theM2.m33,
			theM1.m30 * theM2.m03 + theM1.m31 * theM2.m13 + theM1.m32 * theM2.m23 + theM1.m33 * theM2.m33
		);
	}
	
	static public CCMatrix4f blend(float theBlend, CCMatrix4f theM1, CCMatrix4f theM2) {
		return new CCMatrix4f(
			CCMath.blend(theM1.m00, theM2.m00, theBlend),
			CCMath.blend(theM1.m10, theM2.m10, theBlend),
			CCMath.blend(theM1.m20, theM2.m20, theBlend),
			CCMath.blend(theM1.m30, theM2.m30, theBlend),

			CCMath.blend(theM1.m01, theM2.m01, theBlend),	
			CCMath.blend(theM1.m11, theM2.m11, theBlend),
			CCMath.blend(theM1.m21, theM2.m21, theBlend),
			CCMath.blend(theM1.m31, theM2.m31, theBlend),

			CCMath.blend(theM1.m02, theM2.m02, theBlend),
			CCMath.blend(theM1.m12, theM2.m12, theBlend),
			CCMath.blend(theM1.m22, theM2.m22, theBlend),
			CCMath.blend(theM1.m32, theM2.m32, theBlend),

			CCMath.blend(theM1.m03, theM2.m03, theBlend),
			CCMath.blend(theM1.m13, theM2.m13, theBlend),
			CCMath.blend(theM1.m23, theM2.m23, theBlend),
			CCMath.blend(theM1.m33, theM2.m33, theBlend)
		);
	}

	/**
	 * @param theI
	 * @return
	 */
	public float[] row(int theI) {
		float[] result = new float[] {0,0,0,0};
		
		switch(theI) {
		case 0:
			result[0] = m00; result[1] = m10; result[2] = m20; result[3] = m30;
			break;
		case 1:
			result[0] = m01; result[1] = m11; result[2] = m21; result[3] = m31;
			break;
		case 2:
			result[0] = m02; result[1] = m12; result[2] = m22; result[3] = m32;
			break;
		case 3:
			result[0] = m03; result[1] = m13; result[2] = m23; result[3] = m33;
			break;
		}
			
		return result;
	}
	
	public void position(CCVector3f thePosition) {
		m03 = thePosition.x;
		m13 = thePosition.y;
		m23 = thePosition.z;
	}
	
	public void position(float theX, float theY, float theZ) {
		m03 = theX;
		m13 = theY;
		m23 = theZ;
	}

	public CCVector3f getPosition() {
		return new CCVector3f(m03, m13, m23);
	}
	
	public CCVector3f yawPitchRoll() {
		float yaw, pitch, roll;

		pitch = CCMath.asin(-m21);

		float threshold = 0.001f; // Hardcoded constant - burn him, he's a witch
		float test = cos(pitch);

		if (test > threshold) {
			roll = CCMath.atan2(m01, m11);
			yaw = CCMath.atan2(m20, m22);
		} else {
			roll = CCMath.atan2(-m10, m00);
			yaw = 0.0f;
		}

		return new CCVector3f(yaw, pitch, roll);
	}
	
	public void setRotationFromQuaternion(CCQuaternion q) {
		float x = q.x;
		float y = q.y;
		float z = q.z;
		float w = q.w;
		
		float x2 = x + x;
		float y2 = y + y; 
		float z2 = z + z;
		
		float xx = x * x2; float xy = x * y2; float xz = x * z2;
		float yy = y * y2; float yz = y * z2; float zz = z * z2;
		float wx = w * x2; float wy = w * y2; float wz = w * z2;

		m00 = 1 - ( yy + zz );
		m01 = xy - wz;
		m02 = xz + wy;

		m10 = xy + wz;
		m11 = 1 - ( xx + zz );
		m12 = yz - wx;

		m20 = xz - wy;
		m21 = yz + wx;
		m22 = 1 - ( xx + yy );
	}

	public CCMatrix4f add(CCMatrix4f m) {
		return new CCMatrix4f(
			m00 + m.m00, m10 + m.m10, m20 + m.m20, m30 + m.m30,
			m01 + m.m01, m11 + m.m11, m21 + m.m21, m31 + m.m31,
			m02 + m.m02, m12 + m.m12, m22 + m.m22, m32 + m.m32,
			m03 + m.m03, m13 + m.m13, m23 + m.m23, m33 + m.m33
		);
	}
	
	public CCMatrix4f multiply (float s) {
		return new CCMatrix4f(
			m00 * s, m10 * s, m20 * s, m30 * s,
			m01 * s, m11 * s, m21 * s, m31 * s,
			m02 * s, m12 * s, m22 * s, m32 * s,
			m03 * s, m13 * s, m23 * s, m33 * s
		);
	}
	
	public void multiplyScalar (CCVector3f v) {
		m00 *= v.x; m01 *= v.y; m02 *= v.z;
		m10 *= v.x; m11 *= v.y; m12 *= v.z;
		m20 *= v.x; m21 *= v.y; m22 *= v.z;
		m30 *= v.x; m31 *= v.y; m32 *= v.z;
	}
	
	public CCMatrix4f multiply(CCMatrix4f m){
		return new CCMatrix4f(
			m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30,
			m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30,
			m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30,
			m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30,
			
			m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31,
			m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31,
			m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31,
			m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31,
				
			m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32,
			m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32,
			m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32,
			m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32,
				
			m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33,
			m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33,
			m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33,
			m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33
		);
	}
	
	public boolean equals(CCMatrix4f theOtherMatrix) {
        return (
        	m00 == theOtherMatrix.m00 && m10 == theOtherMatrix.m10 && m20 == theOtherMatrix.m20 && m30 == theOtherMatrix.m30 && 
        	m01 == theOtherMatrix.m01 && m11 == theOtherMatrix.m11 && m21 == theOtherMatrix.m21 && m31 == theOtherMatrix.m31 &&
        	m02 == theOtherMatrix.m02 && m12 == theOtherMatrix.m12 && m22 == theOtherMatrix.m22 && m32 == theOtherMatrix.m32 && 
        	m03 == theOtherMatrix.m03 && m13 == theOtherMatrix.m13 && m23 == theOtherMatrix.m23 && m33 == theOtherMatrix.m33
        );
    }
	
	
	
	public void extractRotation(CCMatrix4f m, CCVector3f s ) {
		float invScaleX = 1 / s.x;
		float invScaleY = 1 / s.y; 
		float invScaleZ = 1 / s.z;

		m00 = m.m00 * invScaleX;
		m10 = m.m10 * invScaleX;
		m20 = m.m20 * invScaleX;

		m01 = m.m01 * invScaleY;
		m11 = m.m11 * invScaleY;
		m21 = m.m21 * invScaleY;

		m02 = m.m02 * invScaleZ;
		m12 = m.m12 * invScaleZ;
		m22 = m.m22 * invScaleZ;

	}

	
	/**
	 * Creates a perspective projection matrix using field-of-view and 
	 * aspect ratio to determine the left, right, top, bottom planes.  This
	 * method is analogous to the now deprecated {@code gluPerspective} method.
	 * 
	 * @param fovy field of view angle, in degrees, in the {@code y} direction
	 * @param aspect aspect ratio that determines the field of view in the x 
	 * direction.  The aspect ratio is the ratio of {@code x} (width) to 
	 * {@code y} (height).
	 * @param zNear near plane distance from the viewer to the near clipping plane (always positive)
	 * @param zFar far plane distance from the viewer to the far clipping plane (always positive)
	 * @return
	 */
	public static final CCMatrix4f perspective(final float fovy, final float aspect, final float zNear, final float zFar) {
		final float halfFovyRadians =  CCMath.radians( (fovy / 2.0f) );
		final float range =  CCMath.tan(halfFovyRadians) * zNear;
		final float left = -range * aspect;
		final float right = range * aspect;
		final float bottom = -range;
		final float top = range;
		
		return new CCMatrix4f(
				(2f * zNear) / (right - left), 0f, 0f, 0f,
				0f, (2f * zNear) / (top - bottom), 0f, 0f,
				0f, 0f, -(zFar + zNear) / (zFar - zNear), -1f,
				0f, 0f, -(2f * zFar * zNear) / (zFar - zNear), 0f
		);
	}
	
	/**
	 * Creates a perspective projection matrix (frustum) using explicit
	 * values for all clipping planes.  This method is analogous to the now
	 * deprecated {@code glFrustum} method.
	 * 
	 * @param left left vertical clipping plane
	 * @param right right vertical clipping plane
	 * @param bottom bottom horizontal clipping plane
	 * @param top top horizontal clipping plane
	 * @param nearVal distance to the near depth clipping plane (must be positive)
	 * @param farVal distance to the far depth clipping plane (must be positive)
	 * @return
	 */
	public static final CCMatrix4f frustum(final float left, final float right, final float bottom, final float top, final float nearVal, final float farVal) {
		final float m00 = (2f * nearVal) / (right - left);
		final float m11 = (2f * nearVal) / (top - bottom);
		final float m20 = (right + left) / (right - left);
		final float m21 = (top + bottom) / (top - bottom);
		final float m22 = -(farVal + nearVal) / (farVal - nearVal);
		final float m23 = -1f;
		final float m32 = -(2f * farVal * nearVal) / (farVal - nearVal);
		
		return new CCMatrix4f(
				m00, 0f, 0f, 0f, 
				0f, m11, 0f, 0f, 
				m20, m21, m22, m23, 
				0f, 0f, m32, 0f
		);
	}
	
	/**
	 * Defines a viewing transformation.  This method is analogous to the now
	 * deprecated {@code gluLookAt} method.
	 * 
	 * @param eye position of the eye point
	 * @param center position of the reference point
	 * @param up direction of the up vector
	 * @return
	 */
	public static final CCMatrix4f lookAt(final CCVector3f eye, final CCVector3f center, final CCVector3f up) {
		final CCVector3f f = center.clone().subtract(eye).normalize();
		CCVector3f u = up.clone().normalize();
		final CCVector3f s = f.cross(u).normalize();
		u = s.cross(f);
		
		return new CCMatrix4f(
			s.x, u.x, -f.x, 0f,
			s.y, u.y, -f.y, 0f,
			s.z, u.z, -f.z, 0f,
			-s.dot(eye), -u.dot(eye), f.dot(eye), 1f
		);
	}
	
	public static CCMatrix4f makeInvert (CCMatrix4f m1) {

		// based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm

		float m00 = m1.m00; float m01 = m1.m01; float m02 = m1.m02; float m03 = m1.m03;
		float m10 = m1.m10; float m11 = m1.m11; float m12 = m1.m12; float m13 = m1.m13;
		float m20 = m1.m20; float m21 = m1.m21; float m22 = m1.m22; float m23 = m1.m23;
		float m30 = m1.m30; float m31 = m1.m31; float m32 = m1.m32; float m33 = m1.m33;

		CCMatrix4f m2 = new CCMatrix4f();

		m2.m00 = m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11 * m23 * m32 - m12 * m21 * m33 + m11 * m22 * m33;
		m2.m01 = m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01 * m23 * m32 + m02 * m21 * m33 - m01 * m22 * m33;
		m2.m02 = m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01 * m13 * m32 - m02 * m11 * m33 + m01 * m12 * m33;
		m2.m03 = m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01 * m13 * m22 + m02 * m11 * m23 - m01 * m12 * m23;
		m2.m10 = m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10 * m23 * m32 + m12 * m20 * m33 - m10 * m22 * m33;
		m2.m11 = m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00 * m23 * m32 - m02 * m20 * m33 + m00 * m22 * m33;
		m2.m12 = m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00 * m13 * m32 + m02 * m10 * m33 - m00 * m12 * m33;
		m2.m13 = m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00 * m13 * m22 - m02 * m10 * m23 + m00 * m12 * m23;
		m2.m20 = m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10 * m23 * m31 - m11 * m20 * m33 + m10 * m21 * m33;
		m2.m21 = m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00 * m23 * m31 + m01 * m20 * m33 - m00 * m21 * m33;
		m2.m22 = m02 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00 * m13 * m31 - m01 * m10 * m33 + m00 * m11 * m33;
		m2.m23 = m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00 * m13 * m21 + m01 * m10 * m23 - m00 * m11 * m23;
		m2.m30 = m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10 * m22 * m31 + m11 * m20 * m32 - m10 * m21 * m32;
		m2.m31 = m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00 * m22 * m31 - m01 * m20 * m32 + m00 * m21 * m32;
		m2.m32 = m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00 * m12 * m31 + m01 * m10 * m32 - m00 * m11 * m32;
		m2.m33 = m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22 + m00 * m11 * m22;
		return m2.multiply( 1 / m1.determinant() );
	}
}
