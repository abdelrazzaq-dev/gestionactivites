package com.fst.gestionactivites.service;

import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.model.Student;
import com.fst.gestionactivites.model.StudentParticipation;
import com.fst.gestionactivites.repository.ActivityRepository;
import com.fst.gestionactivites.repository.ParticipationRepository;
import com.fst.gestionactivites.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Service to initialize the database with test data
 */
public class DataInitializationService {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    private static DataInitializationService instance;

    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final ParticipationRepository participationRepository;

    private DataInitializationService() {
        this.studentRepository = StudentRepository.getInstance();
        this.activityRepository = ActivityRepository.getInstance();
        this.participationRepository = ParticipationRepository.getInstance();
    }

    public static synchronized DataInitializationService getInstance() {
        if (instance == null) {
            instance = new DataInitializationService();
        }
        return instance;
    }

    /**
     * Initialize database with test data if empty
     */
    public void initializeTestData() {
        try {
            // Check if data already exists
            if (activityRepository.count() > 0) {
                logger.info("Database already contains data, skipping initialization");
                return;
            }

            logger.info("Initializing database with test data...");

            // Create students
            Student[] students = createStudents();

            // Create activities
            Activity activity1 = new Activity(
                    "Introduction aux Bases de Données",
                    "Cours magistral sur les concepts fondamentaux des BD",
                    Activity.ActivityType.LECTURE,
                    "Dr. Ahmed Benali"
            );
            activity1.setDeadline(LocalDate.now().plusDays(7));
            activity1.setStatus(Activity.ActivityStatus.IN_PROGRESS);
            activityRepository.save(activity1);

            Activity activity2 = new Activity(
                    "Projet de Gestion d'Entreprise",
                    "Projet de groupe pour développer un système de gestion",
                    Activity.ActivityType.PROJECT,
                    "Pr. Fatima Haouari"
            );
            activity2.setDeadline(LocalDate.now().plusDays(30));
            activity2.setStatus(Activity.ActivityStatus.IN_PROGRESS);
            activityRepository.save(activity2);

            Activity activity3 = new Activity(
                    "Quiz sur les Algorithmes",
                    "Évaluation courte sur les structures de données",
                    Activity.ActivityType.QUIZ,
                    "Dr. Ahmed Benali"
            );
            activity3.setDeadline(LocalDate.now().minusDays(2));
            activity3.setStatus(Activity.ActivityStatus.COMPLETED);
            activityRepository.save(activity3);

            Activity activity4 = new Activity(
                    "Travaux Pratiques - Programmation Python",
                    "Exercices pratiques sur Python et les algorithmes",
                    Activity.ActivityType.PRACTICAL,
                    "Ing. Hassan Idrissi"
            );
            activity4.setDeadline(LocalDate.now().plusDays(14));
            activity4.setStatus(Activity.ActivityStatus.PLANNED);
            activityRepository.save(activity4);

            // Additional activities for testing search functionality
            Activity activity5 = new Activity(
                    "Développement Web avec React",
                    "Atelier pratique sur la création d'applications web modernes avec React",
                    Activity.ActivityType.WORKSHOP,
                    "Pr. Fatima Haouari"
            );
            activity5.setDeadline(LocalDate.now().plusDays(10));
            activity5.setStatus(Activity.ActivityStatus.IN_PROGRESS);
            activityRepository.save(activity5);

            Activity activity6 = new Activity(
                    "Examen Final - Programmation Orientée Objet",
                    "Évaluation finale du module POO avec Java",
                    Activity.ActivityType.EXAM,
                    "Dr. Ahmed Benali"
            );
            activity6.setDeadline(LocalDate.now().plusDays(20));
            activity6.setStatus(Activity.ActivityStatus.PLANNED);
            activityRepository.save(activity6);

            Activity activity7 = new Activity(
                    "Séminaire sur l'Intelligence Artificielle",
                    "Présentation des dernières avancées en IA et Machine Learning",
                    Activity.ActivityType.SEMINAR,
                    "Pr. Karim Zahraoui"
            );
            activity7.setDeadline(LocalDate.now().plusDays(5));
            activity7.setStatus(Activity.ActivityStatus.IN_PROGRESS);
            activityRepository.save(activity7);

            Activity activity8 = new Activity(
                    "Quiz - Réseaux Informatiques",
                    "Évaluation rapide sur les protocoles réseau",
                    Activity.ActivityType.QUIZ,
                    "Ing. Hassan Idrissi"
            );
            activity8.setDeadline(LocalDate.now().minusDays(5));
            activity8.setStatus(Activity.ActivityStatus.COMPLETED);
            activityRepository.save(activity8);

            Activity activity9 = new Activity(
                    "Projet - Application Mobile Android",
                    "Développement d'une application mobile complète en groupe",
                    Activity.ActivityType.PROJECT,
                    "Pr. Fatima Haouari"
            );
            activity9.setDeadline(LocalDate.now().plusDays(45));
            activity9.setStatus(Activity.ActivityStatus.IN_PROGRESS);
            activityRepository.save(activity9);

            Activity activity10 = new Activity(
                    "TP - Sécurité Informatique",
                    "Exercices pratiques sur le cryptage et la sécurité des systèmes",
                    Activity.ActivityType.PRACTICAL,
                    "Dr. Nadia El Ouardi"
            );
            activity10.setDeadline(LocalDate.now().plusDays(12));
            activity10.setStatus(Activity.ActivityStatus.PLANNED);
            activityRepository.save(activity10);

            Activity activity11 = new Activity(
                    "Conférence - Cloud Computing",
                    "Introduction aux services cloud AWS et Azure",
                    Activity.ActivityType.SEMINAR,
                    "Pr. Karim Zahraoui"
            );
            activity11.setDeadline(LocalDate.now().minusDays(1));
            activity11.setStatus(Activity.ActivityStatus.COMPLETED);
            activityRepository.save(activity11);

            Activity activity12 = new Activity(
                    "Atelier - Design Patterns",
                    "Apprentissage des patterns de conception logicielle",
                    Activity.ActivityType.WORKSHOP,
                    "Dr. Ahmed Benali"
            );
            activity12.setDeadline(LocalDate.now().plusDays(8));
            activity12.setStatus(Activity.ActivityStatus.IN_PROGRESS);
            activityRepository.save(activity12);

            // Create participations for each activity
            createParticipations(activity1, students);
            createParticipations(activity2, students);
            createParticipations(activity3, students);
            createParticipations(activity4, students);
            createParticipations(activity5, students);
            createParticipations(activity6, students);
            createParticipations(activity7, students);
            createParticipations(activity8, students);
            createParticipations(activity9, students);
            createParticipations(activity10, students);
            createParticipations(activity11, students);
            createParticipations(activity12, students);

            // Add some scores to the quizzes
            addQuizScores(activity3, students);
            addQuizScores(activity8, students);

            logger.info("Test data initialization completed successfully");

        } catch (Exception e) {
            logger.error("Error initializing test data", e);
        }
    }

