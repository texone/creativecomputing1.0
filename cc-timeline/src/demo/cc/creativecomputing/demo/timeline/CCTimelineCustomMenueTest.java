package cc.creativecomputing.demo.timeline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.timeline.CCUITimelineConnector;
import cc.creativecomputing.timeline.view.swing.SwingTimelineContainer;

public class CCTimelineCustomMenueTest extends CCApp {
	
	private static final int APP_WIDTH = 500;
	private static final int APP_HEIGHT = 500;
	
	private class Circle {
		@CCControl(name = "x", min = -APP_WIDTH / 2, max = APP_WIDTH / 2, external=true)
		private float _myX = 0;
		
		@CCControl(name = "y", min = -APP_HEIGHT / 2, max = APP_HEIGHT / 2, external=true)
		private float _myY = 0;
		
		@CCControl(name = "radius", min = 1, max = 50, external=true)
		private float _myRadius = 0;
		
		public void draw(CCGraphics g) {
			g.ellipse(_myX, _myY, _myRadius);
		}
	}
	
	private ArrayList<Circle> _myCircles;
	
	private SwingTimelineContainer _myTimelineContainer;
	private CCUITimelineConnector _myTimelineConnection;

	@Override
	public void setup() {
		_myCircles = new ArrayList<Circle>();
		for (int i = 0; i < 3; i++) {
			_myCircles.add(new Circle());
		}
		
		_myTimelineContainer = new SwingTimelineContainer("Timeline");
		_myTimelineConnection = new CCUITimelineConnector(this,_myTimelineContainer);
		
		ActionListener myListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				System.out.println("HEllo wOrlD");
			}
		};
		_myTimelineContainer.addCustomCommand("hello","world", myListener, CCKeyCode.VK_H.code(), CCKeyCode.VK_H.code());
		
		_myTimelineContainer.setSize(1900, 500); 
		_myUI.addExternalController(_myTimelineConnection);
		for (int i = 0; i < 3; i++) {
			addControls("Circle", "Circle" + i, _myCircles.get(i));
		}
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
		CCApplicationManager myManager = new CCApplicationManager(CCTimelineCustomMenueTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
