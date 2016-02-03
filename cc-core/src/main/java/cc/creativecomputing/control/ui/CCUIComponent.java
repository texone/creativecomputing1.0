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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.ui.layout.CCUILayoutManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public class CCUIComponent extends CCUIElement{

	protected List<CCUIElement> _myUIElements = new ArrayList<CCUIElement>();
	protected Map<String, CCUIElement> _myUIElementsMap = new HashMap<String, CCUIElement>();

	protected CCUIElement _mySelectedElement;
	
	protected CCUILayoutManager _myLayoutManager;
	
	protected CCXMLElement _myXML;
	
	protected boolean _myDrawLabel = true;
	
	public CCUIComponent(String theLabel, CCVector2f thePosition, CCVector2f theDimension) {
		super(theLabel, thePosition, theDimension);
		_myLayoutManager = new CCUILayoutManager(this);
	}
	
	/**
	 * @param theLabel
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUIComponent(String theLabel, float theX, float theY, float theWidth, float theHeight) {
		super(theLabel, theX, theY, theWidth, theHeight);
		_myLayoutManager = new CCUILayoutManager(this);
		_myBounds.min().set(theX, theY);
	}
	
	public void layoutManager(final CCUILayoutManager theLayoutManager) {
		_myLayoutManager = theLayoutManager;
	}
	
	@Override
	public void setupText() {
		_myLabel.position(10, - 10);
		_myBounds.position(_myPosition);
	}

	public void add(CCUIElement theElement){
		_myLayoutManager.layout(theElement);
		_myUIElements.add(theElement);
		_myUIElementsMap.put(theElement.label(), theElement);

		_myBounds.add(theElement.bounds());
		
		if(_myXML == null)return;
		
		CCXMLElement myUIElementsXML = _myXML.child("uielements");
		for(CCXMLElement myUIElementXML:myUIElementsXML) {
			String myLabel = myUIElementXML.attribute("label");
			if(myLabel.equals(theElement.label())){
				theElement.fromXML(myUIElementXML);
				break;
			}
		}
	}
	
	public void remove(CCUIElement theElement){
		_myUIElements.remove(theElement);
		_myUIElementsMap.remove(theElement.label());
		
		_myLayoutManager.reset();
		
		for(CCUIElement myElement:_myUIElements){
			_myLayoutManager.layout(myElement);
		}
	}

	@Override
	public void draw(CCGraphics g) {
//		g.polygonMode(CCPolygonMode.LINE);
//		g.rect(_myBounds.min().x, _myBounds.min().y, _myBounds.width(), _myBounds.height());
//		g.polygonMode(CCPolygonMode.FILL);
		
		g.pushMatrix();
		g.translate(_myPosition);
		
		if(_myDrawLabel) {
			g.color(255,255,255);	//make sure we paint the title white
			_myLabel.draw(g);
			g.line(12 + _myLabel.width(),-10,200,-10);
			g.line(0,-10,8,-10);
		}
		Collections.sort(_myUIElements);
		for(CCUIElement myElement:_myUIElements){
			myElement.draw(g);
		}
		g.popMatrix();
	}
	
	public boolean isOver(float theX, float theY){
		if(!_myIsVisible) {
			return false;
		}
		theX -= _myPosition.x;
		theY -= _myPosition.y;
		for(CCUIElement myElement:_myUIElements){
			if(myElement.isOver(theX, theY))return true;
		}
		return false;
	}
	
	@Override
	public void onOut() {
		if(_mySelectedElement != null){
			_mySelectedElement.onOut();
			_mySelectedElement = null;
		}
	}
	
	protected CCMouseEvent localEvent(final CCMouseEvent theEvent) {
		CCMouseEvent myLocalEvent = theEvent.clone();
		myLocalEvent.position().subtract(_myPosition);
		myLocalEvent.pPosition().subtract(_myPosition);
		return myLocalEvent;
	}
	
	@Override
	public void onPress(final CCMouseEvent theEvent){
		CCMouseEvent myLocalMouseEvent = localEvent(theEvent);
		if(_mySelectedElement != null){
			_mySelectedElement.onPress(myLocalMouseEvent);
		}
	}
	
	@Override
	public void onRelease(final CCMouseEvent theEvent){
		CCMouseEvent myLocalMouseEvent = localEvent(theEvent);
		if(_mySelectedElement != null){
			if(_mySelectedElement.isOver(myLocalMouseEvent.x(),myLocalMouseEvent.y())){
				_mySelectedElement.onRelease(myLocalMouseEvent);
			}else{
				_mySelectedElement.onReleaseOutside(myLocalMouseEvent);
			}
		}
	}
	
	@Override
	public void onDragg(final CCMouseEvent theEvent){
		CCMouseEvent myLocalMouseEvent = localEvent(theEvent);
		if(_mySelectedElement != null && _mySelectedElement != this){
			_mySelectedElement.onDragg(myLocalMouseEvent);
		}
	}
	
	@Override
	public void onMove(final CCMouseEvent theEvent){
		CCMouseEvent myLocalMouseEvent = localEvent(theEvent);
		if(_mySelectedElement != null){
			if(_mySelectedElement.isOver(myLocalMouseEvent.x(),myLocalMouseEvent.y())){
				_mySelectedElement.onMove(myLocalMouseEvent);
				return;
			}else{
				_mySelectedElement.onOut();
				_mySelectedElement = null;
			}
		}
		for(CCUIElement myUIElement:_myUIElements){
			if(myUIElement.isOver(myLocalMouseEvent.x(),myLocalMouseEvent.y())){
				_mySelectedElement = myUIElement;
				_mySelectedElement.onOver();
				return;
			}
		}
	}
	
	/**
	 * Forward keyboard event to selected element
	 */
	@Override
	public void onKey(final CCKeyEvent theEvent) {
		if (_mySelectedElement != null) {
			_mySelectedElement.onKey(theEvent);
		}
	}
	
	
	@Override
	public void preset(final int thePreset){
		for(CCUIElement myUIElement:_myUIElements){
			if(myUIElement._myDoSerialize){
				myUIElement.preset(thePreset);
			}
		}
	}
	
	@Override
	public void createPreset(){
		for(CCUIElement myUIElement:_myUIElements){
			if(myUIElement._myDoSerialize){
				myUIElement.createPreset();
			}
		}
	}
	
	@Override
	public void deletePreset(){
		for(CCUIElement myUIElement:_myUIElements){
			if(myUIElement._myDoSerialize){
				myUIElement.deletePreset();
			}
		}
	}
	
	public void fromXML(final CCXMLElement theXML) {
		if(theXML == null)return;
		_myXML = theXML;
		CCXMLElement myUIElementsXML = theXML.child("uielements");
		for(CCXMLElement myUIElementXML:myUIElementsXML) {
			String myLabel = myUIElementXML.attribute("label");
			CCUIElement myElement = _myUIElementsMap.get(myLabel);
			if(myElement != null)myElement.fromXML(myUIElementXML);
		}
	}
	
	public void updateValues() {
		if(_myXML == null)return;
		CCXMLElement myUIElementsXML = _myXML.child("uielements");
		for(CCXMLElement myUIElementXML:myUIElementsXML) {
			String myLabel = myUIElementXML.attribute("label");
			CCUIElement myElement = _myUIElementsMap.get(myLabel);
			if(myElement != null)myElement.fromXML(myUIElementXML);
		}
	}

	@Override
	public CCXMLElement toXML(){
		CCXMLElement myComponentXML = super.toXML();
		CCXMLElement myUIElementsXML = new CCXMLElement("uielements");
		for(CCUIElement myUIElement:_myUIElements){
			if(myUIElement._myDoSerialize)
				myUIElementsXML.addChild(myUIElement.toXML());
		}
		myComponentXML.addChild(myUIElementsXML);
		return myComponentXML;
	}
	
	@SuppressWarnings("unchecked")
	public<ElementType extends CCUIElement> ElementType element(final String theName, Class<ElementType> theClass){
		CCUIElement myElement = _myUIElementsMap.get(theName);
		if(myElement == null)return null;
		if(!(myElement.getClass().equals(theClass)))return null;
		return (ElementType)_myUIElementsMap.get(theName);
	}
}
