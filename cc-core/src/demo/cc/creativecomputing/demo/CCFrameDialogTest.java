/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.demo;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings.CCCloseOperation;

/**
 * @author christianriekoff
 * 
 */
public class CCFrameDialogTest extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class CCInnerApp extends CCApp {

		@Override
		public void setup() {}

		@Override
		public void draw() {
			g.clear();

		}
	}

	public CCFrameDialogTest() {
		super("Hello");
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem item = new JMenuItem("Woah");
		file.add(item);
		menuBar.add(file);
		setJMenuBar(menuBar);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(100, 100);
		pack();
		setVisible(true);

		CCApplicationManager myManager1 = new CCApplicationManager(CCInnerApp.class);
		myManager1.settings().size(500, 500);
		myManager1.settings().closeOperation(CCCloseOperation.HIDE_ON_CLOSE);
		myManager1.settings().location(600,200);
		myManager1.settings().dialog(this);
		myManager1.start();

		CCApplicationManager myManager2 = new CCApplicationManager(CCInnerApp.class);
		myManager2.settings().size(500, 500);
		myManager2.settings().closeOperation(CCCloseOperation.HIDE_ON_CLOSE);
		myManager2.settings().location(100,200);
		myManager2.settings().dialog(this);
		myManager2.start();
	}

	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CCFrameDialogTest();
			}
		});
	}
}
