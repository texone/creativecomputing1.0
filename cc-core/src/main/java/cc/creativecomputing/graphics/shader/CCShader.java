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
package cc.creativecomputing.graphics.shader;

import javax.media.opengl.GL;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCIOUtil;


public abstract class CCShader {
	
	protected boolean _myIsVertexShaderEnabled = false;
	protected final boolean _myIsVertexShaderSupported;
	protected boolean _myIsFragmentShaderEnabled = false;
	protected final boolean _myIsFragmentShaderSupported;
	
	protected String _myVertexEntry;
	protected String _myFragmentEntry;

	public CCShader(final String theVertexShaderFile, final String theFragmentShaderFile) {
		this(theVertexShaderFile,null,theFragmentShaderFile,null);
	}
	
	public CCShader(final String[] theVertexShaderFile, final String[] theFragmentShaderFile) {
		this(theVertexShaderFile,null,theFragmentShaderFile,null);
	}
	
	public CCShader(
		final String theVertexShaderFile, 
		final String theVertexEntry, final String theFragmentShaderFile, 
		final String theFragmentEntry
	) {
		this(new String[] {theVertexShaderFile}, theVertexEntry, new String[] {theFragmentShaderFile},theFragmentEntry);
	}
	
	public CCShader(
		final String[]theVertexShaderFile, 
		final String theVertexEntry, final String[] theFragmentShaderFile, 
		final String theFragmentEntry
	) {
		GL gl = CCGraphics.currentGL();
		String extensions = gl.glGetString(GL.GL_EXTENSIONS);
		
		_myIsVertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1;
		_myIsFragmentShaderSupported = extensions.indexOf("GL_ARB_fragment_shader") != -1;
		
		_myVertexEntry = theVertexEntry;
		_myFragmentEntry = theFragmentEntry;
		
		initShader();
		
		if(theVertexShaderFile != null && theVertexShaderFile[0] != null)loadVertexShader(theVertexShaderFile);
		if(theFragmentShaderFile != null && theFragmentShaderFile[0] != null)loadFragmentShader(theFragmentShaderFile);
	}
	
	/**
	 * Takes the given files and merges them to one String. 
	 * This method is used to combine the different shader sources and get rid of the includes
	 * inside the shader files.
	 * @param theFiles
	 * @return
	 */
	protected String buildSource(final String...theFiles) {
		StringBuffer myBuffer = new StringBuffer();
		
		for(String myFile:theFiles) {
			for(String myLine:CCIOUtil.loadStrings(myFile)) {
				myBuffer.append(myLine);
				myBuffer.append("\n");
			}
		}
		
		return myBuffer.toString();
	}
	
	/**
	 * Overwrite this method for initialization steps that have to be done
	 * before loading and binding the shaders
	 */
	public abstract void initShader();

	public abstract void loadVertexShader(final String...theFiles);

	public abstract void loadFragmentShader(final String...theFile);

	public abstract void load();

	public abstract void start();

	public abstract void end();
}
