package cc.creativecomputing.timeline.view.swing.ruler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.TransportController;
import cc.creativecomputing.timeline.controller.TransportController.RulerInterval;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.ControlPoint.ControlPointType;
import cc.creativecomputing.timeline.model.points.MarkerPoint;
import cc.creativecomputing.timeline.model.points.TimedEventPoint;
import cc.creativecomputing.timeline.view.TransportRulerView;
import cc.creativecomputing.timeline.view.swing.SwingGuiConstants;
import cc.creativecomputing.timeline.view.swing.ruler.SwingDraggableValueBox.ChangeValueListener;
import cc.creativecomputing.util.CCFormatUtil;



@SuppressWarnings("serial")
public class SwingTransportRulerView extends JPanel implements TransportRulerView, ChangeValueListener{
	
	private class PlayButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if (_myTransportController.isPlaying()) {
				_myPlayButton.setText("play");
				_myTransportController.stop();
			} else {
				_myPlayButton.setText("stop");
				_myTransportController.play();
			}
		}
	}
	
	private class InsertTimeDialog extends JDialog implements ActionListener, PropertyChangeListener {
		private String typedText = null;
		private JTextField _myTextField;

		private JOptionPane optionPane;

		private String btnString1 = "insert";
		private String btnString2 = "Cancel";

		/** Creates the reusable dialog. */
		public InsertTimeDialog(String aWord) {
			super();

			setTitle("Insert Time");

			_myTextField = new JTextField(10);

			// Create an array of the text and components to be displayed.
			String msgString1 = "Specify the time to insert in seconds.";
			Object[] array = { msgString1, _myTextField };

			// Create an array specifying the number of dialog buttons
			// and their text.
			Object[] options = { btnString1, btnString2 };

			// Create the JOptionPane.
			optionPane = new JOptionPane(
				array, 
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION, 
				null, 
				options, 
				options[0]
			);

			// Make this dialog display it.
			setContentPane(optionPane);

			// Handle window closing correctly.
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					/*
					 * Instead of directly closing the window, we're going to
					 * change the JOptionPane's value property.
					 */
					optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
				}
			});

			// Ensure the text field always gets the first focus.
			addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent ce) {
					_myTextField.requestFocusInWindow();
				}
			});

			// Register an event handler that puts the text into the option
			// pane.
			_myTextField.addActionListener(this);

			// Register an event handler that reacts to option pane state
			// changes.
			optionPane.addPropertyChangeListener(this);
		}

		/** This method handles events for the text field. */
		public void actionPerformed(ActionEvent e) {
			optionPane.setValue(btnString1);
		}

		/** This method reacts to state changes in the option pane. */
		public void propertyChange(PropertyChangeEvent e) {
			String prop = e.getPropertyName();

			if(
				isVisible() && 
				(e.getSource() == optionPane) && 
				(JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))
			) {
				Object value = optionPane.getValue();

				if (value == JOptionPane.UNINITIALIZED_VALUE) {
					// ignore reset
					return;
				}

				// Reset the JOptionPane's value.
				// If you don't do this, then if the user
				// presses the same button next time, no
				// property change event will be fired.
				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

				if (btnString1.equals(value)) {
					typedText = _myTextField.getText();
					double myTime = Double.parseDouble(typedText);
					_myTimelineController.insertTime(_myTransportController.time(),myTime);
					
					clearAndHide();
					
				} else { 
					typedText = null;
					clearAndHide();
				}
			}
		}

		/** This method clears the dialog and hides it. */
		public void clearAndHide() {
			_myTextField.setText(null);
			setVisible(false);
		}
	}
	
	private class TimeField implements FocusListener, ActionListener, MouseListener{

		private boolean _myIsActive = true;

		private JTextField _myTimeField;
		
		public TimeField() {
			_myTimeField = new JTextField();
			style(_myTimeField);
			_myTimeField.setPreferredSize(new Dimension(80,20));
			
			_myTimeField.addFocusListener(this);
			_myTimeField.addActionListener(this);
			_myTimeField.addMouseListener(this);
		}
		
		private double valueStringToTime(String theString) {
			String[] myParts = theString.split(":");
			
			if(myParts.length < 4)return -1;
			
			try {
				int myHours = Integer.parseInt(myParts[0]);
				int myMinutes = Integer.parseInt(myParts[1]);
				int mySeconds = Integer.parseInt(myParts[2]);
				int myMillis = Integer.parseInt(myParts[3]);
				
				return myHours * 3600 + myMinutes * 60 + mySeconds + myMillis * 0.001;
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent theE) {
			double myTime = valueStringToTime(_myTimeField.getText());
			if(myTime >= 0)_myTransportController.time(myTime);
			_myIsActive = true;
		}

		
		@Override
		public void focusGained(FocusEvent theE) {
			_myIsActive = false;
		}

		
		@Override
		public void focusLost(FocusEvent theE) {
			_myIsActive = true;
		}
		
		private String timeToValueString(double theTime) {
			long myTime = (long)(theTime * 1000);
			long myMillis = myTime % 1000;
			myTime /= 1000;
			long mySeconds = myTime % 60;
			myTime /= 60;
			long myMinutes = myTime % 60;
			myTime /= 60;
			long myHours = myTime;
			
			StringBuffer myResult = new StringBuffer();
			
			myResult.append(CCFormatUtil.nf((int)myHours, 2));
			myResult.append(":");
			
			myResult.append(CCFormatUtil.nf((int)myMinutes, 2));
			myResult.append(":");
			
			myResult.append(CCFormatUtil.nf((int)mySeconds, 2));
			myResult.append(":");
			
			myResult.append(CCFormatUtil.nf((int)myMillis,3));
			return myResult.toString();
		}
		
		public void time(double theTime) {
			if(!_myIsActive)return;
			
			_myTimeField.setText(timeToValueString(theTime));
		}

		@Override
		public void mouseClicked(MouseEvent theArg0) {
			_myIsActive = false;
		}

		@Override
		public void mouseEntered(MouseEvent theArg0) {}

		@Override
		public void mouseExited(MouseEvent theArg0) {}

		@Override
		public void mousePressed(MouseEvent theArg0) {}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent theArg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static final int MAX_RULER_LABELS = 10;
	public static final double MIN_RULER_INTERVAL = 0.25;
	
	private int _myViewWidth = 100;
	private int _myControlWidth = 0;
	
	private TimelineController _myTimelineController;
	private TransportController _myTransportController;
	
	private SwingTransportRulerMarkerDialog _myMarkerFrame;
	private InsertTimeDialog _myInsertTimeFrame;
	
	private JButton _myPlayButton;
	private JButton _myLoopButton;
	
	private TimeField _myTimeField;
	
	private SwingDraggableValueBox _mySpeedValue;
	
	public SwingTransportRulerView(TimelineController theTimelineController) {
		_myTimelineController = theTimelineController;
		_myTransportController = theTimelineController.transportController();
		
		_myMarkerFrame = new SwingTransportRulerMarkerDialog(this, "MARKER");
	    _myMarkerFrame.setSize( 300, 200 ); 
	    
	    _myInsertTimeFrame = new InsertTimeDialog("Insert Time");
	    _myInsertTimeFrame.setSize( 300, 200 ); 
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(_myTransportController == null)return;
				
				_myTransportController.mouseReleased(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(_myTransportController == null)return;
				
				if (e.getButton() == MouseEvent.BUTTON3) {
					_myInsertTimeFrame.setLocation(e.getXOnScreen(), e.getYOnScreen());
					_myInsertTimeFrame.setVisible(true);
    			} else if (e.getButton() == MouseEvent.BUTTON1) {
					_myMarkerFrame.setLocation(e.getXOnScreen(), e.getYOnScreen());
    			}
				_myTransportController.mousePressed(e);
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (_myTransportController == null)return;
				
				_myTransportController.mouseDragged(e);
				updateUI();
			}	
		});
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		_myPlayButton = createButton("play");
		_myPlayButton.addActionListener(new PlayButtonAction());
		
		_myLoopButton = createButton("loop");
		_myLoopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_myTransportController.loop();
			}
		});
		
		_myTimeField = new TimeField();
		add(_myTimeField._myTimeField);
		
		if(SwingGuiConstants.CREATE_SPEED_CONTROL) {
			_mySpeedValue = new SwingDraggableValueBox(1,0.1f,10,0.1f);
			_mySpeedValue.addListener(this);
			style(_mySpeedValue);
			add(_mySpeedValue);
			
			JLabel mySpeedLabel = new JLabel("speed");
			mySpeedLabel.setFont(SwingGuiConstants.ARIAL_11);
			add(mySpeedLabel);
		}
	}
	
	private void style(JComponent theComponent){
		theComponent.setPreferredSize(new Dimension(64,20));
		theComponent.setFont(SwingGuiConstants.ARIAL_11);
		theComponent.setBackground(new Color(0.9f, 0.9f, 0.9f));
		theComponent.setForeground(Color.BLACK);
//		theComponent.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	private JButton createButton(String theText){
		JButton myPButton = new JButton(theText);
		style(myPButton);
		add(myPButton);
		return myPButton;
	}

	public void speed(double theSpeed){
		if(_mySpeedValue != null)_mySpeedValue.value(theSpeed);
	}
	
	public void changeValue(double theValue) {
		_myTransportController.speed(theValue);	
	}
	
	public void showMarkerDialog(MarkerPoint theMarker) {
		_myMarkerFrame.marker(theMarker);
		_myMarkerFrame.setVisible(true);
		
	}
	
	public void controller(TransportController theTransportController) {
		_myTransportController = theTransportController;
	}
	
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.view.TransportRulerView#time(double)
	 */
	@Override
	public void time(double theTime) {
		_myTimeField.time(theTime);
		
		if (_myTransportController.isPlaying() && theTime % 1 > 0.5) {
			_myPlayButton.setSelected(true);
		} else {
			_myPlayButton.setSelected(false);
		}
	}
	
	public void render() {
		updateUI();
	}

	public void selectCursour() {
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public void defaultCursour() {
		setCursor(Cursor.getDefaultCursor());
	}
	
	@Override
	public void moveRangeCursor() {
		setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
	}
	
	@Override
	public void moveCursor() {
		setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}
	
	public int timeToViewX(double theCurveX) {
		return (int)((theCurveX-_myTransportController.lowerBound())/(_myTransportController.upperBound() - _myTransportController.lowerBound()) * _myViewWidth + _myControlWidth);
	}
	
	public double viewXToTime(int theViewX) {
		return ((double)theViewX - _myControlWidth) / (_myViewWidth) * (_myTransportController.upperBound() - _myTransportController.lowerBound()) + _myTransportController.lowerBound();
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(0, 20);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(0, 40);
	}
	
	public Dimension getMaximumSize() {
		return new Dimension(5000, 40);
	}
	
	public void setViewWidth(int theViewWidth, int theControlWidth) {
		_myViewWidth = theViewWidth;
		_myControlWidth = theControlWidth;
		updateUI();
	}
	
	private Stroke _myThinStroke = new BasicStroke(1);
	private Stroke _myThickStroke = new BasicStroke(2);
	
	private Color _myTextColor = new Color(100);
	private Color _myStepColor = new Color(0.8f, 0.8f, 0.8f);
	private Color _mySubStepColor = new Color(0.9f, 0.9f, 0.9f);
	
	private String timeToString(double theTime) {
		long myTime = (long)(theTime * 1000);
		long myMillis = myTime % 1000;
		myTime /= 1000;
		long mySeconds = myTime % 60;
		myTime /= 60;
		long myMinutes = myTime % 60;
		myTime /= 60;
		long myHours = myTime;
		
		StringBuffer myResult = new StringBuffer();
		if(myHours != 0) {
			myResult.append(myHours);
			myResult.append("h ");
		}
		if(myMinutes != 0) {
			myResult.append(myMinutes);
			myResult.append("min ");
		}
		if(mySeconds != 0) {
			myResult.append(mySeconds);
			myResult.append("s ");
		}
		if(myMillis != 0) {
			myResult.append(myMillis);
			myResult.append("ms ");
		}
		return myResult.toString();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D myG2 = (Graphics2D)g;
		g.setColor(new Color(255,255,255));
		g.fillRect(0, 0, getWidth(), getHeight() );
		
		RulerInterval ri = _myTransportController.rulerInterval();

		DecimalFormat myFormat = new DecimalFormat();
        myFormat.applyPattern("#0.##");
		
		double myStart = ri.interval() * (Math.floor(_myTransportController.lowerBound() / ri.interval()) ) ;
		
		for (double step = myStart; step <= _myTransportController.upperBound(); step = step + ri.interval()) {
			
	        int myX = timeToViewX(step);
	        if(myX < _myControlWidth)continue;
			
			g.setColor(_myStepColor);
			myG2.setStroke(_myThickStroke);
			g.drawLine(myX, 0, myX, getHeight());
			
			int myTimeX = myX;
			
			g.setColor(_mySubStepColor);
			myG2.setStroke(_myThinStroke);
			
			for(int i = 1; i < _myTimelineController.raster();i++) {
				myX = timeToViewX(step + ri.interval() * i / _myTimelineController.raster());
				g.drawLine(myX, 0, myX, getHeight() / 10);
			}
			
			String myTimeString = timeToString(step);
			
			g.setFont(SwingGuiConstants.ARIAL_BOLD_10);
	        g.setColor(_myTextColor);
			g.drawString(myTimeString, myTimeX + 5, 15);
		}
		ControlPoint myCurrentPoint = _myTransportController.trackData().ceiling(new ControlPoint(_myTransportController.lowerBound(),0));
//		if(myCurrentPoint == null) {
//			myCurrentPoint = _myTransportController.trackData().ceiling(new ControlPoint(_myTransportController.lowerBound(),0));
//		}
        
        while (myCurrentPoint != null) {
            if (myCurrentPoint.time() > _myTransportController.upperBound()) {
                break;
            }

            if(myCurrentPoint instanceof MarkerPoint) {
	            Point2D myUserPoint = _myTransportController.curveToViewSpace(myCurrentPoint);
	            MarkerPoint myMarker = (MarkerPoint)myCurrentPoint;
	            int myMarkerX = (int)myUserPoint.getX();
	            
	            g.setColor(new Color(1f, 0f,0f));
				g.drawLine(myMarkerX, getHeight()/2, myMarkerX, getHeight());
				g.drawString(myMarker.name(), myMarkerX + 5, getHeight()/2 + 15);
				
				Polygon myPolygon = new Polygon();
				myPolygon.addPoint(myMarkerX, getHeight()/2 + 5);
				myPolygon.addPoint(myMarkerX - 5, getHeight()/2);
				myPolygon.addPoint(myMarkerX + 5, getHeight()/2);
				g.fillPolygon(myPolygon);
            }
            
            if(myCurrentPoint.getType() == ControlPointType.TIMED_EVENT) {
            	BasicStroke myThinStroke = new BasicStroke(0.5f);
            	myG2.setStroke(myThinStroke);
            	myG2.setColor(new Color(100,100,255,50));
            	
            	TimedEventPoint myPoint = (TimedEventPoint) myCurrentPoint;
        		Point2D myLowerCorner = _myTransportController.curveToViewSpace(new ControlPoint(myCurrentPoint.time(), 1));
        		Point2D myUpperCorner = _myTransportController.curveToViewSpace(new ControlPoint(myPoint.endPoint().time(),0));

        		myG2.fillRect(
        			(int) myLowerCorner.getX(), getHeight() * 3 / 4,
        			(int) myUpperCorner.getX() - (int) myLowerCorner.getX(), getHeight()/4
        		);
        		myG2.setColor(new Color(0,0,255));
        		myG2.drawLine((int) myLowerCorner.getX(), getHeight() * 3 / 4, (int) myLowerCorner.getX(), getHeight());
        		myG2.drawLine((int) myUpperCorner.getX(), getHeight() * 3 / 4, (int) myUpperCorner.getX(), getHeight());
            }
            
			
            myCurrentPoint = myCurrentPoint.getNext();
        }
		
		int myTransportX = Math.max(_myControlWidth, timeToViewX(_myTransportController.time()));
		g.setColor(new Color(0.8f, 0.8f, 0.8f));
		g.drawLine(myTransportX, getHeight()/2, myTransportX, getHeight());
		
		
		
		int myLoopStartX = Math.max(_myControlWidth,timeToViewX(_myTransportController.loopStart()));
		int myLoopEndX = Math.max(_myControlWidth,timeToViewX(_myTransportController.loopEnd()));
		
		if(myLoopStartX == myLoopEndX)return;

		if(_myTransportController.doLoop()) {
			g.setColor(new Color(1f, 0.0f,0.0f,0.5f));
			g.drawRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
			g.setColor(new Color(1f, 0.0f,0.0f,0.25f));
			g.fillRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
		}else {
			g.setColor(new Color(0.7f, 0.7f,0.7f,0.5f));
			g.drawRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
			g.setColor(new Color(0.7f, 0.7f,0.7f,0.25f));
			g.fillRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
		}
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.util.TimedContentView#height()
	 */
	@Override
	public int height() {
		return getHeight();
	}
	
	public int width() {
		return getWidth();
	}
}
