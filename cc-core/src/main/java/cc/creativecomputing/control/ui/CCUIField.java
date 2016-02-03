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
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.util.CCFormatUtil;
import cc.creativecomputing.xml.CCXMLElement;

public class CCUIField extends CCUIValueElement<CCVector2f>{
	
	private CCVector2f _myMin = new CCVector2f();
	private CCVector2f _myMax = new CCVector2f(1,1);
	private CCVector2f _myForegroundDimension = new CCVector2f();
	
	private CCText _myValueText;

	/**
	 * @param theLabel
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIField(String theLabel, CCVector2f thePosition, CCVector2f theDimension, CCVector2f theMin, CCVector2f theMax) {
		super(theLabel, thePosition, theDimension, new CCVector2f());
		_myMin = theMin;
		_myMax = theMax;
	}
	

	/**
	 * @param theLabel
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIField(String theLabel, CCVector2f thePosition, CCVector2f theDimension, CCVector2f theValue, CCVector2f theMin, CCVector2f theMax) {
		super(theLabel, thePosition, theDimension, theValue);
		_myMin = theMin;
		_myMax = theMax;
	}

	/**
	 * @param theLabel
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUIField(String theLabel, float theX, float theY, float theWidth, float theHeight, CCVector2f theValue, CCVector2f theMin, CCVector2f theMax) {
		super(theLabel, theX, theY, theWidth, theHeight, theValue);
		_myMin = theMin;
		_myMax = theMax;
	}
	
	public CCUIField(String theLabel, float theX, float theY, float theWidth, float theHeight, CCVector2f theMin, CCVector2f theMax) {
		this(theLabel, theX, theY, theWidth, theHeight, new CCVector2f(), theMin, theMax);
	}
	
	private CCVector2f positionToValue(final float theX, final float theY){
		return new CCVector2f(
			CCMath.constrain(CCMath.map(theX, _myPosition.x, _myPosition.x + _myDimension.x, _myMin.x, _myMax.x), _myMin.x, _myMax.x),	
			CCMath.constrain(CCMath.map(theY, _myPosition.y, _myPosition.y + _myDimension.y, _myMin.y, _myMax.y), _myMin.y, _myMax.y)
		);
	}
	
//	private float valueToPostion(float theValue){
//		if(_myIsHorizontal){
//			return CCMath.map(theValue, _myMin, _myMax, 0, _myDimension.x);
//		}else{
//			return CCMath.map(theValue, _myMin, _myMax, 0, _myDimension.y);
//		}
//	}
	
	@Override
	public void setupText() {
		_myLabel.position(
			_myPosition.x + _myDimension.x + 3, 
			_myPosition.y + _myDimension.y - _myLabel.font().size()/2
		);
		_myValueText = new CCText(CCControlUI.FONT);
		_myValueText.position(
			_myPosition.x + 3, _myPosition.y + _myDimension.y - _myValueText.font().size()/2
		);
		

		_myBounds.min().set(_myPosition);
		_myBounds.width(_myDimension.x+_myLabel.width() +3);
		_myBounds.height(_myDimension.y);
		
		super.setupText();
	}

	@Override
	protected void onDragg(CCMouseEvent theEvent) {
		changeValue(positionToValue(theEvent.x(),theEvent.y()));
		_myForegroundDimension = new CCVector2f(
			CCMath.constrain(theEvent.x() - _myPosition.x, 0, _myDimension.x),
			CCMath.constrain(theEvent.y() - _myPosition.y, 0, _myDimension.y)
		);
	}

	@Override
	protected void onPress(CCMouseEvent theEvent) {
		super.onPress(theEvent);
		changeValue(positionToValue(theEvent.x(),theEvent.y()));
		_myForegroundDimension = new CCVector2f(
			theEvent.x() - _myPosition.x,
			theEvent.y() - _myPosition.y
		);
	}

	@Override
	public void draw(CCGraphics g) {
		g.color(_myBackGround);
		g.rect(_myPosition, _myDimension);
		g.color(_myForeGround);
		g.rect(_myPosition,_myForegroundDimension);
		
		g.color(_myUIColor.colorLabel);
		_myLabel.draw(g);
		g.color(_myUIColor.colorValue);
		_myValueText.draw(g);
		
		super.draw(g);
	}
	
	public CCVector2f relativeValue(final float theValue) {
		return new CCVector2f();
	}
	
	@Override
	public void value(final CCVector2f theValue){
		super.value(theValue);
		CCVector2f myValue = value();
		_myValueText.text(CCFormatUtil.nfc(myValue.x, 4) +" : " + CCFormatUtil.nfc(myValue.y, 4));
	}
	
	@Override
	protected CCXMLElement toXML(){
		CCXMLElement myXMLElement = super.toXML();
		CCVector2f myValue = value();
		myXMLElement.addAttribute("valueX", myValue.x);
		myXMLElement.addAttribute("valueY", myValue.y);
		return myXMLElement;
	}
	
	@Override
	public CCXMLElement xmlForValue(CCVector2f myValue) {
		CCXMLElement myResult = new CCXMLElement("preset");
		myResult.addAttribute("x", myValue.x);
		myResult.addAttribute("y", myValue.y);
		return myResult;
	};
	
	public CCVector2f min() {
		return _myMin;
	}
	
	public CCVector2f max() {
		return _myMax;
	}
	
	@Override 
	public void onKey( CCKeyEvent theEvent ) {
		CCKeyCode myKeyCode = theEvent.keyCode();
		if (theEvent.keyChar() == '\n' && _myKeyboardInput.length() > 0) {
			if (_myKeyboardInput.indexOf(",") == -1) {
				_myKeyboardInput.setLength(0);
				return;
			}
			String myFirst = _myKeyboardInput.substring(0, _myKeyboardInput.indexOf(","));
			String mySecond = _myKeyboardInput.substring(_myKeyboardInput.indexOf(",") + 1, _myKeyboardInput.length());
			
			Float myValue1 = Float.parseFloat(myFirst);
			Float myValue2 = Float.parseFloat(mySecond);
			this.changeValue(new CCVector2f(CCMath.constrain(myValue1, _myMin.x, _myMax.x),
										    CCMath.constrain(myValue2, _myMin.y, _myMax.y)));
			_myKeyboardInput.setLength(0);
		} else if (myKeyCode == CCKeyCode.VK_BACK_SPACE) {
			_myKeyboardInput.setLength(0);
		} else if ((theEvent.keyChar() >= CCKeyCode.VK_0.code() && theEvent.keyChar() <= CCKeyCode.VK_9.code()) || (myKeyCode == CCKeyCode.VK_MINUS)) {// && _myKeyboardInput.length() == 0)) {
			_myKeyboardInput.append(theEvent.keyChar());
		} else if (myKeyCode == CCKeyCode.VK_PERIOD) {
			_myKeyboardInput.append(theEvent.keyChar());
		} else if (myKeyCode == CCKeyCode.VK_COMMA) {
			if (_myKeyboardInput.indexOf(",") == -1) {
				_myKeyboardInput.append(theEvent.keyChar());
			}
		} else {
			return;
		}
		_myInputText.text(_myKeyboardInput.toString());
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.CCUIValueElement#valueForXML(cc.creativecomputing.xml.CCXMLElement)
	 */
	@Override
	public CCVector2f valueForXML(CCXMLElement theXMLElement) {
		return new CCVector2f(theXMLElement.floatAttribute("x"), theXMLElement.floatAttribute("y"));
	}
}
