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
package cc.creativecomputing.skeleton.gesture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCMouseAdapter;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCMovingAverage;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;

/**
 * @author christianriekoff
 *
 */
public class CCSkeletonTouchInteractionManager implements CCSkeletonManagerListener{

	public static abstract class CCTouchSource{
		
		protected CCVector2f _myPosition = null;
		private CCMovingAverage<Float> _myLocationProximity;
		
		public CCTouchSource(CCVector2f thePosition){
			_myPosition = thePosition;

			_myLocationProximity = CCMovingAverage.floatAverage(0.9f);
			_myLocationProximity.skipRange(10f);
		}
		
		public CCVector2f position(){
			return _myPosition;
		}
		
		public float locationProximity(){
			return _myLocationProximity.value();
		}
		
		void locationProximity(float theBlend, float theSmooth) {
			_myLocationProximity.weight(theSmooth);
			_myLocationProximity.update(theBlend);
		}
		
		public void update(float theDeltaTime){
			
		}
		
		boolean isActive(){
			return true;
		}
	}
	
	public class CCMouseTouchSource extends CCTouchSource{
		
		private CCApp _myApp;
		
		public CCMouseTouchSource(CCApp theApp) {
			super(new CCVector2f());
			_myApp = theApp;
			_myApp.addMouseListener(new CCMouseAdapter() {
				@Override
				public void mousePressed(CCMouseEvent theEvent) {
					addSource(CCMouseTouchSource.this);
					_myPosition = new CCVector2f(theEvent.position().x, _myApp.height - theEvent.position().y);
				}
				
				@Override
				public void mouseReleased(CCMouseEvent theEvent) {
					removeSource(CCMouseTouchSource.this);
				}
			});
			_myApp.addMouseMotionListener(new CCMouseAdapter() {
				@Override
				public void mouseDragged(CCMouseEvent theEvent) {
					_myPosition.set(theEvent.position().x, _myApp.height - theEvent.position().y);
				}
			});
		}
		
		
		
	}
	
	public abstract class CCSkeletonTouchSource extends CCTouchSource{
		protected CCSkeleton _mySkeleton;
		protected CCVector3f _my3DPosition;
		
		public CCSkeletonTouchSource(CCSkeleton theSkeleton) {
			super(new CCVector2f());
			_mySkeleton = theSkeleton;
		}
		
		public abstract CCVector3f touch3DPosition();
		
		@Override
		public void update(float theDeltaTime) {
			_my3DPosition = touch3DPosition();
			CCVector3f myScreenPosition = _myGraphics.camera().modelToScreen(_my3DPosition);
			_myPosition.set(myScreenPosition.x, myScreenPosition.y);
		}

		@Override
		boolean isActive() {
			if(!_myUseSkeletonTouchSources)return false;
			
			if(!_cCheckHandDistance)return true;
			
			if(_my3DPosition == null)return false;
			
			CCPlane3f myBodyPlane = new CCPlane3f(
				_mySkeleton.joint(CCSkeletonJointType.LEFT_SHOULDER).position(),
				_mySkeleton.joint(CCSkeletonJointType.RIGHT_SHOULDER).position(),
				_mySkeleton.joint(CCSkeletonJointType.HIPS).position()
			);
			
			return myBodyPlane.distance(_my3DPosition) > _cMinHandDistance;
		}
	}
	
	public class CCSingleJointTouchSource extends CCSkeletonTouchSource{
		
		private CCSkeletonJoint _myJoint;
		
		public CCSingleJointTouchSource(CCSkeleton theSkeleton, CCSkeletonJoint theJoint) {
			super(theSkeleton);
			_myJoint = theJoint;
		}

		@Override
		public CCVector3f touch3DPosition() {
			return _myJoint.position();
		}
	}
	
	public class CCBlendedJointTouchSource extends CCSkeletonTouchSource{
		private CCSkeletonJoint _myJoint0;
		private CCSkeletonJoint _myJoint1;
		
		CCBlendedJointTouchSourceFactory _mySettings;
		
		public CCBlendedJointTouchSource(CCSkeleton theSkeleton, CCBlendedJointTouchSourceFactory theSettings, CCSkeletonJoint theJoint0, CCSkeletonJoint theJoint1) {
			super(theSkeleton);
			_mySettings = theSettings;
			_myJoint0 = theJoint0;
			_myJoint1 = theJoint1;
		}
		
