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
package cc.creativecomputing.demo.graphics.shader.tutorial;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.util.CCArcball;

public class CCShaderTutorial03 extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCTexture2D _myTexture;
	private CCVBOMesh _myMesh;
	
	private CCArcball _myArcball;

	private int _myXres = 190;
	private int _myYres = 360;
	
	@CCControl(name = "radius1", min = 100, max = 200)
	private float _cRadius1 = 100;
	
	@CCControl(name = "radius2", min = 100, max = 200)
	private float _cRadius2 = 100;
	
	@CCControl(name = "noiseScale", min = 1, max = 10)
	private float _cNoiseScale = 100;
	
	@CCControl(name = "noiseSpeed", min = 0, max = 10)
	private float _cNoiseSpeed = 100;
	
	private float _myNoiseOffset = 0;
	
	@Override
	public void setup() {
		_myShader = new CCGLSLShader(
			"demo/gpu/tutorial/tutorial03_vert.glsl",
			"demo/gpu/tutorial/tutorial03_frag.glsl"
		);
		_myShader.load();
		
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/texone.png"));
		
		List<Integer> myIndices = new ArrayList<Integer>();
		
		int counter = 0;
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myXres * _myYres);
		for(int beta = 0; beta < _myXres; beta++) {
			for(int phi = 0; phi < _myYres; phi++) {
				
				_myMesh.addVertex((float)beta / (_myXres), (float)phi / (_myYres-1), 0);

				if(beta < _myXres) {
					myIndices.add(counter);
					myIndices.add(counter + _myYres);
					myIndices.add(counter + _myYres + 1);
					myIndices.add(counter + 1);
				}
				counter++;
			}
		}
		
		_myMesh.indices(myIndices);
		
		_myArcball = new CCArcball(this);
		
		addControls("noise", "noise", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myNoiseOffset += theDeltaTime * _cNoiseSpeed;
	}

	@Override
	public void draw() {
		g.clearColor(200);
		g.clear();
		
		_myArcball.draw(g);
//		g.polygonMode(CCPolygonMode.LINE);
		g.texture(0,_myTexture);
		_myShader.start();
		_myShader.uniform1i("colorMap", 0);
		_myShader.uniform1f("radius1", _cRadius1);
		_myShader.uniform1f("radius2", _cRadius2);
		_myShader.uniform1f("noiseScale", _cNoiseScale);
		_myShader.uniform1f("noiseOffset", _myNoiseOffset);
		_myMesh.draw(g);
		_myShader.end();
		g.noTexture();
//		g.polygonMode(CCPolygonMode.FILL);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderTutorial03.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

