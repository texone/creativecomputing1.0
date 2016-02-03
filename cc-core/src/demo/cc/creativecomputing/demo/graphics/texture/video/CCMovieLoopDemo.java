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
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;

public class CCMovieLoopDemo extends CCApp {
	
	private CCGStreamerMovie _myMovie;
	private CCVideoTexture<?> _myTexture;

	@Override
	public void setup() {
//		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/120116_spline2_fine2_1356x136_jpg.mov"));
		_myMovie = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/station.mov"));
		_myMovie.loop();
		_myMovie.time(20);
		
		_myTexture = new CCVideoTexture<CCGStreamerMovie>(_myMovie);
		g.clearColor(1f);
		g.clear();
	}

	@Override
	public void draw() {
		g.color(255,30);
		g.image(_myTexture,mouseX - width/2, height/2 - mouseY);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMovieLoopDemo.class);
		myManager.settings().size(320 * 2, 212 * 2);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

