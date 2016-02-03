package cc.creativecomputing.exco.domainmodels;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.CCExcoRelationType;
import cc.creativecomputing.exco.service.CCExcoWebService;

public class CCExcoEventList extends CCExcoNode{
	
	private CCExcoWebService _myWebService;

	public CCExcoEventList(JsonObject theObjectJson, CCExcoWebService theConnection) {
		super(theObjectJson);
		_myWebService = theConnection;
	}

	public List<CCExcoEvent> hasEvent() {
		List<CCExcoNode> myRunningModeNodes = _myWebService.relatedNodes(CCExcoRelationType.HAS_EVENT, this);
		if(myRunningModeNodes == null)return null;
		
		List<CCExcoEvent> myResult = new ArrayList<>();
		for(CCExcoNode myExcoNode:myRunningModeNodes){
			myResult.add(new CCExcoEvent(myExcoNode.json(), _myWebService));
		}
		return myResult;
	}
}
