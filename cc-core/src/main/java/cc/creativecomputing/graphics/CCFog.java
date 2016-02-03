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
package cc.creativecomputing.graphics;


import javax.media.opengl.*;

import cc.creativecomputing.CCApp;

/**
 * <p>
 * Applies fog to the application.
 * Fog blends a fog color with each rasterized pixel fragment's post-texturing
 * color using a blending factor f. Factor f is computed in one of three ways,
 * depending on the fog mode. Let c be either the distance in eye coordinate 
 * from the origin (in the case that the GL_FOG_COORD_SRC is FRAGMENT_DEPTH) or
 * the current fog coordinate (in the case that GL_FOG_COORD_SRC is GL_FOG_COORD).
 * </p>
 * <p>   
 * The equation for LINEAR fog is f = (end - c) / (end - start)
 * </p>
 * <p>
 * The equation for EXP fog is f = e ^- density * c
 * </p>
 * <p>
 * The equation for EXP2 fog is f = (e ^- density * c)^2
 * </p>
 * <p>
 * Regardless of the fog mode, f is clamped to the range 0 - 1
 * after it is computed. Then, if the GL is in RGBA color mode,
 * the fragment's red, green, and blue colors, represented by
 * @author Christian Riekoff
 *
 */
public class CCFog{
	
	public static enum CCFogMode{
		LINEAR(GL.GL_LINEAR), 
		EXP(GL2.GL_EXP),
        EXP2(GL2.GL_EXP2);
    
		private final int glId;
		  
		private CCFogMode(final int theglID){
			glId = theglID;
		}
	}

	private final CCApp _myApp;
	private final GL2 _myGL;
	
	private float _myFogStart;
	private float _myFogEnd;
	
	private float _myFogDensity;
	
	private CCColor _myFogColor;
	
	private CCFogMode _myFogMode = CCFogMode.LINEAR;

	/**
	 * This is the constructor for the fog, the supplied argument should be a reference to main applet.
	 * @param theApp CCApp, the parent application
	 */
	public CCFog(final CCApp theApp){
		this(theApp, 0,0);
	}
	
	public CCFog(final CCApp theApp, final float theFogStart, final float theFogEnd){
		_myApp = theApp;
		
		_myGL = _myApp.g.gl;
		
		_myFogStart = theFogStart;
		_myFogEnd = theFogEnd;
		
		_myFogDensity = 1;
		
		_myFogColor = new CCColor();
	}

	public void begin(){
		_myGL.glFogi(GL2.GL_FOG_MODE, _myFogMode.glId);
		_myGL.glHint(GL2.GL_FOG_HINT, GL.GL_NICEST);

		_myGL.glFogf(GL2.GL_FOG_DENSITY, _myFogDensity);
		_myGL.glFogf(GL2.GL_FOG_START, _myFogStart);
		_myGL.glFogf(GL2.GL_FOG_END, _myFogEnd);
		_myGL.glFogfv(GL2.GL_FOG_COLOR, _myFogColor.array(),0);
		_myGL.glFogi(GL2.GL_FOG_COORD_SRC, GL2.GL_FRAGMENT_DEPTH);
		_myGL.glEnable(GL2.GL_FOG);
	}

	/**
	 * This sets the fog color to whatever is supplied. This will be the color 
	 * used when doFog() is called. There can only be one fog color per scene, 
	 * it is not possible to have different objects fogged different colors.
	 * @param theColor int, the new color of the fog
	 */
	public void color(final float theRed, final float theGreen, final float theBlue){
		_myFogColor.set(theRed, theGreen, theBlue);
	}
	
	public void color(final float theGray){
		_myFogColor.set(theGray);
	}
	
	public void color(final int theRed, final int theGreen, final int theBlue){
		_myFogColor.set(theRed, theGreen, theBlue);
	}
	
	public void color(final int theGray){
		_myFogColor.set(theGray);
	}

	/**
	 * This command is used to set up how far from the camera you want the fog effect 
	 * to begin, and how far from the camera it should be until things are entirely fogged.
	 * Please note, that the camera is by default a distance away form the coordinates 0,0,0 
	 * so you may have to add a little to the "near" distance to get the fog starting where you want it.
	 * @param theFogStart float, how far from the camera the fog begins
	 * @param theForgEnd float, how far from the camera the fog ends
	 */
	public void range(final float theFogStart, final float theFogEnd){
		_myFogStart = theFogStart;
		_myFogEnd = theFogEnd;
	}
	
	public void density(final float theDensity){
		_myFogDensity = theDensity;
	}
	
	/**
	 * Specifies the equation to be used to compute the fog blend factor,  f.
	 * Three constants are accepted: LINEAR, EXP, EXP2. The initial fog mode is EXP.    
	 * @param theMode
	 */
	public void mode(final CCFogMode theMode){
		_myFogMode = theMode;
	}

	public void end(){
		_myGL.glDisable(GL2.GL_FOG);
	}
}
