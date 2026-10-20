package com.mycompany.programing1a;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    public static final String JSON_FILE = "messages.json";

    // Shared across all messages while the program is running
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

    public Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // Randomly generated ten-digit number
    public static String generateMessageID() {
        long n = ThreadLocalRandom.current().nextLong(0, 10_000_000_000L);
        return String.format("%010d", n);
    }

    // Ensures that the message ID is not more than ten characters
    public boolean checkMessageID() {
        return messageID != null
                && messageID.length() <= 10
                && messageID.matches("\\d+");
    }

    // Recipient must start with an international code (+) and have no more than
    // ten digits after the country code.
    public static String checkRecipientCell(String cell) {
        if (cell != null && cell.matches("^\\+\\d{1,3}\\d{1,10}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // Message may not be more than 250 characters
    public static String checkMessageLength(String msg) {
        if (msg.length() <= 250) {
            return "Message ready to send.";
        }
        int over = msg.length() - 250;
        return "Message exceeds 250 characters by " + over + ", please reduce the size.";
    }

    // First two digits of ID : message number : FIRSTWORD + LASTWORD (all caps)
    // e.g. 00:0:HITONIGHT
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last)
                .toUpperCase();
    }

    // 1 = Send, 2 = Disregard, 3 = Store
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return storeMessage();
            default:
                return "Invalid option.";
        }
    }

    // Stores the message in a JSON file
    public String storeMessage() {
        try {
            File file = new File(JSON_FILE);
            JsonArray array = new JsonArray();

            if (file.exists() && file.length() > 0) {
                try (Reader reader = new FileReader(file)) {
                    array = JsonParser.parseReader(reader).getAsJsonArray();
                }
            }

            JsonObject obj = new JsonObject();
            obj.addProperty("messageID", messageID);
            obj.addProperty("messageHash", messageHash);
            obj.addProperty("recipient", recipient);
            obj.addProperty("message", text);
            array.add(obj);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(array, writer);
            }

            storedMessages.add(this);
            return "Message successfully stored.";
        } catch (IOException | RuntimeException e) {
            return "Could not store message: " + e.getMessage();
        }
    }

    // Full details in the required order: ID, Hash, Recipient, Message
    public String getDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + text;
    }

    // All the messages sent while the program is running
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetails()).append("\n\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Clears the static lists/counter (used by unit tests)
    public static void resetState() {
        sentMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }
}
