package cc.creativecomputing.exco.domainmodels;

import java.util.Map;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.service.CCExcoWebService;

import com.google.gson.JsonObject;

public class CCExcoImage extends CCExcoNode{
	
	private CCExcoWebService _myConnection;

	public CCExcoImage(JsonObject theObjectJson, CCExcoWebService theConnection) {
		super(theObjectJson);
		_myConnection = theConnection;
	}
	
	public Map<String,String> title(){
		return property("title").stringMap();
	}
	
	public Map<String,String> description(){
		return property("description").stringMap();
	}
	
	public Map<String,String> author(){
		return property("author").stringMap();
	}
	
	public String url(){
		return property("url").stringValue();
	}
	
	public String thumbnailUrl(){
		return property("thumbnail_url").stringValue();
	}
	
	public String mimeType(){
		return property("mime_type").stringValue();
	}
	
	public String thumbnailMimeType(){
		return property("thumbnail_mime_type").stringValue();
	}
}
