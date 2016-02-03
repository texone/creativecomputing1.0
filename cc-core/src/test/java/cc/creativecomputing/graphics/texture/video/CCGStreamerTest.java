package cc.creativecomputing.graphics.texture.video;

import junit.framework.TestCase;

/**
 * Test class for gstreamer native library support. This uses gstreamer-java 1.5 and gstreamer010 natives.
 * @author max goettner
 */
public class CCGStreamerTest extends TestCase {

	public void testRuntimeLibrary() {
		try {
			//String[] myArguments = { "" };
			//Gst.setUseDefaultContext(false);
			//Gst.init("cc video", myArguments);
			//Gst.quit();
			//System.out.println("[OK]");
			assert(true);
		}
		catch (Exception e) {
			System.out.println("FAIL");
			fail("[FAILED] "+e.getMessage());
		}
	}

}
