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
package cc.creativecomputing.cv.openni;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openni.CalibrationProgressEventArgs;
import org.openni.CalibrationProgressStatus;
import org.openni.CalibrationStartEventArgs;
import org.openni.GeneralException;
import org.openni.IObservable;
import org.openni.IObserver;
import org.openni.PoseDetectionCapability;
import org.openni.PoseDetectionEventArgs;
import org.openni.SceneMetaData;
import org.openni.SkeletonCapability;
import org.openni.SkeletonProfile;
import org.openni.StatusException;
import org.openni.UserEventArgs;
import org.openni.UserGenerator;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.util.logging.CCLog;

/**
 * An object that generates data relating to a figure in the scene.
 * 
 * @author christianriekoff
 * 
 */
public class CCOpenNIUserGenerator extends CCOpenNIGenerator<UserGenerator>{
	
	class NewUserObserver implements IObserver<UserEventArgs> {
		
		public void update(IObservable<UserEventArgs> observable, UserEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onNewUser - userId: " + theArgs.getId());

			try {
//				if(_mySkeletonCapability.needPoseForCalibration()) {
//					if(CCOpenNI.DEBUG)CCLog.info("start pose detection");
//					_myPoseDetectionCapability.startPoseDetection(_myCalibrationPose, theArgs.getId());
//				}else {
					if(CCOpenNI.DEBUG)CCLog.info("request skeleton calibration");
					_mySkeletonCapability.requestSkeletonCalibration(theArgs.getId(), true);
//				}
			} catch (StatusException e) {
				throw new CCOpenNIException(e);
			}
		}
	}

	class LostUserObserver implements IObserver<UserEventArgs> {
		
		public void update(IObservable<UserEventArgs> observable, UserEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onLostUser - userId: " + theArgs.getId());
			CCOpenNIUser myUser = _myUserMap.remove(theArgs.getId());
			if(myUser == null)return;
			synchronized(_myLostUser) {
				_myLostUser.add(myUser);
			}
		}
	}
	
	class ExitUserObserver implements IObserver<UserEventArgs> {
		public void update(IObservable<UserEventArgs> observable, UserEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onExitUser - userId: " + theArgs.getId());

			// make a user invisible when he exits
			CCOpenNIUser myUser;
			synchronized(_myUserMap) {
				myUser = _myUserMap.get(theArgs.getId());
				if (myUser == null)
					return;
				
			}
			
			synchronized(_myExitUser) {
				_myExitUser.add(myUser);
			}
			myUser.isVisible(false);
		}
	}

	class ReEnterUserObserver implements IObserver<UserEventArgs> {
		public void update(IObservable<UserEventArgs> observable, UserEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onReenterUser - userId: " + theArgs.getId());

			CCOpenNIUser myUser;
			// make a user visible when he reenters
			synchronized(_myUserMap) {
				myUser = _myUserMap.get(theArgs.getId());
			}
			if (myUser == null)
				return;
			myUser.isVisible(true);
			synchronized(_myEnteredUser) {
				_myEnteredUser.add(myUser);
			}
		}
	}
	
	class CalibrationStartObserver implements IObserver<CalibrationStartEventArgs> {
		
		public void update(IObservable<CalibrationStartEventArgs> theObservable, CalibrationStartEventArgs theArgs) {
  			if(CCOpenNI.DEBUG)CCLog.info("onStartCalibration - userId: " + theArgs.getUser());
		}
	}
	
	class CalibrationProgressObserver implements IObserver<CalibrationProgressEventArgs> {
		
