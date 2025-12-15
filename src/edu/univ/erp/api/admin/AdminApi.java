package edu.univ.erp.api.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.AdminService;

import java.util.List;
import java.util.Optional;

public class AdminApi {
    private final AdminService svc = new AdminService();

    // Users
    public String createUser(String username, String role, String plainPassword) {
        return svc.createUser(username, role, plainPassword);
    }
    public List<User> listUsers() { return svc.listUsers(); }

    // Courses
    public boolean addCourse(String code, String title, int credits) { return svc.addCourse(code, title, credits); }
    public List<Course> getAllCourses() { return svc.getAllCourses(); }
    public boolean updateCourse(int courseId, String code, String title, int credits) { return svc.updateCourse(courseId, code, title, credits); }
    public boolean deleteCourse(int courseId) { return svc.deleteCourse(courseId); }

    // Sections
    public boolean addSection(int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) {
        return svc.addSection(courseId, instructorId, dayTime, room, capacity, semester, year);
    }
    public List<Section> getAllSections() { return svc.getAllSections(); }
    public boolean updateSection(Section s) { return svc.updateSection(s); }
    public boolean deleteSection(int sectionId) { return svc.deleteSection(sectionId); }

    // Settings
    public boolean setMaintenanceMode(boolean on) { return svc.setMaintenanceMode(on); }
    public boolean isMaintenanceOn() { return svc.isMaintenanceOn(); }
    public boolean setRegisterDeadline(String isoDate) { return svc.setRegisterDeadline(isoDate); }
    public boolean setDropDeadline(String isoDate) { return svc.setDropDeadline(isoDate); }
    public String getRegisterDeadline() { return svc.getRegisterDeadline(); }
    public String getDropDeadline() { return svc.getDropDeadline(); }

    // Exports
    public String exportEnrollmentsCSV(int sectionId) { return svc.exportEnrollmentsCSV(sectionId); }
    public boolean exportEnrollmentsPDF(int sectionId, String filePath) { return svc.exportEnrollmentsPDF(sectionId, filePath); }
}
