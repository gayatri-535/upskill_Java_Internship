package banking.service;

import banking.exception.AccountNotFoundException;
import banking.exception.AuthenticationException;
import banking.exception.InsufficientFundsException;
import banking.exception.InvalidTransactionException;
import banking.model.BankAccount;
import banking.model.BankAccount.AccountType;
import banking.model.Transaction;
import banking.model.User;
import banking.util.BankingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central service layer for the Banking Information System.
 *
 * <p>All business logic lives here. The service maintains in-memory maps for
 * users and accounts during the prototype session, and exposes clean, checked
 * methods for every operation the UI needs to perform.</p>
 */
public class BankingService {

    // ─── In-Memory Stores ───────────────────────────────────────────────────────

    /** Maps userId → User */
    private final Map<String, User> usersById = new HashMap<>();

    /** Maps email (lowercase) → userId – used for fast login look-ups */
    private final Map<String, String> emailIndex = new HashMap<>();

    /** Maps accountNumber → BankAccount */
    private final Map<String, BankAccount> accountsMap = new HashMap<>();

    // ─── Authentication ─────────────────────────────────────────────────────────

    /**
     * Authenticates a user by email and password.
     *
     * @param email    The user's registered email address.
     * @param password The plain-text password to verify.
     * @return The authenticated {@link User} object.
     * @throws AuthenticationException if the credentials are invalid.
     */
    public User login(String email, String password) throws AuthenticationException {
        String userId = emailIndex.get(email.toLowerCase().trim());
        if (userId == null) {
            throw new AuthenticationException("No account found for email: " + email);
        }
        User user = usersById.get(userId);
        if (!BankingUtils.verifyPassword(password, user.getPassword())) {
            throw new AuthenticationException("Incorrect password. Please try again.");
        }
        return user;
    }

    // ─── User Registration ──────────────────────────────────────────────────────

    /**
     * Registers a new user and opens an initial bank account with the
     * specified deposit amount.
     *
     * @param fullName       User's full name.
     * @param email          User's email address (used as login ID).
     * @param phone          10-digit mobile number.
     * @param address        Residential address.
     * @param password       Plain-text password (will be hashed).
     * @param dateOfBirthStr Date of birth as "YYYY-MM-DD".
     * @param accountType    Account type (SAVINGS or CURRENT).
     * @param initialDeposit Opening deposit amount (≥ 500).
     * @return The newly created {@link User} object.
     * @throws InvalidTransactionException if validation fails.
     */
    public User registerUser(String fullName, String email, String phone,
                             String address, String password,
                             java.time.LocalDate dateOfBirth,
                             AccountType accountType,
                             double initialDeposit)
            throws InvalidTransactionException {

        // ── Validation ──────────────────────────────────────────────────────────
        if (BankingUtils.isNullOrBlank(fullName))
            throw new InvalidTransactionException("Full name is required.");
        if (!BankingUtils.isValidEmail(email))
            throw new InvalidTransactionException("Invalid email address.");
        if (!BankingUtils.isValidPhone(phone))
            throw new InvalidTransactionException("Phone must be exactly 10 digits.");
        if (!BankingUtils.isStrongPassword(password))
            throw new InvalidTransactionException(
                "Password must be ≥ 8 chars, contain at least one digit and one special character.");
        if (emailIndex.containsKey(email.toLowerCase().trim()))
            throw new InvalidTransactionException("Email already registered: " + email);
        if (initialDeposit < 500)
            throw new InvalidTransactionException(
                "Minimum initial deposit is ₹500.");

        // ── Create User ─────────────────────────────────────────────────────────
        String userId = BankingUtils.generateUserId();
        String hashedPwd = BankingUtils.hashPassword(password);
        User user = new User(userId, fullName, email.toLowerCase().trim(),
                             phone, address, hashedPwd, dateOfBirth);

        // ── Create Initial Account ──────────────────────────────────────────────
        String accountNumber = BankingUtils.generateAccountNumber();
        BankAccount account  = new BankAccount(accountNumber, userId, accountType, 0);
        String txnId         = BankingUtils.generateTransactionId();
        account.deposit(initialDeposit, "Account opening deposit", txnId);

        // ── Persist in Memory ───────────────────────────────────────────────────
        user.addAccountNumber(accountNumber);
        usersById.put(userId, user);
        emailIndex.put(user.getEmail(), userId);
        accountsMap.put(accountNumber, account);

        return user;
    }

