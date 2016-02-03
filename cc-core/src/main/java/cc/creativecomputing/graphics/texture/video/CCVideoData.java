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
package cc.creativecomputing.graphics.texture.video;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureData;

/**
 * This class is representing dynamic texture data the content of this object
 * might be fed from a movie or capture device and change. You can add listeners
 * to react on changes of the data.
 * @author christian riekoff
 *
 */
public abstract class CCVideoData extends CCTextureData implements CCUpdateListener, CCPostListener{
	
	/**
	 * indicates a needed update of the data although the movie is not running
	 * this might happen on change of the position
	 */
	protected boolean _myForceUpdate = false;
	
	/**
	 * indicates the initialization of the first frame on data update
	 */
	protected boolean _myIsFirstFrame;
	
	/**
	 * Keep the listeners for update events
	 */
	protected CCListenerManager<CCVideoTextureDataListener> _myListener = new CCListenerManager<CCVideoTextureDataListener>(CCVideoTextureDataListener.class);

	/**
	 * Creates a new instance, without setting any parameters.
	 * @param theApp
	 */
	public CCVideoData(final CCAbstractWindowApp theApp) {
		super();
		_myPixelStorageModes.alignment(1);
		if(theApp == null)return;
		theApp.addUpdateListener(this);
		theApp.addPostListener(this);
	}
	
	public CCVideoData(
		CCApp theApp, 
		int theWidth, int theHeight,
		CCPixelInternalFormat theInternalFormat, CCPixelFormat theFormat,
		CCPixelType theType
	) {
		super(theWidth, theHeight, theInternalFormat, theFormat, theType);
		_myPixelStorageModes.alignment(1);
		theApp.addUpdateListener(this);
		theApp.addPostListener(this);
	}

	/**
	 * Adds a listener to react on update events.
	 * @param theListener the listener 
	 */
	public void addListener(final CCVideoTextureDataListener theListener) {
		_myListener.add(theListener);
	}
	
	/**
	 * Removes a listener to react on update events.
	 * @param theListener the listener 
	 */
	public void removeListener(final CCVideoTextureDataListener theListener) {
		_myListener.remove(theListener);
	}
	
}
