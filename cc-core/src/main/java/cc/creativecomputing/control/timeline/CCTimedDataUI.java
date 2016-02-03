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
package cc.creativecomputing.control.timeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.control.ui.CCUIButton;
import cc.creativecomputing.control.ui.CCUIChangeListener;
import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;
import cc.creativecomputing.control.ui.CCUITextBox;
import cc.creativecomputing.control.ui.CCUIValueElement;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseEvent.CCMouseButton;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.xml.CCXMLElement;

public class CCTimedDataUI extends CCUIValueElement<CCTimedData[]> implements CCZoomable{
	
	protected class CCUIEnvelopeControl extends CCUIComponent{
		private List<String> _myEvelopeNames = new ArrayList<String>();
		private CCUIButton _myPreviouseEnvelopeButton;
		private CCUIButton _myNextEnvelopeButton;
		private CCUITextBox _myTextBox;
		
		public CCUIEnvelopeControl(final int theNumberOfEnvelopes){
			super("envelopes", 0, 0, 0, 0);
			_myDrawLabel = false;
			_myTextBox = new CCUITextBox("Env:0",19 ,0,30,14);
			_myTextBox.isSerialized(false);
			add(_myTextBox);
			
			_myPreviouseEnvelopeButton = new CCUIButton("-",false,0,0,14,14);
			_myPreviouseEnvelopeButton.isSerialized(false);
			_myPreviouseEnvelopeButton.addChangeListener(new CCUIChangeListener(){

				public void onChange(CCUIElement theElement) {
					CCUIButton myButton = (CCUIButton)theElement;
					if(myButton.value())return;
					if(_myEnvelopeIndex > 0){
						_myEnvelopeIndex--;
						_myTextBox.label(_myEvelopeNames.get(_myEnvelopeIndex));
					}
				}
				
			});
			add(_myPreviouseEnvelopeButton);
			
			_myNextEnvelopeButton = new CCUIButton("+",false,54 ,0,14,14);
			_myNextEnvelopeButton.isSerialized(false);
			_myNextEnvelopeButton.addChangeListener(new CCUIChangeListener(){

				public void onChange(CCUIElement theElement) {
					CCUIButton myButton = (CCUIButton)theElement;
					if(myButton.value())return;
					if(_myEnvelopeIndex < _myEvelopeNames.size()-1){
						_myEnvelopeIndex++;
						_myTextBox.label(_myEvelopeNames.get(_myEnvelopeIndex));
					}
				}
			});
			add(_myNextEnvelopeButton);
			
			for(int i = 0; i < theNumberOfEnvelopes;i++) {
				_myEvelopeNames.add("Env:"+i);
			}
		}
		
		public int numberOfPresets(){
			return _myEvelopeNames.size();
		}
		
		public List<String> presetNames(){
			return _myEvelopeNames;
		}
	}

	public static final CCColor LINE_COLOR = new CCColor(0.14f, 0.86f, 0.9f, 0.25f);
	public static final CCColor FILL_COLOR = CCColor.createFromInteger(0xff006799);
	public static final CCColor DOT_COLOR = CCColor.createFromInteger(0xff007FBF);
	public static final CCColor SPECIAL_DOT_COLOR = new CCColor(0.9f, 0.2f, 0.2f, 1.0f);

	public static final int MAX_GRID_LINES = 200;
	public static final int MAX_RULER_LABELS = 10;
	public static final int MIN_RULER_INTERVAL = 250;
	public static final float PICK_RADIUS = 10;
	public static final int DEFAULT_RANGE = 10000;

	private float _myCurrentPosition;
	private float _myLowerBound;
	private float _myUpperBound;
	private float _myGridInterval;
	private int _myPointSize;
	private CCTimedDataPoint _myDraggedPoint;
	private CCTimelineSelection _mySelection;

	private CCZoomControl _myZoomControl;

	private CCTimelineCurveType _myCurveType;
	
	private CCUIEnvelopeControl _myEnvelopeControl;
	
	private CCText _myValueText;
	
	private int _myNumberOfEnvelopes;
	private int _myEnvelopeIndex = 0;
	
