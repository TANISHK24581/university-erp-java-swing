package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoursePanel extends JPanel {
    private final AdminApi api = new AdminApi();
    private final JTable table = new JTable();

    public CoursePanel() {
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add Course");
        JButton edit = new JButton("Edit Course");

        buttons.add(refresh);
        buttons.add(add);
        buttons.add(edit);

        top.add(buttons, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadCourses());
        add.addActionListener(e -> showAdd());
        edit.addActionListener(e -> showEdit());


        loadCourses();
    }

    private void loadCourses() {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Loading courses..."));
            try {
                List<Course> list = api.getAllCourses();
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel m = new DefaultTableModel(new String[]{"CourseId","Code","Title","Credits"},0){
                        @Override public boolean isCellEditable(int r,int c){return false;}
                    };
                    for (Course c : list) m.addRow(new Object[]{c.getCourseId(), c.getCode(), c.getTitle(), c.getCredits()});
                    table.setModel(m);
                    AdminDashboard.styleAdminTable(table);

                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> DialogUtils.showError(this, "Failed: " + ex.getMessage()));
            } finally { SwingUtilities.invokeLater(DialogUtils::hideLoading); }
        }).start();
    }

    private void showAdd() {
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField credits = new JTextField("3");
        p.add(new JLabel("Code:")); p.add(code);
        p.add(new JLabel("Title:")); p.add(title);
        p.add(new JLabel("Credits:")); p.add(credits);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Course", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int cr = Integer.parseInt(credits.getText().trim());
            boolean res = api.addCourse(code.getText().trim(), title.getText().trim(), cr);
            DialogUtils.showInfo(this, res ? "Course Added Successfully" : "Add failed");
            loadCourses();
        } catch (NumberFormatException ex) {
            DialogUtils.showError(this, "Invalid credits.");
        }
    }
    private void showEdit() {

        int row = table.getSelectedRow();
        if (row == -1) {
            DialogUtils.showWarning(this, "Select a course to edit.");
            return;
        }

        int courseId = (int) table.getValueAt(row, 0);
        String existingCode = (String) table.getValueAt(row, 1);
        String existingTitle = (String) table.getValueAt(row, 2);
        int existingCredits = (int) table.getValueAt(row, 3);

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        JTextField code = new JTextField(existingCode);
        JTextField title = new JTextField(existingTitle);
        JTextField credits = new JTextField(String.valueOf(existingCredits));

        p.add(new JLabel("Code:")); p.add(code);
        p.add(new JLabel("Title:")); p.add(title);
        p.add(new JLabel("Credits:")); p.add(credits);

        int ok = JOptionPane.showConfirmDialog(
                this,
                p,
                "Edit Course",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int cr = Integer.parseInt(credits.getText().trim());

            boolean res = api.updateCourse(
                    courseId,
                    code.getText().trim(),
                    title.getText().trim(),
                    cr
            );

            DialogUtils.showInfo(this, res ? "Course updated successfully." : "Could not update course.");
            loadCourses();

        } catch (Exception ex) {
            DialogUtils.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

}
