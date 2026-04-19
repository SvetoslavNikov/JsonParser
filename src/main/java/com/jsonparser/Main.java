package com.jsonparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        boolean pretty = false;
        String filePath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--pretty" -> pretty = true;
                case "--file" -> {
                    if (i + 1 >= args.length) {
                        System.err.println("Error: --file requires a path");
                        System.exit(1);
                    }
                    filePath = args[++i];
                }
                default -> {
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(1);
                }
            }
        }

        String input;
        try {
            input = (filePath != null) ? readFromFile(filePath) : readFromStdin();
        } catch (IOException ex) {
            System.err.println("Error reading input: " + ex.getMessage());
            System.exit(1);
            return;
        }

        try {
            JsonValue value = JsonProcessor.parse(input);
            System.out.println("Valid JSON");
            System.out.println(AstPrinter.print(value));
            if (pretty) {
                System.out.println();
                System.out.println("Pretty JSON:");
                System.out.println(PrettyPrinter.format(value));
            }
        } catch (ParseException ex) {
            System.err.println("Invalid JSON");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private static String readFromFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    private static String readFromStdin() {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
