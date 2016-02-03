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

import org.openni.CodecID;
import org.openni.DepthGenerator;
import org.openni.FieldOfView;
import org.openni.GeneralException;
import org.openni.Point3D;
import org.openni.StatusException;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

/**
 * Represents an image generator
 * @author christianriekoff
 *
 */
public class CCOpenNIDepthGenerator extends CCOpenNIMapGenerator<DepthGenerator>{
	
	private CCVector3f[] _myRealWorldData;
	private Point3D[] _myProjectiveDataP3;
	
	private ShortBuffer _myRawData;
	private CCMatrix4f _myTransformation;
	
	@Override
	public DepthGenerator create(CCOpenNI theOpenNI) {
		try {
			if(theOpenNI.deviceQuery() == null)return DepthGenerator.create(theOpenNI.context());
			return DepthGenerator.create(theOpenNI.context(), theOpenNI.deviceQuery());
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	CCOpenNIDepthGenerator(CCOpenNI theOpenNI) {
		super(theOpenNI, CodecID.Z16WithTables);
		
		_myTextureData = new CCTextureData(_myWidth, _myHeight, CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI, CCPixelFormat.LUMINANCE, CCPixelType.FLOAT);
		if(CCGraphics.currentGL() != null){
		_myTexture = new CCTexture2D(_myTextureData);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		_myTexture.mustFlipVertically(true);
		_myTexture.generateMipmaps(true);
		_myTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		}
		
		_myTransformation = theOpenNI.transformationMatrix();
	}
	
	/**
	 * Returns the horizontal fov of the depth generator
	 * @return
	 */
	public float horizontalFieldOfView() {
		try {
			return (float)_myGenerator.getFieldOfView().getHFOV();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Returns the vertical fov of the depth generator
	 * @return
	 */
	public float verticalFieldOfView() {
		try {
			return (float)_myGenerator.getFieldOfView().getVFOV();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private boolean _myTextureNeedsUpdate = true;
	private boolean _myStopUpdateTexture = false;
//	private boolean _myRealWorldNeedsUpdate = true;
	
	public void stopUpdateTexture(boolean theStopUpdateTexture){
		_myStopUpdateTexture = theStopUpdateTexture;
	}
	
	@Override
	void update(float theDeltaTime) {
//		_myRealWorldNeedsUpdate = true;
		_myTextureNeedsUpdate = true;
		_myRawData = _myGenerator.getMetaData().getData().createShortBuffer();
	}
	
	/**
	 * Converts a list of points from projective coordinates to real world coordinates.
	 * @param projectivePoints projective coordinates
	 * @return real world coordinates
	 */
	public CCVector3f[] convertProjectiveToRealWorld(CCVector3f[] projectivePoints){
		Point3D[] projective = new Point3D[projectivePoints.length];
		for(int i = 0; i < projective.length;i++) {
			projective[i] = new Point3D(
				projectivePoints[i].x, 
				projectivePoints[i].y, 
				projectivePoints[i].z
			);
		}
		Point3D[] realWorld;
		try {
			realWorld = _myGenerator.convertProjectiveToRealWorld(projective);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}

		CCVector3f[] realWorld3f = new CCVector3f[projectivePoints.length];
		for(int i = 0; i < projective.length;i++) {
			realWorld3f[i] = convert(realWorld[i]);
			_myTransformation.inverseTransform(realWorld3f[i]);
		}
		return realWorld3f;
	}
	
	/**
	 * Converts a point from projective coordinates to real world coordinates.
	 * @param projectivePoint projective coordinates
	 * @return real world coordinates
	 */
	public CCVector3f convertProjectiveToRealWorld(CCVector3f projectivePoint) {
		CCVector3f[] projectivePoints = new CCVector3f[1];
        projectivePoints[0] = projectivePoint;

        return convertProjectiveToRealWorld(projectivePoints)[0];
    }

	/**
	 * Converts a list of points from real world coordinates to projective coordinates.
	 * @param realWorldPoints real world coordinates
	 * @return projective coordinates
	 */
    public CCVector3f[] convertRealWorldToProjective(CCVector3f[] realWorldPoints){
    	Point3D[] realWorld = new Point3D[realWorldPoints.length];
		for(int i = 0; i < realWorld.length;i++) {
			realWorld[i] = new Point3D(
				realWorldPoints[i].x, 
				realWorldPoints[i].y, 
				realWorldPoints[i].z
			);
		}
		Point3D[] projective;
		try {
			projective = _myGenerator.convertProjectiveToRealWorld(realWorld);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}

		CCVector3f[] projective3f = new CCVector3f[projective.length];
		for(int i = 0; i < projective.length;i++) {
			projective3f[i] = convert(projective[i]);
		}
		return projective3f;
    }
    
    /**
	 * Converts a  point from real world coordinates to projective coordinates.
	 * @param realWorldPoint real world coordinates
	 * @return projective coordinates
	 */
    public CCVector3f convertRealWorldToProjective(CCVector3f realWorldPoint) {
    	CCVector3f[] realWorldPoints = new CCVector3f[1];
        realWorldPoints[0] = realWorldPoint;

        return convertRealWorldToProjective(realWorldPoints)[0];
    }
    
    public ShortBuffer depthMap() {
		return _myRawData;
	}
    
    private int _myStepSize = 1;
    
    public CCVector3f[] depthMapRealWorld(int theStepSize) {
    	CCVector3f[] myPoints = depthMapRealWorld(theStepSize, 0, 0, _myWidth, _myHeight);
    	return myPoints;
    }
    
    public CCVector3f[] depthMapRealWorld(int theStepSize, int theX, int theY, int theWidth, int theHeight) {
    	return depthMapRealWorld(theStepSize, theX, theY, theWidth, theHeight, true);
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
	public CCVector3f[] depthMapRealWorld(int theStepSize, int theX, int theY, int theWidth, int theHeight, boolean theApplyTransform) {
		if (_myRawData == null)
			return null;
		
		if(theX + theWidth > _myWidth || theY + theHeight > _myHeight) return null;
		
		// if(_myRealWorldData == null || _myStepSize != theStepSize) {
		_myStepSize = theStepSize;

		int myDataSize = theWidth * theHeight / _myStepSize / _myStepSize;
		_myRealWorldData = new CCVector3f[myDataSize];
		_myProjectiveDataP3 = new Point3D[myDataSize];

		for (int i = 0; i < _myRealWorldData.length; i++) {
			_myRealWorldData[i] = new CCVector3f();
			_myProjectiveDataP3[i] = new Point3D();
		}
//		_myRealWorldNeedsUpdate = true;
		// }
		// if(_myRealWorldNeedsUpdate) {
//		_myRealWorldNeedsUpdate = false;
		_myRawData.rewind();
		int i = 0;
		for (int y = theY; y < theY + theHeight; y += theStepSize) {
			for (int x = theX; x < theX + theWidth; x += theStepSize) {
				int id = y * _myWidth + x;
				_myProjectiveDataP3[i].setPoint(x, y, _myRawData.get(id));
				i++;
			}
		}

		// convert all point into realworld coord

		try {
			Point3D[] realWorld = _myGenerator.convertProjectiveToRealWorld(_myProjectiveDataP3);

			for (i = 0; i < realWorld.length; i++) {
				_myRealWorldData[i].set(realWorld[i].getX(), realWorld[i].getY(), realWorld[i].getZ());
				// _myTransformation.inverseTransform(_myRealWorldData[i]);
			}
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}

		if (theApplyTransform) {
			for (CCVector3f myPoint : _myRealWorldData) {
				_myTransformation.inverseTransform(myPoint);
			}
		}

		return _myRealWorldData;
	}
	
	/**
	 * Gets the maximum depth the device can produce.
	 * @return
	 */
	public int maxDepth() {
		return _myGenerator.getDeviceMaxDepth();
	}
	
	/**
	 * Gets the Field-Of-View of the depth generator, in radians.
	 * @return
	 */
	public CCVector2f fieldOfView(){
		try {
			FieldOfView myFieldOfView = _myGenerator.getFieldOfView();
			return new CCVector2f(myFieldOfView.getHFOV(), myFieldOfView.getVFOV());
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
		
	}
	
	@Override
	/**
	 * Gets the current depth-map as texture.
	 * @return
	 */
	public CCTexture2D texture() {
		if(!_myTextureNeedsUpdate) return _myTexture;
		if(_myRawData == null) return _myTexture;
		if(_myStopUpdateTexture) return _myTexture;
			
		_myTextureNeedsUpdate = false;
		FloatBuffer myFloatBuffer = (FloatBuffer) _myTextureData.buffer();
		myFloatBuffer.rewind();
		_myRawData.rewind();
		float myMaxDepth = (float) maxDepth();
		while (_myRawData.hasRemaining()) {
			int myValue = _myRawData.get();
			myFloatBuffer.put(myValue / myMaxDepth);
		}
		myFloatBuffer.rewind();
		_myTexture.updateData(_myTextureData);
		
		return _myTexture;
	}
}
