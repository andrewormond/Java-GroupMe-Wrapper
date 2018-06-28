package github.adeo88.groupme.api;

public class GroupMeException extends Exception {

	private static final long serialVersionUID = 8993708789488800778L;
	public int code;

	public GroupMeException(int code) {
		super();
		this.code = code;
	}

	public GroupMeException(String message, int code) {
		super(message);
		this.code = code;
	}

	public GroupMeException(Throwable cause) {
		super(cause);
	}

	public GroupMeException(String message, Throwable cause) {
		super(message, cause);
	}

	public GroupMeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
