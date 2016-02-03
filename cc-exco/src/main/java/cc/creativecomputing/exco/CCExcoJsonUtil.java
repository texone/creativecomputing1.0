package cc.creativecomputing.exco;

import com.google.gson.JsonObject;

public class CCExcoJsonUtil {
	
	public static int intValue (JsonObject theObject, String theField) {
		if(theObject == null){
			throw new CCExcoException("Cannot retreive valie if object is null");
		}
		
		if (theObject.get(theField)==null) {
			throw new CCExcoException("Json Object does not contain field:" + theField + "\n" + theObject);
		}

		return theObject.get(theField).getAsInt();
	}
	
	public static String stringValue (JsonObject theObject, String theField) {
		if(theObject == null){
			throw new CCExcoException("Cannot retreive valie if object is null");
		}
		
		if (theObject.get(theField)==null) {
			throw new CCExcoException("Json Object does not contain field:" + theField + "\n" + theObject);
		}

		return theObject.get(theField).getAsString();
	}
}
