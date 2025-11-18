package clubSystem;

import java.util.List;

public interface Datastorage {
    void saveStudent(Student student);
    void saveClub(Club club);
    void saveAdmin(ClubAdmin admin);
    List<Student> loadStudents();
    List<Club> loadClubs(List<ClubAdmin> admins);
    List<ClubAdmin> loadClubAdmins();
    void saveStudentInterest(Student student, Club club);
}



