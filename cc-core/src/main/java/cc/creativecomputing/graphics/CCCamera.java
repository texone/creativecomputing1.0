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
package cc.creativecomputing.graphics;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.d.CCVector3d;


/**
 * Use this class to control the virtual camera of your application. 
 * 
 * Defaults: 
 * <ul>
 * <li><b>Camera position</b> - sits on the positive z-axis</li>
 * <li><b>Target position</b> - located at the world origin</li>
 * <li><b>Up direction</b> - point in the negative y</li>
 * <li><b>Field-of-view</b> - PI/3 radians (60 degrees)</li>
 * <li><b>Aspect ratio</b> - application width to application height</li>
 * <li><b>Near clipping plane</b> - 0.1x shot length</li>
 * <li><b>Far clipping plane</b> - 10x the shot length</li>
 * </ul>
 * 
 * @author Christian Riekoff
 */
public class CCCamera implements CCDrawListener<CCGraphics>{
	// --- Class Attributes ----
	private static final float TOL = 0.00001f;
	public static float DEFAULT_FOV = 60;

	private CCGraphics _myGraphics;

	// Camera Orientation Information
	private float _myXrotation;
	private float _myYRotation;
	private float _myZRotation;
	
	private CCViewport _myViewport;
	
	private CCVector2f _myFrustumOffset = new CCVector2f();

	/**
	 * Camera Position
	 */
	private CCVector3f _myPosition;

	/**
	 * Target Position
	 */
	private CCVector3f _myTarget;

	/**
	 * Up Vector
	 */
	private CCVector3f _myUp;

	/**
	 * Field of View
	 */
	private float _myFoV;

	/**
	 * Aspect Ratio
	 */
	private float _myAspect;

	// Clip Planes
	private float _myNearClip;
	private float _myFarClip;

	/**
	 * The length of the view vector
	 */
	private float _myShotLength;

	/**
	 * Distance differences between camera and target
	 */
	private CCVector3f _myDelta;

	/**
	 * Create a camera that sits on the z axis
	 */
	public CCCamera(final CCGraphics g) {
		this(g,g.width,g.height);
	}
	
	public CCCamera(final CCGraphics g, final int theWidth, final int theHeight){
		this(g, theWidth, theHeight, DEFAULT_FOV * CCMath.DEG_TO_RAD);
	}
	
	public CCCamera(final CCGraphics g, final int theWidth, final int theHeight, final float theFov){
		_myGraphics = g;
		set(theWidth, theHeight, theFov);
	}

	/**
	 * Create a camera that sits on the z axis with a specified shot length
	 * @param theParent
	 * @param theShotLength
	 */
	public CCCamera(final CCGraphics g, final float theShotLength) {
		this(g, 0, 0, theShotLength,0,0,0);
	}
	
	public CCCamera(final CCGraphics g, final CCVector3f thePosition){
		this(g, thePosition.x, thePosition.y, thePosition.z,0,0,0);
	}
	
	public CCCamera(final CCGraphics g, final CCVector3f thePosition, final CCVector3f theTarget){
		this(
			g,
			thePosition.x, thePosition.y, thePosition.z,
			theTarget.x, theTarget.y, theTarget.z
		);
	}

	/**
	 * Create a camera at the specified location with the specified target
	 * @param theParent
	 * @param theCameraX
	 * @param theCameraY
	 * @param theCameraZ
	 * @param theTargetX
	 * @param theTargetY
	 * @param theTargetZ
	 */
	public CCCamera(
		final CCGraphics g, 
		final float theCameraX, final float theCameraY, final float theCameraZ, 
		final float theTargetX, final float theTargetY, final float theTargetZ
	) {
		this(
			g, 
			theCameraX, theCameraY, theCameraZ, 
			theTargetX, theTargetY, theTargetZ, 
			0, 1, 0, 
			DEFAULT_FOV * CCMath.DEG_TO_RAD, (float) (1f * g.width / g.height), 
			0, 0
		);

		_myNearClip = _myShotLength * 0.1f;
		_myFarClip = _myShotLength * 10f;
	}