		public void update(IObservable<CalibrationProgressEventArgs> theObservable, CalibrationProgressEventArgs theArgs) {
			if(CCOpenNI.DEBUG)CCLog.info("onUpdateCalibration - userId: " + theArgs.getUser() + ", status: " + theArgs.getStatus());
		}
	}

	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs> {
		
		public void update(IObservable<CalibrationProgressEventArgs> observable, CalibrationProgressEventArgs theArgs) {
			boolean sucessful = theArgs.getStatus() == CalibrationProgressStatus.OK;
			int myUserID = theArgs.getUser();
			if(CCOpenNI.DEBUG)CCLog.info("onEndCalibration - userId: " + myUserID + ", successfull: " + sucessful);

			if (sucessful) {
				if(CCOpenNI.DEBUG)CCLog.info("  User calibrated !!!");
				try {
					_mySkeletonCapability.startTracking(myUserID);
				
					CCOpenNIUser myUser = new CCOpenNIUser( myUserID, _myTransformationMatrix, _mySkeletonCapability);
					myUser.updatePixels(_myUpdatePixels);
	
					synchronized (_myNewUser) {
						_myNewUser.add(myUser);
					}
					synchronized(_myUserMap) {
						_myUserMap.put(myUserID, myUser);
					}
				} catch (StatusException e) {
					throw new CCOpenNIException(e);
				}
			} else {
				if(CCOpenNI.DEBUG)CCLog.info("Failed to calibrate user !!!");
				if(CCOpenNI.DEBUG)CCLog.info("Start pose detection");
				try {
					_mySkeletonCapability.requestSkeletonCalibration(myUserID, true);
				} catch (StatusException e) {
					throw new CCOpenNIException(e);
				}
			}
		}
	}
	

//	class OutOfPoseObserver implements IObserver<PoseDetectionEventArgs> {
//
//		public void update(IObservable<PoseDetectionEventArgs> theObservable, PoseDetectionEventArgs theArgs) {
//			CCLog.info("onEndPose - userId: " + theArgs.getUser() + ", pose: " + theArgs.getPose());
//		}
//	}
//
//	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs> {
//		
//		public void update(IObservable<PoseDetectionEventArgs> observable, PoseDetectionEventArgs theArgs) {
//			if(CCOpenNI.DEBUG)CCLog.info("onStartPose - userId: " + theArgs.getUser() + ", pose: " + theArgs.getPose());
//			if(CCOpenNI.DEBUG)CCLog.info(" stop pose detection");
//
//			try {
//				_myPoseDetectionCapability.stopPoseDetection(theArgs.getUser());
//				_mySkeletonCapability.requestSkeletonCalibration(theArgs.getUser(), true);
//			} catch (StatusException e) {
//				throw new CCOpenNIException(e);
//			}
//		}
//	}
	
	public static interface CCUserListener{
		public void onNewUser(CCOpenNIUser theUser);
		public void onLostUser(CCOpenNIUser theUser);
		public void onEnterUser(CCOpenNIUser theUser);
		public void onExitUser(CCOpenNIUser theUser);
	}
	
	public static enum CCUserSkeletonProfile{
		NONE(SkeletonProfile.NONE),
		ALL(SkeletonProfile.ALL),
		UPPER(SkeletonProfile.UPPER_BODY),
		LOWER(SkeletonProfile.LOWER_BODY),
		HEAD_HANDS (SkeletonProfile.HEAD_HANDS);
		
		private SkeletonProfile _myProfile;
		
		CCUserSkeletonProfile(SkeletonProfile theID){
			_myProfile = theID;
		}
		
		public SkeletonProfile profile() {
			return _myProfile;
		}
	}
	
	private Map<Integer, CCOpenNIUser> _myUserMap = new HashMap<Integer, CCOpenNIUser>();
	
	private List<CCOpenNIUser> _myNewUser = new ArrayList<CCOpenNIUser>();
	private List<CCOpenNIUser> _myLostUser = new ArrayList<CCOpenNIUser>();
	private List<CCOpenNIUser> _myExitUser = new ArrayList<CCOpenNIUser>();
	private List<CCOpenNIUser> _myEnteredUser = new ArrayList<CCOpenNIUser>();
	
	private CCListenerManager<CCUserListener> _myEvents = new CCListenerManager<CCUserListener>(CCUserListener.class);
	
	private SkeletonCapability _mySkeletonCapability;
//	private String _myCalibrationPose;
//	private PoseDetectionCapability _myPoseDetectionCapability;
	
	private boolean _myUpdatePixels = false;
	
	private CCMatrix4f _myTransformationMatrix;

