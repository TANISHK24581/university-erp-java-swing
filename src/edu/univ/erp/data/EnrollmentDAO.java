package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {


    public static Enrollment getEnrollmentById(int enrollmentId) {
        String sql = "SELECT enrollment_id, student_id, section_id, status FROM enrollments WHERE enrollment_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Enrollment> getEnrollmentsBySection(int sectionId) {

        String sql = "SELECT enrollment_id, student_id, section_id, status " +
                "FROM enrollments WHERE section_id=?";

        List<Enrollment> list = new ArrayList<>();

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public static List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT enrollment_id, student_id, section_id, status FROM enrollments WHERE student_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean isEnrolled(int studentId, int sectionId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id=? AND section_id=? AND status='active'";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return true; // safest fallback
        }
    }

    public static String enroll(int studentId, int sectionId) {

        try (Connection conn = AuthDB.getERPConnection()) {
            conn.setAutoCommit(false);

            // 1. Lock section
            PreparedStatement sec = conn.prepareStatement("""
            SELECT capacity,
                   (SELECT COUNT(*) FROM enrollments 
                    WHERE section_id=? AND status='active') AS enrolled
            FROM sections WHERE section_id=? FOR UPDATE
        """);
            sec.setInt(1, sectionId);
            sec.setInt(2, sectionId);
            ResultSet rs = sec.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return "Section not found";
            }

            int capacity = rs.getInt("capacity");
            int enrolled = rs.getInt("enrolled");

            if (enrolled >= capacity) {
                conn.rollback();
                return "Section full";
            }

            PreparedStatement droppedCheck = conn.prepareStatement(
                    "SELECT enrollment_id FROM enrollments WHERE student_id=? AND section_id=? AND status='dropped'"
            );
            droppedCheck.setInt(1, studentId);
            droppedCheck.setInt(2, sectionId);
            ResultSet drs = droppedCheck.executeQuery();

            if (drs.next()) {
                // 🔄 3. Reactivate instead of inserting a new row
                int enrollmentId = drs.getInt("enrollment_id");

                PreparedStatement reactivate = conn.prepareStatement(
                        "UPDATE enrollments SET status='active' WHERE enrollment_id=?"
                );
                reactivate.setInt(1, enrollmentId);
                reactivate.executeUpdate();

                conn.commit();
                return "Re-enrolled successfully";
            }

            // 4. Fresh enroll
            if (isEnrolled(studentId, sectionId)) {
                conn.rollback();
                return "Already enrolled";
            }

            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'active')"
            );
            insert.setInt(1, studentId);
            insert.setInt(2, sectionId);
            insert.executeUpdate();

            conn.commit();
            return "OK";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during enrollment";
        }
    }


    public static boolean drop(int studentId, int sectionId) {
        String sql = "UPDATE enrollments SET status='dropped' WHERE student_id=? AND section_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isAlreadyEnrolled(int studentId, int sectionId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id=? AND section_id=? AND status='active'";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean isSectionFull(int sectionId) {
        String sql = """
        SELECT 
            (SELECT COUNT(*) FROM enrollments WHERE section_id=? AND status='active') AS enrolled,
            (SELECT capacity FROM sections WHERE section_id=?) AS cap
    """;

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setInt(2, sectionId);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("enrolled") >= rs.getInt("cap");

        } catch (Exception e) { e.printStackTrace(); }
        return true; // safe fallback
    }

    public static boolean addEnrollment(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'active')";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static boolean dropEnrollment(int studentId, int sectionId) {
        String sql = "UPDATE enrollments SET status='dropped' WHERE student_id=? AND section_id=?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static String getStatus(int studentId, int sectionId) {
        String sql = "SELECT status FROM enrollments WHERE student_id=? AND section_id=?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("status");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // never enrolled
    }

    public static boolean reactivate(int studentId, int sectionId) {
        String sql = "UPDATE enrollments SET status='active' WHERE student_id=? AND section_id=?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
