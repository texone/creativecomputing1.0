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
package cc.creativecomputing.demo.graphics.shader.noise;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;

public class CCGPUWorleyNoise2DNormal extends CCApp {
	
	private CCShaderBuffer _myWorleyTexture;
	private CCGLSLShader _myWorleyShader;
	private CCGLSLShader _myNormalShader;
	
	private class CCWorleySettings{
		
		@CCControl(name = "jitter", min = 0, max = 1)
		private float _cJitter = 0;
		
		@CCControl(name = "scale", min = 0, max = 1)
		private float _cScale = 0;
		
		@CCControl(name = "ambient range", min = 0, max = 1)
		private float _cAmbientRange = 0;
		
		@CCControl(name = "ambient range Amount", min = 0, max = 1)
		private float _cAmbientRangeAmount = 0;
	
		@CCControl(name = "ambient depth", min = 0, max = 1)
		private float _cAmbientDepth = 0;
		@CCControl(name = "ambient depth 1", min = 0, max = 1)
		private float _cAmbientDepth1 = 0;
		@CCControl(name = "ambient depth 2", min = 0, max = 1)
		private float _cAmbientDepth2 = 0;
		@CCControl(name = "ambient depth 3", min = 0, max = 1)
		private float _cAmbientDepth3 = 0;
		

		@CCControl(name = "ambient amount 1", min = 0, max = 1)
		private float _cAmbientAmount1 = 0;
		@CCControl(name = "ambient amount 2", min = 0, max = 1)
		private float _cAmbientAmount2 = 0;
		@CCControl(name = "ambient amount 3", min = 0, max = 1)
		private float _cAmbientAmount3 = 0;
		
		@CCControl(name = "amount 1", min = 0, max = 1)
		private float _cAmount1 = 0;
		@CCControl(name = "amount 2", min = 0, max = 1)
		private float _cAmount2 = 0;
		@CCControl(name = "amount 3", min = 0, max = 1)
		private float _cAmount3 = 0;
		
		@CCControl(name = "min depth", min = 0, max = 3)
		private float _cMinDepth = 0;
	
		@CCControl(name = "move z", min = 0, max = 30)
		private float _cMoveZ = 0;
		@CCControl(name = "move z 1", min = 0, max = 30)
		private float _cMoveZ1 = 0;
		@CCControl(name = "move z 2", min = 0, max = 30)
		private float _cMoveZ2 = 0;
		@CCControl(name = "move z 3", min = 0, max = 30)
		private float _cMoveZ3 = 0;
		
		@CCControl(name = "move x", min = 0, max = 30)
		private float _cMoveX = 0;
		@CCControl(name = "move y", min = 0, max = 30)
		private float _cMoveY = 0;
	}
	
	private class CCLightSettings{
		
		@CCControl(name = "depth scale", min = 0, max = 10)
		private float _cDepthScale = 0;
		
		@CCControl(name = "light x", min = -1, max = 1)
		private float _cLightX = 0;
		@CCControl(name = "light y", min = -1, max = 1)
		private float _cLightY = 0;
		@CCControl(name = "light z", min = -1, max = 1)
		private float _cLightZ = 0;
		
	
		@CCControl(name = "color depth 1", min = 0, max = 1)
		private float _cColorDepth1 = 0;
		@CCControl(name = "color depth 2", min = 0, max = 1)
		private float _cColorDepth2 = 0;
		
	
		@CCControl(name = "ambient1 r", min = 0, max = 1)
		private float _cAmbient1R = 0;
		@CCControl(name = "ambient1 g", min = 0, max = 1)
		private float _cAmbient1G = 0;
		@CCControl(name = "ambient1 b", min = 0, max = 1)
		private float _cAmbient1B = 0;;
		
	
		@CCControl(name = "ambient2 r", min = 0, max = 1)
		private float _cAmbient2R = 0;
		@CCControl(name = "ambient2 g", min = 0, max = 1)
		private float _cAmbient2G = 0;
		@CCControl(name = "ambient2 b", min = 0, max = 1)
		private float _cAmbient2B = 0;
		
	
		@CCControl(name = "diffuse1 r", min = 0, max = 1)
		private float _cDiffuse1R = 0;
		@CCControl(name = "diffuse1 g", min = 0, max = 1)
		private float _cDiffuse1G = 0;
		@CCControl(name = "diffuse1 b", min = 0, max = 1)
		private float _cDiffuse1B = 0;
		
	
		@CCControl(name = "diffuse2 r", min = 0, max = 1)
		private float _cDiffuse2R = 0;
		@CCControl(name = "diffuse2 g", min = 0, max = 1)
		private float _cDiffuse2G = 0;
		@CCControl(name = "diffuse2 b", min = 0, max = 1)
		private float _cDiffuse2B = 0;
	}
	
	int nWidth = 1200;
	int nHeight = 800;
	
	@CCControl(name = "worley", column = 0)
	private CCWorleySettings _cW = new CCWorleySettings();

