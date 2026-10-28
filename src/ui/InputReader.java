package ui;

import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader() {
        this.scanner = new Scanner(System.in);
    }

    public String readString(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print("Girdi boş olamaz. " + prompt + ": ");
            input = scanner.nextLine().trim();
        }
        return input;
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Lütfen " + min + " ile " + max + " arasında bir sayı girin.");
            } catch (NumberFormatException e) {
                System.out.println("Geçersiz sayı formatı! Lütfen tekrar deneyin.");
            }
        }
    }
}