	/**
	 * @param thePosition
	 * @param theDimension
	 */
	public CCTimedDataUI(final String theLabel, CCVector2f thePosition, CCVector2f theDimension, final int theNumberOfEnvelopes) {
		super(theLabel, thePosition, theDimension,new CCTimedData[theNumberOfEnvelopes]);
		_myNumberOfEnvelopes = theNumberOfEnvelopes;
		
		_myBounds.min().set(thePosition);
		_myBounds.width(theDimension.x);
		_myBounds.height(theDimension.y);
		
		_myZoomControl = new CCZoomControl();
		_myZoomControl.setRange(0, 2);
		_myZoomControl.addZoomable(this);
		
		_myLowerBound = 0;
		_myUpperBound = 1;
		_myGridInterval = 0.1f;
		_myPointSize = 5;
		_myCurrentPosition = 24;
		_mySelection = null;
		_myCurveType = CCTimelineCurveType.LINEAR;
		
		_myEnvelopeControl = new CCUIEnvelopeControl(_myNumberOfEnvelopes);
	}
	
	public CCUIEnvelopeControl envelopeControls() {
		return _myEnvelopeControl;
	}
	
	public CCTimedData currentData() {
		return value()[_myEnvelopeIndex];
	}

	public void clearSelection() {
		_mySelection = null;
	}

	public CCTimedDataPoint curveToViewSpace(CCVector2f thePoint) {
		CCTimedDataPoint myResult = new CCTimedDataPoint();
		int myX = curveXToViewX(thePoint.x);
		int myY = (int) ((1 - thePoint.y) * _myDimension.y); // reverse y axis
		myResult.set(myX, myY);
		return myResult;
	}

	public int curveXToViewX(float theCurveX) {
		return (int)CCMath.map(theCurveX, _myLowerBound, _myUpperBound, 0, _myDimension.x);
	}

	public void dragPoint(CCVector2f theViewCoords) {
		// clamp to visible height

		float myY = theViewCoords.y;
		if (myY <= 0) {
			myY = 0;
		} else if (myY >= _myDimension.y) {
			myY = _myDimension.y;
		}

		CCVector2f myTargetPosition = new CCVector2f(theViewCoords.x, myY);

		if (_myDraggedPoint != null) {
			currentData().move(_myDraggedPoint, snapToGrid(viewToCurveSpace(myTargetPosition)));
		}
	}

	private void drawCurve(CCGraphics g) {
		CCTimedData myData = currentData();
		
		if (myData.size() == 0) {
			return;
		}

		CCTimedDataPoint myLeftLimit = new CCTimedDataPoint(_myLowerBound, myData.value(_myLowerBound));
		CCTimedDataPoint myRightLimit = new CCTimedDataPoint(_myUpperBound, myData.value(_myUpperBound));

		ArrayList<CCTimedDataPoint> myRange = myData.rangeList(_myLowerBound, _myUpperBound);
		if (myRange.size() == 0) {
			drawCurvePiece(g, myData, myLeftLimit, myRightLimit);
			return;
		}

		Iterator<CCTimedDataPoint> it = myRange.iterator();

		CCTimedDataPoint p1 = it.next();
		CCTimedDataPoint p2 = null;
		drawCurvePiece(g, myData, myLeftLimit, p1);

		while (it.hasNext()) {
			p2 = it.next();
			drawCurvePiece(g, myData, p1, p2);
			p1 = p2;
		}

		drawCurvePiece(g, myData, p1, myRightLimit);

	}

	private void drawCurvePiece(CCGraphics g, CCTimedData theData,CCVector2f myFirstPoint, CCVector2f mySecondPoint) {

		if (myFirstPoint.equals(mySecondPoint)) {
			return;
		}

		// GeneralPath myLinePath = new GeneralPath();
		// GeneralPath myFillPath = new GeneralPath();
		// myFillPath.moveTo(curveXToViewX(myFirstPoint.x), _myDimension.y);

		if (mySecondPoint == null) {
			mySecondPoint = new CCTimedDataPoint(_myUpperBound, myFirstPoint.y);
		}

		float myInterval = _myGridInterval / _myDimension.x * (_myUpperBound - _myLowerBound);
		float myStart = myInterval * CCMath.floor(myFirstPoint.x / myInterval);

		g.color(LINE_COLOR);
		g.beginShape(CCDrawMode.LINE_STRIP);
		CCVector2f p = curveToViewSpace(myFirstPoint);
		g.vertex(p);
		// myFillPath.lineTo(p.x, p.y);

		for (float step = myStart + myInterval; step < mySecondPoint.x; step = step + myInterval) {
			float myValue = theData.value(step);
			p = curveToViewSpace(new CCVector2f(step, myValue));
			g.vertex(p);
			// myFillPath.lineTo(p.x, p.y);
		}
		p = curveToViewSpace(mySecondPoint);
		g.vertex(p);
		g.endShape();
		// myFillPath.lineTo(p.x, p.y);
		// myFillPath.lineTo(p.x, _myDimension.y);
		// myFillPath.closePath();

		// g2.setColor(FILL_COLOR);
		// g2.fill(myFillPath);

		// g2.draw(myLinePath);

	}

