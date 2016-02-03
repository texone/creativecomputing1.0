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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JDialog;
import javax.swing.JFrame;

import cc.creativecomputing.CCAbstractGraphicsApp.CCCursor;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseWheelEvent;

public class CCJavaAppContainer implements CCAppContainer{
	
	private static interface CCJavaContainer {

		void addWindowListener(WindowAdapter theCreateWindowAdapter);

		Container getContentPane();

		void setVisible(boolean theVisible);

		int getX();

		int getY();

		int getWidth();

		int getHeight();

		boolean isVisible();

		String getTitle();

		void setTitle(String theTitle);

		void processWindowEvent(WindowEvent theWindowEvent);
		
		Window window();
		
	}
	
	private class CCBaseFrame extends JFrame implements CCJavaContainer{

		private static final long serialVersionUID = 7937229563871713080L;

		private CCBaseFrame(String title, GraphicsConfiguration gc) {
			super(title, gc);
		}

		public void processWindowEvent(WindowEvent theWindowEvent){
			super.processWindowEvent(theWindowEvent);
		}

		@Override
		public void addWindowListener(WindowAdapter theCreateWindowAdapter) {
			super.addWindowListener(theCreateWindowAdapter);
		}

		@Override
		public Window window() {
			return this;
		}
	}
	
	private class CCFrame extends CCBaseFrame{

		private static final long serialVersionUID = -3566712236052208336L;
		
		public CCFrame(final CCApplicationManager theManager) {
			super(theManager.settings().title(),theManager.displayConfiguration());

			setResizable(theManager.settings().isResizable());
			setUndecorated(theManager.settings().undecorated());
			setAlwaysOnTop(theManager.settings().alwaysOnTop());
			getContentPane().setLayout(new BorderLayout());
			getContentPane().setBackground(new Color(
				_mySettings.background().red(),
				_mySettings.background().green(),
				_mySettings.background().blue())
			);
			
			setDefaultCloseOperation(_mySettings.closeOperation().id());
			pack();
		}
	}
	
	private class CCFullFrame extends CCBaseFrame {

		private static final long serialVersionUID = -3566712236052208336L;

		public CCFullFrame(final CCApplicationManager theManager, Component theGLComponent) {
			super(_mySettings.title(),theManager.displayConfiguration());

			getContentPane().add(theGLComponent);
			setDefaultCloseOperation(_mySettings.closeOperation().id());
//			pack();
			//
			Rectangle myBounds = theManager.displayConfiguration().getBounds();
			setBounds(myBounds);
			getContentPane().setBackground(new Color(
				_mySettings.background().red(),
				_mySettings.background().green(),
				_mySettings.background().blue())
			);
			
			
			
			setUndecorated(true);     // no decoration such as title bar
			setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
//			pack();
			
			theManager.display().setFullScreenWindow(this);
			setVisible(true);
		}
	}
	
	private class CCDialog extends JDialog implements CCJavaContainer{

		private static final long serialVersionUID = -3566712236052208336L;
		
		
		public CCDialog(final Window theOwner, final CCApplicationManager theManager) {
			super(theOwner,_mySettings.title(),Dialog.ModalityType.MODELESS, theManager.displayConfiguration());

			setResizable(_mySettings.isResizable());
			setUndecorated(_mySettings.undecorated());
			setAlwaysOnTop(_mySettings.alwaysOnTop());
			getContentPane().setLayout(new BorderLayout());
			getContentPane().setBackground(new Color(
				_mySettings.background().red(),
				_mySettings.background().green(),
				_mySettings.background().blue())
			);
			
			setDefaultCloseOperation(_mySettings.closeOperation().id());
			pack();
			
		}
		
		public void processWindowEvent(WindowEvent theEvent) {
			super.processWindowEvent(theEvent);
		}
		
		@Override
		public void addWindowListener(WindowAdapter theCreateWindowAdapter) {
			addWindowListener(theCreateWindowAdapter);
		}
		
		@Override
		public Window window() {
			return this;
		}
	}
	
	private void setup(Container theContainer, CCApplicationManager theManager){
		
		
		//get insets to adjust frame size
		final Insets myInsets = theContainer.getInsets();
		
		theContainer.setSize(
			_mySettings.width() + myInsets.left + myInsets.right, 
			_mySettings.height() + myInsets.top + myInsets.bottom
		);
				
		Rectangle myBounds = theManager.displayConfiguration().getBounds();
				
		if(_mySettings.x() > -1){
			theContainer.setLocation(myBounds.x + _mySettings.x(),myBounds.y + _mySettings.y());
		}else{
			theContainer.setLocation(
				myBounds.x + (myBounds.width - _mySettings.width())/2,
				myBounds.y + (myBounds.height - _mySettings.height())/2
			);
		}
	}
	
	CCAbstractGraphicsApp<?> _myApplication;
	private CCApplicationSettings _mySettings;
	private GLAutoDrawable _myDrawable;
	protected Component _myGLComponent;
	protected CCJavaContainer _myContainerComponent;
	private int _myLastPressedButton = 0;
	
