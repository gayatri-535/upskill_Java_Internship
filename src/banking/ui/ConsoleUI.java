package banking.ui;

import banking.model.BankAccount;
import banking.model.Transaction;
import banking.model.User;
import banking.util.BankingUtils;

import java.util.List;

/**
 * Centralised display helper for the Banking Information System Console UI.
 *
 * <p>All banner, table, and formatted-message printing is handled here so
 * that the menu classes stay focused on control flow only.</p>
 */
public final class ConsoleUI {

    // ─── ANSI Colour Codes ───────────────────────────────────────────────────────
    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String GREEN   = "\u001B[32m";
    public static final String CYAN    = "\u001B[36m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String RED     = "\u001B[31m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLUE    = "\u001B[34m";
    public static final String WHITE   = "\u001B[37m";
    public static final String BG_BLUE = "\u001B[44m";

    private ConsoleUI() {}

    // ─── App Banner ──────────────────────────────────────────────────────────────

    /** Prints the application welcome banner. */
    public static void printBanner() {
        System.out.println();
        System.out.println(CYAN + BOLD + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "║                                                              ║" + RESET);
        System.out.println(CYAN + BOLD + "║         🏦  BANKING INFORMATION SYSTEM (BIS)  🏦            ║" + RESET);
        System.out.println(CYAN + BOLD + "║                  Core Java Prototype v1.0                    ║" + RESET);
        System.out.println(CYAN + BOLD + "║                                                              ║" + RESET);
        System.out.println(CYAN + BOLD + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    // ─── Section Headers ─────────────────────────────────────────────────────────

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(BLUE + BOLD + "┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.printf (BLUE + BOLD + "│  %-59s│%n" + RESET, title);
        System.out.println(BLUE + BOLD + "└─────────────────────────────────────────────────────────────┘" + RESET);
        System.out.println();
    }

    public static void printSubHeader(String subtitle) {
        System.out.println(YELLOW + BOLD + "  ── " + subtitle + " ──" + RESET);
    }

    // ─── Status Messages ─────────────────────────────────────────────────────────

    public static void printSuccess(String message) {
        System.out.println(GREEN + BOLD + "  ✔  " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + BOLD + "  ✖  ERROR: " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "  ⚠  " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(CYAN + "  ℹ  " + message + RESET);
    }

    public static void printDivider() {
        System.out.println(WHITE + "  " + "─".repeat(62) + RESET);
    }

    // ─── Menu Rendering ───────────────────────────────────────────────────────────

    /**
     * Prints a styled menu from a title and a list of option strings.
     *
     * @param title   The menu heading.
     * @param options The ordered list of menu items (index starts from 1).
     */
    public static void printMenu(String title, String... options) {
        System.out.println();
        System.out.println(MAGENTA + BOLD + "  ╔══════════════════════════════════════╗" + RESET);
        System.out.printf (MAGENTA + BOLD + "  ║  %-36s║%n" + RESET, title);
        System.out.println(MAGENTA + BOLD + "  ╠══════════════════════════════════════╣" + RESET);
        for (int i = 0; i < options.length; i++) {
            System.out.printf(MAGENTA + "  ║  " + RESET
                + BOLD + CYAN + "[" + (i + 1) + "] " + RESET
                + WHITE + "%-33s" + RESET
                + MAGENTA + "║%n" + RESET, options[i]);
        }
        System.out.println(MAGENTA + BOLD + "  ╚══════════════════════════════════════╝" + RESET);
        System.out.print(YELLOW + "\n  Enter your choice: " + RESET);
    }

    // ─── User Info Display ────────────────────────────────────────────────────────

    /** Prints a formatted card showing the user's profile details. */
    public static void printUserProfile(User user) {
        printHeader("👤  User Profile");
        System.out.println(CYAN + "  Name     : " + RESET + BOLD + user.getFullName() + RESET);
        System.out.println(CYAN + "  Email    : " + RESET + user.getEmail());
        System.out.println(CYAN + "  Phone    : " + RESET + user.getPhone());
        System.out.println(CYAN + "  Address  : " + RESET + user.getAddress());
        System.out.println(CYAN + "  DOB      : " + RESET + user.getDateOfBirth());
        System.out.println(CYAN + "  Reg. Date: " + RESET + user.getRegistrationDate());
        System.out.println(CYAN + "  Accounts : " + RESET + user.getAccountNumbers().size());
    }

    // ─── Account Info Display ─────────────────────────────────────────────────────

    /** Prints a summary card for a single bank account. */
    public static void printAccountSummary(BankAccount account) {
        System.out.println();
        System.out.println(GREEN + "  ┌─────────────────────────────────────────┐" + RESET);
        System.out.printf (GREEN + "  │  Account No : %-28s│%n" + RESET, account.getAccountNumber());
        System.out.printf (GREEN + "  │  Type       : %-28s│%n" + RESET, account.getAccountType());
        System.out.printf (GREEN + "  │  Balance    : %-28s│%n" + RESET, BankingUtils.formatCurrency(account.getBalance()));
        System.out.printf (GREEN + "  │  Status     : %-28s│%n" + RESET, account.isActive() ? "ACTIVE" : "INACTIVE");
        System.out.println(GREEN + "  └─────────────────────────────────────────┘" + RESET);
    }

    /** Prints summary cards for all accounts belonging to a user. */
    public static void printAllAccounts(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            printWarning("No accounts found.");
            return;
        }
        printHeader("📋  My Accounts");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println(YELLOW + "  Account " + (i + 1) + RESET);
            printAccountSummary(accounts.get(i));
        }
    }

    // ─── Transaction Statement ────────────────────────────────────────────────────

    /** Prints a full transaction statement table for an account. */
    public static void printStatement(String accountNumber, List<Transaction> transactions) {
        printHeader("📄  Account Statement — " + BankingUtils.maskAccountNumber(accountNumber));

        if (transactions == null || transactions.isEmpty()) {
            printWarning("No transactions found for this account.");
            return;
        }

        String header = String.format("  | %-24s | %-15s | %-13s | %-14s |",
            "Date & Time", "Type", "Amount (₹)", "Balance (₹)");
        String border = "  +" + "─".repeat(26) + "+" + "─".repeat(17) + "+" + "─".repeat(15) + "+" + "─".repeat(16) + "+";

        System.out.println(CYAN + BOLD + border + RESET);
        System.out.println(CYAN + BOLD + header + RESET);
        System.out.println(CYAN + BOLD + border + RESET);

        for (Transaction t : transactions) {
            boolean isCredit = (t.getType() == Transaction.Type.DEPOSIT
                             || t.getType() == Transaction.Type.TRANSFER_IN);
            String colour = isCredit ? GREEN : RED;
            System.out.println(colour + "  " + t.toStatementRow() + RESET);
        }

        System.out.println(CYAN + border + RESET);
        System.out.println();
        printInfo("Total transactions shown: " + transactions.size());
    }

    // ─── Transaction Confirmation Receipt ────────────────────────────────────────

    /** Prints a formatted receipt after a successful deposit. */
    public static void printDepositReceipt(String accountNumber, double amount,
                                           double newBalance) {
        System.out.println();
        System.out.println(GREEN + BOLD + "  ┌──────────── DEPOSIT RECEIPT ─────────────┐" + RESET);
        System.out.printf (GREEN + "  │  Account : %-31s│%n" + RESET, accountNumber);
        System.out.printf (GREEN + "  │  Deposited: %-30s│%n" + RESET, BankingUtils.formatCurrency(amount));
        System.out.printf (GREEN + "  │  New Balance: %-28s│%n" + RESET, BankingUtils.formatCurrency(newBalance));
        System.out.println(GREEN + BOLD + "  └──────────────────────────────────────────┘" + RESET);
    }

    /** Prints a formatted receipt after a successful withdrawal. */
    public static void printWithdrawalReceipt(String accountNumber, double amount,
                                              double newBalance) {
        System.out.println();
        System.out.println(YELLOW + BOLD + "  ┌─────────── WITHDRAWAL RECEIPT ───────────┐" + RESET);
        System.out.printf (YELLOW + "  │  Account   : %-29s│%n" + RESET, accountNumber);
        System.out.printf (YELLOW + "  │  Withdrawn : %-29s│%n" + RESET, BankingUtils.formatCurrency(amount));
        System.out.printf (YELLOW + "  │  New Balance: %-28s│%n" + RESET, BankingUtils.formatCurrency(newBalance));
        System.out.println(YELLOW + BOLD + "  └──────────────────────────────────────────┘" + RESET);
    }

    /** Prints a formatted receipt after a successful fund transfer. */
    public static void printTransferReceipt(String fromAccount, String toAccount,
                                            double amount, double senderBalance) {
        System.out.println();
        System.out.println(CYAN + BOLD + "  ┌─────────── TRANSFER RECEIPT ─────────────┐" + RESET);
        System.out.printf (CYAN + "  │  From     : %-30s│%n" + RESET, fromAccount);
        System.out.printf (CYAN + "  │  To       : %-30s│%n" + RESET, toAccount);
        System.out.printf (CYAN + "  │  Amount   : %-30s│%n" + RESET, BankingUtils.formatCurrency(amount));
        System.out.printf (CYAN + "  │  Your Balance: %-27s│%n" + RESET, BankingUtils.formatCurrency(senderBalance));
        System.out.println(CYAN + BOLD + "  └──────────────────────────────────────────┘" + RESET);
    }

    // ─── Registration Confirmation ────────────────────────────────────────────────

    /** Prints the account creation confirmation with the new account number. */
    public static void printRegistrationConfirmation(User user, String accountNumber,
                                                     double initialDeposit) {
        System.out.println();
        System.out.println(GREEN + BOLD + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(GREEN + BOLD + "║           🎉  REGISTRATION SUCCESSFUL!  🎉                  ║" + RESET);
        System.out.println(GREEN + BOLD + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        System.out.printf (GREEN + "║  Customer Name : %-43s║%n" + RESET, user.getFullName());
        System.out.printf (GREEN + "║  User ID       : %-43s║%n" + RESET, user.getUserId());
        System.out.printf (GREEN + "║  Account No    : %-43s║%n" + RESET, accountNumber);
        System.out.printf (GREEN + "║  Account Type  : %-43s║%n" + RESET,
            user.getAccountNumbers().isEmpty() ? "SAVINGS" : "See account details");
        System.out.printf (GREEN + "║  Initial Deposit: %-42s║%n" + RESET, BankingUtils.formatCurrency(initialDeposit));
        System.out.println(GREEN + BOLD + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println(YELLOW + "  Please save your account number and User ID for future reference." + RESET);
        System.out.println();
    }

    // ─── Prompt Helpers ───────────────────────────────────────────────────────────

    public static void prompt(String message) {
        System.out.print(YELLOW + "  " + message + ": " + RESET);
    }

    public static void pressEnterToContinue(java.util.Scanner scanner) {
        System.out.println();
        System.out.print(WHITE + "  [ Press ENTER to continue... ]" + RESET);
        scanner.nextLine();
    }

    public static void printGoodbye() {
        System.out.println();
        System.out.println(CYAN + BOLD + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "║          Thank you for using the Banking System!             ║" + RESET);
        System.out.println(CYAN + BOLD + "║                    Goodbye! Have a safe day.                 ║" + RESET);
        System.out.println(CYAN + BOLD + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }
}
