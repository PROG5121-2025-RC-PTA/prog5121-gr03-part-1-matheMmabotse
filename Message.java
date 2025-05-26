/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.thandiv2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
class Message {
    private final String sender;      // Sender's username or identifier
    private final String recipient;   // Recipient's phone number or username
    private final String content;     // Actual content of the message

    public Message(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    // Getter methods
    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    // Verifies that the message ID is within acceptable length (<= 10 characters)
    public static boolean checkMessageID(String messageID) {
        return messageID != null && messageID.length() <= 10;
    }

    // Validates recipient phone format: must start with '+' and be between 12-13 characters
    public static boolean checkRecipientCell(String phoneNumber) {
        return phoneNumber != null && phoneNumber.startsWith("+") && phoneNumber.length() >= 12 && phoneNumber.length() <= 13;
    }

    // Generates a hash in format: XX:Y FIRSTLAST (where XX are first 2 digits of hex hash, Y is message number, FIRST and LAST are first and last words)
    public String createMessageHash(int messageNumber) {
        // Generate hex hash from sender, recipient, and content
        String hexHash = Integer.toHexString(Math.abs((sender + recipient + content).hashCode()));
        
        // Extract first two characters (pad with 0 if needed)
        String firstTwoChars = hexHash.length() >= 2 ? hexHash.substring(0, 2) : (hexHash + "0").substring(0, 2);
        
        // Extract first and last words from content
        String[] words = content.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        // Format: XX:Y FIRSTLAST (all caps)
        return (firstTwoChars + ":" + messageNumber + " " + firstWord + lastWord).toUpperCase();
    }

    // Returns a string containing formatted info for all sent messages
    public static String printMessages(List<Message> messages) {
        if (messages.isEmpty()) return "No messages to display.";
        StringBuilder output = new StringBuilder("Sent Messages:\n");
        for (Message m : messages) {
            output.append("From: ").append(m.sender).append(" To: ").append(m.recipient)
                  .append(" Message: ").append(m.content).append("\n");
        }
        return output.toString();
    }

    // Returns the number of sent messages
    public static int returnTotalMessages(List<Message> messages) {
        return messages.size();
    }

    // Writes messages to a plain text file
    public static void saveMessagesToFile(List<Message> messages, String filename) {
        StringBuilder builder = new StringBuilder();
        for (Message m : messages) {
            builder.append("From: ").append(m.sender).append(" To: ").append(m.recipient)
                   .append(" Message: ").append(m.content).append(System.lineSeparator());
        }
        try {
            Files.write(Paths.get(filename), builder.toString().getBytes());
            JOptionPane.showMessageDialog(null, "Messages saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while saving messages.");
        }
    }

    // Writes messages in JSON-style format to a file
    public static void storeMessageJSON(List<Message> messages, String filename) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            json.append("  {")
                .append("\"sender\": \"").append(m.sender).append("\", ")
                .append("\"recipient\": \"").append(m.recipient).append("\", ")
                .append("\"content\": \"").append(m.content).append("\"}");
            if (i < messages.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");

        try {
            Files.write(Paths.get(filename), json.toString().getBytes());
            JOptionPane.showMessageDialog(null, "Messages stored in JSON format.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing messages as JSON.");
        }
    }
}