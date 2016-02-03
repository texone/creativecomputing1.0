package cc.creativecomputing.demo.timeline;

import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
//import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.timeline.CCUITimelineConnector;
import cc.creativecomputing.timeline.view.swing.SwingTimelineContainer;
import cc.creativecomputing.util.logging.CCLog;

public class CCTimelineTest extends CCApp {
	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private class Circle {
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2, external=true)
		private float _myX = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2, external=true)
		private float _myY = 0;
		
		@CCControl(name = "radius", min = 1, max = 50, external=true)
		private float _myRadius = 0;
		
		@CCControl(name = "draw red", external=true)
		private boolean _myDrawRed = true;
		
		@CCControl(name = "draw type", min = 2, max = 16, external=true)
		private int _myDrawType = 0;
		
//		@CCControl(name = "matrix", min = -5, max = 5, external=true)
//		private CCControlMatrix _myMatrix;
		
		public Circle(){
//			_myMatrix = new CCControlMatrix();
//			_myMatrix.addColumn("col0");
//			_myMatrix.addColumn("col1");
//			_myMatrix.addColumn("col2");
//			_myMatrix.addColumn("col3");
//			
//			_myMatrix.addRow("row0");
//			_myMatrix.addRow("row1");
//			_myMatrix.addRow("row2");
//			_myMatrix.addRow("row3");
//			_myMatrix.addRow("row4");
		}
		
		public void draw(CCGraphics g) {
			if(_myDrawRed)g.color(255,0,0);
			else g.color(255);
			g.ellipse(_myX, _myY, _myRadius);
		}
	}
	
	private boolean _myUseTimeline = true;
	
	private ArrayList<Circle> _myCircles;

	@SuppressWarnings("unused")
	private CCUITimelineConnector _myTimelineConnection;
	private SwingTimelineContainer _myTimeline;
//	
//	@CCControl (name = "circle")
//	private Circle _myCircle = new Circle();

	@Override
	public void setup() {
		_myCircles = new ArrayList<Circle>();
		if(_myUseTimeline){
			_myTimeline = new SwingTimelineContainer();
			_myTimeline.minZoomRange(5);
			_myTimeline.raster(0);
			_myTimeline.maxZoomRange(120);
			_myTimeline.setSize(1900, 500);
			_myTimelineConnection = new CCUITimelineConnector(this, _myTimeline);
		}
	
		
//		_myTimeline.addMarker("yo 0",0);
//		_myTimeline.addMarker("yo 1",3);
//		_myTimeline.addMarker("yo 3",6);
		addControls("app", "app", this);
		
		for (int i = 0; i < 3; i++) {
			_myCircles.add(new Circle());
			addControls("Circle", "Circle" + i, _myCircles.get(i));
		}
		myStartMillis = System.currentTimeMillis();
//		_myTimeline.play();
	}
	
	float _myTime = 0;
	long myStartMillis;

	@Override
	public void update(final float theDeltaTime) {
		if(_myUseTimeline){
//			_myTimeline.update(theDeltaTime);
		}
		
		_myTime += theDeltaTime;
		
//		System.out.println("################################");
//		System.out.println("TIME0:" + _myTime);
//		System.out.println("TIME1:" + (System.currentTimeMillis() - myStartMillis));
//		System.out.println("TIME2:" + _myTimeline.transport().time());
	}

	@Override
	public void draw() { 
		g.clear();
		for (Circle myCircle : _myCircles) {
			myCircle.draw(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTimelineTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
