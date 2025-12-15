package edu.univ.erp.api.student.responses;

public class TimetableRow {
    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final String day;
    private final String time;
    private final String room;

    public TimetableRow(int sectionId, String courseCode, String courseTitle,
                        String day, String time, String room) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.day = day;
        this.time = time;
        this.room = room;
    }

    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getRoom() { return room; }
}
