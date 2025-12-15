//package edu.univ.erp.ui.student;
//
//import edu.univ.erp.api.student.StudentApi;
//import edu.univ.erp.api.student.responses.TimetableRow;
//import edu.univ.erp.api.student.utils.ApiResponse;
//import edu.univ.erp.ui.common.DialogUtils;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class TimetablePanel extends JPanel {
//    private final StudentApi api;
//    private final JTable table = new JTable();
//
//    public TimetablePanel(StudentApi api) {
//        this.api = api;
//        setLayout(new BorderLayout(6,6));
//        add(new JScrollPane(table), BorderLayout.CENTER);
//
//        JButton refresh = new JButton("Refresh");
//        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        south.add(refresh);
//        add(south, BorderLayout.SOUTH);
//
//        refresh.addActionListener(e -> load());
//        load();
//    }
//
//    private void load() {
//        ApiResponse<List<TimetableRow>> resp = api.viewTimetable();
//        if (!resp.isSuccess()) {
//            DialogUtils.showError(this, resp.getMessage());
//            return;
//        }
//        DefaultTableModel m = new DefaultTableModel(new String[]{"Course","Title","Day","Time","Room"},0) {
//            @Override public boolean isCellEditable(int row,int col){return false;}
//        };
//        for (TimetableRow r : resp.getData()) {
//            m.addRow(new Object[]{r.getCourseCode(), r.getCourseTitle(), r.getDay(), r.getTime(), r.getRoom()});
//        }
//        table.setModel(m);
//        edu.univ.erp.ui.common.DialogUtils.makeTableSortable(table);
//    }
//}

