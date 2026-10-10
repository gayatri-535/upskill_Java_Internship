# 🏦 Banking Information System (BIS)

> **Core Java Prototype v1.0** — A fully functional console-based Banking Information System built in pure Core Java.

---

## 📋 Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [How to Compile & Run](#how-to-compile--run)
- [System Walkthrough](#system-walkthrough)
- [Architecture Overview](#architecture-overview)
- [Error Handling](#error-handling)

---

## ✨ Features

| # | Feature | Status |
|---|---------|--------|
| 1 | **User Registration** — Name, DOB, email, phone, address, password, account type | ✅ |
| 2 | **Account Management** — View profile, update details, open new accounts | ✅ |
| 3 | **Deposit & Withdrawal** — With balance update and printed receipt | ✅ |
| 4 | **Fund Transfer** — Between any two accounts (cross-user supported) | ✅ |
| 5 | **Account Statements** — Full transaction history with dates & balances | ✅ |
| 6 | **Password Protection** — SHA-256 hashed passwords, 3-attempt login lockout | ✅ |
| 7 | **Error Handling** — Custom exceptions for all failure scenarios | ✅ |
| 8 | **User Interface** — Rich ANSI colour-coded terminal UI with menus & receipts | ✅ |
| 9 | **Persistence** — In-memory storage for the prototype session | ✅ |

---

## 📁 Project Structure

```
Banking_Information_System/
├── src/
│   ├── App.java                            ← Legacy entry point (delegates to BankingApp)
│   └── banking/
│       ├── BankingApp.java                 ← Main entry point
│       ├── model/
│       │   ├── User.java                   ← User entity (profile + account refs)
│       │   ├── BankAccount.java            ← Account entity (balance + transactions)
│       │   └── Transaction.java            ← Transaction entity (deposit/withdrawal/transfer)
│       ├── service/
│       │   └── BankingService.java         ← All business logic
│       ├── exception/
│       │   ├── InsufficientFundsException.java
│       │   ├── AccountNotFoundException.java
│       │   ├── AuthenticationException.java
│       │   └── InvalidTransactionException.java
│       ├── ui/
│       │   ├── ConsoleUI.java              ← All display/print helpers
│       │   ├── GuestMenu.java              ← Login & registration UI
│       │   └── UserDashboard.java          ← Authenticated banking dashboard
│       └── util/
│           └── BankingUtils.java           ← SHA-256, ID generation, validation
├── bin/                                    ← Compiled .class files
├── lib/                                    ← External JARs (none required for v1.0)
└── .vscode/
    ├── settings.json
    └── launch.json
```

---

## 🚀 How to Compile & Run

### Prerequisites
- Java JDK 17 or higher
- A terminal (Command Prompt, PowerShell, or VS Code integrated terminal)

### Option 1 — VS Code (Recommended)
1. Open the `Banking_Information_System` folder in VS Code
2. Press **F5** (or `Run → Start Debugging`)
3. Select **"Banking Information System"** from the launch configuration

> ⚠️ Make sure the terminal supports UTF-8 to see the UI correctly.

### Option 2 — Command Line

**Compile:**
```bash
cd Banking_Information_System
javac -encoding UTF-8 -d bin -sourcepath src src\banking\BankingApp.java src\banking\model\*.java src\banking\exception\*.java src\banking\service\*.java src\banking\ui\*.java src\banking\util\*.java
```

**Run:**
```bash
java -Dfile.encoding=UTF-8 -cp bin banking.BankingApp
```

### Option 3 — PowerShell One-Liner
```powershell
cd "Banking_Information_System"; javac -encoding UTF-8 -d bin -sourcepath src (Get-ChildItem src -Recurse -Filter "*.java").FullName; java -Dfile.encoding=UTF-8 -cp bin banking.BankingApp
```

---

## 🗺️ System Walkthrough

### 1. Registration
- From the main menu, select **[2] Register New Account**
- Provide: Full Name, Date of Birth, Email, Phone (10 digits), Address
- Set a password (min 8 chars, 1 digit, 1 special character)
- Choose account type: Savings or Current
- Enter initial deposit (minimum ₹500)
- A unique **account number** and **User ID** are generated and displayed

### 2. Login
- Select **[1] Login to My Account**
- Enter your registered email and password
- 3 failed attempts → automatically returned to main menu

### 3. Dashboard Operations
After login, the dashboard presents:
- **Account Overview** — Balances for all your accounts
- **Deposit Funds** — Select account → enter amount → receipt printed
- **Withdraw Funds** — Select account → enter amount → receipt printed
- **Fund Transfer** — Select source → enter destination account number → confirm
- **Account Statement** — Full transaction history (newest first)
- **Manage Profile** — Update name/phone/address or change password
- **Open New Account** — Add another Savings or Current account

---

## 🏗️ Architecture Overview

```
┌─────────────────────┐
│    UI Layer         │  GuestMenu, UserDashboard, ConsoleUI
│  (banking.ui)       │
└─────────┬───────────┘
          │ calls
┌─────────▼───────────┐
│  Service Layer      │  BankingService — all business rules
│  (banking.service)  │
└─────────┬───────────┘
          │ uses
┌─────────▼───────────┐
│  Model Layer        │  User, BankAccount, Transaction
│  (banking.model)    │
└─────────────────────┘
          │
┌─────────▼───────────┐
│  Support            │  BankingUtils, Custom Exceptions
│  (banking.util/.exc)│
└─────────────────────┘
```

---

## ⚠️ Error Handling

| Exception | Trigger |
|-----------|---------|
| `InsufficientFundsException` | Withdrawal/transfer exceeds available balance |
| `AccountNotFoundException`   | Invalid account number or User ID referenced |
| `AuthenticationException`    | Wrong password or unknown email during login |
| `InvalidTransactionException`| Negative amount, self-transfer, weak password, duplicate email |

All exceptions are caught in the UI layer and shown as user-friendly messages — the application **never crashes** due to user input errors.

---

## 📌 Notes
- This is a **prototype** — data is stored in-memory and lost when the program exits.
- Passwords are **SHA-256 hashed** (not stored as plain text).
- Account numbers are **unique 10-digit numbers** generated automatically.
- The system supports **multiple accounts per user**.
