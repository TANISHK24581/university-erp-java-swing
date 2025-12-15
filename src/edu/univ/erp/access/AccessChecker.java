package edu.univ.erp.access;

import edu.univ.erp.data.SettingsDAO;

public class AccessChecker {


    public static boolean isMaintenanceOn() {
        return SettingsDAO.isMaintenanceOn();
    }

    public static boolean canWrite(String role) {

        boolean maintenance = SettingsDAO.isMaintenanceOn();

        // Admin can always write
        if ("Admin".equalsIgnoreCase(role)) return true;

        // During maintenance, only Admin writes
        if (maintenance) return false;

        // Normal mode: Instructor & Student can write
        return "Instructor".equalsIgnoreCase(role)
                || "Student".equalsIgnoreCase(role);
    }
}
