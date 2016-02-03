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
public class CCAppContextUpdate implements CCAppContext{
	
	@SuppressWarnings("unused")
	private CCUpdateAnimator _myAnimator;

	public CCAppContextUpdate(CCUpdateAnimator theAnimator) {
		_myAnimator = theAnimator;
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
	}

	@Override
	public void onInit(CCAbstractGraphicsApp<?> theApp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isShared() {
		return false;
	}
}