	private void drawGridLines(CCGraphics g) {
		float myNumberOfLines = (_myUpperBound - _myLowerBound) / _myGridInterval;
		int myIntervalFactor = 1;
		if (myNumberOfLines > MAX_GRID_LINES) {
			myIntervalFactor = (int) (myNumberOfLines / MAX_GRID_LINES + 1);
		}
		float myStart = _myGridInterval * (CCMath.floor(_myLowerBound / _myGridInterval));
		g.color(255,50);
		g.beginShape(CCDrawMode.LINES);
		for (float step = myStart; step <= _myUpperBound; step = step + myIntervalFactor * _myGridInterval) {
			float myX = (step - _myLowerBound) / (_myUpperBound - _myLowerBound) * _myDimension.x;
			if(myX < 0)continue;
			g.vertex((int) myX, 0);
			g.vertex((int) myX, _myDimension.y);
		}
		g.endShape();
	}

	public void editPoint(CCVector2f theViewCoords) {
		
		CCTimedData myData = currentData();

		CCTimedDataPoint myCurveCoords = viewToCurveSpace(theViewCoords);
		CCTimedDataPoint myControlPoint = myData.getNearestPoint(myCurveCoords, getPickRange());

		if (myControlPoint == null) {
			// add a new point with the current curve type
			myCurveCoords.setType(_myCurveType);
			myData.add(myCurveCoords);
		} else {
			CCTimedDataPoint myPoint = curveToViewSpace(myControlPoint);
			if (myPoint.distance(theViewCoords) < PICK_RADIUS) {
				myData.remove(myControlPoint);
			} else {
				myCurveCoords.setType(_myCurveType);
				myData.add(myCurveCoords);
			}
		}
	}

	public void endDrag() {
		_myDraggedPoint = null;
	}

	public CCTimelineCurveType getCurveType() {
		return _myCurveType;
	}

	public CCTimedData getModel() {
		return currentData();
	}

	private float getPickRange() {
		return PICK_RADIUS / _myDimension.x * (_myUpperBound - _myLowerBound);
	}

	public CCTimelineSelection getSelection() {
		return _mySelection;
	}

	public float getTime() {
		return _myCurrentPosition;
	}

	public boolean isDragging() {
		return _myDraggedPoint != null;
	}
	
	@Override
	protected void onPress(final CCMouseEvent theEvent){
		CCLog.info("ON PRESS");
		CCVector2f myLocalPosition = theEvent.position().clone().subtract(_myPosition);
		if (theEvent.button() == CCMouseButton.RIGHT) {
			editPoint(myLocalPosition);
		} else  if (theEvent.button() == CCMouseButton.LEFT) {
			if(theEvent.isAltDown()) {
				_myZoomControl.startDrag(myLocalPosition);
			}else {
				startDrag(myLocalPosition);
			}
			
		}
	}
	
	@Override
	protected void onRelease(final CCMouseEvent theEvent){
		if (theEvent.button() == CCMouseButton.LEFT) {
			endDrag();
			_myZoomControl.endDrag();
		}
	}
	
	@Override
	protected void onReleaseOutside(final CCMouseEvent theEvent){
		if (theEvent.button() == CCMouseButton.LEFT) {
			endDrag();
			_myZoomControl.endDrag();
		}
	}
	
	@Override
	protected void onDragg(final CCMouseEvent theEvent){
		CCVector2f myLocalPosition = theEvent.position().clone().subtract(_myPosition);
		if (!theEvent.isAltDown()) {
			dragPoint(myLocalPosition);
		}else {
			_myZoomControl.performDrag(myLocalPosition, _myDimension.x) ;
		}
	}
	
	@Override
	public void onChange() {
		super.onChange();
		CCTimedDataPoint myLast = currentData().getLastPoint();
		
		_myZoomControl.setRange(0, myLast == null ? 2 : myLast.x);
	}
	
	@Override
	public void createPreset(){
		_myValues.add(new CCTimedData[_myNumberOfEnvelopes]);
		_myPreset = _myValues.size() - 1;
		onChange();
	}

	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.pushAttribute();
		
