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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCDistortableGrid implements CCMouseMotionListener{
	private class CCGridButton{
		private CCVector2f _myOrigin;
		private CCVector2f _myMax;
		private float _mySize;
		private boolean over;
		
		private CCGridButton(final CCVector2f theOrigin, final float theSize){
			_myOrigin = theOrigin;
			_mySize = theSize;
			_myMax = theOrigin.clone();
			_myMax.add(theSize, theSize);
		}
		
		void move(final float theX, final float theY){
			_myOrigin.add(theX, theY);
			_myMax.add(theX, theY);
		}
		
		public CCVector2f origin(){
			return _myOrigin;
		}
		
		
		void draw(CCGraphics g){
			if (over)g.color(0,255,0);
			else g.color(255,0,0);
			g.rect(_myOrigin.x-_mySize/2, _myOrigin.y-_mySize/2, _mySize, _mySize);
		}
		
		boolean isOver(final CCVector2f theMousePosition){
			if(CCVecMath.isInsideBox(theMousePosition, _myOrigin, _mySize/2)){
				over = true;
				return true;
			}
			over = false;
			return false;
		}
	}
	
	private float _myX;
	private float _myY;
	
	private float _myWidth;
	private float _myHeight;
	
	private float _myXspace;
	private float _myYspace;
	
	private int _myXresolution;
	private int _myYresolution;
	
	private CCGridButton[][] _myGridButtons;
	private CCGridButton _mySelectedButton;
	private CCApp _myApp;
	
	private String _myFileName;
	private float _myScale = 1;
	
	public CCDistortableGrid(
			final CCApp theApp, final String theFileName){
		_myApp = theApp;
		_myApp.addMouseMotionListener(this);
		load(theFileName);
	}
	
	public CCDistortableGrid(
		final CCApp theApp, 
		final float theX, final float theY,
		final float theWidth, final float theHeight, 
		final int theXresolution, final int theYresolution,
		final String theFileName
	){
		_myApp = theApp;
		_myApp.addMouseMotionListener(this);
		
		_myFileName = theFileName;
		
		_myX = theX;
		_myY = theY;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myXresolution = theXresolution;
		_myYresolution = theYresolution;
		
		_myXspace = _myWidth / _myXresolution;
		_myYspace = _myHeight / _myYresolution;
		
		initGrid();
	}
	
	private void initGrid(){
		_myGridButtons = new CCGridButton[_myXresolution + 1][_myYresolution + 1];
		
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				_myGridButtons[x][y] = new CCGridButton(new CCVector2f(_myX + x * _myXspace, _myY + y * _myYspace),10);
			}
		}
	}

	
	public void mouseDragged(CCMouseEvent theMouseEvent) {
		if(_mySelectedButton != null){
			_mySelectedButton.move(
				theMouseEvent.x() - theMouseEvent.px(),
				theMouseEvent.y() - theMouseEvent.py()
			);
		}
	}

	public void mouseMoved(CCMouseEvent theMouseEvent) {
		_mySelectedButton = null;
		CCVector2f myMousePosition = new CCVector2f(
			theMouseEvent.x() - _myApp.width/2,
			-theMouseEvent.y() + _myApp.height/2
		);
		myMousePosition.scale(1/_myScale);
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				if(_myGridButtons[x][y].isOver(myMousePosition)){
					_mySelectedButton = _myGridButtons[x][y];
				}
			}
		}
	}
	
	public void drawCalibration(CCGraphics g){
		g.beginShape(CCDrawMode.LINES);
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y < _myYresolution;y++){
				g.vertex(_myX + x * _myXspace, _myY + y * _myYspace);
				g.vertex(_myX + x * _myXspace, _myY + (y+1) * _myYspace);
			}
		}
		for(int x = 0; x < _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				g.vertex(_myX + x * _myXspace, _myY + y * _myYspace);
				g.vertex(_myX + (x+1) * _myXspace, _myY + y * _myYspace);
			}
		}
		g.endShape();
		
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				if(!_myHasFinishedCalibration && x == _myCalibrateX && y == _myCalibrateY){
					g.rect(_myX + x * _myXspace - 25, _myY + y * _myYspace - 25,50,50);
				}else{
					g.rect(_myX + x * _myXspace - 5, _myY + y * _myYspace - 5,10,10);
				}
			}
		}
	}
	
	public void draw(CCGraphics g){
		g.pushMatrix();
		g.scale(_myScale);
		g.beginShape(CCDrawMode.LINES);
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y < _myYresolution;y++){
				g.vertex(_myGridButtons[x][y].origin());
				g.vertex(_myGridButtons[x][y+1].origin());
			}
		}
		for(int x = 0; x < _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				g.vertex(_myGridButtons[x][y].origin());
				g.vertex(_myGridButtons[x+1][y].origin());
			}
		}
		g.endShape();
		
		CCColor saveColor = g.color();
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				_myGridButtons[x][y].draw(g);
			}
		}
		g.color(saveColor);
		g.popMatrix();
	}
	
	public CCVector2f transform(final CCVector2f theVector){
		CCVector2f myResult = theVector.clone();
		if(
			theVector.x <= _myX || 
			theVector.x >= _myX + _myWidth || 
			theVector.y <= _myY || 
			theVector.y >= _myY + _myHeight
		)return myResult;
		
		myResult.subtract(_myX, _myY);
		int x = (int)(myResult.x/_myWidth * _myXresolution);
		int y = (int)(myResult.y/_myHeight * _myYresolution);
		
		float xBlend = myResult.x/_myXspace - x;
		float yBlend = myResult.y/_myYspace - y;
		
		CCVector2f myBottomX = CCVecMath.blend(xBlend,_myGridButtons[x][y]._myOrigin,_myGridButtons[x+1][y]._myOrigin);
		CCVector2f myTopX = CCVecMath.blend(xBlend,_myGridButtons[x][y+1]._myOrigin,_myGridButtons[x+1][y+1]._myOrigin);
		return CCVecMath.blend(yBlend, myBottomX, myTopX);
	}
	
	private int _myCalibrateX = 0;
	private int _myCalibrateY = 0;
	private boolean _myHasFinishedCalibration = true;
	
	public void startCalibration(){
		_myCalibrateX = 0;
		_myCalibrateY = 0;
		_myHasFinishedCalibration = false;
	}
	
	public void calibrateMoveOpposite(final CCVector2f theOrigin){
		if(_myHasFinishedCalibration)return;
		CCVector2f myMove = CCVecMath.subtract(_myGridButtons[_myCalibrateX][_myCalibrateY].origin(), theOrigin);
		_myGridButtons[_myCalibrateX][_myCalibrateY].origin().add(myMove);
		
		_myCalibrateY++;
		if(_myCalibrateY > _myYresolution){
			_myCalibrateX++;
			if(_myCalibrateX > _myXresolution){
				_myHasFinishedCalibration = true;
				save(_myFileName);
			}
			_myCalibrateY = 0;
		}
	}
	
	public void calibrateMoveTo(final CCVector2f theOrigin){
		if(_myHasFinishedCalibration)return;
		_myGridButtons[_myCalibrateX][_myCalibrateY].origin().set(theOrigin);
		
		_myCalibrateY++;
		if(_myCalibrateY > _myYresolution){
			_myCalibrateX++;
			if(_myCalibrateX > _myXresolution){
				_myHasFinishedCalibration = true;
			}
			_myCalibrateY = 0;
		}
	}
	
	public void skipPoint(){
		_myCalibrateY++;
		if(_myCalibrateY > _myYresolution){
			_myCalibrateX++;
			if(_myCalibrateX > _myXresolution){
				_myHasFinishedCalibration = true;
			}
			_myCalibrateY = 0;
		}
	}
	
	public void calibrate(final int theX, final int theY, final CCVector2f theOrigin){
		_myGridButtons[theX][theY].origin().set(theOrigin);
	}
	
	public void save(final String theFile){
		CCXMLElement myGrid = new CCXMLElement("gridpoints");
		myGrid.addAttribute("x", _myX);
		myGrid.addAttribute("y", _myY);
		myGrid.addAttribute("width", _myWidth);
		myGrid.addAttribute("height", _myHeight);
		myGrid.addAttribute("xresolution", _myXresolution);
		myGrid.addAttribute("yresolution", _myYresolution);
		
		for(int x = 0; x <= _myXresolution;x++){
			for(int y = 0; y <= _myYresolution;y++){
				CCXMLElement myPointXML = new CCXMLElement("point");
				myPointXML.addAttribute("x", _myGridButtons[x][y].origin().x);
				myPointXML.addAttribute("y", _myGridButtons[x][y].origin().y);
				myPointXML.addAttribute("xid", x);
				myPointXML.addAttribute("yid", y);
				myGrid.addChild(myPointXML);
			}
		}
		CCXMLIO.saveXMLElement(myGrid, theFile);
	}
	
	public void load(final String theFile){
		CCXMLElement myGridXML = CCXMLIO.createXMLElement(theFile);
		_myX = myGridXML.floatAttribute("x");
		_myY = myGridXML.floatAttribute("y");
		
		_myWidth = myGridXML.floatAttribute("width");
		_myHeight = myGridXML.floatAttribute("height");
		
		_myXresolution = myGridXML.intAttribute("xresolution");
		_myYresolution = myGridXML.intAttribute("yresolution");
		
		_myXspace = _myWidth / _myXresolution;
		_myYspace = _myHeight / _myYresolution;
		
		_myGridButtons = new CCGridButton[_myXresolution + 1][_myYresolution + 1];
		
		for(CCXMLElement myPoint:myGridXML.children()){
			_myGridButtons[myPoint.intAttribute("xid")][myPoint.intAttribute("yid")] = new CCGridButton(new CCVector2f(
				myPoint.floatAttribute("x"), 
				myPoint.floatAttribute("y"))
			,10);
		}
	}
	
	public void scale(final float theScale){
		_myScale = theScale;
	}
}
