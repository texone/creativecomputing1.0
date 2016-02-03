package cc.creativecomputing.timeline.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import cc.creativecomputing.timeline.controller.GroupTrackController;
import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.TrackController;
import cc.creativecomputing.timeline.view.GroupTrackView;
import cc.creativecomputing.timeline.view.TimelineView;
import cc.creativecomputing.timeline.view.TrackView;
import cc.creativecomputing.timeline.view.TransportRulerView;
import cc.creativecomputing.timeline.view.swing.ruler.SwingTransportRulerView;
import cc.creativecomputing.timeline.view.swing.track.SwingGroupTrackView;
import cc.creativecomputing.timeline.view.swing.track.SwingMultiTrackPanel;
import cc.creativecomputing.timeline.view.swing.track.SwingTableLayout;
import cc.creativecomputing.timeline.view.swing.track.SwingTrackDataRenderer;
import cc.creativecomputing.timeline.view.swing.track.SwingTrackDataView;
import cc.creativecomputing.timeline.view.swing.track.SwingTrackView;


@SuppressWarnings("serial")
public class SwingTimelineView extends JLayeredPane implements TimelineView, ActionListener, KeyListener, ComponentListener {
	private JScrollPane _myScrollPane;
    private JViewport _myViewport;
	private SwingMultiTrackPanel _myMultiTrackPanel;
	private SwingTransportRulerView _myRuler;

	private SwingToolChooserPopup _myToolChooserPopup;
	
	private SwingTimelineScrollbar _myScrollbar;
	
	private TimelineController _myTimelineController;
	
	private List<Object> _myTracks = new ArrayList<Object>();