	// Specify all parameters for camera creation
	public CCCamera(
		final CCGraphics g, 
		final CCVector3f thePosition, final CCVector3f theTarget, final CCVector3f theUp, 
		final float theFov, final float theAspect,
		final float theNearClip, final float theFarClip
	){
		this(
			g,
			thePosition.x, thePosition.y, thePosition.z,
			theTarget.x, theTarget.y, theTarget.z,
			theUp.x, theUp.y, theUp.z,
			theFov, theAspect,
			theNearClip, theFarClip
		);
	}
	
	public CCCamera(
		final CCGraphics g, 
		final float theCameraX, final float theCameraY, final float theCameraZ, 
		final float theTargetX, final float theTargetY, final float theTargetZ,
		final float theUpX, final float theUpY, final float theUpZ, 
		final float theFov, final float theAspect,
		final float theNearClip, final float theFarClip
	){
		_myGraphics = g;
		_myPosition = new CCVector3f(theCameraX, theCameraY, theCameraZ);
		_myTarget = new CCVector3f(theTargetX, theTargetY, theTargetZ);
		_myUp = new CCVector3f(theUpX, theUpY, theUpZ);
		
		_myFoV = theFov;
		_myAspect = theAspect;
		
		_myNearClip = theNearClip;
		_myFarClip = theFarClip;
		
		_myDelta = _myPosition.clone();
		_myDelta.subtract(_myTarget);

		_myShotLength = _myDelta.length();

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x));

		if (_myXrotation > CCMath.HALF_PI - TOL) {
			_myUp.y = 0;
			_myUp.z = -1;
		}

		if (_myXrotation < TOL - CCMath.HALF_PI) {
			_myUp.y = 0;
			_myUp.z = 1;
		}
		
		_myViewport = new CCViewport(0,0,g.width,g.height);

		updateUp();
		updateProjectionInfos();
	}
	
	public void set(
		final float theCameraX, final float theCameraY, final float theCameraZ, 
		final float theTargetX, final float theTargetY, final float theTargetZ,
		final float theUpX, final float theUpY, final float theUpZ, 
		final float theFov, final float theAspect,
		final float theNearClip, final float theFarClip
	){
		_myPosition = new CCVector3f(theCameraX, theCameraY, theCameraZ);
		_myTarget = new CCVector3f(theTargetX, theTargetY, theTargetZ);
		_myUp = new CCVector3f(theUpX, theUpY, theUpZ);
			
		_myFoV = theFov;
		_myAspect = theAspect;
			
		_myNearClip = theNearClip;
		_myFarClip = theFarClip;
			
		_myDelta = _myPosition.clone();
		_myDelta.subtract(_myTarget);

		_myShotLength = _myDelta.length();

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x));

		if (_myXrotation > CCMath.HALF_PI - TOL) {
			_myUp.y = 0;
			_myUp.z = -1;
		}

		if (_myXrotation < TOL - CCMath.HALF_PI) {
			_myUp.y = 0;
			_myUp.z = 1;
		}

		updateUp();
	}
	
	public void set(final int theWidth, final int theHeight, final float theFov) {
		_myViewport = new CCViewport(0,0,theWidth,theHeight);
		// init perspective projection based on new dimensions
		float cameraFOV = theFov; // at least for now
		float cameraAspect = (float) theWidth / (float) theHeight;
		
		float cameraX = 0;
		float cameraY = 0;
		float cameraZ = theHeight / 2.0f / CCMath.tan(cameraFOV / 2.0f);
		
		
		float cameraNear = cameraZ / 10.0f;
		float cameraFar = cameraZ * 10.0f;

		set(
			cameraX, cameraY, cameraZ, 
			cameraX, cameraY, 0, 
			0, 1, 0,
			cameraFOV, cameraAspect,
			cameraNear, cameraFar
		);
	}
	
	public void reset(){
		_myFoV = 60 * CCMath.DEG_TO_RAD; // at least for now
		_myAspect = (float) _myGraphics.width / (float) _myGraphics.height;
		
		_myPosition = new CCVector3f(0,0,_myGraphics.height / 2.0f / ((float) Math.tan(_myFoV / 2.0f)));
		_myTarget = new CCVector3f(0,0,0);
		_myUp = new CCVector3f(0,1,0);
		
		_myDelta = _myPosition.clone();
		_myDelta.subtract(_myTarget);
		
		_myShotLength = _myDelta.length();
		
		_myNearClip = _myPosition.z / 10.0f;
		_myFarClip = _myPosition.z * 10.0f;

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x));

		if (_myXrotation > CCMath.HALF_PI - TOL) {
			_myUp.y = 0;
			_myUp.z = -1;
		}

		if (_myXrotation < TOL - CCMath.HALF_PI) {
			_myUp.y = 0;
			_myUp.z = 1;
		}
		
		_myViewport = new CCViewport(0,0,_myGraphics.width,_myGraphics.height);
		_myFrustumOffset = new CCVector2f();

		updateUp();
	}

	/**
	 * @invisible
	 * @param g
	 */
	public void drawFrustum(CCGraphics g){
		_myAspect = _myViewport.aspectRatio();
		
		float ymax = _myNearClip * (float) Math.tan(_myFoV / 2.0f);
		float ymin = -ymax;

		float xmin = ymin * _myAspect;
		float xmax = ymax * _myAspect;

		g.frustum(
			xmin + _myFrustumOffset.x, xmax + _myFrustumOffset.x, 
			ymin + _myFrustumOffset.y, ymax + _myFrustumOffset.y, 
			_myNearClip, _myFarClip
		);
	}
	
	public void drawPerspective(CCGraphics g){
		g.glu.gluPerspective(_myFoV, _myAspect, _myNearClip, _myFarClip);
	}

	/**
	 * Send what this camera sees to the view port
	 */
	public void draw(CCGraphics g) {
		if(_myViewport != null)_myViewport.draw(g);

		updateProjectionInfos();

		g.gl.glLoadIdentity();
		_myGraphics.glu.gluLookAt(
			_myPosition.x, _myPosition.y, _myPosition.z, 
			_myTarget.x, _myTarget.y, _myTarget.z,
			_myUp.x, _myUp.y, _myUp.z
		);
		drawFrustum(g);
		
	}

	/**
	 * Aim the camera at the specified target
	 * @param theTargetX
	 * @param theTargetY
	 * @param theTargetZ
	 */
	public void target(final float theTargetX, final float theTargetY, final float theTargetZ) {
		_myTarget.set(theTargetX, theTargetY, theTargetZ);
		updateDeltas();
	}
	
	/**
	 * 
	 * @param theTarget
	 */
	public void target(final CCVector3f theTarget){
		_myTarget.set(theTarget);
		updateDeltas();
	}

	/**
	 * Returns the target position
	 * @return
	 */
	public CCVector3f target() {
		return _myTarget;
	}

	/**
	 * Jump the camera to the specified position
	 * @param positionX
	 * @param positionY
	 * @param positionZ
	 */
	public void position(final float thePositionX, final float thePositionY, final float thePositionZ) {
		// Move the camera
		_myPosition.set(thePositionX,thePositionY,thePositionZ);
		updateDeltas();
	}
	
	public void position(final CCVector3f thePosition) {
		// Move the camera
		_myPosition.set(thePosition);
		updateDeltas();
	}

	public void up(final CCVector3f theUp) {
		_myUp.set(theUp);
	}
	
	/**
	 * Returns the camera position
	 */
	public CCVector3f position() {
		return _myPosition;
	}

	/**
	 * Change the field of view between "fish-eye" and "close-up"
	 * @param theAmount
	 */
	public void zoom(final float theAmount) {
		_myFoV = CCMath.constrain(_myFoV + theAmount, TOL, CCMath.PI - TOL);
	}
	
	public CCViewport viewport(){
		return _myViewport;
	}
	
	public void viewport(final CCViewport theViewport){
		_myViewport = theViewport;
	}
	
	public CCVector2f frustumOffset(){
		return _myFrustumOffset;
	}

	public void frustumOffset(CCVector2f theOffset){
		_myFrustumOffset.set(theOffset);
	}

	//////////////////////////////////////////////////
	//
	// CAMERA AXIS
	//
	//////////////////////////////////////////////////
	
	/**
	 * Calculates the x axis of the camera
	 * @return x axis of the camera
	 */
	public CCVector3f xAxis() {
		// calculate the camera's X axis in world space
		final CCVector3f myDelta = CCVecMath.subtract(_myTarget, _myPosition);
		myDelta.normalize();
		
		return CCVecMath.cross(myDelta, _myUp);
	}
	
	/**
	 * Calculates the y axis of the camera
	 * @return y axis of the camera
	 */
	public CCVector3f yAxis() {
		return _myUp.clone();
	}
	
	/**
	 * Calculates the z axis of the camera
	 * @return z axis of the camera
	 */
	public CCVector3f zAxis() {
		return _myDelta.clone();
	}
	
	//////////////////////////////////////////////////
	//
	// CAMERA MOVEMENT
	//
	//////////////////////////////////////////////////

	/**
	 * Move the camera and target simultaneously along the camera's X axis
	 * @param theAmount
	 */
	public void moveX(final float theAmount) {
		// calculate the camera's X axis in world space
		final CCVector3f myDirection = xAxis();

		// normalize and scale translation vector
		myDirection.normalize(theAmount);

		// translate both camera position and target
		_myPosition.add(myDirection);
		_myTarget.add(myDirection);
	}

	/**
	 * Move the camera and target simultaneously along the camera's Y axis
	 * @param theAmount
	 */
	public void moveY(final float theAmount) {
		// Perform the boom, if any
		_myPosition.add(
			_myUp.x * theAmount,
			_myUp.y * theAmount,
			_myUp.z * theAmount
		);
		_myTarget.add(
			_myUp.x * theAmount,
			_myUp.y * theAmount,
			_myUp.z * theAmount
		);
	}

	/**
	 * Move the camera and target along the view vector
	 * @param theAmount
	 */
	public void moveZ(final float theAmount) {
		// Normalize the view vector
		final CCVector3f myDirection = _myDelta.clone();
		myDirection.scale(theAmount/ _myShotLength);

		// Perform the dolly, if any
		_myPosition.add(myDirection);
		_myTarget.add(myDirection);
	}

	/**
	 * Moves the camera and target simultaneously in the camera's X-Y plane
	 * @param theXOffset
	 * @param theYOffset
	 */
	public void moveXY(final float theXOffset, final float theYOffset) {
		// Perform the truck, if any
		moveX(theXOffset);

		// Perform the boom, if any
		moveY(theYOffset);
	}
	
	/**
	 * Moves the camera by the defined vector
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void move(final float theX, final float theY, final float theZ){
		_myPosition.add(theX, theY, theZ);
		_myTarget.add(theX, theY, theZ);
	}
	
	public void move(final CCVector3f theVector){
		_myPosition.add(theVector);
		_myTarget.add(theVector);
	}
	
	//////////////////////////////////////////////////
	//
	// CAMERA ROTATION 
	//
	//////////////////////////////////////////////////

	/**
	 * Rotate the camera about its X axis
	 * @param theXrotation
	 */
	public void rotateX(final float theXrotation) {
		// Calculate the new elevation for the camera
		_myXrotation = CCMath.constrain(
			_myXrotation - theXrotation, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		// Update the target
		updateTarget();
	}

	/**
	 * Rotate the camera about its Y axis
	 * @param theYrotation
	 */
	public void rotateY(final float theYrotation) {
		// Calculate the new azimuth for the camera
		_myYRotation = (_myYRotation - theYrotation + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the target
		updateTarget();
	}


	/**
	 * Rotate the camera about its Z axis
	 * @param theZrotation
	 */
	public void rotateZ(final float theZrotation) {
		// Change the roll amount
		_myZRotation = (_myZRotation + theZrotation + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the up vector
		updateUp();
	}
	
	/**
	 * Sets the rotation of the camera about its X axis. Note that this method different
	 * from {@link #rotateX(float)} that rotates the camera by a given amount, while this
	 * is setting the rotation directly. The given value will be constrained to a value
	 * between -HALF_PI and HALF_PI
	 * @param theXrotation new x rotation of the camera 
	 */
	public void xRotation(final float theXrotation) {
		// Calculate the new elevation for the camera
		_myXrotation = CCMath.constrain(
			theXrotation, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		// Update the target
		updateTarget();
	}
	
	/**
	 * Sets the rotation of the camera about its Y axis. Note that this method different
	 * from {@link #rotateY(float)} that rotates the camera by a given amount, while this
	 * is setting the rotation directly. The given value will be constrained to a value
	 * between 0 and TWO_PI
	 * @param theYrotation new y rotation of the camera 
	 */
	public void yRotation(final float theYrotation) {
		// Calculate the new azimuth for the camera
		_myYRotation = CCMath.constrain(
			theYrotation, 
			0, 
			CCMath.TWO_PI
		);

		// Update the target
		updateTarget();
	}
	
	/**
	 * Sets the rotation of the camera about its Z axis. Note that this method different
	 * from {@link #rotateZ(float)} that rotates the camera by a given amount, while this
	 * is setting the rotation directly. The given value will be constrained to a value
	 * between 0 and TWO_PI
	 * @param theZrotation new z rotation of the camera 
	 */
	public void zRotation(final float theZrotation) {
		// Calculate the new azimuth for the camera
		_myZRotation = CCMath.constrain(
				theZrotation, 
			0, 
			CCMath.TWO_PI
		);

		// Update the up vector
		updateUp();
	}

	/**
	 * Rotate the camera about its X axis around the target of the camera
	 * @param theXrotation
	 */
	public void rotateXaroundTarget(final float theXrotation) {
		// Calculate the new elevation for the camera
		_myXrotation = //CCMath.constrain(
			_myXrotation + theXrotation;
//			TOL - CCMath.HALF_PI, 
//			CCMath.HALF_PI - TOL
//		);

		// Update the camera
		updateCamera();
	}

	/**
	 * Circle the camera around a center of interest at a set elevation
	 * @param theYrotation
	 */
	public void rotateYaroundTarget(final float theYrotation) {
		// Calculate the new azimuth for the camera
		_myYRotation = (_myYRotation + theYrotation + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the camera
		updateCamera();
	}
	
	/**
	 * Look about the camera's position
	 * @param theAzimuthOffset
	 * @param theElevationOffset
	 */
	public void look(final float theAzimuthOffset, final float theElevationOffset) {
		// Calculate the new azimuth and elevation for the camera
		_myXrotation = CCMath.constrain(
			_myXrotation - theElevationOffset, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		_myYRotation = (_myYRotation - theAzimuthOffset + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the target
		updateTarget();
	}

	/**
	 * Tumble the camera about its target
	 * @param theAzimuthOffset
	 * @param theElevationOffset
	 */
	public void tumble(final float theAzimuthOffset, final float theElevationOffset) {
		// Calculate the new azimuth and elevation for the camera
		_myXrotation = CCMath.constrain(
			_myXrotation + theElevationOffset, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		_myYRotation = (_myYRotation + theAzimuthOffset + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the camera
		updateCamera();
	}

	// Returns the camera orientation
	public float[] attitude() {
		return new float[] { _myYRotation, _myXrotation, _myZRotation };
	}

	// Returns the "up" vector
	public CCVector3f up() {
		return _myUp;
	}

	/**
	 * Returns the field of view
	 * @return
	 */
	public float fov() {
		return _myFoV;
	}
	
	public void fov(final float theFoV){
		_myFoV = theFoV;
	}
	
	public float aspect(){
		return _myAspect;
	}
	
	public float near(){
		return _myNearClip;
	}
	
	public void near(final float theNearClip) {
		_myNearClip = theNearClip;
	}
	
	public float far(){
		return _myFarClip;
	}
	
	public void far(final float theFarClip) {
		_myFarClip = theFarClip;
	}
	
	public float depthRange() {
		return _myFarClip - _myNearClip;
	}

	/**
	 * Update deltas and related information
	 */
	private void updateDeltas() {
		// Describe the new vector between the camera and the target
		_myDelta = position().clone();
		_myDelta.subtract(_myTarget);

		// Describe the new azimuth and elevation for the camera
		_myShotLength = _myDelta.length();

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z* _myDelta.z + _myDelta.x * _myDelta.x));

		// update the up vector
		updateUp();
	}

	/**
	 * Update target and related information
	 */
	private void updateTarget() {
		// Rotate to the new orientation while maintaining the shot distance.
		_myTarget.x = _myPosition.x - (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.sin(_myYRotation));
		_myTarget.y = _myPosition.y  - (-_myShotLength * CCMath.cos(CCMath.HALF_PI + _myXrotation));
		_myTarget.z = _myPosition.z  - (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.cos(_myYRotation));

		// update the up vector
		updateUp();
	}

	/**
	 * Update target and related information
	 */
	private void updateCamera() {
		// Orbit to the new orientation while maintaining the shot distance.
		_myPosition.x = _myTarget.x	+ (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.sin(_myYRotation));
		_myPosition.y = _myTarget.y + (-_myShotLength * CCMath.cos(CCMath.HALF_PI + _myXrotation));
		_myPosition.z = _myTarget.z + (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.cos(_myYRotation));

		// update the up vector
		updateUp();
	}

	/**
	 * Update the up direction and related information
	 */
	private void updateUp() {
		// Describe the new vector between the camera and the target
		_myDelta = _myPosition.clone();
		_myDelta.subtract(_myTarget);

		// Calculate the new "up" vector for the camera
		_myUp.x = -_myDelta.x * _myDelta.y;
		_myUp.y = _myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x;
		_myUp.z = -_myDelta.z * _myDelta.y;

		// Normalize the "up" vector
		_myUp.normalize();

		// Calculate the roll if there is one
		if (_myZRotation != 0) {
			// Calculate the camera's X axis in world space
			final CCVector3f myDirection = new CCVector3f(
				_myDelta.y * _myUp.z - _myDelta.z * _myUp.y,
				_myDelta.x * _myUp.z - _myDelta.z * _myUp.y,
				_myDelta.x * _myUp.y - _myDelta.y * _myUp.x	
			);

			// Normalize this vector so that it can be scaled
			myDirection.normalize();

			// Perform the roll
			final float myCosRoll = CCMath.cos(_myZRotation);
			final float mySinRoll = CCMath.sin(_myZRotation);
			_myUp.x = _myUp.x * myCosRoll + myDirection.x * mySinRoll;
			_myUp.y = _myUp.y * myCosRoll + myDirection.y * mySinRoll;
			_myUp.z = _myUp.z * myCosRoll + myDirection.z * mySinRoll;
		}
	}
	
	//////////////////////////////////////////////////////////
	//
	// Calculations
	//
	//////////////////////////////////////////////////////////
	private final int viewport[] = new int[4];
	private final double[] _myProjectionMatrix = new double[16];
	private final double[] _myViewMatrix = new double[16];
	private final double[] myResultArray = new double[4];
	
	public void updateProjectionInfos(){
		// get viewport
		_myGraphics.gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		// get projection matrix
		_myGraphics.gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, _myProjectionMatrix, 0);

		// get modelview matrix
		_myGraphics.gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, _myViewMatrix, 0);
	}
	
	public CCMatrix4f viewMatrix(){
		return CCMatrix4f.createFromGLMatrix(_myViewMatrix);
	}
	
	/**
	 * project transforms the specified object coordinates into
	 * window coordinates using model, proj and view. The result
	 * is stored in the returned vector
	 */
	public CCVector3f modelToScreen(final CCVector3f theObjectVector) {
		return modelToScreen(theObjectVector.x,theObjectVector.y,theObjectVector.z);
	}
	
	public CCVector3d modelToScreen(final CCVector3d theObjectVector) {
		return modelToScreen(theObjectVector.x,theObjectVector.y,theObjectVector.z);
	}
	
	public CCVector3f modelToScreen(final float theX, final float theY, final float theZ){
		CCVector3d myResult = modelToScreen((double)theX, theY, theZ);
		return new CCVector3f((float)myResult.x,(float)myResult.y,(float)myResult.z);
	}
	
	public CCVector3d modelToScreen(final double theX, final double theY, final double theZ){
		_myGraphics.glu.gluProject(
			theX, 
			theY, 
			theZ, 
			_myViewMatrix, 0, 
			_myProjectionMatrix, 0, 
			viewport, 0,
			myResultArray, 0
		);
		return new CCVector3d((float)myResultArray[0],(float)myResultArray[1],(float)myResultArray[2]);
	}
	
	/**
	 * Screen to model will map the given window coordinates to model coordinates. The depth of
	 * the model coordinates is read from the depth buffer. Optionally you can pass a depth value
	 * to this function that must be in the range 0 to 1.
	 * @shortdesc Calculates the model coordinates corresponding to the given screen coordinates.
	 * @param theX
	 * @param theY
	 * @param theDepth
	 * @return
	 */
	public CCVector3f screenToModel(final float theX, final float theY, final float theDepth) {
		//For the viewport matrix... not sure what all the values are, I think
		// the first two are width and height, and all Matrices in GL seem to
		// be 4 or 16...
		
		_myGraphics.glu.gluUnProject(
			theX, 
			theY,
			theDepth, 
			_myViewMatrix, 0, 
			_myProjectionMatrix, 0, 
			viewport, 0,
			myResultArray, 0
		);
		return new CCVector3f((float)myResultArray[0],(float)myResultArray[1],(float)myResultArray[2]);
	}

	/**
	 * @param theWindowVector
	 * @return
	 */
	public CCVector3f screenToModel(final CCVector3f theWindowVector) {
		return screenToModel(theWindowVector.x,theWindowVector.y,theWindowVector.z);
	}

	/**
	 * @param theWindowVector
	 * @return
	 */
	public CCVector3f screenToModel(final CCVector2f theWindowVector) {
		return screenToModel(theWindowVector.x, theWindowVector.y, 0);
	}
	
	/**
	 * @param theMouseX
	 * @param theMouseY
	 * @return the model coordinates for the given screen position
	 */
	public CCVector3f screenToModel(final int theMouseX, final int theMouseY) {
		// set up a floatbuffer to get the depth buffer value of the mouse
		final FloatBuffer myFloatBuffer = FloatBuffer.allocate(1);
		
		// Get the depth buffer value at the mouse position. have to do
		// height-mouseY, as GL puts 0,0 in the bottom left, not top left.
		_myGraphics.gl.glReadPixels(theMouseX, _myGraphics.height - theMouseY, 1, 1, GL2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, myFloatBuffer);

		myFloatBuffer.rewind();
		
		float myDepth = myFloatBuffer.get();
		// the result x,y,z will be put in this.. 4th value will be 1, but I
		// think it's "scale" in GL terms, but I think it'll always be 1.

		return  screenToModel(theMouseX,_myGraphics.height - theMouseY,myDepth);
	}
	
	/**
	 * Use this method to calculate the vector perpendicular to the screen. At the
	 * given position. The resulting vector is normalized and facing forward away
	 * from the screen.
	 * @param thePosition
	 * @return
	 */
	public CCVector3f screenOrthogonal(CCVector3f thePosition){
		CCVector3f myScreenCoords = modelToScreen(thePosition);
		CCVector3f myModelCoords = screenToModel(myScreenCoords.x,myScreenCoords.y,0);
		
		CCVector3f myResult = CCVecMath.subtract(myModelCoords,thePosition);
		myResult.normalize();
		return myResult;
	}
	
	public CCVector3f screenOrthogonal(final float theX, final float theY, final float theZ){
		return screenOrthogonal(new CCVector3f(theX,theY,theZ));
	}
	
	public CCVector3f screenOrthogonal(final float theX, final float theY) {
		CCVector3f myVec1 = _myGraphics.camera().screenToModel(theX, theY,0);
		CCVector3f myVec2 = _myGraphics.camera().screenToModel(theX, theY,1);
		
		return CCVecMath.subtract(myVec1, myVec2).normalize();
	}
	
	public CCVector3f screenOrthogonal(final CCVector2f theVector) {
		return screenOrthogonal(theVector.x, theVector.y);
	}
	
	/**
	 * Returns the dimension of the screen at the given z value
	 * @param theZ
	 * @return
	 */
	public CCVector2f screenDimension(final float theZ){
		float tang = CCMath.tan(_myFoV * 0.5f) ;
		float myHeight = (_myPosition.z - theZ) * tang * 2;
		float myWidth = myHeight * _myAspect;
		return new CCVector2f(myWidth,myHeight);
	}
	
	public String toString(){
		StringBuilder _myStringBuilder = new StringBuilder();
		_myStringBuilder.append("Camera Settings:\n");
		_myStringBuilder.append("position:\n");
		_myStringBuilder.append("target:\n");
		_myStringBuilder.append("up:\n");
		return _myStringBuilder.toString();
	}
}