    private Student[] createStudents() {
        String[] firstNames = {"Ahmed", "Fatima", "Mohamed", "Aicha", "Hassan",
                "Layla", "Ibrahim", "Noor", "Omar", "Zainab",
                "Khalid", "Samira", "Ali", "Leila", "Youssef"};

        Student[] students = new Student[firstNames.length];

        for (int i = 0; i < firstNames.length; i++) {
            String firstName = firstNames[i];
            String lastName = "Student" + (i + 1);
            String regNumber = "FST2024" + String.format("%04d", i + 1);
            String email = firstName.toLowerCase() + "@universite.ma";

            Student student = new Student(regNumber, firstName, lastName, email);
            student.setDepartment("Informatique");
            student.setAcademicLevel(Student.AcademicLevel.LICENSE_3);
            student.setEnrollmentYear(2024);
            studentRepository.save(student);

            students[i] = student;
        }

        return students;
    }

    private void createParticipations(Activity activity, Student[] students) {
        for (Student student : students) {
            StudentParticipation participation = new StudentParticipation(activity, student);
            participationRepository.save(participation);
        }
    }

    private void addQuizScores(Activity quiz, Student[] students) {
        // Reload activity to get participations
        Activity reloadedQuiz = activityRepository.findById(quiz.getId()).orElse(null);
        if (reloadedQuiz == null) return;

        // Add scores to first 12 students
        for (int i = 0; i < Math.min(12, students.length); i++) {
            Student student = students[i];

            // Find participation for this student and activity
            StudentParticipation participation = participationRepository
                    .findByActivityAndStudent(reloadedQuiz, student)
                    .orElse(null);

            if (participation != null) {
                participation.setParticipated(true);
                participation.setScore(50 + Math.random() * 50);
                participationRepository.save(participation);
            }
        }
    }
}
