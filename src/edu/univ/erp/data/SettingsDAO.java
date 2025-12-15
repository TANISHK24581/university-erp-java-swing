package edu.univ.erp.data;

import java.sql.*;
import java.time.LocalDate;

public class SettingsDAO {

    private static final String KEY_MAINTENANCE = "maintenance_mode";
    private static final String KEY_REG_DEADLINE = "register_deadline";
    private static final String KEY_DROP_DEADLINE = "drop_deadline";

    public static String getSetting(String key) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key=?";

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return rs.getString("setting_value");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setSetting(String key, String value) {

        String sql = """
            INSERT INTO settings (setting_key, setting_value)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)
        """;

        try (Connection conn = AuthDB.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, key);
            stmt.setString(2, value);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isMaintenanceOn() {
        String v = getSetting(KEY_MAINTENANCE);
        return "ON".equalsIgnoreCase(v);
    }

    public static boolean setMaintenance(boolean enabled) {
        return setSetting(KEY_MAINTENANCE, enabled ? "ON" : "OFF");
    }

    public static LocalDate getRegistrationDeadline() {
        String v = getSetting(KEY_REG_DEADLINE);
        return v == null ? null : LocalDate.parse(v);
    }

    public static boolean setRegistrationDeadline(LocalDate date) {
        return setSetting(KEY_REG_DEADLINE,
                date == null ? null : date.toString());
    }

    public static LocalDate getDropDeadline() {
        String v = getSetting(KEY_DROP_DEADLINE);
        return v == null ? null : LocalDate.parse(v);
    }

    public static boolean setDropDeadline(LocalDate date) {
        return setSetting(KEY_DROP_DEADLINE,
                date == null ? null : date.toString());
    }
}
