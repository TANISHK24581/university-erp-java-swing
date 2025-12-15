package edu.univ.erp.api.student.requests;

public class RegisterSectionRequest {
    private int studentId;
    private int sectionId;

    public RegisterSectionRequest(int studentId, int sectionId) {
        this.studentId = studentId;
        this.sectionId = sectionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSectionId() {
        return sectionId;
    }
}
