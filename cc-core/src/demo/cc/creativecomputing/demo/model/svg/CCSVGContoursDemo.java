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
package cc.creativecomputing.demo.model.svg;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGIO;

public class CCSVGContoursDemo extends CCApp{
	
	private CCSVGDocument _myDocument;
	private List<CCLinearSpline>_myContours = new ArrayList<>();

	@Override
	public void setup(){
		_myDocument = CCSVGIO.newSVG("demo/model/svg/city.svg");
		
		_myContours = _myDocument.contours();
	}
	
	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(-width/2, -height/2);
		for(CCSpline myContour:_myContours){
			myContour.draw(g);
			
			g.pointSize(5);
			g.beginShape(CCDrawMode.POINTS);
			for(CCVector3f myVertex:myContour.points()){
				g.vertex(myVertex);
			}
			g.endShape();
		}
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSVGContoursDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}

}
