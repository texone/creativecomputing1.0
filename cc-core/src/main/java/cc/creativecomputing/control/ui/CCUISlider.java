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
package cc.creativecomputing.control.ui;

import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public class CCUISlider<Type extends Number> extends CCUIValueElement<Type>{
	
	private Type _myMin;
	private Type _myMax;
	private boolean _myIsHorizontal;
	
	private float _myForeGroundSize;
	
	private boolean _myIsInteger = false;
	
	private CCText _myValueText;
	
	public CCUISlider(
		String theLabel, CCVector2f thePosition, CCVector2f theDimension,
		final Type theMin, final Type theMax
	) {
		super(theLabel, thePosition, theDimension,theMin);
		initSlider(theDimension.x, theDimension.y, theMin, theMax);
	}

	/**
	 * @param theLabel
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUISlider(
		String theLabel, 
		float theX, float theY, 
		float theWidth, float theHeight,
		final Type theMin, final Type theMax, final Type theValue
	) {
		super(theLabel, theX, theY, theWidth, theHeight,theValue);
		initSlider(theX, theY, theMin, theMax);
	}

	public CCUISlider(
		String theLabel, 
		float theX, float theY, 
		float theWidth, float theHeight,
		final Type theMin, final Type theMax
	) {
		super(theLabel, theX, theY, theWidth, theHeight,theMin);
		initSlider(theX, theY, theMin, theMax);
	}
	
	private void initSlider(
		final float theX, final float theY, 
		final Type theMin, final Type theMax
	){
		_myMin = theMin;
		_myMax = theMax;
		constrainValues();
		_myIsHorizontal = theX >= theY;
		
		if(_myIsInteger)_myValueText.text(value().intValue());
		else _myValueText.text(value());
		updateRepresentation();
	}
	
	@Override
	public void updateRepresentation() {
		_myForeGroundSize = valueToPostion(_myValues.get(_myPreset));
		
	};
	
	@Override
	public void setupText() {
		float textY = _myPosition.y+_myDimension.y/2 + _myLabel.font().size()/2;
		
		_myLabel.position(_myPosition.x + _myDimension.x + 3, textY);
		
		_myValueText = new CCText(CCControlUI.FONT);
		_myValueText.position(_myPosition.x + 3,textY);
		
		if(_myIsInteger)_myValueText.text(value().intValue());
		else _myValueText.text(value());
	
		_myBounds.min().set(_myPosition);
		_myBounds.width(_myDimension.x+_myLabel.width() +3);
		_myBounds.height(_myDimension.y);
		
		super.setupText();
	}

	public void isInteger(final boolean theIsInteger){
		_myIsInteger = theIsInteger;
	}
	
	@SuppressWarnings("unchecked")
	private Type toType(float theValue) {
		if(_myMin instanceof Float)return (Type)new Float(theValue);
		return (Type)new Integer((int)theValue);
	}
	
	private void constrainValues(){
		for(int i = 0; i < _myValues.size();i++){
			_myValues.set(i, toType(CCMath.constrain(_myValues.get(i).floatValue(), _myMin.floatValue(), _myMax.floatValue())));
		}
	}
	
//	private float positionToValue(CCVector2f thePosition){
//		return positionToValue(thePosition.x, thePosition.y);
//	}
	
	private Type positionToValue(final float theX, final float theY){
		float myResult;
		if(_myIsHorizontal){
			myResult = CCMath.constrain(CCMath.map(theX, _myPosition.x, _myPosition.x + _myDimension.x, _myMin.floatValue(), _myMax.floatValue()), _myMin.floatValue(), _myMax.floatValue());
		}else{
			myResult = CCMath.constrain(CCMath.map(theY, _myPosition.y, _myPosition.y + _myDimension.y, _myMin.floatValue(), _myMax.floatValue()), _myMin.floatValue(), _myMax.floatValue());
		}
		return toType(myResult);
	}
	
	private float valueToPostion(Type theValue){
		if(_myIsHorizontal){
			return CCMath.map(theValue.floatValue(), _myMin.floatValue(), _myMax.floatValue(), 0, _myDimension.x);
		}else{
			return CCMath.map(theValue.floatValue(), _myMin.floatValue(), _myMax.floatValue(), 0, _myDimension.y);
		}
	}
	
	private CCVector2f _myStartPosition = new CCVector2f();
	
	@Override
	protected void onDragg(CCMouseEvent theEvent) {
		
		CCVector2f myEventPosition = CCVecMath.subtract(theEvent.position(), _myStartPosition);
		
		if(theEvent.isShiftDown()) {
			myEventPosition.scale(0.1f);
			myEventPosition = CCVecMath.add(_myStartPosition, myEventPosition);
		}else {
			_myStartPosition = theEvent.position().clone();
			myEventPosition = _myStartPosition;
		}
		
		
		changeValue(positionToValue(myEventPosition.x, myEventPosition.y));
		
		if(_myIsHorizontal){
			_myForeGroundSize = CCMath.constrain(myEventPosition.x - _myPosition.x, 0, _myDimension.x);
		}else{
			_myForeGroundSize = CCMath.constrain(myEventPosition.y - _myPosition.y, 0, _myDimension.y);
		}
	}

	@Override
	protected void onPress(CCMouseEvent theEvent) {
		super.onPress(theEvent);
		_myStartPosition = theEvent.position().clone();
		changeValue(positionToValue(_myStartPosition.x,_myStartPosition.y));
		if(_myIsHorizontal){
			_myForeGroundSize = _myStartPosition.x - _myPosition.x;
		}else{
			_myForeGroundSize = _myStartPosition.y - _myPosition.y;
		}
	}
	
	@Override
	public void onMove( CCMouseEvent theEvent ) {
		super.onMove(theEvent);
		_myInputPosition.x = theEvent.x();
		_myInputPosition.y = theEvent.y() - 4;
	}
	
	/**
	 * When selected the value can be typed into the slider. Any keyboard 
	 * input will be appended to the value string.
	 * Pressing return tries to set the value to the typed number.
	 * Wrong input won't result in any changes. 
	 * Hit "backspace" to cancel input!
	 */
	@Override 
	public void onKey( CCKeyEvent theEvent ) {
		CCKeyCode myKeyCode = theEvent.keyCode();
		if (theEvent.keyChar() == '\n' && _myKeyboardInput.length() > 0) {
			Float myValue = Float.parseFloat(_myKeyboardInput.toString());
			this.changeValue(toType(CCMath.constrain(myValue.floatValue(), _myMin.floatValue(), _myMax.floatValue())));
			_myKeyboardInput.setLength(0);
		} else if (myKeyCode == CCKeyCode.VK_BACK_SPACE) {
			_myKeyboardInput.setLength(0);
		} else if ((theEvent.keyChar() >= CCKeyCode.VK_0.code() && theEvent.keyChar() <= CCKeyCode.VK_9.code()) || (myKeyCode == CCKeyCode.VK_MINUS && _myKeyboardInput.length() == 0)) {
			_myKeyboardInput.append(theEvent.keyChar());
		} else if (myKeyCode == CCKeyCode.VK_PERIOD) {
			if (_myKeyboardInput.indexOf(".") == -1) {
				_myKeyboardInput.append(theEvent.keyChar());
			}
		} else {
			return;
		}
		_myInputText.text(_myKeyboardInput.toString());
	}

	@Override
	public void draw(CCGraphics g) {
		if(_myIsHorizontal){
			g.color(_myForeGround);
			g.rect(
				_myPosition.x,_myPosition.y,
				_myForeGroundSize, _myDimension.y
			);
			g.color(_myBackGround);
			g.rect(
				_myPosition.x+_myForeGroundSize,_myPosition.y,
				_myDimension.x-_myForeGroundSize, _myDimension.y
			);
			g.color(0f,0.5f);
			g.rect(_myLabel.position().x-2, _myLabel.position().y-_myLabel.height() - 12, _myLabel.width()+4, _myLabel.height()+14);
			g.color(_myUIColor.colorLabel);
			_myLabel.draw(g);
			g.color(_myUIColor.colorValue);
			
			_myValueText.draw(g);
			
//			if (_myKeyboardInput.length() > 0) {
//				g.color(new CCColor(0.8f, 0.8f, 0.8f));
//				
//				g.rect(_myInputPosition.x-2, _myInputPosition.y+2, 
//					   _myInputText.width()+3, -_myDimension.y);
//				_myInputText.position(_myInputPosition);
//				g.color(_myInputColor);
//				_myInputText.draw(g);
//			}
		}
		super.draw(g);
	}
	
	@Override
	public Type relativeValue(final float theValue) {
		return toType(CCMath.blend(_myMin.floatValue(), _myMax.floatValue(), theValue));
	}
	
	
	@Override
	public float changeFloatValue() {
		return CCMath.norm(value().floatValue(), _myMin.floatValue(), _myMax.floatValue());
	}
	
	@Override
	public void value(final Type theValue){
		super.value(theValue);
		_myValueText.text(value());
		_myForeGroundSize = valueToPostion(value());
	}
	
	public void min(Type theMin) {
		_myMin = theMin;
	}
	
	public void max(Type theMax) {
		_myMax = theMax;
	}
	
	public void preset(final int thePreset){
		super.preset(thePreset);
		_myForeGroundSize = valueToPostion(value());
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.CCUIValueElement#valueForXML(cc.creativecomputing.xml.CCXMLElement)
	 */
	@Override
	public Type valueForXML(CCXMLElement theXMLElement) {
		return toType(theXMLElement.floatAttribute("value",0));
	}

	
}
