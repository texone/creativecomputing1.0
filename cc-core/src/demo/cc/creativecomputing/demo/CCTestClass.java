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

import java.util.Timer;
import java.util.TimerTask;

import cc.creativecomputing.util.logging.CCLog;

/**
 * @author info
 * 
 */
public class CCTestClass {
	static class Task extends TimerTask {
		@Override
		public void run() {
			CCLog.info("Make my day.");
		}
	}
	
	static class ExceptionTask extends TimerTask {
		@Override
		public void run() {
			throw new RuntimeException("SUCKER");
		}
	}

	public static void main(String[] args) {
		CCLog.info("YO");
		CCLog.info("YO");
		Timer timer = new Timer(); 
	    timer.schedule( new Task(), 2000,3000 ); 
	}
}
