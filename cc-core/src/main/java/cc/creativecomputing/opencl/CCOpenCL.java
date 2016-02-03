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
package cc.creativecomputing.opencl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Set;

import cc.creativecomputing.graphics.CCBufferObject;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.io.CCIOException;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLCommandQueue.Mode;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLException;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImage3d;
import com.jogamp.opencl.CLImageFormat;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLImageFormat.ChannelOrder;
import com.jogamp.opencl.CLImageFormat.ChannelType;
import com.jogamp.opencl.CLMemory.Mem;
import com.jogamp.opencl.gl.CLGLBuffer;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opencl.gl.CLGLTexture2d;
import com.jogamp.opencl.util.CLDeviceFilters;
import com.jogamp.opencl.util.CLPlatformFilters;

/**
 * @author christianriekoff
 *
 */
public class CCOpenCL {

	/**
	 * Call this method to get information on the existing opencl
	 * platform on your system.
	 */
	public static void printInfos() {
		
		// query all platforms
		CLPlatform[] myPlatForms = CLPlatform.listCLPlatforms();
		
		int p = 0;
		for(CLPlatform myPlatform:myPlatForms) {
			System.out.println("*** PLATFORM " + myPlatform.getName() + " ******************************************");
			System.out.println("  id     : " + p++);
			System.out.println("  name   : " + myPlatform.getName());
			System.out.println("  profile: " + myPlatform.getProfile());
			System.out.println("  version: " + myPlatform.getVersion());
			System.out.println("  vendor : " + myPlatform.getVendor());
			
			Set<String> myExtensions = myPlatform.getExtensions();
			System.out.println(" *** Extensions ******************************************");
			for(String myExtension:myExtensions) {
				System.out.println("    " + myExtension);
			}
			CLContext myContext = CLContext.create(myPlatform);
			int d = 0;
			for(CLDevice myDevice:myContext.getDevices()) {
				System.out.println(" *** Device " + myDevice.getName() + " ******************************************");
				System.out.println("   id           : " + d++);
				System.out.println("   name         : " + myDevice.getName());
				System.out.println("   profile      : " + myDevice.getProfile());
				System.out.println("   version      : " + myDevice.getVersion());
				System.out.println("   vendor       : " + myDevice.getVendor());
				System.out.println("   type         : " + myDevice.getType());
				System.out.println("   compute units: " + myDevice.getMaxComputeUnits());
				System.out.println("   gl sharing   : " + myDevice.isGLMemorySharingSupported());
			}
		}
	}
	
	private CLContext _myContext;
	private CLGLContext _myGLContext;
	private CLPlatform _myPlatForm;
	private CLDevice _myDevice;
	
	public CCOpenCL() {
		_myContext = CLContext.create();
		_myDevice = _myContext.getMaxFlopsDevice();
	}
	
	@SuppressWarnings("unchecked")
	public CCOpenCL(CCGraphics g){
		try{
			_myPlatForm = CLPlatform.getDefault(CLPlatformFilters.glSharing());
			_myDevice = _myPlatForm.getMaxFlopsDevice(CLDeviceFilters.glSharing());
			_myGLContext = CLGLContext.create(g.gl.getContext(),_myDevice);
			_myContext = _myGLContext;
		}catch(Exception e){
			e.printStackTrace();
			_myContext = CLContext.create( _myDevice);
		}
	}
	
	public CCOpenCL(String thePlatform, int theDevice, CCGraphics g){
		CLPlatform[] myPlatForms = CLPlatform.listCLPlatforms();
		for(CLPlatform myPlatform:myPlatForms){
			if(myPlatform.getName().toLowerCase().startsWith(thePlatform.toLowerCase())){
				_myPlatForm = myPlatform;
				break;
			}
		}
		if(_myPlatForm == null)throw new CLException("Could not find Platform:" + thePlatform);

		if(theDevice >= 0)_myDevice = _myPlatForm.listCLDevices()[theDevice];
		else _myDevice = _myPlatForm.getMaxFlopsDevice();
		
		if(g != null && _myDevice.isGLMemorySharingSupported()){
			_myGLContext = CLGLContext.create(g.gl.getContext(), _myDevice);
			_myContext = _myGLContext;
		}else{
			_myContext = CLContext.create( _myDevice);
		}
	}
	
