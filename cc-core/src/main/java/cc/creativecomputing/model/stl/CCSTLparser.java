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
package cc.creativecomputing.model.stl;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cc.creativecomputing.io.CCAbstractFileParser;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.model.CCFace;
import cc.creativecomputing.model.CCObject;
import cc.creativecomputing.model.CCModel;
import cc.creativecomputing.model.CCSegment;
import cc.creativecomputing.model.material.CCModelMaterial;
import cc.creativecomputing.util.logging.CCLog;


/**
 * Title: STL Loader Description: STL files loader (Supports ASCII and binary files)
 */

public class CCSTLparser extends CCAbstractFileParser {
	
	private static final boolean DEBUG = true; // Sets mode to Debug: outputs every action done

	private final CCModel _myModel;
	private CCObject _myCurrentGroup;
	private String _myCurrentMaterial;
	private CCSegment _myCurrentSegment;
	private CCFace _myCurrentFace;
	
	private boolean _myIsAscii = true;
	private String _myObjectName = "";

	private String _mySourceFolder;
	private String _myFileName;

	public CCSTLparser(final String theFileName, final CCModel theModel) {
		super(CCIOUtil.createReader(theFileName));

		_myFileName = theFileName;
		_myModel = theModel;

		// creating the default group
		_myCurrentGroup = new CCObject("default");
		// adding default variables to the global data table
		_myModel.objectMap().put(_myCurrentGroup.name(), _myCurrentGroup);

		// creating the default material
		_myCurrentMaterial = "default";
		_myModel.materialMap().put(_myCurrentMaterial, CCModelMaterial.DEFAULT);

		// creating the default model segment
		_myCurrentSegment = new CCSegment(_myModel);
		_myCurrentGroup.segments().add(_myCurrentSegment);

		_mySourceFolder = new File(theFileName).getParent();

		if (_mySourceFolder == null)
			_mySourceFolder = "";
		else
			_mySourceFolder += File.separator;
	}
	
	private void addNormal(final CCVector3f theNormal){
		_myCurrentFace.normalIndices().add(_myModel.normals().size());
		_myCurrentFace.normalIndices().add(_myModel.normals().size());
		_myCurrentFace.normalIndices().add(_myModel.normals().size());
		_myModel.normals().add(theNormal);
	}
	
	private void addVertex(final CCVector3f theVertex){
		_myCurrentFace.vertexIndices().add(_myModel.vertices().size());
		_myModel.addVertex(theVertex);
	}

	/**
	 * Method that reads the word "solid" and stores the object name. It also
	 * detects what kind of file it is TO-DO: 1.- Better way control of
	 * exceptions? 2.- Better way to decide between ASCII and Binary?
	 */
	private void readSolid() {
		if (!sval.equals("solid")) {
			 CCLog.info("Expecting solid on line " + lineno());
			// If the first word is not "solid" then we consider the file is
			// binary
			// Can give us problems if the comment of the binary file begins by
			// "solid"
			_myIsAscii = false;
		} else {
			// It's an ASCII file
			getToken();
			if (ttype != TT_WORD) {
				// Is the object name always provided???
				CCLog.error("Format Error:expecting the object name on line " + lineno());
			} else { // Store the object Name
				_myObjectName = sval;
				if (DEBUG) {
					CCLog.info("Object Name:" + _myObjectName);
				}
			}
			skipToNextLine();
		}
	}

	/**
	 * Method that reads a normal.
	 */
	private void readNormal() {
		if (!(ttype == TT_WORD && sval.equals("normal"))) {
			CCLog.error("Format Error:expecting 'normal' on line " + lineno());
			return;
		}
		
		CCVector3f myNormal = new CCVector3f(getFloat(),getFloat(),getFloat());

		if (DEBUG) CCLog.info("Normal:" + myNormal);
			
		// We add that vector to the Normal's array
		addNormal(myNormal);
		skipToNextLine();
	}

	/**
	 * Method that reads the coordinates of a vector
	 */
	private void readVertex() {
		if (!(ttype == TT_WORD && sval.equals("vertex"))) {
			CCLog.error("Format Error:expecting 'vertex' on line " + lineno());
			return;
		}
		
		CCVector3f myVertex = new CCVector3f(getFloat(),getFloat(),getFloat());

		if (DEBUG) CCLog.info("Vertex:" + myVertex);

		// We add that vertex to the array of vertex
		addVertex(myVertex);
		skipToNextLine();
	}

	/**
	 * Method that reads "outer loop" and then EOL
	 */
	private void readLoop() {
		if (!(ttype == TT_WORD && sval.equals("outer"))) {
			CCLog.error("Format Error:expecting 'outer' on line " + lineno());
			return;
		} 
		
		getToken();
		if (!(ttype == TT_WORD && sval.equals("loop"))) {
			CCLog.error("Format Error:expecting 'loop' on line " + lineno());
		} else{
			skipToNextLine();
		}	
	}

	/**
	 * Method that reads "endloop" then EOL
	 */
	private void readEndLoop() {
		if (!(ttype == TT_WORD && sval.equals("endloop"))) {
			CCLog.error("Format Error:expecting 'endloop' on line " + lineno());
		} else{
			skipToNextLine();
		}
	}

	/**
	 * Method that reads "endfacet" then EOL
	 * 
	 * @param parser
	 *            The file  An instance of StlFileParser.
	 */
	private void readEndFacet() {
		if (!(ttype == TT_WORD && sval.equals("endfacet"))) {
			CCLog.error("Format Error:expecting 'endfacet' on line " + lineno());
		} else{
			skipToNextLine();
		}
	}

