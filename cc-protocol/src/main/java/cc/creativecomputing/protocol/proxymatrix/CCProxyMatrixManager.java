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
package cc.creativecomputing.protocol.proxymatrix;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCGraphics;

/**
 * Use this manager to add multiple proxy matrices instances to one application, this is useful
 * if you connect multiple boards but want to treat them like one input device.
 * @author christianriekoff
 *
 */
public class CCProxyMatrixManager implements CCUpdateListener{

	private List<CCProxiMatrix> _myProxyMatrices = new ArrayList<CCProxiMatrix>();
	
	private CCApp _myApp;
	private int _myScreenWidth;
	private int _myScreenHeight;
	
	public CCProxyMatrixManager(final CCApp theApp,final int theScreenWidth, final int theScreenHeight) {
		_myApp = theApp;
		_myScreenWidth = theScreenWidth;
		_myScreenHeight = theScreenHeight;
		
		_myApp.addUpdateListener(this);
	}
	
	public CCProxiMatrix addConnection(
		final String theIP, final int thePort, 
		final int theMatrixWidth, final int theMatrixHeight
	) {
		CCProxiMatrix myProxiMatrix = new CCProxiMatrix (_myApp, theIP, thePort, theMatrixWidth, theMatrixHeight, _myScreenWidth, _myScreenHeight);

		_myApp.addControls("sensors", theIP,_myProxyMatrices.size(), myProxiMatrix);
		
		_myProxyMatrices.add(myProxiMatrix);
		
		return myProxiMatrix;
	}
	
	public void connect() {
		for(CCProxiMatrix myProxiMatrix:_myProxyMatrices) {
			myProxiMatrix.connect();
		}
	}
	
	public void update(final float theDeltaTime) {
		for(CCProxiMatrix myProxiMatrix:_myProxyMatrices) {
			myProxiMatrix.update(theDeltaTime);
		}
	}
	
	public void addListener(final CCTouchListener theListener) {
		for(CCProxiMatrix myProxiMatrix:_myProxyMatrices) {
			myProxiMatrix.addListener(theListener);
		}
	}
	
	public void draw(CCGraphics g) {
		for(CCProxiMatrix myProxiMatrix:_myProxyMatrices) {
			myProxiMatrix.draw(g);
		}
	}
}
