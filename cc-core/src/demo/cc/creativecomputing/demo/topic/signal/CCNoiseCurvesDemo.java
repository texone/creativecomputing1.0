package cc.creativecomputing.demo.topic.signal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCArcball;

public class CCNoiseCurvesDemo extends CCApp {
	
	private class CCNoiseCurve{
		private CCVector3f _myStart;
		private CCVector3f _myEnd;
		
		private List<CCVector3f> _myNoiseValues = new ArrayList<>();
		
		private float _myOffset = CCMath.random(10000);
		
		private float _mySpeed = CCMath.random(100f);
		
		public CCNoiseCurve(CCVector3f theStart, CCVector3f theEnd){
			_myStart = theStart;
			_myEnd = theEnd;
		}
		
		public void update(float theDeltaTime){
			_myOffset += theDeltaTime * _mySpeed;
			_myNoiseValues.clear();
			for(int i = 0; i <= 100;i++){
				float myBlend = CCMath.map(i, 0, 100, 0, 1);
				float myCurveBlend = CCMath.pow(1 - (CCMath.cos(myBlend * CCMath.TWO_PI) + 1) / 2,0.33f);
				CCVector3f myPos = CCVecMath.blend(myBlend, _myStart, _myEnd);
				float[] myNoise = _myNoise.values(myPos.x + 10000, myPos.y + 10000, myPos.z + _myOffset);
				myPos.add(
					(myNoise[0] - 0.5f) * _cCurveRadius * myCurveBlend,
					(myNoise[1] - 0.5f) * _cCurveRadius * myCurveBlend,
					(myNoise[2] - 0.5f) * _cCurveRadius * myCurveBlend
				);
				_myNoiseValues.add(myPos);
			}
		}
		
		public void draw(CCGraphics g){
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(CCVector3f myPoint:_myNoiseValues){
				g.vertex(myPoint);
			}
			g.endShape();
		}
	}
	
	private List<CCNoiseCurve> _myCurves = new ArrayList<>();
	private CCVector3f _myStart = new CCVector3f();
	private CCVector3f _myEnd = new CCVector3f();

	@CCControl(name = "x0", min = -500, max = 500)
	private float _cX0 = 0;
	@CCControl(name = "y0", min = -500, max = 500)
	private float _cY0 = 0;
	@CCControl(name = "x1", min = -500, max = 500)
	private float _cX1 = 0;
	@CCControl(name = "y1", min = -500, max = 500)
	private float _cY1 = 0;
	@CCControl(name = "curveRadius", min = 1, max = 200)
	private float _cCurveRadius = 0;
	
	@CCControl(name = "noise")
	private CCSimplexNoise _myNoise = new CCSimplexNoise();
	
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		
		addControls("app", "app", this);
		for(int i = 0; i < 10; i++){
			_myCurves.add(new CCNoiseCurve(_myStart, _myEnd));
		}
		
		_myArcball = new CCArcball(this);
	}
	
	

	@Override
	public void update(final float theDeltaTime) {
		_myStart.x = _cX0;
		_myStart.y = _cY0;

		_myEnd.x = _cX1;
		_myEnd.y = _cY1;
		for(CCNoiseCurve myCurve:_myCurves){
			myCurve.update(theDeltaTime);
		}
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		for(CCNoiseCurve myCurve:_myCurves){
			myCurve.draw(g);
		}
//		g.line(_myStart, _myEnd);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNoiseCurvesDemo.class);
		myManager.settings().size(1000, 1000);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
