package edu.univ.erp.ui.admin;

import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final JCheckBox maintenanceCheck;
    private final JTextField regDeadlineField;
    private final JTextField dropDeadlineField;

    public SettingsPanel(AdminService adminService) {

        setLayout(new GridBagLayout());
        setBackground(new Color(240, 243, 248));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(650, 480));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(15, 0, 15, 20);
        c.gridx = 0;
        c.gridy = 0;

        JLabel title = new JLabel("ERP Settings", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(30, 60, 130));

        GridBagConstraints titleC = new GridBagConstraints();
        titleC.gridx = 0;
        titleC.gridy = 0;
        titleC.gridwidth = 2;
        titleC.insets = new Insets(0, 0, 30, 0);
        card.add(title, titleC);


        // LABELS
        JLabel lblMaint = fieldLabel("Maintenance Mode:");
        JLabel lblReg = fieldLabel("Register Deadline (YYYY-MM-DD):");
        JLabel lblDrop = fieldLabel("Drop Deadline (YYYY-MM-DD):");

        // INPUTS
        maintenanceCheck = new JCheckBox("Enable");
        maintenanceCheck.setBackground(Color.WHITE);
        maintenanceCheck.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        regDeadlineField = inputField();
        dropDeadlineField = inputField();


        c.gridy++;
        c.gridx = 0;
        card.add(lblMaint, c);

        c.gridx = 1;
        card.add(maintenanceCheck, c);

        c.gridy++;
        c.gridx = 0;
        card.add(lblReg, c);

        c.gridx = 1;
        card.add(regDeadlineField, c);

        c.gridy++;
        c.gridx = 0;
        card.add(lblDrop, c);

        c.gridx = 1;
        card.add(dropDeadlineField, c);


        JButton saveBtn = new JButton("Save Settings");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(new Color(35, 105, 225));
        saveBtn.setFocusPainted(false);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));

        GridBagConstraints btnC = new GridBagConstraints();
        btnC.gridx = 0;
        btnC.gridy = c.gridy + 1;
        btnC.gridwidth = 2;
        btnC.insets = new Insets(30, 0, 0, 0);
        btnC.anchor = GridBagConstraints.CENTER;

        saveBtn.addActionListener(e -> {
            boolean ok1 = adminService.setMaintenanceMode(maintenanceCheck.isSelected());
            boolean ok2 = adminService.setRegisterDeadline(regDeadlineField.getText().trim());
            boolean ok3 = adminService.setDropDeadline(dropDeadlineField.getText().trim());

            if (ok1 && ok2 && ok3)
                DialogUtils.showInfo(this, "Settings saved successfully.");
            else
                DialogUtils.showError(this, "Failed to save settings.");
        });

        card.add(saveBtn, btnC);

        maintenanceCheck.setSelected(SettingsDAO.isMaintenanceOn());
        regDeadlineField.setText(SettingsDAO.getSetting("register_deadline"));
        dropDeadlineField.setText(SettingsDAO.getSetting("drop_deadline"));

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        add(card, outer);
    }



    private JLabel fieldLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        l.setForeground(new Color(70, 70, 70));
        return l;
    }

    private JTextField inputField() {
        JTextField tf = new JTextField(15);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }
}
