package com.mycompany.programing1a;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Part 3 unit tests, using the Part 3 test data (Test Data Messages 1 to 5)
public class StoredMessagesTest {

    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!";
    private static final String MSG5 = "Ok, I am leaving without you.";

    private File tempJson;
    private Message m1, m2, m3, m4, m5;

    @BeforeEach
    void setUp() throws IOException {
        // Use a temporary JSON file so the tests never touch the real messages.json
        tempJson = File.createTempFile("quickchat-part3-test", ".json");
        tempJson.delete();
        Message.setJsonFile(tempJson.getAbsolutePath());
        Message.resetState();

        // Developer entry of the test data
        m1 = new Message("0012345671", 0, "+27834557896", MSG1);
        m2 = new Message("0012345672", 1, "+27838884567", MSG2);
        m3 = new Message("0012345673", 2, "+27834484567", MSG3);
        m4 = new Message("0838884567", 3, "0838884567", MSG4);
        m5 = new Message("0012345675", 4, "+27838884567", MSG5);

        m1.sentMessage(1); // Sent
        m2.sentMessage(3); // Stored
        m3.sentMessage(2); // Disregard
        m4.sentMessage(1); // Sent
        m5.sentMessage(3); // Stored
    }

    @AfterEach
    void cleanUp() {
        tempJson.delete();
        Message.setJsonFile("messages.json");
        Message.resetState();
    }

    // Sent Messages array correctly populated
    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(MSG1, MSG4), Message.getSentMessages());
    }

    // Disregarded Messages array correctly populated
    @Test
    void disregardedMessagesArrayCorrectlyPopulated() {
        assertEquals(List.of(MSG3), Message.getDisregardedMessages());
    }

    // Stored Messages array is read from the JSON file
    @Test
    void storedMessagesReadFromJsonFile() {
        Message.resetState();
        Message.loadStoredMessages();
        assertEquals(List.of(MSG2, MSG5), Message.getStoredMessages());
    }

    // Message Hash and Message ID arrays correctly populated
    @Test
    void hashAndIdArraysCorrectlyPopulated() {
        assertEquals(5, Message.getMessageHashes().size());
        assertEquals(5, Message.getMessageIDs().size());
        assertTrue(Message.getMessageHashes().contains("00:1:WHERETIME"));
        assertTrue(Message.getMessageIDs().contains("0838884567"));
    }

    // Display the longest message
    @Test
    void displayLongestMessage() {
        assertEquals(MSG2, Message.longestStoredMessage());
    }

    // Search for messageID
    @Test
    void searchForMessageID() {
        assertEquals(MSG4, Message.findByMessageID("0838884567").getText());
    }

    @Test
    void searchForUnknownMessageIDReturnsNull() {
        assertNull(Message.findByMessageID("9999999999"));
    }

    // Search all the messages sent or stored regarding a particular recipient
    @Test
    void searchMessagesForRecipient() {
        assertEquals(List.of(MSG2, MSG5), Message.searchByRecipient("+27838884567"));
    }

    // Delete a message using a message hash (Test Message 2)
    @Test
    void deleteMessageUsingMessageHash() {
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.",
                Message.deleteByHash(m2.getMessageHash()));
        assertEquals(List.of(MSG5), Message.getStoredMessages());
    }

    @Test
    void deletedMessageIsRemovedFromJsonFile() {
        Message.deleteByHash(m2.getMessageHash());
        Message.resetState();
        Message.loadStoredMessages();
        assertEquals(List.of(MSG5), Message.getStoredMessages());
    }

    @Test
    void deleteUnknownHashReportsNotFound() {
        assertEquals("Message hash not found.", Message.deleteByHash("XX:9:NOTHING"));
    }

    // Display report (sent messages)
    @Test
    void displayReportOfSentMessages() {
        String report = Message.printMessages();
        assertTrue(report.contains(m1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(MSG1));
        assertTrue(report.contains(m4.getMessageHash()));
        assertTrue(report.contains(MSG4));
        assertFalse(report.contains(MSG2));
    }

    // Display report (stored messages)
    @Test
    void displayReportOfStoredMessages() {
        String report = Message.storedMessagesReport();
        assertTrue(report.contains(m2.getMessageHash()));
        assertTrue(report.contains(MSG2));
        assertTrue(report.contains(MSG5));
        assertFalse(report.contains(MSG1));
    }

    // Sender and recipient of all stored messages
    @Test
    void displaySenderAndRecipientOfStoredMessages() {
        String result = Message.displaySenderAndRecipient("Danielle Nzadi");
        assertTrue(result.contains("Sender: Danielle Nzadi | Recipient: +27838884567"));
    }
}
