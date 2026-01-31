package com.fst.gestionactivites.repository;

import com.fst.gestionactivites.data.DatabaseManager;
import com.fst.gestionactivites.model.Student;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student entity
 * Handles all database operations for Students
 */
public class StudentRepository {
    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);
    private static StudentRepository instance;
    private final DatabaseManager dbManager;

    private StudentRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepository();
        }
        return instance;
    }

    public void save(Student student) {
        dbManager.executeTransaction(em -> {
            if (student.getId() == null) {
                em.persist(student);
                logger.info("Created new student: {}", student.getRegistrationNumber());
            } else {
                em.merge(student);
                logger.info("Updated student: {}", student.getRegistrationNumber());
            }
        });
    }

    public Optional<Student> findById(Long id) {
        return dbManager.executeQuery(em -> {
            Student student = em.find(Student.class, id);
            return Optional.ofNullable(student);
        });
    }

    public Optional<Student> findByRegistrationNumber(String registrationNumber) {
        return dbManager.executeQuery(em -> {
            try {
                Student student = em.createQuery(
                        "SELECT s FROM Student s WHERE s.registrationNumber = :regNumber", Student.class)
                        .setParameter("regNumber", registrationNumber)
                        .getSingleResult();
                return Optional.of(student);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public Optional<Student> findByEmail(String email) {
        return dbManager.executeQuery(em -> {
            try {
                Student student = em.createQuery(
                        "SELECT s FROM Student s WHERE s.email = :email", Student.class)
                        .setParameter("email", email)
                        .getSingleResult();
                return Optional.of(student);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public List<Student> findAll() {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT s FROM Student s ORDER BY s.lastName, s.firstName", Student.class)
                        .getResultList()
        );
    }

    public List<Student> findByDepartment(String department) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT s FROM Student s WHERE s.department = :dept ORDER BY s.lastName", Student.class)
                        .setParameter("dept", department)
                        .getResultList()
        );
    }

    public List<Student> findByAcademicLevel(Student.AcademicLevel level) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT s FROM Student s WHERE s.academicLevel = :level ORDER BY s.lastName", Student.class)
                        .setParameter("level", level)
                        .getResultList()
        );
    }

    public List<Student> findByStatus(Student.StudentStatus status) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT s FROM Student s WHERE s.status = :status ORDER BY s.lastName", Student.class)
                        .setParameter("status", status)
                        .getResultList()
        );
    }

    public List<Student> searchByName(String searchTerm) {
        return dbManager.executeQuery(em ->
                em.createQuery(
                        "SELECT s FROM Student s WHERE " +
                        "LOWER(s.firstName) LIKE LOWER(:term) OR " +
                        "LOWER(s.lastName) LIKE LOWER(:term) " +
                        "ORDER BY s.lastName, s.firstName", Student.class)
                        .setParameter("term", "%" + searchTerm + "%")
                        .getResultList()
        );
    }

    public void delete(Student student) {
        dbManager.executeTransaction(em -> {
            Student managedStudent = em.find(Student.class, student.getId());
            if (managedStudent != null) {
                em.remove(managedStudent);
                logger.info("Deleted student: {}", student.getRegistrationNumber());
            }
        });
    }

    public boolean existsByRegistrationNumber(String registrationNumber) {
        return dbManager.executeQuery(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(s) FROM Student s WHERE s.registrationNumber = :regNumber", Long.class)
                    .setParameter("regNumber", registrationNumber)
                    .getSingleResult();
            return count > 0;
        });
    }

    public boolean existsByEmail(String email) {
        return dbManager.executeQuery(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(s) FROM Student s WHERE s.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        });
    }

    public long count() {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT COUNT(s) FROM Student s", Long.class)
                        .getSingleResult()
        );
    }
}
