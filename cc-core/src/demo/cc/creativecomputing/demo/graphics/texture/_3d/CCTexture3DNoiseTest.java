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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCTexture3DNoiseTest extends CCApp {
	
	private CCTexture3D _myTexture;
	private float _myOffset = 0;
	
	private CCArcball _myArcball;
	
	@CCControl (name = "noise amount", min = 0, max = 5)
	private float _cNoiseAmount = 1;
	
	@CCControl (name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 1;
	
	private String myFolder = "videos/crash/";

	@Override
	public void setup() {
		addControls("control", "control", this);
		_myUI.drawBackground(false);
		
		String[] myFiles = CCIOUtil.list(myFolder, "png");
		_myTexture = new CCTexture3D(CCTextureIO.newTextureData(myFolder + myFiles[0]), myFiles.length);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		for(int i = 0; i < myFiles.length; i++) {
			_myTexture.updateData(CCTextureIO.newTextureData(myFolder + myFiles[i]), i);
		}
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		_myTexture.mustFlipVertically();
		
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myOffset += theDeltaTime * 0.1f;
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.scale(2);
		g.translate(-320,-180);
		g.texture(_myTexture);
		
		float xres = 128;
		float yres = 64;
		
		float scale = 5;
		
		g.beginShape(CCDrawMode.QUADS);
		for(int x = 0; x < xres;x++) {
			for(int y = 0; y < yres;y++) {
				float texX1 = x/xres;
				float texY1 = y/yres;
				
				float texX2 = (x + 1)/xres;
				float texY2 = (y + 1)/yres;
				
				g.textureCoords(texX1, texY1, CCMath.noise(texX1 * _cNoiseScale, texY1 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount);
				g.vertex(x * scale, y * scale);
				g.textureCoords(texX2, texY1, CCMath.noise(texX2 * _cNoiseScale, texY1 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount);
				g.vertex((x + 1) * scale, y * scale);
				g.textureCoords(texX2, texY2, CCMath.noise(texX2 * _cNoiseScale, texY2 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount);
				g.vertex((x + 1) * scale, (y + 1) * scale);
				g.textureCoords(texX1, texY2, CCMath.noise(texX1 * _cNoiseScale, texY2 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount);
				g.vertex(x * scale, (y + 1) * scale);
			}
		}
		g.endShape();
		g.noTexture();
		//g.image(_myTexture, -320,-180);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture3DNoiseTest.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}

