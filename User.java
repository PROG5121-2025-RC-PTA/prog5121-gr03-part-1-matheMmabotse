
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.thandiv2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
class User {
    private final String username;               // Unique login username
    private final String password;               // User's login password
    private final String firstName;              // First name of the user
    private final String lastName;               // Last name of the user
    private final String phoneNumber;            // User's own phone number
    private final List<Message> messageData;     // Sent messages list
    private final List<Message> storedMessages;  // Stored but unsent messages list

    public User(String username, String password, String firstName, String lastName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.messageData = new ArrayList<>();
        this.storedMessages = new ArrayList<>();
    }

    public String getUsername() {
        return username;  // Returns user's unique login name
    }

    public String getFullName() {
        return firstName + " " + lastName;  // Combines first and last name
    }

    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);  // Checks login password match
    }

    public void sendMessage(Message message) {
        messageData.add(message);  // Adds message to sent list
    }

    public void storeMessage(Message message) {
        storedMessages.add(message);  // Adds message to stored list
    }

    public List<Message> getSentMessages() {
        return messageData;  // Retrieves list of sent messages
    }

    public List<Message> getStoredMessages() {
        return storedMessages;  // Retrieves list of stored messages
    }
}