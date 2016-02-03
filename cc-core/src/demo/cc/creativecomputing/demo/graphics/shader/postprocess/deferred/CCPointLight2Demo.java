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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.postprocess.CCPostProcess;
import cc.creativecomputing.graphics.shader.postprocess.deferred.CCDeferredShading;
import cc.creativecomputing.graphics.shader.postprocess.deferred.CCPointLight;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCPointLight2Demo extends CCApp {
	
	private class Box{
		private float size;
		private CCVector3f position;
		
		private Box() {
			size = CCMath.random(15, 75);
			position = CCVecMath.random(-width/2, width/2, -height/2, height/2, -width/2, width/2);
		}
		
		private Box(float theX, float theY, float theZ) {
			size = CCMath.random(15, 75);
			position = new CCVector3f(theX, theY, theZ);
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
	private CCPointLight _myLight1;
	private CCPointLight _myLight2;
	
	private CCArcball _myArcball;
	
	private List<Box> _myBoxes = new ArrayList<Box>();
	private List<CCPointLight> _myPointLights = new ArrayList<CCPointLight>();
	
	@CCControl(name = "z", min = -1000, max = 1000)
	private float _cZ = 0;
	
	@CCControl(name = "radius", min = 10, max = 1000)
	private float _cRadius = 0;

	public void setup() {
		_myPostProcess = new CCPostProcess(g,width,height);
		_myPostProcess.addEffect(_myDeferredShading = new CCDeferredShading(g));
		_myDeferredShading.add(_myLight1 = new CCPointLight());
		_myDeferredShading.add(_myLight2 = new CCPointLight());
		
		for(int x = 0; x < 30;x++){
			for(int y = 0; y < 30; y++){
				CCPointLight myLight = new CCPointLight();
				myLight.position().set(x * 100 - 1500,0,y * 100 - 3000);
				myLight.radius(600);
				_myPointLights.add(myLight);
				_myDeferredShading.add(myLight);
				
				_myBoxes.add(new Box(x * 100 - 1500, -300,y * 100 - 3000));
			}
		}
//		g.camera().far(1000);
		
//		for(int i = 0; i< 1000;i++) {
//			_myBoxes.add(new Box());
//		}
		
		_myArcball = new CCArcball(this);
		
		addControls("light", "light 1", 0, _myLight1);
		addControls("light", "light 2", 1, _myLight2);
		addControls("light", "light 3", 1, this);
	}

	public void draw() {
		for(CCPointLight myPointLight:_myPointLights){
			myPointLight.position().y = _cZ;
			myPointLight.radius(_cRadius);
		}
		
		
		g.clear();
		_myPostProcess.beginDraw();
		g.clearColor(0, 255);
		g.clear();
		_myArcball.draw(g);
		for(Box myBox:_myBoxes) {
			myBox.draw();
		}
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-10000, -300, -10000);
		g.vertex( 10000, -300, -10000);
		g.vertex( 10000, -300,  10000);
		g.vertex(-10000, -300,  10000);
		g.endShape();
		_myPostProcess.endDraw();
		
		g.pushMatrix();
		g.translate(-width/2, -height/2);
		g.image(_myDeferredShading.content(),0,0);
		g.popMatrix();
		
//		g.texture(0,_myRenderContext.texture(),1);
//		g.beginShape(CCDrawMode.QUADS);
//		g.textureCoords(0f, 0f);
//		g.vertex(-width / 2, -height / 2, 0);
//		g.textureCoords(0f, 1f);
//		g.vertex(-width / 2, height / 2, 0);
//		g.textureCoords(1f, 1f);
//		g.vertex(width / 2, height / 2, 0);
//		g.textureCoords(1f, 0f);
//		g.vertex(width / 2, -height / 2, 0);
//		g.endShape();
//		g.noTexture();
		
//		_myRenderContext.renderTexture().bindDepthTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPointLight2Demo.class);
		myManager.settings().size(1600, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
