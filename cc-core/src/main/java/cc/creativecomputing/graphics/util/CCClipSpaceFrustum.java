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
package cc.creativecomputing.graphics.util;

import javax.media.opengl.fixedfunc.GLMatrixFunc;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Another approach to extract the view frustum planes is presented based on the properties of clip space.
 * Consider a point p = (x,y,z,1) on the 3D world. Consider also a modelview matrix M and a projection matrix P. 
 * The point p is transformed by matrices M and P as point pc =(xc,yc,zc,wc) in clip space using:
 * <blockquote>
 * pc = pMP
 * pc = (xc, yc, zc, wc)
 * </blockquote>
 * @author christianriekoff
 *
 */
public class CCClipSpaceFrustum {

	private final CCGraphics g;
	private final float[][] m_Frustum = new float[6][4];
	
	boolean hasChanged = true;

	public final static int RIGHT = 0;
	public final static int LEFT = 1;
	public final static int BOTTOM = 2;
	public final static int TOP = 3;
	public final static int FAR = 4;
	public final static int NEAR = 5;
	public final static int INSIDE = -1;

	public CCClipSpaceFrustum(final CCGraphics g) {
		this.g = g;
		update();
	}
	
	public void update() {
		
		float[] clip = new float[16];
		float[] proj = new float[16];
		float[] modl = new float[16];
		float t;

		// Get The Current PROJECTION Matrix From OpenGL
		g.gl.glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, proj, 0);

