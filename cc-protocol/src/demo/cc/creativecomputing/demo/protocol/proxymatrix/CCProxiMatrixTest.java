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
package cc.creativecomputing.demo.protocol.proxymatrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.protocol.proxymatrix.CCProxiMatrix;
import cc.creativecomputing.protocol.proxymatrix.CCTouch;
import cc.creativecomputing.protocol.proxymatrix.CCTouchListener;

public class CCProxiMatrixTest extends CCApp implements CCTouchListener{

	private CCProxiMatrix _myProxyMatrix;
	
	private Map<Integer, CCVector2f> _myTouchMap = new HashMap<Integer, CCVector2f>();

	@Override
	public void setup() {

		showControls();

		_myProxyMatrix = new CCProxiMatrix(
			this, "10.1.0.106", 10001, 
			38, 20, width, height
		);
		_myProxyMatrix.addListener(this);
		_myProxyMatrix.connect();
		_myUI.drawBackground(false);
	}

	public void draw() {
		g.clear();
		_myProxyMatrix.draw(g);
		
		g.color(255,0,0);
		for(Entry<Integer, CCVector2f> myEntry:_myTouchMap.entrySet()) {
			g.ellipse(myEntry.getValue(), 20);
		}

	}
	
	public void onTouchMove(CCTouch theTouch) {
		_myTouchMap.get(theTouch.id()).set(theTouch.position());
	}

	public void onTouchPress(CCTouch theTouch) {
		_myTouchMap.put(theTouch.id(), theTouch.position());
	}

	public void onTouchRelease(CCTouch theTouch) {
		_myTouchMap.remove(theTouch.id());
		
	}

	public static void main(String[] args) {
//		final CCApplicationManager<CCProxiMatrixTest> _myManager = new CCApplicationManager<CCProxiMatrixTest>(CCProxiMatrixTest.class);
//		_myManager.settings().size(1920, 1200);
//		_myManager.settings().location(0, 0);
//		_myManager.settings().display(0);
//		_myManager.settings().undecorated(true);
//		_myManager.start();
		

		final CCApplicationManager myManager = new CCApplicationManager(CCProxiMatrixTest.class);
		myManager.settings().size(1200, 800);
//		_myManager.settings().location(0, 0);
//		_myManager.settings().display(0);
//		_myManager.settings().undecorated(true);
		myManager.start();
	}
}
