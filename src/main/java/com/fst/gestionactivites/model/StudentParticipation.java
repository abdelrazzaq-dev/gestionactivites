package com.fst.gestionactivites.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Student Participation Model
 * Tracks student participation in activities
 * Represents the ParticipationActivité table from requirements
 */
@Entity
@Table(name = "student_participations")
public class StudentParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private boolean participated;

    @Column
    private Double score; // Changed to Double to allow null values

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "participation_date")
    private LocalDateTime participationDate;

    // Constructors
    public StudentParticipation() {
        this.participated = false;
    }

    public StudentParticipation(Activity activity, Student student) {
        this();
        this.activity = activity;
        this.student = student;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isParticipated() {
        return participated;
    }

    public void setParticipated(boolean participated) {
        this.participated = participated;
        if (participated && participationDate == null) {
            this.participationDate = LocalDateTime.now();
        }
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getParticipationDate() {
        return participationDate;
    }

    public void setParticipationDate(LocalDateTime participationDate) {
        this.participationDate = participationDate;
    }

    // Convenience methods for backward compatibility
    public Long getActivityId() {
        return activity != null ? activity.getId() : null;
    }

    public Long getStudentId() {
        return student != null ? student.getId() : null;
    }

    public String getStudentName() {
        return student != null ? student.getFullName() : "";
    }

    public String getStudentEmail() {
        return student != null ? student.getEmail() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentParticipation that = (StudentParticipation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getStudentName() + " (" + (participated ? "Participé" : "Non-participé") + ")";
    }
}
