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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public class CCUIDropDown extends CCUIValueElement<Enum<?>>{
	
	protected CCText _myValueLabel;
	
	private Method _myMethod;
	
	private List<CCUIButton> _myButtons = new ArrayList<CCUIButton>();
	private float _myClosedHeight;
	private float _myOpenHeight;
	private Enum<?>[] _myEnums;
	
//	protected boolean _myIsActive = false;
	
	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIDropDown(final String theLabel, final Class<?> theEnum, CCVector2f thePosition, CCVector2f theDimension) {
		this(theLabel, theEnum, thePosition.x, thePosition.y, theDimension.x,theDimension.y);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUIDropDown(final String theLabel, final Class<?> theEnum, float theX, float theY, float theWidth, float theHeight) {
		super(theLabel, theX, theY, theWidth, theHeight,null);
		
		try {
			Method myMethod = theEnum.getMethod("values", new Class<?>[0]);
			Object myResult = myMethod.invoke(null, new Object[0]);
			_myEnums = (Enum<?>[]) myResult;
			float myY = theY + theHeight + 1;
			for(Enum<?> myEnum:_myEnums) {
				CCUIButton myButton = new CCUIButton(myEnum.toString(), theX, myY, theWidth, theHeight);
				myButton.isVisible(false);
				_myButtons.add(myButton);
				myY += theHeight + 1;
			}
			_myMethod = theEnum.getMethod("valueOf", new Class<?>[] {String.class});
			
			_myClosedHeight = theHeight;
			_myOpenHeight = myY - theY;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setupText() {
		_myLabel.position(
			_myPosition.x + _myDimension.x + 3, 
			_myPosition.y + _myDimension.y/2 + _myLabel.font().size()/2
		);
		_myValueLabel = new CCText(CCControlUI.FONT);
		String myValueString = value()== null ? "..." : value().toString();
		_myValueLabel.text(myValueString);
		_myValueLabel.position(
			_myPosition.x + 3, 
			_myPosition.y + _myDimension.y/2 + _myLabel.font().size()/2
		);
		
		float myY = _myPosition.y + _myDimension.y + 1;

		if(_myButtons == null)return;
		for(CCUIButton myButton:_myButtons) {
			myButton.position().set(_myPosition.x, myY);
			myButton.setupText();
			myY += _myDimension.y + 1;
		}
		
		_myBounds.min().set(_myPosition);
		_myBounds.width(_myDimension.x);
		_myBounds.height(_myClosedHeight);
	}
	
	

	@Override
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.translate(0,0,1);
		g.color(_myForeGround);
		g.rect(_myPosition,_myDimension);
		g.color(_myUIColor.colorLabel);
		_myLabel.draw(g);
		_myValueLabel.draw(g);
		g.pushMatrix();
		g.translate(0,0,10);
		for(CCUIButton myButton:_myButtons) {
			if(!myButton._myIsVisible)break;
			myButton.draw(g);
			g.color(0);
			g.line(
				myButton._myPosition.x, 
				myButton._myPosition.y + myButton._myDimension.y, 
				myButton._myPosition.x + myButton._myDimension.x, 
				myButton._myPosition.y + myButton._myDimension.y
			);
		}
		g.popMatrix();
		g.popMatrix();
	}
	
	CCUIButton _mySelectedButton = null;
	
	@Override
	public void onDragg(final CCMouseEvent theEvent) {
		if (_mySelectedButton != null) {
			if (_mySelectedButton.isOver(theEvent.x(), theEvent.y())) {
				return;
			} else {
				_mySelectedButton.onOut();
				_mySelectedButton = null;
			}
		}
		for (CCUIButton myButton : _myButtons) {
			if (myButton.isOver(theEvent.x(), theEvent.y())) {
				_mySelectedButton = myButton;
				_mySelectedButton.onOver();
				return;
			}
		}
	}
	
	@Override
	public void onOver(){
		_myForeGround = _myUIColor.colorForegroundOver;
		_myBackGround = _myUIColor.colorBackgroundOver;
	} 
	
	@Override
	public void onOut(){
		_myForeGround = _myUIColor.colorForeground;
		_myBackGround = _myUIColor.colorBackground;
	}
	
	@Override
	protected void onPress(CCMouseEvent theEvent){
		super.onPress(theEvent);
		
		for(CCUIButton myButton:_myButtons) {
			myButton.isVisible(true);
		}
		_myBounds.height(_myOpenHeight);
		_myDepth = 1000;
	}
	
	private Enum<?> valueOf(final String theValue){
		if(theValue.equals("null"))return null;
		try {
			return (Enum<?>)_myMethod.invoke(null, new Object[] {theValue});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not read value for enumeration");
		} 
	}
	
	@Override
	protected void onRelease(CCMouseEvent theEvent) {
		if (_mySelectedButton != null) {
			_myValueLabel.text(_mySelectedButton.label());
			try {
				changeValue(valueOf(_mySelectedButton.label()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(CCUIButton myButton:_myButtons) {
			myButton.isVisible(false);
		}
		_myBounds.height(_myClosedHeight);
		_myDepth = 0;
	}
	
	@Override
	protected void onReleaseOutside(CCMouseEvent theEvent) {
		super.onRelease(theEvent);
		
		for(CCUIButton myButton:_myButtons) {
			myButton.isVisible(false);
		}

		_myBounds.height(_myClosedHeight);
		_myDepth = 0;
	}
	

	
	@Override
	public Enum<?> relativeValue(final float theValue) {
		return _myEnums[(int)CCMath.blend(0, _myEnums.length - 1, theValue)];
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.CCUIValueElement#value(java.lang.Object)
	 */
	@Override
	public void value(Enum<?> theValue) {
		super.value(theValue);
		if(theValue == null)_myValueLabel.text("...");
		else _myValueLabel.text(theValue.toString());
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.CCUIValueElement#valueForXML(cc.creativecomputing.xml.CCXMLElement)
	 */
	@Override
	public Enum<?> valueForXML(CCXMLElement theXMLElement) {
		return valueOf(theXMLElement.attribute("value"));
	}
	
}
