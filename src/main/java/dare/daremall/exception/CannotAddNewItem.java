package dare.daremall.exception;

public class CannotAddNewItem extends RuntimeException{
    public CannotAddNewItem() {
        super();
    }

    public CannotAddNewItem(String message) {
        super(message);
    }
}
