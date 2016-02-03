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
package cc.creativecomputing.protocol.proxymatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.filter.CCBlurFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMatrix32f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.math.statistics.CCStatistics;
import cc.creativecomputing.protocol.proxymatrix.filter.CCIRasterFilter;

//============================================================================
// Copyright (C) 2008, ART+COM AG Berlin
//
// These coded instructions, statements, and computer programs contain
// unpublished proprietary information of ART+COM AG Berlin, and
// are copy protected by law. They may not be disclosed to third parties
// or copied or duplicated in any form, in whole or in part, without the
// specific, prior written permission of ART+COM AG Berlin.
//============================================================================

/*
 Program to read and display sensor information coming from the I/O controller
 board of the Proximatrix.

 Upon successful connection, grab the initial values.
 Determine if RESET is necessary

 ==Controls==
 --Connection--
 Serial Port (Radio)
 Baud Rate (Radio)


 --Board Operations (Non-Sticky)--
 Auto Calibrate (Button)
 Tara and switch to relative mode (Button)
 Switch to Absolute Mode (Button)
 Pause / Resume Normal Operation (Radio)

 --Error--
 Get Current Error State (Button + Text)
 Clear Error State (Button)

 --Debug from Status Bytes--
 Version (int)
 Status (int)
 ID (int)
 Mode (int)
 Scan Frequency (Int)
 Width (Int)
 Height (Int)
 Grid Spacing, mm (Int) 
 Frame Number (Int)
 Checksum (Int)

 --Matrix Settings (Written to EEPROM)--
 Width (Number Box)
 Height (Number Box)
 Grid Spacing, mm (Number Box) 
 Baud Rate (Radio)
 Transmitter Frequency (Number Box) [0-3]/[57600-921600]
 Scan Frequency (Number Box) [16-100]
 Transmitter Voltage (Number Box) [0-255]


 // Matt Karau
 // matt@artcom.de

 */
public class CCProxiMatrix implements CCProxyMatrixClientListener, CCUpdateListener, CCMouseListener, CCMouseMotionListener {
	
	public static enum CCProxyMatrixDrawMode{
		NOT, RAW, FILTERED, DENOISED, MOMENT, ALL, SMOOTHED, HEATMAP, PROCESSED_HEATMAP
	}
	
	private Queue<CCTouch> _myAddedTouches = new LinkedList<CCTouch>();
	private Queue<CCTouch> _myMovedTouches = new LinkedList<CCTouch>();
	private Queue<CCTouch> _myRemovedTouches = new LinkedList<CCTouch>();
	
	@CCControl(name = "matrix draw mode")
	private CCProxyMatrixDrawMode _cDrawMatrixMode = CCProxyMatrixDrawMode.NOT;
	
	private static class CCPlacementSettings{
		@CCControl(name = "matrix offset", minX = -1, maxX = 1, minY = -1, maxY = 1)
		private CCVector2f _cOffset = new CCVector2f(0, 0); // as a percentage of screenWidth, so 0.1 would be 10% of the
															// screen size offset
		@CCControl(name = "offset scale", min = 0, max = 1)
		private float _cOffsetScale = 1f;
		
		private CCVector2f offset() {
			return _cOffset.clone().scale(_cOffsetScale);
		}

		@CCControl(name = "scaling", minX = 0.5f, maxX = 2f, minY = 0.5f, maxY = 2f, x = 1, y = 1)
		private CCVector2f _cScaling = new CCVector2f(1, 1); // multiplier
		
		@CCControl(name = "scaling scale", min = 0, max = 1)
		private float _cScalingScale = 1f;
		
		private CCVector2f scaling() {
			return _cScaling.clone().scale(_cScalingScale);
		}

		@CCControl(name = "scale point", minX = -1, maxX = 1, minY = -1, maxY = 1)
		private CCVector2f _cScalePoint = new CCVector2f(0, 0); // as a percentage of screenWidth, so 0.1 would be 10% of the
															// screen size offset
		@CCControl(name = "scale pointscale", min = 0, max = 1)
		private float _cScalePointScale = 1f;
		
		private CCVector2f scalePoint() {
			return _cScalePoint.clone().scale(_cScalePointScale);
		}
	}

	@CCControl(name = "draw blobs")
	private boolean _cDrawBlobs = false;

	@CCControl(name = "draw cursors")
	private boolean _cDrawCursors = false;

	@CCControl(name = "draw scale point")
	private boolean _cDrawScalePoint = false;
	
