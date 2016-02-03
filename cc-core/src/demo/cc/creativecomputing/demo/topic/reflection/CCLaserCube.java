package cc.creativecomputing.demo.topic.reflection;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane3f;
import cc.creativecomputing.math.CCRay3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.logging.CCLog;

public class CCLaserCube extends CCApp {
	
	@CCControl(name = "box size", min = 0, max = 1000)
	private float _cBoxSize = 100;
	
	private List<CCPlane3f> _myPLanes = new ArrayList<>();
	
	@CCControl(name = "laser x0", min = -1, max = 1)
	private float _cLaserX0 = 1;
	@CCControl(name = "laser x1", min = -1, max = 1)
	private float _cLaserX1 = 1;
	
	@CCControl(name = "lasers x", min = 1, max = 20)
	private int _cLasersX = 1;
	
	@CCControl(name = "lasers y", min = 1, max = 20)
	private int _cLasersY = 1;
	
	@CCControl(name = "lasers space", min = 1, max = 50)
	private float _cSpace = 1;
	
	@CCControl(name = "lasers speed", min = 0, max = 1)
	private float _cSpeed = 1;
	
	@CCControl(name = "laser y", min = -1, max = 1)
	private float _cLaserY = 1;
	@CCControl(name = "laser z", min = -1, max = 1)
	private float _cLaserZ = 1;
	@CCControl(name = "laser pos z", min = -400, max = 400)
	private float _cLaserPosZ = 1;
	
	@CCControl(name = "reflections", min = 1, max = 100)
	private int _cReflections = 1;
	
	@CCControl(name = "plane alpha", min = 0, max = 1f)
	private float _cPlaneAlpha = 0;
	
	@CCControl(name = "line alpha start", min = 0, max = 1f)
	private float _cLineAlphaStart = 0;
	@CCControl(name = "line alpha end", min = 0, max = 1f)
	private float _cLineAlphaEnd = 0;
	
	private CCArcball _myArcball;
	
	@CCControl(name = "noise")
	private CCSimplexNoise _myNoise = new CCSimplexNoise();
	
	@CCControl(name = "noise amount", min = 0, max = 1f)
	private float _cNoiseAmount = 0f;

	@Override
	public void setup() {
		_myPLanes.add(new CCPlane3f(new CCVector3f(-100,   0,    0), new CCVector3f( 1,  0,  0)));
		_myPLanes.add(new CCPlane3f(new CCVector3f( 100,   0,    0), new CCVector3f(-1,  0,  0)));
		_myPLanes.add(new CCPlane3f(new CCVector3f(   0,-100,    0), new CCVector3f( 0,  1,  0)));
		_myPLanes.add(new CCPlane3f(new CCVector3f(   0, 100,    0), new CCVector3f( 0, -1,  0)));
		_myPLanes.add(new CCPlane3f(new CCVector3f(   0,   0, -100), new CCVector3f( 0,  0, -1)));
		_myPLanes.add(new CCPlane3f(new CCVector3f(   0,   0,  100), new CCVector3f( 0,  0,  1)));
		_myArcball = new CCArcball(this);
		
		addControls("app", "app", this);
	}
	
	private List<CCVector3f> _myLinePoints = new ArrayList<>();
	
	private float _myAngle = 0;

	@Override
	public void update(final float theDeltaTime) {
		for(CCPlane3f myPlane:_myPLanes){
			myPlane.setOriginNormal(myPlane.normal().clone().scale(_cBoxSize / 2), myPlane.normal());
		}
		
		_myAngle += theDeltaTime * _cSpeed * 1000;
		_myLinePoints.clear();
		
		float lWIdth = (_cLasersX - 1) * _cSpace / 2;
		float lHeight = (_cLasersY - 1) * _cSpace / 2;
		for (float x = 0; x < _cLasersX; x++) {
			float myX = CCMath.map(x, 0, _cLasersX, -lWIdth, lWIdth);
			for (float y = 0; y < _cLasersY; y++) {

				float myY = CCMath.map(y, 0, _cLasersY, -lWIdth, lWIdth);
				float[]myNoises = _myNoise.values(myX, myY, _myAngle);
				CCVector3f myNoise = new CCVector3f(myNoises[0] - 0.5f, myNoises[1] - 0.5f, myNoises[2] - 0.5f).normalize(_cNoiseAmount);
				CCRay3f myRay = new CCRay3f(new CCVector3f(myX, _cLaserPosZ, myY), new CCVector3f(_cLaserX0 + myNoise.x, _cLaserY + myNoise.y, _cLaserZ + myNoise.z).normalize());
				// CCLog.info(myX);
				_myLinePoints.add(myRay.origin());

				for (int i = 0; i < _cReflections; i++) {
					float myDist = Float.MAX_VALUE;

					CCVector3f myLastIntersection = null;
					CCVector3f myNormal = null;
					for (CCPlane3f myPlane : _myPLanes) {

						CCVector3f myInterSection = myPlane.intersection(myRay);
						if (myInterSection == null)
							continue;

						float myCurrentDist = myRay.origin().distance(myInterSection);
						if (myCurrentDist < myDist) {
							myDist = myCurrentDist;
							myLastIntersection = myInterSection;
							myNormal = myPlane.normal();
						}
					}
					_myLinePoints.add(myLastIntersection.clone());
					if (i < _cReflections - 1)
						_myLinePoints.add(myLastIntersection.clone());
					CCVector3f myReflection = CCVecMath.reflect(myRay.direction(), myNormal);
					myRay = new CCRay3f(myLastIntersection.add(myReflection.clone().scale(0.0001f)), myReflection);
				}
			}
		}
		g.pointSize(5);
	}

	@Override
	public void draw() {
		g.clear();
		
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(1f,_cPlaneAlpha);
		if(_cPlaneAlpha > 0){
			for(CCPlane3f myPlane:_myPLanes){
				myPlane.draw(g);
				myPlane.drawScale(200);
			}
		}

		int myIndex = 0;
		g.beginShape(CCDrawMode.LINES);
		for(CCVector3f myVertex:_myLinePoints){
			myIndex ++;
			float myAlpha = CCMath.map(myIndex, 0, _myLinePoints.size(), _cLineAlphaStart, _cLineAlphaEnd);
			g.color(1f,0,0, myAlpha);
			g.vertex(myVertex);
		}
		g.endShape();
		 myIndex = 0;
		g.beginShape(CCDrawMode.POINTS);
		for(CCVector3f myVertex:_myLinePoints){
			myIndex ++;
//			float myAlpha = CCMath.map(myIndex % _cLasers, 0, _myLinePoints.size() / _cLasers, _cLineAlphaStart, _cLineAlphaEnd);
			g.color(1f,0,0, 0.3f);
			g.vertex(myVertex);
		}
		g.endShape();
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCLaserCube.class);
		myManager.settings().size(1500, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
