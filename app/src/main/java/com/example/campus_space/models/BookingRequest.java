package com.example.campus_space.models;

public class BookingRequest {
    private String id;
    private String roomId;
    private String roomName;
    private String requestedBy;
    private String requesterRole; // "Faculty", "Club"
    private String purpose;
    private String date;
    private String time;
    private String status; // "Pending", "Approved", "Rejected"
    private String userId;

    public BookingRequest() {}

    public BookingRequest(String id, String roomId, String roomName, String requestedBy, String requesterRole,
                          String purpose, String date, String time, String status, String userId) {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.requestedBy = requestedBy;
        this.requesterRole = requesterRole;
        this.purpose = purpose;
        this.date = date;
        this.time = time;
        this.status = status;
        this.userId = userId;
    }

    public BookingRequest(String key, String name, String s, String s1, String purpose, String date, String time, String pending, String uid) {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public String getRequesterRole() { return requesterRole; }
    public void setRequesterRole(String requesterRole) { this.requesterRole = requesterRole; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
