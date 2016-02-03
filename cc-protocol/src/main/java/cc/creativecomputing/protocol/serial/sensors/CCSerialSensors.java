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
package cc.creativecomputing.protocol.serial.sensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.serial.CCISerialListener;
import cc.creativecomputing.protocol.serial.CCSerial;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCSerialSensors implements CCISerialListener, CCMouseListener, CCUpdateListener{
	
	private static class CCSensorMousePosition{
		private float _myMouseX;
		private float _myMouseY;
		private int _myID;
		
		private CCSensorMousePosition(final float theMouseX, final float theMouseY, final int theID) {
			_myMouseX = theMouseX;
			_myMouseY = theMouseY;
			_myID = theID;
		}
	}
	
	private List<CCSensorMousePosition> _mySensorMousePositions = new ArrayList<CCSensorMousePosition>();
	private float _myMouseScale = 1;
	private float _myMouseRadius = 10;

    private List<CCISerialSensorListener> _myListeners = new ArrayList<CCISerialSensorListener>();
    private List<Integer> _myIdsToSend = new ArrayList<Integer>();
    
    private CCAbstractWindowApp _myApp;
    @SuppressWarnings("unused")
	private CCSerial _mySerial;
    
    private StringBuffer _myMessageBuffer;
    
    private Map<Integer, Integer> _myIDMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> _myCategoryMap = new HashMap<Integer, Integer>();
    private boolean _myLoadedFromXML = false;
    
    private boolean _myDrawSensorPositions = false;
    
    public CCSerialSensors(final CCAbstractWindowApp theApp, final String thePort, final int theRate) {
    	_myApp = theApp;
    	_myApp.addUpdateListener(this);
    	try {
			_mySerial = new CCSerial(theApp, this, thePort, theRate);
		} catch (Exception e) {
		}
    	_myMessageBuffer = new StringBuffer();
    	theApp.addMouseListener(this);
    }
    
    public void loadSensors(final String theSensorData) {
    	_myLoadedFromXML = true;
    	CCXMLElement mySensorsXML = CCXMLIO.createXMLElement(theSensorData);
    	boolean myEmulateMouse = mySensorsXML.booleanAttribute("mouseemulation",false);
    	_myMouseRadius = mySensorsXML.floatAttribute("mouseradius",10);
    	
    	for(CCXMLElement mySensorXML:mySensorsXML.children()) {
    		String myInIDString = mySensorXML.attribute("inID");
    		int myInID;
    		if(myInIDString.contains(","))myInID = idFromString(myInIDString);
    		else myInID = Integer.parseInt(myInIDString);
    		
    		int myOutID = mySensorXML.intAttribute("outID");
    		int myCategory = mySensorXML.intAttribute("category", -1);
    		_myIDMap.put(myInID, myOutID);
    		_myCategoryMap.put(myOutID,myCategory);
    		
    		if(myEmulateMouse) {
    			float myMouseX = mySensorXML.floatAttribute("mouseX");
    			float myMouseY = mySensorXML.floatAttribute("mouseY");
    			_mySensorMousePositions.add(new CCSensorMousePosition(myMouseX, myMouseY, myOutID));
    		}
    	}
    }
    
    public void mouseScale(final float theMouseScale) {
    	_myMouseScale = theMouseScale;
    }
    
    public void addListener(final CCISerialSensorListener theListener){
    	_myListeners.add(theListener);
    }
    
    public void drawSensors(final boolean theDrawSensors){
    	_myDrawSensorPositions = theDrawSensors;
    }
    
    private int idFromString(final String theMessage) {
    	String[] myParts = theMessage.split(",");
    	int myController = Integer.parseInt(myParts[0].trim()) - 1;
    	int mySensor = Integer.parseInt(myParts[1].trim());
    	
    	switch(mySensor){
    	case 1:
    		mySensor = 0;
    		break;
    	case 2:
    		mySensor = 1;
    		break;
    	case 4:
    		mySensor = 2;
    		break;
    	case 8:
    		mySensor = 3;
    		break;
    	case 16:
    		mySensor = 4;
    		break;
    	case 32:
    		mySensor = 5;
    		break;
    	case 64:
    		mySensor = 6;
    		break;
    	case 128:
    		mySensor = 7;
    		break;
    	default:
    		return -1;
    	}
    	
    	int myID = myController * 8 + mySensor;
    	return myID;
    }
    
    private void handleSensorInput(final String theMessage){
    	int myID = idFromString(theMessage);
    	if(myID < 0)return;
    	if(_myLoadedFromXML) {
    		if(!_myIDMap.containsKey(myID))return;
    		else{
    			synchronized (_myIdsToSend) {
    				_myIdsToSend.add(myID);
    			}
    		}
    	}
    }
    
    private void sendSensorInput(final int theID) {
    	int myCategory = -1;
    	if(_myCategoryMap.containsKey(theID))myCategory = _myCategoryMap.get(theID);
    	
    	for(CCISerialSensorListener myListener : _myListeners){
    		myListener.onPressSensor(theID, myCategory);
    	}
    }

	public void onSerialEvent(CCSerial theSerial) {
		try {
			while(theSerial.available() > 0){
				char myChar = theSerial.readChar();
				if(myChar == '\n'){
					handleSensorInput(_myMessageBuffer.toString().trim());
					_myMessageBuffer = new StringBuffer();
					continue;
				}
				_myMessageBuffer.append(myChar);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void mouseClicked(CCMouseEvent theEvent) {}

	public void mouseEntered(CCMouseEvent theEvent) {}

	public void mouseExited(CCMouseEvent theEvent) {}

	public void mousePressed(CCMouseEvent theEvent) {
		for(CCSensorMousePosition myMousePosition:_mySensorMousePositions) {
			float myDist = CCMath.dist(
				theEvent.x() / _myMouseScale, theEvent.y() / _myMouseScale, 
				myMousePosition._myMouseX, myMousePosition._myMouseY
			);
			if(myDist < _myMouseRadius) {
				sendSensorInput(myMousePosition._myID);
			}
		}
	}

	public void mouseReleased(CCMouseEvent theEvent) {}
	
	public void update(float theDeltaTime) {
		
	}
	
	public void draw(CCGraphics g) {
		synchronized (_myIdsToSend) {
			for(Integer myID:_myIdsToSend){
				sendSensorInput(_myIDMap.get(myID));
			}
			_myIdsToSend.clear();
		}
		
		if(!_myDrawSensorPositions)return;
		
		for(CCSensorMousePosition myMousePosition:_mySensorMousePositions) {
			g.color(255);
			g.ellipse(
				myMousePosition._myMouseX - _myApp.width / 2 / _myMouseScale, 
				_myApp.height / 2 / _myMouseScale  - myMousePosition._myMouseY, 
				_myMouseRadius
			);
			g.color(0);
			g.text(myMousePosition._myID+":"+_myCategoryMap.get(myMousePosition._myID),
				myMousePosition._myMouseX - _myApp.width / 2 / _myMouseScale - _myMouseRadius/2, 
				_myApp.height / 2 / _myMouseScale  - myMousePosition._myMouseY + _myMouseRadius/4
			);
		}
	}

}
