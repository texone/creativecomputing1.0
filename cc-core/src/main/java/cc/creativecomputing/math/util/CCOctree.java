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
package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.Collection;

import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCSphere3f;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Implements a spatial subdivision tree to work efficiently with large numbers of 3D particles. This octree can only be
 * used for particle type objects and does NOT support 3D mesh geometry as other forms of Octrees do.
 * 
 * For further reference also see the OctreeDemo in the /examples folder.
 * 
 */
public class CCOctree<ElementType extends CCIOctreeElement> {

	private CCAABB _myAABB;

	/**
	 * alternative tree recursion limit, number of world units when cells are not subdivided any further
	 */
	private float _myMinNodeSize = 4;

	/**
         *
         */
	private CCOctree<ElementType> _myParent;

	private CCOctree<ElementType>[] _myChildren;

	private byte _myNumberOfChildren;

	private ArrayList<ElementType> _myData;

	private float _myDimension, _myHalfDimension;

	private CCVector3f _myOffset;

	private int _myDepth = 0;

	private boolean _myIsAutoReducing = false;

	/**
	 * Constructs a new PointOctree node within the AABB cube volume: {o.x, o.y, o.z} ... {o.x+size, o.y+size, o.z+size}
	 * 
	 * @param theParent parent node
	 * @param theOrigin tree origin
	 * @param theHalfSize half length of the tree volume along a single axis
	 */
	private CCOctree(final CCOctree<ElementType> theParent, final CCVector3f theOrigin, final float theHalfSize) {
		_myAABB = new CCAABB(theOrigin.clone().add(theHalfSize, theHalfSize, theHalfSize), new CCVector3f(theHalfSize, theHalfSize, theHalfSize));
		_myParent = theParent;
		if (_myParent != null) {
			_myDepth = _myParent._myDepth + 1;
		}
		_myDimension = theHalfSize * 2;
		_myHalfDimension = theHalfSize;
		_myOffset = new CCVector3f(theOrigin);
		_myNumberOfChildren = 0;
	}

	/**
	 * Constructs a new PointOctree node within the AABB cube volume: {o.x, o.y, o.z} ... {o.x+size, o.y+size, o.z+size}
	 * 
	 * @param theOrigin tree origin
	 * @param theSize size of the tree volume along a single axis
	 */
	public CCOctree(final CCVector3f theOrigin, final float theSize) {
		this(null, theOrigin, theSize / 2);
	}
	
	public int depth() {
		return _myDepth;
	}
	
	public CCAABB aabb() {
		return _myAABB;
	}

	/**
	 * Adds all elements of the collection to the octree. IMPORTANT: elements need to implement CCIOctreeElement.
	 * 
	 * @param theElements point collection
	 * @return true, if all points have been added successfully.
	 */
	public boolean addAll(Collection<ElementType> theElements) {
		boolean addedAll = true;
		for (ElementType p : theElements) {
			addedAll &= addElement(p);
		}
		return addedAll;
	}

	/**
	 * Adds a new point/particle to the tree structure. All points are stored within leaf nodes only. The tree
	 * implementation is using lazy instantiation for all intermediate tree levels.
	 * 
	 * @param theElement
	 * @return true, if point has been added successfully
	 */
	@SuppressWarnings("unchecked")
	public boolean addElement(final ElementType theElement) {
		// check if point is inside cube
		if (_myAABB.isInside(theElement.position())) {
			// only add data to leaves for now
			if (_myHalfDimension <= _myMinNodeSize) {
				if (_myData == null) {
					_myData = new ArrayList<ElementType>();
				}
				_myData.add(theElement);
				return true;
			} else {
				CCVector3f plocal = CCVecMath.subtract(theElement.position(), _myOffset);
				if (_myChildren == null) {
					_myChildren = new CCOctree[8];
				}
				int octant = getOctantID(plocal);
				if (_myChildren[octant] == null) {
					CCVector3f off = new CCVector3f(_myOffset).add(new CCVector3f((octant & 1) != 0 ? _myHalfDimension : 0, (octant & 2) != 0 ? _myHalfDimension : 0, (octant & 4) != 0 ? _myHalfDimension : 0));
					_myChildren[octant] = new CCOctree<ElementType>(this, off, _myHalfDimension * 0.5f);
					_myNumberOfChildren++;
				}
				return _myChildren[octant].addElement(theElement);
			}
		}
		return false;
	}

	public void empty() {
		_myNumberOfChildren = 0;
		_myChildren = null;
		_myData = null;
	}

	/**
	 * @return a copy of the child nodes array
	 */
	public CCOctree<ElementType>[] getChildren() {
		@SuppressWarnings("unchecked")
		CCOctree<ElementType>[] clones = new CCOctree[8];
		System.arraycopy(_myChildren, 0, clones, 0, 8);
		return clones;
	}

	/**
	 * Finds the leaf node which spatially relates to the given point
	 * 
	 * @param p point to check
	 * @return leaf node or null if point is outside the tree dimensions
	 */
	private CCOctree<ElementType> leafForPoint(final CCVector3f thePoint) {
		// if not a leaf node...
		if (_myAABB.isInside(thePoint)) {
			if (_myNumberOfChildren > 0) {
				int octant = getOctantID(CCVecMath.subtract(thePoint,_myOffset));
				if (_myChildren[octant] != null) {
					return _myChildren[octant].leafForPoint(thePoint);
				}
			} else if (_myData != null) {
				return this;
			}
		}
		return null;
	}

