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
package cc.creativecomputing.demo.graphics.texture;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.util.CCStopWatch;
import cc.creativecomputing.util.logging.CCLog;

public class CCExportCompressedTextureDemo extends CCApp {
	
	private CCTexture2D _myUncompressed;
	private CCTexture2D _myCompressed;
		
	@CCControl(name = "filter")
	private CCTextureFilter _cFilter = CCTextureFilter.NEAREST;
	@CCControl(name = "mipmap filter")
	private CCTextureMipmapFilter _cMipmap_filter = CCTextureMipmapFilter.NEAREST;

	@Override
	public void setup() {
		for(CCPixelFormat myFormat:g.compressedTextureFormats()){
			CCLog.info(myFormat);
		}
		
		addControls("app", "app", this);

		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myUncompressed = new CCTexture2D(myAttributes);
		_myUncompressed.data(CCTextureIO.newTextureData("demo/textures/1080_00003.tga"));
		
		_myCompressed = new CCTexture2D(myAttributes);
		boolean successful = _myCompressed.compressData(CCTextureIO.newTextureData("demo/textures/1080_00003.tga"));
		if(successful){
			CCLog.info(_myCompressed.estimatedMemorySize() + " : " + _myCompressed.internalFormat());
		}
		
		CCTextureData myCompressedData = _myCompressed.data();
		CCLog.info(myCompressedData.buffer());
		CCIOUtil.saveBytes("test/1080_00003.cus", ((ByteBuffer)myCompressedData.buffer()).array());
//		CCTextureIO.write(myCompressedData, "test/1080_00003.dds");
		
		CCLog.info(myCompressedData.mustFlipVertically());
		
		_myCompressed = new CCTexture2D(myAttributes);
		
	}

	@Override
	public void update(final float theDeltaTime) {
		_myUncompressed.textureFilter(_cFilter);
		_myUncompressed.textureMipmapFilter(_cMipmap_filter);
		
		try {
		CCStopWatch.instance().startWatch("data");
		File file = CCIOUtil.dataFile("demo/textures/1080_00003.cus");
		FileInputStream fis;
		
			fis = new FileInputStream(file);
		
		FileChannel chan = fis.getChannel();
		ByteBuffer buf = chan.map(FileChannel.MapMode.READ_ONLY, 0, (int) file.length());
		
		
		CCTextureData myData = new CCTextureData(
				1920, 
				1080, 
				0, 
				CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT, 
				CCPixelFormat.BGR, 
				CCPixelType.UNSIGNED_BYTE, 
				true, 
				false, 
				buf, 
				null
			);
			CCLog.info("data:" + CCStopWatch.instance().endWatch("data"));
			
			CCStopWatch.instance().startWatch("insert");
			_myCompressed.data(myData);
			CCLog.info("insert" + CCStopWatch.instance().endWatch("insert"));
			CCLog.info(frameRate);
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void draw() {
		g.clearColor(0f,1f,0f);
		g.clear();
//		g.gl.glEnable(CCTextureTarget.TEXTURE_2D.glID);
		
//		g.scale(mouseX / (float)width);
		
//		g.color(255);
//		g.texture(_myCompressed);
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(-_myUncompressed.width()/2, -_myUncompressed.height()/2, 0f, 0f);
//		g.vertex( 0, -_myUncompressed.height()/2, 0.5f, 0f);
//		g.vertex( 0,  _myUncompressed.height()/2, 0.5f, 1f);
//		g.vertex(-_myUncompressed.width()/2,  _myUncompressed.height()/2, 0, 1);
//		g.endShape();
//		g.noTexture();
//		
//		g.line(0, -height/2, 0, height/2);
//
//		g.texture(_myUncompressed);
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(0, -_myCompressed.height()/2, 0.5f, 0);
//		g.vertex(_myCompressed.width()/2, -_myCompressed.height()/2, 1f, 0f);
//		g.vertex(_myCompressed.width()/2,  _myCompressed.height()/2, 1f, 1f);
//		g.vertex(0,  _myCompressed.height()/2, 0.5f, 1f);
//		g.endShape();
//		g.noTexture();
		
		g.image(frameCount % 100 < 50 ? _myUncompressed : _myCompressed, -width/2, -height/2);
		
		if(frameCount % 100 < 50){
			g.rect(0,0,100,100);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCExportCompressedTextureDemo.class);
		myManager.settings().size(1900, 1020);
		myManager.start();
	}
}

