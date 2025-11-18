package clubSystem;

import java.time.LocalDateTime;

public class Announcement {
    private int annId;
    private String annCode;
    private int clubDbId;
    private String title;
    private String content;
    private LocalDateTime postedOn;
    public int getAnnId() { return annId; }
    public void setAnnId(int annId) { this.annId = annId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
