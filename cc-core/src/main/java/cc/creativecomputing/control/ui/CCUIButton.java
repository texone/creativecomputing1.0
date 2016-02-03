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

import java.util.List;

import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public class CCUIButton extends CCUIValueElement<Boolean>{
	
	public static abstract class CCUIButtonListener{
		
	}
	
	protected boolean _myIsToggle = true;
	protected boolean _myIsBang = false;
	
//	protected boolean _myIsActive = false;

	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIButton(final String theLabel, CCVector2f thePosition, CCVector2f theDimension) {
		super(theLabel, thePosition, theDimension,false);
		_myBounds.min().set(thePosition);
		_myBounds.width(theDimension.x);
		_myBounds.height(theDimension.y);
	}
	
	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIButton(final String theLabel, CCVector2f thePosition, CCVector2f theDimension, List<Boolean> theValues, final int thePreset) {
		super(theLabel, thePosition, theDimension,theValues,thePreset);
		_myBounds.min().set(thePosition);
		_myBounds.width(theDimension.x);
		_myBounds.height(theDimension.y);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUIButton(final String theLabel, float theX, float theY, float theWidth, float theHeight, List<Boolean> theValues, final int thePreset) {
		super(theLabel, theX, theY, theWidth, theHeight,theValues,thePreset);
		_myBounds.min().set(theX, theY);
		_myBounds.width(theWidth);
		_myBounds.height(theHeight);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUIButton(final String theLabel, float theX, float theY, float theWidth, float theHeight) {
		super(theLabel, theX, theY, theWidth, theHeight,false);
		_myBounds.min().set(theX, theY);
		_myBounds.width(theWidth);
		_myBounds.height(theHeight);
	}
	
	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIButton(final String theLabel, final boolean theIsToggle, CCVector2f thePosition, CCVector2f theDimension, List<Boolean> theValues, final int thePreset) {
		super(theLabel, thePosition, theDimension,theValues,thePreset);
		_myIsToggle = theIsToggle;
		_myBounds.min().set(thePosition);
		_myBounds.width(theDimension.x);
		_myBounds.height(theDimension.y);
	}
	
	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUIButton(final String theLabel, final boolean theIsToggle, CCVector2f thePosition, CCVector2f theDimension) {
		super(theLabel, thePosition, theDimension,false);
		_myIsToggle = theIsToggle;
		_myBounds.min().set(thePosition);
		_myBounds.width(theDimension.x);
		_myBounds.height(theDimension.y);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUIButton(final String theLabel, final boolean theIsToggle, float theX, float theY, float theWidth, float theHeight) {
		super(theLabel, theX, theY, theWidth, theHeight,false);
		_myIsToggle = theIsToggle;
		_myBounds.min().set(theX, theY);
		_myBounds.width(theWidth);
		_myBounds.height(theHeight);
	}
	
	@Override
	public void setupText() {
		_myLabel.position(
			_myPosition.x + 3, 
			_myPosition.y + _myDimension.y/2 + _myLabel.font().size()/2
		);
		_myBounds.min().set(_myPosition);
		_myBounds.width(_myDimension.x);
		_myBounds.height(_myDimension.y);
	}

	@Override
	public void draw(CCGraphics g) {
		if(!_myIsVisible)return;
		g.color(_myForeGround);
		g.rect(_myPosition,_myDimension);
		g.color(_myUIColor.colorLabel);
		if ( _myIsToggle && !value() )	//if im a toggle button and im off, paint label im dimmer color
			g.color(_myUIColor.colorBackground);
		
		_myLabel.draw(g);
	}
	
	@Override
	public void onOver(){
		if(!value()){
			_myForeGround = _myUIColor.colorForegroundOver;
			_myBackGround = _myUIColor.colorBackgroundOver;
		}
	}
	
	@Override
	public void onOut(){
		if(!value()){
			_myForeGround = _myUIColor.colorForeground;
			_myBackGround = _myUIColor.colorBackground;
		}
	}
	
	@Override
	protected void onPress(CCMouseEvent theEvent){
		super.onPress(theEvent);
		
		if(_myIsBang){
			changeValue(true);
			return;
		}
		
		if(!_myIsToggle){
			changeValue(true);
		}else{
			changeValue(!value());
		}
	}
	
	@Override
	public Boolean relativeValue(final float theValue) {
		return theValue > 0.5f;
	}
	
	@Override
	protected void onRelease(CCMouseEvent theEvent) {
		
		if(_myIsBang){
			super.onRelease(theEvent);
			return;
		}
		
		if(!_myIsToggle){
			changeValue(false);
			super.onRelease(theEvent);
		}
	}
	
	@Override
	protected void onReleaseOutside(CCMouseEvent theEvent) {
		super.onRelease(theEvent);
		
		if(!_myIsToggle){
			changeValue(false);
		}
	}
	
	public void isBang(boolean theIsBang){
		_myIsBang = theIsBang;
	}
	
	public boolean isBang(){
		return _myIsBang;
	}
	
	public boolean isToggle(){
		return _myIsToggle;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.control.CCUIValueElement#valueForXML(cc.creativecomputing.xml.CCXMLElement)
	 */
	@Override
	public Boolean valueForXML(CCXMLElement theXMLElement) {
		return theXMLElement.booleanAttribute("value");
	}
	
	@Override
	public void fromXML(CCXMLElement theData) {
		if(_myIsBang)return;
		else super.fromXML(theData);
	}
}
