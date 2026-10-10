package banking.exception;

/**
 * Thrown when an operation references an account number that does not exist.
 */
public class AccountNotFoundException extends Exception {

    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() { return accountNumber; }
}
