package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;

import javax.swing.*;

public class DashboardService {

    public void openDashboard() {
        String role = AuthService.getInstance().getRole();
        JFrame dashboard = null;

        if (role == null) {
            JOptionPane.showMessageDialog(null, "No user in session", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (role) {
            case "Admin":
                dashboard = new AdminDashboard();
                break;
            case "Instructor":
                dashboard = new InstructorDashboard();
                break;
            case "Student":
                dashboard = new StudentDashboard();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (dashboard != null) dashboard.setVisible(true);
    }
}
