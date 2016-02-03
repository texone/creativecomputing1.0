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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUTextureNoise;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.math.util.CCArcball;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUTextureNoiseVertexTest extends CCApp {
	
	private static class CCDisplacementShader extends CCCGShader{
		
		private CGparameter _myNoiseScaleParameter;
		private CGparameter _myNoiseOffsetParameter;
		

		/**
		 * @param theG
		 * @param theVertexShaderFile
		 * @param theFragmentShaderFile
		 */
		public CCDisplacementShader(CCGraphics theG) {
			super(
				"demo/gpu/util/heightmap.vp",
				"demo/gpu/util/heightmap.fp"
			);
			
			_myNoiseScaleParameter = vertexParameter("noiseScale");
			_myNoiseOffsetParameter = vertexParameter("noiseOffset");
			load();
			
			CCGPUTextureNoise.attachVertexNoise(this);
		}
		
		public void noiseScale(final float theNoiseScale) {
			parameter(_myNoiseScaleParameter, theNoiseScale);
		}
		
		public void noiseOffset(final float theNoiseX, final float theNoiseY) {
			parameter(_myNoiseOffsetParameter, theNoiseX, theNoiseY);
		}
	}
	
	private CCDisplacementShader _myDisplacementShader;
	private CCTexture2D _myDisplacementTexture;
	private CCArcball _myArcBall;
	private CCVBOMesh _myMesh;
	private int tesselation = 1000;
	
	@CCControl (name = "noise scale", min = 0, max = 2)
	private float _cNoiseScale = 1;

	@Override
	public void setup() {
		_myDisplacementShader = new CCDisplacementShader(g);
		_myDisplacementTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/util/noise2.png"));
		_myDisplacementTexture.wrap(CCTextureWrap.REPEAT);
		
		_myArcBall = new CCArcball(this);
		_myMesh = buildGrid(tesselation, tesselation, -800.0f, -800.0f, 0.0f, 1600.0f, 0.0f, 0.0f, 0.0f, 1600.0f, 0.0f);
		
		addControls("noise", "noise", this);
	}
	

	// draw a subdivided quad mesh
	CCVBOMesh buildGrid(
		int rows, int cols, 
		float sx, float sy, float sz, 
		float ux, float uy, float uz, 
		float vx, float vy, float vz
	) {
		final CCVBOMesh myMesh = new CCVBOMesh(CCDrawMode.LINES, rows * (cols +1) * 2);

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x <= cols; x++) {
				float u = x / (float) cols;
				float v = y / (float) rows;
				float v2 = (y + 1) / (float) rows;
				myMesh.addTextureCoords(u, v);
				myMesh.addVertex(sx + (u * ux) + (v * vx), sy + (u * uy) + (v * vy), sz + (u * uz) + (v * vz));
				myMesh.addTextureCoords(u, v2);
				myMesh.addVertex(sx + (u * ux) + (v2 * vx), sy + (u * uy) + (v2 * vy), sz + (u * uz) + (v2 * vz));
			}
		}
		
		return myMesh;
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 0.01f;
		_myDisplacementShader.noiseScale(_cNoiseScale);
		_myDisplacementShader.noiseOffset(_myTime, 0);
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.color(255);
		g.texture(0,_myDisplacementTexture);
		_myArcBall.draw(g);
		_myDisplacementShader.start();
		_myMesh.draw(g);
		_myDisplacementShader.end();
		g.noTexture();
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUTextureNoiseVertexTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(32);
		myManager.start();
	}
}

