package edu.univ.erp.data;

import edu.univ.erp.domain.User;
import edu.univ.erp.auth.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static String createUserWithProfile(String username, String role, String plainPassword) {
        if (username == null || username.isEmpty()) return "Username required.";
        if (plainPassword == null || plainPassword.isEmpty()) return "Password required.";

        String hash = PasswordUtil.hashPassword(plainPassword);
        String insert = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'active')";

        try (Connection conn = AuthDB.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, hash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    // create profile in ERP DB accordingly
                    if ("Student".equals(role)) {
                        try (Connection erp = AuthDB.getERPConnection();
                             PreparedStatement ins = erp.prepareStatement("INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)")) {
                            ins.setInt(1, userId);
                            ins.setString(2, "ROLL" + userId);
                            ins.setString(3, "Undeclared");
                            ins.setInt(4, 1);
                            ins.executeUpdate();
                        }
                    } else if ("Instructor".equals(role)) {
                        try (Connection erp = AuthDB.getERPConnection();
                             PreparedStatement ins = erp.prepareStatement("INSERT INTO instructors (user_id, department) VALUES (?, ?)")) {
                            ins.setInt(1, userId);
                            ins.setString(2, "Unknown");
                            ins.executeUpdate();
                        }
                    }
                    return "OK";
                }
            }

        } catch (SQLIntegrityConstraintViolationException ex) {
            return "Username already exists.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return "Failed";
    }

    public static List<User> getAllUsers() {
        String sql = "SELECT user_id, username, role FROM users_auth ORDER BY username";
        List<User> out = new ArrayList<>();
        try (Connection conn = AuthDB.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}