	public SwingTimelineView(TimelineController theController) {
		try {
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setLayout(new BorderLayout());
		
		_myRuler = new SwingTransportRulerView(theController);
		add(_myRuler, BorderLayout.PAGE_START);
		
		_myMultiTrackPanel = new SwingMultiTrackPanel(this);
		
		_myViewport = new JViewport();
        _myViewport.add(_myMultiTrackPanel);
        _myViewport.setBounds(0, 0, 300, 300);
		
        _myScrollPane = new JScrollPane(_myMultiTrackPanel);
//        _myScrollPane.setViewport(_myViewport);
		_myScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		_myScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(_myScrollPane, BorderLayout.CENTER);
		
		_myTimelineController = theController;
		
		_myScrollbar = new SwingTimelineScrollbar(_myTimelineController);
		add(_myScrollbar.scrollbar(), BorderLayout.PAGE_END);
		
		_myToolChooserPopup = new SwingToolChooserPopup(theController.curveTool());

		addComponentListener(this);
		addKeyListener(this);
	}
	
	public TransportRulerView transportRulerView() {
		return _myRuler;
	}
	
	public void openTimelinePopup(int theX, int theY) {
		_myToolChooserPopup.show(_myMultiTrackPanel, theX, theY);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TimelineView#addGroupTrack(int, de.artcom.timeline.controller.GroupTrackController, java.lang.String)
	 */
	@Override
	public GroupTrackView addGroupTrack(int theIndex, GroupTrackController theController) {
		assert (theIndex >= 0);

		SwingGroupTrackView myTrackView = new SwingGroupTrackView(
			_myToolChooserPopup, 
			_myTrackDataRenderer,
			_myMultiTrackPanel,
			_myTimelineController, 
			theController
		);
		insertGroupTrack(theIndex, myTrackView, theController.groupTrack().address());
		
		_myTracks.add(theIndex,myTrackView);
		return myTrackView;
	}
	
	private void insertGroupTrack(int theIndex, SwingGroupTrackView theTrackView, String theAddress){
		assert (theIndex >= 0);
		_myMultiTrackPanel.insertTrackView(theTrackView, theAddress, theIndex, 20);
		_myMultiTrackPanel.insertTrackDataView(theTrackView.dataView(), theAddress, theIndex);
		updateUI();
	}
	
	private SwingTrackDataRenderer _myTrackDataRenderer = new SwingTrackDataRenderer();

	public void trackDataRenderer(SwingTrackDataRenderer theTrackDataRenderer) {
		_myTrackDataRenderer = theTrackDataRenderer;
	}
	
	/**
	 * Gets the Track Model and creates all needed Views and adds them to the needed listeners
	 * @param theIndex
	 * @param theTrack
	 */
	@Override
	public TrackView addTrack(int theIndex, TrackController theTrackDataController) {
		assert (theIndex >= 0);
		
		SwingTrackView myTrackView = new SwingTrackView(
			_myToolChooserPopup, 
			_myTrackDataRenderer,
			_myTimelineController,
			theTrackDataController
		);
		myTrackView.dataView().addComponentListener(this);

		insertTrack(theIndex,myTrackView,theTrackDataController.track().address());
		
		_myTracks.add(theIndex,myTrackView);
		
		return myTrackView;
	}
	
	private void insertTrack(int theIndex, SwingTrackView theTrackView, String theAddress){
		assert (theIndex >= 0);
		_myMultiTrackPanel.insertTrackView(theTrackView.controlView(), theAddress, theIndex, SwingTableLayout.DEFAULT_ROW_HEIGHT);
		_myMultiTrackPanel.insertTrackDataView(theTrackView.dataView(), theAddress, theIndex);
		updateUI();
	}

	public void removeTrack(String theAddress) {
		_myMultiTrackPanel.removeTrackView(theAddress);
	}
	
	@Override
	public void showUnusedTracks() {
		showUnusedTracks(true);
	}
	
	@Override
	public void hideUnusedTracks() {
		showUnusedTracks(false);
	}
	
	private void showUnusedTracks(boolean theShowTracks){
		_myTimelineController.openGroups();
		_myMultiTrackPanel.clear();
		int i = 0;
		for(Object myTrack:_myTracks){
			if(myTrack instanceof SwingTrackView){
				SwingTrackView myTrackView = (SwingTrackView)myTrack;
				if(!theShowTracks && myTrackView.dataView().controller().trackData().size() <= 1)continue;
				
				insertTrack(i++, myTrackView, myTrackView.dataView().controller().track().address());
			}else if(myTrack instanceof SwingGroupTrackView){
				SwingGroupTrackView myTrackView = (SwingGroupTrackView)myTrack;
				myTrackView.showUnusedItems(theShowTracks);
				
				if(!theShowTracks && !myTrackView.containsData())continue;

//				boolean myDoOpen = false;
//				if(myTrackView.controller().groupTrack().isOpen()){
//					myDoOpen = true;
////					myTrackView.controller().closeGroup();
//				}
				insertGroupTrack(i++, myTrackView, myTrackView.controller().groupTrack().address());
//				if(!myDoOpen){
//					myTrackView.controller().closeGroup();
//				}
			}
		}_myTimelineController.closeGroups();
	}

	public void actionPerformed(ActionEvent e) {
//		if (e.getSource().getClass() == de.artcom.timeline.view.swing.SwingTrackView.class) {
//			if (e.getActionCommand().equals(SwingTrackView.ADD_ACTION)) {
//				int myIndex = _myMultiTrackPanel.getTrackList().indexOf(e.getSource()) + 1;
//				Track myModel = new Track();
//				setupTrackView(myIndex, myModel);
//			} else if (e.getActionCommand().equals(SwingTrackView.REMOVE_ACTION)) {
//				removeTrack((SwingTrackView) e.getSource());
//			} 
//		}
	}

	public Dimension getMaximumSize() {
		int myMaxYSize = 0;
		for (Component myPanel : _myMultiTrackPanel) {
			myMaxYSize += myPanel.getMaximumSize().getHeight();
		}
		return new Dimension(6000, myMaxYSize);
	}

	@Override
	public void keyPressed(KeyEvent e) {
//		switch(e.getKeyCode()) {
//		case KeyEvent.VK_SPACE:
//			if (_myTransport.isPlaying()) {
//				_myTransport.stop();
//			} else {
//				_myTransport.play();
//			}
//			break;
//		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	

	

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getSource().getClass().equals(SwingTrackDataView.class)) {
			// the offset comes from the JScrollBar etc.
			int myViewWidth = _myMultiTrackPanel.columnWidth(2) - _myMultiTrackPanel.columnWidth(1) - SwingTableLayout.SEPARATOR_WIDTH;
			int myControlWidth = _myMultiTrackPanel.columnWidth(1) + SwingTableLayout.SEPARATOR_WIDTH;
			
			_myRuler.setViewWidth(myViewWidth, myControlWidth);
			((SwingTrackDataView) e.getSource()).render();
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}




}
