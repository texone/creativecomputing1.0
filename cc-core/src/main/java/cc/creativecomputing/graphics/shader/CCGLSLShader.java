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

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector1f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector3i;
import cc.creativecomputing.math.CCVector4f;

public class CCGLSLShader extends CCShader{
	
	/**
	 * Tries to create a shader based on the given class and the following name scheme. You need to put 
	 * shader files with the names vertex.glsl, geometry.glsl, fragment.glsl into the package folder of
	 * the given class, at least a vertex or fragment shader has to be existent.
	 * <p>
	 * If the shader can not be loaded shader exception is thrown
	 * @param theClass class to look for the resources
	 * @return shader based on the given resources
	 */
	public static CCGLSLShader createFromResource(Class<?> theClass) {
		URL myVertexShader = theClass.getResource("vertex.glsl");
		URL myGeometryShader = theClass.getResource("geometry.glsl");
		URL myFragmentShader = theClass.getResource("fragment.glsl");
		
		if(myVertexShader == null && myGeometryShader == null && myFragmentShader == null) {
			throw new CCShaderException(
				"Could not load Shader based on the given resource.\b" +
				"Note to make this work you need put shader source files with the name scheme:\n" +
				" vertex.glsl\n geometry.glsl\n fragment.glsl\n" +
				"inside the package folder of the given class. " +
				"At least a vertex or fragment shader need to be found."
			);
		}
		
		String myVertexPath = myVertexShader != null ? myVertexShader.getPath() : null;
		String myGeometryPath = myGeometryShader != null ? myGeometryShader.getPath() : null;
		String myFragmentPath = myFragmentShader != null ? myFragmentShader.getPath() : null;
		
		return new CCGLSLShader(myVertexPath, myGeometryPath, myFragmentPath);
	}
	
	/**
	 * Shortcut to {@linkplain #createFromResource(Class)} by taking the class from the object.
	 * @param theObject
	 * @return shader based on the given resources
	 */
	public static CCGLSLShader createFromResource(Object theObject) {
		return createFromResource(theObject.getClass());
	}
	
	public static enum CCGeometryInputType{
		
		POINTS(GL.GL_POINTS),
		LINES(GL.GL_LINES),
		//LINES_ADJACENCY(GL2.GL_LINES_ADJACENCY_EXT),
		LINES_ADJACENCY(GL2.GL_LINES_ADJACENCY_ARB),
		TRIANGLES(GL.GL_TRIANGLES),
		//TRIANGLES_ADJACENCY(GL2.GL_TRIANGLES_ADJACENCY_EXT);
		TRIANGLES_ADJACENCY(GL2.GL_TRIANGLES_ADJACENCY_ARB);
		
		int glID;
		
		private CCGeometryInputType(final int theGLID) {
			glID = theGLID;
		}
	}
	
	public static enum CCGeometryOutputType{
		
		POINTS(GL.GL_POINTS),
		LINE_STRIP(GL.GL_LINE_STRIP),
		TRIANGLE_STRIP(GL.GL_TRIANGLE_STRIP);
		
		int glID;
		
		private CCGeometryOutputType(final int theGLID) {
			glID = theGLID;
		}
	}
	
	protected int _myProgramObject;
	
	protected int _myVertexShaderID;
	protected int _myGeometryShaderID;
	protected int _myFragmentShaderID;
	
	protected String[] _myVertexFiles;
	protected String[] _myFragmentFiles;
	protected String[] _myGeometryFiles;

	public CCGLSLShader(final String theVertexShaderFile, final String theFragmentShaderFile) {
		super(theVertexShaderFile,theFragmentShaderFile);
	}
	
	public CCGLSLShader(final String[] theVertexShaderFile, final String[] theFragmentShaderFile) {
		super(theVertexShaderFile,theFragmentShaderFile);
	}
	
	public CCGLSLShader(
		final String theVertexShaderFile, 
		final String theGeometryShaderFile, 
		final String theFragmentShaderFile
	) {
		super(theVertexShaderFile,theFragmentShaderFile);

		if(theGeometryShaderFile != null)loadShader(GL2.GL_GEOMETRY_SHADER_ARB, theGeometryShaderFile);
	}
	
