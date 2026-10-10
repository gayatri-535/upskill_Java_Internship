package banking.exception;

/**
 * Thrown when a user attempts an operation that exceeds the available account balance.
 */
public class InsufficientFundsException extends Exception {

    private final double availableBalance;
    private final double requestedAmount;

    public InsufficientFundsException(double availableBalance, double requestedAmount) {
        super(String.format(
            "Insufficient funds. Requested: %.2f | Available: %.2f",
            requestedAmount, availableBalance));
        this.availableBalance = availableBalance;
        this.requestedAmount  = requestedAmount;
    }

    public double getAvailableBalance() { return availableBalance; }
    public double getRequestedAmount()  { return requestedAmount; }
}
