package dare.daremall.core.exception;

public class CannotRegisterMemberException extends RuntimeException{

    public CannotRegisterMemberException() {
        super();
    }

    public CannotRegisterMemberException(String message) {
        super(message);
    }
}
