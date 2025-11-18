package clubSystem;

import java.time.LocalDateTime;

public class EventRegistration {
    private int regId;
    private String regCode;
    private int eventId;
    private int studentUserId;
    private LocalDateTime registrationDate;
    private boolean attendance;
    public int getRegId() { return regId; }
    public void setRegId(int regId) { this.regId = regId; }
    public String getRegCode() { return regCode; }
    public void setRegCode(String regCode) { this.regCode = regCode; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getStudentUserId() { return studentUserId; }
    public void setStudentUserId(int studentUserId) { this.studentUserId = studentUserId; }
}

