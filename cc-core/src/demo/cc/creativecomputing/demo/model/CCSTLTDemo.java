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
package cc.creativecomputing.demo.model;


import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCLight;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.CCContent3dIO;
import cc.creativecomputing.model.CCModel;


public class CCSTLTDemo extends CCApp {

	
	private CCModel _myModel;
	private CCLight _myLight;
    private CCMaterial _myMaterial;
    
    private CCArcball _myArcball;
    
	@Override
	public void setup() {
		// frameRate(30);
		_myModel = CCContent3dIO.createModel("demo/model/r.stl");
		_myModel.center();
		_myModel.scale(100);
		_myModel.translate(new CCVector3f(-350,-350,0));
		_myModel.convert();
		
		_myArcball = new CCArcball(this);
		
		g.clearColor(0.5f);
		
		// setup lighting
        g.lights();
		_myLight = new CCDirectionalLight(1f,1f,1f,0f,2f,0f);
//		myLight.specular(0, 0.5f, 1);
		g.light(_myLight);
		_myLight = new CCDirectionalLight(1f,1f,1f,2f,-2f,0f);
//		myLight.specular(0, 0.5f, 1);
		g.light(_myLight);
//		g.colorMaterial(CCGraphics.OFF);
		g.lightModelLocalViewer(true);
		
		_myMaterial = new CCMaterial();
		_myMaterial.ambient(255, 0, 0);
		_myMaterial.diffuse(255, 0, 0);
		_myMaterial.specular(0,75,45);
	}
	
	long time = System.currentTimeMillis();

	public void draw() {
		g.polygonMode(CCPolygonMode.FILL);
		g.clear();
		g.color(255);
		g.pushMatrix();
		_myArcball.draw(g);
//		g.material(_myMaterial);
		_myModel.draw(g);

		g.popMatrix();
	}


	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCSTLTDemo.class);
		myManager.settings().size(600, 600);
		myManager.settings().antialiasing(4);
		myManager.start();
	}
}
