package exceptions;

public class NodeNotFoundException extends Exception {
    public NodeNotFoundException() {
    }

    public NodeNotFoundException(String message) {
        super(message);
    }
}
