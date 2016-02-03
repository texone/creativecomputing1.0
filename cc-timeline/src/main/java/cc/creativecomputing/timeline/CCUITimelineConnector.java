/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
//import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.control.CCExternalController;
import cc.creativecomputing.control.CCValueControl;
import cc.creativecomputing.control.ui.CCUIButton;
//import cc.creativecomputing.control.ui.CCUIMatrix;
import cc.creativecomputing.control.ui.CCUISlider;
import cc.creativecomputing.control.ui.CCUIValueElement;
import cc.creativecomputing.timeline.model.TrackType;
import cc.creativecomputing.timeline.model.communication.TimelineConnector;
import cc.creativecomputing.timeline.view.TimelineContainer;

/**
 * @author christianriekoff
 *
 */
public class CCUITimelineConnector extends TimelineConnector implements CCExternalController {
	
	private HashMap<String, CCUIValueElement<?>> _myControlMap = new HashMap<String, CCUIValueElement<?>>();
	
	private Map<String, CCTimedEventListener> _myTimedEventListener = new HashMap<String, CCTimedEventListener>();
	public CCUITimelineConnector(CCApp theApp, TimelineContainer theTimelineContainer) {
		super(theTimelineContainer);
		theApp.ui().addExternalController(this);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.CommunicationLayer#getObjectNames()
	 */
	public Collection<String> getObjectNames() {
		List<String> myResult = new ArrayList<String>();
		myResult.addAll(_myControlMap.keySet());
		myResult.addAll(_myTimedEventListener.keySet());
		return myResult;
	}
	
//	private void sendMatrix(String theAddress, double theValue, CCUIMatrix theMatrix){
//		String[] myInfo = theAddress.substring(theAddress.lastIndexOf("/") + 1).split("_");
//		theMatrix.value().value(myInfo[0], myInfo[1], (float)theValue);
//	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.CommunicationLayer#sendValue(java.lang.String, double)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sendDoubleValue(String theAddress, double theValue) {
		CCUIValueElement<?> myElement = _myControlMap.get(theAddress);
//		if(myElement instanceof CCUIMatrix){
//			sendMatrix(theAddress, theValue, (CCUIMatrix)myElement);
//			return;
//		}
		CCUISlider<Float> mySlider = (CCUISlider<Float>)_myControlMap.get(theAddress);
		mySlider.changeValue((float)theValue);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.communication.CommunicationLayer#sendIntegerValue(java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sendIntegerValue(String theAddress, int theValue) {
		CCUISlider<Integer> mySlider = (CCUISlider<Integer>)_myControlMap.get(theAddress);
		mySlider.changeValue(theValue);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.communication.CommunicationLayer#sendBooleanValue(java.lang.String, boolean)
	 */
	@Override
	public void sendBooleanValue(String theAddress, boolean theValue) {
		CCUIButton myElement = (CCUIButton)_myControlMap.get(theAddress);
		myElement.changeValue(theValue);
	}
	
	@Override
	public void sendStringValue(String theAddress, String theValue) {
		
	}
	
	public double currentValue(final String theAddress) {
		CCUIValueElement<?> myElement = _myControlMap.get(theAddress);
		if(myElement instanceof CCUIButton) {
			CCUIButton myButton = (CCUIButton)myElement;
			if(myButton.value()) return 1;
			else return 0;
		}
		if(myElement instanceof CCUISlider<?>) {
			CCUISlider<?> mySlider = (CCUISlider<?>)myElement;
			return mySlider.value().doubleValue();
		}
		return 0;
	}
	
//	private void addMatrixControl(
//		String theTabName, 
//		String theObjectID,
//		CCUIMatrix theMatrix
//	){
//		CCControlMatrix myMatrix = theMatrix.value();
//		for(String myColumn:myMatrix.columns()){
//			for(String myRow:myMatrix.rows()){
//				String myKey = "/" + theTabName + "/" + theObjectID + "/" + theMatrix.label() + "/" + myColumn + "_" + myRow;
//				_myControlMap.put(myKey, theMatrix);
//				_myTimelineContainer.addCurveTrack("/" + theTabName + "/" + theObjectID + "/" + theMatrix.label(), myColumn+"_"+myRow, TrackType.DOUBLE, theMatrix.min(),theMatrix.max());
//			}
//		}
//	}
	
	

//	/* (non-Javadoc)
//	 * @see cc.creativecomputing.control.CCUIExternalController#addControl(java.lang.String, java.lang.String, cc.creativecomputing.control.controls.CCUIValueControl)
//	 */
//	@Override
//	public <ValueType> void addControl(
//		String theTabName, 
//		String theObjectID,
//		CCUIValueElement<ValueType> theElement
//	) {
////		CCLog.info(theObjectID+ "  " + theElement.getClass().getName() + ":" + (theElement instanceof CCUIMatrix));
//		if(theElement instanceof CCUIMatrix){
//			
//			addMatrixControl(theTabName,theObjectID,(CCUIMatrix)theElement);
//			return;
//		}
//		
//		String myKey = "/" + theTabName + "/" + theObjectID + "/" + theElement.label();
//		_myControlMap.put(myKey, theElement);
//			
//		TrackType myTrackType = TrackType.DOUBLE;
//		
//		float myMin = 0;
//		float myMax = 1;
//			
//		if(theElement instanceof CCUISlider<?>) {
//			CCUISlider<?> mySlider  = (CCUISlider<?>)theElement;
//			if(mySlider.isInteger()){
//				myTrackType = TrackType.INTEGER;
//			}else{
//				myTrackType = TrackType.DOUBLE;
//			}
//			myMin = mySlider.min();
//			myMax = mySlider.max();
//		}
//		if(theElement instanceof CCUIButton) {
//			myTrackType = TrackType.BOOLEAN;
//		}
//		
//		
//		
//		_myTimelineContainer.addCurveTrack("/" + theTabName + "/" + theObjectID, theElement.label(), myTrackType, myMin, myMax);
//	}

	@Override
	public <ValueType> void addControl(String theTabName, String theObjectID, CCValueControl<ValueType> theControl) {
		String myKey = "/" + theTabName + "/" + theObjectID + "/" + theControl.element().label();
		_myControlMap.put(myKey, theControl.element());
			
		TrackType myTrackType = TrackType.DOUBLE;
		
		float myMin = 0;
		float myMax = 1;
			
		if(theControl.element() instanceof CCUISlider<?>) {
			CCUISlider<?> mySlider  = (CCUISlider<?>)theControl.element();
			if(theControl.value().getClass() == Integer.class){
				myTrackType = TrackType.INTEGER;
			}else{
				myTrackType = TrackType.DOUBLE;
			}
			myMin = theControl.min();
			myMax = theControl.max();
		}
		if(theControl.element() instanceof CCUIButton) {
			myTrackType = TrackType.BOOLEAN;
		}
		
		
		
		_myTimelineContainer.addCurveTrack("/" + theTabName + "/" + theObjectID, theControl.element().label(), myTrackType, myMin, myMax);
	}
}
