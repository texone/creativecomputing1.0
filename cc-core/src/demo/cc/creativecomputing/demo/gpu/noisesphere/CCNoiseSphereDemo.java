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
package cc.creativecomputing.demo.gpu.noisesphere;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCPointLight;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCNoiseSphereDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	
	private int _myXres = 300;
	private int _myYres = 300;
	private CCMesh _myMesh;
	
	private CCPointLight _myPointLight;
	private CCMaterial _myMaterial;
	
	private CCArcball _myArcBall;
	
	@CCControl(name = "base radius", min = 0, max = 1)
	private float _cBaseRadius = 1;
	
	@CCControl(name = "noise scale", min = 0.01f, max = 8)
	private float _cNoiseScale = 1;
	
	@CCControl(name = "sharpness", min = 0.1f, max = 5)
	private float _cSharpness = 1;
	
	@CCControl(name = "displacement", min = 0, max = 2)
	private float _cDisplacement = 1;
	
	@CCControl(name = "speed", min = 0.01f, max = 1)
	private float _cSpeed = 1;
	
	private static class LightControl{
		
		@CCControl (name = "diffuse red", min = 0, max = 1)
		private float diffuseRed = 0;
		@CCControl (name = "diffuse green", min = 0, max = 1)
		private float diffuseGreen = 0;
		@CCControl (name = "diffuse blue", min = 0, max = 1)
		private float diffuseBlue = 0;
		
		@CCControl (name = "specular red", min = 0, max = 1)
		private float specularRed = 0;
		@CCControl (name = "specular green", min = 0, max = 1)
		private float specularGreen = 0;
		@CCControl (name = "specular blue", min = 0, max = 1)
		private float specularBlue = 0;
		
		@CCControl (name = "constant fall off", min = 0, max = 1)
		private float constantFallOff = 1;
		@CCControl (name = "linear fall off", min = 0, max = 0.001f)
		private float linearFallOff = 1;
		@CCControl (name = "quadratic fall off", min = 0, max = 0.001f)
		private float quadraticFallOff = 1;
	}
	
	private static class MaterialControl{

		@CCControl (name = "ambient red", min = 0, max = 1)
		private float ambientRed = 0;
		@CCControl (name = "ambient green", min = 0, max = 1)
		private float ambientGreen = 0;
		@CCControl (name = "ambient blue", min = 0, max = 1)
		private float ambientBlue = 0;
		
		@CCControl (name = "diffuse red", min = 0, max = 1)
		private float diffuseRed = 0;
		@CCControl (name = "diffuse green", min = 0, max = 1)
		private float diffuseGreen = 0;
		@CCControl (name = "diffuse blue", min = 0, max = 1)
		private float diffuseBlue = 0;
		
		@CCControl (name = "specular red", min = 0, max = 1)
		private float specularRed = 0;
		@CCControl (name = "specular green", min = 0, max = 1)
		private float specularGreen = 0;
		@CCControl (name = "specular blue", min = 0, max = 1)
		private float specularBlue = 0;
		
		@CCControl (name = "shininess", min = 0, max = 1)
		private float shininess = 0; 
	}
	
	@CCControl(name = "light control")
	private LightControl _cLightControls = new LightControl();
	
	@CCControl(name = "material control")
	private MaterialControl _cMaterialControls = new MaterialControl();
	

	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath(this,"perlin_vert.glsl"), 
			CCIOUtil.classPath(this,"perlin_frag.glsl")
		);
//		_myShader.uniform("permTexture", 0);
		_myShader.load();
		
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myXres * _myYres);
		List<Integer> myIndices = new ArrayList<Integer>();
		for(int x = 0; x < _myXres;x++) {
			for(int y = 0; y < _myYres;y++) {
				_myMesh.addVertex(x / (_myXres - 1f), y / (_myYres - 1f), 0);
				if(x < _myXres - 1 && y < _myYres - 1) {
					myIndices.add(y + x * _myYres);
					myIndices.add(y + 1 + x * _myYres);
					myIndices.add(y + 1 + (x + 1) * _myYres);
					myIndices.add(y + (x + 1) * _myYres);
				}
				
			}
		}
		_myMesh.indices(myIndices);
		
		addControls("control", "controls", this);
		
		_myPointLight = new CCPointLight(1,0,0,-300,0,0);
		_myMaterial = new CCMaterial();
		
		_myArcBall = new CCArcball(this);
	}
	
	private float _myTimer = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTimer += theDeltaTime;
		
		_myMaterial.ambient(_cMaterialControls.ambientRed, _cMaterialControls.ambientGreen, _cMaterialControls.ambientBlue);
		_myMaterial.diffuse(_cMaterialControls.diffuseRed, _cMaterialControls.diffuseGreen, _cMaterialControls.diffuseBlue);
		_myMaterial.specular(_cMaterialControls.specularRed, _cMaterialControls.specularGreen, _cMaterialControls.specularBlue);
		_myMaterial.shininess(_cMaterialControls.shininess);
		
		_myPointLight.diffuse(_cLightControls.diffuseRed, _cLightControls.diffuseGreen, _cLightControls.diffuseBlue);
		_myPointLight.specular(_cLightControls.specularRed, _cLightControls.specularGreen, _cLightControls.specularBlue);
		_myPointLight.fallOff(_cLightControls.constantFallOff, _cLightControls.linearFallOff, _cLightControls.quadraticFallOff);
		
	}

	@Override
	public void draw() {
		g.clear();
		g.lights();
//		g.polygonMode(CCPolygonMode.LINE);
		_myPointLight.draw(g);
		g.pushMatrix();
		_myArcBall.draw(g);
		g.scale(200);
		g.material(_myMaterial);
		_myShader.start();
		_myShader.uniform1f("BaseRadius", _cBaseRadius);
		_myShader.uniform3f("NoiseScale", new CCVector3f(_cNoiseScale, _cNoiseScale, _cNoiseScale));
		_myShader.uniform1f("Sharpness",_cSharpness);
		_myShader.uniform1f("Displacement", _cDisplacement);
		_myShader.uniform1f("Speed",_cSpeed);
		_myShader.uniform1f("Timer",_myTimer);
		_myMesh.draw(g);
		_myShader.end();
		g.popMatrix();
		g.color(255);
		g.noLights();
		g.text(frameRate,-width/2 +20, -height/2 +20);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseSphereDemo.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}

