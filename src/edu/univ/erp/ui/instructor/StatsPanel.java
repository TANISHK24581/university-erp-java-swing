package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService.ClassStats;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class StatsPanel extends JPanel {

    private final InstructorApi api = new InstructorApi();
    private final JComboBox<SectionItem> sectionCombo = new JComboBox<>();
    private final JButton refreshBtn = new JButton("Refresh Sections");
    private final JButton loadBtn = new JButton("Load Stats");

    private final JLabel countVal = createValueLabel();
    private final JLabel avgVal = createValueLabel();
    private final JLabel minVal = createValueLabel();
    private final JLabel maxVal = createValueLabel();

    public StatsPanel() {
        setLayout(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Section:"));
        top.add(sectionCombo);
        top.add(refreshBtn);
        top.add(loadBtn);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        center.add(createCard("Total Students Graded", countVal));
        center.add(Box.createVerticalStrut(20));
        center.add(createCard("Average Final Grade", avgVal));
        center.add(Box.createVerticalStrut(20));
        center.add(createCard("Minimum Score", minVal));
        center.add(Box.createVerticalStrut(20));
        center.add(createCard("Maximum Score", maxVal));

        add(center, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadSections());
        loadBtn.addActionListener(e -> loadStats());

        loadSections();
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("--", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        lbl.setForeground(new Color(30, 90, 200));  // Blue values
        return lbl;
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLbl.setForeground(Color.DARK_GRAY);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadSections() {
        List<Section> sections = api.getMySections();
        sectionCombo.removeAllItems();

        if (sections != null) {
            for (Section s : sections) {
                sectionCombo.addItem(new SectionItem(s));
            }
        }

        resetValues();
    }

    private void loadStats() {
        SectionItem item = (SectionItem) sectionCombo.getSelectedItem();
        if (item == null) {
            DialogUtils.showWarning(this, "Select a section.");
            return;
        }

        Optional<ClassStats> statsOpt = api.getStats(item.section.getSectionId());
        if (statsOpt.isEmpty()) {
            DialogUtils.showWarning(this, "No stats available.");
            resetValues();
            return;
        }

        ClassStats stats = statsOpt.get();

        countVal.setText(String.valueOf(stats.count));
        avgVal.setText(String.format("%.2f", stats.average));
        minVal.setText(String.format("%.2f", stats.min));
        maxVal.setText(String.format("%.2f", stats.max));
    }

    private void resetValues() {
        countVal.setText("--");
        avgVal.setText("--");
        minVal.setText("--");
        maxVal.setText("--");
    }

    private static class SectionItem {
        final Section section;

        SectionItem(Section s) { this.section = s; }

        @Override
        public String toString() {

            String code = edu.univ.erp.data.CourseDAO.getCourseCode(section.getCourseId());
            String title = edu.univ.erp.data.CourseDAO.getCourseTitle(section.getCourseId());

            return "Sec#" + section.getSectionId() +
                    " — " + code + " (" + title + ") [" +
                    section.getSemester() + " " + section.getYear() + "]";
        }
    }

}
