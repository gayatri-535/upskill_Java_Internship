package banking.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered user in the Banking Information System.
 * Stores personal details and a list of associated bank accounts.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;          // Unique user identifier
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String password;        // Hashed password
    private LocalDate dateOfBirth;
    private LocalDate registrationDate;
    private List<String> accountNumbers; // Account numbers linked to this user

    public User(String userId, String fullName, String email, String phone,
                String address, String password, LocalDate dateOfBirth) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = LocalDate.now();
        this.accountNumbers = new ArrayList<>();
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getUserId()            { return userId; }
    public String getFullName()          { return fullName; }
    public String getEmail()             { return email; }
    public String getPhone()             { return phone; }
    public String getAddress()           { return address; }
    public String getPassword()          { return password; }
    public LocalDate getDateOfBirth()    { return dateOfBirth; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public List<String> getAccountNumbers() { return accountNumbers; }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setFullName(String fullName)    { this.fullName = fullName; }
    public void setEmail(String email)          { this.email = email; }
    public void setPhone(String phone)          { this.phone = phone; }
    public void setAddress(String address)      { this.address = address; }
    public void setPassword(String password)    { this.password = password; }

    /** Adds an account number to this user's list of accounts. */
    public void addAccountNumber(String accountNumber) {
        if (!accountNumbers.contains(accountNumber)) {
            accountNumbers.add(accountNumber);
        }
    }

    @Override
    public String toString() {
        return String.format(
            "User[id=%s, name=%s, email=%s, phone=%s, accounts=%s]",
            userId, fullName, email, phone, accountNumbers
        );
    }
}
