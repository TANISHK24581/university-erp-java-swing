package edu.univ.erp.data;

import edu.univ.erp.domain.User;
import java.sql.*;

public class StudentDAO {

    public static boolean isStudent(int userId) {
        String sql = "SELECT 1 FROM students WHERE user_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static StudentProfile loadStudentProfile(int userId) {
        String sql = "SELECT roll_no, program, year FROM students WHERE user_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new StudentProfile(
                        userId,
                        rs.getString("roll_no"),
                        rs.getString("program"),
                        rs.getInt("year")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Inner simple profile class
    public static class StudentProfile {
        private final int userId;
        private final String rollNo;
        private final String program;
        private final int year;

        public StudentProfile(int userId, String rollNo, String program, int year) {
            this.userId = userId;
            this.rollNo = rollNo;
            this.program = program;
            this.year = year;
        }

        public int getUserId() { return userId; }
        public String getRollNo() { return rollNo; }
        public String getProgram() { return program; }
        public int getYear() { return year; }
    }
}
