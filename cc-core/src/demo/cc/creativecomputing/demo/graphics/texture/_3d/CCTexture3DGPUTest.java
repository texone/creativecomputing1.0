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
package cc.creativecomputing.demo.graphics.texture._3d;

import com.jogamp.opengl.cg.CGparameter;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.CCFormatUtil;

public class CCTexture3DGPUTest extends CCApp {
	
	private CCTexture3D _myTexture;
	private float _myOffset = 0;
	
	private CCArcball _myArcball;
	
	private String _myFolder = "videos/crash/";
	
	private CCCGShader _myNoiseShader;
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseOffsetParameter;
	
	@CCControl(name = "noiseScale", min = 0, max = 1)
	private float _cNoiseScale = 1;

	@Override
	public void setup() {
		String[] myFiles = CCIOUtil.list(_myFolder, "png");
		_myTexture = new CCTexture3D(CCTextureIO.newTextureData(_myFolder + myFiles[0]), myFiles.length);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		
		for(int i = 1; i < myFiles.length; i++) {
			_myTexture.updateData(CCTextureIO.newTextureData(_myFolder + myFiles[i]), myFiles.length - i - 1);
		}
		
		_myNoiseShader = new CCCGShader(
			null,
			CCIOUtil.classPath(this, "simplexnoisefragment.fp")
		);
		_myNoiseScaleParameter = _myNoiseShader.fragmentParameter("noiseScale");
		_myNoiseOffsetParameter = _myNoiseShader.fragmentParameter("noiseOffset");
		_myNoiseShader.load();
			
		CCGPUNoise.attachFragmentNoise(_myNoiseShader);
			
		addControls("noise", "noise", this);
		
		_myArcball = new CCArcball(this);
		
		fixUpdateTime(1/30f);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myOffset += theDeltaTime * 0.1f;
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.translate(-320,-180);
		g.texture(_myTexture);

		_myNoiseShader.start();
		_myNoiseShader.parameter(_myNoiseScaleParameter, _cNoiseScale);
		_myNoiseShader.parameter(_myNoiseOffsetParameter,0,0,_myOffset);
		
		g.beginShape(CCDrawMode.QUADS);

		g.textureCoords(0f, 0f);
		g.vertex(0, 0);
		g.textureCoords(1f, 0f);
		g.vertex(640, 0);
		g.textureCoords(1f, 1f);
		g.vertex(640, 320);
		g.textureCoords(0f, 1f);
		g.vertex(0, 320);
		g.endShape();
		
		_myNoiseShader.end();
		
		g.noTexture();

		CCScreenCapture.capture("export/disney01/crash"+CCFormatUtil.nf(frameCount, 4)+".png", width, height);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture3DGPUTest.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}

