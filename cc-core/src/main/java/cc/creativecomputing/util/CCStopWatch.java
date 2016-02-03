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
package cc.creativecomputing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;

/**
 * Use the stop watch class to measure the time of certain part of your application. The Stop watch watch makes it easy
 * to measure different processes and print out the values.
 * 
 * @author artcom
 * 
 */
public class CCStopWatch implements CCDrawListener<CCGraphics>, CCUpdateListener {

	@CCControl(name = "active", min = 1, max = 100)
	private boolean _cActive = true;

	@CCControl(name = "width", min = 0, max = 1)
	private float _cWidth = 0;

	@CCControl(name = "height", min = 0, max = 1)
	private float _cHeight = 0;

	@CCControl(name = "scale", min = 0f, max = 1000f)
	private float _cScale = 20f;

	@CCControl(name = "samples", min = 50, max = 2000)
	private int _cSamples = 200;

	@CCControl(name = "pause")
	private boolean _cPause = false;
	
	@CCControl(name = "print")
	private boolean _cPrint = false;

	private class CCStopWatchItem {

		private long _myNanoTime = 0;
		private float _myCurrentTime = 0;

		private String _myName = "undefined";
		private ArrayList<Float> _myDeltaTimeHistory = null;

		public CCStopWatchItem(String theItemName) {
			_myName = theItemName;
			_myDeltaTimeHistory = new ArrayList<Float>();
			for (int i = 0; i < _cSamples; i++) {
				_myDeltaTimeHistory.add(0f);
			}
		}

		public void step(int theClockPos) {
			_myDeltaTimeHistory.set(theClockPos, _myCurrentTime);
			_myCurrentTime = 0;
			while (_myDeltaTimeHistory.size() > _cSamples) {
				_myDeltaTimeHistory.remove(_myDeltaTimeHistory.size() - 1);
			}
		}

		public void draw(CCGraphics g) {

			// label
			CCColor myColor = _myColors[_myLastColorIdx];
			g.color(myColor);
			_myLastColorIdx++;
			_myLastColorIdx %= 8;

			float myWidth = g.width * _cWidth;

			if (_myDeltaTimeHistory.size() > 0) {
				g.text(_myName + " : " + _myDeltaTimeHistory.get(_myDeltaTimeHistory.size() - 1), 10 + myWidth, 10 + (float) (_myDeltaTimeHistory.get(0) / 2 + _myLastHeights[0]) * _cScale);
			}

			g.color(myColor.r, myColor.g, myColor.b, 0.25f);

			// draw fill
			g.beginShape(CCDrawMode.QUAD_STRIP);
			for (int i = 0; i < _myDeltaTimeHistory.size(); i++) {
				int index = (_myClockPos + i) % _cSamples;
				float myX = myWidth / _cSamples * (float) i;
				float myY = (_myDeltaTimeHistory.get(index) + _myLastHeights[index]) * _cScale;
				float myLastY = _myLastHeights[index] * _cScale;

				g.vertex(myX, myY);
				g.vertex(myX, myLastY);
			}

			g.endShape();

			// draw lines
			g.color(myColor.r / 2, myColor.g / 2, myColor.b / 2, 0.95f);

			g.lineWidth(2.0f);
			g.beginShape(CCDrawMode.LINE_STRIP);
			for (int i = 0; i < _myDeltaTimeHistory.size(); i++) {
				int index = (_myClockPos + i) % _cSamples;
				float myX = myWidth / _cSamples * (float) i;
				float myY = (_myDeltaTimeHistory.get(index) + _myLastHeights[index]) * _cScale;

				g.vertex(myX, myY);
				_myLastHeights[index] += _myDeltaTimeHistory.get(index);
			}

			g.endShape();
		}

		public void startRecord() {
			while (_myDeltaTimeHistory.size() < _cSamples) {
				_myDeltaTimeHistory.add(0f);
			}
			_myNanoTime = System.nanoTime();
		}

		public void stopRecord() {
			long myDeltaTime = System.nanoTime() - _myNanoTime;
			double myLastDeltaTime = (double) myDeltaTime / 1000000;
			if(_cPrint)System.out.println(_myName + " took " + CCFormatUtil.nd(myLastDeltaTime, 4)+" sec");
			_myCurrentTime += myLastDeltaTime;
		}
	}

