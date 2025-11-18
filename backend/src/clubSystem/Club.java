package clubSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class Club {
    private String clubId;
    private String name;
    private String description;
    private String contactEmail;
    private ClubAdmin admin;
    private final List<Student> interestedStudents;

    // default constructor required by some loaders
    public Club() {
        this.interestedStudents = new ArrayList<>();
    }

    public Club(String clubId, String name, String description, String contactEmail, ClubAdmin admin) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.contactEmail = contactEmail;
        this.admin = admin;
        this.interestedStudents = new ArrayList<>();
        if (this.admin != null) this.admin.setClub(this);
    }

    // getters / setters
    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    // aliases
    public void setClubCode(String code) { setClubId(code); }
    public String getClubCode() { return getClubId(); }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public ClubAdmin getAdmin() { return admin; }
    public void setAdmin(ClubAdmin admin) {
        this.admin = admin;
        if (admin != null && admin.getClub() != this) admin.setClub(this);
    }

    // interested students management
    public void addInterestedStudent(Student student) {
        if (student == null) return;
        if (!interestedStudents.contains(student)) interestedStudents.add(student);
    }

    public List<Student> getInterestedStudents() {
        return interestedStudents;
    }

    // update helpers used by admin menu UI (scanner-based overload)
    public void updateDetails(String name, String description, String contactEmail) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (contactEmail != null) this.contactEmail = contactEmail;
    }

    public void updateName(String name) { setName(name); }
    public void updateDescription(String description) { setDescription(description); }
    public void updateEmail(String contactEmail) { setContactEmail(contactEmail); }

    // interactive update (keeps old UI working)
    public void updateDetails(Scanner scanner) {
        System.out.println("Select the information to be updated: ");
        System.out.println("1. Club Name \n2. Club Description \n3. Contact Email \n4. All details");
        int choice = 1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) { choice = 1; }
        switch (choice) {
            case 1 -> {
                System.out.print("Enter new Name: ");
                String name = scanner.nextLine();
                updateName(name);
                System.out.println("Name updated.");
            }
            case 2 -> {
                System.out.print("Enter new description: ");
                String description = scanner.nextLine();
                updateDescription(description);
                System.out.println("Description updated.");
            }
            case 3 -> {
                System.out.print("Enter new Email: ");
                String email = scanner.nextLine();
                updateEmail(email);
                System.out.println("Email updated.");
            }
            case 4 -> {
                System.out.print("New Club Name: ");
                String name = scanner.nextLine();
                System.out.print("New Description: ");
                String desc = scanner.nextLine();
                System.out.print("New Contact Email: ");
                String email = scanner.nextLine();
                updateDetails(name, desc, email);
                System.out.println("Club updated.");
            }
            default -> System.out.println("Invalid choice. Please try again!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Club)) return false;
        Club c = (Club) o;
        return Objects.equals(getClubId(), c.getClubId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClubId());
    }
}
