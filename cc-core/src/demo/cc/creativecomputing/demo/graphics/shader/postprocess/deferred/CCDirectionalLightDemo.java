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
package cc.creativecomputing.demo.graphics.shader.postprocess.deferred;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.postprocess.CCPostProcess;
import cc.creativecomputing.graphics.shader.postprocess.deferred.CCDeferredShading;
import cc.creativecomputing.graphics.shader.postprocess.deferred.CCDirectionalLight;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCDirectionalLightDemo extends CCApp {
	
	private class Box{
		private float size;
		private CCVector3f position;
		
		private Box() {
			size = CCMath.random(15, 75);
			position = CCVecMath.random(-width/2, width/2, -height/2, height/2, -width/2, width/2);
		}
		
		void draw() {
			g.pushMatrix();
			g.translate(position);
			g.box(size);
			g.popMatrix();
		}
	}
	
	private CCPostProcess _myPostProcess;
	private CCDeferredShading _myDeferredShading;
	private CCDirectionalLight _myLight1;
	private CCDirectionalLight _myLight2;
	
	private CCArcball _myArcball;
	
	private List<Box> _myBoxes = new ArrayList<Box>();

	public void setup() {
		_myPostProcess = new CCPostProcess(g,width,height);
		_myPostProcess.addEffect(_myDeferredShading = new CCDeferredShading(g));
		_myDeferredShading.add(_myLight1 = new CCDirectionalLight());
		_myDeferredShading.add(_myLight2 = new CCDirectionalLight());
		
		g.camera().far(1000);
		
		for(int i = 0; i< 300;i++) {
			_myBoxes.add(new Box());
		}
		
		_myArcball = new CCArcball(this);
		
		addControls("light", "light 1", 0, _myLight1);
		addControls("light", "light 2", 1, _myLight2);
	}

	public void draw() {
		g.clear();
		
		_myPostProcess.beginDraw();
		g.clearColor(0, 255);
		g.clear();
		_myArcball.draw(g);
		for(Box myBox:_myBoxes) {
			myBox.draw();
		}
		_myPostProcess.endDraw();
		
		g.pushMatrix();
		g.translate(-width/2, -height/2);
		g.image(_myDeferredShading.content(),0,0);
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCDirectionalLightDemo.class);
		myManager.settings().size(1400, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
