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
package cc.creativecomputing.demo.cv.openni.skeleton;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.model.collada.CCColladaSkeletonUtil;
import cc.creativecomputing.net.CCUDPIn;
import cc.creativecomputing.net.codec.CCNetXMLCodec;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.util.CCSkeletonReceiver;

public class CCColladaModelOpenNIDemo extends CCApp  {

	private CCArcball _myArcball;

	private CCColladaSkeleton _myColladaSkeleton;
	private CCColladaSkeletonSkin _mySkeletonSkin;
	private CCSkeletonManager _mySkeletonManager;
	
	private static boolean USE_OPENNI = true;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	@CCControl(name = "openni scale", min = 1, max = 10)
	private float _cOpenNIScale = 10;
	
	@CCControl(name = "z", min = -1000, max = 1000)
	private float _cZ = 0;
	
	@CCControl(name = "collada scale", min = 1, max = 10)
	private float _cColladaScale = 10;
	
	@CCControl(name = "collada x", min = -1000, max = 1000)
	private float _cColladaX = 0;
	
	@CCControl(name = "collada y", min = -1000, max = 1000)
	private float _cColladaY = 0;
	
	@CCControl(name = "collada z", min = -1000, max = 1000)
	private float _cColladaZ = 0;
	
	@CCControl(name = "update")
	private boolean _cUpdate = true;

	@CCControl(name = "draw mesh")
	private boolean _cDrawMesh = true;
	

	@CCControl(name = "skeleton provider")
	private CCOpenNISkeletonProvider _mySkeletonProvider; 
	
	private CCSkeletonReceiver _myReceiver;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		CCColladaSkeletonUtil myColladaSkeletonUtil = new CCColladaSkeletonUtil("humanoid.dae", "bvh_import/Hips");

		_myColladaSkeleton = myColladaSkeletonUtil.skeleton();
		_mySkeletonSkin = myColladaSkeletonUtil.skin();

		if(USE_OPENNI){
			_myOpenNI = new CCOpenNI(this);
			_myOpenNI.openFileRecording("SkeletonRec.oni");
			_myUserGenerator = _myOpenNI.createUserGenerator();
			_myOpenNI.start();
			
			_mySkeletonProvider = new CCOpenNISkeletonProvider(_myUserGenerator);
			_mySkeletonManager = new CCSkeletonManager(
				this,
				_myColladaSkeleton, 
				_mySkeletonProvider
			);
		}else{
			_myReceiver = new CCSkeletonReceiver(new CCUDPIn<>(new CCNetXMLCodec(), "127.0.0.1", 9000));
			_mySkeletonManager = new CCSkeletonManager(
				this,
				_myColladaSkeleton, 
				_myReceiver
			);
			_myReceiver.start();
		}
		
		addControls("app", "app", this);
	}

	@Override
	public void update(float theDeltaTime) {
		if(!USE_OPENNI)_myReceiver.update(theDeltaTime);
		if(_cUpdate)_mySkeletonManager.update(theDeltaTime);
	}
	
	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		g.pushMatrix();
		g.scale(_cOpenNIScale);
		g.translate(0,0,_cZ);

		g.polygonMode(CCPolygonMode.LINE);
		for(CCSkeleton mySkeleton:_mySkeletonManager.skeletons()){
			g.color(255);
			mySkeleton.draw(g);
			mySkeleton.drawOrientations(g, 100);
			g.color(255);
			if(_cDrawMesh)_mySkeletonSkin.draw(g, mySkeleton);
		}
//		for(CCOpenNIUser myUser:_myUserGenerator.user()){
//			myUser.drawSkeleton(g);
//		}
		g.popMatrix();
		g.pushMatrix();
		g.translate(_cColladaX,_cColladaY,_cColladaZ);
		g.scale(_cColladaScale);
		_myColladaSkeleton.draw(g);
		_myColladaSkeleton.drawOrientations(g ,10);
		if(_cDrawMesh)_mySkeletonSkin.draw(g, _myColladaSkeleton);
		g.popMatrix();
		g.popMatrix();
		g.polygonMode(CCPolygonMode.FILL);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCColladaModelOpenNIDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		myManager.settings().frameRate(10);
		myManager.start();
	}
}
