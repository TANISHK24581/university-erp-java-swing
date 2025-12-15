package edu.univ.erp.api.student.responses;

public class CourseRow {
    private String code;
    private String title;
    private int credits;
    private int capacity;
    private String instructor;

    public CourseRow(String code, String title, int credits, int capacity, String instructor) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.capacity = capacity;
        this.instructor = instructor;
    }

    // getters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public int getCapacity() { return capacity; }
    public String getInstructor() { return instructor; }
}
