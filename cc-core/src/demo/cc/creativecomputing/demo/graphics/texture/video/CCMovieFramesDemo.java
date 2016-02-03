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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;

public class CCMovieFramesDemo extends CCApp {

	private CCGStreamerMovie _myData;
	private CCVideoTexture<?> _myTexture;
	
	private int _myNewFrame;
	
	private CCText _myText;
	
	@Override
	public void setup() {
		_myText = new CCText(CCFontIO.createVectorFont("arial", 24));
		
		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/120123_counter_640x64_30fps_anim.mov"));
		_myTexture = new CCVideoTexture<CCGStreamerMovie>(_myData);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.image(_myTexture, -width/2, -height/2, width, height);
		
		g.color(240, 20, 30);
		_myText.position(-width/2 + 10, -height/2 + 30);
		_myText.text(_myData.frame() + " / " + (_myData.numberOfFrames() - 1) + " / " + _myNewFrame);
		_myText.draw(g);
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_LEFT:
			if (_myNewFrame > 0)
				_myNewFrame--;
			break;
		case VK_RIGHT:
			if (_myNewFrame < _myData.numberOfFrames() * 2 - 1)
				_myNewFrame++;
			break;
		default:
		}

		_myData.frame(_myNewFrame / 2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMovieFramesDemo.class);
		myManager.settings().size(640, 64);
		myManager.start();
	}
}