    // ─── Account Management ─────────────────────────────────────────────────────

    /**
     * Updates a user's personal information.
     *
     * @param userId     The user's ID.
     * @param newName    Updated full name (pass {@code null} to leave unchanged).
     * @param newPhone   Updated phone number (pass {@code null} to leave unchanged).
     * @param newAddress Updated address (pass {@code null} to leave unchanged).
     * @return The updated {@link User} object.
     * @throws AccountNotFoundException    if the userId is not found.
     * @throws InvalidTransactionException if validation fails.
     */
    public User updateUserInfo(String userId, String newName,
                               String newPhone, String newAddress)
            throws AccountNotFoundException, InvalidTransactionException {

        User user = getUserById(userId);

        if (!BankingUtils.isNullOrBlank(newName))    user.setFullName(newName.trim());
        if (!BankingUtils.isNullOrBlank(newPhone)) {
            if (!BankingUtils.isValidPhone(newPhone))
                throw new InvalidTransactionException("Phone must be exactly 10 digits.");
            user.setPhone(newPhone.trim());
        }
        if (!BankingUtils.isNullOrBlank(newAddress)) user.setAddress(newAddress.trim());

        return user;
    }

    /**
     * Changes a user's password after verifying the old one.
     *
     * @throws AccountNotFoundException    if the userId is not found.
     * @throws AuthenticationException     if the old password is incorrect.
     * @throws InvalidTransactionException if the new password is too weak.
     */
    public void changePassword(String userId, String oldPassword, String newPassword)
            throws AccountNotFoundException, AuthenticationException,
                   InvalidTransactionException {

        User user = getUserById(userId);

        if (!BankingUtils.verifyPassword(oldPassword, user.getPassword()))
            throw new AuthenticationException("Current password is incorrect.");
        if (!BankingUtils.isStrongPassword(newPassword))
            throw new InvalidTransactionException(
                "New password must be ≥ 8 chars, contain at least one digit and one special character.");

        user.setPassword(BankingUtils.hashPassword(newPassword));
    }

    /**
     * Opens an additional bank account for an existing user.
     *
     * @return The newly created {@link BankAccount}.
     * @throws AccountNotFoundException    if the userId is not found.
     * @throws InvalidTransactionException if validation fails.
     */
    public BankAccount openAdditionalAccount(String userId, AccountType accountType,
                                             double initialDeposit)
            throws AccountNotFoundException, InvalidTransactionException {

        User user = getUserById(userId);

        if (initialDeposit < 500)
            throw new InvalidTransactionException("Minimum initial deposit is ₹500.");

        String accountNumber = BankingUtils.generateAccountNumber();
        BankAccount account  = new BankAccount(accountNumber, userId, accountType, 0);
        String txnId         = BankingUtils.generateTransactionId();
        account.deposit(initialDeposit, "Account opening deposit", txnId);

        user.addAccountNumber(accountNumber);
        accountsMap.put(accountNumber, account);

        return account;
    }

    // ─── Deposit ────────────────────────────────────────────────────────────────

    /**
     * Deposits the given amount into the specified account.
     *
     * @return The updated account balance after the deposit.
     * @throws AccountNotFoundException    if the account does not exist.
     * @throws InvalidTransactionException if the amount is invalid or account inactive.
     */
    public double deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidTransactionException {

        BankAccount account = getActiveAccount(accountNumber);
        if (!BankingUtils.isValidAmount(amount))
            throw new InvalidTransactionException("Deposit amount must be a positive number.");

        String txnId = BankingUtils.generateTransactionId();
        account.deposit(amount, "Cash deposit", txnId);
        return account.getBalance();
    }

    // ─── Withdrawal ─────────────────────────────────────────────────────────────

