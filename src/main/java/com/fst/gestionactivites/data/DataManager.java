package com.fst.gestionactivites.data;

import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.model.Student;
import com.fst.gestionactivites.model.StudentParticipation;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Data Manager - Handles all data operations
 * Uses in-memory storage (temporary - will be replaced with database repositories)
 */
public class DataManager {
    private static DataManager instance;
    private List<Activity> activities;
    private List<Student> students;
    private List<StudentParticipation> participations;
    private long activityIdCounter = 100L;
    private long studentIdCounter = 1000L;
    private long participationIdCounter = 1L;

    private DataManager() {
        this.activities = new ArrayList<>();
        this.students = new ArrayList<>();
        this.participations = new ArrayList<>();
        initializeTestData();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // Activity Methods
    public void addActivity(Activity activity) {
        activity.setId(activityIdCounter++);
        activities.add(activity);

        // Initialize student participations for this activity
        for (Student student : students) {
            StudentParticipation sp = new StudentParticipation(activity, student);
            sp.setId(participationIdCounter++);
            participations.add(sp);
            activity.addParticipation(sp);
        }
    }

    public void updateActivity(Activity activity) {
        activities.replaceAll(a -> Objects.equals(a.getId(), activity.getId()) ? activity : a);
    }

    public void deleteActivity(Long activityId) {
        activities.removeIf(a -> Objects.equals(a.getId(), activityId));
        participations.removeIf(p -> Objects.equals(p.getActivityId(), activityId));
    }

    public Activity getActivityById(Long id) {
        return activities.stream()
                .filter(a -> Objects.equals(a.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public List<Activity> getAllActivities() {
        return new ArrayList<>(activities);
    }

    public List<Activity> getActivitiesByStatus(Activity.ActivityStatus status) {
        return activities.stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Activity> getActivitiesByType(Activity.ActivityType type) {
        return activities.stream()
                .filter(a -> a.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Activity> getActivitiesByProfessor(String professor) {
        return activities.stream()
                .filter(a -> a.getProfessor() != null && a.getProfessor().equalsIgnoreCase(professor))
                .collect(Collectors.toList());
    }

    // Student Methods
    public void addStudent(Student student) {
        student.setId(studentIdCounter++);
        students.add(student);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public Student getStudentById(Long id) {
        return students.stream()
                .filter(s -> Objects.equals(s.getId(), id))
                .findFirst()
                .orElse(null);
    }

    // Participation Methods
    public void updateParticipation(StudentParticipation participation) {
        participations.replaceAll(p ->
                Objects.equals(p.getId(), participation.getId()) ? participation : p);

        // Update in activity as well
        Activity activity = participation.getActivity();
        if (activity != null) {
            activity.getParticipations().replaceAll(p ->
                    Objects.equals(p.getId(), participation.getId()) ? participation : p);
        }
    }

    public StudentParticipation getParticipationById(Long id) {
        return participations.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public List<StudentParticipation> getParticipationsByActivity(Long activityId) {
        return participations.stream()
                .filter(p -> Objects.equals(p.getActivityId(), activityId))
                .collect(Collectors.toList());
    }

    public List<StudentParticipation> getParticipationsByStudent(Long studentId) {
        return participations.stream()
                .filter(p -> Objects.equals(p.getStudentId(), studentId))
                .collect(Collectors.toList());
    }

    // Statistics Methods
    public double getOverallParticipationRate() {
        if (participations.isEmpty()) return 0;
        long participating = participations.stream()
                .filter(StudentParticipation::isParticipated)
                .count();
        return (participating * 100.0) / participations.size();
    }

    public double getOverallAverageScore() {
        if (participations.isEmpty()) return 0;
        return participations.stream()
                .filter(StudentParticipation::isParticipated)
                .filter(p -> p.getScore() != null)
                .mapToDouble(StudentParticipation::getScore)
                .average()
                .orElse(0);
    }

    public Map<String, Integer> getActivitiesByTypeCount() {
        Map<String, Integer> counts = new HashMap<>();
        activities.forEach(a ->
                counts.merge(a.getType().getLabel(), 1, Integer::sum)
        );
        return counts;
    }

    // Helper Methods
    private void generateStudents() {
        String[] firstNames = {"Ahmed", "Fatima", "Mohamed", "Aicha", "Hassan",
                "Layla", "Ibrahim", "Noor", "Omar", "Zainab",
                "Khalid", "Samira", "Ali", "Leila", "Youssef"};

        for (int i = 0; i < firstNames.length; i++) {
            String firstName = firstNames[i];
            String lastName = "Student" + (i + 1);
            String regNumber = "FST2024" + String.format("%04d", i + 1);
            String email = firstName.toLowerCase() + "@universite.ma";

            Student student = new Student(regNumber, firstName, lastName, email);
            student.setDepartment("Informatique");
            student.setAcademicLevel(Student.AcademicLevel.LICENSE_3);
            student.setEnrollmentYear(2024);
            addStudent(student);
        }
    }

    private void initializeTestData() {
        // Generate students first
        generateStudents();

        // Create sample activities
        Activity activity1 = new Activity(
                "Introduction aux Bases de Données",
                "Cours magistral sur les concepts fondamentaux des BD",
                Activity.ActivityType.LECTURE,
                "Dr. Ahmed Benali"
        );
        activity1.setDeadline(LocalDate.now().plusDays(7));
        activity1.setStatus(Activity.ActivityStatus.IN_PROGRESS);
        addActivity(activity1);

        Activity activity2 = new Activity(
                "Projet de Gestion d'Entreprise",
                "Projet de groupe pour développer un système de gestion",
                Activity.ActivityType.PROJECT,
                "Pr. Fatima Haouari"
        );
        activity2.setDeadline(LocalDate.now().plusDays(30));
        activity2.setStatus(Activity.ActivityStatus.IN_PROGRESS);
        addActivity(activity2);

        Activity activity3 = new Activity(
                "Quiz sur les Algorithmes",
                "Évaluation courte sur les structures de données",
                Activity.ActivityType.QUIZ,
                "Dr. Ahmed Benali"
        );
        activity3.setDeadline(LocalDate.now().minusDays(2));
        activity3.setStatus(Activity.ActivityStatus.COMPLETED);
        addActivity(activity3);

        Activity activity4 = new Activity(
                "Travaux Pratiques - Programmation Python",
                "Exercices pratiques sur Python et les algorithmes",
                Activity.ActivityType.PRACTICAL,
                "Ing. Hassan Idrissi"
        );
        activity4.setDeadline(LocalDate.now().plusDays(14));
        activity4.setStatus(Activity.ActivityStatus.PLANNED);
        addActivity(activity4);

        // Simulate some participation in quiz
        Activity quiz = getActivityById(102L);
        if (quiz != null && !quiz.getParticipations().isEmpty()) {
            for (int i = 0; i < Math.min(12, quiz.getParticipations().size()); i++) {
                StudentParticipation sp = quiz.getParticipations().get(i);
                sp.setParticipated(true);
                sp.setScore(50 + Math.random() * 50);
                updateParticipation(sp);
            }
        }
    }
}
