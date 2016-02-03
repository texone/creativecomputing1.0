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
package cc.creativecomputing.cv.openni;

import org.openni.CodecID;
import org.openni.GeneralException;
import org.openni.Generator;
import org.openni.Recorder;
import org.openni.StatusException;

/**
 * @author christianriekoff
 * 
 */
public class CCOpenNIRecorder {

	enum CapturingState {
		NOT_CAPTURING, SHOULD_CAPTURE, CAPTURING,
	};

	enum CaptureNodeType {
		CAPTURE_DEPTH_NODE, CAPTURE_IMAGE_NODE, CAPTURE_IR_NODE, CAPTURE_AUDIO_NODE, CAPTURE_NODE_COUNT
	};

	class NodeCapturingData {
		CodecID captureFormat;
		int nCapturedFrames;
		boolean bRecording;
		Generator pGenerator;
	};

	private int MAX_STRINGS = 20;

	class NodeCodec {
		int nValuesCount;
		CodecID[] pValues = new CodecID[MAX_STRINGS];
		String[] pIndexToName = new String[MAX_STRINGS];
	};

	private Recorder _myRecorder;

	String _myFileName;
	long nStartOn; // time to start, in seconds
	boolean bSkipFirstFrame;
	CapturingState _myState;
	int nCapturedFrameUniqueID;
	String csDisplayMessage;

	static CodecID CODEC_DONT_CAPTURE = CodecID.Null;
	static int CAPTURE_NODE_COUNT = 4;

	NodeCodec g_DepthFormat;
	NodeCodec g_ImageFormat;
	NodeCodec g_IRFormat;
	NodeCodec g_AudioFormat;

	CCOpenNI _myOpenNI;

	public CCOpenNIRecorder(CCOpenNI theOpenNI) {
		_myOpenNI = theOpenNI;
		captureInit();
	}

	// --------------------------------
	// Code
	// --------------------------------
	void captureInit() {
		

		// Init
		_myFileName = null;
		_myState = CapturingState.NOT_CAPTURING;
		nCapturedFrameUniqueID = 0;
		csDisplayMessage = "0";
		bSkipFirstFrame = false;

	}

	public boolean isCapturing() {
		return _myState != CapturingState.NOT_CAPTURING;
	}

	private boolean captureOpenWriteDevice() {

		try {
			_myRecorder = Recorder.create(_myOpenNI._myContext, "ONI");
//			_myRecorder.setDestination(RecordMedium.FILE, _myFileName);
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		} catch (GeneralException e) {
			throw new CCOpenNIException(e);
		}

		return true;
	}

	private void captureBrowse(String theFileName) {

		_myFileName = theFileName;

		// as we waited for user input, it's probably better to discard first frame (especially if an accumulating
		// stream is on, like audio).
		bSkipFirstFrame = true;

		captureOpenWriteDevice();
	}
	
	public void start() {
		start(0);
	}

	public void start(int nDelay) {
		if (_myFileName == null) {
			captureBrowse("Capture.oni");
		}

		if (_myFileName == null)
			return;

		long nNow = System.currentTimeMillis();
		nNow /= 1000;

		nStartOn = nNow + nDelay;
		_myState = CapturingState.SHOULD_CAPTURE;
	}

	void captureCloseWriteDevice() {
		if (_myRecorder != null) {
			_myRecorder = null;
		}
	}

	public void restart() {
		captureCloseWriteDevice();
		if (captureOpenWriteDevice())
			start(0);
	}

	public void stop() {
		if (_myState != CapturingState.NOT_CAPTURING) {
			_myState = CapturingState.NOT_CAPTURING;
			captureCloseWriteDevice();
		}
	}
	
	public void captureFrame() {
		try {
			if (_myState == CapturingState.SHOULD_CAPTURE) {
				long myCurrentTime = System.currentTimeMillis();
				myCurrentTime /= 1000;

				// check if time has arrived
				if (myCurrentTime < nStartOn) return;
					
				// check if we need to discard first frame
				if (bSkipFirstFrame) {
					bSkipFirstFrame = false;
					return;
				} 
				
				// start recording
				
				_myState = CapturingState.CAPTURING;

				// add all captured _myNodes
				
				if (_myOpenNI.isDepthGeneratorOn()) {
					_myRecorder.addNodeToRecording(_myOpenNI.createDepthGenerator()._myGenerator);
				}

				if (_myOpenNI.isImageGeneratorOn()) {
					_myRecorder.addNodeToRecording(_myOpenNI.imageGenerator()._myGenerator, _myOpenNI.imageGenerator().captureCodec());
				}

				if (_myOpenNI.isIRGeneratorOn()) {
					_myRecorder.addNodeToRecording(_myOpenNI.irGenerator()._myGenerator, _myOpenNI.irGenerator().captureCodec());
				}
			}

			if (_myState != CapturingState.CAPTURING) return;
			
			// There isn't a real need to call Record() here, as the WaitXUpdateAll() call already makes sure
			// recording is performed.
			_myRecorder.Record();

			
			
		} catch (StatusException e) {
			throw new CCOpenNIException(e);
		}
	}
}