		public CCSkeletonJoint joint0(){
			return _myJoint0;
		}
		
		public CCSkeletonJoint joint1(){
			return _myJoint1;
		}
		
		@Override
		public CCVector3f touch3DPosition() {
			return CCVecMath.blend(_mySettings._cJointBlend, _myJoint0.position(), _myJoint1.position());
		}
	}
	
	public static abstract class CCTouchSourceFactory{
		
		protected List<CCSkeletonJointType> _myTrackedJoints = new ArrayList<>();
		
		public abstract CCTouchSource createTouchSource(CCSkeleton theSkeleton);
		
		public List<CCSkeletonJointType> trackedJoints(){
			return _myTrackedJoints;
		}
	}
	
	public class CCSingleJointTouchSourceFactory extends CCTouchSourceFactory{
		
		public CCSingleJointTouchSourceFactory(CCSkeletonJointType theJoint){
			_myTrackedJoints.add(theJoint);
		}

		@Override
		public CCTouchSource createTouchSource(CCSkeleton theSkeleton) {
			return new CCSingleJointTouchSource(theSkeleton, theSkeleton.joint(_myTrackedJoints.get(0)));
		}
	}
	
	public class CCBlendedJointTouchSourceFactory extends CCTouchSourceFactory{
		
		@CCControl(name = "joint blend", min = -1, max = 2)
		private float _cJointBlend = 0;
		
		public CCBlendedJointTouchSourceFactory(CCSkeletonJointType theJoint0, CCSkeletonJointType theJoint1){
			_myTrackedJoints.add(theJoint0);
			_myTrackedJoints.add(theJoint1);
		}

		@Override
		public CCTouchSource createTouchSource(CCSkeleton theSkeleton) {
			return new CCBlendedJointTouchSource(
				theSkeleton,
				this,
				theSkeleton.joint(_myTrackedJoints.get(0)), 
				theSkeleton.joint(_myTrackedJoints.get(1))
			);
		}
	}
	
	public static interface CCSkeletonTouchInteractionManagerListener{
		public void onAddSource(CCSkeleton theSkeleton, CCTouchSourceFactory theFactory, CCTouchSource theSource);
		
		public void onRemoveSource(CCSkeleton theSkeleton, CCTouchSource theSource);
	}
	
	private CCListenerManager<CCSkeletonTouchInteractionManagerListener> _myEvents = CCListenerManager.create(CCSkeletonTouchInteractionManagerListener.class); 
	
	@CCControl(name = "proximity radius", min = 0, max = 200)
	private float _cProximityRadius = 0;
	@CCControl(name = "proximity filter weight", min = 0f, max = 1f)
	private float _cProximityFilterWeight = 0;
	
	@CCControl(name = "select radius", min = 0, max = 500)
	private float _cSelectRadius = 0;
	
	@CCControl(name = "select time", min = 0, max = 5)
	private float _cSelectTime = 0;
	
	
	@CCControl(name = "ring alpha", min = 0f, max = 1f)
	private float _cRingAlpha = 0;
	
	@CCControl(name = "ring inner radius", min = 0f, max = 100f)
	private float _cRingInnerRadius = 0;
	
	@CCControl(name = "ring outer radius", min = 0f, max = 100f)
	private float _cRingOuterRadius = 0;
	
	@CCControl(name = "min hand distance", min = 0, max = 1000)
	private float _cMinHandDistance = 0;
	
	@CCControl(name = "check hand distance")
	private boolean _cCheckHandDistance = false;
	
	@CCControl(name = "draw debug")
	private boolean _cDrawDebug = false;
	
	private boolean _myUseSkeletonTouchSources = true;
	
	private CCApp _myApp;
	private CCGraphics _myGraphics;
	
	private List<CCTouchSource> _mySources = new ArrayList<>();
	private List<CCSkeletonTouchLocation> _myLocations = new ArrayList<>();
	private Map<Integer, List<CCTouchSource>> _mySkeletonMap = new HashMap<>();
	
	private List<CCTouchSourceFactory> _myTouchFactories = new ArrayList<>();
	
	public CCSkeletonTouchInteractionManager(CCApp theApp) {
		_myApp = theApp;
		_myGraphics = _myApp.g;
		
		theApp.addControls("interaction", "interaction", this);
	}
	
