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
package cc.creativecomputing.cv.openni;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.openni.GeneralException;
import org.openni.Plane3D;
import org.openni.Point3D;
import org.openni.SceneAnalyzer;
import org.openni.StatusException;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVector3f;

/**
 * A Scene Analyzer node is a Map Generator that performs scene analysis. 
 * @author christianriekoff
 *
 */
public class CCOpenNISceneAnalyzer extends CCOpenNIMapGenerator<SceneAnalyzer>{
	

	private CCVector3f _myPlaneOrigin = new CCVector3f();
	private CCVector3f _myPlaneNormal = new CCVector3f();
	
	private CCPlane3f _myFloorPlane;

	private ShortBuffer _myRawData;

	/**
	 * @param theContext
	 */
	CCOpenNISceneAnalyzer(CCOpenNI theOpenNI) {
		super(theOpenNI, null);
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI, CCPixelFormat.LUMINANCE, CCPixelType.FLOAT);
		try{
			_myTexture = new CCTexture2D(_myTextureData);
			_myTexture.textureFilter(CCTextureFilter.NEAREST);
			_myTexture.mustFlipVertically(true);
		}catch(NullPointerException e){
			
		}
	}

	@Override
	SceneAnalyzer create(CCOpenNI theOpenNI) {
		try {
			if(theOpenNI.deviceQuery() == null)return SceneAnalyzer.create(theOpenNI.context());
			return SceneAnalyzer.create(theOpenNI.context(), theOpenNI.deviceQuery());
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public int[] idMap(int theStepSize) {
		int[] myPoints = idMap(theStepSize, 0, 0, _myWidth, _myHeight);
    	return myPoints;
    }
    
    public int[] idMap(int theStepSize, int theX, int theY, int theWidth, int theHeight) {
    	return idMap(theStepSize, theX, theY, theWidth, theHeight, true);
    }

    /**
     * Use this method to get the 3d positions based on the depth image of the sensor, you can use the
     * x, y, width and height parameters to query a sub area of the depth grid. with the step size you can
     * define the resolution of the query, 1 will give you a point for every pixel.
     * @param theStepSize resolution of the returned point cloud 
     * @param theX x position in the depth grid
     * @param theY y position in the depth grid
     * @param theWidth width of the sub area
     * @param theHeight height of the sub area
     * @param theApplyTransform define if you want to transform the 3d points to world coords
     * @return
     */
	public int[] idMap(int theStepSize, int theX, int theY, int theWidth, int theHeight, boolean theApplyTransform) {
		if (_myRawData == null)
			return null;
		
		if(theX + theWidth > _myWidth || theY + theHeight > _myHeight) return null;

		int myDataSize = theWidth * theHeight / theStepSize / theStepSize;
		int[] myResult = new int[myDataSize];

		
//		_myRealWorldNeedsUpdate = true;
		// }
		// if(_myRealWorldNeedsUpdate) {
//		_myRealWorldNeedsUpdate = false;
		_myRawData.rewind();
		int i = 0;
		for (int y = theY; y < theY + theHeight; y += theStepSize) {
			for (int x = theX; x < theX + theWidth; x += theStepSize) {
				int id = y * _myWidth + x;
				myResult[i] = _myRawData.get(id);
				i++;
			}
		}

		return myResult;
	}
	
	@Override
	void update(float theDeltaTime) {
		_myRawData = _myGenerator.getMetaData().getData().createShortBuffer();
		
		FloatBuffer myFloatBuffer = (FloatBuffer)_myTextureData.buffer();
		myFloatBuffer.rewind();
		_myRawData.rewind();
		while(_myRawData.hasRemaining()) {
			int myValue = _myRawData.get();
			myFloatBuffer.put(myValue);
		}
		myFloatBuffer.rewind();
		if(_myTexture != null)_myTexture.updateData(_myTextureData);
		
		try {
			Plane3D myFloor = _myGenerator.getFloor();
			Point3D myPoint = myFloor.getPoint();
			Point3D myNormal = myFloor.getNormal();
			CCVector3f myOrigin = new CCVector3f(myPoint.getX(), myPoint.getY(), myPoint.getZ());
			CCVector3f myCCNormal = new CCVector3f(myNormal.getX(), myNormal.getY(), myNormal.getZ());
			
			_myPlaneOrigin.set(myOrigin);
			_myPlaneNormal.set(myCCNormal);
			_myFloorPlane = new CCPlane3f(_myPlaneOrigin, _myPlaneNormal);
		} catch (StatusException e) {
		}
		
	}
	
	/**
	 * A point on the floor plane.
	 * @return point on the floor plane
	 */
	public CCVector3f floorPoint() {
		return _myPlaneOrigin;
	}
	
	/**
	 * The normal of the floor plane.
	 * @return normal of the floor plane
	 */
	public CCVector3f floorNormal() {
		return _myPlaneNormal;
	}
	
	public CCPlane3f floorPlane() {
		return _myFloorPlane;
	}

	@Override
	
	/**
	 * Gets the current scene map as texture.
	 * @return
	 */
	public CCTexture2D texture() {
		return _myTexture;
	}
	
	/**
	 * Gets the current scene data
	 * @return the current scene data
	 */
	public ShortBuffer rawData() {
		return _myRawData;
	}
}
