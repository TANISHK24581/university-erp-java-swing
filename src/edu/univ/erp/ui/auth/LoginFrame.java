package edu.univ.erp.ui.auth;

import edu.univ.erp.ui.common.UITheme;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.service.DashboardService;
import edu.univ.erp.auth.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {

        UITheme.applyBlueTheme();

        setTitle("ERP Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 380);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 210, 210)),
                        BorderFactory.createEmptyBorder(65, 90, 65, 90)
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("University ERP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 90, 200));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;

        card.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        card.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        card.add(passwordField, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(new Color(30, 90, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        card.add(loginBtn, gbc);

        add(card);

        loginBtn.addActionListener(this::onLogin);
    }

    private void onLogin(ActionEvent e) {

        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            DialogUtils.showWarning(this, "Please enter username and password.");
            return;
        }

        new Thread(() -> {

            DialogUtils.showLoading(this, "Authenticating...");
            AuthService auth = AuthService.getInstance();

            boolean ok = auth.login(user, pass);

            SwingUtilities.invokeLater(() -> {
                DialogUtils.hideLoading();

                if (auth.isLocked()) {
                    DialogUtils.showError(
                            this,
                            "Login temporarily unavailable.\n" +
                                    "Please try again in " + auth.getRemainingSeconds() + " seconds."
                    );
                    return;
                }

                if (ok) {
                    DialogUtils.showInfo(this, "Login successful.");
                    new DashboardService().openDashboard();
                    dispose();
                    return;
                }

                int attemptsUsed = auth.getAttemptCount();
                int attemptsLeft = 5 - attemptsUsed;

                if (attemptsLeft > 0) {
                    DialogUtils.showWarning(
                            this,
                            "Incorrect username or password.\n" +
                                    "You have " + attemptsLeft + " more tries."
                    );
                } else {
                    DialogUtils.showError(
                            this,
                            "Too many incorrect attempts.\n" +
                                    "Please wait 1 minute before trying again."
                    );
                }
            });

        }).start();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
