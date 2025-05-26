/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.thandiv2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class Thandiv2 {
    private static final Map<String, User> users = new HashMap<>(); // Store registered users by username
    private static User currentUser = null; // Tracks the currently logged-in user

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome to Quickchat!"); // Greeting message

        boolean menu = true;
        while (menu) {
            if (currentUser == null) {
                // Menu for unauthenticated users (register or login)
                String choiceStr = JOptionPane.showInputDialog(null, "MENU\n1. Register\n2. Login\n3. Exit\nEnter your choice:");
                try {
                    int choice = Integer.parseInt(choiceStr); // Parse user input to integer
                    menu = userRegister(choice); // Handle unauthenticated menu actions
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                }
            } else {
                // Menu for authenticated users (send/view/store messages)
                String choiceStr = JOptionPane.showInputDialog(null, "MAIN MENU\n1. Send Messages\n2. Show recently sent messages\n3. Load stored messages\n4. Exit\nEnter your choice:");
                try {
                    int choice = Integer.parseInt(choiceStr); // Parse input to integer
                    menu = userLogin(choice); // Handle authenticated menu actions
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
                }
            }
        }
    }

    private static boolean userRegister(int choice) {
        switch (choice) {
            case 1 -> registerUser(); // Option to register new user
            case 2 -> loginUser();    // Option to log in
            case 3 -> {
                return false;         // Exit application
            }
            default -> JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
        }
        return true; // Continue showing menu loop
    }

    private static boolean userLogin(int choice) {
        switch (choice) {
            case 1 -> sendMessages();       // Send messages to phone numbers
            case 2 -> createMessageHash(); // Save a sent message to file
            case 3 -> loadStoredMessages(); // Load messages stored for later
            case 4 -> {
                return false;              // Exit application
            }
            default -> JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
        }
        return true; // Continue showing menu loop
    }

    private static void registerUser() {
        // Prompt and validate user registration details
        String username = JOptionPane.showInputDialog("Enter username:");
        if (!isValidUsername(username)) {
            JOptionPane.showMessageDialog(null, "Invalid username. Username must be no more than 5 characters and must contain an underscore.");
            return;
        }

        String password = JOptionPane.showInputDialog("Enter password:");
        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(null, "Invalid password. Password must be at least 8 characters long and contain at least one capital letter, one number, and one special character.");
            return;
        }

        String firstName = JOptionPane.showInputDialog("Enter first name:");
        String lastName = JOptionPane.showInputDialog("Enter last name:");
        String phoneNumber = JOptionPane.showInputDialog("Enter phone number (include international code +27):");

        if (!isValidPhoneNumber(phoneNumber)) {
            JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted. Please try again.");
            return;
        }

        // Add user to registered user map
        users.put(username, new User(username, password, firstName, lastName, phoneNumber));
        JOptionPane.showMessageDialog(null, "Welcome " + firstName + " " + lastName + ", it is great to see you.");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith("+") && phoneNumber.length() >= 12 && phoneNumber.length() <= 13 && phoneNumber.substring(1).matches("\\d+");
    }

    private static boolean isValidUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasCapital = false, hasNumber = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasCapital = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    private static void loginUser() {
        // Prompt user for login credentials
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");

        String loginMessage = returnLoginStatus(username, password);
        JOptionPane.showMessageDialog(null, loginMessage);

        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            currentUser = user; // Set logged in user
        }
    }

    private static String returnLoginStatus(String username, String password) {
        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            return "Welcome, " + user.getFullName() + "\nLogin successful. You are now connected to SimpleChat.";
        } else {
            return "Login failed. Invalid username or password. Please try again.";
        }
    }

    private static void sendMessages() {
        // Ask how many messages the user wants to send
        String countStr = JOptionPane.showInputDialog("How many messages would you like to send?");
        int messageCount;
        try {
            messageCount = Integer.parseInt(countStr);
            if (messageCount <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid number. Please enter a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number. Operation cancelled.");
            return;
        }

        // Send the specified number of messages
        for (int i = 1; i <= messageCount; i++) {
            // Prompt for recipient phone number (can be unregistered)
            String recipient = JOptionPane.showInputDialog("Enter phone number (with +) for message " + i + ":");
            
            // Validate phone number format
            if (!Message.checkRecipientCell(recipient)) {
                JOptionPane.showMessageDialog(null, "Invalid phone number format. Message " + i + " skipped.");
                continue;
            }

            // Prompt for message content
            String content = JOptionPane.showInputDialog("Enter message content for message " + i + ":");
            if (content == null || content.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Empty message. Message " + i + " skipped.");
                continue;
            }

            // Create new Message object
            Message msg = new Message(currentUser.getUsername(), recipient, content);

            // Provide options using input dialog with numbers
            String actionStr = JOptionPane.showInputDialog("Choose an action for message " + i + ":\n1. Send Message\n2. Discard Message\n3. Store Message\nEnter your choice (1-3):");
            
            try {
                int action = Integer.parseInt(actionStr);
                switch (action) {
                    case 1 -> {
                        currentUser.sendMessage(msg); // Send now
                        String messageHash = msg.createMessageHash(i);
                        JOptionPane.showMessageDialog(null, "Message " + i + " sent successfully!\nMessage Hash: " + messageHash);
                    }
                    case 2 -> JOptionPane.showMessageDialog(null, "Message " + i + " discarded.");
                    case 3 -> {
                        currentUser.storeMessage(msg); // Store to send later
                        JOptionPane.showMessageDialog(null, "Message " + i + " stored for later.");
                    }
                    default -> JOptionPane.showMessageDialog(null, "Invalid choice. Message " + i + " discarded.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Message " + i + " discarded.");
            }
        }
        
        JOptionPane.showMessageDialog(null, "Message sending session completed!");
    }

    private static void createMessageHash() {
        // Save the current user's sent messages to a file
        List<Message> messages = currentUser.getSentMessages();
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to save.");
            return;
        }
        
        StringBuilder builder = new StringBuilder();
        for (Message m : messages) {
            builder.append(m.toString()).append(System.lineSeparator());
        }

        // Attempt to write messages to a file named "sentMessages.txt"
        try {
            Files.write(Paths.get("sentMessages.txt"), builder.toString().getBytes());
            JOptionPane.showMessageDialog(null, "Messages saved successfully to sentMessages.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while saving messages.");
        }
    }

    private static void loadStoredMessages() {
        // Get stored messages from current user
        List<Message> stored = currentUser.getStoredMessages();

        // Display each stored message
        if (stored.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stored messages to display.");
        } else {
            StringBuilder output = new StringBuilder("Stored Messages:\n");
            for (int i = 0; i < stored.size(); i++) {
                Message m = stored.get(i);
                output.append((i + 1)).append(". To: ").append(m.getRecipient())
                      .append(" - ").append(m.getContent()).append("\n");
            }
            JOptionPane.showMessageDialog(null, output.toString());
        }
    }
}