package com.example.campus_space.models;

public class Event {
    private String id;
    private String name;
    private String venue;
    private String time;
    private String date;
    private String organizer;
    private String dateBadge; // "Today", "Tomorrow", or date string
    private String borderColor; // hex color for left border

    public Event() {}

    public Event(String id, String name, String venue, String time, String date,
                 String organizer, String dateBadge, String borderColor) {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.time = time;
        this.date = date;
        this.organizer = organizer;
        this.dateBadge = dateBadge;
        this.borderColor = borderColor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getDateBadge() { return dateBadge; }
    public void setDateBadge(String dateBadge) { this.dateBadge = dateBadge; }
    public String getBorderColor() { return borderColor; }
    public void setBorderColor(String borderColor) { this.borderColor = borderColor; }
}
