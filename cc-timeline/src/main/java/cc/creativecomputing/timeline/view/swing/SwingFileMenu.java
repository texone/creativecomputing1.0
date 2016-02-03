package cc.creativecomputing.timeline.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import cc.creativecomputing.timeline.controller.FileManager;


@SuppressWarnings("serial")
public class SwingFileMenu extends JMenu {
	
	private class XMLFileFilter extends FileFilter {

		@Override
		public boolean accept(File pathname) {
			int myDotIndex = pathname.getName().lastIndexOf(".");
			if (myDotIndex > 0) {
				String myExtension = pathname.getName().substring(myDotIndex + 1);
				if (myExtension.equals(_myFileManager.extension())) {
					return true;
				}
			} else if (pathname.isDirectory()) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return _myFileManager.description();
		}
		
	}
	
	private FileManager _myFileManager;
	
	private File _myCurrentDirectory;
	private File _myCurrentFile;
	private JFileChooser _myFileChooser;
	
	public SwingFileMenu(FileManager theFileManager) {
		super("File");
		
		_myFileManager = theFileManager;
		
		setMnemonic(KeyEvent.VK_F);
		
		JMenuItem myLoadItem = new JMenuItem("Load");
		myLoadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent theE) {
				loadFile();
			}
		});
		myLoadItem.setToolTipText("Removes all tracks and loads the tracks from the file.");
		myLoadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.META_MASK));
		add(myLoadItem);
		
		JMenuItem myNewItem = new JMenuItem("New");
		myNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent theE) {
				newFile();
			}
		});
		myNewItem.setToolTipText("Deletes all Control data.");
		myNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		add(myNewItem);
		
		JMenuItem myReloadItem = new JMenuItem("Reload Existing");
		myReloadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent theE) {
				reloadExistingTracks();
			}
		});
		myReloadItem.setToolTipText("Reloads the data for all tracks that exist in the file and the timeline.");
		myReloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
		add(myReloadItem);
		
		JMenuItem myInsertItem = new JMenuItem("Insert at Time");
		myInsertItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent theE) {
				insertAtTime();
			}
		});
		myInsertItem.setToolTipText("Inserts the data for in the file and the timeline.");
		myInsertItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
		add(myInsertItem);
		
		JMenuItem mySaveItem = new JMenuItem("Save");
		mySaveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				save();
			}
		});
		mySaveItem.setToolTipText("Saves the content of all tracks.");
		mySaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
		add(mySaveItem);
		
		JMenuItem mySaveAsItem = new JMenuItem("Save As ...");
		mySaveAsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				saveAs();
			}
		});
		mySaveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
		add(mySaveAsItem);
		
		JMenuItem mySaveSelectionItem = new JMenuItem("Save Selection");
		mySaveSelectionItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				saveSelection();
			}
		});
		mySaveSelectionItem.setToolTipText("Saves the content of all tracks.");
		add(mySaveSelectionItem);
		
		_myCurrentDirectory = new File(".");
		_myCurrentFile = null;
		
		_myFileChooser = new JFileChooser();
		_myFileChooser.setFileFilter(new XMLFileFilter());
	}
	
	private File choseFile(final String theText) {
		_myFileChooser.setCurrentDirectory(_myCurrentDirectory);
		int myRetVal = _myFileChooser.showDialog(getParent(),theText);
		if (myRetVal == JFileChooser.APPROVE_OPTION) {
			try {
				File myChoosenFile = _myFileChooser.getSelectedFile();

				
				String myFile = myChoosenFile.getAbsolutePath();
				if(!myFile.endsWith("." + _myFileManager.extension()))myFile = myFile + "." + _myFileManager.extension();
				
				myChoosenFile = new File(myFile);
				
				_myCurrentDirectory = myChoosenFile.getParentFile();
				_myCurrentFile = myChoosenFile;
				
				return myChoosenFile;
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public void newFile() {
		_myCurrentDirectory = new File(".");
		_myCurrentFile = null;
		_myFileManager.newFile();
	}

	private void loadFile() {
		File myFile = choseFile("open");
		
		if (myFile != null) {
			_myFileManager.loadFile(myFile);
		}
	}
	
	private void reloadExistingTracks() {
		File myFile = choseFile("open");
		if (myFile != null) {
			_myFileManager.reloadExistingTracks(myFile);
		}
	}
	
	private void insertAtTime(){
		File myFile = choseFile("open");
		if (myFile != null) {
			_myFileManager.insertAtTime(myFile);
		}
	}
	
	public void save() {
		if (_myCurrentFile != null) {
			_myFileManager.save(_myCurrentFile);
		} else {
			saveAs();
		}
	}
	
	private void saveAs() {
		File myFile = choseFile("save");
		if (myFile != null) {
			_myFileManager.save(myFile);
		}
	}
	
	private void saveSelection(){
		File myFile = choseFile("save selection");
		if (myFile != null) {
			_myFileManager.saveSelection(myFile);
		}
	}
}
