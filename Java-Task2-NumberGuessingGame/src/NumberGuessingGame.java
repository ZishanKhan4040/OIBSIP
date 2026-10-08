import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static final String DOUBLE_LINE = "========================================";
    private static final String SINGLE_LINE = "----------------------------------------";

    // Holds the settings of one difficulty level
    static class Difficulty {
        final String name;
        final int minNumber;
        final int maxNumber;
        final int maxAttempts;

        Difficulty(String name, int minNumber, int maxNumber, int maxAttempts) {
            this.name = name;
            this.minNumber = minNumber;
            this.maxNumber = maxNumber;
            this.maxAttempts = maxAttempts;
        }
    }

    // Stores the outcome of one finished round
    static class RoundRecord {
        final int roundNumber;
        final boolean won;
        final int attempts;
        final int maxAttempts;
        final int secretNumber;

        RoundRecord(int roundNumber, boolean won, int attempts, int maxAttempts, int secretNumber) {
            this.roundNumber = roundNumber;
            this.won = won;
            this.attempts = attempts;
            this.maxAttempts = maxAttempts;
            this.secretNumber = secretNumber;
        }
    }

    public static void main(String[] args) {
        List<RoundRecord> history = new ArrayList<>();
        boolean keepPlaying = true;

        displayTitle();

        while (keepPlaying) {
            Difficulty difficulty = selectDifficulty();
            int roundNumber = history.size() + 1;

            RoundRecord record = playRound(difficulty, roundNumber);
            displayResult(record);
            history.add(record);

            keepPlaying = playAgain();
        }

        displayFinalSummary(history);
    }

    private static void displayTitle() {
        System.out.println(DOUBLE_LINE);
        System.out.println("       NUMBER GUESSING GAME");
        System.out.println(DOUBLE_LINE);
    }

    private static Difficulty selectDifficulty() {
        System.out.println();
        System.out.println("Select Difficulty:");
        System.out.println();
        System.out.println("1. Easy   (1-50, 10 attempts)");
        System.out.println("2. Medium (1-100, 7 attempts)");
        System.out.println("3. Hard   (1-200, 5 attempts)");
        System.out.println();

        int choice = readInteger("Enter your choice: ", 1, 3);

        Difficulty difficulty;
        switch (choice) {
            case 1:
                difficulty = new Difficulty("Easy", 1, 50, 10);
                break;
            case 3:
                difficulty = new Difficulty("Hard", 1, 200, 5);
                break;
            default:
                difficulty = new Difficulty("Medium", 1, 100, 7);
                break;
        }
        return difficulty;
    }

    private static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static RoundRecord playRound(Difficulty difficulty, int roundNumber) {
        int secretNumber = generateRandomNumber(difficulty.minNumber, difficulty.maxNumber);
        int attempts = 0;
        boolean guessed = false;

        System.out.println();
        System.out.println(SINGLE_LINE);
        System.out.println("Difficulty: " + difficulty.name);
        System.out.println("Range: " + difficulty.minNumber + "-" + difficulty.maxNumber);
        System.out.println("Maximum Attempts: " + difficulty.maxAttempts);
        System.out.println(SINGLE_LINE);

        while (attempts < difficulty.maxAttempts && !guessed) {
            System.out.println();
            System.out.println("Attempt " + (attempts + 1) + "/" + difficulty.maxAttempts);
            int guess = readInteger("Enter your guess: ", difficulty.minNumber, difficulty.maxNumber);
            attempts++; // counted only after a valid guess

            System.out.println();
            if (guess > secretNumber) {
                System.out.println("Too High!");
            } else if (guess < secretNumber) {
                System.out.println("Too Low!");
            } else {
                System.out.println("Correct!");
                guessed = true;
            }
        }

        return new RoundRecord(roundNumber, guessed, attempts, difficulty.maxAttempts, secretNumber);
    }

    // Keeps asking until the user enters a whole number between min and max
    private static int readInteger(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Out of range! Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number.");
            }
        }
    }

    private static void displayResult(RoundRecord record) {
        System.out.println();
        System.out.println(DOUBLE_LINE);
        System.out.println("             ROUND RESULT");
        System.out.println(DOUBLE_LINE);

        if (record.won) {
            System.out.println("Round " + record.roundNumber + " - guessed in "
                    + record.attempts + " " + attemptWord(record.attempts));
        } else {
            System.out.println("You Lost!");
            System.out.println();
            System.out.println("The correct number was: " + record.secretNumber);
            System.out.println();
            System.out.println("Round " + record.roundNumber + " - Not guessed within "
                    + record.maxAttempts + " attempts.");
        }
        System.out.println(DOUBLE_LINE);
    }

    private static boolean playAgain() {
        while (true) {
            System.out.println();
            System.out.print("Do you want to play again? (Y/N): ");
            String answer = scanner.nextLine().trim().toUpperCase();

            switch (answer) {
                case "Y":
                    return true;
                case "N":
                    return false;
                default:
                    System.out.println("Invalid input! Please enter Y or N.");
            }
        }
    }

    private static void displayFinalSummary(List<RoundRecord> history) {
        int wins = 0;
        for (RoundRecord record : history) {
            if (record.won) {
                wins++;
            }
        }

        System.out.println();
        System.out.println(DOUBLE_LINE);
        System.out.println("          GAME SUMMARY");
        System.out.println(DOUBLE_LINE);
        System.out.println();
        System.out.println("Total Rounds: " + history.size());
        System.out.println("Rounds Won: " + wins);
        System.out.println("Rounds Lost: " + (history.size() - wins));
        System.out.println();

        for (RoundRecord record : history) {
            if (record.won) {
                System.out.println("Round " + record.roundNumber + " - Won in "
                        + record.attempts + " " + attemptWord(record.attempts));
            } else {
                System.out.println("Round " + record.roundNumber + " - Lost");
            }
        }

        System.out.println();
        System.out.println("Thanks for playing!");
    }

    private static String attemptWord(int count) {
        return count == 1 ? "attempt" : "attempts";
    }
}