	@CCControl(name = "image thresh", min = 0, max = 255)
	private float _cThreshold;
	
	@CCControl(name = "image amplify", min = 1, max = 10)
	private float _cAmplify;
	
	@CCControl(name = "gain power", min = 0, max = 10)
	private float _cGainPower;
	
	@CCControl(name = "component thresh", min = 0, max = 255)
	private int _cComponentThreshold;
	
	@CCControl(name = "flip vertical")
	private boolean _cFlipV = false;
	
	@CCControl(name = "flip horizontal")
	private boolean _cFlipH = false;
	
	@CCControl(name = "rotate 90 clockwise")
	private boolean _cRotate = false;
	
	private int _myWidth;
	private int _myHeight;
	
	private CCShaderBuffer _myHeatMap;
	private CCPixelRaster _myRawRaster;
	private CCPixelRaster[] _myLastRasters = new CCPixelRaster[5];
	private int _myRasterID = 0;
	private CCStatistics _myStatistics = new CCStatistics();
	private CCColor[] _myColorPalette = new CCColor[10];
	
	private CCPixelRaster _myBufferedRaster;
	private CCPixelRaster _myDenoisedRaster;
	private CCPixelRaster _myFilteredRaster;
	private CCPixelRaster _myMomentRaster;
    private List<CCIRasterFilter> _myRawFilters;
	
	private CCConnectedPixelAreas _myConnectedPixelAreas;
	private List<CCConnectedPixelArea> _myPixelAreas= new ArrayList<CCConnectedPixelArea>();
	private List<CCVector2f> _myCenterPositions = new ArrayList<CCVector2f>();
	
	private Map<Integer, CCTouch> _myTouchMap = new HashMap<Integer, CCTouch>();
	
	private int _myIDCounter;
	
	private float _myTileSizeX;
	private float _myTileSizeY;
	
	private int _myScreenWidth = 0;
	private int _myScreenHeight = 0;
	
	
	private CCProxyMatrixNetworkClient _myClient;

	
	private CCMatrix32f _myMatrix;
	
	private List<CCTouchListener> _myTouchListener = new ArrayList<CCTouchListener>();
	
	private CCPlacementSettings _myPlacementSettings = new CCPlacementSettings();

	
	@CCControl(name="blur filter")
	private CCBlurFilter _myBlurFilter;
	
	public CCProxiMatrix(
		final String theIP, final int thePort, 
		final int theMatrixWidth, final int theMatrixHeight, 
		final int theScreenWidth, final int theScreenHeight
	) {
		_myScreenWidth = theScreenWidth;
		_myScreenHeight = theScreenHeight;
		
		_myClient = new CCProxyMatrixNetworkClient(this, theIP, thePort, theMatrixWidth, theMatrixHeight);

		_myConnectedPixelAreas = new CCConnectedPixelAreas();
		_myRawFilters = new ArrayList<CCIRasterFilter>();
	}
	
	private CCApp _myApp;
	
