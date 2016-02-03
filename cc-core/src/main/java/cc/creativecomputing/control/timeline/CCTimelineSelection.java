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

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2f;

public class CCTimelineSelection {
	
	public static final CCColor SELECTION_COLOR = new CCColor(0.8f, 0.5f, 0.5f, 0.2f);
	public static final CCColor SELECTION_BORDER_COLOR = new CCColor(0.8f, 0.2f, 0.2f, 0.6f);
	
	private CCTimedDataUI _myTimelinePanel;
	private float _myLowerBound;
	private float _myUpperBound;
	
	public CCTimelineSelection() {
		this(0, 0);
	}
	
	public CCTimelineSelection(float theLowerBound, float theUpperBound) {
		if (theLowerBound < theUpperBound) {
			_myLowerBound = theLowerBound;
			_myUpperBound = theUpperBound;
		} else {
			_myLowerBound = theUpperBound;
			_myUpperBound = theLowerBound;
		}
	}
	
	public void assignTimeline(CCTimedDataUI theTimeline) {
		if (_myTimelinePanel != null) {
			_myTimelinePanel.clearSelection();
		}
		_myTimelinePanel = theTimeline;
		_myTimelinePanel.setSelection(this);
	}

	public CCTimedDataUI getAssignedTimeline() {
		return _myTimelinePanel;
	}
	
	public void select( int theViewX1, int theViewX2 ) {
		if (_myTimelinePanel != null) {
			float myCurveX1 = _myTimelinePanel.viewXToCurveX(theViewX1);
			float myCurveX2 = _myTimelinePanel.viewXToCurveX(theViewX2);
			if (myCurveX1 <= myCurveX2) {
				_myLowerBound = myCurveX1;
				_myUpperBound = myCurveX2;
			} else {
				_myLowerBound = myCurveX2;
				_myUpperBound = myCurveX1;
			}
//			_myTimelinePanel.updateUI();
		}
	}
	
	public void selectTo( int theViewX ) {
		if (_myTimelinePanel != null) {
			float myCurveX = _myTimelinePanel.viewXToCurveX(theViewX);
			if (myCurveX < _myLowerBound) {
				//_myUpperBound = _myLowerBound;
				_myLowerBound = myCurveX;
			} else {
				_myUpperBound = myCurveX;
			}
//			_myTimelinePanel.updateUI();
		}
	}
	
	public float getLowerBound() {
		return _myLowerBound;
	}
	
	public float getUpperBound() {
		return _myUpperBound;
	}
	
	public void clear() {
		_myLowerBound = 0;
		_myUpperBound = 0;
	}
	
	public void draw(CCGraphics g) {
	
		CCVector2f myLowerCorner = _myTimelinePanel.curveToViewSpace(new CCVector2f(_myLowerBound, 1));
		CCVector2f myUpperCorner = _myTimelinePanel.curveToViewSpace(new CCVector2f((_myUpperBound-_myLowerBound)+_myLowerBound, 0));
	
		g.color(SELECTION_COLOR);
		g.rect((int)myLowerCorner.x, (int)myLowerCorner.y, (int)myUpperCorner.x-(int)myLowerCorner.x, (int)myUpperCorner.y);
		
		g.color(SELECTION_BORDER_COLOR);
		g.line((int)myLowerCorner.x, _myTimelinePanel.dimension().y, (int)myLowerCorner.x, 0);
		g.line((int)myUpperCorner.x, (int)myUpperCorner.y, (int)myUpperCorner.x, 0);
	}

}
