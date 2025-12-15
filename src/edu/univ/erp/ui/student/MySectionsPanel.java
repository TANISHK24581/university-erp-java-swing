package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.api.student.responses.TimetableRow;
import edu.univ.erp.api.student.utils.ApiResponse;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class MySectionsPanel extends JPanel {

    private final StudentApi studentApi;
    private JTable sectionTable;

    public MySectionsPanel(StudentApi studentApi) {
        this.studentApi = studentApi;
        setLayout(new BorderLayout());

        sectionTable = new JTable();
        add(new JScrollPane(sectionTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        JButton dropBtn = new JButton("Drop Selected Section");

        south.add(refreshBtn);
        south.add(dropBtn);

        add(south, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadSections());
        dropBtn.addActionListener(e -> dropSelected());

        loadSections();
    }

    private void loadSections() {
        ApiResponse<List<TimetableRow>> response = studentApi.viewTimetable();
        if (!response.isSuccess()) {
            DialogUtils.showError(this, response.getMessage());
            return;
        }

        List<TimetableRow> sections = response.getData();
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Section ID", "Course Code", "Title", "Day", "Time", "Room"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (TimetableRow s : sections) {
            model.addRow(new Object[]{s.getSectionId(), s.getCourseCode(), s.getCourseTitle(),
                    s.getDay(), s.getTime(), s.getRoom()});
        }

        sectionTable.setModel(model);
        JTableHeader header = sectionTable.getTableHeader();
        header.setOpaque(true);
//        header.setBackground(new Color(200, 220, 255));   // Soft light blue
//        header.setForeground(new Color(22, 45, 78));      // Navy text

        header.setBackground(new Color(59, 130, 246));  // button-family soft blue
        header.setForeground(Color.BLACK);




        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        sectionTable.setRowHeight(28);
        sectionTable.setAutoCreateRowSorter(true);

    }

    private void dropSelected() {
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            DialogUtils.showWarning(this, "Select a section first.");
            return;
        }

        int sectionId = (int) sectionTable.getValueAt(row, 0);

        var resp = studentApi.dropSection(
                new edu.univ.erp.api.student.requests.DropSectionRequest(sectionId)
        );

        if (resp.isSuccess()) {
            DialogUtils.showInfo(this, resp.getData());
            loadSections(); // refresh after drop
        } else {
            DialogUtils.showError(this, resp.getMessage());
        }
    }
}
