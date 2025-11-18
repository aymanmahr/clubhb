package clubSystem;

public class ClubAdmin extends User {
    private String clubId;  
    private Club club;        

    public ClubAdmin() { super(); }

    public ClubAdmin(String email, String phoneNumber, String password, String clubId) {
        super(email, phoneNumber, password);
        this.clubId = clubId;
    }

    public String getClubId() { return clubId; }
    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    // aliases (compatibility)
    public void setClubID(String clubId) { setClubId(clubId); }
    public String getClubCode() { return getClubId(); }
    public void setClubCode(String clubCode) { setClubId(clubCode); }

    public Club getClub() { return club; }
    public void setClub(Club club) {
        this.club = club;
        if (club != null && club.getAdmin() != this) { // keep both sides in sync
            club.setAdmin(this);
        }
    }

    // Basic authenticate implementation
    @Override
    public boolean authenticate(String email, String password) {
        if (getEmail() == null || getPassword() == null) return false;
        return getEmail().equalsIgnoreCase(email) && getPassword().equals(password);
    }
}
