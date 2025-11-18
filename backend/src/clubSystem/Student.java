package clubSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends User {
    private String name;
    private String department;    
    private int year;             
    private final List<Club> interestedClubs;

    public Student() {
        super();
        this.interestedClubs = new ArrayList<>();
    }

    public Student(String email, String phoneNumber, String password, String name) {
        super(email, phoneNumber, password);
        this.name = name;
        this.interestedClubs = new ArrayList<>();
    }

   
    public Student(String email, String phoneNumber, String password, String name, String department, int year) {
        super(email, phoneNumber, password);
        this.name = name;
        this.department = department;
        this.year = year;
        this.interestedClubs = new ArrayList<>();
    }

    // name getter/setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    

    // interested clubs
    public void expressInterest(Club club) {
        if (club == null) return;
        if (!interestedClubs.contains(club)) {
            interestedClubs.add(club);
            club.addInterestedStudent(this); 
        }
    }

    public void addInterestedClub(Club club) {
        if (club == null) return;
        if (!interestedClubs.contains(club)) interestedClubs.add(club);
    }

    public List<Club> getInterestedClubs() {
        return interestedClubs;
    }

    @Override
    public boolean authenticate(String email, String password) {
        if (getEmail() == null || getPassword() == null) return false;
        return getEmail().equalsIgnoreCase(email) && getPassword().equals(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return Objects.equals(getEmail(), s.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

    @Override
    public String toString() {
        return "Student{" +
                "email=" + getEmail() +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", year=" + year +
                '}';
    }
}
