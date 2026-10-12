import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Account - represents ONE bank customer account.
 *
 * Responsibilities:
 *  - Stores the account data (User ID, PIN, name, account number, balance).
 *  - Owns the transaction history: ArrayList<Transaction>.
 *  - Performs the money operations: deposit, withdraw, transferTo.
 *  - Guarantees the balance can NEVER become negative.
 *
 * Encapsulation: every field is private. Outside classes can only use the
 * public methods below. There is no setBalance() on purpose - the balance
 * can only change through deposit / withdraw / transferTo, which validate
 * the amount and record a Transaction.
 */
public class Account {

    private final String userId;
    private final String pin;
    private final String holderName;
    private final String accountNumber;
    private double balance;
    private final ArrayList<Transaction> transactionHistory = new ArrayList<>();

    public Account(String userId, String pin, String holderName,
                   String accountNumber, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.userId = userId;
        this.pin = pin;
        this.holderName = holderName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    // ----- getters (no getPin(): the PIN is never exposed, only verified) -----

    public String getUserId() {
        return userId;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    /** Returns a read-only view, so other classes cannot edit the history. */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // ----- authentication helper -----

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    // ----- account operations -----

    /**
     * Adds money to the account.
     * @throws IllegalArgumentException if amount is not greater than zero
     */
    public void deposit(double amount) {
        validateAmount(amount);
        balance += amount;
        recordTransaction(Transaction.Type.DEPOSIT, amount, "Cash deposit");
    }

    /**
     * Removes money from the account.
     * @throws IllegalArgumentException if amount is not greater than zero
     * @throws IllegalStateException    if amount is more than the balance
     */
    public void withdraw(double amount) {
        validateAmount(amount);
        requireSufficientBalance(amount);
        balance -= amount;
        recordTransaction(Transaction.Type.WITHDRAW, amount, "Cash withdrawal");
    }

    /**
     * Moves money from THIS account to the recipient account.
     * All checks happen BEFORE any balance changes, so a failed transfer
     * never leaves the accounts half-updated.
     * Both accounts get a TRANSFER entry in their own history.
     */
    public void transferTo(Account recipient, double amount) {
        validateAmount(amount);
        if (recipient == this) {
            throw new IllegalArgumentException("You cannot transfer money to your own account.");
        }
        requireSufficientBalance(amount);

        this.balance -= amount;
        recipient.balance += amount;

        this.recordTransaction(Transaction.Type.TRANSFER, amount,
                "Transferred to account " + recipient.accountNumber);
        recipient.recordTransaction(Transaction.Type.TRANSFER, amount,
                "Received from account " + this.accountNumber);
    }

    // ----- private helpers -----

    private void validateAmount(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private void requireSufficientBalance(double amount) {
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance.");
        }
    }

    private void recordTransaction(Transaction.Type type, double amount, String description) {
        transactionHistory.add(new Transaction(type, amount, balance, description));
    }
}
