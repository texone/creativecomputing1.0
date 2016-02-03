package cc.creativecomputing.demo.timeline;

import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.timeline.CCUITimelineConnector;
import cc.creativecomputing.timeline.view.swing.SwingTimelineContainer;

public class CCTimelineDrawTest extends CCApp {
	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private class Circle {
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2, external=true)
		private float _myX = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2)
		private float _myY = 0;
		
		@CCControl(name = "radius", min = 1, max = 50)
		private float _myRadius = 0;
		
		@CCControl(name = "draw red")
		private boolean _myDrawRed = true;
		
		@CCControl(name = "draw type", min = 0, max = 3)
		private int _myDrawType = 0;
		
		public void draw(CCGraphics g) {
			if(_myDrawRed)g.color(255,0,0);
			else g.color(255);
			g.ellipse(_myX, _myY, _myRadius);
		}
	}
	
	private ArrayList<Circle> _myCircles;
	
	private SwingTimelineContainer _myTimelineContainer;
	@SuppressWarnings("unused")
	private CCUITimelineConnector _myTimelineConnection;

	@Override
	public void setup() {
		_myCircles = new ArrayList<Circle>();
		
		_myTimelineContainer = new SwingTimelineContainer("Timeline");
		_myTimelineConnection = new CCUITimelineConnector(this, _myTimelineContainer);
		
		_myTimelineContainer.setSize(1000, 500);
		for (int i = 0; i < 10; i++) {
			_myCircles.add(new Circle());
			addControls("Circle", "Circle" + i, _myCircles.get(i));
		}
		_myTimelineContainer.play();
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTimelineContainer.update((long)(theDeltaTime * 1000));
	}

	@Override
	public void draw() { 
		g.clear();
		for (Circle myCircle : _myCircles) {
			myCircle.draw(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTimelineDrawTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
