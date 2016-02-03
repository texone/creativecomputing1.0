package cc.creativecomputing.cv.openni.skeleton;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.CCSystem;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;

public class CCMemDemo extends CCApp {
	
	private float _myMaxMem = 0;

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		_myMaxMem = CCMath.max(_myMaxMem, CCSystem.memoryInUse() / 1000);
		CCLog.info(_myMaxMem);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMemDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

