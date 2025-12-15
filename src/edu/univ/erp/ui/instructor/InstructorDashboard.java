package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.common.ChangePasswordDialog;
import edu.univ.erp.ui.common.LogoutHelper;
import edu.univ.erp.ui.common.UITheme;
import javax.swing.table.JTableHeader;
import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {

    public InstructorDashboard() {

        UITheme.applyBlueTheme();
        setTitle("Instructor Dashboard - " + AuthService.getInstance().getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // LEFT TITLE
        JLabel title = new JLabel("Instructor Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        // RIGHT BUTTON BAR
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton changePwd = new JButton("Change Password");
        changePwd.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> LogoutHelper.askAndLogout(this));

        rightButtons.add(changePwd);
        rightButtons.add(logoutBtn);

        header.add(rightButtons, BorderLayout.EAST);

        // MAINTENANCE MODE BANNER
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
        tabs.addTab("My Sections", new MySectionsPanel());
        tabs.addTab("Gradebook", new GradebookPanel());
        tabs.addTab("Statistics", new StatsPanel());

        add(tabs, BorderLayout.CENTER);
    }
    public static void styleInstructorTable(JTable table) {

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);

        // Soft blue header
        header.setBackground(new Color(59, 130, 246));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Row height
        table.setRowHeight(28);

        table.setGridColor(new Color(220, 220, 220));
    }



}
