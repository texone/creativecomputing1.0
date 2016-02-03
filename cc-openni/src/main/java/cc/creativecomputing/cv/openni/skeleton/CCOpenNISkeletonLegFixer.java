package cc.creativecomputing.cv.openni.skeleton;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserJointType;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.util.logging.CCLog;

public class CCOpenNISkeletonLegFixer {
	@CCControl(name = "foot plane y", min = -5000f, max = 5000f)
	private float _cFootPlanePlaneY = 0;
	
	@CCControl(name = "hip neck floor ratio", min = 0, max = 1)
	private float _cHipNeckFloorRatio = 0.5f;
	
	@CCControl(name = "max foot level", min = 0f, max = 5000f)
	private float _cMaxFootDistance = 500;
	
	@CCControl(name = "plane draw scale", min = 0f, max = 5000f)
	private float _cPlaneDrawScale = 500;
	
	@CCControl(name = "active")
	private boolean _cActive = false;
	
	@CCControl(name = "draw debug")
	private boolean _cDrawDebug = false;

	private CCPlane3f _myPlane = new CCPlane3f(new CCVector3f(0,1,0),0);
	private CCPlane3f _myDistancePlane = new CCPlane3f(new CCVector3f(0,1,0),0);
	
	public void update(float theDeltaTime){
		_myPlane.constant(_cFootPlanePlaneY);
		_myPlane.drawScale(_cPlaneDrawScale);
		
		_myDistancePlane.constant(_cFootPlanePlaneY + _cMaxFootDistance);
		_myDistancePlane.drawScale(_cPlaneDrawScale);
	}
	
	public void fixPositions(CCOpenNIUser theUser){
		if(!_cActive)return;
		
		float myLeftFootFloorDistance = CCMath.abs(_myPlane.distance(theUser.joint(CCOpenNIUserJointType.LEFT_FOOT).position()));
		float myRightFootFloorDistance = CCMath.abs(_myPlane.distance(theUser.joint(CCOpenNIUserJointType.RIGHT_FOOT).position()));
		
		if(theUser.joint(CCOpenNIUserJointType.LEFT_FOOT).positionConfidence() > 0.5 && theUser.joint(CCOpenNIUserJointType.RIGHT_FOOT).positionConfidence() > 0.5){
			if(myLeftFootFloorDistance < _cMaxFootDistance || myRightFootFloorDistance < _cMaxFootDistance)return;
		}

		CCVector3f myNeckJoint = theUser.joint(CCOpenNIUserJointType.NECK).position().clone();
		float myNewHipY = CCMath.blend(myNeckJoint.y, _cFootPlanePlaneY, _cHipNeckFloorRatio);
		theUser.joint(CCOpenNIUserJointType.LEFT_HIP).position().y = myNewHipY;
		theUser.joint(CCOpenNIUserJointType.RIGHT_HIP).position().y = myNewHipY;

		CCVector3f myLeftFoot = theUser.joint(CCOpenNIUserJointType.LEFT_HIP).position().clone();
		myLeftFoot.y = _cFootPlanePlaneY;
		theUser.joint(CCOpenNIUserJointType.LEFT_FOOT).position().set(myLeftFoot);

		CCVector3f myLeftKneeJoint = myLeftFoot.clone();
		myLeftKneeJoint.y = CCMath.blend(myNewHipY, _cFootPlanePlaneY, 0.5f);
		theUser.joint(CCOpenNIUserJointType.LEFT_KNEE).position().set(myLeftKneeJoint);

		CCVector3f myRightFoot = theUser.joint(CCOpenNIUserJointType.RIGHT_HIP).position().clone();
		myRightFoot.y = _cFootPlanePlaneY;
		theUser.joint(CCOpenNIUserJointType.RIGHT_FOOT).position().set(myRightFoot);

		CCVector3f myRightKneeJoint = myRightFoot.clone();
		myRightKneeJoint.y = CCMath.blend(myNewHipY, _cFootPlanePlaneY, 0.5f);
		theUser.joint(CCOpenNIUserJointType.RIGHT_KNEE).position().set(myRightKneeJoint);
	}
	
	public void draw(CCGraphics g){
		if(!_cDrawDebug || !_cActive)return;
		g.color(0,255,0, 100);
		_myPlane.draw(g);
		_myDistancePlane.draw(g);
	}
}
