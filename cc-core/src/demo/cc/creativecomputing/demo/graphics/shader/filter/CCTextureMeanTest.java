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
package cc.creativecomputing.demo.graphics.shader.filter;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.imaging.filter.CCSignalGenerator2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;

public class CCTextureMeanTest extends CCApp{

	private CCSignalGenerator2D _mySignalGenerator;
	private CCRenderBuffer _myBuffer;
	private CCRenderBuffer _myBuffer2;
	
	private int downscale0 = 1;
	private int downscale1 = 2;
	

	@Override
	public void update(final float theDeltaTime) {
		
		_mySignalGenerator.update();
		
		_myBuffer.beginDraw();
		g.clear();
		g.image(_mySignalGenerator.output(), -width/downscale0/2, -height/downscale0/2, width/downscale0, height/downscale0);
		_myBuffer.endDraw();
		
		_myBuffer2.beginDraw();
		g.clear();
		g.image(_myBuffer.attachment(0), -width/downscale1/2, -height/downscale1/2, width/downscale1, height/downscale1);
		_myBuffer2.endDraw();
		
	}	
	
	@Override 
	public void setup() {

		_myBuffer = new CCRenderBuffer (g, width/downscale0, height/downscale0);
		_myBuffer.attachment(0).generateMipmaps (true);
		_myBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		
		_myBuffer2 = new CCRenderBuffer (g, width/downscale1, height/downscale1);
		_myBuffer2.attachment(0).generateMipmaps(true);
		_myBuffer2.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		
		_mySignalGenerator = new CCSignalGenerator2D (g, width, height);
		_mySignalGenerator.output().textureFilter(CCTextureFilter.LINEAR);
		
		addControls("signal", "signal", _mySignalGenerator);
	}
	
	@Override
	public void draw() {
		
		g.clear();
		g.image(_myBuffer2.attachment(0), -width/2, -height/2, width, height);
		
		float sum = 0f;
		for (int i=0; i<_mySignalGenerator.output().width(); i++) {
			for (int j=0; j<_mySignalGenerator.output().height(); j++) {
				sum += _mySignalGenerator.output().getPixel(i, j).r;
			}
		}
		System.out.println("full: "+sum / (_mySignalGenerator.output().width()*_mySignalGenerator.output().height()));
		
		sum = 0;
		for (int i=0; i<_myBuffer2.width(); i++) {
			for (int j=0; j<_myBuffer2.height(); j++) {
				sum += _myBuffer2.attachment(0).getPixel(i, j).r;
			}
		}
		System.out.println("down: "+sum / (_myBuffer2.width()*_myBuffer2.height()));	
	}
	
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextureMeanTest.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}
}
