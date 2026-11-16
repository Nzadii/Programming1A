# Programming 1A Submission

This repository contains the Programming 1A registration and login console application (Part 1) and the QuickChat messaging feature (Part 2).

## Completed Features

### Part 1: Registration and Login
- Username validation
- Password complexity validation
- South African cellphone validation
- User registration
- User login
- Login status messages

### Part 2: QuickChat
- Login required before messages can be sent
- Welcome message and numeric menu (Send Messages, Show recently sent messages, Quit)
- User sets how many messages to enter
- Message ID generation (random 10-digit number)
- Recipient validation (international code required)
- Message length validation (maximum 250 characters)
- Message Hash generation (for example `00:0:HITONIGHT`)
- Send, Disregard or Store each message
- JSON storage of stored messages (Gson)
- Full message details and total messages sent displayed
- `Message` class with `checkMessageID`, `checkRecipientCell`, `createMessageHash`, `sentMessage`, `printMessages`, `returnTotalMessages` and `storeMessage`
- JUnit unit tests (13 tests, all passing)
- GitHub Actions CI that builds the project and runs the tests on every push
- Work done on a feature branch and merged into `main` through a pull request

## Test Data

### Part 1
Username: kyl_1  
Password: Ch&&sec@ke99!  
Cell phone: +27838968976

### Part 2
Message 1: +27718693002, "Hi Mike, can you join us for dinner tonight?" (Send)  
Message 2: 08575975889 (rejected, no international code), then a valid number, "Hi Keegan, did you receive the payment?" (Disregard)

### Part 3: Arrays and Stored Messages
- Arrays for sent, disregarded and stored messages, message hashes and message IDs
- Stored messages read from the JSON file into an array
- Stored Messages menu: sender and recipient, longest message, search by message ID, search by recipient, delete by message hash, full report
- 27 JUnit unit tests, all passing
- GitHub Actions CI runs the tests on every push
- Work done on a feature branch and merged into `main` through a pull request
