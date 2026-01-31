package com.fst.gestionactivites.repository;

import com.fst.gestionactivites.data.DatabaseManager;
import com.fst.gestionactivites.model.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Activity entity
 * Handles all database operations for Activities
 */
public class ActivityRepository {
    private static final Logger logger = LoggerFactory.getLogger(ActivityRepository.class);
    private static ActivityRepository instance;
    private final DatabaseManager dbManager;

    private ActivityRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized ActivityRepository getInstance() {
        if (instance == null) {
            instance = new ActivityRepository();
        }
        return instance;
    }

    public void save(Activity activity) {
        dbManager.executeTransaction(em -> {
            if (activity.getId() == null) {
                em.persist(activity);
                logger.info("Created new activity: {}", activity.getTitle());
            } else {
                em.merge(activity);
                logger.info("Updated activity: {}", activity.getTitle());
            }
        });
    }

    public Optional<Activity> findById(Long id) {
        return dbManager.executeQuery(em -> {
            try {
                Activity activity = em.createQuery(
                        "SELECT a FROM Activity a LEFT JOIN FETCH a.participations WHERE a.id = :id", Activity.class)
                        .setParameter("id", id)
                        .getSingleResult();
                return Optional.of(activity);
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    public List<Activity> findAll() {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT DISTINCT a FROM Activity a LEFT JOIN FETCH a.participations ORDER BY a.dateCreated DESC", Activity.class)
                        .getResultList()
        );
    }

    public List<Activity> findByStatus(Activity.ActivityStatus status) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT DISTINCT a FROM Activity a LEFT JOIN FETCH a.participations WHERE a.status = :status ORDER BY a.dateCreated DESC", Activity.class)
                        .setParameter("status", status)
                        .getResultList()
        );
    }

    public List<Activity> findByType(Activity.ActivityType type) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT DISTINCT a FROM Activity a LEFT JOIN FETCH a.participations WHERE a.type = :type ORDER BY a.dateCreated DESC", Activity.class)
                        .setParameter("type", type)
                        .getResultList()
        );
    }

    public List<Activity> findByProfessor(String professor) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT a FROM Activity a WHERE a.professor = :prof ORDER BY a.dateCreated DESC", Activity.class)
                        .setParameter("prof", professor)
                        .getResultList()
        );
    }

    public List<Activity> findByDeadlineBetween(LocalDate start, LocalDate end) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT a FROM Activity a WHERE a.deadline BETWEEN :start AND :end ORDER BY a.deadline", Activity.class)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .getResultList()
        );
    }

    public List<Activity> findUpcoming() {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT a FROM Activity a WHERE a.deadline >= :today ORDER BY a.deadline", Activity.class)
                        .setParameter("today", LocalDate.now())
                        .getResultList()
        );
    }

    public List<Activity> findOverdue() {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT a FROM Activity a WHERE a.deadline < :today AND a.status != :completed ORDER BY a.deadline", Activity.class)
                        .setParameter("today", LocalDate.now())
                        .setParameter("completed", Activity.ActivityStatus.COMPLETED)
                        .getResultList()
        );
    }

    public List<Activity> searchByTitle(String searchTerm) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT DISTINCT a FROM Activity a LEFT JOIN FETCH a.participations WHERE LOWER(a.title) LIKE LOWER(:term) ORDER BY a.dateCreated DESC", Activity.class)
                        .setParameter("term", "%" + searchTerm + "%")
                        .getResultList()
        );
    }

    public List<Activity> advancedSearch(Activity.ActivityStatus status, Activity.ActivityType type, String professor) {
        return dbManager.executeQuery(em -> {
            StringBuilder query = new StringBuilder("SELECT DISTINCT a FROM Activity a LEFT JOIN FETCH a.participations WHERE 1=1");

            if (status != null) {
                query.append(" AND a.status = :status");
            }
            if (type != null) {
                query.append(" AND a.type = :type");
            }
            if (professor != null && !professor.trim().isEmpty()) {
                query.append(" AND LOWER(a.professor) LIKE LOWER(:prof)");
            }

            query.append(" ORDER BY a.dateCreated DESC");

            var typedQuery = em.createQuery(query.toString(), Activity.class);

            if (status != null) {
                typedQuery.setParameter("status", status);
            }
            if (type != null) {
                typedQuery.setParameter("type", type);
            }
            if (professor != null && !professor.trim().isEmpty()) {
                typedQuery.setParameter("prof", "%" + professor + "%");
            }

            return typedQuery.getResultList();
        });
    }

    public void delete(Activity activity) {
        dbManager.executeTransaction(em -> {
            Activity managedActivity = em.find(Activity.class, activity.getId());
            if (managedActivity != null) {
                em.remove(managedActivity);
                logger.info("Deleted activity: {}", activity.getTitle());
            }
        });
    }

    public long count() {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT COUNT(a) FROM Activity a", Long.class)
                        .getSingleResult()
        );
    }

    public long countByStatus(Activity.ActivityStatus status) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT COUNT(a) FROM Activity a WHERE a.status = :status", Long.class)
                        .setParameter("status", status)
                        .getSingleResult()
        );
    }
}
