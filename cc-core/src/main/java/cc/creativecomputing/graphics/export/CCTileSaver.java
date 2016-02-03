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
package cc.creativecomputing.graphics.export;

import static cc.creativecomputing.io.CCIOUtil.createPath;
import static cc.creativecomputing.io.CCIOUtil.dataPath;
import static cc.creativecomputing.math.CCMath.PI;
import static cc.creativecomputing.math.CCMath.tan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCIOUtil;

/**
 * TileSaver.pde - v0.12 2007.0326 Marius Watz - http://workshop.evolutionzone.com
 * 
 * Class for rendering high-resolution images by splitting them into tiles using the viewport.
 * 
 * Builds heavily on solution by "surelyyoujest": http://processing.org/discourse/yabb_beta/YaBB.cgi?
 * board=OpenGL;action=display;num=1159148942
 * 
 * @author texone
 * 
 */
public class CCTileSaver implements CCUpdateListener, CCPostListener {
	public boolean isTiling = false, done = true;
	public boolean doSavePreview = true;

	private final CCGraphics _myGraphics;
	private final static float FOV = 60; // initial field of view

	private float _myCameraZ;
	private float _myWidth;
	private float _myHeight;

	private int _myNumberOfTiles = 10;
	private int _myTileNumberSequence; // number of tiles
	private int _myTileCounter;

	private int tileX;
	private int tileY;

	private boolean _myIsFirstFrame = false;
	private boolean _myIsSecondFrame = false;

	private String _myTileFilename;
	private String _myTileFileExtension = "tga";

	private BufferedImage _myTileImage;
	private float _myPercentage;
	private float _myPercentageMilestone;

	private CCCamera _myCamera;

	/**
	 * 
	 * @param theGraphics
	 */
	public CCTileSaver(final CCGraphics theGraphics) {
		_myGraphics = theGraphics;
		_myCamera = _myGraphics.camera();
	}
	
	public CCTileSaver(final CCApp theApp) {
		_myGraphics = theApp.g;
		_myCamera = _myGraphics.camera();
		theApp.addUpdateListener(this);
		theApp.addPostListener(this);
	}

	/**
	 * If init() is called without specifying number of tiles, getMaxTiles() will be called to estimate number of tiles
	 * according to free memory.
	 */
	public void init(final String theFileName) {
		init(theFileName, getMaxTiles(_myGraphics.width));
	}

	/**
	 * Initialize using a filename to output to and number of tiles to use.
	 */
	public void init(final String theTileFileName, final int theNumberOfTiles) {
		_myTileFilename = CCIOUtil.dataPath(theTileFileName);
		_myNumberOfTiles = theNumberOfTiles;
		_myTileNumberSequence = (_myNumberOfTiles * _myNumberOfTiles);

		_myWidth = _myGraphics.width;
		_myHeight = _myGraphics.height;
		_myCameraZ = (_myHeight / 2.0f) / tan(PI * FOV / 360.0f);
		
		// remove extension from filename
		if (!new java.io.File(_myTileFilename).isAbsolute())
			_myTileFilename = dataPath(_myTileFilename);
		_myTileFilename = stripExtension(_myTileFilename);
		createPath(_myTileFilename);

		// save preview
		// if (doSavePreview)
		// g.save(tileFilename + "_preview.png");

		// set up off-screen buffer for saving tiled images
		_myTileImage = new BufferedImage(_myGraphics.width * _myNumberOfTiles, _myGraphics.height * _myNumberOfTiles, BufferedImage.TYPE_INT_ARGB);

		// start tiling
		done = false;
		isTiling = false;
		_myPercentage = 0;
		_myPercentageMilestone = 0;
		tileInc();
	}

	/**
	 * set filetype, default is TGA. pass a valid image extension as parameter.
	 */
	public void setSaveType(String extension) {
		_myTileFileExtension = extension;
	}

	// pre() handles initialization of each frame.
	// It should be called in draw() before any drawing occurs.
	public void update(float theDeltaTime) {
		if (!isTiling)
			return;
		if (_myIsFirstFrame)
			_myIsFirstFrame = false;
		else if (_myIsSecondFrame) {
			_myIsSecondFrame = false;
			tileInc();
		}
		setupCamera();
	}

