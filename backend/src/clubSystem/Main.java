package clubSystem;

import java.util.*;
import clubSystem.Event;
import clubSystem.Announcement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

/**
 * Simple console UI. Uses ClubSystem facade for actions.
 */
public class Main {
    private static ClubSystem sys = new ClubSystem();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Club Management ---");
            System.out.println("1. Register Student");
            System.out.println("2. Register Admin");
            System.out.println("3. Create Club");
            System.out.println("4. Express Interest");
            System.out.println("5. Join Club (Membership)");
            System.out.println("6. Create Event (Admin)");
            System.out.println("7. Register for Event");
            System.out.println("8. Post Announcement (Admin)");
            System.out.println("9. View Event Registrations");
            System.out.println("10. Pay Dues");
            System.out.println("11. View My Memberships");
            System.out.println("12. View Payments for Membership");
            System.out.println("13. Edit Profile (student)");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String ch = sc.nextLine();
            switch (ch) {
                case "1": registerStudent(); break;
                case "2": registerAdmin(); break;
                case "3": createClub(); break;
                case "4": expressInterest(); break;
                case "5": joinClub(); break;
                case "6": createEvent(); break;
                case "7": registerForEvent(); break;
                case "8": postAnnouncement(); break;
                case "9": viewEventRegistrations(); break;
                case "10": payDues(); break;
                case "11": viewMyMemberships(); break;
                case "12": viewPaymentsForMembership(); break;
                case "13": editProfile(); break;
                case "0": System.out.println("Bye"); System.exit(0);
                default: System.out.println("Invalid"); break;
            }
        }
    }

    private static void registerStudent() {
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Phone: "); String phone = sc.nextLine();
        System.out.print("Password: "); String pw = sc.nextLine();
        System.out.print("Name: "); String name = sc.nextLine();
        sys.registerStudent(email, phone, pw, name);
        System.out.println("Student registered.");
    }

    private static void registerAdmin() {
        System.out.print("Email: "); String e = sc.nextLine();
        System.out.print("Phone: "); String p = sc.nextLine();
        System.out.print("Password: "); String pw = sc.nextLine();
        System.out.print("Club Code (to map later): "); String cid = sc.nextLine();
        ClubAdmin a = new ClubAdmin(e, p, pw, cid);
        sys.registerAdmin(a);
        System.out.println("Admin (user) created.");
    }

    private static void createClub() {
        System.out.print("Club Code (e.g., C02): "); String code = sc.nextLine();
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Description: "); String desc = sc.nextLine();
        System.out.print("Contact Email: "); String contact = sc.nextLine();
        System.out.print("Admin Email: "); String adminEmail = sc.nextLine();
        ClubAdmin admin = new ClubAdmin(adminEmail, "", "", code);
        sys.registerClub(name, desc, contact, admin);
        System.out.println("Club created.");
    }

    private static void expressInterest() {
        System.out.print("Your Email: "); String email = sc.nextLine();
        System.out.print("Club Code: "); String code = sc.nextLine();
        Student s = new Student(email, "", "", "");
        Club c = new Club(code, "", "", "", null);
        sys.submitInterest(s, c);
        System.out.println("Interest recorded.");
    }

    private static void joinClub() {
        System.out.print("Your Email: "); String email = sc.nextLine();
        System.out.print("Club Code: "); String code = sc.nextLine();
        int memId = sys.joinClub(email, code);
        if (memId > 0) System.out.println("Membership created: id=" + memId);
        else System.out.println("Membership creation failed (maybe already member / storage not SQL).");
    }

    private static void createEvent() {
        System.out.print("Admin Email: "); String admin = sc.nextLine();
        System.out.print("Admin's Club Code: "); String clubcode = sc.nextLine();
        System.out.print("Event Name: "); String name = sc.nextLine();
        System.out.print("Event DateTime (yyyy-MM-dd HH:mm) : "); String when = sc.nextLine();
        System.out.print("Location: "); String loc = sc.nextLine();
        System.out.print("Max capacity (0 = unlimited): "); int cap = Integer.parseInt(sc.nextLine());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        clubSystem.Event e = new clubSystem.Event();
        e.setEventname(name);
        e.setEventdate(LocalDateTime.parse(when, fmt));
        e.setLocation(loc);
        e.setMaxcapacity(cap);
        int eventId = sys.createEvent(admin, e, clubcode);
        if (eventId > 0) System.out.println("Event created: id=" + eventId);
        else System.out.println("Failed to create event (SQL storage required).");
    }

    private static void registerForEvent() {
        System.out.print("Your Email: "); String email = sc.nextLine();
        System.out.print("Event Code (e.g., E001): "); String ecode = sc.nextLine();
        int r = sys.registerForEvent(email, ecode);
        if (r > 0) System.out.println("Registered: regId=" + r);
        else if (r == -1) System.out.println("Event is full.");
        else if (r == -2) System.out.println("Already registered.");
        else System.out.println("Failed to register (SQL storage required).");
    }

    private static void postAnnouncement() {
        System.out.print("Admin Email: "); String admin = sc.nextLine();
        System.out.print("Club Code: "); String clubcode = sc.nextLine();
        System.out.print("Title: "); String title = sc.nextLine();
        System.out.print("Content: "); String content = sc.nextLine();
        clubSystem.Announcement a = new clubSystem.Announcement();
        a.setTitle(title); a.setContent(content);
        int id = sys.postAnnouncement(admin, clubcode, a);
        if (id > 0) System.out.println("Posted announcement id=" + id);
        else System.out.println("Failed to post (SQL storage required).");
    }

    private static void viewEventRegistrations() {
        System.out.print("Event Code: "); String ecode = sc.nextLine();
        List<String> regs = sys.viewEventRegistrations(ecode);
        System.out.println("Registrations:");
        for (String em : regs) System.out.println(" - " + em);
    }

    private static void payDues() {
        System.out.print("Membership ID: "); int memId = Integer.parseInt(sc.nextLine());
        System.out.print("Amount: "); BigDecimal amt = new BigDecimal(sc.nextLine());
        System.out.print("Method: "); String method = sc.nextLine();
        int p = sys.payDues(memId, amt, method);
        if (p > 0) System.out.println("Payment recorded: id=" + p);
        else System.out.println("Payment failed (SQL storage required).");
    }

    private static void viewMyMemberships() {
        System.out.print("Your Email: "); String email = sc.nextLine();
        List<Map<String,Object>> list = sys.viewMemberships(email);
        for (Map<String,Object> m : list) {
            System.out.println("Membership ID: " + m.get("mem_id") + ", Club: " + m.get("club_code") + " - " + m.get("clubname") + ", DuesPaid: " + m.get("dues_paid"));
        }
    }

    private static void viewPaymentsForMembership() {
        System.out.print("Membership ID: "); int memId = Integer.parseInt(sc.nextLine());
        List<Map<String,Object>> ps = sys.viewPayments(memId);
        for (Map<String,Object> p : ps) {
            System.out.println("Payment: " + p.get("payment_id") + " amount=" + p.get("amount") + " date=" + p.get("date") + " method=" + p.get("method"));
        }
    }

    private static void editProfile() {
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pw = sc.nextLine();
        Student s = sys.studentLogin(email, pw);
        if (s == null) { System.out.println("Login failed."); return; }
        System.out.println("1. Change phone\n2. Change password\n3. Change name");
        String ch = sc.nextLine();
        switch (ch) {
        case "1" -> { System.out.print("New phone: "); s.setPhoneNumber(sc.nextLine()); sys.updateStudent(s); System.out.println("Phone updated."); }
        case "2" -> { System.out.print("New password: "); s.setPassword(sc.nextLine()); sys.updateStudent(s); System.out.println("Password updated."); }
        case "3" -> { System.out.print("New name: "); s.setName(sc.nextLine()); sys.updateStudent(s); System.out.println("Name updated."); }

            default -> System.out.println("Invalid."); 
        }
    }
}
