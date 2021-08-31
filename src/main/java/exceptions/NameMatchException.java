package exceptions;

public class NameMatchException extends Exception {
    public NameMatchException() {
    }

    public NameMatchException(String message) {
        super(message);
    }
}