	// post() handles tile update and image saving.
	// It should be called at the very end of draw(), after any drawing.
	public void post() {
		// If first or second frame, don't update or save.
		if (_myIsFirstFrame || _myIsSecondFrame || (!isTiling))
			return;

		// Find image ID from reverse row order
		int imgid = _myTileCounter % _myNumberOfTiles + (_myNumberOfTiles - _myTileCounter / _myNumberOfTiles - 1) * _myNumberOfTiles;
		int idx = (imgid % _myNumberOfTiles);
		int idy = (imgid / _myNumberOfTiles);

		// Get current image from sketch and draw it into buffer
		_myGraphics.loadPixels();
		_myTileImage.setRGB(idx * _myGraphics.width, idy * _myGraphics.height, _myGraphics.width, _myGraphics.height, _myGraphics.pixels, 0, _myGraphics.width);

		// Increment tile index
		_myTileCounter++;
		_myPercentage = 100 * ((float) _myTileCounter / (float) _myTileNumberSequence);
		if (_myPercentage - _myPercentageMilestone > 5 || _myPercentage > 99) {
			_myPercentageMilestone = _myPercentage;
		}

		if (_myTileCounter == _myTileNumberSequence)
			finnishTiledImage();
		else {
			tileInc();
		}
	}

	public boolean checkStatus() {
		return isTiling;
	}

	/**
	 * handles saving of the tiled image
	 */
	private void finnishTiledImage() {
		isTiling = false;

		restoreCamera();

		// save large image to TGA
		_myTileFilename += "_" + _myTileCounter+"_"+(_myGraphics.width * _myNumberOfTiles) + "x" + (_myGraphics.height * _myNumberOfTiles) + "." + _myTileFileExtension;
		try {
			ImageIO.write(_myTileImage, "png", new File(CCIOUtil.dataPath(_myTileFilename)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// clear buffer for garbage collection
		_myTileImage = null;
		done = true;
	}

	/**
	 * Increment tile coordinates
	 */
	private void tileInc() {
		if (!isTiling) {
			isTiling = true;
			_myIsFirstFrame = true;
			_myIsSecondFrame = true;
			_myTileCounter = 0;
		} else {
			if (tileX == _myNumberOfTiles - 1) {
				tileX = 0;
				tileY = (tileY + 1) % _myNumberOfTiles;
			} else
				tileX++;
		}
	}

	/**
	 * set up camera correctly for the current tile
	 */
	private void setupCamera() {

		if (isTiling) {
			float mod = 1f / 10f;
			_myGraphics.frustum(
				_myWidth * (tileX / (float) _myNumberOfTiles - .5f) * mod, 
				_myWidth * ((tileX + 1) / (float) _myNumberOfTiles - .5f) * mod, 
				_myHeight * ((float) tileY / (float) _myNumberOfTiles - .5f) * mod, 
				_myHeight * ((tileY + 1) / (float) _myNumberOfTiles - .5f) * mod, 
				_myCameraZ * mod, 10000
			);
		} else {
			_myGraphics.camera(_myCamera);
		}

	}

	/**
	 * restore camera once tiling is done
	 */
	private void restoreCamera() {
	// final float mod = 1f / 10f;
	//		
	// _myGraphics.camera(
	// _myWidth / 2.0f, _myHeight / 2.0f, _myCameraZ,
	// _myWidth / 2.0f, _myHeight / 2.0f, 0,
	// 0, 1, 0
	// );
	//		
	// _myGraphics.frustum(
	// -(_myWidth / 2) * mod, (_myWidth / 2) * mod,
	// -(_myHeight / 2) * mod, (_myHeight / 2) * mod,
	//			
	// _myCameraZ * mod, 10000
	// );
	}

	/**
	 * checks free memory and gives a suggestion for maximum tile resolution. It should work well in most cases, I've
	 * been able to generate 20k x 20k pixel images with 1.5 GB RAM allocated
	 */
	public int getMaxTiles(final int theWidth) {

		// get an instance of java.lang.Runtime, force garbage collection
		java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
		runtime.gc();

		// calculate free memory for ARGB (4 byte) data, giving some slack
		// to out of memory crashes.
		int num = (int) (Math.sqrt((float) (runtime.freeMemory() / 4) * 0.925f)) / theWidth;

		// warn if low memory
		if (num == 1) {
			num = 2;
		}

		return num;
	}

	/**
	 * strip extension from filename
	 */
	private String stripExtension(final String theName) {
		int last = theName.lastIndexOf(".");
		if (last > 0)
			return theName.substring(0, last);

		return theName;
	}
}
