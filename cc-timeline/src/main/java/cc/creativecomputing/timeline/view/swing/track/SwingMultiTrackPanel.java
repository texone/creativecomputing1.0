package cc.creativecomputing.timeline.view.swing.track;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import cc.creativecomputing.timeline.view.swing.track.SwingTableLayout.Separator;
import cc.creativecomputing.timeline.view.swing.track.SwingTableLayout.SeparatorAlignment;
import cc.creativecomputing.timeline.view.swing.track.SwingTableLayout.TableLayoutConstraints;


@SuppressWarnings("serial")
public class SwingMultiTrackPanel extends JPanel implements Iterable<Component> {
    
    private SwingTableLayout _myLayout = null;
    private Separator _myDraggedSeparator = null;
    
    private ArrayList<Component> _myTrackViews = new ArrayList<Component>();
    private ArrayList<Component> _myTrackDataViews = new ArrayList<Component>();
    
    /**
     * keeps all tracks by there name for easier access
     */
    private Map<String, Component> _myTrackViewsMap = new HashMap<String, Component>();
    private Map<String, Component> _myTrackDataViewsMap = new HashMap<String, Component>();
    
    private Component _myParent;
    
    public SwingMultiTrackPanel(Component theParent) {
    	_myParent = theParent;
        _myLayout = new SwingTableLayout();
        setLayout(_myLayout);
        MultiTrackMouseAdapter myMouseListener = new MultiTrackMouseAdapter();
        addMouseListener(myMouseListener);
        addMouseMotionListener(myMouseListener);
    }
    
   
    
    public void clear() {
        _myTrackViews.clear();
        _myTrackViewsMap.clear();
        
        _myTrackDataViews.clear();
        _myTrackDataViewsMap.clear();

        _myLayout = new SwingTableLayout();
        setLayout(_myLayout);
        
        removeAll();
    }
    
    public int columnWidth(int theColumn){
    	return _myLayout.getWidth(this, theColumn);
    }

    public void startDrag(int theX, int theY) {
        _myDraggedSeparator = _myLayout.getSeparator(this, theX, theY);
    }
    
    public void endDrag(int theX, int theY) {
        _myDraggedSeparator = null;
    }
    
    public void drag(int theX, int theY, boolean applyToAll) {
        if (_myDraggedSeparator != null) {
            _myLayout.setSeparatorPosition( this, _myDraggedSeparator, theX, theY, applyToAll );
        }
    }
    
    public void switchCursor( int theX, int theY ) {
        Separator mySeparator = _myLayout.getSeparator(this, theX, theY);
        if (mySeparator != null) {
            setCursor(new Cursor(mySeparator.alignment.equals(SeparatorAlignment.HORIZONTAL) ? Cursor.N_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR));
            return;
        }
        setCursor(Cursor.getDefaultCursor());
    }
    
    public int index(Component theComponent) {
    	return _myTrackViews.indexOf(theComponent);
    }
    
    public void insertTrackView(Component theTrackView, String theAddress, int theIndex, int theHeight) {
        _myTrackViews.add(theIndex, theTrackView);
        _myTrackViewsMap.put(theAddress, theTrackView);
        _myLayout.insertRow(theIndex, theHeight);
        
        add(theTrackView,new TableLayoutConstraints(theIndex, 0));
        
        updateUI();
    }
    
    public void insertTrackDataView(Component theTrackDataView, String theAddress, int theIndex) {
    	_myTrackDataViews.add(theIndex, theTrackDataView);
        _myTrackDataViewsMap.put(theAddress, theTrackDataView);
        
        add(theTrackDataView, new TableLayoutConstraints(theIndex, 1));
        
        updateUI();
    }
    
    public void removeTrackView(final String theAddress) {
    	Component myTrackView = _myTrackViewsMap.remove(theAddress);
        int myIndex = _myTrackViews.indexOf(myTrackView);
        
        // if track is part of a group and the group is closed this is totally okay
        if(myIndex < 0) {
        	return;
        }
        _myTrackViews.remove(myIndex);
        remove(myTrackView);
        
        Component myTrackDataView = _myTrackDataViewsMap.remove(theAddress);
        _myTrackDataViews.remove(myTrackDataView);
        remove(myTrackDataView);
        
        _myLayout.removeRow(myIndex);
        
        updateUI();
    }
    
    // Small MouseAdapter to trigger the drag operations
    private class MultiTrackMouseAdapter implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }
    
        @Override
        public void mouseEntered(MouseEvent e) {
        }
    
        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getDefaultCursor());
        }
    
        @Override
        public void mousePressed(MouseEvent e) {
            SwingMultiTrackPanel mySource = (SwingMultiTrackPanel)e.getSource();
            mySource.startDrag(e.getX(), e.getY());
        }
    
        @Override
        public void mouseReleased(MouseEvent e) {
            SwingMultiTrackPanel mySource = (SwingMultiTrackPanel)e.getSource();
            mySource.endDrag(e.getX(), e.getY());
        }
    
        @Override
        public void mouseDragged(MouseEvent e) {
            SwingMultiTrackPanel mySource = (SwingMultiTrackPanel)e.getSource();
            mySource.drag(e.getX(), e.getY(), false);
        }
    
        @Override
        public void mouseMoved(MouseEvent e) {
            SwingMultiTrackPanel mySource = (SwingMultiTrackPanel)e.getSource();
            mySource.switchCursor(e.getX(), e.getY());
        }
    }

    @Override
    public Iterator<Component> iterator() {
        return _myTrackViews.iterator();
    }

    
    @Override
    public Dimension getSize() {
    	int myHeight = 0;
    	for(Component myComponent:_myTrackViews){
    		myHeight += myComponent.getHeight() + 2;
    	}
    	return new Dimension(_myParent.getWidth() - 18,myHeight);
    }
 
}
