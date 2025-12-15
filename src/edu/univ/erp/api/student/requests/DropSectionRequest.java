package edu.univ.erp.api.student.requests;

public class DropSectionRequest {
    private final int sectionId;

    public DropSectionRequest(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getSectionId() {
        return sectionId;
    }
}

