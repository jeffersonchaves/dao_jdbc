package exceptions;

public class DatabaseException extends RuntimeException {

	public DatabaseException(String msg) {
		super(msg);
	}
	
	public DatabaseException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}
