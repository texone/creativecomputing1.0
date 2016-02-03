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

/**
 * Listener to react to different events while playing a movie
 * @author christianriekoff
 *
 */
public interface CCMovieListener {

	/**
	 * Called when movie is started
	 */
	public void onPlay();
	
	/**
	 * Called when movie playback is stopped
	 */
	public void onStop();
	
	/**
	 * Called when movie playback is paused
	 */
	public void onPause();
	
	/**
	 * Called when the movie has reached its end
	 */
	public void onEnd();
}
