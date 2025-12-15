package edu.univ.erp.service;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class InstructorService {

    private int currentInstructorId() {
        return AuthService.getInstance().getUserId();
    }

    private String currentRole() {
        return AuthService.getInstance().getRole();
    }
    public boolean updateSection(
            int sectionId,
            int courseId,
            int instructorId,
            String dayTime,
            String room,
            int capacity,
            String semester,
            int year
    ) {
        Section s = new Section(
                sectionId,
                courseId,
                instructorId,
                dayTime,
                room,
                capacity,
                semester,
                year
        );

        return SectionDAO.updateSection(s);
    }


    public List<Section> getMySections() {

        int iid = currentInstructorId();

        if (!"Instructor".equals(currentRole()) || iid <= 0)
            return List.of();

        return SectionDAO.getAllSections().stream()
                .filter(s -> s.getInstructorId() == iid)
                .collect(Collectors.toList());
    }

    public List<Enrollment> getEnrollmentsForSection(int sectionId) {

        int iid = currentInstructorId();
        Section s = SectionDAO.getSectionById(sectionId);

        if (s == null) return List.of();
        if (s.getInstructorId() != iid) return List.of();

        return EnrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public String saveScore(int enrollmentId, String component, double score) {

        if (AccessChecker.isMaintenanceOn())
            return "Maintenance ON — write blocked.";

        Enrollment e = EnrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) return "Enrollment not found.";

        if (!"active".equalsIgnoreCase(e.getStatus()))
            return "Cannot grade a dropped student.";


        Section s = SectionDAO.getSectionById(e.getSectionId());
        if (s == null) return "Section not found.";

        int iid = currentInstructorId();
        if (s.getInstructorId() != iid)
            return "Not your section.";

        if (score < 0 || score > 100)
            return "Score must be between 0 and 100.";

        boolean ok = GradeDAO.upsertGrade(
                enrollmentId,
                component.trim().toLowerCase(),
                score
        );

        return ok ? "Score saved." : "Save failed.";
    }

    public Map<Integer, Double> computeFinalForSection(int sectionId,
                                                       Map<String, Double> weights) {

        if (AccessChecker.isMaintenanceOn()) return Map.of();

        Section s = SectionDAO.getSectionById(sectionId);
        int iid = currentInstructorId();

        if (s == null || s.getInstructorId() != iid)
            return Map.of();

        double sum = weights.values().stream().mapToDouble(v -> v).sum();
        if (Math.abs(sum - 1.0) > 0.001)
            throw new IllegalArgumentException("Weights must sum to 1.0");

        List<Enrollment> active = EnrollmentDAO.getEnrollmentsBySection(sectionId).stream()
                .filter(e -> "active".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        Map<Integer, Double> finals = new HashMap<>();

        for (Enrollment e : active) {

            List<Grade> comps = GradeDAO.getGradesByEnrollment(e.getEnrollmentId());
            double total = 0;

            for (Grade g : comps) {

                if (g.getComponent() == null) continue;

                String comp = g.getComponent().trim().toLowerCase();

                // --- NORMALIZE COMMON TYPOS / VARIATIONS ---
                if (comp.startsWith("quiz") || comp.contains("qui"))
                    comp = "quiz";

                else if (comp.startsWith("mid") || comp.contains("midterm"))
                    comp = "midterm";

                else if (comp.startsWith("end") || comp.contains("endsem") || comp.contains("final"))
                    comp = "endsem";

                Double w = weights.get(comp);
                if (w != null) {
                    total += g.getScore() * w;
                }
            }


            double rounded = Math.round(total * 100.0) / 100.0;
            finals.put(e.getEnrollmentId(), rounded);

            GradeDAO.setFinalGrade(e.getEnrollmentId(), String.valueOf(rounded));
        }

        return finals;
    }
    public String resetGradesForEnrollment(int enrollmentId) {

        Enrollment e = EnrollmentDAO.getEnrollmentById(enrollmentId);
        if (e == null) return "Enrollment not found.";

        Section s = SectionDAO.getSectionById(e.getSectionId());
        if (s == null) return "Section not found.";

        if (s.getInstructorId() != currentInstructorId())
            return "Not your section.";

        boolean ok = GradeDAO.deleteGradesForEnrollment(enrollmentId);
        return ok ? "Scores reset." : "Reset failed.";
    }



    public Optional<ClassStats> getClassStats(int sectionId) {

        int iid = currentInstructorId();
        Section s = SectionDAO.getSectionById(sectionId);

        if (s == null || s.getInstructorId() != iid)
            return Optional.empty();

        List<Enrollment> list = EnrollmentDAO.getEnrollmentsBySection(sectionId);

        List<Double> finals = new ArrayList<>();

        for (Enrollment e : list) {
            if (!"active".equalsIgnoreCase(e.getStatus()))
                continue;

            for (Grade g : GradeDAO.getGradesByEnrollment(e.getEnrollmentId())) {
                String fg = g.getFinalGrade();
                if (fg != null && !fg.isEmpty()) {
                    try {
                        finals.add(Double.parseDouble(fg));
                        break;
                    } catch (NumberFormatException ignore) {}
                }
            }
        }

        if (finals.isEmpty()) return Optional.empty();

        double avg = finals.stream().mapToDouble(d -> d).average().orElse(0);
        double min = finals.stream().mapToDouble(d -> d).min().orElse(0);
        double max = finals.stream().mapToDouble(d -> d).max().orElse(0);

        return Optional.of(new ClassStats(finals.size(), avg, min, max));
    }

    public static class ClassStats {
        public final int count;
        public final double average;
        public final double min;
        public final double max;

        public ClassStats(int count, double average, double min, double max) {
            this.count = count;
            this.average = average;
            this.min = min;
            this.max = max;
        }
    }


    public String exportGradesCSV(int sectionId) {

        int iid = currentInstructorId();
        Section s = SectionDAO.getSectionById(sectionId);

        if (s == null || s.getInstructorId() != iid)
            return null;

        return GradeDAO.exportGradesCSV(sectionId);
    }
}
