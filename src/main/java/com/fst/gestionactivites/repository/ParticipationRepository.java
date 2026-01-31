package com.fst.gestionactivites.repository;

import com.fst.gestionactivites.data.DatabaseManager;
import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.model.Student;
import com.fst.gestionactivites.model.StudentParticipation;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Repository for StudentParticipation entity
 * Handles all database operations for Participations
 */
public class ParticipationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ParticipationRepository.class);
    private static ParticipationRepository instance;
    private final DatabaseManager dbManager;

    private ParticipationRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized ParticipationRepository getInstance() {
        if (instance == null) {
            instance = new ParticipationRepository();
        }
        return instance;
    }

    public void save(StudentParticipation participation) {
        dbManager.executeTransaction(em -> {
            if (participation.getId() == null) {
                em.persist(participation);
                logger.info("Created new participation for student: {}", participation.getStudentName());
            } else {
                em.merge(participation);
                logger.info("Updated participation for student: {}", participation.getStudentName());
            }
        });
    }

    public Optional<StudentParticipation> findById(Long id) {
        return dbManager.executeQuery(em -> {
            try {
                StudentParticipation participation = em.createQuery(
                        "SELECT p FROM StudentParticipation p JOIN FETCH p.student JOIN FETCH p.activity WHERE p.id = :id",
                        StudentParticipation.class)
                        .setParameter("id", id)
                        .getSingleResult();
                return Optional.of(participation);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public Optional<StudentParticipation> findByActivityAndStudent(Activity activity, Student student) {
        return dbManager.executeQuery(em -> {
            try {
                StudentParticipation participation = em.createQuery(
                        "SELECT p FROM StudentParticipation p JOIN FETCH p.student JOIN FETCH p.activity WHERE p.activity = :activity AND p.student = :student",
                        StudentParticipation.class)
                        .setParameter("activity", activity)
                        .setParameter("student", student)
                        .getSingleResult();
                return Optional.of(participation);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public List<StudentParticipation> findByActivity(Activity activity) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT p FROM StudentParticipation p JOIN FETCH p.student WHERE p.activity = :activity ORDER BY p.student.lastName",
                        StudentParticipation.class)
                        .setParameter("activity", activity)
                        .getResultList()
        );
    }

    public List<StudentParticipation> findByStudent(Student student) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT p FROM StudentParticipation p JOIN FETCH p.activity WHERE p.student = :student ORDER BY p.activity.dateCreated DESC",
                        StudentParticipation.class)
                        .setParameter("student", student)
                        .getResultList()
        );
    }

    public List<StudentParticipation> findParticipatedByActivity(Activity activity) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT p FROM StudentParticipation p JOIN FETCH p.student WHERE p.activity = :activity AND p.participated = true ORDER BY p.student.lastName",
                        StudentParticipation.class)
                        .setParameter("activity", activity)
                        .getResultList()
        );
    }

    public List<StudentParticipation> findNonParticipatedByActivity(Activity activity) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT p FROM StudentParticipation p JOIN FETCH p.student WHERE p.activity = :activity AND p.participated = false ORDER BY p.student.lastName",
                        StudentParticipation.class)
                        .setParameter("activity", activity)
                        .getResultList()
        );
    }

    public void delete(StudentParticipation participation) {
        dbManager.executeTransaction(em -> {
            StudentParticipation managedParticipation = em.find(StudentParticipation.class, participation.getId());
            if (managedParticipation != null) {
                em.remove(managedParticipation);
                logger.info("Deleted participation: {}", participation.getId());
            }
        });
    }

    public long countByActivity(Activity activity) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT COUNT(p) FROM StudentParticipation p WHERE p.activity = :activity", Long.class)
                        .setParameter("activity", activity)
                        .getSingleResult()
        );
    }

    public long countParticipatedByActivity(Activity activity) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT COUNT(p) FROM StudentParticipation p WHERE p.activity = :activity AND p.participated = true", Long.class)
                        .setParameter("activity", activity)
                        .getSingleResult()
        );
    }

    public double getAverageScoreByActivity(Activity activity) {
        return dbManager.executeQuery(em -> {
            Double avg = em.createQuery(
                    "SELECT AVG(p.score) FROM StudentParticipation p WHERE p.activity = :activity AND p.score IS NOT NULL", Double.class)
                    .setParameter("activity", activity)
                    .getSingleResult();
            return avg != null ? avg : 0.0;
        });
    }

    public double getParticipationRateByActivity(Activity activity) {
        return dbManager.executeQuery(em -> {
            Long total = countByActivity(activity);
            if (total == 0) return 0.0;
            Long participated = countParticipatedByActivity(activity);
            return (participated * 100.0) / total;
        });
    }
}
