package banking.exception;

/**
 * Thrown when authentication fails due to incorrect credentials.
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}
