package cc.creativecomputing.demo.cv.openni.skeleton;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings.CCDisplayMode;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.cv.openni.CCOpenNI;
import cc.creativecomputing.cv.openni.CCOpenNIDepthGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIImageGenerator;
import cc.creativecomputing.cv.openni.CCOpenNIPlayer;
import cc.creativecomputing.cv.openni.CCOpenNIUser;
import cc.creativecomputing.cv.openni.CCOpenNIUserGenerator;
import cc.creativecomputing.cv.openni.skeleton.CCOpenNISkeletonProvider;
import cc.creativecomputing.cv.openni.util.CCOpenNIFloorPlaneDetector;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSkeleton;
import cc.creativecomputing.model.collada.CCColladaSkinController;
import cc.creativecomputing.net.CCUDPOut;
import cc.creativecomputing.net.codec.CCNetXMLCodec;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonManager;
import cc.creativecomputing.skeleton.util.CCSkeletonInteractionArea;
import cc.creativecomputing.skeleton.util.CCSkeletonTransmitter;
import cc.creativecomputing.util.CCCommandLineParser;
import cc.creativecomputing.util.CCStopWatch;

public class CCOpenNISkeletonSenderDemo extends CCApp {
	
	private static String TARGET_IP = "127.0.0.1";
	private static int TARGET_PORT = 9000;
	private static int KINECT_ID = -1;
	private static String PLAYER_FILE = null;
	
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

	@CCControl(name = "openNI scale", min = 0.1f, max = 10f)
	private float _cScale = 0;
	
	@CCControl(name = "use floor plane detector")
	private boolean _cUseFloorPlaneDetector = true;
	
	@CCControl(name = "draw textures")
	private boolean _cDrawTextures = false;
	
	@CCControl(name = "draw openni skeleton")
	private boolean _cDrawOpenNISkeleton = false;
	
	@CCControl(name = "draw cc skeleton")
	private boolean _cDrawCCSkeleton = false;
	
	@CCControl(name = "start", min = 0, max = 1)
	private float _cStart = 0;
	
	@CCControl(name = "end", min = 0, max = 1)
	private float _cEnd = 0;
	
	
	
	private CCArcball _myArcball;

	private CCSkeletonTransmitter _mySender;
	
	private CCOpenNI _myOpenNI;
	private CCOpenNIPlayer _myPlayer;
	private CCOpenNIDepthGenerator _myDepthGenerator;
	private CCOpenNIImageGenerator _myImageGenerator;
	private CCOpenNIUserGenerator _myUserGenerator;
	
	private CCOpenNIFloorPlaneDetector _myFloorPlaneDetector;
	
	private CCSkeletonManager _mySkeletonManager;
	private CCSkeletonInteractionArea _myInteractionArea;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		CCColladaLoader myColladaLoader = new CCColladaLoader("humanoid.dae");
		
		CCColladaScene myScene = myColladaLoader.scenes().element(0);
		CCColladaSkinController mySkinController = myColladaLoader.controllers().element(0).skin();
		
		CCColladaSkeleton mySkeleton = new CCColladaSkeleton(
			mySkinController,
			myScene.node("bvh_import/Hips")
		);
	
		if(KINECT_ID >= 0){
			_myOpenNI = new CCOpenNI(this, KINECT_ID);
		}else{
			_myOpenNI = new CCOpenNI(this);
			if(PLAYER_FILE != null){
				_myPlayer = _myOpenNI.openFileRecording(PLAYER_FILE);
				_myPlayer.repeat(true);
			}
		}
		_myDepthGenerator = _myOpenNI.createDepthGenerator();
		_myImageGenerator = _myOpenNI.imageGenerator();
		_myUserGenerator = _myOpenNI.createUserGenerator();
//		_myOpenNI.mirror(true);
		_myOpenNI.start();
		
		_mySkeletonManager = new CCSkeletonManager(
			this,
			mySkeleton, 
			new CCOpenNISkeletonProvider(_myUserGenerator)
		);
		
		_myFloorPlaneDetector = new CCOpenNIFloorPlaneDetector(_myOpenNI);
		addControls("openni", "floorplane", 3,_myFloorPlaneDetector);

		_myInteractionArea = new CCSkeletonInteractionArea(_mySkeletonManager);
		_mySender = new CCSkeletonTransmitter(new CCUDPOut<>(new CCNetXMLCodec(), TARGET_IP, TARGET_PORT));
		_myInteractionArea.events().add(_mySender);
		_mySender.start();
		

