package dare.daremall.core.exception;

public class CannotAddNewItemException extends RuntimeException{
    public CannotAddNewItemException() {
        super();
    }

    public CannotAddNewItemException(String message) {
        super(message);
    }
}
