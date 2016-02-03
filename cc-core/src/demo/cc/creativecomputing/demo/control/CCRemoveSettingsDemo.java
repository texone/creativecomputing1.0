package cc.creativecomputing.demo.control;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;

public class CCRemoveSettingsDemo extends CCApp{
	
	private class Settings1{
		@CCControl(name = "test1", min = 0, max = 20)
		private float _cName1 = 0;
	}
	
	private class Settings2{
		@CCControl(name = "test2", min = 0, max = 20)
		private float _cName2 = 0;
	}
	
	private Settings1 _mySettings1 = new Settings1();
	private Settings2 _mySettings2 = new Settings2();
	
	@Override
	public void setup() {
		addControls("app", "circle",_mySettings1);
	}
	
	@Override
	public void draw() {
		g.clear();
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch (theKeyEvent.keyCode()) {
		case VK_1:
			_myUI.removeControl("app", "circle");
			addControls("app", "circle", _mySettings1);
			break;
		case VK_2:
			_myUI.removeControl("app", "circle");
			addControls("app", "circle", _mySettings2);
			break;

		default:
			break;
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCRemoveSettingsDemo.class);
		myManager.settings().size(1000, 400);
		myManager.start();
	}
}
