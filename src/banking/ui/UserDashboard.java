package banking.ui;

import banking.exception.AccountNotFoundException;
import banking.exception.AuthenticationException;
import banking.exception.InsufficientFundsException;
import banking.exception.InvalidTransactionException;
import banking.model.BankAccount;
import banking.model.BankAccount.AccountType;
import banking.model.Transaction;
import banking.model.User;
import banking.service.BankingService;
import banking.util.BankingUtils;

import java.util.List;
import java.util.Scanner;

/**
 * Handles the authenticated user dashboard and all banking operations.
 *
 * <p>This menu is shown only after a successful login. It provides access to
 * account management, deposits, withdrawals, fund transfers, account statements,
 * profile updates, and password changes.</p>
 */
public class UserDashboard {

    private final BankingService service;
    private final Scanner        scanner;
    private       User           currentUser;

    public UserDashboard(BankingService service, Scanner scanner, User user) {
        this.service     = service;
        this.scanner     = scanner;
        this.currentUser = user;
    }

    /**
     * Runs the main dashboard loop until the user chooses to log out.
     */
    public void run() {
        boolean running = true;
        while (running) {
            ConsoleUI.printHeader("🏦  Dashboard  ─  Welcome, " + currentUser.getFullName());
            ConsoleUI.printMenu("  MAIN DASHBOARD",
                "Account Overview",
                "Deposit Funds",
                "Withdraw Funds",
                "Fund Transfer",
                "Account Statement",
                "Manage Profile",
                "Open New Account",
                "Logout"
            );

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showAccountOverview();
                case "2" -> handleDeposit();
                case "3" -> handleWithdrawal();
                case "4" -> handleTransfer();
                case "5" -> handleStatement();
                case "6" -> handleProfileManagement();
                case "7" -> handleOpenNewAccount();
                case "8" -> running = false;
                default  -> ConsoleUI.printError("Invalid choice. Please select 1–8.");
            }
        }
        ConsoleUI.printInfo("You have been logged out successfully.");
        System.out.println();
    }

    // ─── 1. Account Overview ─────────────────────────────────────────────────────

    private void showAccountOverview() {
        try {
            List<BankAccount> accounts = service.getAccountsForUser(currentUser.getUserId());
            ConsoleUI.printAllAccounts(accounts);

            // Also show total balance
            double total = accounts.stream().mapToDouble(BankAccount::getBalance).sum();
            System.out.println(ConsoleUI.GREEN + ConsoleUI.BOLD
                + "  Total Portfolio Balance: " + BankingUtils.formatCurrency(total)
                + ConsoleUI.RESET);
            System.out.println();
        } catch (AccountNotFoundException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── 2. Deposit ──────────────────────────────────────────────────────────────

    private void handleDeposit() {
        ConsoleUI.printHeader("💰  Deposit Funds");
        String accountNumber = promptAndSelectAccount("Select account to deposit into");
        if (accountNumber == null) return;

        ConsoleUI.prompt("Enter Deposit Amount (₹)");
        double amount = readDouble();
        if (amount < 0) return;

        try {
            double newBalance = service.deposit(accountNumber, amount);
            ConsoleUI.printDepositReceipt(accountNumber, amount, newBalance);
        } catch (AccountNotFoundException | InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── 3. Withdrawal ───────────────────────────────────────────────────────────

    private void handleWithdrawal() {
        ConsoleUI.printHeader("💸  Withdraw Funds");
        String accountNumber = promptAndSelectAccount("Select account to withdraw from");
        if (accountNumber == null) return;

        ConsoleUI.prompt("Enter Withdrawal Amount (₹)");
        double amount = readDouble();
        if (amount < 0) return;

        try {
            double newBalance = service.withdraw(accountNumber, amount);
            ConsoleUI.printWithdrawalReceipt(accountNumber, amount, newBalance);
        } catch (AccountNotFoundException | InsufficientFundsException
                 | InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── 4. Fund Transfer ────────────────────────────────────────────────────────

    private void handleTransfer() {
        ConsoleUI.printHeader("🔄  Fund Transfer");

        String fromAccount = promptAndSelectAccount("Select source account");
        if (fromAccount == null) return;

        System.out.println();
        ConsoleUI.printSubHeader("Destination Account");
        ConsoleUI.prompt("Enter recipient's Account Number");
        String toAccount = scanner.nextLine().trim();

        ConsoleUI.prompt("Enter Transfer Amount (₹)");
        double amount = readDouble();
        if (amount < 0) return;

        // Show preview and ask for confirmation
        System.out.println();
        ConsoleUI.printInfo("Transfer Preview:");
        System.out.println(ConsoleUI.WHITE + "  From    : " + fromAccount + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE + "  To      : " + toAccount + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE + "  Amount  : " + BankingUtils.formatCurrency(amount) + ConsoleUI.RESET);
        System.out.println();
        ConsoleUI.prompt("Confirm transfer? (yes/no)");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            ConsoleUI.printWarning("Transfer cancelled by user.");
            ConsoleUI.pressEnterToContinue(scanner);
            return;
        }

        try {
            service.transfer(fromAccount, toAccount, amount);
            BankAccount source = service.getAccountByNumber(fromAccount);
            ConsoleUI.printTransferReceipt(fromAccount, toAccount, amount,
                                           source.getBalance());
        } catch (AccountNotFoundException | InsufficientFundsException
                 | InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── 5. Account Statement ────────────────────────────────────────────────────

    private void handleStatement() {
        ConsoleUI.printHeader("📄  Account Statement");
        String accountNumber = promptAndSelectAccount("Select account to view statement");
        if (accountNumber == null) return;

        try {
            List<Transaction> transactions = service.getAccountStatement(accountNumber);
            ConsoleUI.printStatement(accountNumber, transactions);
        } catch (AccountNotFoundException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── 6. Profile Management ───────────────────────────────────────────────────

    private void handleProfileManagement() {
        while (true) {
            ConsoleUI.printUserProfile(currentUser);
            ConsoleUI.printMenu("  PROFILE MANAGEMENT",
                "Update Personal Information",
                "Change Password",
                "Back to Dashboard"
            );

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleUpdatePersonalInfo();
                case "2" -> handleChangePassword();
                case "3" -> { return; }
                default  -> ConsoleUI.printError("Invalid choice. Please select 1–3.");
            }
        }
    }

    private void handleUpdatePersonalInfo() {
        ConsoleUI.printHeader("✏️  Update Personal Information");
        ConsoleUI.printInfo("Press ENTER to keep the current value unchanged.");
        System.out.println();

        ConsoleUI.prompt("New Full Name [current: " + currentUser.getFullName() + "]");
        String newName = scanner.nextLine().trim();

        ConsoleUI.prompt("New Phone Number [current: " + currentUser.getPhone() + "]");
        String newPhone = scanner.nextLine().trim();

        ConsoleUI.prompt("New Address [current: " + currentUser.getAddress() + "]");
        String newAddress = scanner.nextLine().trim();

        try {
            currentUser = service.updateUserInfo(currentUser.getUserId(),
                newName.isEmpty()    ? null : newName,
                newPhone.isEmpty()   ? null : newPhone,
                newAddress.isEmpty() ? null : newAddress
            );
            ConsoleUI.printSuccess("Profile updated successfully!");
        } catch (AccountNotFoundException | InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    private void handleChangePassword() {
        ConsoleUI.printHeader("🔑  Change Password");

        ConsoleUI.prompt("Current Password");
        String oldPassword = scanner.nextLine().trim();

        ConsoleUI.prompt("New Password");
        String newPassword = scanner.nextLine().trim();

        ConsoleUI.prompt("Confirm New Password");
        String confirmNew = scanner.nextLine().trim();

        if (!newPassword.equals(confirmNew)) {
            ConsoleUI.printError("New passwords do not match.");
            ConsoleUI.pressEnterToContinue(scanner);
            return;
        }

        try {
            service.changePassword(currentUser.getUserId(), oldPassword, newPassword);
            ConsoleUI.printSuccess("Password changed successfully. Please use your new password at next login.");
        } catch (AccountNotFoundException | AuthenticationException
                 | InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── 7. Open New Account ─────────────────────────────────────────────────────

    private void handleOpenNewAccount() {
        ConsoleUI.printHeader("➕  Open New Bank Account");

        ConsoleUI.printSubHeader("Account Type");
        System.out.println(ConsoleUI.WHITE + "  [1] Savings Account" + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE + "  [2] Current Account" + ConsoleUI.RESET);
        ConsoleUI.prompt("Select Account Type");
        String typeChoice = scanner.nextLine().trim();

        AccountType accountType = switch (typeChoice) {
            case "1" -> AccountType.SAVINGS;
            case "2" -> AccountType.CURRENT;
            default  -> null;
        };

        if (accountType == null) {
            ConsoleUI.printError("Invalid account type.");
            ConsoleUI.pressEnterToContinue(scanner);
            return;
        }

        ConsoleUI.prompt("Initial Deposit Amount (minimum ₹500)");
        double initialDeposit = readDouble();
        if (initialDeposit < 0) return;

        try {
            BankAccount newAccount = service.openAdditionalAccount(
                currentUser.getUserId(), accountType, initialDeposit
            );
            // Refresh the user object to get updated account list
            currentUser = service.getUserById(currentUser.getUserId());

            ConsoleUI.printSuccess("New account opened successfully!");
            ConsoleUI.printAccountSummary(newAccount);
        } catch (AccountNotFoundException | InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
        }
        ConsoleUI.pressEnterToContinue(scanner);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    /**
     * Lists the user's accounts and lets them pick one by number.
     *
     * @param prompt Descriptive prompt shown above the account list.
     * @return The selected account number, or {@code null} if cancelled.
     */
    private String promptAndSelectAccount(String prompt) {
        try {
            List<BankAccount> accounts = service.getAccountsForUser(currentUser.getUserId());
            if (accounts.isEmpty()) {
                ConsoleUI.printWarning("You have no accounts. Please register first.");
                ConsoleUI.pressEnterToContinue(scanner);
                return null;
            }

            System.out.println();
            ConsoleUI.printSubHeader(prompt);
            for (int i = 0; i < accounts.size(); i++) {
                BankAccount acc = accounts.get(i);
                System.out.printf(ConsoleUI.WHITE + "  [%d] %s  (%s)  Balance: %s%n" + ConsoleUI.RESET,
                    i + 1, acc.getAccountNumber(), acc.getAccountType(),
                    BankingUtils.formatCurrency(acc.getBalance())
                );
            }
            System.out.println(ConsoleUI.WHITE + "  [0] Cancel" + ConsoleUI.RESET);
            ConsoleUI.prompt("Enter selection");
            String sel = scanner.nextLine().trim();

            if (sel.equals("0")) {
                ConsoleUI.printWarning("Operation cancelled.");
                ConsoleUI.pressEnterToContinue(scanner);
                return null;
            }

            int idx;
            try { idx = Integer.parseInt(sel) - 1; }
            catch (NumberFormatException e) { idx = -1; }

            if (idx < 0 || idx >= accounts.size()) {
                ConsoleUI.printError("Invalid selection.");
                ConsoleUI.pressEnterToContinue(scanner);
                return null;
            }
            return accounts.get(idx).getAccountNumber();

        } catch (AccountNotFoundException e) {
            ConsoleUI.printError(e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
            return null;
        }
    }

    /** Reads a double from the scanner; returns -1 on parse failure. */
    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Please enter a valid numeric amount.");
            ConsoleUI.pressEnterToContinue(scanner);
            return -1;
        }
    }
}
