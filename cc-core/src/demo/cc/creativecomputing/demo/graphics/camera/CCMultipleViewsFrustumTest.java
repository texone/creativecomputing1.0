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
package cc.creativecomputing.demo.graphics.camera;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCLight;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.graphics.CCPointLight;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;

public class CCMultipleViewsFrustumTest extends CCApp {
	
	CCCamera _myCamera1;
	CCCamera _myCamera2;
	CCCamera _myCamera3;
	
	private CCLight myLight;
    private CCMaterial _myMaterial;

	@Override
	public void setup() {
		setCameras();

        // setup lighting
        g.lights();
		myLight = new CCPointLight(1f,1f,1f,0f,0f,2f);
		myLight.specular(0, 0.5f, 1);
		g.light(myLight);
		g.colorMaterial(CCColorMaterialMode.OFF);
		
		_myMaterial = new CCMaterial();
		_myMaterial.diffuse(255, 0, 0);
		_myMaterial.specular(0,75,45);
	}
	
	float _myAngle = 0;

	@Override
	public void draw() {
		g.clear();
		g.material(_myMaterial);
		
		_myCamera1.draw(g);
		g.rotateX(_myAngle);
		g.box(200,200,200);
		
		_myCamera2.draw(g);
		g.rotateX(_myAngle);
		g.box(200,200,200);
		
		_myCamera3.draw(g);
		g.rotateX(_myAngle);
		g.box(200,200,200);
		
		_myAngle+=0.1f;
	}
	
	private void setCameras(){
		_myCamera1 = new CCCamera(g);
		_myCamera1.viewport(new CCViewport(0,0,300,400));
		_myCamera1.frustumOffset().set(-300,0);
		
		_myCamera2 = new CCCamera(g);
		_myCamera2.viewport(new CCViewport(310,0,300,400));
		
		_myCamera3 = new CCCamera(g);
		_myCamera3.viewport(new CCViewport(620,0,300,400));
		_myCamera3.frustumOffset().set(300,0);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent){
		setCameras();
	}

	public static void main(String[] args) {
		final CCApplicationManager myManager = new CCApplicationManager(CCMultipleViewsFrustumTest.class);
		myManager.settings().size(920, 400);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
