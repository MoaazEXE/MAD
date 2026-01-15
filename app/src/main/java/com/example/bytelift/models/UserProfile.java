package com.example.bytelift.models;

import com.google.firebase.Timestamp;

public class UserProfile {
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicUrl;
    private Timestamp createdAt;

    // Empty constructor required for Firestore
    public UserProfile() {
    }

    // Constructor with 5 parameters (the one we're using)
    public UserProfile(String userId, String username, String firstName, String lastName, String email) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicUrl = ""; // Default empty
        this.createdAt = Timestamp.now();
    }

    // Full constructor with all parameters
    public UserProfile(String userId, String username, String firstName, String lastName, String email, String profilePicUrl) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.createdAt = Timestamp.now();
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}