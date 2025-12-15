package edu.univ.erp.ui.student;

import edu.univ.erp.ui.common.UITheme;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.ui.common.LogoutHelper;
import edu.univ.erp.ui.common.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    public StudentDashboard() {

        UITheme.applyBlueTheme();
        setTitle("Student Dashboard - " + AuthService.getInstance().getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // LEFT TITLE
        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton changePwd = new JButton("Change Password");
        changePwd.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> LogoutHelper.askAndLogout(this));

        rightButtons.add(changePwd);
        rightButtons.add(logoutBtn);

        header.add(rightButtons, BorderLayout.EAST);

        // MAINTENANCE BANNER (bottom of header)
        if (SettingsDAO.isMaintenanceOn()) {
            JLabel banner = new JLabel("MAINTENANCE MODE — System is read-only.");
            banner.setOpaque(true);
            banner.setBackground(new Color(0x1e73be));
            banner.setForeground(Color.WHITE);
            banner.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            header.add(banner, BorderLayout.SOUTH);
        }

        add(header, BorderLayout.NORTH);


        JTabbedPane tabs = new JTabbedPane();
        StudentApi api = new StudentApi();
        int studentId = AuthService.getInstance().getUserId();

        tabs.addTab("Catalog", new CatalogPanel(api, studentId));
        tabs.addTab("My Sections", new MySectionsPanel(api));
        tabs.addTab("Timetable", new TimetablePanel(api));
        tabs.addTab("Grades", new GradesPanel(api));

        add(tabs, BorderLayout.CENTER);
    }
}
