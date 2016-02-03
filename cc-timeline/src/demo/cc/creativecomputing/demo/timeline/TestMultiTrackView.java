package cc.creativecomputing.demo.timeline;

import javax.swing.JButton;
import javax.swing.JFrame;

import cc.creativecomputing.timeline.view.swing.track.SwingMultiTrackPanel;
import cc.creativecomputing.timeline.view.swing.track.SwingTableLayout.TableLayoutConstraints;


public class TestMultiTrackView {
	
	public static void createAndShowGUI() {
		JFrame myFrame = new JFrame();
		
		SwingMultiTrackPanel myMultiTrackPanel = new SwingMultiTrackPanel(myFrame);
		
		JButton b1 = new JButton("b1");
		myMultiTrackPanel.add(b1,new TableLayoutConstraints(0, 0));

		
		JButton b2 = new JButton("b2");
		myMultiTrackPanel.add(b2,new TableLayoutConstraints(1, 0));
		
		JButton b3 = new JButton("b3");
		myMultiTrackPanel.add(b3, new TableLayoutConstraints(1, 1));
	
		myFrame.getContentPane().add(myMultiTrackPanel);
		myFrame.pack();
		myFrame.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	     });
	}

}
