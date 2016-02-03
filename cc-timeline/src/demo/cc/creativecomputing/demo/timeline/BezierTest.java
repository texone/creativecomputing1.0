package cc.creativecomputing.demo.timeline;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.timeline.model.util.CubicSolver;

public class BezierTest extends CCApp {

	/**
	 * Restrictions
	 * 
	 * x1 <= x2 <= x3 <= x4
	 */
	
	private CCVector2f _myPoint1;
	private CCVector2f _myControlPoint1;
	private CCVector2f _myControlPoint2;
	private CCVector2f _myPoint2;
	
	private List<CCVector2f> _myControlPoints = new ArrayList<CCVector2f>();

	@Override
	public void setup() {
		_myControlPoints.add(_myPoint1 = new CCVector2f(100,100));
		_myControlPoints.add(_myControlPoint1 = new CCVector2f(200,100));
		_myControlPoints.add(_myControlPoint2 = new CCVector2f(200,400));
		_myControlPoints.add(_myPoint2 = new CCVector2f(400,400));
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.ortho();

		g.line(_myPoint1, _myControlPoint1);
		g.line(_myControlPoint2, _myPoint2);
		g.bezier(_myPoint1, _myControlPoint1, _myControlPoint2, _myPoint2);
		
		g.ellipse(_myPoint1,10,10);
		g.ellipse(_myControlPoint1,10,10);
		g.ellipse(_myControlPoint2,10,10);
		g.ellipse(_myPoint2,10,10);

		float x1 = bezierPoint3(_myPoint1.x, _myControlPoint1.x, _myControlPoint2.x, _myPoint2.x, CCMath.norm(mouseX, _myPoint1.x, _myPoint2.x));
		float y1 = bezierPoint3(_myPoint1.y, _myControlPoint1.y, _myControlPoint2.y, _myPoint2.y, CCMath.norm(mouseX, _myPoint1.x, _myPoint2.x));

		float x2 = mouseX;
		float y2 = bezierPoint3(_myPoint1.y, _myControlPoint1.y, _myControlPoint2.y, _myPoint2.y, bezierTime(_myPoint1.x, _myControlPoint1.x, _myControlPoint2.x, _myPoint2.x, mouseX));

		g.ellipse(x1, y1, 10, 10);
		g.ellipse(x2, y2, 10, 10);
		
		g.line(x2,0,x2,height);
	}

	float bezierPoint2(float a, float b, float c, float d, float t) {
		float t1 = 1.0f - t;

		return a * t1 * t1 * t1 + 3 * b * t * t1 * t1 + 3 * c * t * t * t1 + d * t * t * t;
	}

	float bezierPoint3(float p0, float p1, float p2, float p3, float t) {
		return (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t + (3 * p0 - 6 * p1 + 3 * p2) * t * t + (-3 * p0 + 3 * p1) * t + p0;
	}

	float bezierTime(float p0, float p1, float p2, float p3, float x) {
		float a = -p0 + 3 * p1 - 3 * p2 + p3;
		float b = 3 * p0 - 6 * p1 + 3 * p2;
		float c = -3 * p0 + 3 * p1;
		float d = p0 - x;

		double[] myResult = CubicSolver.solveCubic(a, b, c, d);

		System.out.println("++++++++++");
		System.out.println(myResult[0]);
		System.out.println(myResult[1]);
		System.out.println(myResult[2]);
		int i = 0;
		while(i < myResult.length - 1 && (myResult[i] < 0 || myResult[i] > 1)) {
			i++;
		}
		return (float)myResult[i];
	}
	
	private CCVector2f _mySelectedPoint;

	@Override
	public void mousePressed(CCMouseEvent theMouseEvent) {
		for(CCVector2f myControlPoint:_myControlPoints) {
			if(myControlPoint.distance(theMouseEvent.position())< 10) {
				_mySelectedPoint = myControlPoint;
				return;
			}
		}
		_mySelectedPoint = null;
	}
	
	@Override
	public void mouseDragged(CCMouseEvent theMouseEvent) {
		if(_mySelectedPoint != null) {
			_mySelectedPoint.set(theMouseEvent.position());
		}
		
		_myControlPoint1.x = CCMath.max(_myPoint1.x, _myControlPoint1.x);
		_myControlPoint2.x = CCMath.max(_myControlPoint1.x, _myControlPoint2.x);
		_myPoint2.x = CCMath.max(_myControlPoint2.x, _myPoint2.x);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(BezierTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
