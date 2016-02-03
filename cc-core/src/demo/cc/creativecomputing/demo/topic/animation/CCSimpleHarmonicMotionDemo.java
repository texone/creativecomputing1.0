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
package cc.creativecomputing.demo.topic.animation;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.CCMath;

/**
 * based on "Pendulum Waves" http://j.mp/lxZfTY including all numbers (0.85Hz for slowest pendulum, 1.083Hz for fastest)
 * if the above shortened link dies, try:
 * http://sciencedemonstrations.fas.harvard.edu/icb/icb.do?keyword=k16940&pageid=icb
 * .page80863&pageContentId=icb.pagecontent341734
 * &state=maximize&view=view.do&viewParam_name=indepth.html#a_icb_pagecontent341734
 * 
 * based on processing sketch by Memo Akten, May 2011. http://www.memo.tv
 * http://openprocessing.org/visuals/?visualID=28555
 * 
 * @author christianriekoff
 * 
 */
public class CCSimpleHarmonicMotionDemo extends CCApp {
	
	private class CCPendulum {
		// physical params
		private int _myIndex;

		// physical vars
		private float _myPosition; // position of pendulum (-1...1)
		private float _myX;

		// contructor
		private CCPendulum(int theIndex) {
			_myIndex = theIndex;
			_myX = CCMath.map(theIndex, 0, _myNumberOfPendulums - 1, _myPendulumSize / 2, width - _myPendulumSize / 2);
			_myPosition = 1;
		}

		// update position and trigger sound if nesescary
		private void update(float t) {
			
			int myIndex = CCMath.abs(_cStartIndex - _myIndex);
			float myFreq = (_cStartFrequency + myIndex) / 60.0f;
			_myPosition = CCMath.cos(myFreq * t * 2 * CCMath.PI); // calculate new position
		}

		// draw
		private void draw(float size) {
			g.color(255);
			g.ellipse(_myX, CCMath.map(_myPosition, -1, 1, size / 2, height - size / 2), size, size);
		}
	}

	private final static int _myNumberOfPendulums = 38;
	private List<CCPendulum> _myPendulums;

	@CCControl(name = "time", min = 0, max = 10)
	private float _cTime = 0;
	
	@CCControl(name = "start frequency", min = 0, max = 100)
	private float _cStartFrequency = 0;
	
	@CCControl(name = "start index", min = 0, max = _myNumberOfPendulums)
	private int _cStartIndex = 0;

	// size to draw pendulum
	private float _myPendulumSize;

	// -------------------------------------
	public void setup() {

		// create list of pendulums
		_myPendulums = new ArrayList<CCPendulum>();
		_myPendulumSize = width * 1.0f / _myNumberOfPendulums;

		// loop through and init each pendulum
		for (int c = 0; c < _myNumberOfPendulums; c++) {
			_myPendulums.add(new CCPendulum(c));
		}

		addControls("app", "app", this);
	}

	float _myTime = 0;

	@Override
	public void update(float theDeltaTime) {
		_myTime += theDeltaTime * _cTime;
		for (CCPendulum myPendulum : _myPendulums) {
			myPendulum.update(_myTime);
		}
	}

	@Override
	public void draw() {
		g.clear();

		g.translate(-width / 2, -height / 2);
		for (CCPendulum myPendulum : _myPendulums) {
			myPendulum.draw(_myPendulumSize);
		}
	}

	@Override
	public void keyPressed(CCKeyEvent theEvent) {
		// _myTime = 0;
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSimpleHarmonicMotionDemo.class);
		myManager.settings().size(1400, 250);
		myManager.settings().vsync(true);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