    /**
     * Withdraws the given amount from the specified account.
     *
     * @return The updated account balance after the withdrawal.
     * @throws AccountNotFoundException    if the account does not exist.
     * @throws InsufficientFundsException  if the balance is too low.
     * @throws InvalidTransactionException if the amount is invalid or account inactive.
     */
    public double withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException,
                   InvalidTransactionException {

        BankAccount account = getActiveAccount(accountNumber);
        if (!BankingUtils.isValidAmount(amount))
            throw new InvalidTransactionException("Withdrawal amount must be a positive number.");
        if (amount > account.getBalance())
            throw new InsufficientFundsException(account.getBalance(), amount);

        String txnId = BankingUtils.generateTransactionId();
        account.withdraw(amount, "Cash withdrawal", txnId);
        return account.getBalance();
    }

    // ─── Fund Transfer ──────────────────────────────────────────────────────────

    /**
     * Transfers funds from one account to another.
     *
     * @param fromAccountNumber Source account number.
     * @param toAccountNumber   Destination account number.
     * @param amount            Amount to transfer.
     * @throws AccountNotFoundException    if either account does not exist.
     * @throws InsufficientFundsException  if the source account has insufficient funds.
     * @throws InvalidTransactionException if the transfer is self-referential or amount invalid.
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException,
                   InvalidTransactionException {

        if (fromAccountNumber.equals(toAccountNumber))
            throw new InvalidTransactionException("Cannot transfer to the same account.");
        if (!BankingUtils.isValidAmount(amount))
            throw new InvalidTransactionException("Transfer amount must be a positive number.");

        BankAccount source      = getActiveAccount(fromAccountNumber);
        BankAccount destination = getActiveAccount(toAccountNumber);

        if (amount > source.getBalance())
            throw new InsufficientFundsException(source.getBalance(), amount);

        String txnId = BankingUtils.generateTransactionId();
        source.sendTransfer(amount, toAccountNumber, txnId);
        destination.receiveTransfer(amount, fromAccountNumber, txnId);
    }

    // ─── Account Statement ──────────────────────────────────────────────────────

    /**
     * Returns the list of all transactions for the given account,
     * ordered from newest to oldest.
     *
     * @throws AccountNotFoundException if the account does not exist.
     */
    public List<Transaction> getAccountStatement(String accountNumber)
            throws AccountNotFoundException {

        BankAccount account = getAccountByNumber(accountNumber);
        List<Transaction> sorted = new ArrayList<>(account.getTransactions());
        // Reverse-chronological order (latest first)
        sorted.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return sorted;
    }

    // ─── Queries ─────────────────────────────────────────────────────────────────

    /**
     * Returns the {@link User} with the given userId.
     *
     * @throws AccountNotFoundException if no such user exists.
     */
    public User getUserById(String userId) throws AccountNotFoundException {
        User user = usersById.get(userId);
        if (user == null)
            throw new AccountNotFoundException("User not found: " + userId);
        return user;
    }

    /**
     * Returns the {@link BankAccount} for the given account number.
     *
     * @throws AccountNotFoundException if the account does not exist.
     */
    public BankAccount getAccountByNumber(String accountNumber)
            throws AccountNotFoundException {
        BankAccount account = accountsMap.get(accountNumber);
        if (account == null)
            throw new AccountNotFoundException(accountNumber);
        return account;
    }

    /**
     * Returns all {@link BankAccount} objects belonging to the given user.
     *
     * @throws AccountNotFoundException if the user does not exist.
     */
    public List<BankAccount> getAccountsForUser(String userId)
            throws AccountNotFoundException {
        User user = getUserById(userId);
        List<BankAccount> accounts = new ArrayList<>();
        for (String accNum : user.getAccountNumbers()) {
            BankAccount acc = accountsMap.get(accNum);
            if (acc != null) accounts.add(acc);
        }
        return accounts;
    }

    /** Returns the total number of registered users in the session. */
    public int getTotalUsers() { return usersById.size(); }

    /** Returns the total number of accounts in the session. */
    public int getTotalAccounts() { return accountsMap.size(); }

    // ─── Private Helpers ────────────────────────────────────────────────────────

    /**
     * Retrieves an active account, throwing if it is not found or is inactive.
     */
    private BankAccount getActiveAccount(String accountNumber)
            throws AccountNotFoundException, InvalidTransactionException {
        BankAccount account = getAccountByNumber(accountNumber);
        if (!account.isActive())
            throw new InvalidTransactionException(
                "Account " + accountNumber + " is not active.");
        return account;
    }
}
