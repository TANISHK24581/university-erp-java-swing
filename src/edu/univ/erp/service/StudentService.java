package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.*;
import java.time.LocalDate;

public class StudentService {

    private final int uid() { return AuthService.getInstance().getUserId(); }
    private final String role() { return AuthService.getInstance().getRole(); }

    private void ensureStudent() {
        if (!"Student".equals(role()))
            throw new SecurityException("Student access required.");
    }

    public String registerSection(int sectionId) {
        ensureStudent();

        // 1. Check maintenance mode
        if (SettingsDAO.isMaintenanceOn())
            return "Maintenance Mode is ON — you cannot register.";

        // 2. Check registration deadline
        LocalDate deadline = SettingsDAO.getRegistrationDeadline();
        LocalDate today = LocalDate.now();
        if (deadline != null && today.isAfter(deadline))
            return "Registration deadline has passed (" + deadline + ").";

        // 3. Duplicate check
        String status = EnrollmentDAO.getStatus(uid(), sectionId);

// Already active
        if ("active".equalsIgnoreCase(status)) {
            return "You are already enrolled in this course.";
        }

// Previously dropped → allow re-enroll
        if ("dropped".equalsIgnoreCase(status)) {

            if (EnrollmentDAO.isSectionFull(sectionId))
                return "Section is full.";

            boolean ok = EnrollmentDAO.reactivate(uid(), sectionId);
            return ok ? "Re-enrolled successfully!" : "Re-enroll failed.";
        }

        // 4. Capacity check
        if (EnrollmentDAO.isSectionFull(sectionId))
            return "Section is full.";

        // 5. Register
        boolean ok = EnrollmentDAO.addEnrollment(uid(), sectionId);
        return ok ? "Registered successfully!" : "Registration failed.";
    }

    public String dropSection(int sectionId) {
        ensureStudent();

        if (SettingsDAO.isMaintenanceOn())
            return "Maintenance Mode is ON — you cannot drop a section.";

        LocalDate deadline = SettingsDAO.getDropDeadline();
        LocalDate today = LocalDate.now();
        if (deadline != null && today.isAfter(deadline))
            return "Drop deadline has passed (" + deadline + ").";

        boolean ok = EnrollmentDAO.dropEnrollment(uid(), sectionId);
        return ok ? "Section dropped!" : "Drop failed.";
    }
}
