package clubSystem;

import clubSystem.*;

import clubSystem.util.DBUtil;
import clubSystem.util.HashUtil;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;



public class SQLDataStorage implements Datastorage {

    public SQLDataStorage() {}

    private Integer findUserIdByEmail(String email, Connection conn) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
                else return null;
            }
        }
    }

    private Integer findClubDbIdByCode(String clubCode, Connection conn) throws SQLException {
        String sql = "SELECT club_id FROM clubs WHERE club_code = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, clubCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("club_id");
                else return null;
            }
        }
    }

    // Datastorage methods
    @Override
    public void saveStudent(Student student) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String upsertUser = "INSERT INTO users(email, phone, password, role) VALUES (?,?,?,?) " +
                        "ON CONFLICT (email) DO UPDATE SET phone = EXCLUDED.phone, password = EXCLUDED.password RETURNING user_id";
                try (PreparedStatement pst = conn.prepareStatement(upsertUser)) {
                    pst.setString(1, student.getEmail());
                    pst.setString(2, student.getPhoneNumber());
                    pst.setString(3, HashUtil.sha256(student.getPassword()));
                    pst.setString(4, "student");
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int userId = rs.getInt("user_id");
                            String upsertStudent = "INSERT INTO students(student_id, name, department, year) VALUES (?,?,?,?) " +
                                    "ON CONFLICT (student_id) DO UPDATE SET name = EXCLUDED.name, department = EXCLUDED.department, year = EXCLUDED.year";
                            try (PreparedStatement pst2 = conn.prepareStatement(upsertStudent)) {
                                pst2.setInt(1, userId);
                                pst2.setString(2, student.getName());
                                pst2.setString(3, student.getDepartment());
                                pst2.setInt(4, student.getYear());
                                pst2.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Error saving student: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("DB connection error in saveStudent: " + e.getMessage());
        }
    }

    @Override
    public void saveClub(Club club) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO clubs(club_code, clubname, description, contact_email) VALUES (?,?,?,?) " +
                    "ON CONFLICT (club_code) DO UPDATE SET clubname = EXCLUDED.clubname, description = EXCLUDED.description, contact_email = EXCLUDED.contact_email RETURNING club_id";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, club.getClubId());
                pst.setString(2, club.getName());
                pst.setString(3, club.getDescription());
                pst.setString(4, club.getContactEmail());
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int clubId = rs.getInt("club_id");
                        ClubAdmin admin = club.getAdmin();
                        if (admin != null) {
                            Integer adminUserId = findUserIdByEmail(admin.getEmail(), conn);
                            if (adminUserId != null) {
                                String mapSql = "INSERT INTO club_admin(admin_id, club_id, position) VALUES (?,?,?) ON CONFLICT (admin_id, club_id) DO UPDATE SET position = EXCLUDED.position";
                                try (PreparedStatement pst2 = conn.prepareStatement(mapSql)) {
                                    pst2.setInt(1, adminUserId);
                                    pst2.setInt(2, clubId);
                                    pst2.setString(3, "President");
                                    pst2.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving club: " + e.getMessage());
        }
    }

    @Override
    public void saveAdmin(ClubAdmin admin) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String upsert = "INSERT INTO users(email, phone, password, role) VALUES (?,?,?,?) " +
                        "ON CONFLICT (email) DO UPDATE SET phone = EXCLUDED.phone, password = EXCLUDED.password";
                try (PreparedStatement pst = conn.prepareStatement(upsert)) {
                    pst.setString(1, admin.getEmail());
                    pst.setString(2, admin.getPhoneNumber());
                    pst.setString(3, HashUtil.sha256(admin.getPassword()));
                    pst.setString(4, "club_admin");
                    pst.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Error saving admin: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("DB connection error in saveAdmin: " + e.getMessage());
        }
    }

    @Override
    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT u.email, u.phone, u.password, s.name, s.department, s.year FROM users u JOIN students s ON u.user_id = s.student_id";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(rs.getString("email"), rs.getString("phone"), rs.getString("password"), rs.getString("name"));
                    s.setDepartment(rs.getString("department"));
                    s.setYear(rs.getInt("year"));
                    students.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
        return students;
    }

    @Override
    public List<ClubAdmin> loadClubAdmins() {
        List<ClubAdmin> admins = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT u.email, u.phone, u.password, c.club_code FROM users u JOIN club_admin ca ON u.user_id = ca.admin_id JOIN clubs c ON ca.club_id = c.club_id";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ClubAdmin a = new ClubAdmin(rs.getString("email"), rs.getString("phone"), rs.getString("password"), rs.getString("club_code"));
                    admins.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading club admins: " + e.getMessage());
        }
        return admins;
    }

    @Override
    public List<Club> loadClubs(List<ClubAdmin> admins) {
        List<Club> clubs = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT club_code, clubname, description, contact_email, club_id FROM clubs";
            try (PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("club_code");
                    String name = rs.getString("clubname");
                    String desc = rs.getString("description");
                    String contact = rs.getString("contact_email");
                    ClubAdmin admin = admins.stream().filter(a -> code.equals(a.getClubId())).findFirst().orElse(null);
                    Club c = new Club(code, name, desc, contact, admin);
                    int clubId = rs.getInt("club_id");
                    String q2 = "SELECT u.email, u.phone, u.password FROM interests i JOIN users u ON i.student_id = u.user_id WHERE i.club_id = ?";
                    try (PreparedStatement pst2 = conn.prepareStatement(q2)) {
                        pst2.setInt(1, clubId);
                        try (ResultSet rs2 = pst2.executeQuery()) {
                            while (rs2.next()) {
                                Student s = new Student(rs2.getString("email"), rs2.getString("phone"), rs2.getString("password"), "");
                                c.addInterestedStudent(s);
                            }
                        }
                    }
                    clubs.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading clubs: " + e.getMessage());
        }
        return clubs;
    }

    @Override
    public void saveStudentInterest(Student student, Club club) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer uid = findUserIdByEmail(student.getEmail(), conn);
                if (uid == null) {
                    String ins = "INSERT INTO users(email, phone, password, role) VALUES (?,?,?,?) RETURNING user_id";
                    try (PreparedStatement pst = conn.prepareStatement(ins)) {
                        pst.setString(1, student.getEmail());
                        pst.setString(2, student.getPhoneNumber());
                        pst.setString(3, HashUtil.sha256(student.getPassword()));
                        pst.setString(4, "student");
                        try (ResultSet rs = pst.executeQuery()) {
                            if (rs.next()) uid = rs.getInt("user_id");
                            String insS = "INSERT INTO students(student_id, name) VALUES (?,?) ON CONFLICT (student_id) DO NOTHING";
                            try (PreparedStatement pst2 = conn.prepareStatement(insS)) {
                                pst2.setInt(1, uid);
                                pst2.setString(2, student.getName() == null ? "" : student.getName());
                                pst2.executeUpdate();
                            }
                        }
                    }
                }
                Integer clubDbId = findClubDbIdByCode(club.getClubId(), conn);
                if (clubDbId == null) {
                    String insClub = "INSERT INTO clubs(club_code, clubname, description, contact_email) VALUES (?,?,?,?) RETURNING club_id";
                    try (PreparedStatement pst = conn.prepareStatement(insClub)) {
                        pst.setString(1, club.getClubId());
                        pst.setString(2, club.getName());
                        pst.setString(3, club.getDescription());
                        pst.setString(4, club.getContactEmail());
                        try (ResultSet rs = pst.executeQuery()) {
                            if (rs.next()) clubDbId = rs.getInt("club_id");
                        }
                    }
                }
                String insInterest = "INSERT INTO interests(student_id, club_id) VALUES (?,?) ON CONFLICT (student_id, club_id) DO NOTHING";
                try (PreparedStatement pst = conn.prepareStatement(insInterest)) {
                    pst.setInt(1, uid);
                    pst.setInt(2, clubDbId);
                    pst.executeUpdate();
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Error saving interest: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("DB connection error in saveStudentInterest: " + e.getMessage());
        }
    }

    // --- advanced SQL methods (events, membership, payments, announcements) ---
    public int createMembershipByStudentEmail(String studentEmail, String clubCode) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer userId = findUserIdByEmail(studentEmail, conn);
                if (userId == null) throw new SQLException("Student not found: " + studentEmail);
                Integer clubDbId = findClubDbIdByCode(clubCode, conn);
                if (clubDbId == null) throw new SQLException("Club not found: " + clubCode);

                String sql = "INSERT INTO membership(student_id, club_id, mem_code) " +
                        "VALUES (?, ?, ('M' || lpad(nextval('seq_mem_code')::text,3,'0'))) " +
                        "ON CONFLICT (student_id, club_id) DO NOTHING RETURNING mem_id";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, userId);
                    pst.setInt(2, clubDbId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int memId = rs.getInt("mem_id");
                            conn.commit();
                            return memId;
                        } else {
                            String q = "SELECT mem_id FROM membership WHERE student_id = ? AND club_id = ?";
                            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                                pst2.setInt(1, userId);
                                pst2.setInt(2, clubDbId);
                                try (ResultSet rs2 = pst2.executeQuery()) {
                                    if (rs2.next()) {
                                        int memId = rs2.getInt("mem_id");
                                        conn.commit();
                                        return memId;
                                    }
                                }
                            }
                        }
                    }
                }
                conn.commit();
                return -1;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public int createEventByAdminEmail(String adminEmail, clubSystem.Event e, String adminClubCode) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer adminUserId = findUserIdByEmail(adminEmail, conn);
                if (adminUserId == null) throw new SQLException("Admin not found: " + adminEmail);
                Integer clubDbId = findClubDbIdByCode(adminClubCode, conn);
                if (clubDbId == null) throw new SQLException("Club not found: " + adminClubCode);

                String checkAdmin = "SELECT 1 FROM club_admin WHERE admin_id = ? AND club_id = ?";
                try (PreparedStatement pst = conn.prepareStatement(checkAdmin)) {
                    pst.setInt(1, adminUserId);
                    pst.setInt(2, clubDbId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) throw new SQLException("User is not admin of club: " + adminClubCode);
                    }
                }

                String sql = "INSERT INTO events(club_id, eventname, eventdate, location, maxcapacity, event_code) " +
                        "VALUES(?,?,?,?,?, ('E' || lpad(nextval('seq_event_code')::text,3,'0'))) RETURNING event_id";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, clubDbId);
                    pst.setString(2, e.getEventname());
                    if (e.getEventdate() != null) pst.setTimestamp(3, Timestamp.valueOf(e.getEventdate()));
                    else pst.setNull(3, Types.TIMESTAMP);
                    pst.setString(4, e.getLocation());
                    pst.setInt(5, e.getMaxcapacity());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int eventId = rs.getInt("event_id");
                            conn.commit();
                            return eventId;
                        } else throw new SQLException("Failed to create event");
                    }
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public int registerStudentForEvent(String studentEmail, String eventCode) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer studentId = findUserIdByEmail(studentEmail, conn);
                if (studentId == null) throw new SQLException("Student not found: " + studentEmail);

                String getEventSql = "SELECT event_id, maxcapacity FROM events WHERE event_code = ? FOR UPDATE";
                Integer eventId = null;
                Integer maxcap = null;
                try (PreparedStatement pst = conn.prepareStatement(getEventSql)) {
                    pst.setString(1, eventCode);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            eventId = rs.getInt("event_id");
                            maxcap = rs.getInt("maxcapacity");
                        } else throw new SQLException("Event not found: " + eventCode);
                    }
                }

                String countSql = "SELECT COUNT(*) FROM event_registration WHERE event_id = ?";
                int current = 0;
                try (PreparedStatement pst = conn.prepareStatement(countSql)) {
                    pst.setInt(1, eventId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) current = rs.getInt(1);
                    }
                }

                if (maxcap > 0 && current >= maxcap) {
                    conn.rollback();
                    return -1; // full
                }

                String ins = "INSERT INTO event_registration(event_id, student_id, reg_code) VALUES (?,?,('R' || lpad(nextval('seq_reg_code')::text,3,'0'))) " +
                        "ON CONFLICT (event_id, student_id) DO NOTHING RETURNING reg_id";
                try (PreparedStatement pst = conn.prepareStatement(ins)) {
                    pst.setInt(1, eventId);
                    pst.setInt(2, studentId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int regId = rs.getInt("reg_id");
                            conn.commit();
                            return regId;
                        } else {
                            conn.commit();
                            return -2; // already registered
                        }
                    }
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public int recordPaymentForMembership(int memId, BigDecimal amount, String method) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String q = "SELECT mem_id FROM membership WHERE mem_id = ? FOR UPDATE";
                try (PreparedStatement pst = conn.prepareStatement(q)) {
                    pst.setInt(1, memId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Membership not found: " + memId);
                    }
                }

                String ins = "INSERT INTO payment(mem_id, amount, payment_code, payment_date, payment_method) VALUES (?, ?, ('P' || lpad(nextval('seq_payment_code')::text,3,'0')), NOW(), ? ) RETURNING payment_id";
                try (PreparedStatement pst = conn.prepareStatement(ins)) {
                    pst.setInt(1, memId);
                    pst.setBigDecimal(2, amount);
                    pst.setString(3, method);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int paymentId = rs.getInt("payment_id");
                            String up = "UPDATE membership SET dues_paid = TRUE WHERE mem_id = ?";
                            try (PreparedStatement pst2 = conn.prepareStatement(up)) {
                                pst2.setInt(1, memId);
                                pst2.executeUpdate();
                            }
                            conn.commit();
                            return paymentId;
                        } else throw new SQLException("Payment insertion failed");
                    }
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public int postAnnouncementByAdmin(String adminEmail, String clubCode, clubSystem.Announcement ann) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Integer adminId = findUserIdByEmail(adminEmail, conn);
                if (adminId == null) throw new SQLException("Admin not found: " + adminEmail);
                Integer clubDbId = findClubDbIdByCode(clubCode, conn);
                if (clubDbId == null) throw new SQLException("Club not found: " + clubCode);
                String check = "SELECT 1 FROM club_admin WHERE admin_id = ? AND club_id = ?";
                try (PreparedStatement pst = conn.prepareStatement(check)) {
                    pst.setInt(1, adminId);
                    pst.setInt(2, clubDbId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) throw new SQLException("User is not admin of this club");
                    }
                }
                String ins = "INSERT INTO announcement(club_id, title, content, ann_code, posted_on) VALUES (?,?,?,?,NOW()) RETURNING ann_id";
                try (PreparedStatement pst = conn.prepareStatement(ins)) {
                    pst.setInt(1, clubDbId);
                    pst.setString(2, ann.getTitle());
                    pst.setString(3, ann.getContent());
                    pst.setString(4, "AN" + String.format("%03d", nextSeqVal("seq_ann_code", conn)));
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            int id = rs.getInt("ann_id");
                            conn.commit();
                            return id;
                        } else throw new SQLException("Failed to post announcement");
                    }
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    private long nextSeqVal(String seqName, Connection conn) {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT nextval('" + seqName + "')")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException ignore) {}
        return System.currentTimeMillis() % 100000;
    }

    public List<String> getEventRegistrationsByEventCode(String eventCode) throws SQLException {
        List<String> emails = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String q = "SELECT u.email FROM event_registration er JOIN users u ON er.student_id = u.user_id JOIN events e ON er.event_id = e.event_id WHERE e.event_code = ?";
            try (PreparedStatement pst = conn.prepareStatement(q)) {
                pst.setString(1, eventCode);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) emails.add(rs.getString("email"));
                }
            }
        }
        return emails;
    }

    public List<Map<String,Object>> getPaymentsForMembership(int memId) throws SQLException {
        List<Map<String,Object>> out = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String q = "SELECT payment_id, amount, payment_date, payment_method FROM payment WHERE mem_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(q)) {
                pst.setInt(1, memId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Map<String,Object> m = new HashMap<>();
                        m.put("payment_id", rs.getInt("payment_id"));
                        m.put("amount", rs.getBigDecimal("amount"));
                        m.put("date", rs.getTimestamp("payment_date"));
                        m.put("method", rs.getString("payment_method"));
                        out.add(m);
                    }
                }
            }
        }
        return out;
    }

    public List<Map<String,Object>> getMembershipsByStudentEmail(String studentEmail) throws SQLException {
        List<Map<String,Object>> out = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String q = "SELECT m.mem_id, m.mem_code, c.club_code, c.clubname, m.dues_paid, m.joined_on FROM membership m JOIN clubs c ON m.club_id = c.club_id JOIN users u ON m.student_id = u.user_id WHERE u.email = ?";
            try (PreparedStatement pst = conn.prepareStatement(q)) {
                pst.setString(1, studentEmail);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Map<String,Object> m = new HashMap<>();
                        m.put("mem_id", rs.getInt("mem_id"));
                        m.put("mem_code", rs.getString("mem_code"));
                        m.put("club_code", rs.getString("club_code"));
                        m.put("clubname", rs.getString("clubname"));
                        m.put("dues_paid", rs.getBoolean("dues_paid"));
                        m.put("joined_on", rs.getTimestamp("joined_on"));
                        out.add(m);
                    }
                }
            }
        }
        return out;
    }

    public boolean membershipExists(int memId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            String q = "SELECT 1 FROM membership WHERE mem_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(q)) {
                pst.setInt(1, memId);
                try (ResultSet rs = pst.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }
}
