/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing;

import javax.media.opengl.GLAutoDrawable;

import cc.creativecomputing.CCAbstractGraphicsApp.CCCursor;

/**
 * Context the application is running in that might be a frame or a dialog so far
 * @author christianriekoff
 *
 */
public interface CCAppContainer {
	
	public void show();
	
	public void hide();

	public void close();
	
	public int x();
	
	public int y();
	
	public int width();
	
	public int height();
	
	public boolean isVisible();
	
	public void setVisible(boolean theIsVisible);
	
	public String title();
	
	public void title(String theTitle);
	
	public void dispose();
	
	/**
	 * Use this method to hide the mouse cursor.
	 */
	public void noCursor();
	
	public void cursor(final CCCursor theCursor);
	
	public GLAutoDrawable glAutoDrawable();
}
