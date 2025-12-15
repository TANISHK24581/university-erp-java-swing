package edu.univ.erp.api.student.responses;

public class GradeRow {
    private String courseCode;
    private double quiz;
    private double midterm;
    private double endSem;
    private double finalGrade;  // add this field

    public GradeRow(String courseCode, double quiz, double midterm, double endSem, double finalGrade) {
        this.courseCode = courseCode;
        this.quiz = quiz;
        this.midterm = midterm;
        this.endSem = endSem;
        this.finalGrade = finalGrade;
    }



    // getters
    public String getCourseCode() { return courseCode; }
    public double getQuiz() { return quiz; }
    public double getMidterm() { return midterm; }
    public double getEndSem() { return endSem; }
    public double getFinalGrade() { return finalGrade; }
}