	public CCOpenCL(CLPlatform thePlatForm){
		
	}
	
	public boolean isGLSharing() {
		return _myDevice.isGLMemorySharingSupported();
	}
	
	public CLContext context() {
		return _myContext;
	}
	
	public CLCommandQueue createCommandQueue(Mode...theModes){
		return _myDevice.createCommandQueue(theModes);
	}
	
	public CLGLBuffer<?> createFromGLBuffer(CCBufferObject theBufferObject, Mem...theFlags){
		return _myGLContext.createFromGLBuffer(theBufferObject.id(), theBufferObject.data().capacity(),theFlags);
	}
	
	public CLGLTexture2d<?> createCLGLTexture(CCTexture2D theTexture, Mem...theFlags){
		return _myGLContext.createFromGLTexture2d(
				theTexture.target().glID, 
				theTexture.id(), 0, theFlags);
	}
	
	private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }
	
	/**
	 * Creates an climage based on a texture data object, this is so far
	 * only working for data in byteBuffers, others will follow as needed
	 * @param theData
	 * @return
	 */
	public CLImage2d<FloatBuffer> createCLImage(CCTextureData theData){
		CCPixelFormat myFormat = theData.pixelFormat();
		int myTargetSize = theData.width() * theData.height() * 4;
		int myNumberOfPixels = theData.width() * theData.height();
		
		FloatBuffer myFloatBuffer = Buffers.newDirectFloatBuffer(myTargetSize);
		if(theData.buffer() instanceof ByteBuffer) {
			ByteBuffer myBuffer = (ByteBuffer)theData.buffer();
			for(int i = 0; i < myNumberOfPixels; i++) {
				int c = 0;
				for(c = 0; c < myFormat.numberOfChannels;c++) {
					myFloatBuffer.put(i * 4 + c, unsignedByteToInt(myBuffer.get(i * myFormat.numberOfChannels + myFormat.offsets[c])));
				}
				for(c = myFormat.numberOfChannels; c < 4;c++) {
					myFloatBuffer.put(i * 4 + c, 255);
				}
			}
			return _myContext.createImage2d(myFloatBuffer, theData.width(), theData.height(), new CLImageFormat(ChannelOrder.RGBA, ChannelType.FLOAT));
		}
		return null;
	}
	
	public CLBuffer<FloatBuffer> createCLFloatBuffer(CCTextureData theData, Mem theMem){
		int mySize = theData.width() * theData.height() * theData.pixelFormat().numberOfChannels;
		CCPixelFormat myFormat = theData.pixelFormat();
		FloatBuffer myFloatBuffer = Buffers.newDirectFloatBuffer(mySize);
		
		if(theData.buffer() instanceof ByteBuffer) {
			ByteBuffer myBuffer = (ByteBuffer)theData.buffer();
			for(int i = 0; i < mySize; i += myFormat.numberOfChannels) {
				for(int c = 0; c < myFormat.numberOfChannels;c++) {
					myFloatBuffer.put(i + c, unsignedByteToInt(myBuffer.get(i + myFormat.offsets[c])));
				}
			}
			return _myContext.createBuffer(myFloatBuffer, theMem);
		}
		return null;
	}
	
	public CLBuffer<FloatBuffer>createCLFloatBuffer(int theSize, Mem theMem){
		return _myContext.createFloatBuffer(theSize, theMem);
	}
	
	public CLImage3d<FloatBuffer> createCL3DImage(int theWidth, int theHeight, int theDepth){
		return _myContext.createImage3d(
			Buffers.newDirectFloatBuffer(theWidth * theHeight * 4), 
			theWidth, theHeight, theDepth,
			new CLImageFormat(ChannelOrder.RGBA, ChannelType.FLOAT)
		);
	}
	
	public CLImage2d<FloatBuffer> createCLImage(int theWidth, int theHeight){
		return _myContext.createImage2d(
			Buffers.newDirectFloatBuffer(theWidth * theHeight * 4), 
			theWidth, theHeight, 
			new CLImageFormat(ChannelOrder.RGBA, ChannelType.FLOAT)
		);
	}
	
	public CCCLTextureData createCLTextureData(int theWidth, int theHeight, CCCLPixelFormat thePixelFormat, CCCLPixelType thePixelType) {
		Buffer myBuffer = null;
		
		switch(thePixelType) {
		case FLOAT:
		case HALF_FLOAT:
			myBuffer = Buffers.newDirectFloatBuffer(theWidth * theHeight * thePixelFormat.format().numberOfChannels);
			break;
        default:
		
		}
		
		CLImage2d<Buffer> myImage = _myContext.createImage2d(
			myBuffer, 
			theWidth, theHeight, 
			new CLImageFormat(thePixelFormat.channelOrder(), thePixelType.channelType())
		);
		
		return new CCCLTextureData(myImage, thePixelFormat, thePixelType);
	}
	
	private CCTextureData createTextureData(FloatBuffer theFloatBuffer, int theWidth, int theHeight) {
		CCTextureData myResult = new CCTextureData(theWidth, theHeight);
		
		ByteBuffer myDstBuffer = (ByteBuffer)myResult.buffer();
		
		int myImageChannels = theFloatBuffer.capacity() / theWidth / theHeight;
		
//		myResult.pixelType(CCPixelType.FLOAT);
//		
//		switch(myImageChannels) {
//		case 1:
//			myResult.pixelFormat(CCPixelFormat.LUMINANCE);
//			break;
//		case 2:
//			myResult.pixelFormat(CCPixelFormat.LUMINANCE_ALPHA);
//			break;
//		case 3:
//			myResult.pixelFormat(CCPixelFormat.RGB);
//			break;
//		case 4:
//			myResult.pixelFormat(CCPixelFormat.RGBA);
//			break;
//		}
//		
//		System.out.println(myImageChannels);
		
		for(int x = 0; x < theWidth; x++) {
			for(int y = 0; y < theHeight; y++) {
				int iSrc = (y * theWidth + x) * myImageChannels;
				int iDst = (y * theWidth + x) * 4;
				
				int channel = 0;
				for(; channel < myImageChannels;channel++) {
					myDstBuffer.put(iDst + channel,(byte)(CCMath.saturate(theFloatBuffer.get(iSrc + channel)) * 255));
				}
				for(; channel < 4;channel++) {
					myDstBuffer.put(iDst + channel,(byte)(255));
				}
			}
		}
		
		myResult.buffer().rewind();
		return myResult;
	}
	
	/**
	 * This is so far only working for RGBA float images, more formats are
	 * going to be implemented on need
	 * @param theImage
	 * @return
	 */
	public CCTextureData createTextureData(CLImage2d<FloatBuffer> theImage) {
		return createTextureData(theImage.getBuffer(), theImage.width, theImage.height);
	}
	
	public CCTextureData createTextureData(CLBuffer<FloatBuffer> theBuffer, int theWidth, int theHeight) {
		return createTextureData(theBuffer.getBuffer(), theWidth, theHeight);
	}
	
	public CLProgram createProgram(String...theFileName) {
		return createProgram(CCOpenCLUtil.class, theFileName);
	}
	
	public CLProgram createProgram(Class<?> theClass, String... theFileName) {
		StringBuffer myCLCode = new StringBuffer();

		for (String myFile : theFileName) {
			try {
				System.out.println(myFile + ":" + theClass.getResource(myFile));
				BufferedReader myReader = CCIOUtil.createReader(theClass.getResourceAsStream(myFile));
				String myLine;
				while ((myLine = myReader.readLine()) != null) {
					myCLCode.append(myLine);
					myCLCode.append("\n");
				}
			} catch (IOException e) {
				throw new CCIOException(e);
			} catch(NullPointerException e) {
				throw new CCIOException("Could not load file:" + theClass.getResource("") + myFile);
			}
		}
		return _myContext.createProgram(myCLCode.toString());

	}
}
