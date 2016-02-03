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

import java.util.ArrayList;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.imaging.filter.CCBlurFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCDistanceFieldFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCMipMapSobelFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCNormalizeFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCPotentialFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCSignalGenerator2D;
import cc.creativecomputing.graphics.shader.imaging.filter.CCSobelFilter;
import cc.creativecomputing.graphics.shader.imaging.filter.CCVectorFieldFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;

public class CCFilterChainTest extends CCApp{

	CCSignalGenerator2D _mySignalGenerator;		
	CCSobelFilter       _mySobelFilter;
	CCVectorFieldFilter _myVectorFieldFilter;
	CCMipMapSobelFilter	_myMipMapSobelFilter;
	ArrayList<CCTexture2D> _myTextures;
	
	int frame = 0;
	
	CCPotentialFilter   	_myAverageFilter;		 
	CCDistanceFieldFilter   _myDistanceFieldFilter;
	CCBlurFilter			_myBlurFilter;
	CCNormalizeFilter		_myNormalizeFilter;
	
	
	@Override
	public void setup() {
		_mySignalGenerator        = new CCSignalGenerator2D (g, width, height);
		
		_mySobelFilter            = new CCSobelFilter       (g, _mySignalGenerator.output());
		
		// TODO: STRANGE BUG CANT FIND SHADER FILE _myVectorFieldFilter      = new CCVectorFieldFilter (g, _mySobelFilter.output(), 1);
		
		_myMipMapSobelFilter      = new CCMipMapSobelFilter (g, _mySignalGenerator.output());
		
		
		_myNormalizeFilter      = new CCNormalizeFilter     (g, _mySignalGenerator.output());
		_myBlurFilter			= new CCBlurFilter          (g, _mySignalGenerator.output(), 10);		
		_myDistanceFieldFilter  = new CCDistanceFieldFilter (g, _myBlurFilter.output());
		_myAverageFilter        = new CCPotentialFilter     (g, _mySignalGenerator.output());
		
		addControls("signal", "signal", _mySignalGenerator);
		addControls("sobel", "sobel",   _mySobelFilter);
		addControls("mipmap_sobel", "mipmap_sobel", _myMipMapSobelFilter);
		
		//addControls("normalize", "normalize", _myNormalizeFilter);
		//addControls("lp", "lp", _myLowPassFilter);
		//addControls("forcefield", "forcefield", _myDistanceFieldFilter);
		//addControls("derivation", "derivation", _myDirectionFieldFilter);
		
	}
	
	@Override
	public void update (float theDeltaTime) {
		
		_mySignalGenerator.update();
		//_mySobelFilter.setInput(_myTextures.get(Math.abs(frame-5)));
		//_mySobelFilter.update(theDeltaTime);
		
		frame = (frame+1)%10;

		g.clear();


		//_myMipMapSobelFilter.setInput(_myTextures.get(Math.abs(frame-4)));
		//_myMipMapSobelFilter.update(theDeltaTime);
	
		//_myVectorFieldFilter.update(theDeltaTime);
			
		//_myBlurFilter.update(theDeltaTime);
		//_myNormalizeFilter.update(theDeltaTime);
		//_myDistanceFieldFilter.update(theDeltaTime);
		//_myDirectionFieldFilter.update(theDeltaTime);
		//_myLowPassFilter.update(theDeltaTime);
	}
	
	@Override
	public void draw() {
	
		//g.image(_myTextures.get(Math.abs(frame-5)), -width/2, -height/2, width, height);
		//g.image(_mySobelFilter.output(), -width/2, -height/2, width, height);
		
		
		//_myVectorFieldFilter.draw();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFilterChainTest.class);
		myManager.settings().size(800, 800);
		myManager.start();
	}
}
