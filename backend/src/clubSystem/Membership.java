package clubSystem;

import java.time.LocalDateTime;

public class Membership {
    private int memId;
    private String memCode;
    private int studentUserId;
    private int clubDbId;
    private LocalDateTime joinedOn;
    private boolean duesPaid;
    private String status;
    public int getMemId() { return memId; }
    public void setMemId(int memId) { this.memId = memId; }
    public String getMemCode() { return memCode; }
    public void setMemCode(String memCode) { this.memCode = memCode; }
    public int getStudentUserId() { return studentUserId; }
    public void setStudentUserId(int studentUserId) { this.studentUserId = studentUserId; }
}
