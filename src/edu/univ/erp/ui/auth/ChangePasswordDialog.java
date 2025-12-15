package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.AuthDB;
import edu.univ.erp.ui.common.DialogUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4,2,8,8));

        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();

        add(new JLabel("Current Password:"));
        add(oldPass);

        add(new JLabel("New Password:"));
        add(newPass);

        add(new JLabel("Confirm New:"));
        add(confirmPass);

        JButton save = new JButton("Update");
        add(new JLabel());
        add(save);

        save.addActionListener(e -> {
            String oldP = new String(oldPass.getPassword()).trim();
            String newP = new String(newPass.getPassword()).trim();
            String conf = new String(confirmPass.getPassword()).trim();

            if (oldP.isEmpty() || newP.isEmpty()) {
                DialogUtils.showWarning(this, "Please enter all fields.");
                return;
            }
            if (!newP.equals(conf)) {
                DialogUtils.showError(this, "New passwords do not match.");
                return;
            }

            int userId = AuthService.getInstance().getUserId();
            String username = AuthService.getInstance().getUsername();
            String hash = AuthDB.getPasswordHash(username);

            if (!BCrypt.checkpw(oldP, hash)) {
                DialogUtils.showError(this, "Current password is incorrect.");
                return;
            }

            String newHash = BCrypt.hashpw(newP, BCrypt.gensalt());
            AuthDB.updatePassword(userId, newHash);

            DialogUtils.showInfo(this, "Password updated successfully.");
            dispose();
        });
    }
}
