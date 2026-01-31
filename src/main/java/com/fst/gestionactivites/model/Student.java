package com.fst.gestionactivites.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Student entity
 * Represents a student in the system
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", unique = true, nullable = false, length = 20)
    private String registrationNumber; // e.g., "FST20230001"

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String department; // e.g., "Computer Science", "Mathematics"

    @Column(length = 100)
    private String specialization; // e.g., "Software Engineering", "Data Science"

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_level")
    private AcademicLevel academicLevel;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentParticipation> participations = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user; // Link to User account for authentication

    // Constructors
    public Student() {
        this.status = StudentStatus.ACTIVE;
    }

    public Student(String registrationNumber, String firstName, String lastName, String email) {
        this();
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public AcademicLevel getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(AcademicLevel academicLevel) {
        this.academicLevel = academicLevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public List<StudentParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<StudentParticipation> participations) {
        this.participations = participations;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }

    public int getParticipationCount() {
        return participations.size();
    }

    public double getParticipationRate() {
        if (participations.isEmpty()) {
            return 0.0;
        }
        long participated = participations.stream()
                .filter(StudentParticipation::isParticipated)
                .count();
        return (participated * 100.0) / participations.size();
    }

    public double getAverageScore() {
        return participations.stream()
                .filter(p -> p.getScore() != null)
                .mapToDouble(StudentParticipation::getScore)
                .average()
                .orElse(0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) &&
               Objects.equals(registrationNumber, student.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationNumber);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Academic Level Enumeration
     */
    public enum AcademicLevel {
        LICENSE_1("Licence 1"),
        LICENSE_2("Licence 2"),
        LICENSE_3("Licence 3"),
        MASTER_1("Master 1"),
        MASTER_2("Master 2"),
        DOCTORAT("Doctorat");

        private final String displayName;

        AcademicLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Student Status Enumeration
     */
    public enum StudentStatus {
        ACTIVE("Actif"),
        INACTIVE("Inactif"),
        GRADUATED("Diplômé"),
        SUSPENDED("Suspendu"),
        TRANSFERRED("Transféré");

        private final String displayName;

        StudentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
