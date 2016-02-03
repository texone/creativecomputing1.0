package cc.creativecomputing.timeline.view.swing.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import cc.creativecomputing.timeline.controller.GroupTrackController;
import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.TrackController;
import cc.creativecomputing.timeline.view.GroupTrackView;
import cc.creativecomputing.timeline.view.TrackDataView;
import cc.creativecomputing.timeline.view.swing.SwingGuiConstants;
import cc.creativecomputing.timeline.view.swing.SwingToolChooserPopup;


@SuppressWarnings("serial")
public class SwingGroupTrackView extends JPanel implements GroupTrackView {
	
	private class SingleGroupTrackControlPopup extends JPopupMenu{

		public SingleGroupTrackControlPopup() {
			this.setFont(SwingGuiConstants.ARIAL_9);
			
			JMenuItem entryHead = new JMenuItem("Group Edit Funtions");
			entryHead.setFont(SwingGuiConstants.ARIAL_11);
			add(entryHead);
			addSeparator();
			
			JMenuItem myColorItem = new JMenuItem("color track");
			myColorItem.setFont(SwingGuiConstants.ARIAL_11);
			myColorItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					openColorChooser();
				}
			});
			add(myColorItem);
			
			JMenuItem myWriteItem = new JMenuItem("write values");
			myWriteItem.setFont(SwingGuiConstants.ARIAL_11);
			myWriteItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					_myGroupController.writeValues();
				}
			});
			add(myWriteItem);
		}
		
		public void openColorChooser() {
			Color newColor = JColorChooser.showDialog(
					SwingGroupTrackView.this,
				"Choose Background Color",
				SwingGroupTrackView.this.getBackground().brighter()
			);
			_myGroupController.color(newColor);
		}
	}
	
	static final Dimension SMALL_BUTTON_SIZE = new Dimension(20,15);
	static final int RESIZE_HANDLE_HEIGHT = 5;
	static final Dimension ADDRESS_FIELD_SIZE = new Dimension(100,20);


    static final public DecimalFormat VALUE_FORMAT;
    static {
    	VALUE_FORMAT = new DecimalFormat();
    	VALUE_FORMAT.applyPattern("#0.00");
    }
	
	private GroupTrackController _myGroupController;
	private TimelineController _myTimelineController;
	private JButton _myOpenButton;
	private JLabel _myAddressField;
	private ArrayList<ActionListener> _myListeners;
	
	private SingleGroupTrackControlPopup _myPopUp = new SingleGroupTrackControlPopup();
	
	private SwingTrackDataView _myDataView;
	
	private SwingMultiTrackPanel _myMultiTrackPanel;
	
	private boolean _myShowUnusedItems = true;
	
	public SwingGroupTrackView(
		SwingToolChooserPopup theToolChooserPopUp, 
	    SwingTrackDataRenderer theDataRenderer,
		SwingMultiTrackPanel theMultiTrackPanel, 
		TimelineController theTimelineController, 
		GroupTrackController theGroupController
	) {
		_myMultiTrackPanel = theMultiTrackPanel;
		_myGroupController = theGroupController;
		_myTimelineController = theTimelineController;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 3,2));
		
		setMinimumSize(new Dimension( 100, 20));
		setPreferredSize(new Dimension(100,20));
		
		_myOpenButton = new JButton("-");
		_myOpenButton.setBackground(Color.WHITE);
		_myOpenButton.setForeground(Color.BLACK);
//		_myOpenButton.setBorderPainted(false);
		_myOpenButton.setMargin(new Insets(0, 0, 0, 0));
		_myOpenButton.setFont(SwingGuiConstants.ARIAL_9);
		_myOpenButton.setPreferredSize(SMALL_BUTTON_SIZE);
		_myOpenButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean _myPressedShift = (e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
				
				if(_myOpenButton.getText().equals("+")) {
					if(_myPressedShift){
						_myTimelineController.openGroups();
					}else{
						_myGroupController.openGroup();
					}
				}else if(_myOpenButton.getText().equals("-")) {
					if(_myPressedShift){
						_myTimelineController.closeGroups();
					}else{
						_myGroupController.closeGroup();
					}
				}
			}
		});
		
		add(_myOpenButton);
		
		_myAddressField = new JLabel("");
		_myAddressField.setPreferredSize(new Dimension(250,15));
		_myAddressField.setFont(SwingGuiConstants.ARIAL_BOLD_10);
		_myAddressField.setForeground(Color.WHITE);
		
		add(_myAddressField);
		
		_myListeners = new ArrayList<ActionListener>();
		
		_myDataView = new SwingTrackDataView(theToolChooserPopUp, theDataRenderer, theTimelineController, theGroupController);;
		
		addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent theE) {
				if (theE.getButton() == MouseEvent.BUTTON3)
					_myPopUp.show(SwingGroupTrackView.this, theE.getX(), theE.getY());
			}
		});
		
	}
	
	@Override
	public TrackDataView trackDataView() {
		return _myDataView;
	}
	
	public GroupTrackController controller(){
		return _myGroupController;
	}
	
	public boolean containsData(){
		for(TrackController myTrackController:_myGroupController.trackController()) {
			if(myTrackController.trackData().size() > 0)return true;
			
		}
		return false;
	}
	
	public void showUnusedItems(boolean theShowUsedItems){
		_myShowUnusedItems = theShowUsedItems;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.GroupTrackView#openGroup()
	 */
	@Override
	public void openGroup() {
		_myOpenButton.setText("-");
		int myIndex = _myMultiTrackPanel.index(this) + 1;
		for(TrackController myTrackController:_myGroupController.trackController()) {
			if(!_myShowUnusedItems){
				if(myTrackController.trackData().size() <= 1)continue;
			}
			SwingTrackView myTrackView = (SwingTrackView)myTrackController.view();
			
			String myAddress = myTrackController.track().address();
			
			_myMultiTrackPanel.insertTrackView(myTrackView.controlView(), myAddress, myIndex, SwingTableLayout.DEFAULT_ROW_HEIGHT);
			_myMultiTrackPanel.insertTrackDataView(myTrackView.dataView(), myAddress, myIndex);
			
			myIndex++;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.GroupTrackView#closeGroup()
	 */
	@Override
	public void closeGroup() {
		_myOpenButton.setText("+");
		for(TrackController myTrackDataController:_myGroupController.trackController()) {
//			CCLog.info(myTrackDataController+":"+myTrackDataController.track().address());
			_myMultiTrackPanel.removeTrackView(myTrackDataController.track().address());
		}
	}
	
	public JPanel dataView() {
		return _myDataView;
	}
	
	public void color(Color theColor) {
		Color myColor = theColor.darker();
		setBackground(myColor);
		_myDataView.setBackground(myColor);
	}
	
	public void addActionListener( ActionListener theListener ) {
		_myListeners.add(theListener);
	}
	
	public void removeActionListener( ActionListener theListener ) {
		_myListeners.remove(theListener);
	}
	
	public void mute(final boolean theMute) {
		_myOpenButton.setSelected(theMute);
	}

	public void address(final String theAddress) {
		_myAddressField.setText(theAddress);
	}

	@Override
	public void finalize() {
	}
}
