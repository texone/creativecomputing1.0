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
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.video.CCScreenCaptureData;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.util.logging.CCLog;

public class CCScreenCaptureDataTest extends CCApp {
	
	private CCScreenCaptureData _myData;
	private CCVideoTexture<CCScreenCaptureData> _myVideoTexture;

	@Override
	public void setup() {
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myData = new CCScreenCaptureData(this, 0,0,1200, 800, 60);
		_myData.grabArea().isActive(true);
		_myVideoTexture = new CCVideoTexture<CCScreenCaptureData>(_myData, CCTextureTarget.TEXTURE_2D, myAttributes);
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myVideoTexture,-width/2, -height/2);
		CCLog.info(_myData.captureRate());
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCScreenCaptureDataTest.class);
		myManager.settings().size(640, 480);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

