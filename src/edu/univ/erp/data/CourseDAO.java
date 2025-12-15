package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.*;
import java.util.*;

public class CourseDAO {

    public static List<Course> getAllCourses() {
        String sql = "SELECT course_id, code, title, credits FROM courses";
        List<Course> list = new ArrayList<>();

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getCourseName(int courseId) {
        String sql = "SELECT name FROM courses WHERE course_id = ?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }


    public static Course getCourseById(int id) {

        String sql = "SELECT course_id, code, title, credits FROM courses WHERE course_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addCourse(String code, String title, int credits) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCourse(int courseId, String code, String title, int credits) {
        String sql = "UPDATE courses SET code=?, title=?, credits=? WHERE course_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            stmt.setInt(4, courseId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getCourseTitle(int courseId) {
        String sql = "SELECT title FROM courses WHERE course_id = ?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                return (title == null || title.isBlank()) ? "Untitled" : title;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    public static String getCourseCode(int courseId) {
        String sql = "SELECT code FROM courses WHERE course_id = ?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String code = rs.getString("code");
                return (code == null || code.isBlank()) ? "???" : code;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "???";
    }

}
