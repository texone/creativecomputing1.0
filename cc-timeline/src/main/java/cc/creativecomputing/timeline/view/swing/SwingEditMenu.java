package cc.creativecomputing.timeline.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.model.UndoHistory;


@SuppressWarnings("serial")
public class SwingEditMenu extends JMenu {

	private TimelineController _myTimelineController;

	public SwingEditMenu(TimelineController theTimelineController) {
		super("Edit");

		setMnemonic(KeyEvent.VK_E);

		_myTimelineController = theTimelineController;
		
		if(SwingGuiConstants.CREATE_UNDO_ENTRIES) {
			JMenuItem myUndoMenu = new JMenuItem("Undo");
			myUndoMenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					UndoHistory.instance().undo();
				}
			});
			myUndoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK));
			myUndoMenu.setMnemonic(KeyEvent.VK_Z);
			add(myUndoMenu);
			
			JMenuItem myRedoMenu = new JMenuItem("Redo");
			myRedoMenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					UndoHistory.instance().redo();
				}
			});
			myRedoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
			myRedoMenu.setMnemonic(KeyEvent.VK_R);
			add(myRedoMenu);
		}

		JMenuItem myCutMenu = new JMenuItem("Cut");
		myCutMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				// TODO fix cut
//				_myTimelineController.selectionController().cut();
			}
		});
		myCutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.META_MASK));
		myCutMenu.setMnemonic(KeyEvent.VK_T);
		add(myCutMenu);

		JMenuItem myCopyMenu = new JMenuItem("Copy");
		myCopyMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				// TODO fix copy
//				_myTimelineController.selectionController().copy();
			}
		});
		myCopyMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		myCopyMenu.setMnemonic(KeyEvent.VK_P);
		add(myCopyMenu);

		JMenuItem myPasteMenu = new JMenuItem("Paste");
		myPasteMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				// TODO fix insert
//				_myTimelineController.selectionController().insert();
			}
		});
		myPasteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.META_MASK));
		myPasteMenu.setMnemonic(KeyEvent.VK_A);
		add(myPasteMenu);

		JMenuItem myReplaceMenu = new JMenuItem("Replace");
		myReplaceMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				// TODO fix replace
//				_myTimelineController.selectionController().replace();
			}
		});
		myReplaceMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.SHIFT_MASK | ActionEvent.META_MASK));
		myReplaceMenu.setMnemonic(KeyEvent.VK_R);
		add(myReplaceMenu);

		// TODO fix write values
//		JMenuItem myWriteValuesMenue = new JMenuItem("Write Values");
//		myWriteValuesMenue.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent theE) {
//				_myTimelineController.writeValues();
//			}
//		});
//		myWriteValuesMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.META_MASK));
//		myWriteValuesMenue.setMnemonic(KeyEvent.VK_W);
//		add(myWriteValuesMenue);
		
		JMenuItem myInsertTimeMenue = new JMenuItem("Insert Time");
		myInsertTimeMenue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				_myTimelineController.insertTime();
			}
		});
		myInsertTimeMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.META_MASK));
		myInsertTimeMenue.setMnemonic(KeyEvent.VK_I);
		add(myInsertTimeMenue);
		
		JMenuItem myRemoveTimeMenue = new JMenuItem("Remove Time");
		myRemoveTimeMenue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				_myTimelineController.removeTime();
			}
		});
		myRemoveTimeMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.META_MASK));
		myRemoveTimeMenue.setMnemonic(KeyEvent.VK_R);
		add(myRemoveTimeMenue);
		
		JMenuItem myReverseTracksMenue = new JMenuItem("Reverse");
		myReverseTracksMenue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				_myTimelineController.reverseTracks();
			}
		});
		add(myReverseTracksMenue);
		
		
	}
}