		// Get The Current MODELVIEW Matrix From OpenGL
		g.gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, modl, 0);

		// Combine The Two Matrices (Multiply Projection By Modelview)
		clip[0] = modl[0] * proj[0] + modl[1] * proj[4] + modl[2] * proj[8] + modl[3] * proj[12];
		clip[1] = modl[0] * proj[1] + modl[1] * proj[5] + modl[2] * proj[9] + modl[3] * proj[13];
		clip[2] = modl[0] * proj[2] + modl[1] * proj[6] + modl[2] * proj[10] + modl[3] * proj[14];
		clip[3] = modl[0] * proj[3] + modl[1] * proj[7] + modl[2] * proj[11] + modl[3] * proj[15];

		clip[4] = modl[4] * proj[0] + modl[5] * proj[4] + modl[6] * proj[8] + modl[7] * proj[12];
		clip[5] = modl[4] * proj[1] + modl[5] * proj[5] + modl[6] * proj[9] + modl[7] * proj[13];
		clip[6] = modl[4] * proj[2] + modl[5] * proj[6] + modl[6] * proj[10] + modl[7] * proj[14];
		clip[7] = modl[4] * proj[3] + modl[5] * proj[7] + modl[6] * proj[11] + modl[7] * proj[15];

		clip[8] = modl[8] * proj[0] + modl[9] * proj[4] + modl[10] * proj[8] + modl[11] * proj[12];
		clip[9] = modl[8] * proj[1] + modl[9] * proj[5] + modl[10] * proj[9] + modl[11] * proj[13];
		clip[10] = modl[8] * proj[2] + modl[9] * proj[6] + modl[10] * proj[10] + modl[11] * proj[14];
		clip[11] = modl[8] * proj[3] + modl[9] * proj[7] + modl[10] * proj[11] + modl[11] * proj[15];

		clip[12] = modl[12] * proj[0] + modl[13] * proj[4] + modl[14] * proj[8] + modl[15] * proj[12];
		clip[13] = modl[12] * proj[1] + modl[13] * proj[5] + modl[14] * proj[9] + modl[15] * proj[13];
		clip[14] = modl[12] * proj[2] + modl[13] * proj[6] + modl[14] * proj[10] + modl[15] * proj[14];
		clip[15] = modl[12] * proj[3] + modl[13] * proj[7] + modl[14] * proj[11] + modl[15] * proj[15];

		// Extract The Numbers For The RIGHT Plane
		m_Frustum[0][0] = clip[3] - clip[0];
		m_Frustum[0][1] = clip[7] - clip[4];
		m_Frustum[0][2] = clip[11] - clip[8];
		m_Frustum[0][3] = clip[15] - clip[12];

		// Normalize The Result
		t = CCMath.sqrt(m_Frustum[0][0] * m_Frustum[0][0] + m_Frustum[0][1] * m_Frustum[0][1] + m_Frustum[0][2] * m_Frustum[0][2]);
		m_Frustum[0][0] /= t;
		m_Frustum[0][1] /= t;
		m_Frustum[0][2] /= t;
		m_Frustum[0][3] /= t;

		// Extract The Numbers For The LEFT Plane
		m_Frustum[1][0] = clip[3] + clip[0];
		m_Frustum[1][1] = clip[7] + clip[4];
		m_Frustum[1][2] = clip[11] + clip[8];
		m_Frustum[1][3] = clip[15] + clip[12];

		// Normalize The Result
		t = CCMath.sqrt(m_Frustum[1][0] * m_Frustum[1][0] + m_Frustum[1][1] * m_Frustum[1][1] + m_Frustum[1][2] * m_Frustum[1][2]);
		m_Frustum[1][0] /= t;
		m_Frustum[1][1] /= t;
		m_Frustum[1][2] /= t;
		m_Frustum[1][3] /= t;

		// Extract The BOTTOM Plane
		m_Frustum[2][0] = clip[3] + clip[1];
		m_Frustum[2][1] = clip[7] + clip[5];
		m_Frustum[2][2] = clip[11] + clip[9];
		m_Frustum[2][3] = clip[15] + clip[13];

		// Normalize The Result
		t = CCMath.sqrt(m_Frustum[2][0] * m_Frustum[2][0] + m_Frustum[2][1] * m_Frustum[2][1] + m_Frustum[2][2] * m_Frustum[2][2]);
		m_Frustum[2][0] /= t;
		m_Frustum[2][1] /= t;
		m_Frustum[2][2] /= t;
		m_Frustum[2][3] /= t;

		// Extract The TOP Plane
		m_Frustum[3][0] = clip[3] - clip[1];
		m_Frustum[3][1] = clip[7] - clip[5];
		m_Frustum[3][2] = clip[11] - clip[9];
		m_Frustum[3][3] = clip[15] - clip[13];

		// Normalize The Result
		t = CCMath.sqrt(m_Frustum[3][0] * m_Frustum[3][0] + m_Frustum[3][1] * m_Frustum[3][1] + m_Frustum[3][2] * m_Frustum[3][2]);
		m_Frustum[3][0] /= t;
		m_Frustum[3][1] /= t;
		m_Frustum[3][2] /= t;
		m_Frustum[3][3] /= t;

		// Extract The FAR Plane
		m_Frustum[4][0] = clip[3] - clip[2];
		m_Frustum[4][1] = clip[7] - clip[6];
		m_Frustum[4][2] = clip[11] - clip[10];
		m_Frustum[4][3] = clip[15] - clip[14];

		// Normalize The Result
		t = CCMath.sqrt(m_Frustum[4][0] * m_Frustum[4][0] + m_Frustum[4][1] * m_Frustum[4][1] + m_Frustum[4][2] * m_Frustum[4][2]);
		m_Frustum[4][0] /= t;
		m_Frustum[4][1] /= t;
		m_Frustum[4][2] /= t;
		m_Frustum[4][3] /= t;

		// Extract The NEAR Plane
		m_Frustum[5][0] = clip[3] + clip[2];
		m_Frustum[5][1] = clip[7] + clip[6];
		m_Frustum[5][2] = clip[11] + clip[10];
		m_Frustum[5][3] = clip[15] + clip[14];

		// Normalize The Result
		t = CCMath.sqrt(m_Frustum[5][0] * m_Frustum[5][0] + m_Frustum[5][1] * m_Frustum[5][1] + m_Frustum[5][2] * m_Frustum[5][2]);
		m_Frustum[5][0] /= t;
		m_Frustum[5][1] /= t;
		m_Frustum[5][2] /= t;
		m_Frustum[5][3] /= t;
	}
	
	/* CHECK IF POINT IS IN FRUSTUM */

	/**
	 * The Idea Behind This Algorithm Is That If The Point 
	 * Is Inside All 6 Clipping Planes Then It Is Inside Our
	 * Viewing Volume So We Can Return True.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int isInFrustum(float x, float y, float z) {
		for (int i = 0; i < 6; i++) {
			if (m_Frustum[i][0] * x + m_Frustum[i][1] * y + m_Frustum[i][2] * z + m_Frustum[i][3] <= 0) {
				return i;
			}
		}
		return -1;
	}
	
	public int isInFrustum(final CCVector3f theVector){
		return isInFrustum(theVector.x,theVector.y,theVector.z);
	}
	
	/* CHECK IF SPHERE IS IN FRUSTUM */
	
	public int isInFrustum(final CCVector3f theCenter, final float theRadius){
		return isInFrustum(theCenter.x, theCenter.y, theCenter.z, theRadius);
	}

	public int isInFrustum(final float theCenterX, final float theCenterY, final float theCenterZ, final float theRadius) {
		for (int i = 0; i < 6; i++) {
			if (m_Frustum[i][0] * theCenterX + m_Frustum[i][1] * theCenterY + m_Frustum[i][2] * theCenterZ + m_Frustum[i][3] <= -theRadius) {
				return i;
			}
		}
		return -1;
	}
	
	public float distance(final CCVector3f theCenter, final int theSide){
		return distance(theCenter.x, theCenter.y, theCenter.z, theSide);
	}
	
	public float distance(final float theCenterX, final float theCenterY, final float theCenterZ, final int theSide){
		return m_Frustum[theSide][0] * theCenterX + m_Frustum[theSide][1] * theCenterY + m_Frustum[theSide][2] * theCenterZ + m_Frustum[theSide][3];
	}
	
	/* CHECK IF BOX IS IN FRUSTUM */
	
	public int isInFrustum(final CCAABB theBoundingBox){
		return cubeInFrustum(theBoundingBox.center(), theBoundingBox.extent());
	}

	public int isCubeInFrustum(final CCVector3f center, float width){
		return cubeInFrustum(center, new CCVector3f(width, width, width));
	}
	
	public int cubeInFrustum(CCVector3f center, CCVector3f size){
		float x = center.x;
		float y = center.y;
		float z = center.z;
		float sizeX = size.x/2.0f;
		float sizeY = size.y/2.0f;
		float sizeZ = size.z/2.0f;

		for(int i = 0; i < 6; i++){
			if(m_Frustum[i][0] * (x - sizeX) + m_Frustum[i][1] * (y - sizeY) + m_Frustum[i][2] * (z - sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x + sizeX) + m_Frustum[i][1] * (y - sizeY) + m_Frustum[i][2] * (z - sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x - sizeX) + m_Frustum[i][1] * (y + sizeY) + m_Frustum[i][2] * (z - sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x + sizeX) + m_Frustum[i][1] * (y + sizeY) + m_Frustum[i][2] * (z - sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x - sizeX) + m_Frustum[i][1] * (y - sizeY) + m_Frustum[i][2] * (z + sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x + sizeX) + m_Frustum[i][1] * (y - sizeY) + m_Frustum[i][2] * (z + sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x - sizeX) + m_Frustum[i][1] * (y + sizeY) + m_Frustum[i][2] * (z + sizeZ) + m_Frustum[i][3] > 0)continue;
			if(m_Frustum[i][0] * (x + sizeX) + m_Frustum[i][1] * (y + sizeY) + m_Frustum[i][2] * (z + sizeZ) + m_Frustum[i][3] > 0)continue;

			return i;
		}

		return -1;
	}
	
	public void frustumWrap(CCIFrustumWrapable theWrapable){
		int myFrustumMode = theWrapable.frustumMode(this);
		
		if(myFrustumMode == CCClipSpaceFrustum.INSIDE){
			return;
		}
		
		float myMoveX = 0;
		float myMoveY = 0;
		
		switch(myFrustumMode){
		case CCClipSpaceFrustum.LEFT:
			myMoveX = distance(theWrapable.frustumWrapPosition(), CCClipSpaceFrustum.RIGHT) + theWrapable.frustumWrapDimension().x;
			break;
		case CCClipSpaceFrustum.RIGHT:
			myMoveX = -distance(theWrapable.frustumWrapPosition(), CCClipSpaceFrustum.LEFT) - theWrapable.frustumWrapDimension().x;
			break;
		case CCClipSpaceFrustum.TOP:
			myMoveY = distance(theWrapable.frustumWrapPosition(), CCClipSpaceFrustum.BOTTOM) + theWrapable.frustumWrapDimension().y;
			break;
		case CCClipSpaceFrustum.BOTTOM:
			myMoveY = -distance(theWrapable.frustumWrapPosition(), CCClipSpaceFrustum.TOP) - theWrapable.frustumWrapDimension().y;
			break;
		}
		
		theWrapable.frustumWrap(new CCVector3f(myMoveX,myMoveY,0));
	}
}
