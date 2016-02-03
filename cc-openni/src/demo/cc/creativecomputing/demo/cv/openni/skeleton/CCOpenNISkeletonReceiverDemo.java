package cc.creativecomputing.demo.cv.openni.skeleton;

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
import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.model.collada.CCColladaSkeletonUtil;
import cc.creativecomputing.net.CCUDPIn;
import cc.creativecomputing.net.codec.CCNetXMLCodec;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonTransformCalculator;
import cc.creativecomputing.skeleton.util.CCSkeletonReceiver;

public class CCOpenNISkeletonReceiverDemo extends CCApp  {

	private CCArcball _myArcball;

	private CCSkeletonManager _mySkeletonManager;
	private CCSkeletonReceiver _myReceiver;
	private CCColladaSkeletonSkin _mySkin;
	
	@CCControl(name = "draw skin")
	private boolean _cDrawSkin = false;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		CCColladaSkeletonUtil myColladaSkeletonUtil = new CCColladaSkeletonUtil("humanoid.dae", "bvh_import/Hips");

		CCColladaSkeleton mySkeleton = myColladaSkeletonUtil.skeleton();
		_mySkin = myColladaSkeletonUtil.skin();
		
		_myReceiver = new CCSkeletonReceiver(new CCUDPIn<>(new CCNetXMLCodec(), "127.0.0.1", 9000));
		_mySkeletonManager = new CCSkeletonManager(
			this,
			mySkeleton, 
			_myReceiver
		);
		_myReceiver.start();
		
		addControls("app", "filter", _mySkeletonManager.filter());
		addControls("app", "app", this);
		addControls("app", "transform", 0, CCSkeletonTransformCalculator.upperSettings);
		addControls("app", "transform", 1, CCSkeletonTransformCalculator.lowerSettings);
	}

	@Override
	public void update(float theDeltaTime) {
		_myReceiver.update(theDeltaTime);
		_mySkeletonManager.update(theDeltaTime);
	}
	
	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();
		_myArcball.draw(g);

		g.polygonMode(CCPolygonMode.LINE);
		for(CCSkeleton mySkeleton:_mySkeletonManager.skeletons()){
			mySkeleton.draw(g);
			mySkeleton.drawOrientations(g, 2);
			g.color(1f,0.5f);
			if(_cDrawSkin)_mySkin.draw(g, mySkeleton);
		}
		g.polygonMode(CCPolygonMode.FILL);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISkeletonReceiverDemo.class);
		myManager.settings().size(500, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}