	/**
	 * Method that reads a face of the object (Cares about the format)
	 */
	private void readFace() {
		if (!(ttype == TT_WORD && sval.equals("facet"))) {
			CCLog.error("Format Error:expecting 'facet' on line " + lineno());
			return;
		}
				
		_myCurrentFace = new CCFace(_myModel);
		getToken();
		readNormal();

		getToken();
		readLoop();

		getToken();
		readVertex();

		getToken();
		readVertex();

		getToken();
		readVertex();

		getToken();
		readEndLoop();

		getToken();
		readEndFacet();
				
		_myCurrentSegment.faces().add(_myCurrentFace);
		_myModel.faces().add(_myCurrentFace);
	}

	/**
	 * Method that reads a face in binary files All binary versions of the
	 * methods end by 'B' As in binary files we can read the number of faces, we
	 * don't need to use coordArray and normArray (reading binary files should
	 * be faster)
	 * 
	 * @param in The ByteBuffer with the data of the object.
	 * @param index The facet index
	 * @throws IOException
	 */
	private void readFacetB(ByteBuffer in, int index) throws IOException {

		if (DEBUG){
			CCLog.info("Reading face number " + index);
		}

		// Read the Normal
		addNormal(
			new CCVector3f(
				in.getFloat(),
				in.getFloat(),
				in.getFloat()
			)
		);

		if (DEBUG){
			CCLog.info("Normal:" + _myModel.normals().get(_myModel.normals().size() - 1));
		}

		// Read vertex1
		addVertex(new CCVector3f(
			in.getFloat(),
			in.getFloat(),
			in.getFloat()
		));

		if (DEBUG)
			CCLog.info("Vertex 1:" + _myModel.vertices().get(_myModel.vertices().size() - 1));

		// Read vertex2
		addVertex(new CCVector3f(
			in.getFloat(),
			in.getFloat(),
			in.getFloat()
		));

		if (DEBUG)
			CCLog.info("Vertex 2:" + _myModel.vertices().get(_myModel.vertices().size() - 1));

		// Read vertex3
		addVertex(new CCVector3f(
			in.getFloat(),
			in.getFloat(),
			in.getFloat()
		));

		if (DEBUG)
			CCLog.info("Vertex 3:" + _myModel.vertices().get(_myModel.vertices().size() - 1));

	}

	/**
	 * Method for reading binary files Execution is completely different It uses
	 * ByteBuffer for reading data and ByteOrder for retrieving the machine's
	 * endian (Needs JDK 1.4)
	 * 
	 * TO-DO: 1.-Be able to read files over Internet 2.-If the amount of data
	 * expected is bigger than what is on the file then the program will block
	 * forever
	 * 
	 * @param file The name of the file
	 * 
	 * @throws IOException
	 */
	private void readBinaryFile(String file) throws IOException {
		InputStream data; // For reading the file
		ByteBuffer dataBuffer; // For reading in the correct endian
		byte[] Info = new byte[80]; // Header data
		byte[] Array_number = new byte[4]; // Holds the number of faces
		byte[] Temp_Info; // Intermediate array

		int Number_faces; // First info (after the header) on the file

		if (DEBUG){
			CCLog.info("Machine's endian: " + ByteOrder.nativeOrder());
		}

		// Get file's name
		data = CCIOUtil.openStream(file);

		// First 80 bytes aren't important
		if (80 != data.read(Info)) { // File is incorrect
			// CCLog.info("Format Error: 80 bytes expected");
			throw new RuntimeException("Incorrect Format Exception");
		} else { // We must first read the number of faces -> 4 bytes int
			// It depends on the endian so..

			data.read(Array_number); // We get the 4 bytes
			dataBuffer = ByteBuffer.wrap(Array_number); // ByteBuffer for
			// reading correctly
			// the int
			dataBuffer.order(ByteOrder.nativeOrder()); // Set the right
			// order
			Number_faces = dataBuffer.getInt();

			Temp_Info = new byte[50 * Number_faces]; // Each face has 50
			// bytes of data

			data.read(Temp_Info); // We get the rest of the file

			dataBuffer = ByteBuffer.wrap(Temp_Info); // Now we have all
			// the data in this
			// ByteBuffer
			dataBuffer.order(ByteOrder.nativeOrder());

			if (DEBUG){
				CCLog.info("Number of faces= " + Number_faces);
			}
			
			for (int i = 0; i < Number_faces; i++) {
				try {
					readFacetB(dataBuffer, i);
					// After each facet there are 2 bytes without
					// information
					// In the last iteration we dont have to skip those
					// bytes..
					if (i != Number_faces - 1) {
						dataBuffer.get();
						dataBuffer.get();
					}
				} catch (IOException e) {
					//Quitar
					CCLog.info("Format Error: iteration number " + i);
					throw new RuntimeException("Incorrect Format Exception");
				}
			}
		}
	}

	/**
	 * Method that reads ASCII files Uses StlFileParser for correct reading and
	 * format checking The beginning of that method is common to binary and
	 * ASCII files We try to detect what king of file it is
	 * 
	 * TO-DO: 1.- Find a best way to decide what kind of file it is 2.- Is that
	 * return (first catch) the best thing to do?
	 */
	public void readFile() {
		getToken();

		// Here we try to detect what kind of file it is (see readSolid)
		readSolid();

		if (_myIsAscii) { // ASCII file
			getToken();

			// Read all the facets of the object
			while (ttype != TT_EOF && !sval.equals("endsolid")) {
				readFace();
				getToken();
			}

			// Why are we out of the while?: EOF or end solid
			if (ttype == TT_EOF)
				CCLog.error("Format Error:expecting 'endsolid', line " + lineno());
			else {
				if (DEBUG)
					CCLog.info("File readed");
			}
		}else { // Binary file
			try {
				readBinaryFile(_myFileName);
			} catch (IOException e) {
				CCLog.error("Format Error: reading the binary file");
			}
		}
	}
}
