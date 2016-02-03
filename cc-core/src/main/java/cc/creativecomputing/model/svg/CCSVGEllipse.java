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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGEllipse extends CCSVGElement{
	
	private CCVector2f _myCenter;
	private CCVector2f _myRadius;

	public CCSVGEllipse(CCSVGGroup theParent) {
		super(theParent);
		
		_myCenter = new CCVector2f();
		_myRadius = new CCVector2f();
	}
	
	public CCVector2f center(){
		return _myCenter;
	}
	
	public CCVector2f radius(){
		return _myRadius;
	}
	
	@Override
	public List<CCLinearSpline> contours() {
		CCLinearSpline myContour = new CCLinearSpline(true);
		myContour.beginEditSpline();
		for(int i = 0; i <= 360;i++){
			float myX = CCMath.cos(CCMath.radians(i)) * _myRadius.x + _myCenter.x;
			float myY = CCMath.sin(CCMath.radians(i)) * _myRadius.y + _myCenter.y;
			myContour.addPoint(new CCVector3f(myX, myY,0));
		}
		myContour.endEditSpline();
		List<CCLinearSpline> myResult = new ArrayList<>();
		myResult.add(myContour);
		myContour.endEditSpline();
		return myResult;
	}

	@Override
	public void drawImplementation(CCGraphics g) {
		CCShapeMode myEllipseMode = g.ellipseMode();
		g.ellipse(_myCenter, _myRadius.x, _myRadius.y);
		g.ellipseMode(myEllipseMode);
	}
}
