package cc.creativecomputing.demo.cv.openni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCSystem;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIImageGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIPlayer;
import cc.creativecomputing.cv.openni.CCOpenNISceneAnalyzer;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.net.CCUDPOut;
import cc.creativecomputing.net.codec.CCNetXMLCodec;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonJoint;
import cc.creativecomputing.skeleton.CCSkeletonJointType;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.CCSkeletonManager.CCSkeletonManagerListener;
import cc.creativecomputing.skeleton.util.CCSkeletonInteractionArea;
import cc.creativecomputing.skeleton.util.CCSkeletonTransmitter;
import cc.creativecomputing.util.logging.CCLog;

public class CCOpenNOKissDetector extends CCApp {
	
	private static final String TARGET_IP_NAME = "targetip";
	private static final String TARGET_PORT_NAME = "targetport";
	private static final String DEVICE_BUS_ID = "busid";
	private static final String ONIFILE = "onifile";
	private static final String MIRROR = "mirror";
	
	@CCControl(name = "rotate from plane")
	private boolean _cRotateFromFloorPlane = true;
	
	@CCControl(name = "translate from plane")
	private boolean _cTranslateFromFloorPlane = true;
	
	@CCControl(name = "openNI rotate X", min = -180, max = 180)
	private float _cRotateX = 0;
	
	@CCControl(name = "openNI rotate Y", min = -180, max = 180)
	private float _cRotateY = 0;
	
	@CCControl(name = "openNI rotate Z", min = -180, max = 180)
	private float _cRotateZ = 0;

	@CCControl(name = "openNI translate X", min = -5000f, max = 5000f)
	private float _cTranslateX = 0;

	@CCControl(name = "openNI translate Y", min = -5000f, max = 5000f)
	private float _cTranslateY = 0;

	@CCControl(name = "openNI translate Z", min = -5000f, max = 5000f)
	private float _cTranslateZ = 0;

	@CCControl(name = "floor plane y", min = -5000f, max = 5000f)
	private float _cFloorPlaneY = 0;

	@CCControl(name = "openNI scale", min = 0.1f, max = 10f)
	private float _cScale = 0;
	
	@CCControl(name = "draw textures")
	private boolean _cDrawTextures = false;
	
	@CCControl(name = "draw depth map skeleton")
	private boolean _cDrawDepthMap = false;
	
	@CCControl(name = "draw openni skeleton")
	private boolean _cDrawOpenNISkeleton = false;
	
	@CCControl(name = "draw cc skeleton")
	private boolean _cDrawCCSkeleton = false;
	
	@CCControl(name = "start", min = 0, max = 1)
	private float _cStart = 0;
	
	@CCControl(name = "end", min = 0, max = 1)
	private float _cEnd = 0;

	@CCControl(name = "update")
	private boolean _cUpdate = true;
	@CCControl(name = "log memery usage")
	private boolean _cLogMemoryUsage = true;
	
	private CCPlane3f _myPlane = new CCPlane3f(new CCVector3f(0,1,0),0);

	private String _myTargetIP = "127.0.0.1";
	private int _myTargetPort = 9000;
	private int _myKinectBusID = -1;
	private String _myPlayerFile = null;
	
	@CCControl(name = "arcball")
	private CCArcball _myArcball;

	private CCSkeletonTransmitter _mySender;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIPlayer _myPlayer;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIImageGenerator _myImageGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	private CCSkeletonManager _mySkeletonManager;
	private CCSkeletonInteractionArea _myInteractionArea;
	
	private CCOpenNISceneAnalyzer _mySceneGenerator;
	
	@CCControl(name = "skeleton provider")
	private CCOpenNISkeletonProvider _mySkeletonProvider;
	
	private CCVector3f _myTranslation = new CCVector3f();
	
	private CCText _myText;
	
	
	private class CCKissHeadPointCloud{

		private CCColor _myColor;
		private List<CCVector3f> _myHeadPoints = new ArrayList<CCVector3f>();
		
		private CCSkeleton _mySkeleton;
		
