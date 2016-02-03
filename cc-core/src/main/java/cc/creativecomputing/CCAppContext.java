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
public interface CCAppContext {
	
	public boolean isShared();
	
	public void onInit(CCAbstractGraphicsApp<?>  theApp);
	
	public void onFirstDisplay(CCAbstractGraphicsApp<?>  theApp);

	public abstract GLContext glContext();
}
