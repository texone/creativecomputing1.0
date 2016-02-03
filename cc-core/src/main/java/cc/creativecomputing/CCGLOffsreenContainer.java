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

import java.awt.Rectangle;

import javax.media.nativewindow.util.InsetsImmutable;
import javax.media.opengl.GLAutoDrawable;

import cc.creativecomputing.CCAbstractGraphicsApp.CCCursor;

import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;

/**
 * @author texone
 */
public class CCGLOffsreenContainer implements CCAppContainer{
	
	CCAbstractGraphicsApp<?> _myApplication;
	
	private GLWindow _myWindow;

	public CCGLOffsreenContainer(final CCAbstractGraphicsApp<?> theApplication, final CCApplicationManager theManager) {
		CCApplicationSettings mySettings = theManager.settings();
		
		mySettings.glCapabilities.setOnscreen(false);
		mySettings.glCapabilities.setAlphaBits(1); 
		mySettings.glCapabilities.setPBuffer(true);
		mySettings.glCapabilities.setDoubleBuffered(true);
		// Create the OpenGL rendering canvas
		_myWindow = GLWindow.create(mySettings.glCapabilities);
		
		
		
		_myWindow.setTitle(mySettings.title());
//		_myWindow.setResizable(theSettings.isResizable());
		_myWindow.setUndecorated(mySettings.undecorated());
		
		Rectangle myBounds = theManager.displayConfiguration().getBounds();
		
		//get insets to adjust frame size
		final InsetsImmutable myInsets = _myWindow.getInsets();
		
		_myWindow.setSize(
			mySettings.width() + myInsets.getLeftWidth() + myInsets.getRightWidth(), 
			mySettings.height() + myInsets.getTopHeight() + myInsets.getBottomHeight()
		);
				
		if(mySettings.x() > -1){
			_myWindow.setPosition(myBounds.x+ mySettings.x(),myBounds.y + mySettings.y());
		}else{
			_myWindow.setPosition(
				myBounds.x + (myBounds.width - mySettings.width())/2,
				myBounds.y + (myBounds.height - mySettings.height())/2
			);
		}
		
		_myWindow.setVisible(true);

		_myApplication = theApplication;
		_myApplication._myContainer = this;
		_myApplication.frameSetup();
		
		_myWindow.addWindowListener(new WindowListener() {
			
			@Override
			public void windowResized(com.jogamp.newt.event.WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowRepaint(WindowUpdateEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowMoved(com.jogamp.newt.event.WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowLostFocus(com.jogamp.newt.event.WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowGainedFocus(com.jogamp.newt.event.WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDestroyed(com.jogamp.newt.event.WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setVisible(boolean visible) {
		_myWindow.setVisible(visible);

		//attempt to get the focus of the canvas
		if(visible)_myWindow.requestFocus();
	}
	
	public void close() {
//		processWindowEvent(new WindowEvent(this, _myApplication.settings.closeOperation().id()));
	}

	public int x() {
		return _myWindow.getX();
	}

	public int y() {
		return _myWindow.getY();
	}

	public int width() {
		return _myWindow.getWidth();
	}

	public int height() {
		return _myWindow.getHeight();
	}
	
	public String title() {
		return _myWindow.getTitle();
	}
	
	public void title(String theTitle) {
		_myWindow.setTitle(theTitle);
	}


	@Override
	public void show() {
		_myWindow.setVisible(true);
	}


	@Override
	public void hide() {
		_myWindow.setVisible(false);
	}


	@Override
	public boolean isVisible() {
		return _myWindow.isVisible();
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public void noCursor(){
		_myWindow.setPointerVisible(false);
	}
	
	
	public void cursor(final CCCursor theCursor){
		_myWindow.setPointerVisible(true);
	}
	
	@Override
	public GLAutoDrawable glAutoDrawable() {
		return _myWindow;
	}
}