	/**
	 * 
	 * @param theApp
	 * @param theTrackedJoints
	 */
	public CCSkeletonTouchInteractionManager(CCApp theApp, CCSkeletonJointType...theTrackedJoints) {
		_myApp = theApp;
		_myGraphics = _myApp.g;
		
		for(CCSkeletonJointType theJointType:theTrackedJoints){
			_myTouchFactories.add(new CCSingleJointTouchSourceFactory(theJointType));
		}
		
		theApp.addControls("interaction", "interaction", this);
	}
	
	public CCListenerManager<CCSkeletonTouchInteractionManagerListener> events(){
		return _myEvents;
	}
	
	/**
	 * Allows turning off the skeleton touch sources this must be setup at application start to work
	 * @param theUseSkeletonTouchSources
	 */
	public void useSkeletonTouchSources(boolean theUseSkeletonTouchSources){
		_myUseSkeletonTouchSources = theUseSkeletonTouchSources;
	}
	
	public void addTouchSourceFactory(CCTouchSourceFactory theFactory){
		_myTouchFactories.add(theFactory);
	}
	
	public CCMouseTouchSource createMouseSource(){
		return new CCMouseTouchSource(_myApp);
	}
	
	@Override
	public void onNewSkeleton(CCSkeleton theSkeleton) {
		if(theSkeleton.rootJoint().position().x == 0){
			return;
		}
		List<CCTouchSource> mySkeletonTouchSources = new ArrayList<>();
		_mySkeletonMap.put(theSkeleton.id(), mySkeletonTouchSources);
		for(CCTouchSourceFactory myFactory:_myTouchFactories){
			CCTouchSource myTouchSource = myFactory.createTouchSource(theSkeleton);
			mySkeletonTouchSources.add(myTouchSource);
			addSource(myTouchSource);
			_myEvents.proxy().onAddSource(theSkeleton, myFactory, myTouchSource);
		}
	}
	
	@Override
	public void onLostSkeleton(CCSkeleton theSkeleton) {
		if(theSkeleton.rootJoint().position().x == 0){
			return;
		}
		List<CCTouchSource> mySkeletonTouchSources = _mySkeletonMap.remove(theSkeleton.id());
		if(mySkeletonTouchSources == null)return;

		for(CCTouchSource mySource:mySkeletonTouchSources){
			removeSource(mySource);
			_myEvents.proxy().onRemoveSource(theSkeleton, mySource);
		}
	}
	
	public List<CCSkeletonTouchLocation> locations(){
		return _myLocations;
	}
	
	public List<CCTouchSource> sources(){
		return _mySources;
	}
	
	public void addSource(CCTouchSource theSource) {
		_mySources.add(theSource);
	}
	
	public void removeSource(CCTouchSource theSource) {
		_mySources.remove(theSource);
		for(CCSkeletonTouchPair myPair:_mySelectedPairs) {
			if(myPair._mySource == theSource) {
				myPair._mySource = null;
			}
		}
	}
	
	public static interface CCSkeletonTouchListener{
		public void onSelect(CCSkeletonTouchPair thePair);
	}
	
	public class CCSkeletonTouchPair implements Comparable<CCSkeletonTouchPair>{
		private CCTouchSource _mySource;
		private CCSkeletonTouchLocation _myLocation;
		
		private float _mySelectTime = 0;
		
		private boolean _myIsSelected = false;
		
		private CCListenerManager<CCSkeletonTouchListener> _myEvents = CCListenerManager.create(CCSkeletonTouchListener.class);
		
		private boolean _myIsOver = false;
		
		public CCSkeletonTouchPair(CCTouchSource theSource, CCSkeletonTouchLocation theLocation) {
			_mySource = theSource;
			_myLocation = theLocation;
		}
		
		public CCListenerManager<CCSkeletonTouchListener> events(){
			return _myEvents;
		}

		public float distance() {
			if(_mySource == null)return Float.MAX_VALUE;
			return _mySource.position().distance(_myLocation.position2D());
		}
		
		public CCSkeletonTouchLocation location(){
			return _myLocation;
		}
		
		public CCVector3f position2D(){
			return _myLocation.position2D();
		}
		
