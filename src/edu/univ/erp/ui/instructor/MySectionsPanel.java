package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MySectionsPanel extends JPanel {

    private final InstructorApi api = new InstructorApi();
    private final JTable table = new JTable();
    private final JButton refreshBtn = new JButton("Refresh");

    public MySectionsPanel() {

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JLabel title = new JLabel("My Sections");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        styleButton(refreshBtn);
        right.add(refreshBtn);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        add(scroll, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadSections());

        loadSections();
    }

    private void loadSections() {

        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(
                            SwingUtilities.getWindowAncestor(this),
                            "Loading your sections..."
                    )
            );

            try {
                List<Section> list = api.getMySections();
                SwingUtilities.invokeLater(() -> populateTable(list));

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showError(this, "Failed: " + ex.getMessage())
                );

            } finally {
                SwingUtilities.invokeLater(DialogUtils::hideLoading);
            }
        }).start();
    }

    private void populateTable(List<Section> list) {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{
                        "Section ID", "Course",
                        "Day/Time", "Room", "Capacity",
                        "Year", "Semester"
                },
                0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Section s : list) {

            String code = CourseDAO.getCourseCode(s.getCourseId());
            String title = CourseDAO.getCourseTitle(s.getCourseId());

            String courseDisplay = code + " — " + title;

            model.addRow(new Object[]{
                    s.getSectionId(),
                    courseDisplay,
                    s.getDayTime(),
                    s.getRoom(),
                    s.getCapacity(),
                    s.getYear(),
                    s.getSemester()
            });
        }

        table.setModel(model);

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(59, 130, 246));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setRowHeight(28);

        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(220, 220, 220));

        DialogUtils.makeTableSortable(table);
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(new Color(70, 120, 210));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
