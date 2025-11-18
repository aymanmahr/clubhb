package clubSystem;

import clubSystem.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Merged ClubSystem: loads data from storage (file or SQL) at startup,
 * exposes basic operations and also SQL-backed advanced operations when available.
 */
public class ClubSystem {
    private Datastorage storage;
    private List<Student> students = new ArrayList<>();
    private List<Club> clubs = new ArrayList<>();
    private List<ClubAdmin> admins = new ArrayList<>();

    public ClubSystem() {
        try {
           
            this.storage = new SQLDataStorage();
        } catch (Throwable t) {
            
            try { this.storage = new SQLDataStorage(); } catch (Throwable ex) { throw new RuntimeException("No storage available", ex); }
        }
        reloadFromStorage();
    }

    public void reloadFromStorage() {
        try {
            this.admins = storage.loadClubAdmins();
            this.students = storage.loadStudents();
            this.clubs = storage.loadClubs(admins);
        } catch (Exception e) {
            System.err.println("Error loading from storage: " + e.getMessage());
            this.admins = new ArrayList<>();
            this.students = new ArrayList<>();
            this.clubs = new ArrayList<>();
        }
    }

    // Basic getters for UI
    public List<Club> getAllClubs() { return new ArrayList<>(clubs); }
    public List<Student> getAllStudents() { return new ArrayList<>(students); }
    public List<ClubAdmin> getAllAdmins() { return new ArrayList<>(admins); }

    // Authentication
    public Student studentLogin(String email, String password) {
        for (Student s : students) if (s.authenticate(email, password)) return s;
        // fallback: try reload
        try {
            List<Student> fresh = storage.loadStudents();
            for (Student s : fresh) if (s.authenticate(email, password)) return s;
        } catch (Exception ignored) {}
        return null;
    }
    public ClubAdmin adminLogin(String email, String password) {
        for (ClubAdmin a : admins) if (a.authenticate(email, password)) return a;
        try {
            List<ClubAdmin> fresh = storage.loadClubAdmins();
            for (ClubAdmin a : fresh) if (a.authenticate(email, password)) return a;
        } catch (Exception ignored) {}
        return null;
    }

    // Registration / creation - updates in-memory list and persists
    public void registerStudent(String email, String phone, String password, String name) {
        Student s = new Student(email, phone, password, name);
        students.add(s);
        storage.saveStudent(s);
    }

    public void registerAdmin(ClubAdmin a) {
        admins.add(a);
        storage.saveAdmin(a);
    }
    

 
    public void updateStudent(Student s) {
        if (s == null) return;

       
        try {
           
            String pw = s.getPassword();
            if (pw != null && !pw.matches("[0-9a-fA-F]{64}")) {
                s.setPassword(clubSystem.util.HashUtil.sha256(pw));
            }
        } catch (Throwable t) {
          
        }

        
        try {
            storage.saveStudent(s);
        } catch (Throwable ex) {
            System.err.println("Failed to persist student to storage: " + ex.getMessage());
        }

        
        boolean replaced = false;
        for (int i = 0; i < students.size(); i++) {
            Student st = students.get(i);
            if (st.getEmail() != null && st.getEmail().equalsIgnoreCase(s.getEmail())) {
                students.set(i, s);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            students.add(s);
        }
    }



    public void registerClub(String name, String desc, String contactEmail, ClubAdmin admin) {
        String clubId = admin.getClubId();
        Club c = new Club(clubId, name, desc, contactEmail, admin);
        clubs.add(c);
        storage.saveClub(c);
    }

    public void submitInterest(Student student, Club club) {
        student.expressInterest(club);
        storage.saveStudentInterest(student, club);
    }

    // SQL advanced wrappers (direct cast)
    public int joinClub(String studentEmail, String clubCode) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).createMembershipByStudentEmail(studentEmail, clubCode); }
            catch (Exception e) { System.err.println("joinClub error: " + e.getMessage()); return -1; }
        }
        return -1;
    }

    public int createEvent(String adminEmail, clubSystem.Event e, String adminClubCode) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).createEventByAdminEmail(adminEmail, e, adminClubCode); }
            catch (Exception ex) { System.err.println("createEvent error: " + ex.getMessage()); return -1; }
        }
        return -1;
    }

    public int registerForEvent(String studentEmail, String eventCode) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).registerStudentForEvent(studentEmail, eventCode); }
            catch (Exception ex) { System.err.println("registerForEvent error: " + ex.getMessage()); return -1; }
        }
        return -1;
    }

    public int payDues(int memId, BigDecimal amount, String method) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).recordPaymentForMembership(memId, amount, method); }
            catch (Exception ex) { System.err.println("payDues error: " + ex.getMessage()); return -1; }
        }
        return -1;
    }

    public int postAnnouncement(String adminEmail, String clubCode, clubSystem.Announcement ann) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).postAnnouncementByAdmin(adminEmail, clubCode, ann); }
            catch (Exception ex) { System.err.println("postAnnouncement error: " + ex.getMessage()); return -1; }
        }
        return -1;
    }

    public List<String> viewEventRegistrations(String eventCode) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).getEventRegistrationsByEventCode(eventCode); } catch (Exception ex) { System.err.println(ex.getMessage()); }
        }
        return Collections.emptyList();
    }

    public List<Map<String,Object>> viewPayments(int memId) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).getPaymentsForMembership(memId); } catch (Exception ex) { System.err.println(ex.getMessage()); }
        }
        return Collections.emptyList();
    }

    public List<Map<String,Object>> viewMemberships(String email) {
        if (storage instanceof SQLDataStorage) {
            try { return ((SQLDataStorage) storage).getMembershipsByStudentEmail(email); } catch (Exception ex) { System.err.println(ex.getMessage()); }
        }
        return Collections.emptyList();
    }
}
