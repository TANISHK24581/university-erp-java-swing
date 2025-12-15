package edu.univ.erp.api.student.responses;


public class SectionRow {
    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final String instructor;
    private final String dayTime;
    private final String room;
    private final int capacity;
    private final int enrolled;
    private final String semester;
    private final int year;

    public SectionRow(int sectionId, String courseCode, String courseTitle,
                      String instructor, String dayTime, String room,
                      int capacity, int enrolled, String semester, int year) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.instructor = instructor;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.semester = semester;
        this.year = year;
    }

    // getters
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getInstructor() { return instructor; }
    public String getDayTime() { return dayTime; }
    public String getRoom() { return room; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
}
