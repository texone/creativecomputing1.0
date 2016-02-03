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
package cc.creativecomputing.math.util;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCSizeListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCArcball implements CCMouseListener, CCMouseMotionListener, CCDrawListener<CCGraphics>, CCSizeListener {

	private CCVector3f _myCenter = new CCVector3f();
	private float _myRadius;
	
	private float _myWidth;
	private float _myHeight;

	private float _myScale = 1.0f;
	private CCVector3f _myTranslation = new CCVector3f();
	
	private CCVector3f _myDownVector;
	private CCVector3f _myDragVector;
	private CCVector4f _myRotation = new CCVector4f();

	private CCQuaternion _myCurrentQuat;
	private CCQuaternion _myDownQuat;

	private CCVector3f[] _myAxisSet;
	private final CCMatrix4f _myRotationMatrix = new CCMatrix4f();

	private int _myAxis;
	private boolean _myIsActive = true;
	
	private CCViewport _myViewport = null;
	private CCAbstractWindowApp _myApp = null;

	/** defaults to radius of min(width/2,height/2) and center_z of -radius */
	
	public CCArcball(final float theWidth, final float theHeight) {
		_myDownVector = new CCVector3f();
		_myDragVector = new CCVector3f();

		_myCurrentQuat = new CCQuaternion();
		_myDownQuat = new CCQuaternion();

		_myAxisSet = new CCVector3f[] {
			new CCVector3f(1.0f, 0.0f, 0.0f), 
			new CCVector3f(0.0f, 1.0f, 0.0f), 
			new CCVector3f(0.0f, 0.0f, 1.0f)
		};
		_myAxis = -1; // no constraints...
		_myRadius = 200;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
	}

	public CCArcball(final CCAbstractWindowApp theApp){
		this(theApp.width, theApp.height);
		
		_myApp = theApp; 
		theApp.addMouseListener(this);
		theApp.addMouseMotionListener(this);
		theApp.addSizeListener(this);
	}

	public float scale(){
		return _myScale;
	}
	
	@CCControl(name = "reset")
	public void reset(){
		_myDownVector = new CCVector3f();
		_myDragVector = new CCVector3f();
		_myTranslation = new CCVector3f();

		_myCurrentQuat = new CCQuaternion();
		_myDownQuat = new CCQuaternion();

		_myAxisSet = new CCVector3f[] {
			new CCVector3f(1.0f, 0.0f, 0.0f), 
			new CCVector3f(0.0f, 1.0f, 0.0f), 
			new CCVector3f(0.0f, 0.0f, 1.0f)
		};
		_myAxis = -1; // no constraints...
		_myScale = 1.0f;
	}
	
	/**
	 * Use this method to activate and deactivate the mouse action of the arcball.
	 * @param theIsActive
	 */
	public void active(final boolean theIsActive){
		_myIsActive = theIsActive;
	}
		
	public void setViewport(CCViewport theViewport) {
		_myViewport = theViewport;
	}

	public void mouseClicked(CCMouseEvent theEvent) {}

	public void mouseEntered(CCMouseEvent theEvent) {}

	public void mouseExited(CCMouseEvent theEvent) {}

	public void mousePressed(CCMouseEvent theEvent) {
		if(!_myIsActive)return;
		if(_myViewport != null && !_myViewport.pointInside(new CCVector2i(theEvent.x(), theEvent.y()))) return;

		CCVector2i myPressedPosition = translateViewportToScreen(new CCVector2i(theEvent.x(), theEvent.y()));
		// translate viewport to screen coordinates
		
		_myDownVector = mouseToSphere(myPressedPosition.x, myPressedPosition.y);
		_myDownQuat.set(_myCurrentQuat);
	}

	public void mouseReleased(CCMouseEvent theEvent) {
		if(!_myIsActive)return;
	}
	
	private CCVector2i translateViewportToScreen(CCVector2i thePosition) {
		CCVector2i myScreenPos = new CCVector2i(thePosition);
		if (_myViewport != null) {
			myScreenPos.x = (int)((float)(thePosition.x - _myViewport.x()) * ((float)_myWidth / (float)_myViewport.width()));
			myScreenPos.y = (int)((float)(thePosition.y - _myViewport.y()) * ((float)_myHeight / (float)_myViewport.height()));		
		}
		
		return myScreenPos;
	}
	
	public void mouseDragged(CCMouseEvent theEvent) {
		if(!_myIsActive) return;
		if(_myViewport != null && !_myViewport.pointInside(new CCVector2i(theEvent.x(), theEvent.y()))) return;
		
		if ( _myApp != null){
			if (_myApp instanceof CCApp){
				CCApp myTempApp = (CCApp)_myApp;
				if ( myTempApp.areControlsVisible() )
					return;	//we dont want to move camera when UI is shown
			}
		}
		
		
		switch (theEvent.button() ){
		
			case RIGHT:	//zoom with right mouse button
				_myScale -= theEvent.movement().y * 0.003f * _myScale;
				break;
	
			case LEFT:	//rotate with left mouse button
				CCVector2i myScreenPos = translateViewportToScreen(new CCVector2i(theEvent.x(), theEvent.y()));
				_myDragVector = mouseToSphere(myScreenPos.x(), myScreenPos.y());
				CCQuaternion myDragQuat = new CCQuaternion(_myDownVector.cross(_myDragVector), _myDownVector.dot(_myDragVector));	

				_myCurrentQuat.multiply(myDragQuat, _myDownQuat);
				break;
				
			case CENTER://translate with middle mouse button
				_myTranslation.x += theEvent.movement().x * 0.5f;
				_myTranslation.y += -theEvent.movement().y * 0.5f;
				break;
			
			default:	//unknown mouse button
				break;
		}
	}

	public void mouseMoved(CCMouseEvent theMouseEvent) {}
	
	/**
	 * Use this method to set the arcball directly. This might be used at initialization.
	 * @param theAngle new angle to apply
	 * @param theX x coord of the rotation vector
	 * @param theY y coord of the rotation vector
	 * @param theZ z coord of the rotation vector
	 */
	public void setFromVectorAndAngle(final float theAngle, final float theX, final float theY, final float theZ) {
		_myCurrentQuat.fromVectorAndAngle(theAngle, theX, theY, theZ);
	}

	/**
	 * Applies the arcball.
	 */
	public void draw(final CCGraphics g){
		g.translate(_myTranslation);
		g.translate(_myCenter);
		applyQuat2Matrix(g,_myCurrentQuat);		
		g.translate(-_myCenter.x, -_myCenter.y, -_myCenter.z);
		g.scale(_myScale);
	}
	
	public void inverseRotation(CCGraphics g){
		g.rotate(-CCMath.degrees(_myRotation.w()), _myRotation.x, _myRotation.y, _myRotation.z);
	}

	private CCVector3f mouseToSphere(final float theMouseX, final float theMouseY){
		CCVector3f v = new CCVector3f();
		v.x = (theMouseX - _myCenter.x - _myWidth/2) / _myRadius;
		v.y = (_myHeight/2 - theMouseY + _myCenter.y ) / _myRadius;

		float mag = v.x * v.x + v.y * v.y;
		if (mag > 1.0f){
			v.normalize();
		}else{
			v.z = CCMath.sqrt(1.0f - mag);
		}

		return (_myAxis == -1) ? v : constrain_vector(v, _myAxisSet[_myAxis]);
	}

	private CCVector3f constrain_vector(CCVector3f vector, CCVector3f axis){
		CCVector3f res = vector.clone();

		float dot = axis.dot(vector);
		axis.scale(dot);

		res.subtract(axis);
		res.normalize();
		return res;
	}
	
	private CCVector3f _myRotationVector = new CCVector3f(0,0,1);

	private void applyQuat2Matrix(final CCGraphics g,final CCQuaternion theQuad){
		_myRotation = theQuad.getVectorAndAngle();
		g.rotate(CCMath.degrees(_myRotation.w), _myRotation.x, _myRotation.y, _myRotation.z);
		_myRotationMatrix.reset();
		_myRotationMatrix.rotate(-_myRotation.w, _myRotation.x, _myRotation.y, _myRotation.z);
		
		_myRotationVector = new CCVector3f(0,0,1);
		_myRotationMatrix.transform(_myRotationVector);
//		g.rotate(_myZrotation, _myRotationVector);
	}
	
	public CCVector3f getTextRotation(){
		CCVector3f myResult = new CCVector3f(-1,0,0);
		_myRotationMatrix.transform(myResult);
		return myResult;
	}
	
	public CCVector3f rotationVector(){
		return _myRotationVector;
	}
	
	public void applyToMatrix(CCMatrix4f theMatrix){
		theMatrix.rotate(CCMath.degrees(_myRotation.w()), _myRotation.x, _myRotation.y, _myRotation.z);
	}
	
	public CCArcball clone() {
		CCArcball myClone = new CCArcball(_myApp);
		myClone._myTranslation = _myTranslation.clone();
		myClone._myCurrentQuat = _myCurrentQuat.clone();
		myClone._myScale = _myScale;
		return myClone;
	}
	
	public CCXMLElement toXML() {
		CCXMLElement myElement = new CCXMLElement("arcball");
		CCXMLElement myTranslation = new CCXMLElement("translation");
		myTranslation.addAttribute("x", _myTranslation.x);
		myTranslation.addAttribute("y", _myTranslation.y);
		myTranslation.addAttribute("z", _myTranslation.z);
		myElement.addChild(myTranslation);

		CCXMLElement myScale = new CCXMLElement("scale");
		myScale.addAttribute("scale", _myScale);
		myElement.addChild(myScale);

		CCXMLElement myRotation = new CCXMLElement("rotation");
		myRotation.addAttribute("x", _myCurrentQuat.x);
		myRotation.addAttribute("y", _myCurrentQuat.y);
		myRotation.addAttribute("z", _myCurrentQuat.z);
		myRotation.addAttribute("w", _myCurrentQuat.w);
		myElement.addChild(myRotation);
		
		return myElement;
	}
	
	public void save(final String theFile) {
		CCXMLIO.saveXMLElement(toXML(), theFile);
	}
	
	public void fromXML(final CCXMLElement theXML) {
		CCXMLElement myTranslation = theXML.child("translation");
		_myTranslation.x = myTranslation.floatAttribute("x");
		_myTranslation.y = myTranslation.floatAttribute("y");
		_myTranslation.z = myTranslation.floatAttribute("z");

		CCXMLElement myScale = theXML.child("scale");
		_myScale = myScale.floatAttribute("scale");

		CCXMLElement myRotation = theXML.child("rotation");
		_myCurrentQuat.x = myRotation.floatAttribute("x");
		_myCurrentQuat.y = myRotation.floatAttribute("y");
		_myCurrentQuat.z = myRotation.floatAttribute("z");
		_myCurrentQuat.w = myRotation.floatAttribute("w");
	}
	
	public static CCArcball load(final CCApp theApp, final String theFile) {
		
		CCXMLElement myElement = CCXMLIO.createXMLElement(theFile);

		CCArcball myArcball = new CCArcball(theApp);
		myArcball.fromXML(myElement);
		
		return myArcball;
	}

	public void size(int theWidth, int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
}
