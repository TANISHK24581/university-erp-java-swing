package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import java.sql.*;
import java.util.*;

public class SectionDAO {

    public static List<Section> getAllSections() {
        String sql = """
            SELECT section_id, course_id, instructor_id,
                   day_time, room, capacity, semester, year
            FROM sections
        """;

        List<Section> list = new ArrayList<>();

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Section getSectionById(int sectionId) {
        String sql = """
            SELECT section_id, course_id, instructor_id,
                   day_time, room, capacity, semester, year
            FROM sections WHERE section_id=?
        """;

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean createSection(int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, instructorId);
            ps.setString(3, dayTime);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setString(6, semester);
            ps.setInt(7, year);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean updateSection(Section s) {
        String sql = "UPDATE sections SET course_id=?, instructor_id=?, day_time=?, room=?, capacity=?, semester=?, year=? WHERE section_id=?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getCourseId());
            ps.setInt(2, s.getInstructorId());
            ps.setString(3, s.getDayTime());
            ps.setString(4, s.getRoom());
            ps.setInt(5, s.getCapacity());
            ps.setString(6, s.getSemester());
            ps.setInt(7, s.getYear());
            ps.setInt(8, s.getSectionId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id=?";
        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }



}
