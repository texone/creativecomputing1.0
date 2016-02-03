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
package cc.creativecomputing.demo.math;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCIcoSphere;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCIcoSphereDemo extends CCApp {
	
	private CCIcoSphere _mySphere;
	private CCVBOMesh _myMesh;
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_mySphere = new CCIcoSphere(new CCVector3f(), 200, 1);
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES);
		_myMesh.vertices(_mySphere.vertices());
		_myMesh.indices(_mySphere.indices());
		_myArcball = new CCArcball(this);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.polygonMode(CCPolygonMode.LINE);
		_myMesh.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCIcoSphereDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

