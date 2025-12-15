package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.common.ChangePasswordDialog;
import edu.univ.erp.ui.common.UITheme;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.ui.common.LogoutHelper;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public static void styleAdminTable(JTable table) {

        // Enable sorting
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(new Color(59, 130, 246));   // same blue as student panel
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        table.setRowHeight(35);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
    }


    public AdminDashboard() {

        UITheme.applyBlueTheme();

        setTitle("Admin Dashboard - " + AuthService.getInstance().getUsername());
        setSize(1150, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        AdminService adminService = new AdminService();

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.setBackground(new Color(245, 247, 252));

        JLabel title = new JLabel("Admin Console");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(40, 60, 85));
        header.add(title, BorderLayout.WEST);

        // Change Password button
        JButton changePass = new JButton("Change Password");
        changePass.setFocusPainted(false);
        changePass.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));

// Logout button
        JButton logout = new JButton("Logout");
        logout.setFocusPainted(false);
        logout.addActionListener(e -> LogoutHelper.askAndLogout(this));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(changePass);
        right.add(logout);

        header.add(right, BorderLayout.EAST);


        // Maintenance banner (if ON)
        if ("ON".equalsIgnoreCase(SettingsDAO.getSetting("maintenance_mode"))) {
            JLabel banner = new JLabel("MAINTENANCE MODE ACTIVE — Students & Instructors are READ-ONLY");
            banner.setOpaque(true);
            banner.setBackground(new Color(0x1E73BE));
            banner.setForeground(Color.WHITE);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 14));
            banner.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            header.add(banner, BorderLayout.SOUTH);
        }

        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        tabs.addTab("Users", new UserPanel());
        tabs.addTab("Courses", new CoursePanel());
        tabs.addTab("Sections", new SectionPanel());
        tabs.addTab("Settings", new SettingsPanel(adminService));

        add(tabs, BorderLayout.CENTER);
    }



}
