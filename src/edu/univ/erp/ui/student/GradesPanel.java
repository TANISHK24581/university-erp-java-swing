package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.api.student.responses.GradeRow;
import edu.univ.erp.api.student.utils.ApiResponse;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;

public class GradesPanel extends JPanel {

    private final StudentApi studentApi;
    private JTable gradesTable;

    public GradesPanel(StudentApi studentApi) {
        this.studentApi = studentApi;
        setLayout(new BorderLayout());

        gradesTable = new JTable();
        add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export Transcript CSV");

        south.add(refreshBtn);
        south.add(exportBtn);
        add(south, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadGrades());
        exportBtn.addActionListener(e -> exportTranscript());

        loadGrades();
    }

    private void loadGrades() {
        ApiResponse<List<GradeRow>> response = studentApi.viewGrades();
        if (!response.isSuccess()) {
            DialogUtils.showError(this, response.getMessage());
            return;
        }

        List<GradeRow> grades = response.getData();
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Course Code", "Quiz", "Midterm", "End Sem", "Final Grade"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (GradeRow g : grades) {
            model.addRow(new Object[]{
                    g.getCourseCode(),
                    g.getQuiz(),
                    g.getMidterm(),
                    g.getEndSem(),
                    g.getFinalGrade()
            });
        }

        gradesTable.setModel(model);
        JTableHeader header = gradesTable.getTableHeader();
        header.setOpaque(true);
//        header.setBackground(new Color(200, 220, 255));   // Soft light blue
//        header.setForeground(new Color(22, 45, 78));      // Navy text

        header.setBackground(new Color(59, 130, 246));  // button-family soft blue
        header.setForeground(Color.BLACK);




        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        gradesTable.setRowHeight(28);
        gradesTable.setAutoCreateRowSorter(true);

    }

    private void exportTranscript() {
        ApiResponse<String> response = studentApi.downloadTranscript();
        if (!response.isSuccess()) {
            DialogUtils.showError(this, response.getMessage());
            return;
        }

        String csv = response.getData();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Transcript");

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.write(csv);
                DialogUtils.showInfo(this, "Transcript saved successfully.");
            } catch (Exception ex) {
                DialogUtils.showError(this, "Error saving file: " + ex.getMessage());
            }
        }
    }
}
