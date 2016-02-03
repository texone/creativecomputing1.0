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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCTexture3DTest extends CCApp {
	
	private CCTexture3D _myTexture;
	private float _myOffset = 0;
	
	private CCArcball _myArcball;
	
	private String _myFolder = "videos/crash/";

	@Override
	public void setup() {
		String[] myFiles = CCIOUtil.list(_myFolder, "png");
		_myTexture = new CCTexture3D(CCTextureIO.newTextureData(_myFolder + myFiles[0]), myFiles.length);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		
		for(int i = 1; i < myFiles.length; i++) {
			_myTexture.updateData(CCTextureIO.newTextureData(_myFolder + myFiles[i]), i);
		}
		
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
		g.translate(-320,-180);
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		for(int x = 0; x < 64;x++) {
			for(int y = 0; y < 32;y++) {
				float texX1 = x/64f;
				float texY1 = y/32f;
				
				float texX2 = (x + 1)/64f;
				float texY2 = (y + 1)/32f;
				
				g.textureCoords(texX1, texY1, CCMath.noise(texX1, texY1, _myOffset));
				g.vertex(x * 10, y * 10);
				g.textureCoords(texX2, texY1, CCMath.noise(texX2, texY1, _myOffset));
				g.vertex((x + 1) * 10, y * 10);
				g.textureCoords(texX2, texY2, CCMath.noise(texX2, texY2, _myOffset));
				g.vertex((x + 1) * 10, (y + 1) * 10);
				g.textureCoords(texX1, texY2, CCMath.noise(texX1, texY2, _myOffset));
				g.vertex(x * 10, (y + 1) * 10);
			}
		}
		g.endShape();
		g.noTexture();
		//g.image(_myTexture, -320,-180);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture3DTest.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}

