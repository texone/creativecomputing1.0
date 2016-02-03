package cc.creativecomputing.demo.io;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.io.CCIOUtil;

public class CCSelectFolderDemo extends CCApp {
	
	String _myFolder;

	@Override
	public void setup() {
		_myFolder = CCIOUtil.appPath("");
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {

	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch (theKeyEvent.keyCode()) {
		case VK_O:
			String myFolder = CCIOUtil.selectFolder("select a folder", _myFolder);
			if(myFolder != null)_myFolder = myFolder;
			break;

		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSelectFolderDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