		public CCKissHeadPointCloud(CCSkeleton theSkeleton){
			_mySkeleton = theSkeleton;
			_myColor = CCColor.createFromHSB(theSkeleton.id() / 6f, 1f, 1f);
			_myHeadPoints = new ArrayList<CCVector3f>();
		}
		
		public void draw(CCGraphics g){
			g.color(_myColor);
			g.beginShape(CCDrawMode.POINTS);
			for(CCVector3f myPoint:_myHeadPoints){
				g.vertex(myPoint);
			}
			g.endShape();
		}
	}
	
	private class CCKiss{
		private float _myTime;
		
		private CCVector3f _myPos0;
		private CCVector3f _myPos1;
		
		private CCColor _myColor;
		
		private List<Float> _myHistory = new ArrayList<Float>();
		
		private float _myDistance = 0;
		
		public CCKiss(){
			_myColor = CCColor.random();
		}
		
		public void update(float theDeltaTime, CCVector3f theA, CCVector3f theB){
			_myPos0 = theA;
			_myPos1 = theB;
			
			_myDistance = _myPos0.distance(_myPos1);
			
				_myTime+= theDeltaTime;
			_myHistory.add(_myDistance);
		}
		
		public float distance(){
			return _myDistance;
		}
	}
	
	@CCControl(name = "min kiss distance", min = 0, max = 400)
	private float _cMinKissDistance = 0f;
	
	private class CCKissDetector implements CCSkeletonManagerListener{
		
		private CCKissHeadPointCloud[] _myHeadPointMap = new CCKissHeadPointCloud[6];
		
		private CCKiss _myKisses[][] = new CCKiss[6][6];
		
		private void resetKiss(int i, int j){
			if(_myKisses[i][j] == null)return;
				
			CCLog.info("ENDED KISS:" + _myKisses[i][j]._myTime);
			_myKisses[i][j] = null;
		}
		
		@Override
		public void update(float theDeltaTime) {
			CCVector3f[] myPoints = _myDepthGenerator.depthMapRealWorld(4);
			int[] myIDMap = _mySceneGenerator.idMap(4);
			
			for(int i = 0; i < _myHeadPointMap.length;i++){
				if(_myHeadPointMap[i] != null)_myHeadPointMap[i]._myHeadPoints.clear();
			}
			
			if(myPoints == null)return;
			for(int i = 0; i < myPoints.length;i++) {
				int myId = myIDMap[i];
				if(_myHeadPointMap[myId] == null){
					continue;
				}
				CCVector3f myPoint = myPoints[i];
				
				_myHeadPointMap[myId]._myHeadPoints.add(myPoint);
			}
			
			for(int i = 0; i < _myHeadPointMap.length - 1;i++){
				CCKissHeadPointCloud myCloudA = _myHeadPointMap[i];
				if(myCloudA == null){
					for(int j = 0; j < _myHeadPointMap.length;j++){
						resetKiss(i, j);
					}
					continue;
				}
				for(int j = i + 1; j < _myHeadPointMap.length;j++){
					CCKissHeadPointCloud myCloudB = _myHeadPointMap[j];
					if(myCloudB == null){
						resetKiss(i, j);
						continue;
					}
					float myDistance = myCloudA._mySkeleton.joint(CCSkeletonJointType.HEAD).position().distance(myCloudB._mySkeleton.joint(CCSkeletonJointType.HEAD).position());
					if(myDistance > _cMinKissDistance){
						resetKiss(i, j);
						continue;
					}
					if(_myKisses[i][j] == null){
						_myKisses[i][j] = new CCKiss();
					}
					_myKisses[i][j].update(theDeltaTime, myCloudA._mySkeleton.joint(CCSkeletonJointType.HEAD).position(), myCloudB._mySkeleton.joint(CCSkeletonJointType.HEAD).position());
					CCLog.info(myDistance + " " + _myKisses[i][j]);
				}
			}
		}

		@Override
		public void onNewSkeleton(CCSkeleton theSkeleton) {
			_myHeadPointMap[theSkeleton.id()] =  new CCKissHeadPointCloud(theSkeleton);
		}

