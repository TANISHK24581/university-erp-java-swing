package edu.univ.erp.data;

import edu.univ.erp.domain.User;
import edu.univ.erp.auth.PasswordUtil;

import java.sql.*;

public class AuthDB {

    private static final String AUTH_URL = "jdbc:mysql://localhost:3306/auth_db_2";
    private static final String ERP_URL  = "jdbc:mysql://localhost:3306/erp_db_2";
    private static final String USER = "root";
    private static final String PASSWORD = "TaNaKs@1006";

    public static Connection getAuthConnection() throws SQLException {
        return DriverManager.getConnection(AUTH_URL, USER, PASSWORD);
    }

    public static Connection getERPConnection() throws SQLException {
        return DriverManager.getConnection(ERP_URL, USER, PASSWORD);
    }
    public static boolean updatePasswordHash(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, role FROM users_auth WHERE username=?";
        try (Connection conn = getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";

        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users_auth WHERE username=?";
        try (Connection conn = getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void updatePassword(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash=? WHERE user_id=?";

        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {


        System.out.println("Seeding complete! You can now login with seeded users.");
    }
    public static String getUsernameById(int userId) {
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";

        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
