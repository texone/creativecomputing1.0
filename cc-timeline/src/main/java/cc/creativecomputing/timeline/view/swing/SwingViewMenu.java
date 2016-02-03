package cc.creativecomputing.timeline.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cc.creativecomputing.timeline.controller.TimelineController;


@SuppressWarnings("serial")
public class SwingViewMenu extends JMenu {

	private TimelineController _myTimelineController;

	public SwingViewMenu(TimelineController theTimelineController) {
		super("View");

		setMnemonic(KeyEvent.VK_V);

		_myTimelineController = theTimelineController;

		JMenuItem myResetZoomMenu = new JMenuItem("Reset Zoom");
		myResetZoomMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				resetZoom();
			}
		});
		myResetZoomMenu.setMnemonic(KeyEvent.VK_R);
		add(myResetZoomMenu);

		JMenuItem myZoomToMax = new JMenuItem("Zoom to Max");
		myZoomToMax.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				zoomToMax();
			}
		});
		myZoomToMax.setMnemonic(KeyEvent.VK_M);
		add(myZoomToMax);

		JMenuItem myZoomSelection = new JMenuItem("Zoom to Selection");
		myZoomSelection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				zoomToSelection();
			}
		});
		myZoomSelection.setMnemonic(KeyEvent.VK_S);
		add(myZoomSelection);
		
		ActionListener myShowUnusedTracksListener = new ActionListener() {
			public void actionPerformed(ActionEvent theE) {
				JCheckBoxMenuItem myButton = (JCheckBoxMenuItem)theE.getSource();
				boolean selected = myButton.isSelected();
				_myTimelineController.hideUnusedTracks(selected);
			}
		};
		
		JCheckBoxMenuItem myHideUnusedTracksItem = new JCheckBoxMenuItem("Hide Unused Tracks");
		myHideUnusedTracksItem.setSelected(false);
		myHideUnusedTracksItem.addActionListener(myShowUnusedTracksListener);
		add(myHideUnusedTracksItem);
	}

	private void resetZoom() {
		_myTimelineController.zoomController().reset();
	}
	
	private void zoomToMax() {
		_myTimelineController.zoomToMaximum();
	}
	
	private void zoomToSelection() {
		_myTimelineController.zoomToSelection();
	}
}