		@Override
		public void onLostSkeleton(CCSkeleton theSkeleton) {
			_myHeadPointMap[theSkeleton.id()] =  null;
		}
		
		public void draw(CCGraphics g){
			for(int i = 0; i < _myHeadPointMap.length;i++){
				if(_myHeadPointMap[i] != null)_myHeadPointMap[i].draw(g);
			}
		}
		

		
		public void drawGraphs(CCGraphics g){
			for(int i = 0; i < 6; i++){
				for(int j = 0; j < 6; j++){
					if(_myKisses[i][j] == null)continue;
					float x = 0;

					g.color(_myKisses[i][j]._myColor);
					g.line(0,_cMinKissDistance - 200,_myKisses[i][j]._myHistory.size(), _cMinKissDistance - 200);
					g.beginShape(CCDrawMode.LINE_STRIP);
					for(float myDistance:_myKisses[i][j]._myHistory){
						g.vertex(x++, myDistance - 200);
//						CCLog.info(x+" : "+ myDistance );
					}
					g.endShape();
				}
			}
		}
	}

	private float _myMaxMem = 0;
	
	private CCKissDetector _myKissDetector;

	public void setup() {
		_myTargetIP = settings.getProperty(TARGET_IP_NAME, _myTargetIP);
		_myTargetPort = settings.getIntProperty(TARGET_PORT_NAME, _myTargetPort);
		_myKinectBusID = settings.getIntProperty(DEVICE_BUS_ID, _myKinectBusID);
		_myPlayerFile = settings.getProperty(ONIFILE, _myPlayerFile);
		
		_myArcball = new CCArcball(this);
		
		_myPlane.drawScale(5000);
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("humanoid.dae");
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		CCColladaSkeleton mySkeleton = new CCColladaSkeleton(
			mySkinController,
			myScene.node("bvh_import/Hips")
		);
		
		if(_myKinectBusID >= 0){
			_myOpenNI = new CCOpenNI(this, _myKinectBusID);
		}else{
			_myOpenNI = new CCOpenNI(this);
			if(_myPlayerFile != null){
				_myPlayer = _myOpenNI.openFileRecording(_myPlayerFile);
				_myPlayer.repeat(true);
			}
		}
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myImageGenerator = _myOpenNI.imageGenerator();
		_myUserGenerator = _myOpenNI.createUserGenerator();
		_mySceneGenerator = _myOpenNI.createSceneAnalyzer();
		if(settings.hasProperty(MIRROR)){
			_myOpenNI.mirror(true);
		}
		_myOpenNI.start();
		
		_mySkeletonProvider = new CCOpenNISkeletonProvider(_myUserGenerator);
		
		_mySkeletonManager = new CCSkeletonManager(
			this,
			mySkeleton, 
			_mySkeletonProvider
		);

		_myInteractionArea = new CCSkeletonInteractionArea(_mySkeletonManager);
		_mySender = new CCSkeletonTransmitter(new CCUDPOut<>(new CCNetXMLCodec(), _myTargetIP, _myTargetPort));
		_myInteractionArea.events().add(_mySender);
		_mySender.start();
		
		_myKissDetector = new CCKissDetector();
		_mySkeletonManager.events().add(_myKissDetector);
		
		addControls("openni","transform", 0, this);
		addControls("openni", "filter",2, _mySkeletonManager.filter());
		addControls("openni", "area",1, _myInteractionArea);
		
		_myText = new CCText(CCFontIO.createVectorFont("arial", 500));
	}
	
