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

import javax.media.opengl.GLContext;

/**
 * @author christianriekoff
 *
 */
public class CCAppContextSimple implements CCAppContext{
	
	private CCAnimator _myAnimator;

	public CCAppContextSimple(int theFrameRate) {
		if(theFrameRate < 0){
			_myAnimator = new CCAnimator();
		}else{
			_myAnimator = new CCFPSAnimator(theFrameRate);
		}
	}
	
	@Override
	public GLContext glContext() {
		return null;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCAppContext#onAppSetup(cc.creativecomputing.CCApp)
	 */
	@Override
	public void onFirstDisplay(CCAbstractGraphicsApp<?>  theApp) {
		_myAnimator.add(theApp._myAutoDrawable);
		_myAnimator.start();
	}

	@Override
	public void onInit(CCAbstractGraphicsApp<?> theApp) {}

	@Override
	public boolean isShared() {
		return false;
	}
}
