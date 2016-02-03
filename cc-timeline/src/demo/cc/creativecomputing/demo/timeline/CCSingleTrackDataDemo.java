package cc.creativecomputing.demo.timeline;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.timeline.controller.tools.TimelineTool;
import cc.creativecomputing.timeline.view.swing.SwingCurveFrame;
import cc.creativecomputing.timeline.view.swing.SwingGuiConstants;

public class CCSingleTrackDataDemo extends CCApp {

	private SwingCurveFrame _myFrame;
	
	@Override
	public void setup() {
		SwingGuiConstants.CURVE_TOOLS = new TimelineTool[] {TimelineTool.LINEAR};
		_myFrame = new SwingCurveFrame();
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {
		g.clear();
		for(float f = 0; f <= 1f; f+=0.01f) {
			float myX = CCMath.blend(-width/2, width/2,f);
			g.color(f);
			g.rect(myX, 0, width * 0.01f, height/2);

			g.color(_myFrame.value(f));
			g.rect(myX, -height/2, width * 0.01f, height/2);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSingleTrackDataDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
