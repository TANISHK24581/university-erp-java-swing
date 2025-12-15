package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {
    private final AdminApi api = new AdminApi();
    private final JTable table = new JTable();

    public UserPanel() {
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add User");
        top.add(refresh);
        top.add(add);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadUsers());
        add.addActionListener(e -> showAddDialog());

        loadUsers();
    }

    private void loadUsers() {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Loading users..."));
            try {
                List<User> users = api.listUsers();
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel m = new DefaultTableModel(new String[]{"UserId","Username","Role"},0) {
                        @Override public boolean isCellEditable(int r,int c){return false;}
                    };
                    for (User u : users) m.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole()});
                    table.setModel(m);
                    AdminDashboard.styleAdminTable(table);


                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> DialogUtils.showError(this, "Failed: " + ex.getMessage()));
            } finally { SwingUtilities.invokeLater(DialogUtils::hideLoading); }
        }).start();
    }

    private void showAddDialog() {
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        JTextField userF = new JTextField();
        JComboBox<String> roleC = new JComboBox<>(new String[]{"Student","Instructor","Admin"});
        JPasswordField passF = new JPasswordField();

        p.add(new JLabel("Username:")); p.add(userF);
        p.add(new JLabel("Role:")); p.add(roleC);
        p.add(new JLabel("Password:")); p.add(passF);

        int ok = JOptionPane.showConfirmDialog(this, p, "Create User", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String u = userF.getText().trim();
        String role = (String) roleC.getSelectedItem();
        String pass = new String(passF.getPassword());

        new Thread(() -> {
            SwingUtilities.invokeLater(() -> DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Creating user..."));
            try {
                String res = api.createUser(u, role, pass);
                SwingUtilities.invokeLater(() -> {
                    if (res.equalsIgnoreCase("OK")) {
                        DialogUtils.showInfo(this, "User created successfully.");
                    } else {
                        DialogUtils.showError(this, res);
                    }

                    loadUsers();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> DialogUtils.showError(this, "Failed: " + ex.getMessage()));
            } finally { SwingUtilities.invokeLater(DialogUtils::hideLoading); }
        }).start();
    }
}
