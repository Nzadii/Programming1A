package com.mycompany.programing1a;

import java.util.Scanner;

public class QuickChat {

    // Called from PROGRAMING1A.main() once the user has logged in successfully
    public static void start(Scanner sc) {

        System.out.println();
        System.out.println("Welcome to QuickChat.");

        int maxMessages = readPositiveInt(sc,
                "How many messages do you wish to enter? ");
        int messagesEntered = 0;

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");
            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    if (messagesEntered >= maxMessages) {
                        System.out.println("You have already entered the "
                                + maxMessages + " message(s) you planned.");
                        break;
                    }
                    while (messagesEntered < maxMessages) {
                        processMessage(sc, messagesEntered);
                        messagesEntered++;
                    }
                    System.out.println();
                    System.out.println("Total messages sent: "
                            + Message.returnTotalMessages());
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }
    }

    private static void processMessage(Scanner sc, int messageNumber) {
        System.out.println();
        System.out.println("--- Message " + (messageNumber + 1) + " ---");

        String id = Message.generateMessageID();
        System.out.println("Message ID generated: " + id);

        // Recipient (international code required)
        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            recipient = sc.nextLine().trim();
            String result = Message.checkRecipientCell(recipient);
            System.out.println(result);
            if (result.equals("Cell phone number successfully captured.")) {
                break;
            }
        }

        // Message text (max 250 characters)
        String text;
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            text = sc.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }
            if (text.length() <= 250) {
                System.out.println("Message ready to send.");
                break;
            }
            System.out.println(Message.checkMessageLength(text));
            System.out.println("Please enter a message of less than 250 characters.");
        }

        Message msg = new Message(id, messageNumber, recipient, text);

        System.out.println();
        System.out.println("Choose an option:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int choice = readChoice(sc);

        System.out.println(msg.sentMessage(choice));

        if (choice == 2) {
            System.out.print("Enter 0 to delete the message: ");
            if (sc.nextLine().trim().equals("0")) {
                System.out.println("Message deleted.");
            }
        }

        // Full details are shown once the message has been sent
        if (choice == 1) {
            System.out.println();
            System.out.println(msg.getDetails());
        }
    }

    private static int readChoice(Scanner sc) {
        while (true) {
            System.out.print("Option (1-3): ");
            String in = sc.nextLine().trim();
            if (in.equals("1") || in.equals("2") || in.equals("3")) {
                return Integer.parseInt(in);
            }
            System.out.println("Please enter 1, 2 or 3.");
        }
    }

    private static int readPositiveInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n > 0) {
                    return n;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }
}
