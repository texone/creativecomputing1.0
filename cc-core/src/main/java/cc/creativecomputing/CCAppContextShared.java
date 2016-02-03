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

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLContext;

/**
 * This class is used to manage multiple application, this means to share a glcontext 
 * and the animator
 * @author christianriekoff
 *
 */
public class CCAppContextShared implements CCAppContext{
	
	private GLContext _myGLContext;
	protected CCAnimator _myAnimator;
	
	private List<CCAbstractGraphicsApp<?> > _myApplications;
	
	public CCAppContextShared(int theFrameRate) {
		if(theFrameRate < 0){
			_myAnimator = new CCAnimator();
		}else{
			_myAnimator = new CCFPSAnimator(theFrameRate);
		}
		_myApplications = new ArrayList<CCAbstractGraphicsApp<?> >();
	}
	
	public CCAppContextShared() {
		this(-1);
	}
	
	public GLContext glContext() {
		return _myGLContext;
	}
	
	@Override
	public void onInit(CCAbstractGraphicsApp<?>  theApp) {
		if(_myGLContext == null) {
			_myGLContext = theApp._myAutoDrawable.getContext();
		}
		_myApplications.add(theApp);
	}
	
	private int _mySetupAppCounter = 0;
	
	@Override
	public void onFirstDisplay(CCAbstractGraphicsApp<?>  theApp) {
		if(_myApplications.contains(theApp)) {
			_myAnimator.add(theApp._myAutoDrawable);
			_mySetupAppCounter++;
		}
		
		if(_mySetupAppCounter == _myApplications.size()) {
			_myAnimator.start();
		}
	}

	@Override
	public boolean isShared() {
		return true;
	}
	
}
