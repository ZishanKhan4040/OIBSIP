import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class Main {

    public static void main(String[] args) {
        // Print as UTF-8 so the Rupee symbol shows correctly in most consoles.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        Bank bank = createBankWithSampleAccounts();
        ATM atm = new ATM(bank);
        atm.start();
    }

    /**
     * SAMPLE TEST DATA - the initial balances are initialized HERE.
     * These credentials are only for testing and can be changed freely.
     *
     * Arguments: userId, pin, holderName, accountNumber, initialBalance
     */
    private static Bank createBankWithSampleAccounts() {
        Bank bank = new Bank("OASIS Bank");
        bank.addAccount(new Account("1001", "1234", "Rahul", "500001", 10000));
        bank.addAccount(new Account("1002", "5678", "Aman",  "500002", 15000));
        bank.addAccount(new Account("1003", "4321", "Priya", "500003", 20000));
        return bank;
    }
}
