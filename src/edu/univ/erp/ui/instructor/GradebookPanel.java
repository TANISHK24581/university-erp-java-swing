package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GradebookPanel extends JPanel {

    private final InstructorApi api = new InstructorApi();
    private final JComboBox<SectionComboItem> sectionCombo = new JComboBox<>();
    private final JTable enrollTable = new JTable();
    private final JButton refreshSectionsBtn = new JButton("Refresh Sections");
    private final JButton loadEnrollBtn = new JButton("Load Students");
    private final JButton saveScoreBtn = new JButton("Save Score");
    private final JButton computeFinalBtn = new JButton("Compute Finals");
    private final JButton exportBtn = new JButton("Export CSV");
    private final JButton refreshEnrollBtn = new JButton("Refresh Enrollments");

    public GradebookPanel() {

        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.add(new JLabel("Section:"));
        left.add(sectionCombo);
        left.add(refreshSectionsBtn);
        left.add(loadEnrollBtn);
        left.add(refreshEnrollBtn);

        top.add(left, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        JScrollPane tablePane = new JScrollPane(enrollTable);
        tablePane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210), 1),
                BorderFactory.createEmptyBorder(10,10,10,10)
        ));

        add(tablePane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        bottom.add(saveScoreBtn);
        bottom.add(computeFinalBtn);
        bottom.add(exportBtn);

        add(bottom, BorderLayout.SOUTH);

        refreshSectionsBtn.addActionListener(e -> loadSections());
        loadEnrollBtn.addActionListener(e -> loadEnrollmentsForSelected());
        refreshEnrollBtn.addActionListener(e -> loadEnrollmentsForSelected());
        saveScoreBtn.addActionListener(e -> promptSaveScore());
        computeFinalBtn.addActionListener(e -> promptComputeFinals());
        exportBtn.addActionListener(e -> exportCSV());

        loadSections();
    }

    private void loadSections() {
        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Loading sections...")
            );
            try {
                List<Section> secs = api.getMySections();
                SwingUtilities.invokeLater(() -> {
                    sectionCombo.removeAllItems();
                    if (secs != null) {
                        for (Section s : secs) {
                            sectionCombo.addItem(new SectionComboItem(s));
                        }
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showError(this, "Failed to load sections: " + ex.getMessage())
                );
            } finally {
                SwingUtilities.invokeLater(DialogUtils::hideLoading);
            }
        }).start();
    }

    private void loadEnrollmentsForSelected() {
        SectionComboItem sel = (SectionComboItem) sectionCombo.getSelectedItem();
        if (sel == null) {
            DialogUtils.showWarning(this, "Pick a section first.");
            return;
        }
        int sectionId = sel.section.getSectionId();

        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Loading enrollments...")
            );
            try {
                List<Enrollment> enrollments = api.getEnrollmentsForSection(sectionId);
                SwingUtilities.invokeLater(() -> populateEnrollmentTable(enrollments));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showError(this, "Failed to load enrollments: " + ex.getMessage())
                );
            } finally {
                SwingUtilities.invokeLater(DialogUtils::hideLoading);
            }
        }).start();
    }

    private void populateEnrollmentTable(List<Enrollment> list) {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"EnrollmentId", "StudentId", "Status",
                        "Quiz", "Midterm", "End Sem", "Final Grade"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        if (list != null) {
            for (Enrollment e : list) {

                List<Grade> grades = api.getGradesForEnrollment(e.getEnrollmentId());

                Double quiz = null, mid = null, end = null;
                String finalGrade = "";

                for (Grade g : grades) {
                    if (g.getComponent() == null) continue;

                    String comp = g.getComponent().trim().toLowerCase();

                    if (comp.startsWith("quiz") || comp.contains("qui"))
                        quiz = g.getScore();
                    else if (comp.startsWith("mid") || comp.contains("midterm"))
                        mid = g.getScore();
                    else if (comp.startsWith("end") || comp.contains("endterm") || comp.contains("endsem"))
                        end = g.getScore();

                    if (g.getFinalGrade() != null && !g.getFinalGrade().isEmpty())
                        finalGrade = g.getFinalGrade();
                }

                model.addRow(new Object[]{
                        e.getEnrollmentId(),
                        e.getStudentId(),
                        e.getStatus(),
                        quiz == null ? "" : quiz,
                        mid == null ? "" : mid,
                        end == null ? "" : end,
                        finalGrade
                });
            }
        }

        enrollTable.setModel(model);

        enrollTable.setRowHeight(28);
        enrollTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        enrollTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        enrollTable.getTableHeader().setBackground(new Color(59, 130, 246));
        enrollTable.getTableHeader().setForeground(Color.BLACK);

        enrollTable.setShowHorizontalLines(true);
        enrollTable.setShowVerticalLines(false);
        enrollTable.setGridColor(new Color(220,220,220));
        enrollTable.setRowHeight(28);

        DialogUtils.makeTableSortable(enrollTable);
    }

    private void promptSaveScore() {
        int row = enrollTable.getSelectedRow();
        if (row == -1) {
            DialogUtils.showWarning(this, "Select a student row.");
            return;
        }

        int enrollmentId = (int) enrollTable.getValueAt(row, 0);
        String component = JOptionPane.showInputDialog(this, "Component (Quiz/Midterm/...):");
        if (component == null || component.trim().isEmpty()) return;

        String scoreStr = JOptionPane.showInputDialog(this, "Score:");
        if (scoreStr == null) return;

        double score;
        try { score = Double.parseDouble(scoreStr.trim()); }
        catch (NumberFormatException ex) {
            DialogUtils.showError(this, "Invalid score.");
            return;
        }

        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Saving score...")
            );
            try {
                String result = api.saveScore(enrollmentId, component.trim(), score);
                SwingUtilities.invokeLater(() -> {
                    DialogUtils.showInfo(this, result);
                    loadEnrollmentsForSelected();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showError(this, "Failed: " + ex.getMessage())
                );
            } finally {
                SwingUtilities.invokeLater(DialogUtils::hideLoading);
            }
        }).start();
    }

    private void promptComputeFinals() {
        SectionComboItem sel = (SectionComboItem) sectionCombo.getSelectedItem();
        if (sel == null) {
            DialogUtils.showWarning(this, "Pick a section first.");
            return;
        }

        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        JTextField quizF = new JTextField("20");
        JTextField midF = new JTextField("30");
        JTextField endF = new JTextField("50");

        p.add(new JLabel("Quiz %:")); p.add(quizF);
        p.add(new JLabel("Midterm %:")); p.add(midF);
        p.add(new JLabel("End-sem %:")); p.add(endF);

        int ok = JOptionPane.showConfirmDialog(this, p, "Weight %", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        Map<String, Double> weights = new HashMap<>();
        try {
            weights.put("quiz", Double.parseDouble(quizF.getText()) / 100);
            weights.put("midterm", Double.parseDouble(midF.getText()) / 100);
            weights.put("endsem", Double.parseDouble(endF.getText()) / 100);
        } catch (Exception ex) {
            DialogUtils.showError(this, "Invalid weight.");
            return;
        }

        int sectionId = sel.section.getSectionId();

        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Computing final grades...")
            );
            try {
                Map<Integer, Double> finals = api.computeFinals(sectionId, weights);
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showInfo(this, "Finals computed for " + finals.size() + " students.")
                );
                loadEnrollmentsForSelected();
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showError(this, "Compute failed: " + ex.getMessage())
                );
            } finally {
                SwingUtilities.invokeLater(DialogUtils::hideLoading);
            }
        }).start();
    }

    private void exportCSV() {
        SectionComboItem sel = (SectionComboItem) sectionCombo.getSelectedItem();
        if (sel == null) {
            DialogUtils.showWarning(this, "Pick a section first.");
            return;
        }

        int sectionId = sel.section.getSectionId();

        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Exporting CSV...")
            );
            try {
                String csv = api.exportCSV(sectionId);

                SwingUtilities.invokeLater(() -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Save CSV");

                    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                            fw.write(csv);
                            DialogUtils.showInfo(this, "Export saved.");
                        } catch (Exception ex) {
                            DialogUtils.showError(this, "Save failed: " + ex.getMessage());
                        }
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showError(this, "Export failed: " + ex.getMessage())
                );
            } finally {
                SwingUtilities.invokeLater(DialogUtils::hideLoading);
            }
        }).start();
    }


    private static class SectionComboItem {
        final Section section;
        private final String display;

        SectionComboItem(Section s) {
            this.section = s;

            String code = CourseDAO.getCourseCode(s.getCourseId());
            String title = CourseDAO.getCourseTitle(s.getCourseId());

            this.display = "Sec#" + s.getSectionId() +
                    " — " + code + " (" + title + ")" +
                    " [" + s.getSemester() + " " + s.getYear() + "]";
        }

        @Override
        public String toString() {
            return display;
        }
    }


}
