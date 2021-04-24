package HalfPlaneIntersectionMethod;

import java.util.Arrays;
import java.util.Scanner;

public class CommandLineControl {

    /**
     * decompose received string command into tokens (words and numbers)
     * @param command command that must tokenized
     * @return array of tokens
     */
    public String[] commandToTokens(String command) {
        //  make all letters lowercase and remove repeating dots
        command = command.toLowerCase();
        command = command.replaceAll("\\.+", ".");

        //  split string by whitespaces (repeatable and non-repeatable)
        String[] words = command.split("\\s+");

        //  for all possible words and numbers
        for (int i = 0; i < words.length; i++) {
            //  if this is a number, then remove repeating dots and remove commas
            if (words[i].matches(".*\\d.*")) {
                words[i] = words[i].replaceAll(",+", "");
                continue;
            }

            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        return words;
    }

    public static void main(String[] args) {
        CommandLineControl cmd = new CommandLineControl();
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter your data");

        String data = scanner.nextLine();
        System.out.println("you inputted = " + data);
        System.out.println("length of the input is = " + data.length());
        System.out.println("length of the input is = " + Arrays.toString(cmd.commandToTokens(data)));
    }
}
