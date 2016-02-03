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
package cc.creativecomputing.demo.graphics.texture.video;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.video.CCSequenceTexture;
import cc.creativecomputing.io.CCIOUtil;

public class CCSequenceTextureTest extends CCApp {
	
	private CCSequenceTexture _mySequenceTexture;
	
	@CCControl(name = "rate", min = -2, max = 2)
	private static float _cRate = 1;
	
	private String _myFolder = "videos/kaki/";

	@Override
	public void setup() {
//		frameRate(30);
		addControls("app", "app", this);
		String[] myFiles = CCIOUtil.list(_myFolder,"png");
		for(int i = 0; i < myFiles.length;i++) {
			myFiles[i] = _myFolder + myFiles[i];
		}
		
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(false);
		
		_mySequenceTexture = new CCSequenceTexture(this, CCTextureTarget.TEXTURE_2D, myAttributes, myFiles);
		_mySequenceTexture.loop();
		_mySequenceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(final float theDeltaTime) {
		_mySequenceTexture.rate(_cRate);
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
		g.texture(_mySequenceTexture);
		_mySequenceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-200, -100, -0.5f, -0.5f);
		g.vertex( 0, -100, 1.5f, -0.5f);
		g.vertex( 0,  100, 1.5f, 1.5f);
		g.vertex(-200,  100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSequenceTextureTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

