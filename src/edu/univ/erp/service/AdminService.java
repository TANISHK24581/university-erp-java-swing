package edu.univ.erp.service;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.User;
import edu.univ.erp.util.ReportsUtil;

import java.util.List;
import java.util.Optional;

public class AdminService {
    private final int uid() { return AuthService.getInstance().getUserId(); }
    private final String role() { return AuthService.getInstance().getRole(); }

    private void ensureAdmin() {
        if (!"Admin".equals(role())) throw new SecurityException("Admin access required.");
    }

    public String createUser(String username, String role, String plainPassword) {
        ensureAdmin();
        if (username == null || username.trim().isEmpty()) return "Username required.";
        if (role == null) role = "Student";
        return UserDAO.createUserWithProfile(username.trim(), role, plainPassword);
    }

    public List<User> listUsers() { ensureAdmin(); return UserDAO.getAllUsers(); }

    public boolean addCourse(String code, String title, int credits) { ensureAdmin(); return CourseDAO.addCourse(code, title, credits); }
    public List<Course> getAllCourses() { ensureAdmin(); return CourseDAO.getAllCourses(); }
    public boolean updateCourse(int courseId, String code, String title, int credits) { ensureAdmin(); return CourseDAO.updateCourse(courseId, code, title, credits); }
    public boolean deleteCourse(int courseId) { ensureAdmin(); return CourseDAO.deleteCourse(courseId); }

    public boolean addSection(int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) {
        ensureAdmin();
        return SectionDAO.createSection(courseId, instructorId, dayTime, room, capacity, semester, year);
    }
    public List<Section> getAllSections() { ensureAdmin(); return SectionDAO.getAllSections(); }
    public boolean updateSection(Section s) { ensureAdmin(); return SectionDAO.updateSection(s); }
    public boolean deleteSection(int sectionId) { ensureAdmin(); return SectionDAO.deleteSection(sectionId); }

    public boolean setMaintenanceMode(boolean on) { ensureAdmin(); return SettingsDAO.setSetting("maintenance_mode", on ? "ON" : "OFF"); }
    public boolean isMaintenanceOn() { return "ON".equalsIgnoreCase(SettingsDAO.getSetting("maintenance_mode")); }
    public boolean setRegisterDeadline(String isoDate) { ensureAdmin(); return SettingsDAO.setSetting("register_deadline", isoDate); }
    public boolean setDropDeadline(String isoDate) { ensureAdmin(); return SettingsDAO.setSetting("drop_deadline", isoDate); }
    public String getRegisterDeadline() { return SettingsDAO.getSetting("register_deadline"); }
    public String getDropDeadline() { return SettingsDAO.getSetting("drop_deadline"); }

    public String exportEnrollmentsCSV(int sectionId) {
        ensureAdmin();
        return ReportsUtil.exportEnrollmentsCSV(sectionId);
    }
    public boolean exportEnrollmentsPDF(int sectionId, String filePath) {
        ensureAdmin();
        return ReportsUtil.exportEnrollmentsPDF(sectionId, new java.io.File(filePath));
    }

}

