package com.fst.gestionactivites.repository;

import com.fst.gestionactivites.data.DatabaseManager;
import com.fst.gestionactivites.model.User;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity
 * Handles all database operations for Users
 */
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private static UserRepository instance;
    private final DatabaseManager dbManager;

    private UserRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void save(User user) {
        dbManager.executeTransaction(em -> {
            if (user.getId() == null) {
                em.persist(user);
                logger.info("Created new user: {}", user.getLogin());
            } else {
                em.merge(user);
                logger.info("Updated user: {}", user.getLogin());
            }
        });
    }

    public Optional<User> findById(Long id) {
        return dbManager.executeQuery(em -> {
            User user = em.find(User.class, id);
            return Optional.ofNullable(user);
        });
    }

    public Optional<User> findByLogin(String login) {
        return dbManager.executeQuery(em -> {
            try {
                User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                        .setParameter("login", login)
                        .getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public Optional<User> findByEmail(String email) {
        return dbManager.executeQuery(em -> {
            try {
                User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                        .setParameter("email", email)
                        .getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public Optional<User> findByResetToken(String token) {
        return dbManager.executeQuery(em -> {
            try {
                User user = em.createQuery("SELECT u FROM User u WHERE u.resetToken = :token", User.class)
                        .setParameter("token", token)
                        .getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public List<User> findAll() {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT u FROM User u ORDER BY u.createdAt DESC", User.class)
                        .getResultList()
        );
    }

    public List<User> findByRole(User.UserRole role) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT u FROM User u WHERE u.role = :role ORDER BY u.login", User.class)
                        .setParameter("role", role)
                        .getResultList()
        );
    }

    public List<User> findByStatus(User.UserStatus status) {
        return dbManager.executeQuery(em ->
                em.createQuery("SELECT u FROM User u WHERE u.status = :status ORDER BY u.login", User.class)
                        .setParameter("status", status)
                        .getResultList()
        );
    }

    public void delete(User user) {
        dbManager.executeTransaction(em -> {
            User managedUser = em.find(User.class, user.getId());
            if (managedUser != null) {
                em.remove(managedUser);
                logger.info("Deleted user: {}", user.getLogin());
            }
        });
    }

    public boolean existsByLogin(String login) {
        return dbManager.executeQuery(em -> {
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class)
                    .setParameter("login", login)
                    .getSingleResult();
            return count > 0;
        });
    }

    public boolean existsByEmail(String email) {
        return dbManager.executeQuery(em -> {
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        });
    }
}
