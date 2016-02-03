package cc.creativecomputing.exco;

import java.util.List;

import cc.creativecomputing.exco.service.CCExcoNotificationService;
import cc.creativecomputing.exco.service.CCExcoWebService;
import cc.creativecomputing.exco.service.CCExcoNotificationService.CCExcoNotificationListener;
import cc.creativecomputing.util.logging.CCLog;

public class CCExcoTest {
	private CCExcoWebService _myWebservice;
	private CCExcoNotificationService _myNotificationService;

	public CCExcoTest() {
		_myWebservice = new CCExcoWebService("https://backend.it-exhibits.artcom.de/");
		
		_myNotificationService = new CCExcoNotificationService(
			"https://backend.it-exhibits.artcom.de/notification-service/", 
			"10.1.8.110", 
			4574
		);
	}

	public CCExcoNode searchExhibitByName(String theExhibitName) {
		return _myWebservice.searchNodeForId(CCExcoNodeType.EXHIBIT, theExhibitName);
	}

	public CCExcoNode currentOperationalMode(CCExcoNode theExhibit) {
		List<CCExcoNode> myRelatedNodes = _myWebservice.relatedNodes(CCExcoRelationType.IS_CURRENTLY_IN_OPERATIONAL_MODE, theExhibit);

		if (myRelatedNodes == null || myRelatedNodes.size() < 1)
			return null;

		return myRelatedNodes.get(0);
	}

	public void addOperationalModeChangeListener(CCExcoNode theExhibit, CCExcoNotificationListener theListener) {
		_myNotificationService.registerCreateRelation(theExhibit, CCExcoRelationType.IS_CURRENTLY_IN_OPERATIONAL_MODE, theListener);
	}

	public void close() {
		_myNotificationService.close();
	}

	private static class CCOperationalModeChangeListener implements CCExcoNotificationListener {

		private CCExcoTest _myExcoTest;
		private CCExcoNode _myExhibit;

		public CCOperationalModeChangeListener(CCExcoTest theExcoTest, CCExcoNode theExhibit) {
			_myExcoTest = theExcoTest;
			_myExhibit = theExhibit;
		}

		@Override
		public void onNotification() {
			CCExcoNode myOperationalMode = _myExcoTest.currentOperationalMode(_myExhibit);
			CCLog.info(myOperationalMode);
		}

	}

	public static void main(String[] args) {
		
			CCExcoWebService _myConnection = new CCExcoWebService(
				"https://backend.it-exhibits.artcom.de/exco-web/"
			);
		
			CCExcoNode myExhibit = _myConnection.searchExhibitById("welcome_wall");

			CCLog.info(" type:     " + myExhibit.type());
			CCLog.info(" id:       " + myExhibit.objectID());
			CCLog.info(" url:      " + myExhibit.url());
			CCLog.info(" prop.id:  " + myExhibit.property("id").stringValue());
			CCLog.info(" prop.name:" + myExhibit.property("name").stringValue());
		
		
		

//		CCExcoNode myOperationalMode = myTest.currentOperationalMode(myExhibit);
//		CCLog.info(myOperationalMode);
//
//		myTest.addOperationalModeChangeListener(myExhibit, new CCOperationalModeChangeListener(myTest, myExhibit));
//
//		try {
//			Thread.sleep(300000);
//		} catch (Exception e) {
//		}
//
//		myTest.close();
//		_myConnection.close();
		// myRegistration.unregisterFromEvent
		// ("/created/relationship/start_node/53/type/is_currently_in_running_mode/rm");
	}
}
