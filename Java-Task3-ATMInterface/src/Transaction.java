import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Transaction - an immutable record of ONE successful banking operation.
 *
 * Every successful deposit, withdrawal and transfer creates a Transaction
 * object which is stored in the owning Account's ArrayList<Transaction>.
 *
 * All fields are private and final: once created, a transaction can never
 * be modified (just like a real bank statement entry).
 */
public class Transaction {

    /** The kinds of transactions supported by the ATM. */
    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER
    }

    /** Unicode escape for the Indian Rupee sign, so the source file is encoding-safe. */
    public static final String CURRENCY_SYMBOL = "\u20B9";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(Type type, double amount, double balanceAfter, String description) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // ----- getters only (no setters: a transaction is immutable) -----

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DATE_FORMAT);
    }

    /**
     * Formats money like "₹12,000" (whole numbers) or "₹12,000.50" (with paise).
     * Shared helper so every class prints money in the same style.
     */
    public static String formatCurrency(double value) {
        if (value == Math.floor(value)) {
            return String.format(Locale.US, CURRENCY_SYMBOL + "%,.0f", value);
        }
        return String.format(Locale.US, CURRENCY_SYMBOL + "%,.2f", value);
    }

    /** Multi-line text used by the Transaction History screen. */
    public String toDisplayString() {
        return type + "\n"
                + "   Amount: " + formatCurrency(amount) + "\n"
                + "   Balance: " + formatCurrency(balanceAfter) + "\n"
                + "   Details: " + description + "\n"
                + "   Date: " + getFormattedTimestamp();
    }
}
