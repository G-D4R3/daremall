package dare.daremall.exception;

public class CannotAddNewAdException extends RuntimeException{
    public CannotAddNewAdException() {
        super();
    }

    public CannotAddNewAdException(String message) {
        super(message);
    }
}
