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
package cc.creativecomputing.events;

import cc.creativecomputing.CCApp;

/**
 * <p>
 * Update events are generated for every frame of the application right
 * before the drawing. Classes that are interested in processing this 
 * event implement this interface (and its update method).The listener 
 * object created from that class is then registered using the application's 
 * <code>addUpdateListener</code> method. Use the update method for
 * updates in your application logic. The method receives the time since
 * the last frame so that you can make frame rate independent changes.
 * </p>
 * @author texone
 * @see CCApp#addUpdateListener(CCUpdateListenerListener)
 */
public interface CCUpdateListener{
	/**
	 * This method is called before the draw method on every frame. Override this
	 * method to define updates in your applications logic. The update function
	 * receives the time since the last frame as float value in seconds. You can
	 * use this parameter to calculate you updates dependent on the frame rate. 
	 * @shortdesc automatically called before draw for application updates
	 * @param theDeltaTime time since the last frame in seconds
	 */
	public void update(float theDeltaTime);
}
