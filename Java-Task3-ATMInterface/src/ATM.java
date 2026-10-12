import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ATM - the user interface of the application.
 *
 * Responsibilities:
 *  - Login (User ID + PIN, maximum 3 attempts).
 *  - Show the menu and route the user's choice with a switch statement.
 *  - Read and validate console input (Scanner).
 *  - Ask the Bank / Account to do the real work and print the results.
 *
 * The ATM does NOT store accounts and does NOT change balances itself.
 * It only talks to Bank and Account (association).
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner = new Scanner(System.in);
    private Account currentAccount;   // the logged-in customer (null until login succeeds)

    public ATM(Bank bank) {
        this.bank = bank;
    }

    // =====================================================================
    //  Application flow
    // =====================================================================

    /** Runs the whole ATM session: login, then menu loop until Quit. */
    public void start() {
        try {
            if (!login()) {
                return;   // login failed 3 times: the program ends, menu is never reached
            }
            runMenuLoop();
        } catch (NoSuchElementException e) {
            // Happens if the input stream is closed (e.g. Ctrl+D / Ctrl+Z)
            System.out.println("\nInput closed. Exiting ATM. Goodbye!");
        } finally {
            scanner.close();
        }
    }

    /**
     * Asks for User ID and PIN up to MAX_LOGIN_ATTEMPTS times.
     * @return true if login succeeded, false if all attempts were used up
     */
    private boolean login() {
        System.out.println("========== ATM LOGIN ==========");
        System.out.println("Welcome to " + bank.getBankName());
        System.out.println();

        int attempts = 0;
        while (attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticate(userId, pin);
            if (account != null) {
                currentAccount = account;
                System.out.println();
                System.out.println("Login successful!");
                System.out.println("Welcome to the ATM, " + account.getHolderName() + ".");
                return true;
            }

            attempts++;
            int remaining = MAX_LOGIN_ATTEMPTS - attempts;
            System.out.println();
            System.out.println("Invalid User ID or PIN.");
            if (remaining > 0) {
                System.out.println("Attempts remaining: " + remaining);
                System.out.println();
            }
        }

        System.out.println("Maximum login attempts exceeded.");
        System.out.println("Your account has been temporarily locked.");
        return false;
    }

    /** Shows the menu repeatedly until the user chooses Quit. */
    private void runMenuLoop() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    showTransactionHistory();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    deposit();
                    break;
                case 4:
                    transfer();
                    break;
                case 5:
                    quit();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    System.out.println("Please select an option from 1 to 5.");
                    break;
            }
        }
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("========== ATM MENU ==========");
        System.out.println();
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.println();
        System.out.print("Enter your choice: ");
    }

    /** Reads the menu choice. Non-numeric text returns -1 (handled as invalid by the switch). */
    private int readMenuChoice() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // =====================================================================
    //  Menu operations
    // =====================================================================

    private void showTransactionHistory() {
        List<Transaction> history = currentAccount.getTransactionHistory();

        System.out.println();
        System.out.println("========== TRANSACTION HISTORY ==========");
        System.out.println();

        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i).toDisplayString());
            System.out.println();
        }
        System.out.println("Current balance: " + Transaction.formatCurrency(currentAccount.getBalance()));
    }

    private void withdraw() {
        System.out.println();
        System.out.print("Enter amount to withdraw: ");
        Double amount = readAmount();
        if (amount == null) {
            return;   // readAmount() already printed the reason
        }

        try {
            currentAccount.withdraw(amount);
            System.out.println();
            System.out.println("Withdrawal successful!");
            System.out.println("Amount withdrawn: " + Transaction.formatCurrency(amount));
            System.out.println("Remaining balance: " + Transaction.formatCurrency(currentAccount.getBalance()));
        } catch (IllegalArgumentException e) {            // zero / negative amount
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {               // insufficient balance
            System.out.println(e.getMessage());
            System.out.println("Available balance: " + Transaction.formatCurrency(currentAccount.getBalance()));
        }
    }

    private void deposit() {
        System.out.println();
        System.out.print("Enter amount to deposit: ");
        Double amount = readAmount();
        if (amount == null) {
            return;
        }

        try {
            currentAccount.deposit(amount);
            System.out.println();
            System.out.println("Deposit successful!");
            System.out.println("Amount deposited: " + Transaction.formatCurrency(amount));
            System.out.println("Current balance: " + Transaction.formatCurrency(currentAccount.getBalance()));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void transfer() {
        System.out.println();
        System.out.print("Enter recipient account number: ");
        String recipientAccountNumber = scanner.nextLine().trim();

        System.out.print("Enter amount to transfer: ");
        Double amount = readAmount();
        if (amount == null) {
            return;
        }

        try {
            bank.transfer(currentAccount, recipientAccountNumber, amount);
            System.out.println();
            System.out.println("Transfer successful!");
            System.out.println("Amount transferred: " + Transaction.formatCurrency(amount));
            System.out.println("Recipient Account: " + recipientAccountNumber);
            System.out.println("Remaining balance: " + Transaction.formatCurrency(currentAccount.getBalance()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println();
            System.out.println("Transfer failed.");
            System.out.println(e.getMessage());
        }
    }

    private void quit() {
        System.out.println();
        System.out.println("Thank you for using the ATM.");
        System.out.println("Please collect your card.");
        System.out.println("Goodbye!");
    }

    // =====================================================================
    //  Input helper
    // =====================================================================

    /**
     * Reads an amount from the console.
     * @return the amount, or null if the text was not a valid number
     *         (an error message has already been printed in that case)
     * Zero / negative amounts are returned as-is; Account rejects them,
     * so the "greater than zero" rule lives in exactly one place.
     */
    private Double readAmount() {
        String input = scanner.nextLine().trim();
        try {
            // Accept only plain numbers like 500, -20 or 99.50 (rejects "abc", "1e5", "5d", "NaN")
            if (!input.matches("-?\\d+(\\.\\d+)?")) {
                throw new NumberFormatException("not a plain number");
            }
            double value = Double.parseDouble(input);
            if (Math.round(value * 100) / 100.0 != value) {
                System.out.println("Amount can have at most 2 decimal places.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            System.out.println("Please enter a numeric value.");
            return null;
        }
    }
}
