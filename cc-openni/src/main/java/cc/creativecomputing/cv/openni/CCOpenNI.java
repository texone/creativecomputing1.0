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
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.openni.Context;
import org.openni.Device;
import org.openni.GeneralException;
import org.openni.License;
import org.openni.NodeInfo;
import org.openni.NodeInfoList;
import org.openni.NodeType;
import org.openni.Query;
import org.openni.StatusException;

import com.jogamp.common.os.NativeLibrary;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCDisposeListener;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.util.CCNativeLibUtil;
import cc.creativecomputing.util.CCStopWatch;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 *
 */
public class CCOpenNI implements Runnable, CCDisposeListener, CCUpdateListener{
	
	public static interface CCOpenNIListener{
		public void lostDevice();
	}
	
	static boolean DEBUG = false;
	static boolean MEASURE = true;
	
	Context _myContext;
	
	private Thread _myThread;
	
	private boolean _myIsRunning = false;
	
	private List<CCOpenNIGenerator<?>> _myGenerators = new ArrayList<CCOpenNIGenerator<?>>();
	
	private CCListenerManager<CCOpenNIListener> _myEvents = CCListenerManager.create(CCOpenNIListener.class);
	
	private CCMatrix4f _myTransformationMatrix;
	
	/**
	 * int main()
{
        XnStatus nRetVal = XN_STATUS_OK;

        // Getting Sensors information and configure all sensors
        DepthRgbSensors sensors[NUM_OF_SENSORS];
        nRetVal = g_Context.Init();

        NodeInfoList devicesList;
        int devicesListCount = 0;
        nRetVal = g_Context.EnumerateProductionTrees(XN_NODE_TYPE_DEVICE,NULL, devicesList);
        for (NodeInfoList::Iterator it = devicesList.Begin(); it != devicesList.End(); ++it)
        {
                devicesListCount++;
        }
        CHECK_RC(nRetVal, "Enumerate");
        int i=0;
        for (NodeInfoList::Iterator it = devicesList.Begin(); it !=devicesList.End(); ++it, ++i)
        {
                // Create the device node
                NodeInfo deviceInfo = *it;
                nRetVal = g_Context.CreateProductionTree(deviceInfo);
                CHECK_RC(nRetVal, "Create Device");

                // create a query to depend on this node
                Query query;
                query.AddNeededNode(deviceInfo.GetInstanceName());

                // Copy the device name
                xnOSMemCopy(sensors[i].name,deviceInfo.GetInstanceName(), xnOSStrLen(deviceInfo.GetInstanceName()));
                // now create a depth generator over this device
                nRetVal = g_Context.CreateAnyProductionTree(XN_NODE_TYPE_DEPTH, &query, sensors[i].depth);
                CHECK_RC(nRetVal, "Create Depth");
        }

        g_Context.StartGeneratingAll();

        XnFPSData xnFPS;
        nRetVal = xnFPSInit(&xnFPS, 180);
        CHECK_RC(nRetVal, "FPS Init");

        while (!xnOSWasKeyboardHit())
        {
                for(i = 0 ; i < NUM_OF_SENSORS ; ++i)
                {
                        printf("Sensor [%d] output:\n",i);
                        UpdateCommon(sensors[i]);
                        xnFPSMarkFrame(&xnFPS);
                        // Print Depth central pixel
                        const DepthMetaData *dmd = &sensors[i].depthMD;
                        const XnDepthPixel* pDepthMap = sensors[i].depthMD.Data();
                        printf("Depth frame [%d] Middle point is: %u. FPS: %f\n", dmd-
>FrameID(), sensors[i].depthMD(dmd->XRes() / 2, dmd->YRes() / 2),
xnFPSCalc(&xnFPS));
                }
        }

        closeDevice();

        return 0;
}
	 * @param theApp
	 */
	
	private Query _myDeviceQuery = null;

	/**
	 * Creates openni context with kinect connected on usbbus: busid.
	 * @param theBusId busid of kinect.
	 */

