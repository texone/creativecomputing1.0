package cc.creativecomputing.exco.domainmodels;

import java.time.LocalDate;

public class CCExcoInterval {

	private LocalDate _myStart;
	private LocalDate _myEnd;
	
	public CCExcoInterval(LocalDate theStart, LocalDate theEnd){
		_myStart = theStart;
		_myEnd = theEnd;
	}
	
	public boolean isWithin(LocalDate theDate){
		return theDate.isAfter(_myStart) && theDate.isBefore(_myEnd);
	}
}
