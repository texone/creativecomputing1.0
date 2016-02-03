package cc.creativecomputing.timeline.view.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.UndoHistory;
import cc.creativecomputing.timeline.view.TimelineContainer;
import cc.creativecomputing.timeline.view.swing.track.SwingTrackDataRenderer;


public class SwingDialogTimelineContainer extends TimelineContainer {
	
	private JDialog _myDialog;
	private SwingTimelineView _myTimelineView;
	private JMenuBar _myMenuBar;
	
	private String _myFrameName = "";
	private String _myChangeAdd = "";
	
	private Map<String, SwingCustomMenu> _myCustomMenues = new HashMap<String, SwingCustomMenu>();

	private SwingDialogTimelineContainer(JDialog theDialog, JMenuBar theMenuBar) throws HeadlessException {
		super();
		_myDialog = theDialog;
		_myMenuBar = theMenuBar;
		setup();
	}

	public SwingDialogTimelineContainer(Frame theOwner, JMenuBar theMenuBar, String theTitle) throws HeadlessException {
		this(new JDialog(theOwner, theTitle), theMenuBar);
	}
	
	public void trackDataRenderer(SwingTrackDataRenderer theRenderer) {
		_myTimelineView.trackDataRenderer(theRenderer);
	}
	
	@Override
	public void onLoad(File theFile) {
		_myFrameName = theFile.getAbsolutePath();
		_myDialog.setTitle(_myFrameName + " " + _myChangeAdd);
	}
	
	@Override
	public void onChange(UndoHistory theHistory) {
		_myChangeAdd = theHistory.size() == 0 ? "" : "*";
		_myDialog.setTitle(_myFrameName + " " + _myChangeAdd);
	}
	
	public void setSize(int theWidth, int theHeight){
		_myDialog.setSize(theWidth, theHeight);
	}
	
	
	public void createEmptyTrack() {
		_myTimelineController.addCurveTrack(new Track(), null);
	}
	
	private void setup() {
		_myTimelineView = new SwingTimelineView(_myTimelineController);
		_myTimelineController.view(_myTimelineView);
		
		_myDialog.getContentPane().add(_myTimelineView, BorderLayout.CENTER);
		
		SwingFileMenu myFileMenu = new SwingFileMenu(_myFileManager);
		_myMenuBar.add(myFileMenu);
		if(SwingGuiConstants.CREATE_EDIT_MENU)_myMenuBar.add(new SwingEditMenu(_myTimelineController));
		_myMenuBar.add(new SwingViewMenu(_myTimelineController));
		
		_myDialog.addWindowListener(new ExitHandler(myFileMenu));
		
		_myDialog.pack();
		_myDialog.setVisible(true);
	}
	
	public JMenuItem addCustomCommand(final String theMenu, final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator) {
		if(!_myCustomMenues.containsKey(theMenu)) {
			SwingCustomMenu myCustomMenu = new SwingCustomMenu(theMenu, _myTimelineView);
			_myMenuBar.add(myCustomMenu);
			_myCustomMenues.put(theMenu, myCustomMenu);
		}
		return _myCustomMenues.get(theMenu).addCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator);
	}
	
	public JCheckBoxMenuItem addCustomCheckBoxCommand(final String theMenu, final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator, boolean theDefault) {
		if(!_myCustomMenues.containsKey(theMenu)) {
			SwingCustomMenu myCustomMenu = new SwingCustomMenu(theMenu, _myTimelineView);
			_myMenuBar.add(myCustomMenu);
			_myCustomMenues.put(theMenu, myCustomMenu);
		}
		return _myCustomMenues.get(theMenu).addCheckBoxCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator, theDefault);
	}

}
