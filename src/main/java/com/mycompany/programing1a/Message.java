package com.mycompany.programing1a;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    private static String jsonFile = "messages.json";

    // ---------- Part 3 arrays (populated while the program runs, no hard-coding) ----------
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

    // Used when a new message is created
    public Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // Used when a message is read back from the JSON file (the hash is already known)
    private Message(String messageID, String messageHash, String recipient, String text) {
        this.messageID = messageID;
        this.messageHash = messageHash;
        this.recipient = recipient;
        this.text = text;
        this.messageNumber = numberFromHash(messageHash);
    }

    private static int numberFromHash(String hash) {
        try {
            return Integer.parseInt(hash.split(":")[1]);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    // ---------- Message ID ----------
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

    // ---------- Recipient ----------
    // Must start with an international code (+) and have no more than
    // ten digits after the country code.
    public static String checkRecipientCell(String cell) {
        if (cell != null && cell.matches("^\\+\\d{1,3}\\d{1,10}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    // ---------- Message length ----------
    public static String checkMessageLength(String msg) {
        if (msg.length() <= 250) {
            return "Message ready to send.";
        }
        int over = msg.length() - 250;
        return "Message exceeds 250 characters by " + over + ", please reduce the size.";
    }

    // ---------- Message hash ----------
    // First two digits of ID : message number : FIRSTWORD + LASTWORD (all caps)
    // e.g. 00:0:HITONIGHT
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last)
                .toUpperCase();
    }

    // ---------- Send / Disregard / Store ----------
    // 1 = Send, 2 = Disregard, 3 = Store
    public String sentMessage(int choice) {
        String result;
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                result = "Message successfully sent.";
                break;
            case 2:
                disregardedMessages.add(this);
                result = "Press 0 to delete the message.";
                break;
            case 3:
                result = storeMessage();
                break;
            default:
                return "Invalid option.";
        }
        // Every message that is handled has its hash and ID recorded
        messageHashes.add(messageHash);
        messageIDs.add(messageID);
        return result;
    }

    // ---------- JSON storage ----------
    // Stores the message in the JSON file
    public String storeMessage() {
        try {
            loadStoredMessages();          // bring in what is already in the file
            storedMessages.add(this);
            writeStoredMessages();         // write the whole stored array back
            return "Message successfully stored.";
        } catch (IOException | RuntimeException e) {
            return "Could not store message: " + e.getMessage();
        }
    }

    // Writes the Stored Messages array to the JSON file
    private static void writeStoredMessages() throws IOException {
        JsonArray array = new JsonArray();
        for (Message m : storedMessages) {
            JsonObject obj = new JsonObject();
            obj.addProperty("messageID", m.messageID);
            obj.addProperty("messageHash", m.messageHash);
            obj.addProperty("recipient", m.recipient);
            obj.addProperty("message", m.text);
            array.add(obj);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(array, writer);
        }
    }

    // Reads the JSON file into the Stored Messages array (replaces its contents)
    public static List<Message> loadStoredMessages() {
        storedMessages.clear();
        File file = new File(jsonFile);
        if (!file.exists() || file.length() == 0) {
            return storedMessages;
        }
        try (Reader reader = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                storedMessages.add(new Message(
                        obj.get("messageID").getAsString(),
                        obj.get("messageHash").getAsString(),
                        obj.get("recipient").getAsString(),
                        obj.get("message").getAsString()));
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read the stored messages: " + e.getMessage());
        }
        return storedMessages;
    }

    // ---------- Part 3: working with the arrays ----------

    // a. Sender and recipient of all stored messages
    public static String displaySenderAndRecipient(String sender) {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(sender)
              .append(" | Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString().trim();
    }

    // b. The longest stored message
    public static String longestStoredMessage() {
        Message longest = null;
        for (Message m : storedMessages) {
            if (longest == null || m.text.length() > longest.text.length()) {
                longest = m;
            }
        }
        return (longest == null) ? "No stored messages." : longest.text;
    }

    // c. Search for a message ID (looks through sent, disregarded and stored messages)
    public static Message findByMessageID(String id) {
        for (Message m : sentMessages) {
            if (m.messageID.equals(id)) return m;
        }
        for (Message m : disregardedMessages) {
            if (m.messageID.equals(id)) return m;
        }
        for (Message m : storedMessages) {
            if (m.messageID.equals(id)) return m;
        }
        return null;
    }

    // d. All messages sent or stored for a particular recipient
    public static List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipient)) found.add(m.text);
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipient)) found.add(m.text);
        }
        return found;
    }

    // e. Delete a stored message using its message hash
    public static String deleteByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (m.messageHash.equalsIgnoreCase(hash)) {
                storedMessages.remove(i);
                messageHashes.remove(m.messageHash);
                messageIDs.remove(m.messageID);
                try {
                    writeStoredMessages();   // keep the JSON file in step with the array
                } catch (IOException e) {
                    return "Message deleted, but the JSON file could not be updated: "
                            + e.getMessage();
                }
                return "Message: \"" + m.text + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    // f. Report of all stored messages (full details)
    public static String storedMessagesReport() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        return buildReport(storedMessages);
    }

    // Report of all messages sent (Part 2's printMessages)
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        return buildReport(sentMessages);
    }

    private static String buildReport(List<Message> list) {
        StringBuilder sb = new StringBuilder();
        for (Message m : list) {
            sb.append(m.getDetails()).append("\n\n");
        }
        return sb.toString().trim();
    }

    // Full details in the required order: ID, Hash, Recipient, Message
    public String getDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + text;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // ---------- Array getters (text / values only) ----------
    public static List<String> getSentMessages() {
        return texts(sentMessages);
    }

    public static List<String> getDisregardedMessages() {
        return texts(disregardedMessages);
    }

    public static List<String> getStoredMessages() {
        return texts(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    private static List<String> texts(List<Message> list) {
        List<String> result = new ArrayList<>();
        for (Message m : list) {
            result.add(m.text);
        }
        return result;
    }

    // ---------- Housekeeping (used by unit tests) ----------
    public static void setJsonFile(String path) {
        jsonFile = path;
    }

    public static String getJsonFile() {
        return jsonFile;
    }

    // Clears all the arrays and the counter
    public static void resetState() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }
}
