package edu.univ.erp.api.student;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.api.student.requests.*;
import edu.univ.erp.api.student.responses.*;
import edu.univ.erp.api.student.utils.ApiResponse;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.service.StudentService;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class StudentApi {

    public StudentApi() {}

    // 1. Browse Catalog
    public ApiResponse<List<CourseRow>> browseCatalog() {
        try {
            List<Course> courses = CourseDAO.getAllCourses();
            List<Section> allSections = SectionDAO.getAllSections();

            List<CourseRow> rows = courses.stream().map(c -> {
                int totalCapacity = 0;
                String instructorName = "-";

                for (Section s : allSections) {
                    if (s.getCourseId() == c.getCourseId()) {
                        totalCapacity += s.getCapacity();
                        if (s.getInstructorId() > 0 && instructorName.equals("-")) {
                            String temp = lookupUsernameById(s.getInstructorId());
                            if (temp != null) instructorName = temp;
                        }
                    }
                }

                return new CourseRow(
                        c.getCode(),
                        c.getTitle(),
                        c.getCredits(),
                        totalCapacity,
                        instructorName
                );
            }).collect(Collectors.toList());

            return ApiResponse.success(rows);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    // 2. Register
    // 2. Register (Uses StudentService)
    public ApiResponse<String> registerSection(RegisterSectionRequest request) {
        try {
            StudentService svc = new StudentService();

            // ALWAYS use logged-in user (enforced inside StudentService)
            String result = svc.registerSection(request.getSectionId());

            if (result.toLowerCase().contains("registered"))
                return ApiResponse.success(result);

            return ApiResponse.error(result);

        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }




    // 3. Drop
    public ApiResponse<String> dropSection(DropSectionRequest request) {
        try {
            StudentService svc = new StudentService();
            String result = svc.dropSection(request.getSectionId());

            if (result.startsWith("Section dropped"))
                return ApiResponse.success(result);

            return ApiResponse.error(result);

        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }


    // 4. Timetable
    // 4. Timetable
    public ApiResponse<List<TimetableRow>> viewTimetable() {
        try {
            int studentId = AuthService.getInstance().getUserId();

            List<Integer> sectionIds = EnrollmentDAO.getEnrollmentsByStudent(studentId)
                    .stream()
                    .filter(e -> "active".equalsIgnoreCase(e.getStatus()))
                    .map(Enrollment::getSectionId)
                    .toList();

            List<TimetableRow> list = new ArrayList<>();

            for (int secId : sectionIds) {

                Section s = SectionDAO.getSectionById(secId);
                if (s == null) continue;

                Course c = CourseDAO.getCourseById(s.getCourseId());

                String raw = s.getDayTime();
                if (raw == null) raw = "";

                // Example: "Mon Tues 10:00-11:00"
                String[] parts = raw.trim().split("\\s+");

                if (parts.length == 0) continue;

                // last token is always time (10:00-11:00)
                String time = parts[parts.length - 1];

                // All tokens except the last are days
                for (int i = 0; i < parts.length - 1; i++) {

                    String d = parts[i].toLowerCase();
                    String fullDay = switch (d) {
                        case "mon" -> "Monday";
                        case "tue", "tues", "tuesday" -> "Tuesday";
                        case "wed", "weds", "wednesday" -> "Wednesday";
                        case "thu", "thur", "thurs", "thursday" -> "Thursday";
                        case "fri", "friday" -> "Friday";
                        default -> null;
                    };

                    if (fullDay == null) continue;

                    // ADD separate row for each day
                    list.add(new TimetableRow(
                            s.getSectionId(),
                            c != null ? c.getCode() : "?",
                            c != null ? c.getTitle() : "?",
                            fullDay,
                            time,
                            s.getRoom()
                    ));
                }
            }

            return ApiResponse.success(list);

        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }


    // 5. Grades
    public ApiResponse<List<GradeRow>> viewGrades() {
        try {
            int studentId = AuthService.getInstance().getUserId();

            List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByStudent(studentId);
            List<GradeRow> rows = new ArrayList<>();

            for (Enrollment enr : enrollments) {
                if (!"active".equalsIgnoreCase(enr.getStatus())) continue;

                List<Grade> comps = GradeDAO.getGradesForEnrollment(enr.getEnrollmentId());

                double quiz = 0, mid = 0, end = 0;
                double finalNum = 0;

                for (Grade g : comps) {
                    String cmp = g.getComponent().toLowerCase();
                    if (cmp.contains("quiz")) quiz = g.getScore();
                    else if (cmp.contains("mid")) mid = g.getScore();
                    else if (cmp.contains("end")) end = g.getScore();

                    if (g.getFinalGrade() != null) {
                        try {
                            finalNum = Double.parseDouble(g.getFinalGrade());
                        } catch (NumberFormatException ignored) {}
                    }
                }

                Section s = SectionDAO.getSectionById(enr.getSectionId());
                Course c = CourseDAO.getCourseById(s.getCourseId());

                rows.add(new GradeRow(c.getCode(), quiz, mid, end, finalNum));
            }

            return ApiResponse.success(rows);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    // 6. Transcript
    public ApiResponse<String> downloadTranscript() {
        try {
            int studentId = AuthService.getInstance().getUserId();

            List<Enrollment> enrollments = EnrollmentDAO.getEnrollmentsByStudent(studentId);

            // CSV header
            StringBuilder sb = new StringBuilder("Course,Credits,Quiz,Midterm,EndSem,Final\n");

            for (Enrollment e : enrollments) {

                Section s = SectionDAO.getSectionById(e.getSectionId());
                Course c = CourseDAO.getCourseById(s.getCourseId());

                double quiz = 0, mid = 0, end = 0;
                String finalGrade = "";

                // READ ALL COMPONENTS
                for (Grade g : GradeDAO.getGradesForEnrollment(e.getEnrollmentId())) {

                    String cmp = g.getComponent().toLowerCase();

                    if (cmp.contains("quiz")) quiz = g.getScore();
                    else if (cmp.contains("mid")) mid = g.getScore();
                    else if (cmp.contains("end")) end = g.getScore();

                    if (g.getFinalGrade() != null)
                        finalGrade = g.getFinalGrade();
                }

                // Append row
                sb.append(c.getCode()).append(",")
                        .append(c.getCredits()).append(",")
                        .append(quiz).append(",")
                        .append(mid).append(",")
                        .append(end).append(",")
                        .append(finalGrade).append("\n");
            }

            return ApiResponse.success(sb.toString());

        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }


    // Helper
    private String lookupUsernameById(int id) {
        try (Connection c = AuthDB.getAuthConnection();
             PreparedStatement ps = c.prepareStatement("SELECT username FROM users_auth WHERE user_id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (Exception ignored) {}
        return null;
    }
}
