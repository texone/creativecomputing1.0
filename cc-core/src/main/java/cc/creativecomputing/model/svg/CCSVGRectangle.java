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
package cc.creativecomputing.model.svg;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGRectangle extends CCSVGElement{
	
	private CCVector2f _myCenter;
	
	private CCVector2f _myDimension;
	
	public CCSVGRectangle(CCSVGGroup theParent) {
		super(theParent);
		_myCenter = new CCVector2f();
		_myDimension = new CCVector2f();
	}
	
	public CCVector2f center(){
		return _myCenter;
	}
	
	public CCVector2f dimension(){
		return _myDimension;
	}
	
	@Override
	public List<CCLinearSpline> contours() {
		CCLinearSpline myContour = new CCLinearSpline(true);
		myContour.beginEditSpline();
		myContour.addPoint(new CCVector3f(_myCenter.x, _myCenter.y));
		myContour.addPoint(new CCVector3f(_myCenter.x + _myDimension.x, _myCenter.y));
		myContour.addPoint(new CCVector3f(_myCenter.x + _myDimension.x, _myCenter.y + _myDimension.y));
		myContour.addPoint(new CCVector3f(_myCenter.x, _myCenter.y + _myDimension.y));
		myContour.endEditSpline();
		List<CCLinearSpline> myResult = new ArrayList<>();
		myResult.add(myContour);
		return myResult;
	}


	@Override
	public void drawImplementation(CCGraphics g) {
		CCShapeMode myRectMode = g.rectMode();
		g.rect(_myCenter, _myDimension);
		g.rectMode(myRectMode);
	}
}
