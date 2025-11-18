package clubSystem;

import java.time.LocalDateTime;

public class Event {
    private int eventId;
    private String eventCode;
    private int clubDbId;
    private String clubCode;
    private String eventname;
    private LocalDateTime eventdate;
    private String location;
    private int maxcapacity;

    public Event() {}
    public String getEventname() { return eventname; }
    public void setEventname(String eventname) { this.eventname = eventname; }
    public LocalDateTime getEventdate() { return eventdate; }
    public void setEventdate(LocalDateTime eventdate) { this.eventdate = eventdate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getMaxcapacity() { return maxcapacity; }
    public void setMaxcapacity(int maxcapacity) { this.maxcapacity = maxcapacity; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public int getClubDbId() { return clubDbId; }
    public void setClubDbId(int clubDbId) { this.clubDbId = clubDbId; }
    public String getClubCode() { return clubCode; }
    public void setClubCode(String clubCode) { this.clubCode = clubCode; }
}
