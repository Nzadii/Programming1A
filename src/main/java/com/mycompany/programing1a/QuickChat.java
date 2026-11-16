package com.mycompany.programing1a;

import java.util.List;
import java.util.Scanner;

public class QuickChat {

    // Called from PROGRAMING1A.main() once the user has logged in successfully
    public static void start(Scanner sc, String senderName) {

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
            System.out.println("4) Stored Messages");
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
                    System.out.println();
                    System.out.println(Message.printMessages());
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                case "4":
                    storedMessagesMenu(sc, senderName);
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2, 3 or 4.");
            }
        }
    }

    // ---------- Part 3: Stored Messages menu ----------
    private static void storedMessagesMenu(Scanner sc, String senderName) {

        // Read the JSON file into the Stored Messages array
        Message.loadStoredMessages();

        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Stored Messages ---");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages sent or stored for a recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("0) Back to the main menu");
            System.out.print("Choose an option: ");
            String option = sc.nextLine().trim();

            switch (option) {
                case "1":
                    System.out.println(Message.displaySenderAndRecipient(senderName));
                    break;
                case "2":
                    System.out.println("Longest stored message: "
                            + Message.longestStoredMessage());
                    break;
                case "3":
                    System.out.print("Enter the message ID: ");
                    Message found = Message.findByMessageID(sc.nextLine().trim());
                    if (found == null) {
                        System.out.println("Message ID not found.");
                    } else {
                        System.out.println("Recipient: " + found.getRecipient());
                        System.out.println("Message: " + found.getText());
                    }
                    break;
                case "4":
                    System.out.print("Enter the recipient's cell number: ");
                    List<String> results = Message.searchByRecipient(sc.nextLine().trim());
                    if (results.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String text : results) {
                            System.out.println("\"" + text + "\"");
                        }
                    }
                    break;
                case "5":
                    System.out.print("Enter the message hash: ");
                    System.out.println(Message.deleteByHash(sc.nextLine().trim()));
                    break;
                case "6":
                    System.out.println(Message.storedMessagesReport());
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please choose 0 to 6.");
            }
        }
    }

    // ---------- Part 2: sending a message ----------
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
