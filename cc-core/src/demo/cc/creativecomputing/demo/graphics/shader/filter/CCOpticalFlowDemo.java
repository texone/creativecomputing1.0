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

import java.text.DecimalFormat;
import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.imaging.filter.CCOpticalFlowFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCUpscaleFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCVectorFieldFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.video.CCGStreamerMovie;
import cc.creativecomputing.graphics.texture.video.CCVideoTexture;
import cc.creativecomputing.io.CCIOUtil;


public class CCOpticalFlowDemo extends CCApp{

	CCOpticalFlowFilter _myOpticalFlowFilter;
	CCUpscaleFilter     	  _myUpscaleFilter;
	CCVectorFieldFilter  	  _myVectorFieldFilter;
	
	ArrayList<CCTexture2D> _myTextures;
	
	private CCGStreamerMovie _myVideoInput;
	private CCTexture2D      _myVideoTexture;
	
	int frame = 0;
	
	
	@Override
	public void setup() {
		
		_myVideoInput = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/zoom.mp4"));
		_myVideoInput.loop();
		
		_myVideoTexture = new CCVideoTexture<CCGStreamerMovie>(_myVideoInput, CCTextureTarget.TEXTURE_RECT, new CCTextureAttributes());
		_myVideoTexture.textureFilter(CCTextureFilter.NEAREST);
		
		
		DecimalFormat df =   new DecimalFormat  ( "00" );
		_myTextures = new ArrayList<CCTexture2D>();
		for (int i=1; i<13; i++) {
			CCTexture2D txt = new CCTexture2D(CCTextureIO.newTextureData("demo/textures/sequence/seq"+df.format(i)+".jpg"));
			_myTextures.add(txt);
		}
		
		// use upscale filter to insert single textures into filter chain
		_myUpscaleFilter	  = new CCUpscaleFilter(g, _myTextures.get(0), 1); 
		
		// optical flow calculator
		_myOpticalFlowFilter  = new CCOpticalFlowFilter (g, _myUpscaleFilter.output());
		
		// visualization filter
		_myVectorFieldFilter  = new CCVectorFieldFilter(g, _myOpticalFlowFilter.output(), 1);
		
		addControls ("vec", "vec", _myVectorFieldFilter);
	}
	
	
	@Override
	public void update (float theDeltaTime) {
		
		frame = (frame+1)%12;
		//_myVideoTexture.data(_myVideoInput);
		
		//_myUpscaleFilter.setInput(_myTextures.get(frame));
		_myUpscaleFilter.setInput(_myTextures.get(frame));
		
		_myUpscaleFilter.update(theDeltaTime);
		_myOpticalFlowFilter.update(theDeltaTime);
		frame = (frame+1)%12;
	}
	
	@Override
	public void draw() {
		g.clear();
		
		
		g.image(_myUpscaleFilter.output(), -width/2, -height/2, width, height);
		g.pushMatrix();
		System.out.println(width+" "+height+" "+_myVectorFieldFilter.width()+" "+_myVectorFieldFilter.height()+" : "+(float)(width/_myVectorFieldFilter.width()));
		g.scale((float)width/_myVectorFieldFilter.width(), (float)height/_myVectorFieldFilter.height());
		//g.translate(-80, -height/8);
		_myVectorFieldFilter.draw();
		g.popMatrix();
		
		//g.image(_myVideoTexture, -width/2, -height/2, width, height);
	}
	
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager (CCOpticalFlowDemo.class);
		myManager.settings().size(800, 800);
		myManager.settings().frameRate(10);
		myManager.start();
	}
}