	@CCControl(name = "precalibrate from skeleton")
	public void precalibrateFromSkeleton(){
		
		if(_myInteractionArea.skeletons().size() < 1)return;
		
		CCSkeleton mySkeleton = _myInteractionArea.skeletons().get(0);
		
		CCSkeletonJoint myHeadJoint = mySkeleton.joint(CCSkeletonJointType.HEAD);
		CCSkeletonJoint myHipsJoint = mySkeleton.joint(CCSkeletonJointType.HIPS);
		CCSkeletonJoint myFootJoint = mySkeleton.joint(CCSkeletonJointType.LEFT_FOOT);
		
		CCVector3f myHeadPosition = _myOpenNI.transformationMatrix().transform(myHeadJoint.position().clone());
		CCVector3f myHipsPosition = _myOpenNI.transformationMatrix().transform(myHipsJoint.position().clone());
		CCVector3f myFootPosition = _myOpenNI.transformationMatrix().transform(myFootJoint.position().clone());
		
		CCVector3f myUpDirection = CCVecMath.subtract(myHeadPosition, myHipsPosition).normalize();
		float myXAngle = -CCVecMath.angle(new CCVector3f(0,myUpDirection.y,myUpDirection.z).normalize(), new CCVector3f(0,1,0));
		float myZAngle = -CCVecMath.angle(new CCVector3f(myUpDirection.x,myUpDirection.y,0).normalize(), new CCVector3f(0,1,0));
		float myHipFootDistance = myHipsPosition.y - myFootPosition.y;
		
		_cRotateX = CCMath.degrees(myXAngle);
		_cRotateZ = -CCMath.degrees(myZAngle);
		_myUI.readBackControl("openni","transform", "openNI rotate X");
		_myUI.readBackControl("openni","transform", "openNI rotate Z");
		
		float myYCorrection = CCMath.sin(myXAngle) * myHipsPosition.z;
		
		_cTranslateX = myHipsPosition.x;
		_cTranslateY = myHipsPosition.y + myYCorrection;
		_cTranslateZ = myHipsPosition.z;
		
		_myUI.readBackControl("openni","transform", "openNI translate X");
		_myUI.readBackControl("openni","transform", "openNI translate Y");
		_myUI.readBackControl("openni","transform", "openNI translate Z");
		
		_cFloorPlaneY = _cTranslateY - myHipFootDistance;
		_myUI.readBackControl("openni","transform", "floor plane y");
		
		_myInteractionArea.area(
			_cTranslateX - 500, _cTranslateZ + 500, 
			_cTranslateX + 500, _cTranslateZ + 500, 
			_cTranslateX + 500, _cTranslateZ - 500, 
			_cTranslateX - 500, _cTranslateZ - 500
		);
		
		_myUI.readBackControl("openni", "area", "x1");
		_myUI.readBackControl("openni", "area", "z1");
		_myUI.readBackControl("openni", "area", "x2");
		_myUI.readBackControl("openni", "area", "z2");
		_myUI.readBackControl("openni", "area", "x3");
		_myUI.readBackControl("openni", "area", "z3");
		_myUI.readBackControl("openni", "area", "x4");
		_myUI.readBackControl("openni", "area", "z4");
	}

	@Override
	public void update(float theDeltaTime) {
		if(_cLogMemoryUsage){
			_myMaxMem = CCMath.max(_myMaxMem, CCSystem.memoryInUse() / 1000);
		}
		_myPlane.constant(_cFloorPlaneY);
		
		if(_myPlayer != null)_myPlayer.loop((int)(_cStart * _myPlayer.numberOfFrames()), (int)(_cEnd * _myPlayer.numberOfFrames()));
		_myOpenNI.transformationMatrix().reset();
		
		if(_cRotateFromFloorPlane){
			CCVector3f myRotationVector = _mySceneGenerator.floorNormal().cross(new CCVector3f(0,1,0)).normalize();
			float myAngle = -CCVecMath.angle(_mySceneGenerator.floorNormal(), new CCVector3f(0,1,0));
			_myOpenNI.transformationMatrix().rotate(myAngle, myRotationVector);
		}
		
		_myOpenNI.transformationMatrix().rotateX(CCMath.radians(_cRotateX));
		_myOpenNI.transformationMatrix().rotateZ(CCMath.radians(_cRotateZ));
		
		if(_cTranslateFromFloorPlane){
			_myTranslation.set(_mySceneGenerator.floorPoint());
		}else{
			_myTranslation.set(_cTranslateX, _cTranslateY, _cTranslateZ);
		}
		_myOpenNI.transformationMatrix().translate(_myTranslation);
		_myOpenNI.transformationMatrix().scale(_cScale);
		_myOpenNI.transformationMatrix().rotateY(CCMath.radians(_cRotateY));
		
		_mySkeletonManager.update(theDeltaTime);
		
		_myInteractionArea.scale(_cScale);
		_myInteractionArea.translation(-_myTranslation.x, -_myTranslation.y, -_myTranslation.z);
		_myInteractionArea.bottomY(_cFloorPlaneY);
		_myInteractionArea.update(theDeltaTime);
		
		_mySender.update(theDeltaTime);
		

//		CCLog.info(1 / theDeltaTime);
	}
	
