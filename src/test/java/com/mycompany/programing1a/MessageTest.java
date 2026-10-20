package com.mycompany.programing1a;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // Fixed ID so the hash starts with "00", matching the test data
    private static final String FIXED_ID = "0012345678";

    private Message msg1; // Test Data for Message 1: Send
    private Message msg2; // Test Data for Message 2: Disregard

    @BeforeEach
    void setUp() {
        Message.resetState();
        msg1 = new Message(FIXED_ID, 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        msg2 = new Message(FIXED_ID, 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    @AfterEach
    void cleanUp() {
        new File(Message.JSON_FILE).delete();
    }

    // ---------- Message length ----------
    @Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.",
                Message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    void messageLengthFailure() {
        String longMsg = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10, please reduce the size.",
                Message.checkMessageLength(longMsg));
    }

    // ---------- Recipient number ----------
    @Test
    void recipientSuccess() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27718693002"));
    }

    @Test
    void recipientFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                        + "international code. Please correct the number and try again.",
                Message.checkRecipientCell("08575975889"));
    }

    // ---------- Message hash ----------
    @Test
    void hashTestCase1() {
        assertEquals("00:0:HITONIGHT", msg1.getMessageHash());
    }

    @Test
    void hashTestCase2() {
        assertEquals("00:1:HIPAYMENT", msg2.getMessageHash());
    }

    // ---------- Message ID ----------
    @Test
    void messageIDIsTenDigits() {
        String id = Message.generateMessageID();
        assertEquals(10, id.length());
        assertTrue(new Message(id, 0, "+27718693002", "Hello there").checkMessageID());
    }

    @Test
    void messageIDTooLongFails() {
        assertFalse(new Message("00123456789", 0, "+27718693002", "Hello there")
                .checkMessageID());
    }

    // ---------- Send / Disregard / Store ----------
    @Test
    void messageSent() {
        assertEquals("Message successfully sent.", msg1.sentMessage(1));
    }

    @Test
    void messageDisregarded() {
        assertEquals("Press 0 to delete the message.", msg2.sentMessage(2));
    }

    @Test
    void messageStored() {
        assertEquals("Message successfully stored.", msg1.sentMessage(3));
        assertTrue(new File(Message.JSON_FILE).exists());
    }

    // ---------- Totals / printing ----------
    @Test
    void totalMessagesSent() {
        msg1.sentMessage(1); // sent
        msg2.sentMessage(2); // disregarded, not counted
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessagesContainsSentMessage() {
        msg1.sentMessage(1);
        assertTrue(Message.printMessages().contains("00:0:HITONIGHT"));
    }
}
