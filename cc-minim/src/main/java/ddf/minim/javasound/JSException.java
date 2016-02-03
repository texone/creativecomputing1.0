package ddf.minim.javasound;

public class JSException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1891429429779844039L;

	public JSException() {
		super();
	}

	public JSException(String theMessage, Throwable theCause, boolean theEnableSuppression, boolean theWritableStackTrace) {
		super(theMessage, theCause, theEnableSuppression, theWritableStackTrace);
	}

	public JSException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	public JSException(String theMessage) {
		super(theMessage);
	}

	public JSException(Throwable theCause) {
		super(theCause);
	}

}
