package edu.univ.erp.ui.common;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.AuthDB;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    private final JPasswordField oldPwd = new JPasswordField(15);
    private final JPasswordField newPwd = new JPasswordField(15);
    private final JPasswordField confirmPwd = new JPasswordField(15);

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true);

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Current Password:"));
        form.add(oldPwd);

        form.add(new JLabel("New Password:"));
        form.add(newPwd);

        form.add(new JLabel("Confirm Password:"));
        form.add(confirmPwd);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton changeBtn = new JButton("Change");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(changeBtn);
        buttons.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dispose());
        changeBtn.addActionListener(e -> changePassword());
    }

    private void changePassword() {
        String oldPass = new String(oldPwd.getPassword());
        String newPass = new String(newPwd.getPassword());
        String confirmPass = new String(confirmPwd.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            DialogUtils.showWarning(this, "All fields are required.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            DialogUtils.showError(this, "New passwords do not match.");
            return;
        }

        // Get user info
        int userId = AuthService.getInstance().getUserId();
        String username = AuthService.getInstance().getUsername();
        String storedHash = AuthDB.getPasswordHash(username);

        if (storedHash == null || !BCrypt.checkpw(oldPass, storedHash)) {
            DialogUtils.showError(this, "Current password is incorrect.");
            return;
        }

        // Generate new password hash
        String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt());

        boolean ok = AuthDB.updatePasswordHash(userId, newHash);

        if (!ok) {
            DialogUtils.showError(this, "Password update failed!");
            return;
        }

        DialogUtils.showInfo(this, "Password changed successfully!");
        dispose();
    }
}
