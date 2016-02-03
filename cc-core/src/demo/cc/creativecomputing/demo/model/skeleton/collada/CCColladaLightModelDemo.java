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
package cc.creativecomputing.demo.model.skeleton.collada;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeletonProvider;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.skeleton.CCSkeleton;

public class CCColladaLightModelDemo extends CCApp {
	
	@CCControl(name = "amount", min = 0, max = 1)
	private float _cAmount;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha;
	
	@CCControl(name = "light x", min = -1, max = 1)
	private float _cLightX = 0;
	
	@CCControl(name = "light y", min = -1, max = 1)
	private float _cLightY = 0;
	
	@CCControl(name = "light z", min = -1, max = 1)
	private float _cLightZ = 0;
	
	
	@CCControl(name = "specularPow", min = -1, max = 10)
	private float _cSpecularPow = 0;
	@CCControl(name = "specularBrightPow", min = -1, max = 150)
	private float _cSpecularBrightPow = 0;

	@CCControl(name = "axis length", min = 0, max = 100)
	private float _cAxisLength = 0;

	@CCControl(name = "time", min = 0, max = 2)
	private float _cTime = 0;

	private CCArcball _myArcball;

	private CCColladaSkeletonProvider _myColladaSkeletonProvider;
	private CCColladaSkeletonSkin _mySkeletonSkin;
	private CCSkeleton _mySkeleton;
	
	private CCGLSLShader _myLightShader;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		CCColladaLoader myColladaLoader = new CCColladaLoader("demo/model/collada/humanoid.dae");
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		_myColladaSkeletonProvider = new CCColladaSkeletonProvider(myColladaLoader.animations().animations(), mySkinController,myScene.node("bvh_import/Hips"));
		_mySkeleton = _myColladaSkeletonProvider.skeleton();

		_mySkeletonSkin = new CCColladaSkeletonSkin(mySkinController);
		
		_myLightShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "light_weights_vert.glsl"), 
			CCIOUtil.classPath(this, "light_weights_frag.glsl")
		);
		_myLightShader.load();
	}

	@Override
	public void update(float theDeltaTime) {
		_myColladaSkeletonProvider.time(_cTime);
	}

	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();

		_myArcball.draw(g);
		
		g.translate(0,-200,0);
		g.scale(2.0);

		g.color(125, 0, 0);
		g.strokeWeight(1);
		g.line(0, 0, 0, width, 0, 0);
		g.color(0, 125, 0);
		g.line(0, 0, 0, 0, 0, -width);
		g.color(0, 0, 125);
		g.line(0, 0, 0, 0, -height, 0);

		g.color(255);
		g.polygonMode(CCPolygonMode.FILL);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myLightShader.start();
		_myLightShader.uniform1f("amount", _cAmount);
		_myLightShader.uniform1f("alpha", _cAlpha);
		_myLightShader.uniform3f("lightDir", new CCVector3f(_cLightX, _cLightY, _cLightZ).normalize());
		_myLightShader.uniform1f("specularPow", _cSpecularPow);
		_myLightShader.uniform1f("specularBrightPow", _cSpecularBrightPow);
		_myLightShader.uniformMatrix4fv("joints", _mySkeletonSkin.skinningMatrices(_mySkeleton));
		_mySkeletonSkin.mesh().draw(g);
		_myLightShader.end();
//		_mySkeletonSkin.draw(g, _mySkeleton);
		g.clearDepthBuffer();
		g.color(255, 0, 0);

		_mySkeleton.draw(g);
		_mySkeleton.drawOrientations(g, _cAxisLength);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColladaLightModelDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}