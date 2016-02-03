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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 * 
 */
public abstract class CCAbstractGraphics<GLType extends GL2GL3> {

	/**
	 * Gives you the possibility to directly access OPENGLs utility functions
	 */
	public GLU glu;

	public GLType gl;
	
	/**
	 * width of the parent application
	 */
	public int width;

	/**
	 * height of the parent application
	 */
	public int height;
	
	protected CCTexture[] _myTextures;

	protected CCAbstractGraphics(GLType theGL) {
		gl = theGL;
		glu = new GLU();
	}
	
	public void beginDraw() {
		
	}
	
	public void endDraw() {
		
	}

	// /////////////////////////////////////////////////
	//
	// OPENGL INFORMATIONS
	//
	// ////////////////////////////////////////////////

	private int[] _myIntegerGet = new int[1];

	public int getInteger(int theGLIID) {
		gl.glGetIntegerv(theGLIID, _myIntegerGet, 0);
		return _myIntegerGet[0];
	}

	public IntBuffer getIntBuffer(int theGLID, int theNumberOfValues) {
		final IntBuffer myResult = IntBuffer.allocate(theNumberOfValues);
		gl.glGetIntegerv(theGLID, myResult);
		myResult.rewind();
		return myResult;
	}

	public int[] getIntArray(int theGLID, int theNumberOfValues) {
		int[] result = new int[theNumberOfValues];
		gl.glGetIntegerv(theGLID, result, 0);
		return result;
	}

	private float[] _myFloatGet = new float[1];

	public float getFloat(int theGLIID) {
		gl.glGetFloatv(theGLIID, _myFloatGet, 0);
		return _myFloatGet[0];
	}

	public FloatBuffer getFloatBuffer(int theGLID, int theNumberOfValues) {
		final FloatBuffer myResult = FloatBuffer.allocate(theNumberOfValues);
		gl.glGetFloatv(theGLID, myResult);
		myResult.rewind();
		return myResult;
	}

	public float[] getFloatArray(int theGLID, int theNumberOfValues) {
		float[] result = new float[theNumberOfValues];
		gl.glGetFloatv(theGLID, result, 0);
		return result;
	}

	public String getString(int theGLID) {
		return gl.glGetString(theGLID);
	}

	/**
	 * Returns the name of the hardware vendor.
	 * 
	 * @return the name of the hardware vendor
	 */
	public String vendor() {
		return getString(GL.GL_VENDOR);
	}

	/**
	 * Returns a brand name or the name of the vendor dependent on the OPENGL implementation.
	 * 
	 * @return brand name or name of the vendor
	 */
	public String renderer() {
		return getString(GL.GL_RENDERER);
	}

	/**
	 * returns the version number followed by a space and any vendor-specific information.
	 * 
	 * @return the version number
	 */
	public String version() {
		return getString(GL.GL_VERSION);
	}

	/**
	 * Returns an array with all the extensions that are available on the current hardware setup.
	 * 
	 * @return the available extensions
	 */
	public String[] extensions() {
		return getString(GL.GL_EXTENSIONS).split(" ");
	}

	/**
	 * Returns true if the given extension is available at the current hardware setup.
	 * 
	 * @param theExtension extension to check
	 * @return true if the extension is available otherwise false
	 */
	public boolean isExtensionSupported(final String theExtension) {
		for (String myExtension : extensions()) {
			if (myExtension.equals(theExtension))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns an array with the pixel formats that are allowed for compression
	 * @return array with the pixel formats that are allowed for compression
	 */
	public CCPixelFormat[] compressedTextureFormats(){
		int myNumberOfFormats = getInteger(GL2.GL_NUM_COMPRESSED_TEXTURE_FORMATS);
		int[] myFormats = getIntArray(GL2.GL_COMPRESSED_TEXTURE_FORMATS, myNumberOfFormats);
		CCPixelFormat[] myPixelFormats = new CCPixelFormat[myNumberOfFormats];
		for(int myFormat = 0; myFormat < myFormats.length; myFormat++){
			myPixelFormats[myFormat] = CCPixelFormat.pixelFormat(myFormats[myFormat]);
		}
		return myPixelFormats;
	}

	/**
	 * true if you want to report that no error occurred
	 */
	private boolean _myReportNoError = false;
	protected boolean _myReportErrors = true;

	/**
	 * Call this method to check for drawing errors. cc checks for drawing errors at the end of each frame
	 * automatically. However only the last error will be reported. You can call this method for debugging to find where
	 * errors occur. Error codes are cleared when checked, and multiple error flags may be currently active. To retrieve
	 * all errors, call this function repeatedly until you get no error.
	 * 
	 * @shortdesc Use this method to check for drawing errors.
	 */
	public void checkError(final String theString) {
		switch (gl.glGetError()) {
		case GL.GL_NO_ERROR:
			if (_myReportNoError)
				CCLog.error(theString + " # NO ERROR REPORTED");
			return;
		case GL.GL_INVALID_ENUM:
			CCLog.error(theString + " # INVALID ENUMERATION REPORTED. check for errors in OPENGL calls with constants.");
			return;
		case GL.GL_INVALID_VALUE:
			CCLog.error(theString + "# INVALID VALUE REPORTED. check for errors with passed values that are out of a defined range.");
			return;
		case GL.GL_INVALID_OPERATION:
			CCLog.error(theString + "# INVALID OPERATION REPORTED. check for function calls that are invalid in the current graphics state.");
			return;
		
		case GL2ES1.GL_STACK_OVERFLOW:
			CCLog.error(theString + "# STACK OVERFLOW REPORTED. check for errors in matrix operations");
			return;
		case GL2ES1.GL_STACK_UNDERFLOW:
			CCLog.error(theString + "# STACK UNDERFLOW REPORTED. check for errors  in matrix operations");
			return;
		
		case GL.GL_OUT_OF_MEMORY:
			CCLog.error(theString + "# OUT OF MEMORY. not enough memory to execute the commands");
			return;
		case GL2.GL_TABLE_TOO_LARGE:
			CCLog.error(theString + "# TABLE TOO LARGE.");
			return;
		}
	}

	public void checkError() {
		checkError("");
	}

	/**
	 * Use this method to tell cc if it should report no error
	 * 
	 * @param theReportNoError
	 */
	public void reportNoError(final boolean theReportNoError) {
		_myReportNoError = theReportNoError;
	}

	public void reportError(final boolean theReportError) {
		_myReportErrors = theReportError;
	}
	
	public abstract void updateGL(GLAutoDrawable theDrawable);

	public abstract void reshape(int theX, int theY, int theWidth, int theHeight);
	
	/**
	 * Called in response to a resize event, handles setting the
	 * new width and height internally.
	 * @invisible
	 */
	public void resize(final int theWidth, final int theHeight){ // ignore
		width = theWidth;
		height = theHeight;
	}
	
	
}
