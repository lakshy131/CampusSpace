package com.example.campus_space.models;

public class ChatMessage {
    private String id;
    private String text;
    private String senderId;
    private long timestamp;
    private String type; // "text"
    private String timeFormatted;

    public ChatMessage() {}

    public ChatMessage(String id, String text, String senderId, long timestamp,
                       String type, String timeFormatted) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.type = type;
        this.timeFormatted = timeFormatted;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTimeFormatted() { return timeFormatted; }
    public void setTimeFormatted(String timeFormatted) { this.timeFormatted = timeFormatted; }
}
