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

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings.CCGLContainer;

public class CCMenubarDemo extends CCApp {
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#frameSetup()
	 */
	@Override
	public void frameSetup() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		JMenuBar _myMenuBar = new JMenuBar();
		menuBar(_myMenuBar);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false); //so menus appear above GLCanvas 
		

      JMenu options = new JMenu("Options"); 
      options.add(new JCheckBoxMenuItem("Wireframe")); 
      options.add(new JCheckBoxMenuItem("Head Light")); 
      options.add(new JCheckBoxMenuItem("Directional Light"));
      options.add(new JCheckBoxMenuItem("Specular Lighting")); 
      options.add(new JCheckBoxMenuItem("Two-sided Lighting")); 

      JMenu levels = new JMenu("Level"); 
      ButtonGroup lbg = new ButtonGroup(); 
      JRadioButtonMenuItem[] levelItems = new JRadioButtonMenuItem[7]; 
      for (int i = 0; i < levelItems.length; i++) { 
          levelItems[i] = new JRadioButtonMenuItem(i + ""); 
          levels.add(levelItems[i]); 
          lbg.add(levelItems[i]); 
          
      } 
      _myMenuBar.add(levels); 
      
	}

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMenubarDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().container(CCGLContainer.FRAME);
		myManager.start();
	}
}