	public CCOpenNI(CCApp theApp, int theBusId) {
		
		CCNativeLibUtil.prepareLibraryForLoading (Context.class, "OpenNI");
		theApp.addUpdateListener(this);
		theApp.addDisposeListener(this);
	
		_myTransformationMatrix = new CCMatrix4f();
		_myTransformationMatrix.rotateY(CCMath.PI);
		
		try {
			NativeLibrary.open("OpenNI",Context.class.getClassLoader());
			CCNativeLibUtil.prepareLibraryForLoading(Context.class, "OpenNI");
			_myContext = new Context();
			License licence = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");   // vendor, key
			_myContext.addLicense(licence); 
			
			if(theBusId < 0) return;
			
			NodeInfoList myList = _myContext.enumerateProductionTrees(NodeType.DEVICE);
			NodeInfo myDeviceNode = null;
			CCLog.info("Kinect DeviceInfo, i am looking for kinect on busid: " + theBusId);
			for(NodeInfo myInfo:myList){
				CCLog.info("-------");
				String myCreationInfo = myInfo.getCreationInfo();
			    Scanner s = new Scanner(myCreationInfo);
			    s.useDelimiter("\\s*/\\s*");
				String myVendorId =  s.next();
				String myTmp = s.next();
			    Scanner t = new Scanner(myTmp);
			    t.useDelimiter("\\s*@\\s*");
				String myProductId = t.next();
				int myBus = Integer.parseInt(t.next());
				String myAdress = s.next();
				s.close();
				t.close();
				
				CCLog.info("Creationinfo: " + myCreationInfo);
				CCLog.info("Vendor: " + myVendorId + ", Product:" + myProductId + ", Bus: " + myBus + ", Adress: " + myAdress);
				if (myBus == theBusId) {
					CCLog.info("..... got it");
					myDeviceNode = myInfo;
					break;
				}
			}
			if (myDeviceNode == null) {
				throw new CCOpenNIException("Can not find device on bus" + theBusId);
			}
			
			_myDeviceQuery = new Query();
			_myDeviceQuery.addNeededNode(_myContext.createProductionTree(myDeviceNode));
			
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
		
	}
	
	public CCOpenNI(CCApp theApp){
		this(theApp,-1);
	}
	
	public CCListenerManager<CCOpenNIListener> events(){
		return _myEvents;
	}
	
	Context context(){
		return _myContext;
	}
	
	Query deviceQuery(){
		return _myDeviceQuery;
	}
	
	public CCMatrix4f transformationMatrix() {
		return _myTransformationMatrix;
	}
	
	/**
	 * Make sure all generators are generating data.
	 */
	public void start() {
		try {
			_myIsRunning = true;
			_myContext.startGeneratingAll();
			_myThread = new Thread(this);
			_myThread.start();
		} catch (Exception e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Stop all generators from generating data.
	 */
	public void stop() {
		try {
			_myContext.stopGeneratingAll();
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	/**
	 * Sets the global mirror flag. This will set all current existing nodes' mirror state, 
	 * and also affect future created nodes. The default mirror flag is FALSE.
	 * @param theMirror New Mirror state.
	 */
	public void mirror(boolean theMirror) {
		try {
			_myContext.setGlobalMirror(theMirror);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	public CCOpenNIRecorder createRecorder() {
		return new CCOpenNIRecorder(this);
	}
	
	private CCOpenNIPlayer _myPlayer = null;;
	
	/**
	 * Opens a recording file, adding all nodes in it to the context.
	 * @param theFile The file to open.
	 * @return The created player node.
	 */
	public CCOpenNIPlayer openFileRecording(String theFile) {
		try {
			_myPlayer = new CCOpenNIPlayer(_myContext.openFileRecordingEx(CCIOUtil.dataPath(theFile)));
			return _myPlayer;
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}
	}
	
	private CCOpenNIImageGenerator _myImageGenerator;
	
	public CCOpenNIImageGenerator imageGenerator() {
		if(_myImageGenerator == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_myImageGenerator = new CCOpenNIImageGenerator(this);
			if(_myPlayer != null)_myPlayer.generator(_myImageGenerator.generator());
			_myGenerators.add(_myImageGenerator);
		}
		return _myImageGenerator;
	}
	
	public boolean isImageGeneratorOn() {
		return _myImageGenerator != null;
	}
	
	private CCOpenNIIRGenerator _myIRGenerator;
	
	public CCOpenNIIRGenerator irGenerator() {
		if(_myIRGenerator == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_myIRGenerator = new CCOpenNIIRGenerator(this);
			if(_myPlayer != null)_myPlayer.generator(_myIRGenerator.generator());
			_myGenerators.add(_myIRGenerator);
		}
		return _myIRGenerator;
	}
	
	public boolean isIRGeneratorOn() {
		return _myIRGenerator != null;
	}
	
	private CCOpenNIDepthGenerator _myDepthGenerator;
	
	public CCOpenNIDepthGenerator createDepthGenerator() {
		if(_myDepthGenerator == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_myDepthGenerator = new CCOpenNIDepthGenerator(this);
			if(_myPlayer != null)_myPlayer.generator(_myDepthGenerator.generator());
			_myGenerators.add(_myDepthGenerator);
		}
		return _myDepthGenerator;
	}
	
	public boolean isDepthGeneratorOn() {
		return _myDepthGenerator != null;
	}
	
	private CCOpenNISceneAnalyzer _mySceneAnalyzer;
	
	public CCOpenNISceneAnalyzer createSceneAnalyzer() {
		if(_mySceneAnalyzer == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_mySceneAnalyzer = new CCOpenNISceneAnalyzer(this);
			_myGenerators.add(_mySceneAnalyzer);
		}
		return _mySceneAnalyzer;
	}
	
	private CCOpenNIHandGenerator _myHandGenerator;
	
	public CCOpenNIHandGenerator createHandGenerator() {
		if(_myHandGenerator == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_myHandGenerator = new CCOpenNIHandGenerator(this);
			_myGenerators.add(_myHandGenerator);
		}
		return _myHandGenerator;
	}
	
	private CCOpenNIGestureGenerator _myGestureGenerator;
	
	public CCOpenNIGestureGenerator createGestureGenerator() {
		if(_myGestureGenerator == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_myGestureGenerator = new CCOpenNIGestureGenerator(this);
			_myGenerators.add(_myGestureGenerator);
		}
		return _myGestureGenerator;
	}
	
	private CCOpenNIUserGenerator _myUserGenerator;
	
	public CCOpenNIUserGenerator createUserGenerator() {
		if(_myUserGenerator == null) {
			if(_myIsRunning) {
				throw new CCOpenNIException("You can not create generators after OpenNI is started. Make sure you at least call this method once before calling CCOpenNI.start().");
			}
			_myUserGenerator = new CCOpenNIUserGenerator(this);
			_myGenerators.add(_myUserGenerator);
			createDepthGenerator();
		}
		return _myUserGenerator;
	}
	
	private double _myTimeWithoutUpdate = 0;
	private double _myLastSpendTime = 0;
	
	public void update(float theDeltaTime) {
		if(_mySpendTime != _myLastSpendTime){
			_myLastSpendTime = _mySpendTime;
			_myTimeWithoutUpdate = 0;
		}else{
			_myTimeWithoutUpdate += theDeltaTime;
			if(_myTimeWithoutUpdate > 10){
				_myEvents.proxy().lostDevice();
			}
		}
		if(!_myIsUpdated)return;
		
//		if(MEASURE)CCStopWatch.instance().startWatch("player");
		if(_myPlayer!= null)_myPlayer.update(theDeltaTime);
//		if(MEASURE)CCStopWatch.instance().endWatch("player");
		_myIsUpdated = false;
		
//		if(MEASURE)CCStopWatch.instance().startWatch("openni generator");
		for(CCOpenNIGenerator<?> myGenerator:_myGenerators) {
			myGenerator.update((float)_mySpendTime);
		}
//		if(MEASURE)CCStopWatch.instance().endWatch("openni generator");
	}
	
	public void dispose() {
		_myIsRunning = false;
	}
	
//	private long myLastNanos;
	private boolean _myIsUpdated = false;
	
	private long _myLastNanos = System.nanoTime();
	private double _mySpendTime = 0;
	
	public void run() {
//		myLastNanos = System.nanoTime();
		while(_myIsRunning) {
			try {
				_myContext.waitAndUpdateAll();
//				long myNewNanos = System.nanoTime();
//				long mySpentTime = myNewNanos - myLastNanos;
//				myLastNanos = myNewNanos;
				if(_myIsUpdated == false) {
					_mySpendTime = 0;
				}
				_mySpendTime += (System.nanoTime() - _myLastNanos) * 1e-9;
				_myLastNanos = System.nanoTime();
				_myIsUpdated = true;
			} catch (StatusException e) {
				
			}
			
		}
	}
	
	/**
     *  Helper method that draw the 3d camera and the frustum of the camera
     * 
     */          
	public void drawCamFrustum(CCGraphics g) {
		createDepthGenerator();
		// g.pushStyle();
		g.pushMatrix();
		g.applyMatrix(_myTransformationMatrix.clone().invert());

		// draw cam case
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex( 270 * 0.5f,  40 * 0.5f,   0.0f);
		g.vertex(-270 * 0.5f,  40 * 0.5f,   0.0f);
		g.vertex(-270 * 0.5f, -40 * 0.5f,   0.0f);
		g.vertex( 270 * 0.5f, -40 * 0.5f,   0.0f);
		g.endShape();

		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex( 220 * 0.5f,  40 * 0.5f,  -50.0f);
		g.vertex(-220 * 0.5f,  40 * 0.5f,  -50.0f);
		g.vertex(-220 * 0.5f, -40 * 0.5f,  -50.0f);
		g.vertex( 220 * 0.5f, -40 * 0.5f,  -50.0f);
		g.endShape();

		g.beginShape(CCDrawMode.LINES);
		g.vertex( 270 * 0.5f,  40 * 0.5f,   0.0f);
		g.vertex( 220 * 0.5f,  40 * 0.5f,  -50.0f);

		g.vertex(-270 * 0.5f,  40 * 0.5f,   0.0f);
		g.vertex(-220 * 0.5f,  40 * 0.5f,  -50.0f);

		g.vertex(-270 * 0.5f, -40 * 0.5f,   0.0f);
		g.vertex(-220 * 0.5f, -40 * 0.5f,  -50.0f);

		g.vertex( 270 * 0.5f, -40 * 0.5f,   0.0f);
		g.vertex( 220 * 0.5f, -40 * 0.5f,  -50.0f);
		g.endShape();

		// draw cam opening angles
		g.color(200, 200, 0, 50);
		g.line(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1000.0f);

		// calculate the angles of the cam, values are in radians, radius is 10m
		float distDepth = 10000;

		float valueH = distDepth * CCMath.tan(_myDepthGenerator.horizontalFieldOfView() * .5f);
		float valueV = distDepth * CCMath.tan(_myDepthGenerator.verticalFieldOfView() * .5f);

		g.line(0.0f, 0.0f, 0.0f, valueH, valueV, distDepth);
		g.line(0.0f, 0.0f, 0.0f, -valueH, valueV, distDepth);
		g.line(0.0f, 0.0f, 0.0f, valueH, -valueV, distDepth);
		g.line(0.0f, 0.0f, 0.0f, -valueH, -valueV, distDepth);
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(valueH, valueV, distDepth);
		g.vertex(-valueH, valueV, distDepth);
		g.vertex(-valueH, -valueV, distDepth);
		g.vertex(valueH, -valueV, distDepth);
		g.endShape();

		g.popMatrix();
	}
	
	/////////////////////////////////////////////////
	//
	// MATH STUFF
	//
	/////////////////////////////////////////////////

	@CCControl(name = "scaleX", min = 500, max = 700)
	public static float scaleX = 594.21434211923247f;
	@CCControl(name = "scaleY", min = 500, max = 700)
	public static float scaleY = 591.04053696870778f;
	
	@CCControl(name = "centerX", min = 300, max = 400)
	public static float centerX = 339.30780975300314f;
	@CCControl(name = "centerY", min = 200, max = 300)
	public static float centerY = 242.73913761751615f;
	
	
}
