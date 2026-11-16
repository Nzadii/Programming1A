# Programming 1A

## Registration, Login and QuickChat Messaging System

A Java console application built with Maven. It combines Part 1 (registration and login), Part 2 (sending messages) and Part 3 (arrays, stored messages and automated tests).

## Features

### Part 1: Registration and Login
- Username validation (contains an underscore, no more than five characters)
- Password complexity validation (8+ characters, a capital letter, a number and a special character)
- South African cellphone number validation (international code `+27`)
- User registration and login with status messages

### Part 2: QuickChat
- Messages can only be sent after a successful login
- Numeric menu with a welcome message
- Random 10-digit Message ID, recipient with international code, messages of up to 250 characters
- Auto-generated Message Hash (for example `00:0:HITONIGHT`)
- Send, Disregard or Store each message; stored messages are saved to `messages.json` using Gson

### Part 3: Arrays and Stored Messages
- Five arrays populated while the program runs: Sent Messages, Disregarded Messages, Stored Messages (read from the JSON file), Message Hashes and Message IDs
- Menu option 2 shows a report of the sent messages
- Menu option 4, Stored Messages, lets the user:
  1. display the sender and recipient of all stored messages
  2. display the longest stored message
  3. search for a message ID and display the recipient and message
  4. search for all messages sent or stored for a recipient
  5. delete a message using the message hash (the JSON file is updated too)
  6. display a report of all stored messages

## Project Structure

```
src/main/java/com/mycompany/programing1a/
    Login.java          Part 1: registration and login
    PROGRAMING1A.java   Main class (Part 1, then QuickChat after login)
    QuickChat.java      Menus and message flow
    Message.java        Message validation, hash, arrays, JSON storage, searching
src/test/java/com/mycompany/programing1a/
    MessageTest.java         Part 2 unit tests
    StoredMessagesTest.java  Part 3 unit tests
.github/workflows/maven.yml  GitHub Actions CI
```

## How to Run

1. Install JDK 26 and Maven (or open the project in NetBeans).
2. Build: `mvn clean package`
3. Run the class `com.mycompany.programing1a.PROGRAMING1A`.

## Unit Tests

Run all 27 JUnit tests with:

```
mvn test
```

Part 3 tests check that the arrays are populated correctly, the longest message, searching by message ID and by recipient, deleting by message hash, and the reports.

## Continuous Integration

GitHub Actions (`.github/workflows/maven.yml`) builds the project and runs all unit tests on every push and pull request.

## Test Data

**Part 1:** `kyl_1` / `Ch&&sec@ke99!` / `+27838968976`

**Part 3**

| Message | Recipient | Message | Flag |
|---|---|---|---|
| 1 | +27834557896 | Did you get the cake? | Sent |
| 2 | +27838884567 | Where are you? You are late! I have asked you to be on time. | Stored |
| 3 | +27834484567 | Yohoooo, I am at your gate. | Disregard |
| 4 | Developer 0838884567 | It is dinner time! | Sent |
| 5 | +27838884567 | Ok, I am leaving without you. | Stored |
