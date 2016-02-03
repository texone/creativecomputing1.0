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

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGElement;
import cc.creativecomputing.model.svg.CCSVGGroup;
import cc.creativecomputing.model.svg.CCSVGIO;
import cc.creativecomputing.model.svg.CCSVGPath;
import cc.creativecomputing.model.svg.CCSVGElement.CCShapeKind;

public class CCSVGDemo extends CCApp{
	
	private CCSVGDocument _myDocument;
	private List<List<CCVector2f>>_mySegments = new ArrayList<>();

	@Override
	public void setup(){
		_myDocument = CCSVGIO.newSVG("demo/model/svg/city.svg");
		
		for(CCSVGElement myElement:_myDocument){
			printGroup(myElement);
		}
	}
	
	private void printGroup(CCSVGElement theElement){

		System.out.println(theElement.kind());
		
		if(theElement.kind() == CCShapeKind.GROUP){
			CCSVGGroup myGroup = (CCSVGGroup)theElement;
			for(CCSVGElement myElement:myGroup){
				printGroup(myElement);
			}
		}

		if(theElement.kind() == CCShapeKind.PATH){
			CCSVGPath myPath = (CCSVGPath)theElement;
			PathIterator myIterator = myPath.path().getPathIterator(null, 10f);
			
			List<CCVector2f> mySegment = new ArrayList<>();
			float[] myCoords = new float[2];
			while(!myIterator.isDone()){
				int mySegmentType = myIterator.currentSegment(myCoords);// +";"+myCoords[0]+";"+myCoords[1]
				if(mySegmentType == PathIterator.SEG_MOVETO){
					mySegment = new ArrayList<>();
				}
				mySegment.add(new CCVector2f(myCoords[0],myCoords[1]));
				if(mySegmentType == PathIterator.SEG_CLOSE){
					_mySegments.add(mySegment);
				}
				
				System.out.println(mySegmentType);
//				_myVectors.add();
				myIterator.next();
			}
		}
	}
	
	@Override
	public void draw() {
		g.clearColor(255,0,0);
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(-width/2, -height/2);
		_myDocument.draw(g);
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSVGDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}

}
