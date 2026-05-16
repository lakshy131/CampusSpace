package com.example.campus_space.models;

public class Booking {
    private String id;
    private String roomId;
    private String roomName;
    private String userId;
    private String className;
    private String faculty;
    private String time;
    private String date;
    private String status; // "Confirmed", "Pending", "Cancelled"
    private String type; // "Class", "Event"
    private String roomType; // "Classroom", "Lab"
    private int capacity;

    public Booking() {}

    public Booking(String id, String roomId, String roomName, String userId, String className,
                   String faculty, String time, String date, String status, String type,
                   String roomType, int capacity) {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.userId = userId;
        this.className = className;
        this.faculty = faculty;
        this.time = time;
        this.date = date;
        this.status = status;
        this.type = type;
        this.roomType = roomType;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
