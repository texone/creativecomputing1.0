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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;

public class CCUITextBox extends CCUIElement{

	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCUITextBox(final String theLabel, CCVector2f thePosition, CCVector2f theDimension) {
		super(theLabel, thePosition, theDimension);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public CCUITextBox(final String theLabel, float theX, float theY, float theWidth, float theHeight) {
		super(theLabel, theX, theY, theWidth, theHeight);
	}
	
	@Override
	public void setupText() {
		_myLabel.position(_myPosition.x + 3, _myPosition.y + _myDimension.y/2 + _myLabel.size()/2);
	}

	@Override
	public void draw(CCGraphics g) {
		g.polygonMode(CCPolygonMode.LINE);
		g.color(_myForeGround);
		g.rect(_myPosition,_myDimension);
		g.color(_myUIColor.colorLabel);
		_myLabel.draw(g);
		g.polygonMode(CCPolygonMode.FILL);
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
	
	public void fromXML(CCXMLElement theElement) {
		
	}

	@Override
	protected CCXMLElement toXML(){
		CCXMLElement myXMLElement = super.toXML();
		return myXMLElement;
	}
}
