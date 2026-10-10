package banking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction (deposit, withdrawal, or transfer).
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Enumeration of supported transaction types. */
    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    }

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    private String transactionId;
    private String accountNumber;
    private Type type;
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, String accountNumber,
                       Type type, double amount, double balanceAfter,
                       String description) {
        this.transactionId = transactionId;
        this.accountNumber  = accountNumber;
        this.type           = type;
        this.amount         = amount;
        this.balanceAfter   = balanceAfter;
        this.description    = description;
        this.timestamp      = LocalDateTime.now();
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getTransactionId()  { return transactionId; }
    public String getAccountNumber()  { return accountNumber; }
    public Type   getType()           { return type; }
    public double getAmount()         { return amount; }
    public double getBalanceAfter()   { return balanceAfter; }
    public String getDescription()    { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    /** Returns a formatted statement row for display in account statements. */
    public String toStatementRow() {
        String sign = (type == Type.DEPOSIT || type == Type.TRANSFER_IN) ? "+" : "-";
        return String.format("| %-24s | %-15s | %s%-12.2f | %-14.2f |",
            timestamp.format(FORMATTER),
            type.toString().replace("_", " "),
            sign,
            amount,
            balanceAfter
        );
    }

    @Override
    public String toString() {
        return String.format(
            "Transaction[id=%s, type=%s, amount=%.2f, balance=%.2f, time=%s]",
            transactionId, type, amount, balanceAfter, timestamp.format(FORMATTER)
        );
    }
}