		public void update(final float theDeltaTime) {
			boolean myIsOver = _mySource != null && _mySource.isActive() && distance() < _cSelectRadius;
			if(_myIsOver && !myIsOver) {
				_myLocation.onOut();
			}
			_myIsOver = myIsOver;
			
			if(_myIsOver) {
				_mySelectTime += theDeltaTime;
			}else {
				if(_myIsSelected){
					_myIsSelected = false;
					_myLocation.onDeselect();
				}
				_mySelectTime = 0;
			}
			_mySelectTime = CCMath.constrain(_mySelectTime,0,_cSelectTime);
			
			if(_mySelectTime >= _cSelectTime && !_myIsSelected) {
				_myIsSelected = true;
				_myLocation.onSelect();
			}
			_myLocation.selectProgress(_mySelectTime / _cSelectTime);
		}
		
		public boolean isInSelection() {
			return _mySelectTime > 0;
		}
		
		public float selectProgress() {
			return _mySelectTime / _cSelectTime;
		}
		
		@Override
		public int compareTo(CCSkeletonTouchPair theArg0) {
			float difference =  theArg0.distance() - distance();
			return difference < 0 ? 1 : -1;
		}
	}
	
	private CCMatrix4f _myTransform = new CCMatrix4f();
	
	public CCMatrix4f transform() {
		return _myTransform;
	}
	
	
	public void updateLocations(float theDeltaTime) {
		
		for(CCTouchSource mySource:_mySources) {
			mySource.update(theDeltaTime);
		}
		
		for(CCSkeletonTouchLocation myLocation:locations()) {
			CCVector3f my2DCoords = _myGraphics.camera().modelToScreen(myLocation.position());
			myLocation.position2D().set(my2DCoords);
		}
	}
	
	private List<CCSkeletonTouchPair> _mySelectedPairs = new ArrayList<CCSkeletonTouchInteractionManager.CCSkeletonTouchPair>();
	
	public List<CCSkeletonTouchPair> touchSelection(){
		return _mySelectedPairs;
	}
	
	public void update(final float theDeltaTime) {
//		updateLocations(theDeltaTime);
		
		List<CCSkeletonTouchPair> myPairs = new ArrayList<CCSkeletonTouchInteractionManager.CCSkeletonTouchPair>();
		List<CCTouchSource> mySources = new ArrayList<>(_mySources);
		List<CCSkeletonTouchLocation> myLocations = new ArrayList<CCSkeletonTouchLocation>(_myLocations);
		
		for(int i = 0; i < _mySelectedPairs.size(); ) {
			CCSkeletonTouchPair myPair = _mySelectedPairs.get(i);
			myPair.update(theDeltaTime);
			
			if(!myPair.isInSelection()) {
				_mySelectedPairs.remove(i);
			}else {
				i++;
			}
		}
		
		for(CCSkeletonTouchPair mySelectedPair:_mySelectedPairs) {
			myPairs.add(mySelectedPair);
//			mySources.remove(mySelectedPair._mySource);
			myLocations.remove(mySelectedPair._myLocation);
		}
		
		for(CCTouchSource mySource:mySources) {
			if(mySource.position() == null || !mySource.isActive()) {
				continue;
			}
			
			for(CCSkeletonTouchLocation myLocation:myLocations) {
				if(!myLocation.isSelectable()) {
					continue;
				}
				CCSkeletonTouchPair myPair = new CCSkeletonTouchPair(mySource, myLocation);
				if(myPair.distance() > _cProximityRadius) {
					continue;
				}

				myPairs.add(myPair);
				
				if(myPair.isInSelection())continue;
				
				if(myPair.distance() <= _cSelectRadius) {
					myPair._myLocation.onOver();
					_mySelectedPairs.add(myPair);
				}
			}
		}
		
		Collections.sort(myPairs);
		for(int i = 0; i < myPairs.size() - 1; i++) {
			CCSkeletonTouchPair myPair1 = myPairs.get(i);
			
			for(int j = i + 1; j < myPairs.size();) {
				CCSkeletonTouchPair myPair2 = myPairs.get(j);
				if(myPair1._myLocation == myPair2._myLocation) {
					myPairs.remove(j);
				}else {
					j++;
				}
			}
		}
		
		for(CCSkeletonTouchLocation myLocation:_myLocations) {
			
			float myMinDistance = _cProximityRadius;
			CCSkeletonTouchPair myNearestPair = null;
			for(CCSkeletonTouchPair myPair:myPairs) {  
				if(myLocation != myPair._myLocation ) continue;
					
				float myDistance = myPair.distance();
				if(myDistance < myMinDistance) {
					myMinDistance = myDistance;
					myNearestPair = myPair;
				}
			}
			if(myNearestPair != null) {
				float mySourceLocationProximity = 1 - myNearestPair.distance() / _cProximityRadius;
				myNearestPair._mySource.locationProximity(mySourceLocationProximity, _cProximityFilterWeight);
				myLocation.sourceProximity(mySourceLocationProximity, _cProximityFilterWeight);
			}else {
				myLocation.sourceProximity(0, _cProximityFilterWeight);
			}
			
			myLocation.update(theDeltaTime);
		}
	}
	
