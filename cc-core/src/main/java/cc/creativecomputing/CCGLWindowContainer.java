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
import cc.creativecomputing.CCApplicationSettings.CCDisplayMode;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyEvent.CCKeyEventType;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseEvent.CCMouseEventType;
import cc.creativecomputing.events.CCMouseWheelEvent;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;

/**
 * @author texone
 */
public class CCGLWindowContainer implements CCAppContainer{
	
	CCAbstractGraphicsApp<?> _myApplication;
	
	private GLWindow _myWindow;
	

	public CCGLWindowContainer(final CCAbstractGraphicsApp<?> theApplication, final CCApplicationManager theManager) {
		final CCApplicationSettings mySettings = theManager.settings();
		_myWindow = GLWindow.create(mySettings.glCapabilities);
		
		_myWindow.setTitle(mySettings.title());
//		_myWindow.setResizable(theSettings.isResizable());
		_myWindow.setUndecorated(mySettings.undecorated());
		_myWindow.setAlwaysOnTop(mySettings.alwaysOnTop());
		
		if(mySettings.appContext().isShared())_myWindow.setSharedContext(mySettings.appContext().glContext());
		
		if(mySettings.displayMode() == CCDisplayMode.FULLSCREEN){
			Rectangle myBounds = theManager.displayConfiguration().getBounds();
			_myWindow.setPosition(myBounds.x, myBounds.y);
			_myWindow.setSize(myBounds.width, myBounds.height);
			_myWindow.setFullscreen(true);
		}else{
		
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
		}

		_myApplication = theApplication;
		_myApplication._myContainer = this;
		
		_myWindow.addKeyListener(new KeyListener() {
			
//			@Override
//			public void keyTyped(KeyEvent theEvent) {
//				_myApplication.enqueueKeyEvent(new CCKeyEvent(theEvent, CCKeyEventType.TYPED));
//			}
			
			@Override
			public void keyReleased(KeyEvent theEvent) {
				_myApplication.enqueueKeyEvent(new CCKeyEvent(theEvent, CCKeyEventType.RELEASED));
			}
			
			@Override
			public void keyPressed(KeyEvent theEvent) {
				if(theEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
					switch(mySettings.closeOperation()) {
					case DO_NOTHING_ON_CLOSE:
						break;
					case HIDE_ON_CLOSE:
						hide();
						break;
					case DISPOSE_ON_CLOSE:
						close();
						break;
					case EXIT_ON_CLOSE:
						dispose();
						System.exit(0);
						break;
					}
				}
				_myApplication.enqueueKeyEvent(new CCKeyEvent(theEvent, CCKeyEventType.PRESSED));
			}
		});
		
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
		
		_myWindow.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseWheelMoved(MouseEvent theEvent) {
				_myApplication.enqueueMouseWheelEvent(new CCMouseWheelEvent(theEvent));
			}
			
			@Override
			public void mouseReleased(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_RELEASED));
			}
			
			@Override
			public void mousePressed(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_PRESSED));
			}
			
			@Override
			public void mouseMoved(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_MOVED));
			}
			
			@Override
			public void mouseExited(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_EXITED));
			}
			
			@Override
			public void mouseEntered(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_ENTERED));
			}
			
			@Override
			public void mouseDragged(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_DRAGGED));
			}
			
			@Override
			public void mouseClicked(MouseEvent theEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theEvent, _myApplication.pMouseX, _myApplication.pMouseY, CCMouseEventType.MOUSE_CLICKED));
			}
		});
		
		_myApplication.frameSetup();
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
