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
 * Post events are generated for every frame of the application right
 * after the drawing. Classes that are interested in processing this 
 * event implement this interface (and its post method).The listener 
 * object created from that class is then registered using the application's 
 * <code>addPostListener</code> method.
 * </p>
 * @author texone
 * @see CCApp#addPostListener(CCPostListener)
 */
public interface CCPostListener{
	/**
	 * The post method is called directly after the draw call.
	 */
	public void post();
}
