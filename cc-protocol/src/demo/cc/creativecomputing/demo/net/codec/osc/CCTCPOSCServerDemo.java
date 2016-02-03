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
package cc.creativecomputing.demo.net.codec.osc;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

public class CCTCPOSCServerDemo extends CCApp {

//	private boolean pause = false;

	@Override
	public void setup() {
//		final Object sync = new Object();
//		 	final CCTCPServer<CCOSCMessage> c;
//		  try {
//		  	// create TCP server on loopback port 0x5454
//		  	c = new CCTCPServer 0x5454);
//		  } catch (IOException e1) {
//		  	e1.printStackTrace();
//		  	return;
//		  }
//		  
//		  // now add a listener for incoming messages from
//		  // any of the active connections
//		  c.addOSCListener(new OSCListener() {
//		  	public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
//		  		// first of all, send a reply message (just a demo)
//		  		try {
//		  			c.send(new OSCMessage(&quot;/done&quot;, new Object[] { m.getName() }), addr);
//		  		} catch (IOException e1) {
//		  			e1.printStackTrace();
//		  		}
//		  
//		  		if (m.getName().equals(&quot;/pause&quot;)) {
//		  			// tell the main thread to pause the server,
//		  			// wake up the main thread
//		  			pause = true;
//		  			synchronized (sync) {
//		  				sync.notifyAll();
//		  			}
//		  		} else if (m.getName().equals(&quot;/quit&quot;)) {
//		  			// wake up the main thread
//		  			synchronized (sync) {
//		  				sync.notifyAll();
//		  			}
//		  		} else if (m.getName().equals(&quot;/dumpOSC&quot;)) {
//		  			// change dumping behaviour
//		  			c.dumpOSC(((Number) m.getArg(0)).intValue(), System.err);
//		  		}
//		  	}
//		  });
//		  try {
//		  	do {
//		  		if (pause) {
//		  			System.out.println(&quot;  waiting four seconds...&quot;);
//		  			try {
//		  				Thread.sleep(4000);
//		  			} catch (InterruptedException e1) {
//		  			}
//		  			pause = false;
//		  		}
//		  		System.out.println(&quot;  start()&quot;);
//		  		// start the server (make it attentive for incoming connection requests)
//		  		c.start();
//		  		try {
//		  			synchronized (sync) {
//		  				sync.wait();
//		  			}
//		  		} catch (InterruptedException e1) {
//		  		}
//		  
//		  		System.out.println(&quot;  stop()&quot;);
//		  		c.stop();
//		  	} while (pause);
//		  } catch (IOException e1) {
//		  	e1.printStackTrace();
//		  }
//		  
//		  // kill the server, free its resources
//		  c.dispose();
	}

	@Override
	public void update(final float theDeltaTime) {

	}

	@Override
	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(
				CCTCPOSCServerDemo.class);
		myManager.settings().location(100, 100);
		myManager.start();
	}
}
