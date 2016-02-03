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
package cc.creativecomputing.demo.graphics.mesh;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.CCStringUtil;

public class CCPointCloudDemo extends CCApp {

	private CCVBOMesh _myMesh;
	private CCArcball _myArcBall;

	@Override
	public void setup() {
		// cloud.loadFloats(loadPoints("culdesac.csv"));
		loadPoints("demo/geometry/pointclouds/city.csv.gz");
		
		_myArcBall = new CCArcball(this);
	}

	private void loadPoints(String path) {
		String[] raw = CCIOUtil.loadStrings(path);
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, raw.length);
		for (int i = 0; i < raw.length; i++) {
			String[] thisLine = CCStringUtil.split(raw[i], ",");
			_myMesh.addVertex(
				Float.parseFloat(thisLine[0]) / 1000,
				Float.parseFloat(thisLine[1]) / 1000,
				Float.parseFloat(thisLine[2]) / 1000
			);

			// colors[i*4] = new Float(thisLine[3]).floatValue()/3f ;
			// colors[i*4+1] = new Float(thisLine[3]).floatValue()/3f ;
			// colors[i*4+2] = 0f ;
			// colors[i*4+3] = 100f ;

		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcBall.draw(g);
		
		g.pointSize(0.1f);
		g.color(255, 10, 0, 150);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myMesh.draw(g);
	}

	

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPointCloudDemo.class);
		myManager.settings().antialiasing(8);
		myManager.settings().size(1024, 768);
		myManager.start();
	}
}