	private HashMap<String, HashMap<String, CCStopWatchItem>> _myBlocks;
	private String _myCurrentBlock = "default";
	private String _myCurrentItemName = null;
	private int _myClockPos = 0;

	private static CCStopWatch _myInstance = null;

	private CCColor _myColors[] = new CCColor[] { 
		new CCColor(11, 175, 181), 
		new CCColor(167, 184, 145), 
		new CCColor(59, 149, 178), 
		new CCColor(187, 173, 106),
		new CCColor(170, 143, 154), 
		new CCColor(155, 143, 170), 
		new CCColor(143, 154, 170), 
		new CCColor(143, 170, 153)
	};

	public CCStopWatch() {
		_myBlocks = new HashMap<String, HashMap<String, CCStopWatchItem>>();
		startBlock("default");
	}

	public static CCStopWatch instance() {
		if (_myInstance == null) {
			_myInstance = new CCStopWatch();
		}
		return _myInstance;
	}

	private boolean breakExecution() {
		return _cPause || !_cActive;
	}
	
	public void print(boolean theDoPrint){
		_cPrint = theDoPrint;
	}

	public void startBlock(String theBlockName) {
		if (breakExecution())
			return;

		_myCurrentBlock = theBlockName;

		if (!_myBlocks.containsKey(_myCurrentBlock)) {
			_myBlocks.put(_myCurrentBlock, new HashMap<String, CCStopWatchItem>());
		}
	}

	public void endLastAndStartWatch(String theName) {
		endLast();
		startWatch(theName);
	}

	public void endLast() {
		if (breakExecution())
			return;
		endWatch(_myCurrentItemName);
	}

	public void startWatch(String theName) {
		if (breakExecution())
			return;

		_myCurrentItemName = theName;

		if (!_myBlocks.get(_myCurrentBlock).containsKey(theName)) {
			_myBlocks.get(_myCurrentBlock).put(theName, new CCStopWatchItem(theName));
		}
		_myBlocks.get(_myCurrentBlock).get(theName).startRecord();
	}

	public float endWatch(String theName) {
		if (breakExecution())
			return 0;

		CCStopWatchItem myCurrentItem = _myBlocks.get(_myCurrentBlock).get(theName);
		if(myCurrentItem == null)throw new RuntimeException("You end a watch with the name: +\"" + theName +"\" that does not have been started.");
		myCurrentItem.stopRecord();
		return myCurrentItem._myCurrentTime;
	}

	public void update(float theDeltaTime) {
		if (breakExecution())
			return;

		_myClockPos++;
		_myClockPos %= _cSamples;
	}

	private int _myLastColorIdx = 0;
	private float[] _myLastHeights;

	public void draw(CCGraphics g) {
		if (!_cActive) {
			return;
		}

		g.pushAttribute();
		float myWidth = g.width * _cWidth;
		float myHeight = g.height * _cHeight;

		_myLastHeights = new float[_cSamples];
		Arrays.fill(_myLastHeights, 0);
		_myLastColorIdx = 0;

		g.beginOrtho2D();

		for (HashMap<String, CCStopWatchItem> myItems : _myBlocks.values()) {
			for (CCStopWatchItem myItem : myItems.values()) {
				// XXX: should happen in update() and not in draw()
				myItem.step(_myClockPos);
				myItem.draw(g);
			}
		}

		// draw frame and grid
		g.color(1.0f);
		g.beginShape(CCDrawMode.LINES);

		g.vertex(0, myHeight);
		g.vertex(0, 0);
		g.vertex(myWidth, 0);

		g.endShape();

		float myLockBorder = (1000.0f / 60.0f) * _cScale;

		g.text("60Hz", 3, myLockBorder);
		g.color(1.0f, 0.5f);
		g.beginShape(CCDrawMode.LINES);

		g.vertex(30, myLockBorder);
		g.vertex(myWidth, myLockBorder);

		g.endShape();

		g.endOrtho2D();
		g.popAttribute();
	}
}