		g.translate(_myPosition);

		// paint background
		g.color(0,100);
		g.polygonMode(CCPolygonMode.FILL);
		g.rect(0, 0, _myDimension.x, _myDimension.y);
		g.color(255,50);
		g.polygonMode(CCPolygonMode.LINE);
		g.rect(0, 0, _myDimension.x, _myDimension.y);

		// paint grid lines
		g.strokeWeight(0.5f);
		drawGridLines(g);

		// paint ruler
		// drawRuler(g2d);

		// paint curve
		g.strokeWeight(0.5f);
		drawCurve(g);

		// paint curve points
//		g.strokeWeight(0.5f);
		g.color(DOT_COLOR);
		g.pointSize(_myPointSize);
		CCTimedData myData = currentData();
		CCTimedDataPoint myCurrentPoint = myData.getFirstPointAt(_myLowerBound);
		g.beginShape(CCDrawMode.POINTS);
		while (myCurrentPoint != null) {
			if (myCurrentPoint.x > _myUpperBound) {
				break;
			}
			CCVector2f myUserPoint = curveToViewSpace(myCurrentPoint);

			g.vertex((int) myUserPoint.x, (int) myUserPoint.y);
			myCurrentPoint = myCurrentPoint.getNext();
		}
		g.endShape();

//		// paint selection
//		if (_mySelection != null) {
//			_mySelection.draw(g);
//		}
		
		int myViewX = curveXToViewX(myData.time());
		
		if(myViewX < _myDimension.x) {
			g.color(0.9f, 0.5f);
			g.line(myViewX, 0, myViewX, _myDimension.y);
		}
		
		g.color(_myUIColor.colorLabel);
		_myLabel.draw(g);
		_myValueText.text(_myZoomControl.getLowerBound()+":"+_myZoomControl.getUpperBound());
		_myValueText.draw(g);
		
		g.color(_myUIColor.colorValue);

