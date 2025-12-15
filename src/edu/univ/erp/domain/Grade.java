package edu.univ.erp.domain;

public class Grade {
    private int gradeId;
    private int enrollmentId;
    private String component;
    private double score;
    private String finalGrade;

    public Grade(int gradeId, int enrollmentId, String component, double score, String finalGrade) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }


    public int getGradeId() {
        return gradeId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public String getComponent() {
        return component;
    }

    public double getScore() {
        return score;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}
