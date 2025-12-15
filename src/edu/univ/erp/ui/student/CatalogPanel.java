package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.api.student.responses.CourseRow;
import edu.univ.erp.api.student.utils.ApiResponse;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Section;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.JTableHeader;


public class CatalogPanel extends JPanel {

    private final StudentApi studentApi;
    private final int studentId;
    private final JTable courseTable = new JTable();

    public CatalogPanel(StudentApi api, int studentId) {
        this.studentApi = api;
        this.studentId = studentId;

        setLayout(new BorderLayout(8,8));
        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerBtn = new JButton("Register Selected");
        south.add(registerBtn);
        add(south, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerSelected());
        loadCourses();
    }

    private void loadCourses() {
        ApiResponse<List<CourseRow>> resp = studentApi.browseCatalog();
        if (!resp.isSuccess()) {
            DialogUtils.showError(this, resp.getMessage());
            return;
        }

        List<CourseRow> list = resp.getData();
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Code","Title","Credits","Capacity","Instructor"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (CourseRow row : list) {
            model.addRow(new Object[]{
                    row.getCode(),
                    row.getTitle(),
                    row.getCredits(),
                    row.getCapacity(),
                    row.getInstructor()
            });
        }

        courseTable.setModel(model);
        courseTable.setAutoCreateRowSorter(true);


// --- STYLE HEADER ---
        JTableHeader header = courseTable.getTableHeader();
        header.setOpaque(true);
//        header.setBackground(new Color(200, 220, 255));   // Soft light blue
//        header.setForeground(new Color(22, 45, 78));      // Navy text

        header.setBackground(new Color(59, 130, 246));
        header.setForeground(Color.BLACK);




        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        courseTable.setRowHeight(28);

    }

    private void registerSelected() {
        int selected = courseTable.getSelectedRow();
        if (selected == -1) {
            DialogUtils.showWarning(this, "Select a course first.");
            return;
        }

        String courseCode = courseTable.getValueAt(selected, 0).toString();
        int courseId = getCourseIdByCode(courseCode);

        List<Section> sections = SectionDAO.getAllSections()
                .stream()
                .filter(s -> s.getCourseId() == courseId)
                .toList();

        if (sections.isEmpty()) {
            DialogUtils.showError(this, "No sections available for this course.");
            return;
        }

        String[] choices = sections.stream()
                .map(s -> "Section " + s.getSectionId() + "  (" + s.getDayTime() + ", Room " + s.getRoom() + ")")
                .toArray(String[]::new);

        String selectedSection = (String) JOptionPane.showInputDialog(
                this,
                "Select a section:",
                "Choose Section",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[0]
        );

        if (selectedSection == null) return;

        int sectionId = Integer.parseInt(selectedSection.split(" ")[1]);

        var resp = studentApi.registerSection(
                new edu.univ.erp.api.student.requests.RegisterSectionRequest(
                        studentId, sectionId
                )
        );

        if (resp.isSuccess()) DialogUtils.showInfo(this, resp.getData());
        else DialogUtils.showError(this, resp.getMessage());
    }


    private int getCourseIdByCode(String code) {
        return edu.univ.erp.data.CourseDAO.getAllCourses()
                .stream()
                .filter(c -> c.getCode().equals(code))
                .map(c -> c.getCourseId())
                .findFirst()
                .orElse(-1);
    }
}
