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
package cc.creativecomputing.demo.cv.openni;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIPlayer;
import cc.creativecomputing.util.logging.CCLog;

public class CCOpenNIPlayerDemo extends CCApp {
	
	@CCControl(name = "start", min = 0, max = 1)
	private float _cStart = 0;
	
	@CCControl(name = "end", min = 0, max = 1)
	private float _cEnd = 0;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIPlayer _myPlayer;

	@Override
	public void setup() {
		_myOpenNI = new CCOpenNI(this);
		_myPlayer = _myOpenNI.openFileRecording("SkeletonRec.oni");
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		CCLog.info("number of frames:" + _myPlayer.numberOfFrames());
		CCLog.info("format:          " + _myPlayer.format());
		CCLog.info("source:          " + _myPlayer.source());
		_myOpenNI.start();
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		CCLog.info("current frame:" + _myPlayer.frame());
		_myPlayer.loop((int)(_cStart * _myPlayer.numberOfFrames()), (int)(_cEnd * _myPlayer.numberOfFrames()));
	}

	@Override
	public void draw() {
		g.clear();
		g.image(_myDepthGenerator.texture(),-320,-240);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNIPlayerDemo.class);
		myManager.settings().size(640, 480);
		myManager.start();
	}
}

