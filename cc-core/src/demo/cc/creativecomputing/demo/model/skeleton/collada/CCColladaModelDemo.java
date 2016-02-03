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
package cc.creativecomputing.demo.model.skeleton.collada;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeletonProvider;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonSkin.CCSkeletonSkinPoint;

public class CCColladaModelDemo extends CCApp {

	@CCControl(name = "axis length", min = 0, max = 100)
	private float _cAxisLength = 0;

	@CCControl(name = "time", min = 0, max = 2)
	private float _cTime = 0;
	
	@CCControl(name = "skin point", min = 0, max = 1)
	private float _cSkinPoint = 0;
	
	@CCControl(name = "skin scale", min = 0, max = 10)
	private float _cSkinScale = 0;

	private CCArcball _myArcball;

	private CCColladaSkeletonProvider _myColladaSkeletonProvider;
	private CCColladaSkeletonSkin _mySkeletonSkin;
	private CCSkeleton _mySkeleton;
	private CCSkeletonSkinPoint _mySkinPoint;
	private List<CCSkeletonSkinPoint> _myBox = new ArrayList<>();
	private CCVector3f[] _myBoxPoints = new CCVector3f[]{
		new CCVector3f(-10,-10,-10),
		new CCVector3f( 10,-10,-10),
		new CCVector3f( 10,-10, 10),
		new CCVector3f(-10,-10, 10),
		new CCVector3f(-10, 10,-10),
		new CCVector3f( 10, 10,-10),
		new CCVector3f( 10, 10,10),
		new CCVector3f(-10, 10,10),
	};
	private CCSkeletonSkinPoint _myOuterSkinPoint;
	private CCVector3f _myTransformedPoint;
	private CCVector3f _myOuterTransformedPoint;

	public void setup() {
		addControls("app", "app", this);
		_myArcball = new CCArcball(this);

		CCColladaLoader myColladaLoader = new CCColladaLoader("demo/model/collada/humanoid.dae");
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		_myColladaSkeletonProvider = new CCColladaSkeletonProvider(myColladaLoader.animations().animations(), mySkinController,myScene.node("bvh_import/Hips"));
		_mySkeleton = _myColladaSkeletonProvider.skeleton();

		_mySkeletonSkin = new CCColladaSkeletonSkin(mySkinController);
	}

	@Override
	public void update(float theDeltaTime) {
		_myColladaSkeletonProvider.time(_cTime);
		int myIndex = (int)(_cSkinPoint * _mySkeletonSkin.numberOfVertices());
		
		_mySkinPoint = _mySkeletonSkin.point(myIndex);
		
		_myBox.clear();
		for(CCVector3f myBoxPoint:_myBoxPoints){
			_myBox.add(new CCSkeletonSkinPoint(
				_mySkinPoint.origin().x + myBoxPoint.x, 
				_mySkinPoint.origin().y + myBoxPoint.y, 
				_mySkinPoint.origin().z + myBoxPoint.z,
				_mySkinPoint.normal().x, 
				_mySkinPoint.normal().y, 
				_mySkinPoint.normal().z,
				_mySkinPoint.weights(),
				_mySkinPoint.weightIndices()
			));
		}
		
		_myOuterSkinPoint = new CCSkeletonSkinPoint(
			_mySkinPoint.origin().x + _mySkinPoint.normal().x * 30, 
			_mySkinPoint.origin().y + _mySkinPoint.normal().y * 30, 
			_mySkinPoint.origin().z + _mySkinPoint.normal().z * 30,
			_mySkinPoint.normal().x, 
			_mySkinPoint.normal().y, 
			_mySkinPoint.normal().z,
			_mySkinPoint.weights(),
			_mySkinPoint.weightIndices()
		);
		_myTransformedPoint = _mySkeletonSkin.position(_mySkinPoint, _mySkeleton);
		_myOuterTransformedPoint = _mySkeletonSkin.position(_myOuterSkinPoint, _mySkeleton);
	}

	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();

		_myArcball.draw(g);

		g.color(125, 0, 0);
		g.strokeWeight(1);
		g.line(0, 0, 0, width, 0, 0);
		g.color(0, 125, 0);
		g.line(0, 0, 0, 0, 0, -width);
		g.color(0, 0, 125);
		g.line(0, 0, 0, 0, -height, 0);

		g.color(255);
		g.polygonMode(CCPolygonMode.LINE);
		_mySkeletonSkin.draw(g, _mySkeleton);
		g.polygonMode(CCPolygonMode.FILL);
		g.clearDepthBuffer();
		g.color(255, 0, 0);

		_mySkeleton.draw(g);
		_mySkeleton.drawOrientations(g, _cAxisLength);
		
		g.color(255,0,0);
		g.ellipse(_mySkinPoint.origin(), 10);
		g.color(0,255,0);
		g.ellipse(_myTransformedPoint, 10);
		g.color(0,0,255);
		g.ellipse(_myOuterTransformedPoint, 10);
		g.color(255);
		g.line(_myTransformedPoint, _myOuterTransformedPoint);
		
		for(CCSkeletonSkinPoint myBoxPoint:_myBox){
			g.ellipse(_mySkeletonSkin.position(myBoxPoint, _mySkeleton), 10);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColladaModelDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
//		myManager.settings().displayMode(CCDisplayMode.NEWT);
		myManager.start();
	}
}