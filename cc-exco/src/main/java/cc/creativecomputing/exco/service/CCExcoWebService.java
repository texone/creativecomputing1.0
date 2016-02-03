package cc.creativecomputing.exco.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.CCExcoNodeType;
import cc.creativecomputing.exco.CCExcoProperty;
import cc.creativecomputing.exco.CCExcoRelation;
import cc.creativecomputing.exco.CCExcoRelationType;
import cc.creativecomputing.exco.CCExcoSearchKeyType;
import cc.creativecomputing.exco.domainmodels.CCExcoEvent;
import cc.creativecomputing.exco.domainmodels.CCExcoEventList;
import cc.creativecomputing.exco.domainmodels.CCExcoExhibit;
import cc.creativecomputing.exco.domainmodels.CCExcoLocation;
import cc.creativecomputing.exco.domainmodels.CCExcoRunningMode;
import cc.creativecomputing.util.logging.CCLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CCExcoWebService extends CCExcoService {
	
	private String _myToken;
	
	public CCExcoWebService(String theDataUrl){
		super(theDataUrl);
		_myToken = getToken("admin", "test");
	}
	
	private static final String EXCO_WEB_SESSION = "exco-web-session";
	
	private String getToken(String theID, String thePassword){

		try {
			byte[] myJsonBytes = new String(
				"{" +
					"\"id\":\""+theID+"\"," +
					"\"authentication_string\":\""+thePassword+"\"" +
				"}"
			).getBytes(StandardCharsets.UTF_8);
		

			HttpURLConnection myConnection = (HttpURLConnection)new URL(_myServiceUrl + "user/login.json").openConnection();
			myConnection.setDoOutput(true);
			myConnection.setRequestMethod("POST");
			myConnection.setRequestProperty("Content-Type", "application/json");
			myConnection.setRequestProperty("Content-Length", Integer.toString(myJsonBytes.length));
			myConnection.setDoOutput(true);
			myConnection.getOutputStream().write(myJsonBytes);
			
			String myCookie = myConnection.getHeaderField("Set-Cookie");
			if(myCookie == null){
				throw new RuntimeException("no cookie defined");
			}
			myCookie = myCookie.substring(0, myCookie.indexOf(";"));
			String[] myValues = myCookie.split("=");
			String cookieName = myValues[0];
			String cookieValue = myValues[1];
			
			if(!cookieName.equals(EXCO_WEB_SESSION)){
				throw new RuntimeException("no cookie " + EXCO_WEB_SESSION + " defined");
			}
			return cookieValue;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CCExcoRelation relation(CCExcoRelationType theRelationType, CCExcoNode theNode){
		return relations(theRelationType, theNode).get(0);
	}
	
	public CCExcoRelation relation(CCExcoRelationType theRelationType, String theBucketName, CCExcoNode theNode){
		JsonArray myArray = jsonRequest(
			"/edm/" + theNode.type() + 
			"/" + theNode.objectID() + 
			"/relations/" + theRelationType.id() + 
			".json?search_for_key=bucket_name&with_value=" + theBucketName
		).getAsJsonArray();
		return new CCExcoRelation(myArray.get(0).getAsJsonObject());
	}
	
	public List<CCExcoRelation> relations(CCExcoRelationType theRelationType, CCExcoNode theNode){
		JsonArray myArray = (JsonArray)jsonRequest("/edm/" + theNode.type() + "/" + theNode.objectID() + "/relations/" + theRelationType.id() + ".json");
		List<CCExcoRelation> myResult = new ArrayList<CCExcoRelation>();
		for(JsonElement myElement:myArray){
			myResult.add(new CCExcoRelation(myElement.getAsJsonObject()));
		}
		return myResult;
	}
	
	public List<CCExcoNode> relatedNodes(CCExcoRelationType theRelationType, CCExcoNode theStartNode){
		JsonObject myResult = (JsonObject)jsonRequest("/edm/" + theStartNode.type() + "/" + theStartNode.objectID() + "/relations/" + theRelationType.id() + "/nodes.json");
		JsonArray myNodeArray = myResult.entrySet().iterator().next().getValue().getAsJsonArray();
		List<CCExcoNode> myRelatedNodes = new ArrayList<>();
		for(JsonElement myElement : myNodeArray){
			myRelatedNodes.add(new CCExcoNode(myElement.getAsJsonObject()));
		}
		return myRelatedNodes;
	}
	
	public CCExcoNode relatedNode(CCExcoRelationType theRelationType, CCExcoNode theStartNode){
		CCExcoRelation myRelation = relation(theRelationType, theStartNode);
		return searchNodeForObjectId(CCExcoNodeType.UNKNOWN, myRelation.endNodeID());
	}
	
	public CCExcoNode relatedNode(CCExcoRelationType theRelationType, String theBucketName, CCExcoNode theStartNode){
		CCExcoRelation myRelation = relation(theRelationType, theBucketName, theStartNode);
		return searchNodeForObjectId(CCExcoNodeType.UNKNOWN, myRelation.endNodeID());
	}
//	}
	
	/*
	 * [
	 * 	{
	 * 	"url":"/edm/exhibits/15",
	 * "id":15,
	 * "type":"exhibit",
	 * "last_modified":"2014-01-27T09:50:04Z",
	 * "properties":[{
	 * 		"url":"/edm/exhibits/15/properties/id",
	 * 		"name":"id",
	 * 		"type":"string",
	 * 		"value":"BOX_02"
	 * 		},{
	 * 		"url":"/edm/exhibits/15/properties/name",
	 * 		"name":"name",
	 * 		"type":"string",
	 * 		"value":"Body Scan"}]}]
	 */
	public CCExcoNode searchNode(CCExcoNodeType theNodeType, CCExcoSearchKeyType theSearchKey, String theValue){
		JsonElement myResult = jsonRequest ("/edm/" + theNodeType.id() + "s.json?search_for_key=" + theSearchKey.id() + "&with_value=" + theValue);
		JsonArray myArray = myResult.getAsJsonArray();
		JsonObject myNodeJson = myArray.get(0).getAsJsonObject();
		
		return new CCExcoNode(
			myNodeJson
		);
	}
	
	public CCExcoNode searchNodeForName(CCExcoNodeType theNodeType, String theValue){
		return searchNode(theNodeType, CCExcoSearchKeyType.NAME, theValue);
	}
	
	public CCExcoNode searchNodeForId(CCExcoNodeType theNodeType, String theValue){
		return searchNode(theNodeType, CCExcoSearchKeyType.ID, theValue);
	}
	
	public CCExcoNode searchNodeForObjectId(CCExcoNodeType theNodeType, int theValue){
		JsonObject myObject = (JsonObject)jsonRequest("/edm/" + theNodeType.id() + "/" + theValue + ".json");
		return new CCExcoNode(myObject);
	}
	
	public CCExcoExhibit searchExhibit(CCExcoSearchKeyType theSearchKey, String theValue){
		return new CCExcoExhibit(searchNode(CCExcoNodeType.EXHIBIT, theSearchKey, theValue).json(), this);
	}
	
	public CCExcoExhibit searchExhibitByName(String theExhibitName) {
		return searchExhibit(CCExcoSearchKeyType.NAME, theExhibitName);
	}
	
	public CCExcoExhibit searchExhibitById(String theExhibitId) {
		return searchExhibit(CCExcoSearchKeyType.ID, theExhibitId);
	}
	
	
	public CCExcoEvent searchEvent(CCExcoSearchKeyType theSearchKey, String theValue){
		return new CCExcoEvent(searchNode(CCExcoNodeType.EVENT, theSearchKey, theValue).json(), this);
	}
	
	public CCExcoEvent searchEventByName(String theEventName) {
		return searchEvent(CCExcoSearchKeyType.NAME, theEventName);
	}
	
	public CCExcoEvent searchEventById(String theEventId) {
		return searchEvent(CCExcoSearchKeyType.ID, theEventId);
	}
	
	
	public CCExcoEventList searchEventList(CCExcoSearchKeyType theSearchKey, String theValue){
		return new CCExcoEventList(searchNode(CCExcoNodeType.EVENT_LIST, theSearchKey, theValue).json(), this);
	}
	
	public CCExcoEventList searchEventListByName(String theEventName) {
		return searchEventList(CCExcoSearchKeyType.NAME, theEventName);
	}
	
	public CCExcoEventList searchEventListById(String theEventId) {
		return searchEventList(CCExcoSearchKeyType.ID, theEventId);
	}
	
	
	public CCExcoRunningMode searchRunningMode(CCExcoSearchKeyType theSearchKey, String theValue){
		return new CCExcoRunningMode(searchNode(CCExcoNodeType.EXHIBIT, theSearchKey, theValue).json(), this);
	}
	
	public CCExcoRunningMode searchRunningModeByName(String theExhibitName) {
		return searchRunningMode(CCExcoSearchKeyType.NAME, theExhibitName);
	}
	
	public CCExcoRunningMode searchRunningModeByID(String theExhibitId) {
		return searchRunningMode(CCExcoSearchKeyType.ID, theExhibitId);
	}
	
	
	public CCExcoLocation searchLocation(CCExcoSearchKeyType theSearchKey, String theValue){
		return new CCExcoLocation(searchNode(CCExcoNodeType.LOCATION, theSearchKey, theValue).json(), this);
	}
	
	public CCExcoLocation searchLocationByName(String theLocationName) {
		return searchLocation(CCExcoSearchKeyType.NAME, theLocationName);
	}
	
	public CCExcoLocation searchLocationById(String theLocationId) {
		return searchLocation(CCExcoSearchKeyType.ID, theLocationId);
	}
	
	public void jsonRequest(String theUrl, String theMethod, JsonElement theJson){
		try {
			String myJsonString = theJson.toString();
			byte[] postData = myJsonString.getBytes( StandardCharsets.UTF_8 );
			URL url = new URL(_myServiceUrl + theUrl);
			CCLog.info(_myServiceUrl + theUrl + ":" + myJsonString);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(theMethod);
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("Content-Length", Integer.toString(postData.length));
			httpCon.setRequestProperty("Cookie", EXCO_WEB_SESSION+"=" + _myToken);
			httpCon.setDoOutput(true);
	        httpCon.getOutputStream().write(postData);
	        CCLog.info(httpCon.getResponseCode());

//			try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream(), "UTF-8"))){
//				StringBuffer myResult = new StringBuffer();
//				for (String line; (line = reader.readLine()) != null;) {
//					myResult.append(line);
//				}
//				CCLog.info(myResult.toString());
//			} catch (Exception e) {
//				CCLog.error(e);
//			}
			

		} catch (Exception e) {
			CCLog.error(e);
		}
	}
	
	public void jsonPost(String theUrl, JsonElement theJson){
		jsonRequest(theUrl, "POST", theJson);
	}
	
	public void jsonPut(String theUrl, JsonElement theJson){
		jsonRequest(theUrl, "PUT", theJson);
	}
	
	public void updateNode(CCExcoNode theNode){
		 jsonPut ("edm/" + CCExcoNodeType.RUNTIME_STATE.id() + "/" + theNode.objectID() + ".json", theNode.json());
	}
	
	public void updateProperty(CCExcoNode theNode, String theProperty){
		CCExcoProperty myProperty = theNode.property(theProperty);
		 jsonPut ("edm/" + CCExcoNodeType.RUNTIME_STATE.id() + "/" + theNode.objectID() + "/properties/" + theProperty + ".json", myProperty.json());
	}
	 
	public void update(final float theDeltaTime){
		
	}
	
}
