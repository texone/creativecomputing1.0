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
package cc.creativecomputing.model.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.nio.CCFileChannel;
import cc.creativecomputing.nio.CCFileChannel.CCFileMode;

public class CCOBJExporter {
	
	private CCFileChannel _myChannel;

	private CCDrawMode _myDrawMode;
	
	private List<CCVector3f> _myVertices = new ArrayList<CCVector3f>();
	private List<int[]> _myFaces = new ArrayList<>();
	private List<int[]> _myLines = new ArrayList<>();
	
	private int _myVertexCount = 0;

	public CCOBJExporter(String thePath) {
		if(CCIOUtil.exists(thePath)){
			File myFile = new File(thePath);
			myFile.delete();
		}
		_myChannel = new CCFileChannel(thePath, CCFileMode.RW);
//		if (file == null) {
//			throw new RuntimeException("OBJExport requires an absolute path " + "for the location of the output file.");
//		}
	}

	public void beginGroup(String theGroup) {
		// have to create file object here, because the name isn't yet
		// available in allocate()
		
		_myVertices.clear();
		_myFaces.clear();
		_myLines.clear();
		
		_myVertexCount = 0;
	}

	public void endGroup() {
		writeVertices();
		writeFaces();
		_myChannel.force();
	}

	private void writeVertices() {
		for (CCVector3f myVertex:_myVertices) {
			_myChannel.write("v " + myVertex.x + " " + myVertex.y + " " + myVertex.z+"\n");
		}
	}

	@SuppressWarnings("unused")
	private void writeLines() {
		for (int[] myLine:_myLines) {
			_myChannel.write("l");
			for (int myLineIndex:myLine) {
				_myChannel.write(" " + myLineIndex);
			}
			_myChannel.write("\n");
		}
	}

	private void writeFaces() {
		for (int[] myFace:_myFaces) {
			_myChannel.write("f");
			for (int myIndex:myFace) {
				_myChannel.write(" " + (myIndex + 1));
			}
			_myChannel.write("\n");
		}
		_myFaces.clear();
	}

	public void beginShape(CCDrawMode theMode) {
		_myDrawMode = theMode;
		_myVertexCount += _myVertices.size();
		_myVertices = new ArrayList<CCVector3f>();
	}

	public void vertex(float theX, float theY) {
		vertex(theX, theY, 0);
	}

	public void vertex(float theX, float theY, float theZ) {
		vertex(new CCVector3f(theX, theY, theZ));
	}
	
	public void vertex(CCVector3f theVertex){
		_myVertices.add(theVertex);
	}

	public void endShape() {
		switch (_myDrawMode) {
		case TRIANGLES:
			for (int i = 0; i < _myVertices.size() - 2; i += 3) {
				addFace(
					_myVertexCount + i,
					_myVertexCount + i + 1,
					_myVertexCount + i + 2	
				);
			}
			break;
		case TRIANGLE_STRIP:
			for (int i = 0; i < _myVertices.size() - 2; i++) {
				// have to switch between clockwise/counter-clockwise
				// otherwise the feller is backwards and renderer won't draw
				int myEven = i % 2;
				addFace(
					_myVertexCount + i,
					_myVertexCount + i + 2 - myEven,
					_myVertexCount + i + 1 + myEven
				);
			}
		
			break;
		case QUADS: 
			for (int i = 0; i < _myVertices.size() - 3; i += 4) {
				addFace(
					_myVertexCount + i,
					_myVertexCount + i + 1,
					_myVertexCount + i + 2,
					_myVertexCount + i + 3
				);
			}
		
			break;

		case QUAD_STRIP: 
			for (int i = 0; i < _myVertices.size() - 3; i += 2) {
				addFace(
					_myVertexCount + i,
					_myVertexCount + i + 1,
					_myVertexCount + i + 2,
					_myVertexCount + i + 3	
				);
			}
			break;
		case TRIANGLE_FAN: 
			for (int i = 1; i < _myVertices.size() - 1; i++) {
				addFace(
					_myVertexCount + 0,
					_myVertexCount + i,
					_myVertexCount + i + 1
				);
			}
			break;
		default:
		}
	}

	private void addFace(int...theFaceIndices) {
		_myFaces.add(theFaceIndices);
	}

	@SuppressWarnings("unused")
	private void addLine(int...theLineIndices) {
		_myLines.add(theLineIndices);
	}
}