		addControls("openni","transform", 0, this);
		addControls("openni", "filter",2, _mySkeletonManager.filter());
		addControls("openni", "area",1, _myInteractionArea);
		addControls("stopwatch", "stopwatch", CCStopWatch.instance());
	}

	@Override
	public void update(float theDeltaTime) {
		
		_myPlayer.loop((int)(_cStart * _myPlayer.numberOfFrames()), (int)(_cEnd * _myPlayer.numberOfFrames()));
		_myFloorPlaneDetector.update(theDeltaTime);
		_myOpenNI.transformationMatrix().reset();
		if(_cUseFloorPlaneDetector) {
			_myOpenNI.transformationMatrix().set(_myFloorPlaneDetector.transformation());
			_myOpenNI.transformationMatrix().translate(_cTranslateX, _cTranslateY, _cTranslateZ);
			_myOpenNI.transformationMatrix().scale(_cScale);
		}else {
			_myOpenNI.transformationMatrix().reset();
			_myOpenNI.transformationMatrix().rotateX(CCMath.radians(_cRotateX));
			_myOpenNI.transformationMatrix().rotateY(CCMath.radians(_cRotateY));
			_myOpenNI.transformationMatrix().rotateZ(CCMath.radians(_cRotateZ));
			_myOpenNI.transformationMatrix().translate(_cTranslateX, _cTranslateY, _cTranslateZ);
			_myOpenNI.transformationMatrix().scale(_cScale);
		}
		
		CCStopWatch.instance().startWatch("skeleton manager");
		_mySkeletonManager.update(theDeltaTime);
		CCStopWatch.instance().endWatch("skeleton manager");
		
		CCStopWatch.instance().startWatch("interaction area");
		_myInteractionArea.update(theDeltaTime);
		CCStopWatch.instance().endWatch("interaction area");
		
		CCStopWatch.instance().startWatch("sender");
		_mySender.update(theDeltaTime);
		CCStopWatch.instance().endWatch("sender");
		
		CCStopWatch.instance().update(theDeltaTime);
	}
	
	public void drawDebug(CCGraphics g){
		CCVector3f[] myPoints = _myDepthGenerator.depthMapRealWorld(4);
		g.color(255);
		g.beginShape(CCDrawMode.POINTS);
		for(CCVector3f myPoint:myPoints) {
			g.vertex(myPoint);
		}
		g.endShape();
			
		g.color(255,0,0);
		_myInteractionArea.draw(g);
		_myFloorPlaneDetector.draw(g);
		
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
			for (CCSkeleton mySkeleton : _mySkeletonManager.skeletons()) {
				g.color(1f, 1f);
				mySkeleton.draw(g);
			}
		}
			
		_myOpenNI.drawCamFrustum(g);
	}

	public void drawTextures(CCGraphics g) {
		if(_cDrawTextures) {
			g.color(255);
			g.image(_myDepthGenerator.texture(), -_myDepthGenerator.width(), -_myDepthGenerator.height() / 2);
			g.image(_myImageGenerator.texture(), 0, -_myImageGenerator.height() / 2);
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

		CCStopWatch.instance().draw(g);
	}

	public static void main(String[] args) {
		
		CCCommandLineParser myCommandLineParser = new CCCommandLineParser(CCOpenNISkeletonSenderDemo.class.getName());
		myCommandLineParser.addParameter("targetip", true, "target ip for skeleton data");
		myCommandLineParser.addParameter("targetport", true, "target port for skeleton data");
		myCommandLineParser.addParameter("deviceid", true, "id of the device to use");
		myCommandLineParser.addParameter("onifile", true, "path of the oni file to open");
		myCommandLineParser.addParameter("updateonly", false, "changes the app to run without window");
		myCommandLineParser.parse(args);
		
		if(myCommandLineParser.hasOption("i")){
			TARGET_IP = myCommandLineParser.optionValue("i");
		}
		if(myCommandLineParser.hasOption("p")){
			TARGET_PORT = Integer.parseInt(myCommandLineParser.optionValue("p"));
		}
		if(myCommandLineParser.hasOption("d")){
			KINECT_ID = Integer.parseInt(myCommandLineParser.optionValue("d"));
		}
		if(myCommandLineParser.hasOption("o")){
			PLAYER_FILE = myCommandLineParser.optionValue("o");
		}
		
		CCApplicationManager myManager = new CCApplicationManager(CCOpenNISkeletonSenderDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		if(myCommandLineParser.hasOption("u")){
			myManager.settings().displayMode(CCDisplayMode.UPDATE_ONLY);
		}
		myManager.start();
	}
}
