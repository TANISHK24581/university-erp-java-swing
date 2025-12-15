package edu.univ.erp.api.instructor;

import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InstructorApi {

    private final InstructorService service;

    public InstructorApi() {
        this.service = new InstructorService();
    }

    public List<Section> getMySections() {
        return service.getMySections();
    }
    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        return GradeDAO.getGradesByEnrollment(enrollmentId);
    }


    public List<Enrollment> getEnrollmentsForSection(int sectionId) {
        return service.getEnrollmentsForSection(sectionId);
    }

    public String saveScore(int enrollmentId, String component, double score) {
        return service.saveScore(enrollmentId, component, score);
    }

    public Map<Integer, Double> computeFinals(int sectionId, Map<String, Double> weights) {
        return service.computeFinalForSection(sectionId, weights);
    }

    public Optional<InstructorService.ClassStats> getStats(int sectionId) {
        return service.getClassStats(sectionId);
    }
    public boolean resetGrades(int enrollmentId) {
        return GradeDAO.deleteGradesForEnrollment(enrollmentId);
    }


    public String exportCSV(int sectionId) {
        return service.exportGradesCSV(sectionId);
    }
}
