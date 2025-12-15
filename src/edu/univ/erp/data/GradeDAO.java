package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;

public class GradeDAO {

    public static List<Grade> getGradesByEnrollment(int enrollmentId) {
        String sql = "SELECT grade_id, enrollment_id, component, score, final_grade FROM grades WHERE enrollment_id=?";
        List<Grade> list = new ArrayList<>();

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Grade(
                        rs.getInt("grade_id"),
                        rs.getInt("enrollment_id"),
                        rs.getString("component"),
                        rs.getDouble("score"),
                        rs.getString("final_grade")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Grade> getGradesForEnrollment(int enrollmentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT grade_id, enrollment_id, component, score, final_grade FROM grades WHERE enrollment_id = ?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Grade(
                        rs.getInt("grade_id"),
                        rs.getInt("enrollment_id"),
                        rs.getString("component"),
                        rs.getDouble("score"),
                        rs.getString("final_grade")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public static boolean upsertGrade(int enrollmentId, String component, double score) {
        String select = "SELECT grade_id FROM grades WHERE enrollment_id=? AND component=?";
        String insert = "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?)";
        String update = "UPDATE grades SET score=? WHERE grade_id=?";

        try (Connection conn = AuthDB.getERPConnection()) {
            // try find
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setInt(1, enrollmentId);
                ps.setString(2, component);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int gradeId = rs.getInt("grade_id");
                        try (PreparedStatement ups = conn.prepareStatement(update)) {
                            ups.setDouble(1, score);
                            ups.setInt(2, gradeId);
                            ups.executeUpdate();
                            return true;
                        }
                    } else {
                        try (PreparedStatement ins = conn.prepareStatement(insert)) {
                            ins.setInt(1, enrollmentId);
                            ins.setString(2, component);
                            ins.setDouble(3, score);
                            ins.executeUpdate();
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Grade> getGradesBySection(int sectionId) {
        String sql = """
            SELECT g.grade_id, g.enrollment_id, g.component, g.score, g.final_grade
            FROM grades g
            JOIN enrollments e ON e.enrollment_id = g.enrollment_id
            WHERE e.section_id = ?
        """;

        List<Grade> out = new ArrayList<>();
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Grade(
                            rs.getInt("grade_id"),
                            rs.getInt("enrollment_id"),
                            rs.getString("component"),
                            rs.getDouble("score"),
                            rs.getString("final_grade")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static boolean setFinalGrade(int enrollmentId, String finalGrade) {
        String updateAll = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ?";
        String insertFinal = "INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES (?, 'Final', 0, ?)";

        try (Connection conn = AuthDB.getERPConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(updateAll)) {
                ps.setString(1, finalGrade);
                ps.setInt(2, enrollmentId);
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    return true;
                }
            }

            try (PreparedStatement ins = conn.prepareStatement(insertFinal)) {
                ins.setInt(1, enrollmentId);
                ins.setString(2, finalGrade);
                ins.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * Export CSV for a section: student_username, enrollment_id, component1:score,..., final_grade
     */
    public static String exportGradesCSV(int sectionId) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Username,EnrollmentId,Component,Score,FinalGrade");

        String sql = """
        SELECT u.username, e.enrollment_id, g.component, g.score, g.final_grade
        FROM erp_db.enrollments e
        JOIN auth_db.users_auth u ON u.user_id = e.student_id
        LEFT JOIN erp_db.grades g ON g.enrollment_id = e.enrollment_id
        WHERE e.section_id = ?
        ORDER BY u.username, e.enrollment_id
    """;

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pw.printf("%s,%d,%s,%.2f,%s%n",
                            rs.getString("username"),
                            rs.getInt("enrollment_id"),
                            rs.getString("component"),
                            rs.getDouble("score"),
                            rs.getString("final_grade"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pw.flush();
        return sw.toString();
    }


    public static boolean deleteGradesForEnrollment(int enrollmentId) {
        String sql = "DELETE FROM grades WHERE enrollment_id=?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





}