	public void drawDebug(CCGraphics g) {
		if(!_cDrawDebug)return;
		
		g.pushMatrix();
		g.translate( -_myApp.width/2, -_myApp.height/2);
		for(CCSkeletonTouchLocation myLocation:_myLocations) {
			if(!myLocation.isSelectable())continue;

			g.color(1 - myLocation.sourceProximity(), myLocation.sourceProximity(),0);
			g.ellipse(myLocation.position2D().x, myLocation.position2D().y, 20);
		}

		
		g.color(0,255,0);
		for(CCTouchSource mySource:_mySources) {
			if(mySource.position() == null)continue;
			g.ellipse(mySource.position().x, mySource.position().y, 20);
		}
		for(CCSkeletonTouchPair myPair:_mySelectedPairs) {

			g.color(0,0,1f);
			g.ellipse(myPair._myLocation.position2D().x, myPair._myLocation.position2D().y, 20);
			
			g.color(1f);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(int i = 0; i < myPair.selectProgress() * 360; i++) {
				float myAngle = CCMath.radians(i);
				float x = CCMath.sin(myAngle);
				float y = CCMath.cos(myAngle);
				g.vertex(x * 22 + myPair._myLocation.position2D().x, y * 22 + myPair._myLocation.position2D().y);
				g.vertex(x * 28 + myPair._myLocation.position2D().x, y * 28 + myPair._myLocation.position2D().y);
			}
			g.endShape();
			
			
			g.text(myPair._myLocation.name(), myPair._myLocation.position2D().x, myPair._myLocation.position2D().y + 20);
		}
		g.popMatrix();
	}
	
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.translate( -_myApp.width/2, -_myApp.height/2);
		for(CCSkeletonTouchPair myPair:_mySelectedPairs) {

			g.color(1f, _cRingAlpha);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(int i = 0; i < CCMath.saturate(myPair.selectProgress()) * 360; i++) {
				float myAngle = CCMath.radians(i);
				float x = CCMath.sin(myAngle);
				float y = CCMath.cos(myAngle);
				g.vertex(x * _cRingInnerRadius + myPair._myLocation.position2D().x, y * _cRingInnerRadius + myPair._myLocation.position2D().y);
				g.vertex(x * _cRingOuterRadius + myPair._myLocation.position2D().x, y * _cRingOuterRadius + myPair._myLocation.position2D().y);
			}
			g.endShape();
			
			
//			g.pushMatrix();
//			g.color(1f);
//			g.translate(myPair._myLocation.position2D().x, myPair._myLocation.position2D().y + _cLocationNameY);
//			myPair._myLocation.textShadow().progress(myPair.selectProgress());
//			myPair._myLocation.textShadow().draw(g);
//			myPair._myLocation.text().progress(myPair.selectProgress());
//			myPair._myLocation.text().draw(g);
//			g.popMatrix();
//			myPair._myLocation.contentShadow().progress(myPair.selectProgress());
//			myPair._myLocation.contentShadow().draw(g);
//			myPair._myLocation.content().progress(myPair.selectProgress());
//			myPair._myLocation.content().draw(g);
		}
		
		for(CCSkeletonTouchLocation myLocation:_myLocations){
			g.ellipse(myLocation.position2D(), _cRingInnerRadius);
		}
		
//		CCLog.info("_mySources:" + _mySources.size());
		for(CCTouchSource mySource:_mySources){
			if (mySource.isActive()) {
				g.color(0.0f, 1.0f, 0.0f);
			} else {
				g.color(1.0f, 0.0f, 0.0f);
			}
			g.clearDepthBuffer();
			g.ellipse(mySource.position(),30);
		}
		g.color(1.0f, 1.0f, 1.0f);
		g.popMatrix();
	}
}
