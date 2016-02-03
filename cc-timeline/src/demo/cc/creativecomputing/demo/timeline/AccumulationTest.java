package cc.creativecomputing.demo.timeline;

import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.timeline.CCUITimelineConnector;
import cc.creativecomputing.timeline.view.swing.SwingTimelineContainer;

public class AccumulationTest extends CCApp {

	private class Circle {

		@CCControl(name = "speed", min = -5, max = 5, external = true, accumulate = true)
		private float _cSpeed = 0;

		public void draw(CCGraphics g) {
			g.ellipse(_cSpeed, 0, 30);
		}
	}

	private ArrayList<Circle> _myCircles;

	private SwingTimelineContainer _myTimelineContainer;
	@SuppressWarnings("unused")
	private CCUITimelineConnector _myTimelineConnection;

	@Override
	public void setup() {
		_myCircles = new ArrayList<Circle>();
		for (int i = 0; i < 3; i++) {
			_myCircles.add(new Circle());
		}

		_myTimelineContainer = new SwingTimelineContainer("Timeline");
		_myTimelineContainer.setSize(1000, 500);
		_myTimelineConnection = new CCUITimelineConnector(this,_myTimelineContainer);

		for (int i = 0; i < 3; i++) {
			addControls("Circle", "Circle" + i, _myCircles.get(i));
		}
	}

	@Override
	public void update(final float theDeltaTime) {
		_myTimelineContainer.update((long) (theDeltaTime * 1000));
	}

	@Override
	public void draw() {
		g.clear();
		for (Circle myCircle : _myCircles) {
			myCircle.draw(g);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(
				AccumulationTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