	public void drawDebug(CCGraphics g){
		g.pushMatrix();
		g.scale(_cScale);
		g.translate(-_myTranslation.x, -_myTranslation.y, -_myTranslation.z);
		g.color(200, 200, 0, 50);
		_myPlane.draw(g);
		g.popMatrix();
		
		if(_cDrawDepthMap){
//			CCVector3f[] myPoints = _myDepthGenerator.depthMapRealWorld(4);
//			int[] myIDMap = _mySceneGenerator.idMap(4);
//			g.color(255);
//			g.beginShape(CCDrawMode.POINTS);
//			for(int i = 0; i < myPoints.length;i++) {
//				int myId = myIDMap[i];
//				CCVector3f myPoint = myPoints[i];
//				if(myId > 0)g.color(255,0,0);
//				else g.color(255);
//					
//				g.vertex(myPoint);
//			}
//			g.endShape();
			
			_myKissDetector.draw(g);
		}
			
		g.color(255,0,0);
		_myInteractionArea.draw(g);
		
		g.color(1f,0f,0f);
		g.line(0,0,0,1000,0,0);
		g.color(0f,1f,0f);
		g.line(0,0,0,0,1000,0);
		g.color(0f,0f,1f);
		g.line(0,0,0,0,0,1000);
			
		if(_cDrawOpenNISkeleton){
			for (CCOpenNIUser myUser : _myUserGenerator.user()) {
				g.color(1f, 1f);
				myUser.drawSkeleton(g);
				g.color(0f,1f,0,1f);
				myUser.boundingBox().draw(g);
				
			}
		}
		
		if(_cDrawCCSkeleton){
			for (CCSkeleton mySkeleton : _myInteractionArea.skeletons()) {
				g.color(1f, 1f);
				mySkeleton.draw(g);
				_myText.text(mySkeleton.id());
				_myText.position().set(mySkeleton.joint(CCSkeletonJointType.HEAD).position());
				_myText.draw(g);
			}
		}
		
		_mySkeletonProvider.legFixer().draw(g);
			
		_myOpenNI.drawCamFrustum(g);
//		_mySceneGenerator.floorPlane().draw(g);
	}

	public void drawTextures(CCGraphics g) {
		if(_cDrawTextures) {
			g.color(255);
			g.image(_myDepthGenerator.texture(), -width/2, -height / 2);
			g.image(_myImageGenerator.texture(), -width/2 + _myDepthGenerator.width(), -height / 2);
			g.image(_mySceneGenerator.texture(), -width/2 + _myDepthGenerator.width() + _myImageGenerator.width(), -height / 2);
		}
	}

	public void draw() {
		g.clearColor(0, 0, 0);
		g.clear();
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.scale(0.1f);
		drawDebug(g);
		g.popMatrix();

		drawTextures(g);
		
		_myKissDetector.drawGraphs(g);
		
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_R:
//			_myArcball.reset();
			break;
		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNOKissDetector.class);
//		myManager.settings().addParameter("targetip", true, "target ip for skeleton data");
//		myManager.settings().addParameter("targetport", true, "target port for skeleton data");
//		myManager.settings().addParameter("deviceid", true, "id of the device to use");
//		myManager.settings().addParameter("onifile", true, "path of the oni file to open");

		myManager.settings().size(1500, 900);
		myManager.settings().vsync(false);
//		myManager.settings().displayMode(CCDisplayMode.UPDATE_ONLY);
//		myManager.settings().frameRate(30);
//		myManager.settings().size(500, 500);
//		myManager.settings().antialiasing(8);
		
		myManager.settings(args);
		
		myManager.start();
	}
}
