package cc.creativecomputing.timeline.view.swing.track;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.creativecomputing.timeline.view.swing.SwingGuiConstants;

public class SwingTableLayout implements LayoutManager2 {
	
	enum SeparatorAlignment {
		VERTICAL,
		HORIZONTAL
	}
	
	public class Separator {
		int index = 0;
		SeparatorAlignment alignment = SeparatorAlignment.VERTICAL;
		public String toString() {
			return "[" + index + ", " + (alignment.equals(SeparatorAlignment.VERTICAL) ? "vertical" : "horizontal") + "]";
		}
	}
	
	public static class TableLayoutConstraints {
		public int row = 0;
		public int column = 0;
		public TableLayoutConstraints(int theRow, int theColumn) {
			row = theRow;
			column = theColumn;
		}
	}
	
	public static final int SEPARATOR_WIDTH = 2;
	public static final int DEFAULT_ROW_HEIGHT = 60;
	
	private Map<Component, TableLayoutConstraints> _myConstraintsMap = new HashMap<Component, TableLayoutConstraints>();
	private int _myNumRows = 0;
	private int _myNumColumns = 0;
	private ArrayList<Integer> _myRowHeights = new ArrayList<Integer>();
	private ArrayList<Float>	_myColumnWeights = new ArrayList<Float>();
	private int _myMinimumHeight = 0;
	private int _myMaximumHeight = 0;
	public SwingTableLayout() {
	}

