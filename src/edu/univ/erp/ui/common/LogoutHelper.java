package edu.univ.erp.ui.common;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.ui.auth.LoginFrame;

import javax.swing.*;

public class LogoutHelper {

    public static void askAndLogout(JFrame parent) {
        int choice = JOptionPane.showConfirmDialog(
                parent,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            parent.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
