package banking.ui;

import banking.exception.AccountNotFoundException;
import banking.exception.InvalidTransactionException;
import banking.model.BankAccount;
import banking.model.BankAccount.AccountType;
import banking.model.User;
import banking.service.BankingService;
import banking.util.BankingUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Handles the Guest menu (pre-login): user registration and login navigation.
 */
public class GuestMenu {

    private final BankingService service;
    private final Scanner scanner;

    public GuestMenu(BankingService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    /**
     * Displays the guest menu and returns the logged-in {@link User} after
     * successful authentication, or {@code null} to signal application exit.
     *
     * @return Authenticated {@link User}, or {@code null} if the user chose to exit.
     */
    public User show() {
        while (true) {
            ConsoleUI.printBanner();
            ConsoleUI.printMenu("  WELCOME — MAIN MENU",
                "Login to My Account",
                "Register New Account",
                "Exit Application"
            );

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    User user = handleLogin();
                    if (user != null) return user;
                }
                case "2" -> handleRegistration();
                case "3" -> {
                    ConsoleUI.printGoodbye();
                    return null;
                }
                default  -> ConsoleUI.printError("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    // ─── Login ───────────────────────────────────────────────────────────────────

    private User handleLogin() {
        ConsoleUI.printHeader("🔐  User Login");

        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            ConsoleUI.prompt("Email");
            String email = scanner.nextLine().trim();

            ConsoleUI.prompt("Password");
            String password = scanner.nextLine().trim();

            try {
                User user = service.login(email, password);
                ConsoleUI.printSuccess("Welcome back, " + user.getFullName() + "!");
                System.out.println();
                return user;
            } catch (banking.exception.AuthenticationException e) {
                attempts++;
                ConsoleUI.printError(e.getMessage());
                int remaining = MAX_ATTEMPTS - attempts;
                if (remaining > 0) {
                    ConsoleUI.printWarning(remaining + " attempt(s) remaining.");
                } else {
                    ConsoleUI.printError("Too many failed attempts. Returning to main menu.");
                }
            }
        }
        ConsoleUI.pressEnterToContinue(scanner);
        return null;
    }

    // ─── Registration ─────────────────────────────────────────────────────────────

    private void handleRegistration() {
        ConsoleUI.printHeader("📝  New User Registration");
        ConsoleUI.printInfo("Please fill in the following details to create your account.");
        System.out.println();

        try {
            // ── Personal Details ────────────────────────────────────────────────
            ConsoleUI.printSubHeader("Personal Information");

            ConsoleUI.prompt("Full Name");
            String fullName = scanner.nextLine().trim();

            ConsoleUI.prompt("Date of Birth (YYYY-MM-DD)");
            String dobStr = scanner.nextLine().trim();
            LocalDate dob;
            try {
                dob = LocalDate.parse(dobStr);
            } catch (DateTimeParseException e) {
                ConsoleUI.printError("Invalid date format. Use YYYY-MM-DD (e.g. 1995-06-15).");
                ConsoleUI.pressEnterToContinue(scanner);
                return;
            }

            ConsoleUI.prompt("Email Address");
            String email = scanner.nextLine().trim();

            ConsoleUI.prompt("Mobile Number (10 digits)");
            String phone = scanner.nextLine().trim();

            ConsoleUI.prompt("Residential Address");
            String address = scanner.nextLine().trim();

            // ── Security ────────────────────────────────────────────────────────
            System.out.println();
            ConsoleUI.printSubHeader("Security Setup");
            ConsoleUI.printInfo("Password must be ≥ 8 characters with at least 1 digit and 1 special character.");

            ConsoleUI.prompt("Create Password");
            String password = scanner.nextLine().trim();

            ConsoleUI.prompt("Confirm Password");
            String confirmPassword = scanner.nextLine().trim();

            if (!password.equals(confirmPassword)) {
                ConsoleUI.printError("Passwords do not match.");
                ConsoleUI.pressEnterToContinue(scanner);
                return;
            }

            // ── Account Setup ───────────────────────────────────────────────────
            System.out.println();
            ConsoleUI.printSubHeader("Account Setup");

            AccountType accountType = promptAccountType();
            if (accountType == null) { ConsoleUI.pressEnterToContinue(scanner); return; }

            ConsoleUI.prompt("Initial Deposit Amount (minimum ₹500)");
            double initialDeposit;
            try {
                initialDeposit = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Please enter a valid numeric amount.");
                ConsoleUI.pressEnterToContinue(scanner);
                return;
            }

            // ── Create User ─────────────────────────────────────────────────────
            User user = service.registerUser(
                fullName, email, phone, address, password, dob, accountType, initialDeposit
            );

            // The first account number is the one just created
            String accountNumber = user.getAccountNumbers().get(0);
            ConsoleUI.printRegistrationConfirmation(user, accountNumber, initialDeposit);

        } catch (InvalidTransactionException e) {
            ConsoleUI.printError(e.getMessage());
            ConsoleUI.pressEnterToContinue(scanner);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────────

    private AccountType promptAccountType() {
        System.out.println();
        System.out.println(ConsoleUI.CYAN + "  Account Types:" + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE + "  [1] Savings Account" + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE + "  [2] Current Account" + ConsoleUI.RESET);
        ConsoleUI.prompt("Select Account Type");
        String choice = scanner.nextLine().trim();
        return switch (choice) {
            case "1" -> AccountType.SAVINGS;
            case "2" -> AccountType.CURRENT;
            default  -> {
                ConsoleUI.printError("Invalid account type selected.");
                yield null;
            }
        };
    }
}
