package cc.creativecomputing.timeline.view.swing;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.UndoHistory;
import cc.creativecomputing.timeline.view.TimelineContainer;
import cc.creativecomputing.timeline.view.swing.track.SwingTrackDataRenderer;


public class SwingTimelineContainer extends TimelineContainer {
	
	private JFrame _myFrame;
	private SwingTimelineView _myTimelineView;
	private JMenuBar _myMenuBar;
	
	private String _myFrameName = "";
	private String _myChangeAdd = "";
	
	private Map<String, SwingCustomMenu> _myCustomMenues = new HashMap<String, SwingCustomMenu>();

	private SwingTimelineContainer(JFrame theFrame) throws HeadlessException {
		super();
		_myFrame = theFrame;
		setup();
	}
	
	public SwingTimelineContainer() throws HeadlessException {
		this(new JFrame());
	}

	public SwingTimelineContainer(String title) throws HeadlessException {
		this(new JFrame(title));
	}

	public SwingTimelineContainer(String title, GraphicsConfiguration gc) {
		this(new JFrame(title, gc));
	}
	
	public void trackDataRenderer(SwingTrackDataRenderer theRenderer) {
		_myTimelineView.trackDataRenderer(theRenderer);
	}
	
	@Override
	public void onLoad(File theFile) {
		_myFrameName = theFile.getAbsolutePath();
		_myFrame.setTitle(_myFrameName + " " + _myChangeAdd);
	}
	
	@Override
	public void onChange(UndoHistory theHistory) {
		_myChangeAdd = theHistory.size() == 0 ? "" : "*";
		_myFrame.setTitle(_myFrameName + " " + _myChangeAdd);
	}
	
	public void setSize(int theWidth, int theHeight){
		_myFrame.setSize(theWidth, theHeight);
	}
	
	public JFrame frame(){
		return _myFrame;
	}
	
	public void createEmptyTrack() {
		_myTimelineController.addCurveTrack(new Track(), null);
	}
	
	private void setup() {
		_myTimelineView = new SwingTimelineView(_myTimelineController);
		_myTimelineController.view(_myTimelineView);
		
		_myFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		_myFrame.getContentPane().add(_myTimelineView, BorderLayout.CENTER);
		
		_myMenuBar = new JMenuBar();
		SwingFileMenu myFileMenu = new SwingFileMenu(_myFileManager);
		_myMenuBar.add(myFileMenu);
		if(SwingGuiConstants.CREATE_EDIT_MENU)_myMenuBar.add(new SwingEditMenu(_myTimelineController));
		_myMenuBar.add(new SwingViewMenu(_myTimelineController));
		_myFrame.setJMenuBar(_myMenuBar);
		
		_myFrame.addWindowListener(new ExitHandler(myFileMenu));
		
		_myFrame.pack();
		_myFrame.setVisible(true);
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
