package cc.creativecomputing;

import javax.media.opengl.GLAutoDrawable;

import cc.creativecomputing.CCAbstractGraphicsApp.CCCursor;

public class CCUpdateContainer implements CCAppContainer{

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void close() {}

	@Override
	public int x() {
		return 0;
	}

	@Override
	public int y() {
		return 0;
	}

	@Override
	public int width() {
		return 0;
	}

	@Override
	public int height() {
		return 0;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setVisible(boolean theIsVisible) {
		
	}

	@Override
	public String title() {
		return null;
	}

	@Override
	public void title(String theTitle) {}

	@Override
	public void dispose() {}

	@Override
	public void noCursor() {}

	@Override
	public void cursor(CCCursor theCursor) {}

	@Override
	public GLAutoDrawable glAutoDrawable() {
		return null;
	}

}