	public CCJavaAppContainer(
		final CCAbstractGraphicsApp<?> theApplication, 
		final CCApplicationManager theManager, 
		GLAutoDrawable theDrawable, 
		Component theGLComponent
	){
		_myApplication = theApplication;
		_mySettings = theManager.settings();
		_myDrawable = theDrawable;
		_myGLComponent = theGLComponent;
		
		switch(_mySettings.displayMode()){
		case FULLSCREEN:
			_myContainerComponent = new CCFullFrame(theManager, theGLComponent);
			break;
		case WINDOW:
			switch(_mySettings.container()){
			case DIALOG:
				_myContainerComponent = new CCDialog(theManager.settings().dialogOwner(), theManager);
				setup(_myContainerComponent.window(), theManager);
				break;
			case FRAME:
				_myContainerComponent = new CCFrame(theManager);
				setup(_myContainerComponent.window(), theManager);
				break;
			default:
				throw new RuntimeException("Invalid display mode for java container: " + _mySettings.displayMode()+" : " + _mySettings.container());
			}
			break;
		default:
			throw new RuntimeException("Invalid display mode for java container: " + _mySettings.displayMode()+" : " + _mySettings.container());
		}
		

		_myContainerComponent.addWindowListener(createWindowAdapter());
		_myContainerComponent.getContentPane().add(theGLComponent, BorderLayout.CENTER);
		

		
		_myGLComponent.addKeyListener(new KeyListener() {
			
			public void keyPressed(KeyEvent theKeyEvent) {
				if(theKeyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
					switch(_mySettings.closeOperation()) {
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
				
				_myApplication.enqueueKeyEvent(new CCKeyEvent(theKeyEvent));
			}

			public final void keyReleased(KeyEvent theKeyEvent) {
				_myApplication.enqueueKeyEvent(new CCKeyEvent(theKeyEvent));
			}

			public final void keyTyped(KeyEvent theKeyEvent) {
				_myApplication.enqueueKeyEvent(new CCKeyEvent(theKeyEvent));
			}
		});
		
		_myGLComponent.addMouseListener(new MouseListener() {
			
			
			public void mousePressed(final MouseEvent theMouseEvent) {
				_myLastPressedButton = theMouseEvent.getButton();
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY));
			}

			/** @invisible */
			public void mouseReleased(final MouseEvent theMouseEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY));
			}

			/** @invisible */
			public void mouseClicked(final MouseEvent theMouseEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY));
			}
			
			public void mouseEntered(final MouseEvent theMouseEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY));
			}

			public void mouseExited(final MouseEvent theMouseEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY));
			}
		});
		
		_myGLComponent.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseDragged(final MouseEvent theMouseEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY,_myLastPressedButton));
			}

			public void mouseMoved(final MouseEvent theMouseEvent) {
				_myApplication.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myApplication.pMouseX, _myApplication.pMouseY));
			}
		});
		
		_myGLComponent.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent theMouseEvent) {
				_myApplication.enqueueMouseWheelEvent(new CCMouseWheelEvent(theMouseEvent));
			}
		});
	}
	
	public WindowAdapter createWindowAdapter(){
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				switch(_mySettings.closeOperation()) {
				case DO_NOTHING_ON_CLOSE:
					break;
				case HIDE_ON_CLOSE:
					setVisible(false);
					break;
				case DISPOSE_ON_CLOSE:
				case EXIT_ON_CLOSE:
					_myGLComponent.setVisible(false);
					_myApplication.dispose();
					break;
				}
			}
		};
	}
	
	public void setVisible(boolean visible) {
		_myGLComponent.setVisible(visible);
		_myContainerComponent.setVisible(visible);

		//attempt to get the focus of the canvas
		if(visible)_myGLComponent.requestFocus();
	}

	@Override
	public void show() {
		setVisible(true);
	}

	@Override
	public void hide() {
		setVisible(false);
	}

	@Override
	public int x() {
		return _myContainerComponent.getX();
	}

	@Override
	public int y() {
		return _myContainerComponent.getY();
	}

	@Override
	public int width() {
		return _myContainerComponent.getWidth();
	}

	@Override
	public int height() {
		return _myContainerComponent.getHeight();
	}

	@Override
	public boolean isVisible() {
		return _myContainerComponent.isVisible();
	}

	@Override
	public void dispose() {
		_myApplication.dispose();
	}
	
	public void noCursor(){
		_myGLComponent.setCursor(_myGLComponent.getToolkit().createCustomCursor(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB), new Point(10,10), "Cursor" ));
	}
	
	
	public void cursor(final CCCursor theCursor){
		_myGLComponent.setCursor(theCursor.javaCursor());
	}

	@Override
	public GLAutoDrawable glAutoDrawable() {
		return _myDrawable;
	}
	
	public String title() {
		return _myContainerComponent.getTitle();
	}
	
	public void title(String theTitle) {
		_myContainerComponent.setTitle(theTitle);
	}
	
	public void close() {
		_myContainerComponent.processWindowEvent(new WindowEvent(_myContainerComponent.window(), _myApplication.settings.closeOperation().id()));
	}

}