//package edu.univ.erp.ui.student;
//
//import edu.univ.erp.api.student.StudentApi;
//import edu.univ.erp.api.student.responses.TimetableRow;
//import edu.univ.erp.api.student.utils.ApiResponse;
//import edu.univ.erp.ui.common.DialogUtils;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * TimetablePanel - grid view with days as columns (Mon..Fri) and only the time rows
// * that the student is actually enrolled for.
// *
// * Expects StudentApi.viewTimetable() -> ApiResponse<List<TimetableRow>>
// *
// * Replace the old TimetablePanel with this class.
// */
//public class TimetablePanel extends JPanel {
//    private final StudentApi api;
//    private JPanel gridContainer;        // holds the timetable grid
//    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
//
//    public TimetablePanel(StudentApi api) {
//        this.api = api;
//        initUI();
//        loadTimetableAsync();
//    }
//
//    private void initUI() {
//        setLayout(new BorderLayout(8, 8));
//        setBackground(Color.WHITE);
//
//        // Top bar with title + buttons
//        JPanel top = new JPanel(new BorderLayout());
//        top.setBackground(new Color(235, 243, 255));
//        top.setBorder(new EmptyBorder(10, 12, 10, 12));
//
//        JLabel title = new JLabel("Student Timetable", SwingConstants.CENTER);
//        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
//        title.setForeground(new Color(27, 95, 190));
//        top.add(title, BorderLayout.CENTER);
//
//        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
//        buttons.setOpaque(false);
//
//        JButton filter = new JButton("Filter");
//        JButton refresh = new JButton("Refresh");
//
//        filter.setFocusPainted(false);
//        refresh.setFocusPainted(false);
//
//        buttons.add(filter);
//        buttons.add(refresh);
//        top.add(buttons, BorderLayout.EAST);
//
//        add(top, BorderLayout.NORTH);
//
//        // Grid container inside scroll pane
//        gridContainer = new JPanel(new BorderLayout());
//        gridContainer.setBackground(Color.WHITE);
//        JScrollPane sp = new JScrollPane(gridContainer,
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        sp.getVerticalScrollBar().setUnitIncrement(16);
//        add(sp, BorderLayout.CENTER);
//
//        // Hooks
//        refresh.addActionListener(e -> loadTimetableAsync());
//        filter.addActionListener(e -> {
//            // placeholder — you can implement filters (by semester, course, instructor etc.)
//            DialogUtils.showInfo(this, "Filter not implemented yet.");
//        });
//
//    }
//
//    // Load data in background thread
//    private void loadTimetableAsync() {
//        new Thread(() -> {
//            SwingUtilities.invokeLater(() -> DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this), "Loading timetable..."));
//            try {
//                ApiResponse<List<TimetableRow>> resp = api.viewTimetable();
//                if (!resp.isSuccess()) {
//                    SwingUtilities.invokeLater(() -> {
//                        DialogUtils.hideLoading();
//                        DialogUtils.showError(this, resp.getMessage());
//                    });
//                    return;
//                }
//                List<TimetableRow> rows = resp.getData();
//                SwingUtilities.invokeLater(() -> {
//                    buildGrid(rows);
//                    DialogUtils.hideLoading();
//                });
//            } catch (Exception ex) {
//                SwingUtilities.invokeLater(() -> {
//                    DialogUtils.hideLoading();
//                    DialogUtils.showError(this, "Failed to load timetable: " + ex.getMessage());
//                });
//            }
//        }).start();
//    }
//
//    // Build grid UI from timetable rows
//    private void buildGrid(List<TimetableRow> rows) {
//        gridContainer.removeAll();
//
//        // If no rows, show friendly message
//        if (rows == null || rows.isEmpty()) {
//            JPanel p = new JPanel(new BorderLayout());
//            p.setBackground(Color.WHITE);
//            JLabel l = new JLabel("No enrolled classes to show.", SwingConstants.CENTER);
//            l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//            p.add(l, BorderLayout.CENTER);
//            gridContainer.add(p, BorderLayout.CENTER);
//            revalidate();
//            repaint();
//            return;
//        }
//
//        // Extract distinct times in order (use start-time parsing)
//        List<String> times = rows.stream()
//                .map(TimetableRow::getTime)
//                .filter(Objects::nonNull)
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .distinct()
//                .sorted(this::compareTimeRange) // custom comparator
//                .collect(Collectors.toList());
//
//        // If time strings are empty or not parseable, fallback to using index order
//        if (times.isEmpty()) {
//            // create a single time row to show data mapped by day only
//            times = Collections.singletonList("");
//        }
//
//        // Create grid panel with GridBagLayout to control column widths
//        JPanel grid = new JPanel(new GridBagLayout());
//        grid.setBackground(Color.WHITE);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.BOTH;
//
//        // Header row: first empty cell (top-left), then days
//        gbc.gridy = 0;
//        gbc.gridx = 0;
//        gbc.weightx = 0.18;
//        gbc.weighty = 0;
//        JPanel timeHeader = createLeftHeader("");
//        grid.add(timeHeader, gbc);
//
//        for (int c = 0; c < days.length; c++) {
//            gbc.gridx = c + 1;
//            gbc.weightx = 1.0;
//            JPanel dayHeader = createDayHeader(days[c]);
//            grid.add(dayHeader, gbc);
//        }
//
//        // Rows: for each time slot, left label (time) and 5 day-cells
//        int r = 1;
//        for (String time : times) {
//            gbc.gridy = r;
//            // left time column
//            gbc.gridx = 0;
//            gbc.weightx = 0.18;
//            JPanel left = createLeftHeader(time.isEmpty() ? "Time" : time);
//            grid.add(left, gbc);
//
//            // days columns
//            for (int c = 0; c < days.length; c++) {
//                gbc.gridx = c + 1;
//                gbc.weightx = 1.0;
//                JPanel cell = createEmptyCell();
//
//                // find if a TimetableRow matches this day & time
//                final String dayName = days[c];
//                final String thisTime = time;
//                Optional<TimetableRow> match = rows.stream()
//                        .filter(tr -> equalsIgnoreCaseTrim(tr.getDay(), dayName))
//                        .filter(tr -> {
//                            String t = tr.getTime();
//                            if (t == null) t = "";
//                            t = t.trim();
//                            if (thisTime.isEmpty()) return true; // fallback: show everything
//                            return t.equalsIgnoreCase(thisTime);
//                        })
//                        .findFirst();
//
//                match.ifPresent(tr -> {
//                    JPanel card = createClassCard(tr);
//                    // center the card inside the cell
//                    cell.setLayout(new GridBagLayout());
//                    GridBagConstraints ic = new GridBagConstraints();
//                    ic.gridx = 0; ic.gridy = 0;
//                    ic.weightx = 1.0; ic.weighty = 1.0;
//                    ic.anchor = GridBagConstraints.CENTER;
//                    cell.add(card, ic);
//                });
//
//                grid.add(cell, gbc);
//            }
//            r++;
//        }
//
//        // Put grid in a wrapper to allow wide minimum width
//        JPanel wrapper = new JPanel(new BorderLayout());
//        wrapper.setBackground(Color.WHITE);
//        wrapper.add(grid, BorderLayout.NORTH); // keep to top so rows don't stretch weirdly
//
//        gridContainer.add(wrapper, BorderLayout.NORTH);
//        revalidate();
//        repaint();
//    }
//
//    // Utilities / UI helpers
//
//    private JPanel createDayHeader(String day) {
//        JPanel p = new JPanel(new BorderLayout());
//        p.setBackground(new Color(22, 45, 78));
//        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(200,200,200)));
//        JLabel l = new JLabel(day, SwingConstants.CENTER);
//        l.setForeground(Color.WHITE);
//        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        l.setBorder(new EmptyBorder(10, 6, 10, 6));
//        p.add(l, BorderLayout.CENTER);
//        return p;
//    }
//
//    private JPanel createLeftHeader(String text) {
//        JPanel p = new JPanel(new BorderLayout());
//        p.setBackground(new Color(20, 40, 67));
//        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(200,200,200)));
//        JLabel l = new JLabel(text, SwingConstants.CENTER);
//        l.setForeground(Color.WHITE);
//        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        l.setBorder(new EmptyBorder(18, 6, 18, 6));
//        p.add(l, BorderLayout.CENTER);
//        return p;
//    }
//
//    private JPanel createEmptyCell() {
//        JPanel p = new JPanel();
//        p.setBackground(Color.WHITE);
//        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(230,230,230)));
//        return p;
//    }
//
//    private JPanel createClassCard(TimetableRow tr) {
//
//        JPanel card = new JPanel();
//        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));   // vertical stacking
//        card.setOpaque(true);
//
//        card.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(0, 0, 0, 40), 1, true),
//                BorderFactory.createEmptyBorder(10, 12, 10, 12)
//        ));
//        card.setBackground(colorForCourse(tr.getCourseCode()));
//
//        // --- COURSE CODE (bold, bigger)
//        JLabel lblCode = new JLabel(safe(tr.getCourseCode()));
//        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        lblCode.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        // --- FULL TITLE (wrapped manually using multiple JLabels)
//        JLabel lblTitle = new JLabel(safe(tr.getCourseTitle()));
//        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        // --- META INFO (day • time • room)
//        String metaText = safe(tr.getDay()) + "  •  " + safe(tr.getTime()) + "  •  " + safe(tr.getRoom());
//        JLabel lblMeta = new JLabel(metaText);
//        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
//        lblMeta.setForeground(new Color(40, 40, 40));
//        lblMeta.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        // Add components in vertical order
//        card.add(lblCode);
//        card.add(Box.createVerticalStrut(4));   // small gap
//        card.add(lblTitle);
//        card.add(Box.createVerticalStrut(6));
//        card.add(lblMeta);
//
//        // Optional: tooltip
//        card.setToolTipText(
//                safe(tr.getCourseCode()) + " - " + safe(tr.getCourseTitle()) +
//                        " (" + safe(tr.getDay()) + " " + safe(tr.getTime()) + ")"
//        );
//
//        // Ensure size
//        card.setMaximumSize(new Dimension(400, 200));
//        card.setPreferredSize(new Dimension(220, 95));
//
//        return card;
//    }
//
//
//
//
//    // Choose a pleasant pastel color derived from course code hash
//    private Color colorForCourse(String courseCode) {
//        if (courseCode == null) courseCode = "";
//        int h = Math.abs(courseCode.hashCode());
//        // produce pastel-ish colors
//        int r = 120 + (h % 80);
//        int g = 140 + ((h / 31) % 80);
//        int b = 170 + ((h / 47) % 60);
//        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
//    }
//
//    private String safe(String s) {
//        return s == null ? "" : s;
//    }
//
//    private String shortTitle(String title) {
//        if (title == null) return "";
//        title = title.trim();
//        if (title.length() <= 24) return title;
//        return title.substring(0, 21) + "...";
//    }
//
//    // Compare two time ranges like "10:30-11:30" or "10:00" by start time
//    private int compareTimeRange(String a, String b) {
//        try {
//            long ta = parseStartMillis(a);
//            long tb = parseStartMillis(b);
//            return Long.compare(ta, tb);
//        } catch (Exception e) {
//            return a.compareTo(b);
//        }
//    }
//
//    // parse "HH:mm" or "HH:mm-HH:mm" or "Mon 10:00-11:00" etc.
//    private long parseStartMillis(String t) {
//        if (t == null) return 0;
//        t = t.trim();
//        // if day prefix present (e.g., "Mon 10:00-11:30"), try splitting
//        if (t.contains(" ")) {
//            String[] parts = t.split("\\s+", 2);
//            t = parts[1];
//        }
//        // if range, take left
//        if (t.contains("-")) t = t.split("-", 2)[0].trim();
//        // if has ':'
//        String[] hhmm = t.split(":");
//        if (hhmm.length >= 2) {
//            int hh = Integer.parseInt(hhmm[0].replaceAll("\\D", ""));
//            int mm = Integer.parseInt(hhmm[1].replaceAll("\\D", ""));
//            return hh * 60L + mm;
//        }
//        // fallback: try to parse number
//        try { return Long.parseLong(t); } catch (Exception ignored) {}
//        return 0;
//    }
//
//    private boolean equalsIgnoreCaseTrim(String a, String b) {
//        if (a == null && b == null) return true;
//        if (a == null || b == null) return false;
//        return a.trim().equalsIgnoreCase(b.trim());
//    }
//}