	@CCControl(name = "light", column = 1)
	private CCLightSettings _cL = new CCLightSettings();
	

	@Override
	public void setup() {
		
		_myWorleyShader = new CCGLSLShader(
			new String[] {
				CCIOUtil.classPath(this, "cellular_vertex.glsl")
			},
			new String[] {
				CCIOUtil.classPath(this, "worleynoise.glsl"),
				CCIOUtil.classPath(this, "cellular2D_fragment.glsl")
			}
		);
		_myWorleyShader.load();
		
		_myNormalShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "depthmap_normal_vertex.glsl"),
			CCIOUtil.classPath(this, "depthmap_normal_fragment.glsl")
		);
		_myNormalShader.load();
		
		_myWorleyTexture = new CCShaderBuffer(nWidth, nHeight, CCTextureTarget.TEXTURE_2D);
		_myWorleyTexture.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		
		addControls("worley", "worley", this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		_myWorleyTexture.beginDraw();
		g.clear();
		_myWorleyShader.start();
		_myWorleyShader.uniform1f("jitter", _cW._cJitter);
		_myWorleyShader.uniform1f("scale", _cW._cScale);
		_myWorleyShader.uniform1f("minDepth", _cW._cMinDepth);

		_myWorleyShader.uniform1f("ambientRange", _cW._cAmbientRange);
		_myWorleyShader.uniform1f("ambientRangeAmount", _cW._cAmbientRangeAmount);
		_myWorleyShader.uniform1f("ambientDepth", _cW._cAmbientDepth);
		_myWorleyShader.uniform1f("ambientDepth1", _cW._cAmbientDepth1);
		_myWorleyShader.uniform1f("ambientDepth2", _cW._cAmbientDepth2);
		_myWorleyShader.uniform1f("ambientDepth3", _cW._cAmbientDepth3);
		
		_myWorleyShader.uniform1f("ambientAmount1", _cW._cAmbientAmount1);
		_myWorleyShader.uniform1f("ambientAmount2", _cW._cAmbientAmount2);
		_myWorleyShader.uniform1f("ambientAmount3", _cW._cAmbientAmount3);

		_myWorleyShader.uniform1f("amount1", _cW._cAmount1);
		_myWorleyShader.uniform1f("amount2", _cW._cAmount2);
		_myWorleyShader.uniform1f("amount3", _cW._cAmount3);

		_myWorleyShader.uniform1f("moveZ", _cW._cMoveZ);
		_myWorleyShader.uniform1f("moveZ1", _cW._cMoveZ1);
		_myWorleyShader.uniform1f("moveZ2", _cW._cMoveZ2);
		_myWorleyShader.uniform1f("moveZ3", _cW._cMoveZ3);
		
		_myWorleyShader.uniform3f("move", _cW._cMoveX, _cW._cMoveY, 0);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0f, 0f);
		g.vertex(0, 0);
		g.textureCoords(0, 1f, 0f);
		g.vertex(nWidth, 0);
		g.textureCoords(0, 1f, 1f);
		g.vertex(nWidth,  nHeight);
		g.textureCoords(0, 0f, 1f);
		g.vertex(0,  nHeight);
		g.endShape();
		_myWorleyShader.end();
		_myWorleyTexture.endDraw();
		
		g.clear();
		_myNormalShader.start();
		_myNormalShader.uniform1i("depthMap", 0);
		_myNormalShader.uniform1f("depthScale", _cL._cDepthScale);
		_myNormalShader.uniform3f("lightDir", new CCVector3f(_cL._cLightX, _cL._cLightY, _cL._cLightZ).normalize());
		_myNormalShader.uniform1f("colorDepth1", _cL._cColorDepth1);
		_myNormalShader.uniform1f("colorDepth2", _cL._cColorDepth2);
		_myNormalShader.uniform3f("ambient1", _cL._cAmbient1R, _cL._cAmbient1G, _cL._cAmbient1B);
		_myNormalShader.uniform3f("ambient2", _cL._cAmbient2R, _cL._cAmbient2G, _cL._cAmbient2B);
		_myNormalShader.uniform3f("diffuse1", _cL._cDiffuse1R, _cL._cDiffuse1G, _cL._cDiffuse1B);
		_myNormalShader.uniform3f("diffuse2", _cL._cDiffuse2R, _cL._cDiffuse2G, _cL._cDiffuse2B);
		g.texture(0, _myWorleyTexture.attachment(0));
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0, 0f, 0f);
		g.vertex(-width / 2, -height / 2);
		g.textureCoords(0, 1f, 0f);
		g.vertex( width / 2, -height / 2);
		g.textureCoords(0, 1f, 1f);
		g.vertex( width / 2,  height / 2);
		g.textureCoords(0, 0f, 1f);
		g.vertex(-width / 2,  height / 2);
		g.endShape();
		g.noTexture();
		_myNormalShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUWorleyNoise2DNormal.class);
		myManager.settings().size(1200, 800);
		myManager.settings().append(args);
		myManager.start();
	}
}

