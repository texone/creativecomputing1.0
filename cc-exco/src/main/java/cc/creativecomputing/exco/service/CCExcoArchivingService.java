package cc.creativecomputing.exco.service;

import cc.creativecomputing.exco.service.CCExcoService;

public class CCExcoArchivingService extends CCExcoService{

	public CCExcoArchivingService(String theUrl){
		super(theUrl);
	}
	
	public CCExcoArchiveImage image(String theImageId){
		return new CCExcoArchiveImage(jsonRequest(theImageId).getAsJsonObject());
	}
}