	CCOpenNIUserGenerator(CCOpenNI theOpenNI) {
		super(theOpenNI);
		_myTransformationMatrix = theOpenNI.transformationMatrix();
		

	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.openni2.CCOpenNIGenerator#create(org.OpenNI.Context)
	 */
	@Override
	UserGenerator create(CCOpenNI theOpenNI) {
		try {
			UserGenerator myUserGenerator;
			if(theOpenNI.deviceQuery() == null)myUserGenerator = UserGenerator.create(theOpenNI.context());
			else myUserGenerator = UserGenerator.create(theOpenNI.context(), theOpenNI.deviceQuery());
			
			_mySkeletonCapability = myUserGenerator.getSkeletonCapability();
//			_myCalibrationPose = _mySkeletonCapability.getSkeletonCalibrationPose();
//			_myPoseDetectionCapability = myUserGenerator.getPoseDetectionCapability();
			
			myUserGenerator.getNewUserEvent().addObserver(new NewUserObserver());
			myUserGenerator.getLostUserEvent().addObserver(new LostUserObserver());
			myUserGenerator.getUserExitEvent().addObserver(new ExitUserObserver());
			myUserGenerator.getUserReenterEvent().addObserver(new ReEnterUserObserver());
			
			_mySkeletonCapability.getCalibrationStartEvent().addObserver(new CalibrationStartObserver());
			_mySkeletonCapability.getCalibrationInProgressEvent().addObserver(new CalibrationProgressObserver());
			_mySkeletonCapability.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
			
			_mySkeletonCapability.setSkeletonProfile(SkeletonProfile.ALL);
			
//			_myPoseDetectionCapability.getOutOfPoseEvent().addObserver(new OutOfPoseObserver());
//			_myPoseDetectionCapability.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
			
			return myUserGenerator;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public SkeletonCapability skeletonCapability() {
		return _mySkeletonCapability;
	}
	
	public CCListenerManager<CCUserListener> events(){
		return _myEvents;
	}
	
	public void updatePixels(final boolean theUpdatePixels) {
		_myUpdatePixels = theUpdatePixels;
	}

	/**
	 * Save the calibration data to file.
	 * @param user
	 * @param calibrationFile
	 */
	public void saveSkeletonCalibrationDataToFile(int user, String calibrationFile) {
		String path = CCIOUtil.dataPath(calibrationFile);
		CCIOUtil.createPath(path);
		try {
			_mySkeletonCapability.saveSkeletonCalibrationDataToFile(user, path);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}

	/**
	 * Load previously saved calibration data from file.
	 * @param user
	 * @param calibrationFile
	 */
	public void loadCalibrationDataSkeleton(int user, String calibrationFile) {
		String path = CCIOUtil.dataPath(calibrationFile);
		try {
			_mySkeletonCapability.loadSkeletonCalibrationDatadFromFile(user, path);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	@Override
	void update(float theDeltaTime) {
		
		synchronized (_myLostUser) {
			for(CCOpenNIUser myLostUser:_myLostUser) {
				_myEvents.proxy().onLostUser(myLostUser);
			}
			_myLostUser.clear();
		}
		
		synchronized (_myExitUser) {
			for(CCOpenNIUser myExitUser:_myExitUser) {
				_myEvents.proxy().onExitUser(myExitUser);
			}
			_myExitUser.clear();
		}
		
		synchronized (_myEnteredUser) {
			for(CCOpenNIUser myEnteredUser:_myEnteredUser) {
				_myEvents.proxy().onEnterUser(myEnteredUser);
			}
			_myEnteredUser.clear();
		}
		
		synchronized (_myNewUser) {
			for(CCOpenNIUser myNewUser:_myNewUser) {
				_myEvents.proxy().onNewUser(myNewUser);
			}
			_myNewUser.clear();
		}
		
		for(CCOpenNIUser myUser:_myUserMap.values()) {
			myUser.update(theDeltaTime);
			
			if(!myUser.needsUpdate())return;
			
			if(_myUpdatePixels) {
				SceneMetaData myMetaData = _myGenerator.getUserPixels(myUser.id());
				myUser.raw(myMetaData.getData().createShortBuffer());
			}
			
//			Point3D myCom;
//			try {
//				myCom = _myGenerator.getUserCoM(myUser.id());
//				myUser.centerOfMass().set(myCom.getX(), myCom.getY(), myCom.getZ());
//				_myTransformationMatrix.inverseTransform(myUser.centerOfMass());
//			} catch (StatusException e) {
//				throw new CCOpenNIException(e);
//			}
		}
	}
	
	public Collection<CCOpenNIUser> user(){
		return _myUserMap.values();
	}
}
