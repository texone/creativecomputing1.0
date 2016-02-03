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
package cc.creativecomputing.timeline.view.swing;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.Zoomable;
import cc.creativecomputing.timeline.model.TimeRange;


/**
 * @author christianriekoff
 *
 */
public class SwingTimelineScrollbar implements Zoomable, AdjustmentListener{
	
	private TimelineController _myTimelineController;
	private JScrollBar _myScrollBar;
	
	public SwingTimelineScrollbar(TimelineController theTimelineController) {
		_myTimelineController = theTimelineController;
		
		_myScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		_myScrollBar.setMinimum(0);
		_myScrollBar.setMaximum(1000);
		_myScrollBar.addAdjustmentListener(this);
		_myTimelineController.zoomController().addZoomable(this);
	}
	
	public JScrollBar scrollbar() {
		return _myScrollBar;
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.Zoomable#setRange(double, double)
	 */
	@Override
	public void setRange(double theLowerBound, double theUpperBound) {
		double myMax = Math.max(theUpperBound, _myTimelineController.maximumTime());
		
		int myValue = (int)((theLowerBound) / myMax * 1000);
		int myExtent = (int)((theUpperBound - theLowerBound) / myMax * 1000);
		_myScrollBar.setValues(myValue, myExtent, 0, 1000);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent theE) {
		double myValue = theE.getValue() / 1000.0;
		double myLower = _myTimelineController.zoomController().lowerBound();
		double myUpper = _myTimelineController.zoomController().upperBound();
		double myMax = Math.max(myUpper, _myTimelineController.maximumTime());
		myValue *= myMax;
		double myRange = myUpper - myLower;
		_myTimelineController.zoomController().setRange(new TimeRange(myValue, myValue + myRange));
	}

}
