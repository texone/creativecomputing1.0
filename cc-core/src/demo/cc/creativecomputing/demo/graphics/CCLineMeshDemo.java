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
package cc.creativecomputing.demo.graphics;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCLineMeshDemo extends CCApp {
	
	
	private List<CCVector3f> _myPoints = new ArrayList<CCVector3f>();
	
	private CCArcball _myArcBArcball;
	private CCMesh _myMesh;

	public void setup() {
		for(int i = 0; i < 300000;i++){
			_myPoints.add(CCVecMath.random3f(400));
		}
		_myArcBArcball = new CCArcball(this);
		_myMesh = new CCMesh(CCDrawMode.LINE_STRIP, 300000);
		
		_myMesh.clearAll();
		for(int i = 0; i < 300;i++){
			for(int j = 0; j < 1000;j++){
				CCVector3f myVector = _myPoints.get(i * 1000 + j);
				_myMesh.addColor(0.03f, 0.04f, j / 1000f);
				_myMesh.addVertex(myVector);
			}

		}
	}

	public void draw() {
		_myArcBArcball.draw(g);
		g.clear();
		g.noDepthTest();
		g.blendMode(CCBlendMode.ADD);
		g.color(1);
		
		
		
//		for(int i = 0; i < 100;i++){
//			g.beginShape(CCDrawMode.LINE_STRIP);
//			for(int j = 0; j < 1000;j++){
//				CCVector3f myVector = _myPoints.get(i * 1000 + j);
//				g.color(0.03f, 0.04f, j / 1000f);
//				g.vertex(myVector);
//			}
//
//			g.endShape();
//		}
		
		_myMesh.draw(g);
	}

	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCLineMeshDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().vsync(true);
		myManager.start();
	}
}