package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.api.student.responses.TimetableRow;
import edu.univ.erp.api.student.utils.ApiResponse;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.AuthDB;
import edu.univ.erp.domain.Section;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TimetablePanel extends JPanel {

    private final StudentApi api;
    private JPanel gridContainer;
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private List<TimetableRow> lastFetchedRows = new ArrayList<>();

    private String filterDay = "All";
    private String filterCourse = "All";
    private String filterTime = "All";
    private String filterInstructor = "All";
    private String filterRoom = "All";

    public TimetablePanel(StudentApi api) {
        this.api = api;
        initUI();
        loadTimetableAsync();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(235, 243, 255));
        top.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel title = new JLabel("Student Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(27, 95, 190));
        top.add(title, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton filterBtn = new JButton("Filter");
        JButton refreshBtn = new JButton("Refresh");

        buttons.add(filterBtn);
        buttons.add(refreshBtn);

        top.add(buttons, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBackground(Color.WHITE);
        JScrollPane sp = new JScrollPane(gridContainer);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        add(sp, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadTimetableAsync());
        filterBtn.addActionListener(e -> openFilterDialog());
    }

    private void loadTimetableAsync() {
        new Thread(() -> {
            SwingUtilities.invokeLater(() ->
                    DialogUtils.showLoading(SwingUtilities.getWindowAncestor(this),
                            "Loading timetable...")
            );

            try {
                ApiResponse<List<TimetableRow>> resp = api.viewTimetable();

                if (!resp.isSuccess()) {
                    SwingUtilities.invokeLater(() -> {
                        DialogUtils.hideLoading();
                        DialogUtils.showError(this, resp.getMessage());
                    });
                    return;
                }

                lastFetchedRows = resp.getData();

                SwingUtilities.invokeLater(() -> {
                    applyFiltersAndBuildGrid();
                    DialogUtils.hideLoading();
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    DialogUtils.hideLoading();
                    DialogUtils.showError(this, "Failed to load timetable: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void applyFiltersAndBuildGrid() {
        List<TimetableRow> filtered = lastFetchedRows.stream()
                .filter(r ->
                        filterDay.equals("All") ||
                                r.getDay().equalsIgnoreCase(filterDay))
                .filter(r ->
                        filterCourse.equals("All") ||
                                r.getCourseCode().equalsIgnoreCase(filterCourse))
                .filter(r ->
                        filterTime.equals("All") ||
                                r.getTime().equalsIgnoreCase(filterTime))
                .filter(r ->
                        filterRoom.equals("All") ||
                                r.getRoom().equalsIgnoreCase(filterRoom))
                .filter(r -> {
                    if (filterInstructor.equals("All")) return true;
                    Section sec = SectionDAO.getSectionById(r.getSectionId());
                    String inst = AuthDB.getUsernameById(sec.getInstructorId());
                    return inst != null && inst.equalsIgnoreCase(filterInstructor);
                })
                .collect(Collectors.toList());

        buildGrid(filtered);
    }

    private void openFilterDialog() {
        if (lastFetchedRows.isEmpty()) {
            DialogUtils.showInfo(this, "No data available for filtering.");
            return;
        }

        Set<String> daySet = new TreeSet<>(lastFetchedRows.stream().map(TimetableRow::getDay).collect(Collectors.toSet()));
        Set<String> courseSet = new TreeSet<>(lastFetchedRows.stream().map(TimetableRow::getCourseCode).collect(Collectors.toSet()));
        Set<String> timeSet = new TreeSet<>(lastFetchedRows.stream().map(TimetableRow::getTime).collect(Collectors.toSet()));
        Set<String> roomSet = new TreeSet<>(lastFetchedRows.stream().map(TimetableRow::getRoom).collect(Collectors.toSet()));

        Set<String> instSet = new TreeSet<>();
        for (TimetableRow r : lastFetchedRows) {
            Section sec = SectionDAO.getSectionById(r.getSectionId());
            String inst = AuthDB.getUsernameById(sec.getInstructorId());
            if (inst != null) instSet.add(inst);
        }

        JComboBox<String> cbDay = new JComboBox<>(buildArray("All", daySet));
        JComboBox<String> cbCourse = new JComboBox<>(buildArray("All", courseSet));
        JComboBox<String> cbTime = new JComboBox<>(buildArray("All", timeSet));
        JComboBox<String> cbInstructor = new JComboBox<>(buildArray("All", instSet));
        JComboBox<String> cbRoom = new JComboBox<>(buildArray("All", roomSet));

        JPanel panel = new JPanel(new GridLayout(0,2,8,8));
        panel.add(new JLabel("Day:")); panel.add(cbDay);
        panel.add(new JLabel("Course:")); panel.add(cbCourse);
        panel.add(new JLabel("Time:")); panel.add(cbTime);
        panel.add(new JLabel("Instructor:")); panel.add(cbInstructor);
        panel.add(new JLabel("Room:")); panel.add(cbRoom);

        int res = JOptionPane.showConfirmDialog(this, panel,
                "Filter Timetable", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            filterDay = (String) cbDay.getSelectedItem();
            filterCourse = (String) cbCourse.getSelectedItem();
            filterTime = (String) cbTime.getSelectedItem();
            filterInstructor = (String) cbInstructor.getSelectedItem();
            filterRoom = (String) cbRoom.getSelectedItem();

            applyFiltersAndBuildGrid();
        }
    }

    private String[] buildArray(String first, Set<String> rest) {
        List<String> list = new ArrayList<>();
        list.add(first);
        list.addAll(rest);
        return list.toArray(new String[0]);
    }

    private void buildGrid(List<TimetableRow> rows) {
        gridContainer.removeAll();

        if (rows == null || rows.isEmpty()) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.WHITE);
            JLabel l = new JLabel("No classes match your filter.", SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            p.add(l, BorderLayout.CENTER);
            gridContainer.add(p, BorderLayout.CENTER);
            revalidate();
            repaint();
            return;
        }

        List<String> times = rows.stream()
                .map(TimetableRow::getTime)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .sorted(this::compareTimeRange)
                .collect(Collectors.toList());

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // header row
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.18;
        grid.add(createLeftHeader(""), gbc);

        for (int c = 0; c < days.length; c++) {
            gbc.gridx = c + 1;
            gbc.weightx = 1.0;
            grid.add(createDayHeader(days[c]), gbc);
        }

        // rows
        int r = 1;
        for (String time : times) {
            gbc.gridy = r;

            gbc.gridx = 0;
            grid.add(createLeftHeader(time), gbc);

            for (int c = 0; c < days.length; c++) {
                gbc.gridx = c + 1;
                JPanel cell = createEmptyCell();

                String dayName = days[c];

                rows.stream()
                        .filter(tr -> rowContainsDay(tr, dayName))
                        .filter(tr -> tr.getTime().equalsIgnoreCase(time))

                        .findFirst()
                        .ifPresent(tr -> {
                            JPanel card = createClassCard(tr);
                            cell.setLayout(new GridBagLayout());
                            cell.add(card);
                        });

                grid.add(cell, gbc);
            }
            r++;
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(grid, BorderLayout.NORTH);

        gridContainer.add(wrapper, BorderLayout.NORTH);
        revalidate();
        repaint();
    }


    private JPanel createDayHeader(String day) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(22, 45, 78));
        JLabel l = new JLabel(day, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(l);
        return p;
    }

    private JPanel createLeftHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 40, 67));
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(l);
        return p;
    }

    private JPanel createEmptyCell() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        return p;
    }
    private boolean rowContainsDay(TimetableRow tr, String dayName) {
        if (tr.getDay() == null) return false;


        String d = tr.getDay().toLowerCase().replace(",", " ").replace("-", " ");
        String target = dayName.toLowerCase();

        for (String part : d.split("\\s+")) {
            if (part.trim().isEmpty()) continue;

            if (dayMatches(part, target)) return true;
        }
        return false;
    }

    private boolean dayMatches(String part, String target) {
        return part.startsWith(target.substring(0, 3));
    }


    private JPanel createClassCard(TimetableRow tr) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(colorForCourse(tr.getCourseCode()));
        card.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,40)));

        card.setPreferredSize(new Dimension(220, 75));
        card.setMaximumSize(new Dimension(400, 100));



        JLabel code = new JLabel(tr.getCourseCode());
        code.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel title = new JLabel(tr.getCourseTitle());
        title.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel meta = new JLabel(tr.getDay() + " • " + tr.getTime() + " • " + tr.getRoom());
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        card.add(code);
        card.add(title);
        card.add(meta);

        return card;
    }

    private Color colorForCourse(String c) {
        int h = Math.abs(c.hashCode());
        return new Color(120 + h % 80, 140 + (h/31)%80, 170 + (h/47)%60);
    }

    private int compareTimeRange(String a, String b) {
        return a.compareTo(b);
    }
}