	/**
	 * Returns the underlying gl program id
	 * @return the underlying gl program id
	 */
	public int glProgram() {
		return _myProgramObject;
	}

	@Override
	public void initShader() {
		GL2 gl = CCGraphics.currentGL();
		_myProgramObject = gl.glCreateProgramObjectARB();
	}
	
	private void loadShader(int theShaderID, final String...theFiles) {
		if(theFiles == null || theFiles.length <= 0)return;
		String shaderSource = buildSource(theFiles);

		GL2 gl = CCGraphics.currentGL();
		//create an object that act as vertex shader container
		
		// add the source code of the hader to the object
		gl.glShaderSourceARB(theShaderID, 1, new String[] { shaderSource },(int[]) null, 0);
		
		// finally compile the shader
		gl.glCompileShader(theShaderID);
		
		checkLogInfo(gl, theShaderID, theFiles);
		
		// attach vertex shader to this program object
		gl.glAttachObjectARB(_myProgramObject, theShaderID);

		// delete shader objects this will mark them for deletion
		// shader object will be deleted when the program object is deleted
		gl.glDeleteObjectARB(theShaderID);
	}

	@Override
	public void loadVertexShader(final String...theFiles) {
		GL2 gl = CCGraphics.currentGL();
		_myVertexShaderID = gl.glCreateShaderObjectARB(GL2.GL_VERTEX_SHADER);
		_myVertexFiles = theFiles;
		loadShader(_myVertexShaderID, _myVertexFiles);
	}

	public void loadGeometryShader(final String...theFiles) {
		GL2 gl = CCGraphics.currentGL();
		_myGeometryShaderID = gl.glCreateShaderObjectARB(GL2.GL_GEOMETRY_SHADER_ARB);
		_myGeometryFiles = theFiles;
		loadShader(_myGeometryShaderID, _myGeometryFiles);
	}

