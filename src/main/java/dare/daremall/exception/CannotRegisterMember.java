package dare.daremall.exception;

public class CannotRegisterMember extends RuntimeException{

    public CannotRegisterMember() {
        super();
    }

    public CannotRegisterMember(String message) {
        super(message);
    }
}
