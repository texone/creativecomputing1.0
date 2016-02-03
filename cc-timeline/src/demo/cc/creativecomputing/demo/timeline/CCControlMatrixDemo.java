package cc.creativecomputing.demo.timeline;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
//import cc.creativecomputing.control.CCControlMatrix;

public class CCControlMatrixDemo extends CCApp {
	
//	@CCControl(name = "matrix", min = -5, max = 5)
//	private CCControlMatrix _myMatrix;

	@Override
	public void setup() {
//		_myMatrix = new CCControlMatrix();
//		_myMatrix.addColumn("col0");
//		_myMatrix.addColumn("col1");
//		_myMatrix.addColumn("col2");
//		_myMatrix.addColumn("col3");
//		
//		_myMatrix.addRow("row0");
//		_myMatrix.addRow("row1");
//		_myMatrix.addRow("row2");
//		_myMatrix.addRow("row3");
//		_myMatrix.addRow("row4");
		
		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCControlMatrixDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