		// updateUI();
		g.popAttribute();
		g.popMatrix();
	}

	// public void paintComponent(Graphics g) {
	//		
	// super.paintComponent(g);
	//		
	// g.drawImage(_myRenderBuffer, 0, 0, null);
	//		
	// int myViewX = curveXToViewX(_myCurrentPosition);
	// g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
	// g.drawLine(myViewX, 0, myViewX, _myDimension.y);
	//		
	// DecimalFormat myFormat = new DecimalFormat();
	// myFormat.applyPattern("#0.00");
	// float myValue = getModel().getValue(_myCurrentPosition);
	// float myScaledValue = myValue * (_myMaxValue - _myMinValue) + _myMinValue;
	// g.drawString(myFormat.format(myScaledValue), myViewX + 10, (int)curveToViewSpace(new
	// ControlPoint(_myCurrentPosition, myValue)).y);
	//		
	// }

	public CCVector2f pickNearestPoint(CCVector2f theViewCoords) {
		CCTimedDataPoint myNearest = currentData().getNearestPoint(viewToCurveSpace(theViewCoords), getPickRange());
		if (myNearest == null) {
			return null;
		} else {
			return curveToViewSpace(myNearest);
		}
	}

	public void setCurveType(CCTimelineCurveType theType) {
		_myCurveType = theType;
	}

	public void setModel(CCTimedData theModel) {
		value()[_myEnvelopeIndex] = (theModel);
	}

	public void setRange(float theLowerBound, float theUpperBound) {

		if (theLowerBound > theUpperBound) {
			float tmp = theLowerBound;
			theLowerBound = theUpperBound;
			theUpperBound = tmp;
		}
		_myLowerBound = theLowerBound;
		_myUpperBound = theUpperBound;
	}

	public void setSelection(CCTimelineSelection theSelection) {
		_mySelection = theSelection;
	}

	public void update(float thePosition) {
		_myCurrentPosition = thePosition;
		// updateUI();
	}

	public CCTimedDataPoint snapToGrid(CCTimedDataPoint thePoint) {
		float myX = Math.round(thePoint.x / _myGridInterval) * _myGridInterval;
		thePoint.set(myX, thePoint.y);
		return thePoint;
	}

	public void startDrag(CCVector2f theViewCoords) {
		if (currentData().isEmpty()) {
			return;
		}
		CCTimedDataPoint myNearest = currentData().getNearestPoint(viewToCurveSpace(theViewCoords), getPickRange());

		if (myNearest != null && curveToViewSpace(myNearest).distance(theViewCoords) < PICK_RADIUS) {
			_myDraggedPoint = myNearest;
		}
	}

	public CCTimedDataPoint viewToCurveSpace(CCVector2f thePoint) {
		CCTimedDataPoint myResult = new CCTimedDataPoint();
		float myX = viewXToCurveX((int) thePoint.x);
		float myY = 1 - thePoint.y / _myDimension.y; // reverse y axis
		myResult.set(myX, myY);
		return myResult;
	}

	public float viewXToCurveX(int theViewX) {
		return (float) theViewX / (float) _myDimension.x * (_myUpperBound - _myLowerBound) + _myLowerBound;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.ui.CCUIValueElement#relativeValue(float)
	 */
	@Override
	public CCTimedData[] relativeValue(float theValue) {
		return null;
	}

	
	
	static final String TIMELINE_NODE = "Timeline";
	static final String POINTLIST_NODE = "PointList";
	static final String TRACK_NODE = "Track";
	static final String MUTE_ATTRIB = "mute";
	static final String CONTROLPOINT_NODE = "ControlPoint";
	static final String X_ATTRIB = "x";
	static final String Y_ATTRIB = "y";
	static final String TYPE_ATTRIB = "type";
	static final String ADDRESS_ATTRIB = "address";
	static final String PLAYBACK_SPEED_ATTRIB = "speed";
	static final String LOWER_BOUND_ATTRIB = "lower_bound";
	static final String UPPER_BOUND_ATTRIB = "upper_bound";
	
	@Override
	public CCXMLElement xmlForValue(CCTimedData[] theValue) {
		CCXMLElement myResult = new CCXMLElement("preset");
		if(theValue == null)myResult.addAttribute("value", null);
		else {
			for(int i = 0; i < theValue.length;i++) {
				CCXMLElement myEnvelope = new CCXMLElement("envelope");
				ArrayList<CCTimedDataPoint> myPoints = theValue[i].rangeList(0);
				Iterator<CCTimedDataPoint> it = myPoints.iterator();
				while (it.hasNext()) {
					CCTimedDataPoint myPoint = it.next();
					CCXMLElement myNode = new CCXMLElement(CONTROLPOINT_NODE);
					
					myNode.addAttribute(X_ATTRIB, "" + myPoint.x);
					myNode.addAttribute(Y_ATTRIB, "" + myPoint.y);
					
					switch (myPoint.getType()) {
					case CUBIC:
						myNode.addAttribute(TYPE_ATTRIB, "cubic");
						break;
					case LINEAR:
					default:
						myNode.addAttribute(TYPE_ATTRIB, "linear");
						break;
					}
					myEnvelope.addChild(myNode);
				}
				myResult.addChild(myEnvelope);
			}
		}
		return myResult;
	}
	
	@Override
	public CCTimedData[] valueForXML(CCXMLElement theXMLElement) {
		CCTimedData[] myControlPointSets = new CCTimedData[theXMLElement.countChildren()];
		int index = 0;
		for(CCXMLElement myArrayChildNode:theXMLElement) {
			CCTimedData myControlPointSet = new CCTimedData();
			for (CCXMLElement myChildNode:myArrayChildNode) {
			
				if (myChildNode.name().equals(CONTROLPOINT_NODE)) {
					float myX = myChildNode.floatAttribute(X_ATTRIB);
					float myY = myChildNode.floatAttribute(Y_ATTRIB);
					
					CCTimedDataPoint myPoint = new CCTimedDataPoint(myX, myY);
					
					String myType = myChildNode.attribute(TYPE_ATTRIB);
					if (myType.equals("cubic")) {
						myPoint.setType(CCTimelineCurveType.CUBIC);
					} else {
						myPoint.setType(CCTimelineCurveType.LINEAR);
					}
					
					myControlPointSet.add(myPoint);
				}
			}
			myControlPointSets[index++] = myControlPointSet;
		}
		return myControlPointSets;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.ui.CCUIElement#setupText()
	 */
	@Override
	public void setupText() {
		_myLabel.position(
			_myDimension.x + 3, 
			_myDimension.y - _myLabel.font().size()/2
		);
		
		_myValueText = new CCText(CCControlUI.FONT);
		_myValueText.position(
			_myDimension.x + 3, 
			_myDimension.y - _myValueText.font().size()/2 - 30
		);
			
		_myBounds.min().set(_myPosition);
		_myBounds.width(_myDimension.x+_myLabel.width() +3);
		_myBounds.height(_myDimension.y);
	}

}
