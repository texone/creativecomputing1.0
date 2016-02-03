package cc.creativecomputing.timeline.view.swing.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import cc.creativecomputing.timeline.controller.CurveTrackController;
import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.TrackController;
import cc.creativecomputing.timeline.view.swing.SwingGuiConstants;


@SuppressWarnings("serial")
public class SwingTrackControlView extends JPanel{
	
	static final Dimension SMALL_BUTTON_SIZE = new Dimension(20,15);
	static final int RESIZE_HANDLE_HEIGHT = 5;
	static final Dimension ADDRESS_FIELD_SIZE = new Dimension(100,20);
	
	static final String ADD_ACTION = "add";
	static final String REMOVE_ACTION = "remove";


    static final public DecimalFormat VALUE_FORMAT;
    static {
    	VALUE_FORMAT = new DecimalFormat();
    	VALUE_FORMAT.applyPattern("#0.000");
    }
	
	private class SingleTrackControlPopup extends JPopupMenu implements ActionListener{

		public SingleTrackControlPopup() {
			this.setFont(SwingGuiConstants.ARIAL_9);
			
			JMenuItem entryHead = new JMenuItem("Track Edit Funtions");
			entryHead.setFont(SwingGuiConstants.ARIAL_11);
			add(entryHead);
			addSeparator();
			
			JMenuItem myRemoveItem = new JMenuItem("remove track");
			myRemoveItem.setFont(SwingGuiConstants.ARIAL_11);
			myRemoveItem.setActionCommand(REMOVE_ACTION);
			myRemoveItem.addActionListener(this);
			add(myRemoveItem);
			
			JMenuItem myAddItem = new JMenuItem("add track");
			myAddItem.setFont(SwingGuiConstants.ARIAL_11);
			myAddItem.setActionCommand(ADD_ACTION);
			myAddItem.addActionListener(this);
			add(myAddItem);
			
			JMenuItem myColorItem = new JMenuItem("color track");
			myColorItem.setFont(SwingGuiConstants.ARIAL_11);
			myColorItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					openColorChooser();
				}
			});
			add(myColorItem);
			JMenuItem myResetZoomItem = new JMenuItem("reset zoom");
			myColorItem.setFont(SwingGuiConstants.ARIAL_11);
			myColorItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					openColorChooser();
				}
			});
			add(myColorItem);
		}
		
		public void openColorChooser() {
			Color newColor = JColorChooser.showDialog(
				SwingTrackControlView.this,
				"Choose Background Color",
				SwingTrackControlView.this.getBackground()
			);
			_myTrackController.color(newColor);
		}
		
		public void actionPerformed( ActionEvent e ) {
			e.setSource(SwingTrackControlView.this);
				
			for (int i = _myListeners.size() - 1; i >= 0; i--) {
				ActionListener myListener = _myListeners.get(i);
				myListener.actionPerformed(e);
			}
		}
	}
	
	private TimelineController _myTimelineController;
	private TrackController _myTrackController;
	private JToggleButton _myMuteButton;
	private JTextField _myValueField;
	private JLabel _myAddressField;
	private ArrayList<ActionListener> _myListeners;
	
	private SingleTrackControlPopup _myPopUp = new SingleTrackControlPopup();
	
	public SwingTrackControlView(
		TimelineController theTimelineController,
		TrackController theTrackController
	) {
		_myTimelineController = theTimelineController;
		_myTrackController = theTrackController;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 3,2));
		
		setMinimumSize(new Dimension( 150, 50));
		setPreferredSize(new Dimension(150,50));
		
		if(SwingGuiConstants.CREATE_MUTE_BUTTON) {
			_myMuteButton = new JToggleButton("m");
			_myMuteButton.setBackground(Color.WHITE);
			_myMuteButton.setForeground(Color.BLACK);
	//		_myMuteButton.setBorderPainted(false);
			_myMuteButton.setMargin(new Insets(0, 0, 0, 0));
			_myMuteButton.setFont(SwingGuiConstants.ARIAL_9);
			_myMuteButton.setPreferredSize(SMALL_BUTTON_SIZE);
			_myMuteButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean _myPressedShift = (e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
					boolean _myPressedAlt = (e.getModifiers() & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK;
					
					if(_myPressedShift) {
						_myTimelineController.muteAll(_myMuteButton.isSelected());
					}else if(_myPressedAlt){
						_myTrackController.muteGroup(_myMuteButton.isSelected());
					}else {
						_myTrackController.mute(_myMuteButton.isSelected());
					}
				}
			});
			
			add(_myMuteButton);
		}
		
		if(theTrackController instanceof CurveTrackController) {
			_myValueField = new JTextField();
			_myValueField.setBackground(Color.WHITE);
			_myValueField.setForeground(Color.BLACK);
			_myValueField.setBorder(BorderFactory.createEmptyBorder());
			_myValueField.setHorizontalAlignment(JTextField.RIGHT);
			_myValueField.setFont(SwingGuiConstants.ARIAL_9);
			_myValueField.setText("0.0");
			_myValueField.setPreferredSize(new Dimension(45,15));
			
			add(_myValueField);
		}
		
		_myAddressField = new JLabel("");
		_myAddressField.setPreferredSize(new Dimension(250,15));
		_myAddressField.setFont(SwingGuiConstants.ARIAL_BOLD_10);
		_myAddressField.setForeground(Color.WHITE);
		
		add(_myAddressField);
		
		_myListeners = new ArrayList<ActionListener>();
		
		setBorder(BorderFactory.createLineBorder(Color.gray));
		
		addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent theE) {
				if (theE.getButton() == MouseEvent.BUTTON3)
					_myPopUp.show(SwingTrackControlView.this, theE.getX(), theE.getY());
			}
		});
		
	}
	
	public void color(Color theColor) {
		setBackground(theColor);
	}
	
	public void addActionListener( ActionListener theListener ) {
		_myListeners.add(theListener);
	}
	
	public void removeActionListener( ActionListener theListener ) {
		_myListeners.remove(theListener);
	}
	
	public void mute(final boolean theMute) {
		if(_myMuteButton != null)_myMuteButton.setSelected(theMute);
	}

	public void address(final String theAddress) {
		_myAddressField.setText(theAddress);
	}
	
	public void value(final double theValue) {
		if(_myValueField != null)_myValueField.setText(SwingTrackControlView.VALUE_FORMAT.format(theValue));
	}

	@Override
	public void finalize() {
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
	    if ( !isOpaque( ) ){
	        super.paintComponent( g );
	        return;
	    }
	 
	    Graphics2D g2d = (Graphics2D)g;
	    
	    int w = getWidth( );
	    int h = getHeight( );
	    
	    Color color1 = getBackground( );
	    Color color2 = color1.darker( );
	     
	    // Paint a gradient from top to bottom
	    GradientPaint gp = new GradientPaint(
	        0, 0, color1,
	        0, h, color2 );

	    g2d.setPaint( gp );
	    g2d.fillRect( 0, 0, w, h );
	 
	    setOpaque( false );
	    super.paintComponent( g );
	    setOpaque( true );
	}
}
