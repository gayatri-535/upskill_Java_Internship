package banking;

import banking.model.User;
import banking.service.BankingService;
import banking.ui.ConsoleUI;
import banking.ui.GuestMenu;
import banking.ui.UserDashboard;

import java.util.Scanner;

/**
 * Entry point for the Banking Information System prototype.
 *
 * <p>Initialises the single shared {@link BankingService} instance and the
 * shared {@link Scanner}, then alternates between the {@link GuestMenu} (for
 * authentication / registration) and the {@link UserDashboard} (for all
 * banking operations) until the user chooses to exit.</p>
 */
public class BankingApp {

    public static void main(String[] args) {

        // ── Infrastructure ──────────────────────────────────────────────────────
        BankingService service = new BankingService();
        Scanner        scanner = new Scanner(System.in);

        ConsoleUI.printBanner();
        ConsoleUI.printInfo("Banking Information System started successfully.");
        ConsoleUI.printInfo("All data is stored in-memory for this prototype session.");
        System.out.println();

        // ── Application Loop ────────────────────────────────────────────────────
        try {
            GuestMenu guestMenu = new GuestMenu(service, scanner);

            while (true) {
                // Show guest menu; returns logged-in user or null to exit
                User loggedInUser = guestMenu.show();

                if (loggedInUser == null) {
                    // User chose "Exit Application"
                    break;
                }

                // Hand off to the authenticated dashboard
                UserDashboard dashboard = new UserDashboard(service, scanner, loggedInUser);
                dashboard.run();
                // After logout, loop back to the guest menu
            }

        } catch (Exception e) {
            // Catch-all for unexpected runtime errors
            ConsoleUI.printError("An unexpected error occurred: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace(System.err);
        } finally {
            scanner.close();
        }
    }
}