	public CCProxiMatrix(
		final CCApp theApp, 
		final String theIP, final int thePort, 
		final int theMatrixWidth, final int theMatrixHeight, 
		final int theScreenWidth, final int theScreenHeight
	) {
		this(theIP, thePort, theMatrixWidth, theMatrixHeight, theScreenWidth, theScreenHeight);
		_myScreenWidth = theScreenWidth;
		_myScreenHeight = theScreenHeight;

		
		_myClient = new CCProxyMatrixNetworkClient(this, theIP, thePort, theMatrixWidth, theMatrixHeight);

		_myConnectedPixelAreas = new CCConnectedPixelAreas();

		_myApp = theApp;
		_myApp.addUpdateListener(this);
		
		_myApp.addMouseListener(this);
		_myApp.addMouseMotionListener(this);
		

		_myHeatMap = new CCShaderBuffer (width(), height());
		_myHeatMap.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myHeatMap.clear();
		
		_myBlurFilter = new CCBlurFilter(theApp.g, _myHeatMap.attachment(0), 20);

		_myApp.addControls("sensors", "Calibration", 0,this);
		_myApp.addControls("sensors", "placement", 1,_myPlacementSettings);
		 for (int i=0; i<10; i++) {
			 _myColorPalette[i] = new CCColor((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
		 }
	}
	
	
	public void connect() {
		_myClient.connect();
	}
	
	public int width() {
		return _myWidth;
	}

	public int height() {
		return _myHeight;
	}
	
	public List<CCTouch> touches(){
		return new ArrayList<CCTouch>(_myTouchMap.values());
	}
	
	public CCPixelRaster rawRaster() {
		return _myRawRaster;
	}
	
	public CCPixelRaster momentRaster() {
		return _myMomentRaster;
	}
	
	public void addListener(final CCTouchListener theListener) {
		_myTouchListener.add(theListener);
	}

 
    public void addRawFilter(CCIRasterFilter theFilter) {
    	_myRawFilters.add(theFilter);
    }
	
	public void update(final float theDeltaTime) {
		synchronized (_myAddedTouches) {
			while(!_myAddedTouches.isEmpty()) {
				CCTouch myAddedTouch = _myAddedTouches.poll();
				for(CCTouchListener myTouchListener:_myTouchListener) {
					myTouchListener.onTouchPress(myAddedTouch);
				}
			}
		}

		synchronized (_myMovedTouches) {
			while(!_myMovedTouches.isEmpty()) {
				CCTouch myMovedTouch = _myMovedTouches.poll();
				for(CCTouchListener myTouchListener:_myTouchListener) {
					myTouchListener.onTouchMove(myMovedTouch);
				}
			}
		}

		synchronized (_myRemovedTouches) {
			while(!_myRemovedTouches.isEmpty()) {
				CCTouch myRemovedTouch = _myRemovedTouches.poll();
				for(CCTouchListener myTouchListener:_myTouchListener) {
					myTouchListener.onTouchRelease(myRemovedTouch);
				}
			}
		}
    	_myBlurFilter.update(0);
    	if (_myRawRaster != null) {
    		raster2Texture();
    	}
	}
	

	
	private CCVector2f interpolateMaximum(CCVector2i theCenter, float theMaximum) {
        CCVector2f myResult = new CCVector2f();

        // centroid based calculation
        float myMx   = 0.0f;
        float myMy   = 0.0f;
        float myMass = 0.0f;
        
        // NOTE: window size can be increased for higher stabilty. 
        for (int x=-1; x<=1; x++) {
        	for (int y=-1; y<=1; y++) {
        		float myValue = _myDenoisedRaster.get(theCenter.x + x, theCenter.y + y);
        		if (myValue > 0.0f) {
        			myMx += (x + theCenter.x)*myValue;
        			myMy += (y + theCenter.y)*myValue;
        			myMass += myValue;
        		}
        	}
        }

        myResult = new CCVector2f( myMx/myMass, myMy/myMass );  
        return myResult;
    }

    List<CCVector2f> computeCursorPositions(final List<CCConnectedPixelArea> theConnectedPixelAreas) {
       List<CCVector2f> myResult = new ArrayList<CCVector2f>();
        
        for (CCConnectedPixelArea myPixelArea:theConnectedPixelAreas) {
            
            CCVector2i myMaximumPosition = new CCVector2i();
            float myMaximum = myPixelArea.max(_myMomentRaster, myMaximumPosition);

            CCVector2f myCenter = interpolateMaximum(myMaximumPosition, myMaximum);
            myCenter.scale(1f / (_myWidth), 1f / (_myHeight));

            if(myCenter.isNAN()) {
            }else {
            	myResult.add(myCenter);
            }
        }

        return myResult;
    }
    
    private void correlatePositions(
    	final List<CCVector2f> thePositions,
    	final List<CCConnectedPixelArea> theConnectedPixelAreas
    ){

        // populate a map with all distances between existing cursors and new positions
        Map<Float, List<CCVector2i>> myDistanceMap = new TreeMap<Float, List<CCVector2i>>();
        float myDistanceThreshold = 4.0f / ((_myWidth + _myHeight) / 2f);
        
        for (int myCursorID:_myTouchMap.keySet()) {
            //computeIntensity(myCursorIt, myRawRaster);
        	CCTouch myTouch = _myTouchMap.get(myCursorID);
        	myTouch.isCorrelated(false);
            for (int i = 0; i < thePositions.size();i++) {
                float myDistance = CCVecMath.add(myTouch.relativePosition(),myTouch.motion()).distance(thePositions.get(i));
                if (myDistance < myDistanceThreshold) {
                	List<CCVector2i> myList = myDistanceMap.get(myDistance);
                	if(myList == null) {
                		myList = new ArrayList<CCVector2i>();
                		myDistanceMap.put(myDistance, myList);
                	}
                	myList.add(new CCVector2i(i, myCursorID));
                }
            }
        }

        // will contain the correlated cursor id at index n for position n or -1 if uncorrelated
        List<Integer> myCorrelatedPositions = new ArrayList<Integer>();
        for(int i = 0; i < thePositions.size();i++) {
        	myCorrelatedPositions.add(-1);
        }

        // iterate through the distance map and correlate cursors in increasing distance order
        synchronized (_myMovedTouches) {
        	for (List<CCVector2i> myEntry:myDistanceMap.values()){
            	for(CCVector2i myIndices:myEntry) {
    	            // check if we already have correlated one of our nodes
    	            int myPositionIndex = myIndices.x;
    	            int myCursorId = myIndices.y;
    	            
    	            CCTouch myTouch = _myTouchMap.get(myCursorId);
    	            CCVector2f myRelativePosition = thePositions.get(myPositionIndex);
    	            CCVector2f myPosition = _myMatrix.transform(myRelativePosition);
    	            
    	            if (!myTouch.isCorrelated()) {
    	                if (myCorrelatedPositions.get(myPositionIndex) == -1)  {
    	                    // correlate
    	                    myCorrelatedPositions.set(myPositionIndex,myCursorId);
    	                    myTouch.isCorrelated(true);
    	
    	                    // update cursor with new position
    	                    myTouch.motion(CCVecMath.subtract(myRelativePosition, myTouch.relativePosition()));
    	                    myTouch.relativePosition().set(myRelativePosition);
    	                    myTouch.position().set(myPosition);
    	
    	                    // post a move event
    	                    _myMovedTouches.add(myTouch);
    	                }
    	            }
            	}
            }
		}
        

    	// Now let us iterate through all new positions and create 
        //"cursor add" events for every uncorrelated position

		synchronized (_myAddedTouches) {
			for (int i = 0; i < thePositions.size(); ++i) {
				if (myCorrelatedPositions.get(i) == -1) {
					// new cursor
					int myNewID = _myIDCounter++;
					if (Float.isNaN(thePositions.get(i).x)) {
						System.out.println("new cursor " + myNewID + " at " + thePositions.get(i));
					}
					myCorrelatedPositions.set(i, myNewID);

					CCVector2f myRelativePosition = thePositions.get(i);
					CCVector2f myPosition = _myMatrix.transform(myRelativePosition);
					
					CCTouch myTouch = new CCTouch(myPosition, myRelativePosition);
					myTouch.id(myNewID);
					myTouch.isCorrelated(true);

					_myTouchMap.put(myNewID, myTouch);
					_myAddedTouches.add(myTouch);
				}
			}
		}
        

        List<Integer> myIdsToRemove = new ArrayList<Integer>();
        
    	// Now let us iterate through all cursors and create 
        //"cursor remove" events for every uncorrelated cursors
        synchronized (_myRemovedTouches) {
            for (Entry<Integer, CCTouch> myEntry:_myTouchMap.entrySet()) {
                if (!myEntry.getValue().isCorrelated()) {
                    // cursor removed
                    _myRemovedTouches.add(myEntry.getValue());
                    myIdsToRemove.add(myEntry.getKey());
                }
            }
		}
        
        for(int myID:myIdsToRemove) {
            _myTouchMap.remove(myID);
        }
    }

	/* (non-Javadoc)
	 * @see de.artcom.deutschebank.brandspace.centerboard.input.CCProxyMatrixClientListener#onChangeMatrixSize(int, int)
	 */
	public void onChangeMatrixSize(int theNewMatrixWidth, int theNewMatrixHeight) {
		_myWidth = theNewMatrixWidth;
		_myHeight = theNewMatrixHeight;
	
		_myTileSizeX = _myScreenWidth / _myWidth;
		_myTileSizeY = _myScreenHeight / _myHeight;
	}

	/* (non-Javadoc)
	 * @see de.artcom.deutschebank.brandspace.centerboard.input.CCProxyMatrixClientListener#onUpdateRaster(de.artcom.deutschebank.brandspace.centerboard.input.CCPixelRaster)
	 */
	public void onUpdateRaster(CCPixelRaster theRaster) {
		
		_myRawRaster = theRaster.clone();
		
		_myFilteredRaster = _myRawRaster.clone();
		for (CCIRasterFilter myFilter : _myRawFilters) {
			_myFilteredRaster = myFilter.filter(_myFilteredRaster);
		}
        		
		_myLastRasters[_myRasterID] = _myFilteredRaster.clone();

		_myRasterID++;
		_myRasterID %= _myLastRasters.length;
		
		_myBufferedRaster = new CCPixelRaster("BUFFERED",_myWidth, _myHeight);
		
		for(CCPixelRaster myRaster:_myLastRasters) {
			if(myRaster != null)_myBufferedRaster.add(myRaster);
		}
		_myBufferedRaster.scale(1f / _myLastRasters.length);
		
		_myDenoisedRaster = _myBufferedRaster.clone();
		_myDenoisedRaster.threshold(_cThreshold);
		
		_myMomentRaster = _myDenoisedRaster.clone();
		_myMomentRaster.scale(_cAmplify);
		_myMomentRaster.power(_cGainPower);
		
		_myPixelAreas = _myConnectedPixelAreas.connectedPixelAreas(_myMomentRaster, _cComponentThreshold);
    	
    	CCVector2f myOffset = _myPlacementSettings.offset();
    	CCVector2f myScaling = _myPlacementSettings.scaling();
    	CCVector2f myScalingPoint = _myPlacementSettings.scalePoint();
    	
    	_myMatrix = new CCMatrix32f();
		_myMatrix.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
		_myMatrix.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
		_myMatrix.scale(myScaling.x * _myWidth * _myTileSizeX, myScaling.y * -_myHeight * _myTileSizeY);
		_myMatrix.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
    	
    	_myCenterPositions = computeCursorPositions(_myPixelAreas);
    	correlatePositions(_myCenterPositions, _myPixelAreas);
	}
	

	@Override
	public void onUpdateTouches (final List<CCVector4f> theTouches) {
		synchronized (_myTouchMap) {
			_myTouchMap.clear();
			int i=0;
			
			for (CCVector4f touch : theTouches) {
				CCTouch newTouch = new CCTouch(new CCVector2f(touch.x/100,touch.y/100), new CCVector2f(0,0));
				newTouch.id((int)touch.w);
				_myTouchMap.put(i, newTouch);
				i+=1;
			}
			/*
			for (CCVector4f touch : theTouches) {
			//	_myTouchMap.put (i, new CCTouch(new CCVector2f(touch.x, touch.y), new CCVector2f(touch.x, touch.y)));
			//	i+=1;
			}*/
		}
	}
	
	
	private void drawRaster(CCGraphics g, CCPixelRaster theRaster) {
		for (int x = 0; x < _myWidth; x++) {
			for (int y = 0; y < _myHeight; y++) {
				g.color((int)theRaster.get(x, y));
				g.rect(x,y,1,1);
			}
		}
	}
	
	private void raster2Texture() {
		
		CCPixelRaster lastRaster = _myLastRasters[_myRasterID];
		if (lastRaster==null) {
			lastRaster = _myRawRaster;
		}

		float _cSmooth = 0.5f;
		
		_myHeatMap.beginDraw();
		_myApp.g.clear();
		_myApp.g.color(1f);
		
		float max = 0;
		float sum = 0;
		//_myApp.g.color(0, 1f);

		
		_myApp.g.beginShape(CCDrawMode.POINTS);
		
		for (int x = 0; x < width(); x++) {
			for (int y = 0; y < height(); y++) {
				
				int x_shifted = x;
				int y_shifted = y;
				
				if (_cFlipH) {
					x_shifted = width() - x;
				}
				if (_cFlipV) {
					y_shifted = height() - y;
				}
				
				if (_cRotate) {
					int x_tmp = x_shifted;
					x_shifted = y_shifted;
					y_shifted = x_tmp;
				}
			 	
				float myValue = _myRawRaster.get (x_shifted, y_shifted) * (1 - _cSmooth) + lastRaster.get (x_shifted, y_shifted) * _cSmooth;
				//float myValue = _myMomentRaster.get (x, height()-y);
				myValue /= 255f;
				if (myValue > max) {
					max = myValue;
				}
				sum += myValue;
				//myValue = 1f;
				_myApp.g.color(myValue);						
				_myApp.g.vertex(x, y);
			}
		}
		_myApp.g.endShape();
		_myApp.g.color(1f);
		
		_myHeatMap.endDraw();
		
		_myStatistics.sum(sum);
		_myStatistics.max(max);
		_myStatistics.mean(sum/width()/height());
		
	}
	
	private void drawTexture(CCGraphics g, CCTexture2D texture) {
		g.image(texture, 0, 0);
	}

	public void draw(CCGraphics g) {
		if(_myRawRaster == null)return;
		g.pushAttribute();

		CCVector2f myOffset = _myPlacementSettings.offset();	
		CCVector2f myScaling = _myPlacementSettings.scaling();
		CCVector2f myScalingPoint = _myPlacementSettings.scalePoint();

		if (_cDrawMatrixMode != null && _cDrawMatrixMode != CCProxyMatrixDrawMode.NOT && _cDrawMatrixMode != CCProxyMatrixDrawMode.ALL) {
			g.pushMatrix();

			switch(_cDrawMatrixMode) {
			case DENOISED:
				drawRaster(g,_myDenoisedRaster);
				break;
			case FILTERED:
				drawRaster(g, _myFilteredRaster);
				break;
			case MOMENT:
				drawRaster(g,_myMomentRaster);
				break;
			case RAW:
				g.color(1f);
				drawRaster(g, _myRawRaster);
				break;
			case HEATMAP:
				drawTexture(g, _myHeatMap.attachment(0));
				break;
			case PROCESSED_HEATMAP:
				drawTexture(g, _myBlurFilter.output());
				break;
			default:
				break;
			}
			g.popMatrix();
		}
		
		if(_cDrawMatrixMode == CCProxyMatrixDrawMode.ALL) {
			g.pushMatrix();
			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
			g.scale(0.5f);
			drawRaster(g, _myRawRaster);
			g.popMatrix();
			
			g.pushMatrix();
			g.translate(0, _myScreenHeight * 0.5f);
			g.scale(0.5f);
			drawRaster(g, _myDenoisedRaster);
			g.popMatrix();
			
			g.pushMatrix();
			g.translate(-_myScreenWidth * 0.5f, 0);
			g.scale(0.5f);
			drawRaster(g, _myMomentRaster);
			g.popMatrix();
		}

		if (_cDrawBlobs) {
			g.pushMatrix();

			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
			g.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
			
			g.scale(myScaling.x * _myTileSizeX, -myScaling.y * _myTileSizeY);
			g.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
			g.color(1f,0.5f);
			for(CCConnectedPixelArea myPixelArea:_myPixelAreas) {
				myPixelArea.draw(g);
			}
			g.popMatrix();
		}
		
		if (_cDrawCursors) {
			g.pushMatrix();
			for(CCTouch myCursors:touches()) {
				g.color(_myColorPalette[myCursors.id() % 10]);
				g.ellipse(myCursors.position(), 1f);
				g.color(0,0,255);
				g.text(""+myCursors.id(), myCursors.position());
			}
			g.popMatrix();
		}

		if (_cDrawScalePoint) {
			g.pushMatrix();

			g.translate(-_myScreenWidth * 0.5f, _myScreenHeight * 0.5f);
			g.translate(myOffset.x * _myScreenWidth, -myOffset.y * _myScreenHeight);
			
			g.scale(myScaling.x * _myTileSizeX, -myScaling.y * _myTileSizeY);
			g.translate(myScalingPoint.x * _myScreenWidth, -myScalingPoint.y * _myScreenHeight);
			g.color(0f,1f,0,0.5f);

			g.ellipse( -myScalingPoint.x * _myScreenWidth, myScalingPoint.y * _myScreenHeight, 3);

			g.popMatrix();
		}
		
		g.popAttribute();
	}
	
	public void mousePressed(CCMouseEvent theEvent) {
		_myAddedTouches.add(new CCTouch(theEvent.position().clone(), new CCVector2f(theEvent.x() / _myApp.width, theEvent.y() / _myApp.height)));
	}
	
	public void mouseReleased(CCMouseEvent theEvent) {
		_myRemovedTouches.add(new CCTouch(theEvent.position().clone(), new CCVector2f(theEvent.x() / _myApp.width, theEvent.y() / _myApp.height)));
	}

	public void mouseDragged(CCMouseEvent theEvent) {
		_myMovedTouches.add(new CCTouch(theEvent.position().clone(), new CCVector2f(theEvent.x() / _myApp.width, theEvent.y() / _myApp.height)));
	}

	public void mouseMoved(CCMouseEvent theMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(CCMouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(CCMouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(CCMouseEvent theEvent) {
		// TODO Auto-generated method stub
		
	}


	public CCTexture2D heatmap() {
		return _myHeatMap.attachment(0);
	}
	
	public CCTexture2D processedHeatmap() {
		return _myBlurFilter.output();
	}
	
	public CCStatistics statistics() {
		return _myStatistics;
	}
}
