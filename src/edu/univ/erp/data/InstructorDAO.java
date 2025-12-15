package edu.univ.erp.data;

import java.sql.*;

public class InstructorDAO {

    public static boolean isInstructor(int userId) {
        String sql = "SELECT 1 FROM instructors WHERE user_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static InstructorProfile loadInstructorProfile(int userId) {
        String sql = "SELECT department FROM instructors WHERE user_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new InstructorProfile(
                        userId,
                        rs.getString("department")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class InstructorProfile {
        private final int userId;
        private final String department;

        public InstructorProfile(int userId, String department) {
            this.userId = userId;
            this.department = department;
        }

        public int getUserId() { return userId; }
        public String getDepartment() { return department; }
    }
}
