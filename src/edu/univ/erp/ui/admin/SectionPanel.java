package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SectionPanel extends JPanel {
    private final AdminApi api = new AdminApi();
    private final JTable table = new JTable();
    private final JButton editBtn = new JButton("Edit Section");


    public SectionPanel() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add Section");
        JButton editBtn = new JButton("Edit Section");



        top.add(refresh);
        top.add(add);
        top.add(editBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadSections());
        add.addActionListener(e -> showAdd());
        editBtn.addActionListener(e -> editSelectedSection());  // <-- New

        loadSections();
    }


    private void loadSections() {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Loading sections..."));
            try {
                List<Section> list = api.getAllSections();
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel m = new DefaultTableModel(new String[]{"SectionId","CourseId","Course","InstructorId","DayTime","Room","Capacity","Semester","Year"},0){
                        @Override public boolean isCellEditable(int r,int c){return false;}
                    };
                    for (Section s : list) {
                        String title = CourseDAO.getCourseTitle(s.getCourseId());
                        m.addRow(new Object[]{s.getSectionId(), s.getCourseId(), title, s.getInstructorId(), s.getDayTime(), s.getRoom(), s.getCapacity(), s.getSemester(), s.getYear()});
                    }
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
        List<Course> courses = CourseDAO.getAllCourses();
        String[] courseItems = courses.stream().map(c -> c.getCourseId() + " - " + c.getCode()).toArray(String[]::new);
        JComboBox<String> courseBox = new JComboBox<>(courseItems);
        JTextField instructorId = new JTextField();
        JTextField dayTime = new JTextField();
        JTextField room = new JTextField();
        JTextField capacity = new JTextField("30");
        JTextField semester = new JTextField("Fall");
        JTextField year = new JTextField("2025");

        p.add(new JLabel("Course:")); p.add(courseBox);
        p.add(new JLabel("InstructorId:")); p.add(instructorId);
        p.add(new JLabel("Day/Time:")); p.add(dayTime);
        p.add(new JLabel("Room:")); p.add(room);
        p.add(new JLabel("Capacity:")); p.add(capacity);
        p.add(new JLabel("Semester:")); p.add(semester);
        p.add(new JLabel("Year:")); p.add(year);

        int ok = JOptionPane.showConfirmDialog(this, p, "Create Section", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String courseSel = (String) courseBox.getSelectedItem();
            int courseId = Integer.parseInt(courseSel.split(" - ")[0]);
            int iid = Integer.parseInt(instructorId.getText().trim());
            int cap = Integer.parseInt(capacity.getText().trim());
            int yr = Integer.parseInt(year.getText().trim());
            boolean res = api.addSection(courseId, iid, dayTime.getText().trim(), room.getText().trim(), cap, semester.getText().trim(), yr);
            DialogUtils.showInfo(this, res ? "Section created" : "Create failed");
            loadSections();
        } catch (Exception ex) {
            DialogUtils.showError(this, "Invalid input: " + ex.getMessage());
        }
    }
    private void editSelectedSection() {

        int rowView = table.getSelectedRow();
        if (rowView == -1) {
            DialogUtils.showWarning(this, "Please select a section to edit.");
            return;
        }

        int row = table.convertRowIndexToModel(rowView);
        DefaultTableModel m = (DefaultTableModel) table.getModel();

        int sectionId = (int) m.getValueAt(row, 0);
        int courseId = (int) m.getValueAt(row, 1);
        int instructorId = (int) m.getValueAt(row, 3);
        String dayTime = (String) m.getValueAt(row, 4);
        String room = (String) m.getValueAt(row, 5);
        int capacity = (int) m.getValueAt(row, 6);
        String semester = (String) m.getValueAt(row, 7);
        int year = (int) m.getValueAt(row, 8);

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));

        List<Course> courses = CourseDAO.getAllCourses();
        String[] courseItems = courses.stream()
                .map(c -> c.getCourseId() + " - " + c.getCode() + " : " + c.getTitle())
                .toArray(String[]::new);

        JComboBox<String> courseBox = new JComboBox<>(courseItems);

        for (int i = 0; i < courseItems.length; i++)
            if (courseItems[i].startsWith(courseId + " -"))
                courseBox.setSelectedIndex(i);

        JTextField instField = new JTextField(String.valueOf(instructorId));
        JTextField dayField = new JTextField(dayTime);
        JTextField roomField = new JTextField(room);
        JTextField capField = new JTextField(String.valueOf(capacity));
        JTextField semField = new JTextField(semester);
        JTextField yearField = new JTextField(String.valueOf(year));

        p.add(new JLabel("Course:")); p.add(courseBox);
        p.add(new JLabel("Instructor ID:")); p.add(instField);
        p.add(new JLabel("Day/Time:")); p.add(dayField);
        p.add(new JLabel("Room:")); p.add(roomField);
        p.add(new JLabel("Capacity:")); p.add(capField);
        p.add(new JLabel("Semester:")); p.add(semField);
        p.add(new JLabel("Year:")); p.add(yearField);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Section", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String courseSel = (String) courseBox.getSelectedItem();
            int newCourseId = Integer.parseInt(courseSel.split(" - ")[0]);
            int newInstructorId = Integer.parseInt(instField.getText().trim());
            int newCapacity = Integer.parseInt(capField.getText().trim());
            int newYear = Integer.parseInt(yearField.getText().trim());

            Section updated = new Section(
                    sectionId,
                    newCourseId,
                    newInstructorId,
                    dayField.getText().trim(),
                    roomField.getText().trim(),
                    newCapacity,
                    semField.getText().trim(),
                    newYear
            );

            boolean res = api.updateSection(updated);

            if (res) {
                DialogUtils.showInfo(this, "Section updated successfully.");
                loadSections();
            } else {
                DialogUtils.showError(this, "Could not save changes.");
            }

        } catch (Exception ex) {
            DialogUtils.showError(this, "Error: " + ex.getMessage());
        }
    }


}
