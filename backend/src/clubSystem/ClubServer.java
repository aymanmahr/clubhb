package clubSystem;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClubServer {

    private static final ClubSystem clubSystem = new ClubSystem();
    private static final Datastorage storage = new SQLDataStorage();

    public static void main(String[] args) {
        System.out.println("ClubHub Server running at http://localhost:8080");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String request = in.readLine();
            if (request == null || request.isEmpty()) return;

            String[] parts = request.split(" ");
            if (parts.length < 2) return;

            String method = parts[0];
            String path = parts[1];

            // Skip headers
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {}

            // Read body
            char[] bodyChars = new char[2048];
            int len = in.read(bodyChars);
            String body = len > 0 ? new String(bodyChars, 0, len) : "";

            Map<String, String> params = new HashMap<>();
            for (String param : body.split("&")) {
                String[] kv = param.split("=");
                if (kv.length == 2) {
                    params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                }
            }

            String response;

            switch (path) {
                case "/student/register":
                    clubSystem.registerStudent(params.get("email"), params.get("phone"), params.get("password"), params.get("name"));
                    response = "Student registered successfully.";
                    break;

                case "/admin/register":
                    ClubAdmin admin = new ClubAdmin(params.get("email"), params.get("phone"), params.get("password"), params.get("clubId"));
                    clubSystem.registerAdmin(admin);
                    clubSystem.registerClub(params.get("clubName"), params.get("description"), params.get("contactEmail"), admin);
                    response = "Admin and club registered successfully.";
                    break;

                case "/student/login":
                    Student s = clubSystem.studentLogin(params.get("email"), params.get("password"));
                    response = (s != null) ? "success:student" : "Invalid student credentials.";
                    break;

                case "/admin/login":
                    ClubAdmin a = clubSystem.adminLogin(params.get("email"), params.get("password"));
                    response = (a != null) ? "success:admin:" + a.getClubId() : "Invalid admin credentials.";
                    break;

                case "/interest":
                    response = expressInterest(params.get("clubId"), params.get("email"));
                    break;

                case "/view_interests":
                    response = viewInterested(params.get("clubId"));
                    break;

//                case "/view_clubs":
//                    response = viewClubs();
//                    break;

                case "/edit_student":
                    response = updateDetails("students.txt", params);
                    break;

                case "/edit_admin":
                    response = updateDetails("admins.txt", params);
                    break;

                case "/update_club":
                    response = updateClubInfo(params.get("clubId"), params.get("clubName"), params.get("description"), params.get("contactEmail"));
                    break;

                default:
                    response = "Unknown path.";
            }

            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("Access-Control-Allow-Origin: *\r\n");
            out.write("Content-Length: " + response.length() + "\r\n");
            out.write("\r\n");
            out.write(response);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String expressInterest(String clubId, String studentEmail) {
        Club club = clubSystem.getAllClubs().stream()
                .filter(c -> c.getClubId().equals(clubId))
                .findFirst().orElse(null);
        if (club == null) {
            return "Club not found.";
        }

        Student student = clubSystem.studentLogin(studentEmail, null);
        if (student == null) {
            return "Student not found.";
        }

        clubSystem.submitInterest(student, club);
        return "Interest expressed successfully.";
    }

    private static String viewInterested(String clubId) {
        StringBuilder result = new StringBuilder("Interested students:\n");
        try (BufferedReader reader = new BufferedReader(new FileReader("interests.txt"))) {
            String line;
            boolean found = false;
            List<Student> students = clubSystem.getAllClubs().stream()
                    .flatMap(c -> c.getInterestedStudents().stream())
                    .distinct()
                    .toList();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[1].equals(clubId)) {
                    String studentEmail = parts[0];
                    String studentName = students.stream()
                            .filter(s -> s.getEmail().equals(studentEmail))
                            .map(Student::getName)
                            .findFirst().orElse(studentEmail);
                    result.append("- ").append(studentName).append("\n");
                    found = true;
                }
            }
            return found ? result.toString() : "No students have shown interest yet.";
        } catch (IOException e) {
            return "Error reading interests.txt: " + e.getMessage();
        }
    }

//    private static String viewClubs() {
//        List<Club> clubs = clubSystem.getAllClubs();
//        if (clubs.isEmpty()) {
//            return "No clubs available.";
//        }
//        StringBuilder result = new StringBuilder();
//        for (Club club : clubs) {
//            result.append(club.getClubId()).append("|")
//                  .append(club.getName()).append("|")
//                  .append(club.getDescription()).append("\n");
//        }
//        return result.toString();
//    }

    private static String updateDetails(String fileName, Map<String, String> data) {
        File original = new File(fileName);
        File temp = new File("temp_" + fileName);
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(original));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(data.get("email"))) {
                    writer.write(data.get("email") + "," + data.get("phone") + "," + data.get("password") + "," + data.get("name") + "\n");
                    updated = true;
                } else {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            return "Error editing details: " + e.getMessage();
        }

        if (updated) {
            if (original.delete() && temp.renameTo(original)) {
                return "Details updated successfully.";
            } else {
                temp.delete();
                return "Error updating file.";
            }
        } else {
            temp.delete();
            return "User not found.";
        }
    }

    private static String updateClubInfo(String clubId, String name, String desc, String email) {
        if (clubId == null || name == null || desc == null || email == null) {
            return "Missing required parameters.";
        }
        File inputFile = new File("clubs.txt");
        File tempFile = new File("clubs_temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(clubId)) {
                    writer.write(clubId + "," + name + "," + desc + "," + email + "," + parts[4] + "\n");
                    updated = true;
                } else {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            tempFile.delete();
            return "Error updating club info: " + e.getMessage();
        }

        if (updated) {
            if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                return "Club information updated successfully.";
            } else {
                tempFile.delete();
                return "Error updating club file.";
            }
        } else {
            tempFile.delete();
            return "Club not found.";
        }
    }
}
