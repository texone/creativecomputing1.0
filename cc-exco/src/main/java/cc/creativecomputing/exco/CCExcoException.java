package cc.creativecomputing.exco;

public class CCExcoException extends RuntimeException{

	public CCExcoException() {
		super();
	}

	public CCExcoException(String theMessage, Throwable theCause, boolean theEnableSuppression, boolean theWritableStackTrace) {
		super(theMessage, theCause, theEnableSuppression, theWritableStackTrace);
	}

	public CCExcoException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	public CCExcoException(String theMessage) {
		super(theMessage);
	}

	public CCExcoException(Throwable theCause) {
		super(theCause);
	}

}
