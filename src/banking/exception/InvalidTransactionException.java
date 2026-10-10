package banking.exception;

/**
 * Thrown when a transaction is deemed invalid (e.g., negative amount,
 * duplicate transaction ID, or operation on an inactive account).
 */
public class InvalidTransactionException extends Exception {

    public InvalidTransactionException(String message) {
        super(message);
    }
}
