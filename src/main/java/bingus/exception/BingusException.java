package bingus.exception;

/**
 * Represents and invalid command or input for bingus.Bingus program
 */
public class BingusException extends RuntimeException {
    public BingusException(String message) {
        super(message);
    }
}
