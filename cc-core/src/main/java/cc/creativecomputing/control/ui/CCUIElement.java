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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public abstract class CCUIElement implements Comparable<CCUIElement>{
	protected CCText _myLabel;
	protected String _myID;
	protected boolean _myDoSerialize = true;
	protected CCVector2f _myPosition;
	protected CCVector2f _myDimension;
	protected CCUIColor _myUIColor = new CCUIColor();
	
	protected boolean _myIsVisible = true;
	
	protected CCColor _myForeGround = _myUIColor.colorForeground;
	protected CCColor _myBackGround = _myUIColor.colorBackground;
	
	protected List<CCUIChangeListener> _myChangeListener = new ArrayList<CCUIChangeListener>();
	
	protected int _myDepth = 0;
	
	protected CCAABoundingRectangle _myBounds;
	
	protected int _myColumn = 0;
	
	protected CCUIElement() {
		_myBounds = new CCAABoundingRectangle();
	}
	
	public CCUIElement(final String theID, final CCVector2f thePosition, final CCVector2f theDimension){
		_myLabel = new CCText(CCControlUI.FONT);
		_myLabel.text(theID);
		_myPosition = thePosition;
		_myDimension = theDimension;
		_myID = theID;
		_myBounds = new CCAABoundingRectangle();
		setupText();
	}
	
	public CCUIElement(final String theLabel, final float theX, final float theY, final float theWidth, final float theHeight){
		this(theLabel, new CCVector2f(theX, theY), new CCVector2f(theWidth, theHeight));
	}
	

	
	public void column(int theColumn){
		_myColumn = theColumn;
	}
	
	public int column(){
		return _myColumn;
	}
	
	public abstract void setupText();
	
	public void updateRepresentation() {
		
	}
	
	public abstract void draw(final CCGraphics g);
	
	public void onOver(){
		_myForeGround = _myUIColor.colorForegroundOver;
		_myBackGround = _myUIColor.colorBackgroundOver;
	}
	
	public void onOut(){
		_myForeGround = _myUIColor.colorForeground;
		_myBackGround = _myUIColor.colorBackground;
	}
	
	protected void onPress(final CCMouseEvent theEvent){
		_myForeGround = _myUIColor.colorActive;
	}
	
	protected void onRelease(final CCMouseEvent theEvent){
		_myForeGround = _myUIColor.colorForegroundOver;
	}
	
	protected void onReleaseOutside(final CCMouseEvent theEvent){
		_myForeGround = _myUIColor.colorForeground;
	}
	
	protected void onDragg(final CCMouseEvent theEvent){
		
	}
	
	public void onMove(final CCMouseEvent theEvent){
		
	}
	
	public void onKey(final CCKeyEvent theEvent) {
	
	}
	
	public void onChange(){
		for(CCUIChangeListener myListener:_myChangeListener){
			myListener.onChange(this);
		}
	}
	
	public void addChangeListener(final CCUIChangeListener theChangeListener){
		_myChangeListener.add(theChangeListener);
	}
	
	public boolean isOver(final float theX, final float theY){
		if(!_myIsVisible) {
			return false;
		}
		
		return _myBounds.isInside(theX, theY);
//		return 
//			theX > _myPosition.x() && 
//			theX < _myPosition.x() + _myDimension.x() &&
//			theY > _myPosition.y() && 
//			theY < _myPosition.y() + _myDimension.y();
	}
	
	public String label(){
		return _myLabel.text();
	}
	
	public void label(final String theLabel){
		_myLabel.text(theLabel);
	}
	
	public void isVisible(final boolean theIsVisible) {
		_myIsVisible = theIsVisible;
	}
	
	public void preset(final int thePreset){}
	
	public void createPreset(){}
	
	public void deletePreset(){}
	
	protected CCXMLElement toXML(){
		CCXMLElement myXMLElement = new CCXMLElement(this.getClass().getSimpleName());
		myXMLElement.addAttribute("label", _myID);
		return myXMLElement;
	}
	
	protected abstract void fromXML(final CCXMLElement theXML);

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(CCUIElement theO) {
		return _myDepth - theO._myDepth;
	}
	
	/**
	 * Returns a reference to the position vector of the element
	 * @return reference to the position vector of the element
	 */
	public CCVector2f position() {
		return _myPosition;
	}
	
	/**
	 * Returns a reference to the dimension vector of the element
	 * @return reference to the dimension vector of the element
	 */
	public CCVector2f dimension() {
		return _myDimension;
	}
	
	public CCAABoundingRectangle bounds() {
		return _myBounds;
	}
	
	public void isSerialized(final boolean theIsSerialized) {
		_myDoSerialize = theIsSerialized;
	}
}
