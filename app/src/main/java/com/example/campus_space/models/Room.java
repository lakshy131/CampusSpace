package com.example.campus_space.models;

public class Room {
    private String id;
    private String name;
    private String type; // "Classroom", "Lab", "Conference Room"
    private int capacity;
    private String status; // "Available", "Booked", "Pending"

    public Room() {}

    public Room(String id, String name, String type, int capacity, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