	@Override
	public void loadFragmentShader(final String...theFiles) {
		GL2 gl = CCGraphics.currentGL();
		_myFragmentShaderID = gl.glCreateShaderObjectARB(GL2.GL_FRAGMENT_SHADER);
		_myFragmentFiles = theFiles;
		
		try {
			loadShader(_myFragmentShaderID, _myFragmentFiles);
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	@Override
	/**
	 * @invisible
	 */
	public void load() {
		GL2 gl = CCGraphics.currentGL();
		gl.glLinkProgram(_myProgramObject);
//		gl.glValidateProgram(_myProgramObject);
		checkLogInfo(gl, _myProgramObject, null);
	}
	
	private long _myLastModified = System.currentTimeMillis();
	
	public void checkUpdates(){
		if(_myVertexFiles != null)
			for(String myVertexFile:_myVertexFiles){
				File myFile = new File(CCIOUtil.dataPath(myVertexFile));
				if(myFile.lastModified() > _myLastModified){
					_myLastModified = myFile.lastModified();
					reload();
					return;
				}
			}
		
		if(_myFragmentFiles != null)
			for(String myFragmentFile:_myFragmentFiles){
				File myFile = new File(CCIOUtil.dataPath(myFragmentFile));
				if(myFile.lastModified() > _myLastModified){
					_myLastModified = myFile.lastModified();
					reload();
					return;
				}
			}
		
		if(_myGeometryFiles != null)
			for(String myVertexFile:_myGeometryFiles){
				File myFile = new File(CCIOUtil.dataPath(myVertexFile));
				if(myFile.lastModified() > _myLastModified){
					_myLastModified = myFile.lastModified();
					reload();
					return;
				}
			}
	}
	
	public void reload() {
//		finalize();
//		initShader();
		loadShader(_myVertexShaderID, _myVertexFiles);
		loadShader(_myGeometryShaderID, _myGeometryFiles);
		loadShader(_myFragmentShaderID, _myFragmentFiles);
		load();
	}
	
	private boolean _myIsShaderInUse = false;

	@Override
	public void start() {
		GL2 gl = CCGraphics.currentGL();
		gl.glUseProgram(_myProgramObject);
		_myIsShaderInUse = true;
	}

	@Override
	public void end() {
		_myIsShaderInUse = false;
		GL2 gl = CCGraphics.currentGL();
		gl.glUseProgram(0);
	}

	void checkLogInfo(GL2 gl, int theObject, String[] theFiles) {
		IntBuffer iVal = CCBufferUtil.newIntBuffer(1);
		gl.glGetObjectParameterivARB(theObject, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB,iVal);

		int length = iVal.get();
		if (length <= 1) {
			return;
		}
		ByteBuffer infoLog = CCBufferUtil.newByteBuffer(length);
		iVal.flip();
		gl.glGetInfoLogARB(theObject, length, iVal, infoLog);
		byte[] infoBytes = new byte[length];
		infoLog.get(infoBytes);
		String myReply = new String(infoBytes);
		if(myReply.startsWith("WARNING:"))return;
		
		if(theFiles != null) {
			StringBuffer myReplyBuffer = new StringBuffer("Problem inside the following shader:");
			for(String myFile:theFiles) {
				myReplyBuffer.append("\n");
				myReplyBuffer.append(myFile);
			}
			myReplyBuffer.append("\n");
			myReplyBuffer.append("The following Problem occured:\n");
			myReplyBuffer.append(myReply);
			myReplyBuffer.append("\n");
			myReply = myReplyBuffer.toString();
		}
		
		throw new CCShaderException(myReply);
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void finalize(){
		GL2 gl = CCGraphics.currentGL();
		gl.glDeleteObjectARB(_myProgramObject);
	}
	
// SETTINGS FOR GEOMETRY SHADER
	
	/**
	 * The input primitive type is a parameter of the program object, and must be
	 * set before loading the shader with the {@link #load()} function. by calling ProgramParameteriARB with <pname> set to
    GEOMETRY_INPUT_TYPE_ARB and <value> set to one of POINTS, LINES,
    LINES_ADJACENCY_ARB, TRIANGLES or TRIANGLES_ADJACENCY_ARB. This setting
    will not be in effect until the next time LinkProgram has been called
	 */
	public void geometryInputType(final CCGeometryInputType theInputType) {
		GL2 gl = CCGraphics.currentGL();
		//gl.glProgramParameteri(_myProgramObject, GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, theInputType.glID);
		gl.glProgramParameteriARB(_myProgramObject, GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, theInputType.glID);
	}
	
	public void geometryVerticesOut(final int theVerticesOut) {
		GL2 gl = CCGraphics.currentGL();
		//gl.glProgramParameteri(_myProgramObject, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, theVerticesOut);
		gl.glProgramParameteriARB(_myProgramObject, GL2.GL_GEOMETRY_VERTICES_OUT_ARB, theVerticesOut);
	}
	
	public int maximumGeometryOutputVertices() {
		IntBuffer temp = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_GEOMETRY_OUTPUT_VERTICES_ARB,temp);
		return temp.get();
	}
	
	public void geometryOutputType(final CCGeometryOutputType theOutputType) {
		GL2 gl = CCGraphics.currentGL();
		//gl.glProgramParameteri(_myProgramObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, theOutputType.glID);
		gl.glProgramParameteriARB(_myProgramObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_ARB, theOutputType.glID);
	}

	int getAttribLocation(String name) {
		GL2 gl = CCGraphics.currentGL();
		return (gl.glGetAttribLocation(_myProgramObject, name));
	}

	public int uniformLocation(final String theName) {
		GL2 gl = CCGraphics.currentGL();
		return gl.glGetUniformLocation(_myProgramObject, theName);
	}
	
	public void uniform1i(final int theLocation, final int theValue){
		GL2 gl = CCGraphics.currentGL();
		gl.glUniform1i(theLocation, theValue);
	}
	
	public void uniform1i(final String theName, final int theValue){
		uniform1i(uniformLocation(theName), theValue);
	}
	
	public void uniform(final int theLocation, final boolean theValue){
		GL2 gl = CCGraphics.currentGL();
		if(theValue)gl.glUniform1i(theLocation, 1);
		else gl.glUniform1i(theLocation, 0);
	}
	
	public void uniform(final String theName, final boolean theValue){
		uniform(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final float theValue){
		CCGraphics.currentGL().glUniform1f(theLocation, theValue);
	}
	
	public void uniform1f(final String theName, final float theValue){
		uniform1f(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final CCVector1f theValue){
		uniform1f(theLocation, theValue.x);
	}
	
	public void uniform1f(final String theName, final CCVector1f theValue){
		uniform1f(theName, theValue.x);
	}
	
	public void uniform1fv(final int theLocation, final List<?> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 4);
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector1f){
				CCVector1f myVector = (CCVector1f)myObject;
				myData.put(myVector.x);
			}else if(myObject instanceof Float){
				Float myVector = (Float)myObject;
				myData.put(myVector);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform1fv(theLocation, theVectors.size(), myData);
	}
	
	public void uniform1fv(final String theName, final List<?> theVectors){
		uniform1fv(uniformLocation(theName), theVectors);
	}
	
	public void uniform1fv(final int theLocation, float...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.wrap(theValues);
		myData.rewind();
		CCGraphics.currentGL().glUniform1fv(theLocation, theValues.length, myData);
	}
	
	public void uniform1fv(final String theName, float...theValues){
		uniform1fv(uniformLocation(theName), theValues);
	}
	
	public void uniform1iv(final int theLocation, int...theValues){
		if(theValues.length == 0)return;
		
		IntBuffer myData = IntBuffer.wrap(theValues);
		myData.rewind();
		CCGraphics.currentGL().glUniform1iv(theLocation, theValues.length, myData);
	}
	
	public void uniform1iv(final String theName, int...theValues){
		uniform1iv(uniformLocation(theName), theValues);
	}
	
	
	public void uniform2f(final int theLocation, final float theX, final float theY){
		CCGraphics.currentGL().glUniform2f(theLocation, theX, theY);
	}
	
	public void uniform2f(final int theLocation, final CCVector2f theVector){
		uniform2f(theLocation, theVector.x, theVector.y);
	}
	
	public void uniform2f(final String theName, final float theX, final float theY){
		uniform2f(uniformLocation(theName), theX, theY);
	}
	
	public void uniform2f(final String theName, final CCVector2f theValue){
		uniform2f(uniformLocation(theName), theValue);
	}
	
	public void uniform2fv(final int theLocation, CCVector2f...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 2);
		for(CCVector2f myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
			}else{
				myData.put(myValue.x);
				myData.put(myValue.y);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform2fv(theLocation, theValues.length, myData);
	}
	
	public void uniform2fv(final String theName, CCVector2f...theValues){
		uniform2fv(uniformLocation(theName), theValues);
	}
	
	public void uniform3f(final int theLocation, final float theX, final float theY, final float theZ){
		CCGraphics.currentGL().glUniform3f(theLocation, theX, theY, theZ);
	}
	
	public void uniform3f(final int theLocation, final CCVector3f theVector){
		uniform3f(theLocation, theVector.x, theVector.y, theVector.z);
	}
	
	public void uniform3f(final int theLocation, final CCColor theColor){
		uniform3f(theLocation, theColor.r, theColor.g, theColor.b);
	}
	
	public void uniform3f(final String theName, final float theX, final float theY, final float theZ){
		uniform3f(uniformLocation(theName), theX, theY, theZ);
	}
	
	public void uniform3f(final String theName, final CCVector3f theValue){
		uniform3f(uniformLocation(theName), theValue);
	}
	
	public void uniform3f(final String theName, final CCColor theValue){
		uniform3f(uniformLocation(theName), theValue);
	}
	
	public void uniform3fv(final int theLocation, CCVector3f...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 3);
		for(CCVector3f myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
				myData.put(0);
			}else{
				myData.put(myValue.x);
				myData.put(myValue.y);
				myData.put(myValue.z);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform3fv(theLocation, theValues.length, myData);
	}
	
	public void uniform3fv(final String theName, CCVector3f...theValues){
		uniform3fv(uniformLocation(theName), theValues);
	}
	
	public void uniform3fv(final int theLocation, CCColor...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 3);
		for(CCColor myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
				myData.put(0);
			}else{
				myData.put(myValue.r);
				myData.put(myValue.g);
				myData.put(myValue.b);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform3fv(theLocation, theValues.length, myData);
	}
	
	public void uniform3fv(final String theName, CCColor...theValues){
		uniform3fv(uniformLocation(theName), theValues);
	}
	
	public void uniform4fv(final int theLocation, CCVector4f...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 4);
		for(CCVector4f myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
				myData.put(0);
				myData.put(0);
			}else{
				myData.put(myValue.x);
				myData.put(myValue.y);
				myData.put(myValue.z);
				myData.put(myValue.w);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform4fv(theLocation, theValues.length, myData);
	}
	
	public void uniform4fv(final String theName, CCVector4f...theValues){
		uniform4fv(uniformLocation(theName), theValues);
	}
	
	public void uniform3iv(final int theLocation, CCVector3i...theValues){
		if(theValues.length == 0)return;
		
		IntBuffer myData = IntBuffer.allocate(theValues.length * 3);
		for(CCVector3i myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
				myData.put(0);
			}else{
				myData.put(myValue.x);
				myData.put(myValue.y);
				myData.put(myValue.z);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform3iv(theLocation, theValues.length, myData);
	}
	
	public void uniform3iv(final String theName, CCVector3i...theValues){
		uniform3iv(uniformLocation(theName), theValues);
	}
	
	public void uniform4f(int theLocation, final float theX, final float theY, final float theZ, float theW){
		CCGraphics.currentGL().glUniform4f(theLocation, theX, theY, theZ, theW);
	}
	
	public void uniform4f(final String theName, final float theX, final float theY, final float theZ, float theW){
		uniform4f(uniformLocation(theName), theX, theY, theZ, theW);
	}
	
	public void uniform4f(final int theLocation, final CCVector4f theVector){
		uniform4f(theLocation, theVector.x, theVector.y, theVector.z, theVector.w);
	}
	
	public void uniform4f(final int theLocation, final CCColor theColor){
		uniform4f(theLocation, theColor.r, theColor.g, theColor.b, theColor.a);
	}
	
	public void uniform4f(final String theName, final CCVector4f theValue){
		uniform4f(uniformLocation(theName), theValue);
	}
	
	public void uniform4f(final String theName, final CCColor theValue){
		uniform4f(uniformLocation(theName), theValue);
	}
	
	public void uniform4fv(final int theLocation, final List<?> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 4);
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector4f){
				CCVector4f myVector = (CCVector4f)myObject;
				myData.put(myVector.x);
				myData.put(myVector.y);
				myData.put(myVector.z);
				myData.put(myVector.w);
			}else if(myObject instanceof CCVector3f){
				CCVector3f myVector = (CCVector3f)myObject;
				myData.put(myVector.x);
				myData.put(myVector.y);
				myData.put(myVector.z);
				myData.put(0);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform4fv(theLocation, theVectors.size(), myData);
	}
	
	public void uniform4fv(final String theName, final List<?> theVectors){
		uniform4fv(uniformLocation(theName), theVectors);
	}
	
	
	public void uniform(final int theLocation, final CCColor theColor){
		if(!_myIsShaderInUse)throw new CCShaderException("You can only change values if a shader is in use. See start() and end() method of CCShader.");
		GL2 gl = CCGraphics.currentGL();
		gl.glUniform4f(theLocation, theColor.red(), theColor.green(), theColor.blue(), theColor.alpha());
	}
	
	public void uniform(final String theName, final CCColor theColor){
		uniform(uniformLocation(theName), theColor);
	}
	
	public void uniformMatrix4f(final int theLocation, CCMatrix4f theMatrix) { 
		GL2 gl = CCGraphics.currentGL();
		gl.glUniformMatrix4fv(theLocation, 1, false, theMatrix.toFloatBuffer());
	}
	
	public void uniformMatrix4f(final String theName, CCMatrix4f theMatrix) {
		uniformMatrix4f(uniformLocation(theName), theMatrix);
	}
	
	public void uniformMatrix4fv(final int theLocation, final List<CCMatrix4f> theMatrices){
		if(theMatrices.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theMatrices.size() * 16);
		for(CCMatrix4f myMatrix:theMatrices){
			if(myMatrix == null)continue;
			myData.put(myMatrix.toFloatBuffer());
		}
		myData.rewind();
		CCGraphics.currentGL().glUniformMatrix4fv(theLocation, theMatrices.size(), false, myData);
	}
	
	public void uniformMatrix4fv(final String theName, final List<CCMatrix4f> theMatrices){
		uniformMatrix4fv(uniformLocation(theName), theMatrices);
	}
	

	
	public void uniformMatrix4fv(final int theLocation, final CCMatrix4f...theMatrices){
		if(theMatrices.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theMatrices.length * 16);
		for(CCMatrix4f myMatrix:theMatrices){
			if(myMatrix == null)continue;
			myData.put(myMatrix.toFloatBuffer());
		}
		myData.rewind();
		CCGraphics.currentGL().glUniformMatrix4fv(theLocation, theMatrices.length, false, myData);
	}
	
	public void uniformMatrix4fv(final String theName, final CCMatrix4f...theMatrices){
		uniformMatrix4fv(uniformLocation(theName), theMatrices);
	}
	
	//////////////////////////////////////////////////
	//
	// GET INFORMATION ON SHADER SUPPORT
	//
	//////////////////////////////////////////////////
	
	/**
	 * Defines the number of active vertex attributes that are available. 
	 * The minimum legal value is 16.
	 */
	public int maximumVertexAttributes(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VERTEX_ATTRIBS_ARB, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of components (i.e., floating-point values) that 
	 * are available for vertex shader uniform variables. The minimum legal value is 512.
	 * @return
	 */
	public int maximumVertexUniformComponents(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VERTEX_UNIFORM_COMPONENTS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of floating-point variables available 
	 * for varying variables. The minimum legal value is 32.
	 * @return
	 */
	public int maximumVariyingFloats(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VARYING_FLOATS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of hardware units that can be used to 
	 * access texture maps from the vertex processor. The minimum legal value is 0.
	 * @return
	 */
	public int maximumVertexTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the total number of hardware units that can be used to access texture 
	 * maps from the vertex processor and the fragment processor combined. The minimum legal value is 2.
	 * @return
	 */
	public int maximumCombinedTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the total number of hardware units that can be used to access texture 
	 * maps from the fragment processor. The minimum legal value is 2.
	 * @return
	 */
	public int maximumTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_IMAGE_UNITS_ARB, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of texture coordinate sets that are available. 
	 * The minimum legal value is 2.
	 * @return
	 */
	public int maximumTextureCoords(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_COORDS_ARB, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of components (i.e., floating-point values) that 
	 * are available for fragment shader uniform variables. The minimum legal value is 64.
	 * @return
	 */
	public int maximumFragmentUniformComponents(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, myResult);
		return myResult.get();
	}
	
	public void printSpecs(){
		GL2 gl = CCGraphics.currentGL();
		System.out.println(gl.glGetString(GL.GL_EXTENSIONS));
		System.out.println("GLSL SHADER SUPPORT");
		System.out.println("########################");
		System.out.println("maximumVertexAttributes:         "+maximumVertexAttributes());
		System.out.println("maximumVertexUniformComponents:  "+maximumVertexUniformComponents());
		System.out.println("maximumVariyingFloats:           "+maximumVariyingFloats());
		System.out.println("maximumVertexTextureImageUnits:  "+maximumVertexTextureImageUnits());
		System.out.println("maximumCombinedTextureImageUnits:"+maximumCombinedTextureImageUnits());
		System.out.println("maximumTextureImageUnits:        "+maximumTextureImageUnits());
		System.out.println("maximumTextureCoords:            "+maximumTextureCoords());
		System.out.println("maximumFragmentUniformComponents:"+maximumFragmentUniformComponents());
	}
}
