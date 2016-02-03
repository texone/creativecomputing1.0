package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;

public class CCSettingsDemo extends CCApp{
	
	public static class Circle{
		public static class Settings{
			@CCControl(name = "radius", min = 1, max = 200)
			private float _cRadius = 200;
			
			@CCControl(name = "polygonMode")
			private CCPolygonMode _cPolygonMode = CCPolygonMode.LINE;
		}
		
		private float _myX;
		
		public Circle(float theX){
			_myX = theX;
		}
		
		Settings _mySettings = new Settings();
		
		public void draw(CCGraphics g){
			g.polygonMode(_mySettings._cPolygonMode);
			g.ellipse(_myX, 0, _mySettings._cRadius);
		}
	}
	
	private Circle _myCircle1 = new Circle(-100);
	private Circle _myCircle2 = new Circle( 100);
	
	@Override
	public void setup() {
		Circle.Settings mySettings = new Circle.Settings();
		_myCircle1._mySettings = mySettings;
		_myCircle2._mySettings = mySettings;
		addControls("app", "circle",mySettings );
	}
	
	@Override
	public void draw() {
		g.clear();
		_myCircle1.draw(g);
		_myCircle2.draw(g);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSettingsDemo.class);
		myManager.settings().size(1000, 400);
		myManager.start();
	}
}
