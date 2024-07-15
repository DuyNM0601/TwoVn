package com.example.twovn.model;

public class ChatMessage {
    private String messageId;
    private String senderId;
    private String message;
    private long timestamp;

    // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    public ChatMessage() {}

    public ChatMessage(String messageId, String senderId, String message) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = System.currentTimeMillis(); // Add timestamp when the message is created
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
