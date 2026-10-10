/**
 * Legacy entry point – delegates to banking.BankingApp.
 *
 * This file exists for compatibility with the default VS Code Java launch
 * configuration that targets "App" in the root package. The actual
 * application logic lives in banking.BankingApp.
 */
public class App {
    public static void main(String[] args) {
        banking.BankingApp.main(args);
    }
}
