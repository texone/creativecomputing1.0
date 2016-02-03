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

/**
 * Implement this interface to react on the setup event of the application.
 * @author christian riekoff
 *
 */
public interface CCSetupListener {
	
	/**
	 * Called when the application is setup
	 */
	public void setup();
}
