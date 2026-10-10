package banking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a bank account belonging to a registered user.
 * Tracks balance and transaction history for the account.
 */
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Enumeration of account types supported by the system. */
    public enum AccountType {
        SAVINGS, CURRENT
    }

    private String accountNumber;
    private String userId;              // Owner's user ID
    private AccountType accountType;
    private double balance;
    private List<Transaction> transactions;
    private boolean active;

    public BankAccount(String accountNumber, String userId,
                       AccountType accountType, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.userId        = userId;
        this.accountType   = accountType;
        this.balance       = initialDeposit;
        this.transactions  = new ArrayList<>();
        this.active        = true;
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getAccountNumber()        { return accountNumber; }
    public String getUserId()               { return userId; }
    public AccountType getAccountType()     { return accountType; }
    public double getBalance()              { return balance; }
    public boolean isActive()               { return active; }

    /** Returns an unmodifiable view of the transaction list. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    // ─── Mutators ───────────────────────────────────────────────────────────────

    public void setActive(boolean active) { this.active = active; }

    /**
     * Credits the account by the given amount and records a DEPOSIT transaction.
     *
     * @param amount      Amount to deposit (must be positive).
     * @param description Narrative for the transaction record.
     * @param txnId       Unique transaction identifier.
     */
    public void deposit(double amount, String description, String txnId) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
        transactions.add(new Transaction(txnId, accountNumber,
            Transaction.Type.DEPOSIT, amount, balance, description));
    }

    /**
     * Debits the account by the given amount and records a WITHDRAWAL transaction.
     *
     * @param amount      Amount to withdraw (must be positive and ≤ balance).
     * @param description Narrative for the transaction record.
     * @param txnId       Unique transaction identifier.
     * @throws IllegalArgumentException if the account has insufficient funds.
     */
    public void withdraw(double amount, String description, String txnId) {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > balance)
            throw new IllegalArgumentException(
                String.format("Insufficient funds. Available balance: %.2f", balance));
        balance -= amount;
        transactions.add(new Transaction(txnId, accountNumber,
            Transaction.Type.WITHDRAWAL, amount, balance, description));
    }

    /**
     * Credits the account as a result of an incoming transfer.
     */
    public void receiveTransfer(double amount, String fromAccount,
                                String txnId) {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive.");
        balance += amount;
        transactions.add(new Transaction(txnId, accountNumber,
            Transaction.Type.TRANSFER_IN, amount, balance,
            "Transfer received from " + fromAccount));
    }

    /**
     * Debits the account as a result of an outgoing transfer.
     */
    public void sendTransfer(double amount, String toAccount,
                             String txnId) {
        if (amount <= 0)
            throw new IllegalArgumentException("Transfer amount must be positive.");
        if (amount > balance)
            throw new IllegalArgumentException(
                String.format("Insufficient funds. Available balance: %.2f", balance));
        balance -= amount;
        transactions.add(new Transaction(txnId, accountNumber,
            Transaction.Type.TRANSFER_OUT, amount, balance,
            "Transfer sent to " + toAccount));
    }

    @Override
    public String toString() {
        return String.format(
            "Account[number=%s, type=%s, balance=%.2f, active=%b]",
            accountNumber, accountType, balance, active
        );
    }
}
