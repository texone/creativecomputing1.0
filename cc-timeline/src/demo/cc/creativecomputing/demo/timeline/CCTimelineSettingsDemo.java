package cc.creativecomputing.demo.timeline;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.timeline.CCUITimelineConnector;
import cc.creativecomputing.timeline.view.swing.SwingTimelineContainer;
import cc.creativecomputing.util.logging.CCLog;

public class CCTimelineSettingsDemo extends CCApp{
	public static class CCDrawInfo{
		
		@CCControl(name = "polygonMode")
		private CCPolygonMode _cPolygonMode = CCPolygonMode.LINE;

		@CCControl(name = "color", external = true)
		private CCColor _cColor = new CCColor();
	}
	
	public static class Circle{
		
		public static class Settings{
			@CCControl(name = "radius", min = 1, max = 200, external = true)
			private float _cRadius = 200;
			
			@CCControl(name = "center", external = true)
			private CCVector3f _cVector = new CCVector3f();
			
			@CCControl(name = "draw info", external = true)
			private CCDrawInfo _cDrawInfo = new CCDrawInfo();
			
		}
		
		private float _myX;
		
		public Circle(float theX){
			_myX = theX;
		}
		
		Settings _mySettings = new Settings();
		
		public void draw(CCGraphics g){
			g.polygonMode(_mySettings._cDrawInfo._cPolygonMode);
			g.color(_mySettings._cDrawInfo._cColor);
			g.ellipse(_mySettings._cVector, _mySettings._cRadius);
		}
	}
	
	private Circle _myCircle1 = new Circle(-100);
	private Circle _myCircle2 = new Circle( 100);
	
	Circle.Settings mySettings = new Circle.Settings();
	
	@SuppressWarnings("unused")
	private CCUITimelineConnector _myTimelineConnection;
	private SwingTimelineContainer _myTimeline;
	
	@Override
	public void setup() {
		_myTimeline = new SwingTimelineContainer();
		_myTimeline.minZoomRange(5);
		_myTimeline.raster(1);
		_myTimeline.maxZoomRange(120);
		_myTimelineConnection = new CCUITimelineConnector(this, _myTimeline);
		_myTimeline.setSize(1900, 500);
		CCLog.info("YOOYO");
		_myCircle1._mySettings = mySettings;
		_myCircle2._mySettings = mySettings;
		
		addControls("app", "circle", mySettings );
	}
	
	@Override
	public void update(float theDeltaTime) {
		_myTimeline.update(theDeltaTime);
	}
	
	@Override
	public void draw() {
		g.clear();
		_myCircle1.draw(g);
		_myCircle2.draw(g);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTimelineSettingsDemo.class);
		myManager.settings().size(1000, 900);
		myManager.start();
	}
}
