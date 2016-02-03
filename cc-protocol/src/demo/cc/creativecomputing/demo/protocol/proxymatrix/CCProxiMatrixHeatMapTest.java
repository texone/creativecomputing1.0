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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.protocol.proxymatrix.CCProxiMatrix;
import cc.creativecomputing.protocol.proxymatrix.CCProxyMatrixManager;

public class CCProxiMatrixHeatMapTest extends CCApp {

	private CCProxyMatrixManager _myProxyMatrixManager;
	private CCProxiMatrix myMatrix;

	@Override
	public void setup() {

		showControls();
		_myProxyMatrixManager = new CCProxyMatrixManager(this, width, height);
		_myProxyMatrixManager.addConnection("brombeere", 10002, 64, 64);
		myMatrix = _myProxyMatrixManager.addConnection("himbeere", 10001, 64, 64);
		_myProxyMatrixManager.connect();
	}
	
	@Override
	public void update(float theDeltaTime) {
	}

	public void draw() {
		g.clear();
		g.color(1f);
		g.image(myMatrix.heatmap(), -myMatrix.heatmap().width() / 2, -myMatrix.heatmap().height() / 2);
	}

	
	public static void main(String[] args) {
		

		final CCApplicationManager myManager = new CCApplicationManager(CCProxiMatrixHeatMapTest.class);
		myManager.settings().size(1200, 800);
		myManager.start();
	}
}