	@Override
	public void addLayoutComponent(Component theComponent, Object theConstraints) {
		if (theConstraints instanceof TableLayoutConstraints) {
			
			TableLayoutConstraints myConstraints = (TableLayoutConstraints) theConstraints;
			if (myConstraints.row + 1 > _myNumRows) {
				for (int row = _myNumRows; row < myConstraints.row + 1; row++) {
					_myRowHeights.add(DEFAULT_ROW_HEIGHT);
				}
				_myNumRows = _myRowHeights.size();
			}
			if (myConstraints.column > _myNumColumns - 1) {
				_myNumColumns = myConstraints.column + 1;
				_myColumnWeights.clear();
				for (int i = 0; i < _myNumColumns; i++) {
					_myColumnWeights.add((float)i/(float)_myNumColumns);
				}
				_myColumnWeights.add(1.0f);
				// HACK
				_myColumnWeights.set(1, SwingGuiConstants.DEFAULT_TRACK_CONTROL_WEIGHT);
			}
			_myConstraintsMap.put(theComponent, myConstraints);
		}
	}
	

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(10000,_myMaximumHeight);
	}

	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	@Override
	public void layoutContainer(Container theParent) {
		Insets myInsets = theParent.getInsets();
		Dimension mySize = theParent.getSize();
		
		int myWidth = mySize.width + (myInsets.left + myInsets.right);

		Iterator<Component> it = _myConstraintsMap.keySet().iterator();
		while (it.hasNext()) {
			
			Component myComponent = it.next();
			TableLayoutConstraints myConstraints = _myConstraintsMap.get(myComponent);
			
			int myX = (int) (_myColumnWeights.get(myConstraints.column) * myWidth + SEPARATOR_WIDTH/2.0);
			int myY = 0;
			for (int i = 0; i < myConstraints.row; i++) {
				myY += _myRowHeights.get(i) + SEPARATOR_WIDTH;
			}
		
			int myComponentWidth = (int) (_myColumnWeights.get(myConstraints.column+1) * myWidth - myX) - SEPARATOR_WIDTH;
			int myComponentHeight = _myRowHeights.get(myConstraints.row);
			
			myComponent.setBounds(myX, myY,  myComponentWidth, myComponentHeight);
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		return new Dimension(100, _myMinimumHeight);
	}

	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		return new Dimension(1000, _myMaximumHeight);
	}

	@Override
	public void removeLayoutComponent(Component theComponent) {
		_myConstraintsMap.remove(theComponent);
		
		recalculateHeights();
	}
	
	public void insertRow(int theRow, int theHeight) {
		Iterator<Component> it = _myConstraintsMap.keySet().iterator();
		while (it.hasNext()) {
			Component myComponent = it.next();
			TableLayoutConstraints myConstraints = _myConstraintsMap.get(myComponent);
			if (myConstraints.row >= theRow) {
				myConstraints.row += 1;
			}
		}
		_myRowHeights.add(theRow, theHeight);

		recalculateHeights();
	}
	
	public void removeRow(int theRow) {
		Iterator<Component> it = _myConstraintsMap.keySet().iterator();
		
		while (it.hasNext()) {
			Component myComponent = it.next();
			TableLayoutConstraints myConstraints = _myConstraintsMap.get(myComponent);
			
			if(myConstraints.row == theRow) {
				_myConstraintsMap.remove(myComponent);
			}
			
			if (myConstraints.row > theRow) {
				myConstraints.row -= 1;
			}
		}
		_myRowHeights.remove(theRow);
	}
	
	public int getWidth(Container theParent, int theX){
		Insets myInsets = theParent.getInsets();
		Dimension mySize = theParent.getSize();
		int myWidth = mySize.width + (myInsets.left + myInsets.right);
		return (int)(myWidth * _myColumnWeights.get(theX));
	}
	
	public Separator getSeparator(Container theParent, int theX, int theY ) {
		
		Separator mySeparator = null;
		
		Insets myInsets = theParent.getInsets();
		Dimension mySize = theParent.getSize();
		
		int myWidth = mySize.width + (myInsets.left + myInsets.right);
		
		for ( Float myWeight : _myColumnWeights ) {
			if (
				theX <= myWeight * myWidth + SEPARATOR_WIDTH && 
				theX >= myWeight * myWidth - SEPARATOR_WIDTH
			){
				mySeparator = new Separator();
				mySeparator.alignment = SeparatorAlignment.VERTICAL;
				mySeparator.index = _myColumnWeights.indexOf(myWeight);
			}
		}
		
		int myPos = 0;
		for (int i = 0; i < _myRowHeights.size(); i++) {
			myPos += _myRowHeights.get(i) + SEPARATOR_WIDTH;
			if (theY <= (myPos + SEPARATOR_WIDTH) 
			 && theY >= (myPos - SEPARATOR_WIDTH)) 
			{
				mySeparator = new Separator();
				mySeparator.alignment = SeparatorAlignment.HORIZONTAL;
				mySeparator.index = i;
			}
		}
		return mySeparator;
	}
	
	private void adjustWeight( int theIndex, float theValue, ArrayList<Float> theWeights ) {
		Float myPrevWeight = theIndex > 0 ? theWeights.get(theIndex-1) : 0;
		
		if (theValue > myPrevWeight) {
			float theDiff = theValue - theWeights.get(theIndex);
			theWeights.set(theIndex, theValue);
			for (int i = theIndex + 1; i < theWeights.size()-1; i++) {
				float myNewWeight = theWeights.get(i) + theDiff;
     			if (myNewWeight > 1) {
					myNewWeight = 1;
				}
				theWeights.set(i, myNewWeight);
			}
		}	
	}
	
	public void setSeparatorPosition( Container theParent, Separator theSeparator, int theX, int theY, boolean applyToAll ) {

		Insets myInsets = theParent.getInsets();
		Dimension mySize = theParent.getSize();
		
		int myWidth = mySize.width + (myInsets.left + myInsets.right);
		
		if (theSeparator.alignment.equals(SeparatorAlignment.VERTICAL)) {
			adjustWeight(theSeparator.index, (float)theX/(float)myWidth, _myColumnWeights);
		} else if (theSeparator.alignment.equals(SeparatorAlignment.HORIZONTAL)) {
			int myDiff = 0;
			for (int row = 0; row < theSeparator.index+1; row++) {
				myDiff += _myRowHeights.get(row) + SEPARATOR_WIDTH;
			} 
			myDiff = theY - myDiff;
			if (applyToAll) {
				for (int row = 0; row < _myRowHeights.size(); row++) {
					_myRowHeights.set(row, _myRowHeights.get(row) + myDiff);
				}
			} else {
				_myRowHeights.set(theSeparator.index, _myRowHeights.get(theSeparator.index) + myDiff);
			}
		}
		layoutContainer(theParent);
	}
	
	
	private void recalculateHeights() {
		// recalculate layout heights
		float myMinimumHeights[] = new float[_myNumRows];
		float myMaximumHeights[] = new float[_myNumRows];
		float myPreferredHeights[] = new float[_myNumRows];
		
		for(Component myComponent:_myConstraintsMap.keySet()) {
			TableLayoutConstraints c = _myConstraintsMap.get(myComponent);
			
			if (myMinimumHeights[c.row] < myComponent.getMinimumSize().height) {
				myMinimumHeights[c.row] = myComponent.getMinimumSize().height;
			}
			if (myPreferredHeights[c.row] < myComponent.getPreferredSize().height) {
				myPreferredHeights[c.row] = myComponent.getPreferredSize().height;
			}
			if (myMaximumHeights[c.row] < myComponent.getMaximumSize().height) {
				myMaximumHeights[c.row] = myComponent.getMaximumSize().height;
			}
		}
		
		_myMinimumHeight = 0;
		_myMaximumHeight = 0;
		for (int i = 0; i < _myNumRows; i++) {
			_myMinimumHeight += myMinimumHeights[i];
			_myMaximumHeight += myMaximumHeights[i];
		}		
	}

}
