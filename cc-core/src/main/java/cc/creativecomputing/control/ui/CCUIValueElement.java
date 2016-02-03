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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public abstract class CCUIValueElement<ValueType> extends CCUIElement{

	protected boolean _myIsControlledExternally;
	protected List<ValueType> _myValues;
	protected int _myPreset;
	
	protected StringBuffer _myKeyboardInput = new StringBuffer();
	protected CCVector2f _myInputPosition = new CCVector2f();
	protected CCColor _myInputColor = new CCColor(0.2f, 0.2f, 0.2f);
	protected CCText _myInputText;
	
	
	public CCUIValueElement(String theLabel, CCVector2f thePosition, CCVector2f theDimension, final List<ValueType> theValues, final int thePreset) {
		super();
		_myLabel = new CCText(CCControlUI.FONT);
		_myLabel.text(theLabel);
		_myID = theLabel;
		_myPosition = thePosition;
		_myDimension = theDimension;
		
		_myValues = theValues;
		_myPreset = thePreset;
		
		_myIsControlledExternally = false;
		
		setupText();
	}

	public CCUIValueElement(String theLabel, float theX, float theY, float theWidth, float theHeight, final List<ValueType> theValues, final int thePreset) {
		super();
		_myLabel = new CCText(CCControlUI.FONT);
		_myLabel.text(theLabel);
		_myID = theLabel;
		_myPosition = new CCVector2f(theX, theY);
		_myDimension = new CCVector2f(theWidth, theHeight);
		
		_myValues = theValues;
		_myPreset = thePreset;
		
		_myIsControlledExternally = false;
		
		setupText();
	}
	
	public CCUIValueElement(String theLabel, CCVector2f thePosition, CCVector2f theDimension, final ValueType theValue) {
		super();
		_myLabel = new CCText(CCControlUI.FONT);
		_myLabel.text(theLabel);
		_myID = theLabel;
		_myPosition = thePosition;
		_myDimension = theDimension;
		
		_myValues = new ArrayList<ValueType>();
		_myValues.add(theValue);
		_myPreset = 0;
		
		_myIsControlledExternally = false;
		
		setupText();
	}

	public CCUIValueElement(String theLabel, float theX, float theY, float theWidth, float theHeight, final ValueType theValue) {
		super();
		_myLabel = new CCText(CCControlUI.FONT);
		_myLabel.text(theLabel);
		_myID = theLabel;
		_myPosition = new CCVector2f(theX, theY);
		_myDimension = new CCVector2f(theWidth, theHeight);
		
		_myValues = new ArrayList<ValueType>();
		_myValues.add(theValue);
		_myPreset = 0;
		
		_myIsControlledExternally = false;
		
		setupText();
	}
	
	public void values(final List<ValueType> theValues) {
		_myValues = theValues;
		value(value());
	}

	public ValueType value(){
		return _myValues.get(CCMath.min(_myPreset,_myValues.size()-1));
	}
	
	public void value(final ValueType theValue){
		_myValues.set(CCMath.min(_myPreset,_myValues.size()-1),theValue);
	}
	
	public void changeValue(final ValueType theValue){
		value(theValue);
		onChange();
	}
	
	public abstract ValueType relativeValue(final float theValue); 
	
	public void changeFloatValue(final float theValue){
		value(relativeValue(theValue));
		onChange();
	}
	
	public float changeFloatValue() {
		return 0;
	}
	
	@Override
	public void preset(final int thePreset){
		_myPreset = thePreset;
		onChange();
	}
	
	@Override
	public void createPreset(){
		_myValues.add(_myValues.get(_myPreset));
		_myPreset = _myValues.size() - 1;
	}
	
	@Override
	public void deletePreset(){
		_myValues.remove(_myPreset);
		if(_myPreset != 0) _myPreset--;
		onChange();
	}
	
	@Override
	public void setupText() {
		float textY = _myPosition.y+_myDimension.y/2 + _myLabel.font().size()/2;
		_myInputText = new CCText(CCControlUI.FONT);
		_myInputText.position(_myPosition.x + 10, textY + 10);
	}
	
	public void draw(CCGraphics g) {
		if (_myKeyboardInput.length() > 0) {
			g.color(new CCColor(0.8f, 0.8f, 0.8f));
			
			g.rect(_myInputPosition.x-2, _myInputPosition.y+2, 
				   _myInputText.width()+3, -_myInputText.height()-3);
			_myInputText.position(_myInputPosition);
			g.color(_myInputColor);
			_myInputText.draw(g);
		}
	}

	
	public CCXMLElement xmlForValue(ValueType myValue) {
		CCXMLElement myResult = new CCXMLElement("preset");
		if(myValue == null)myResult.addAttribute("value", null);
		else myResult.addAttribute("value", myValue.toString());
		return myResult;
	}
	
	public abstract ValueType valueForXML(CCXMLElement theXMLElement);
	
	public void fromXML(CCXMLElement theData) {
		_myValues = new ArrayList<ValueType>();
		for(CCXMLElement myPresetXML:theData) {
			_myValues.add(valueForXML(myPresetXML));
		}
		changeValue(value());
	}
	
	@Override
	protected CCXMLElement toXML(){
		CCXMLElement myXMLElement = super.toXML();
		
		for(ValueType myValue:_myValues){
			myXMLElement.addChild(xmlForValue(myValue));
		}
		return myXMLElement;
	}
}
