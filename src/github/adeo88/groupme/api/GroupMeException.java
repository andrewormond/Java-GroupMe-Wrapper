package github.adeo88.groupme.api;

/**
 * Exception Class meant to unify various HTTP and JSON errors into one
 * convenient exception. It additionally can carry convenient information about
 * the meaning of the exception. For example, if the user is not the owner of
 * the group and therefore cannot change ownership.
 * 
 * @author adeo8
 *
 */
public class GroupMeException extends Exception {

	private static final long serialVersionUID = 8993708789488800778L;
	
	/**
	 * The HTTP Code associated with this error.
	 */
	public int code;

	/**
	 * Creates the exception with the respective HTTP Code.
	 * @param code The HTTP Code
	 */
	public GroupMeException(int code) {
		super();
		this.code = code;
	}


	/**
	 * Creates the exception with the respective HTTP Code and message.
	 * @param code The HTTP Code
	 * @param message The message to attach.
	 */
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