	/**
	 * Returns the minimum size of nodes (in world units). This value acts as tree recursion limit since nodes smaller
	 * than this size are not subdivided further. Leaf node are always smaller or equal to this size.
	 * 
	 * @return the minimum size of tree nodes
	 */
	public float getMinNodeSize() {
		return _myMinNodeSize;
	}

	public float getNodeSize() {
		return _myDimension;
	}

	/**
	 * @return the number of child nodes (max. 8)
	 */
	public int getNumChildren() {
		return _myNumberOfChildren;
	}

	/**
	 * Computes the local child octant/cube index for the given point
	 * 
	 * @param theLocalPoint point in the node-local coordinate system
	 * @return octant index
	 */
	protected final int getOctantID(CCVector3f theLocalPoint) {
		return (theLocalPoint.x >= _myHalfDimension ? 1 : 0) + (theLocalPoint.y >= _myHalfDimension ? 2 : 0) + (theLocalPoint.z >= _myHalfDimension ? 4 : 0);
	}

	/**
	 * Selects all stored points within the given axis-aligned bounding box.
	 * 
	 * @param theAABB AABB
	 * @return all points with the box volume
	 */
	public ArrayList<ElementType> elementsWithinBox(final CCAABB theAABB) {
		ArrayList<ElementType> results = null;
		if (_myAABB.intersectsBox(theAABB)) {
			if (_myData != null) {
				for (ElementType q : _myData) {
					if (theAABB.isInside(q.position())) {
						if (results == null) {
							results = new ArrayList<ElementType>();
						}
						results.add(q);
					}
				}
			} else if (_myNumberOfChildren > 0) {
				for (int i = 0; i < 8; i++) {
					if (_myChildren[i] != null) {
						ArrayList<ElementType> points = _myChildren[i].elementsWithinBox(theAABB);
						if (points != null) {
							if (results == null) {
								results = new ArrayList<ElementType>();
							}
							results.addAll(points);
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * Selects all stored points within the given sphere volume
	 * 
	 * @param theSphere sphere
	 * @return selected points
	 */
	public ArrayList<ElementType> elementsWithinSphere(final CCSphere3f theSphere) {
		ArrayList<ElementType> myResults = null;
		if (_myAABB.intersectsSphere(theSphere)) {
			if (_myData != null) {
				for (ElementType q : _myData) {
					if (theSphere.containsPoint(q.position())) {
						if (myResults == null) {
							myResults = new ArrayList<ElementType>();
						}
						myResults.add(q);
					}
				}
			} else if (_myNumberOfChildren > 0) {
				for (int i = 0; i < 8; i++) {
					if (_myChildren[i] != null) {
						ArrayList<ElementType> points = _myChildren[i].elementsWithinSphere(theSphere);
						if (points != null) {
							if (myResults == null) {
								myResults = new ArrayList<ElementType>();
							}
							myResults.addAll(points);
						}
					}
				}
			}
		}
		return myResults;
	}

	/**
	 * Selects all stored points within the given sphere volume
	 * 
	 * @param theSphereOrigin
	 * @param theClipRadius
	 * @return selected points
	 */
	public ArrayList<ElementType> elementsWithinSphere(final CCVector3f theSphereOrigin, final float theClipRadius) {
		return elementsWithinSphere(new CCSphere3f(theSphereOrigin, theClipRadius));
	}

	private void reduceBranch() {
		if (_myData != null && _myData.size() == 0) {
			_myData = null;
		}
		if (_myNumberOfChildren > 0) {
			for (int i = 0; i < 8; i++) {
				if (_myChildren[i] != null && _myChildren[i]._myData == null) {
					_myChildren[i] = null;
				}
			}
		}
		if (_myParent != null) {
			_myParent.reduceBranch();
		}
	}

	/**
	 * Removes a point from the tree and (optionally) tries to release memory by reducing now empty sub-branches.
	 * 
	 * @param thePoint point to delete
	 * @return true, if the point was found & removed
	 */
	public boolean remove(final CCVector3f thePoint) {
		boolean myFound = false;
		CCOctree<ElementType> myLeaf = leafForPoint(thePoint);
		if (myLeaf != null) {
			if (myLeaf._myData.remove(thePoint)) {
				myFound = true;
				if (_myIsAutoReducing && myLeaf._myData.size() == 0) {
					myLeaf.reduceBranch();
				}
			}
		}
		return myFound;
	}

	public void removeAll(Collection<CCVector3f> thePoints) {
		for (CCVector3f myPoint : thePoints) {
			remove(myPoint);
		}
	}

	/**
	 * @param theMinNodeSize
	 */
	public void minNodeSize(final float theMinNodeSize) {
		_myMinNodeSize = theMinNodeSize;
	}

	/**
	 * Enables/disables auto reduction of branches after points have been deleted from the tree. Turned off by default.
	 * 
	 * @param theIsAutoReducing true, to enable feature
	 */
	public void isUsingTreeAutoReduction(final boolean theIsAutoReducing) {
		_myIsAutoReducing = theIsAutoReducing;
	}


	public String toString() {
		return "<octree> offset: " + super.toString() + " size: " + _myDimension;
	}
}
