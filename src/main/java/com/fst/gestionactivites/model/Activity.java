package com.fst.gestionactivites.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Activity Domain Model
 * Represents an educational activity/task
 */
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_created", nullable = false)
    private LocalDate dateCreated;

    @Column
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Column(length = 100)
    private String professor;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentParticipation> participations = new ArrayList<>();

    public enum ActivityStatus {
        PLANNED("Planifiée"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminée"),
        CANCELLED("Annulée");

        private final String label;

        ActivityStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum ActivityType {
        LECTURE("Cours"),
        ASSIGNMENT("Devoir"),
        PROJECT("Projet"),
        QUIZ("Quiz"),
        DISCUSSION("Discussion"),
        PRACTICAL("Travaux pratiques"),
        SEMINAR("Séminaire"),
        WORKSHOP("Atelier"),
        EXAM("Examen");

        private final String label;

        ActivityType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    // Constructors
    public Activity() {
        this.dateCreated = LocalDate.now();
        this.participations = new ArrayList<>();
        this.status = ActivityStatus.PLANNED;
    }

    public Activity(String title, String description, ActivityType type, String professor) {
        this();
        this.title = title;
        this.description = description;
        this.type = type;
        this.professor = professor;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public List<StudentParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<StudentParticipation> participations) {
        this.participations = participations;
    }

    public void addParticipation(StudentParticipation participation) {
        participations.add(participation);
        participation.setActivity(this);
    }

    public void removeParticipation(StudentParticipation participation) {
        participations.remove(participation);
        participation.setActivity(null);
    }

    public double getParticipationRate() {
        if (participations.isEmpty()) {
            return 0;
        }
        long participating = participations.stream()
                .filter(StudentParticipation::isParticipated)
                .count();
        return (participating * 100.0) / participations.size();
    }

    public double getAverageScore() {
        if (participations.isEmpty()) {
            return 0;
        }
        return participations.stream()
                .filter(p -> p.getScore() != null)
                .mapToDouble(StudentParticipation::getScore)
                .average()
                .orElse(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title;
    }
}
