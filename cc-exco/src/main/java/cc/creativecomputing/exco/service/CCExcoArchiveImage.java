package cc.creativecomputing.exco.service;

import com.google.gson.JsonObject;

public class CCExcoArchiveImage {
	
	/*
	 * {
	 * 	"node_id":647,
	 *   "title":{"ar":"Event_45.jpg","en":"Event_45.jpg"},
	 *   "status":"not yet released","tags":["Welcome Wall"]}
	 */
	
	private final String _myMimeType;
	private final String _myThumbnailMimeType;
	
	private final String _myUrl;
	private final String _myThumbnailUrl;

	public CCExcoArchiveImage(JsonObject theObjectJson) {
		
		_myMimeType = theObjectJson.get("mime_type").getAsString();
		_myThumbnailMimeType = theObjectJson.get("thumbnail_mime_type").getAsString();

		_myUrl = theObjectJson.get("url").getAsString();
		_myThumbnailUrl = theObjectJson.get("thumbnail_url").getAsString();
	}
	
	
	public String url(){
		return _myUrl;
	}
	
	public String thumbnailUrl(){
		return _myThumbnailUrl;
	}
	
	public String mimeType(){
		return _myMimeType;
	}
	
	public String thumbnailMimeType(){
		return _myThumbnailMimeType;
	}
}
