import java.util.ArrayList;

/**
 * Bank - manages the collection of all customer accounts.
 *
 * Responsibilities:
 *  - Stores every Account (Bank "has many" Accounts).
 *  - Finds accounts by User ID or by account number.
 *  - Authenticates a User ID + PIN pair.
 *  - Coordinates transfers between two accounts.
 *
 * The Bank knows nothing about menus or Scanner input - that is the ATM's job.
 */
public class Bank {

    private final String bankName;
    private final ArrayList<Account> accounts = new ArrayList<>();

    public Bank(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }

    /** Registers a new account. User IDs and account numbers must be unique. */
    public void addAccount(Account account) {
        if (findByUserId(account.getUserId()) != null) {
            throw new IllegalArgumentException("Duplicate User ID: " + account.getUserId());
        }
        if (findByAccountNumber(account.getAccountNumber()) != null) {
            throw new IllegalArgumentException("Duplicate account number: " + account.getAccountNumber());
        }
        accounts.add(account);
    }

    /** @return the matching account, or null if none exists */
    public Account findByUserId(String userId) {
        for (Account account : accounts) {
            if (account.getUserId().equals(userId)) {
                return account;
            }
        }
        return null;
    }

    /** @return the matching account, or null if none exists */
    public Account findByAccountNumber(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Checks a User ID + PIN pair.
     * @return the Account if the credentials are correct, otherwise null
     *         (the same result for "unknown user" and "wrong PIN", so an
     *         attacker cannot learn which User IDs exist)
     */
    public Account authenticate(String userId, String pin) {
        Account account = findByUserId(userId);
        if (account != null && account.verifyPin(pin)) {
            return account;
        }
        return null;
    }

    /**
     * Transfers money from the sender to the account with the given number.
     * @throws IllegalArgumentException recipient missing / same account / bad amount
     * @throws IllegalStateException    sender has insufficient balance
     */
    public void transfer(Account sender, String recipientAccountNumber, double amount) {
        Account recipient = findByAccountNumber(recipientAccountNumber);
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient account not found.");
        }
        sender.transferTo(recipient, amount);
    }
}
