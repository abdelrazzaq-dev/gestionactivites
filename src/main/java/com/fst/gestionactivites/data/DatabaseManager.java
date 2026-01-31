package com.fst.gestionactivites.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database Manager - Manages JPA EntityManagerFactory and EntityManager instances
 * Implements Singleton pattern for EntityManagerFactory
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private EntityManagerFactory entityManagerFactory;

    private DatabaseManager() {
        try {
            logger.info("Initializing database connection...");
            entityManagerFactory = Persistence.createEntityManagerFactory("GestionActivitesPU");
            logger.info("Database connection established successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database connection", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Get a new EntityManager instance
     * Remember to close the EntityManager when done!
     * @return 
     */
    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Shutdown the database connection
     */
    public void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            logger.info("Shutting down database connection...");
            entityManagerFactory.close();
            logger.info("Database connection closed");
        }
    }

    /**
     * Execute a database transaction with automatic resource management
     * @param callback
     */
    public void executeTransaction(TransactionCallback callback) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            callback.execute(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Transaction failed", e);
            throw new RuntimeException("Transaction failed", e);
        } finally {
            em.close();
        }
    }

    /**
     * Execute
     * @param <T>a database query with automatic resource management
     * @param callback
     * @return 
     */
    public <T> T executeQuery(QueryCallback<T> callback) {
        try (EntityManager em = getEntityManager()) {
            return callback.execute(em);
        }
    }

    @FunctionalInterface
    public interface TransactionCallback {
        void execute(EntityManager em);
    }

    @FunctionalInterface
    public interface QueryCallback<T> {
        T execute(EntityManager em);
    }
}
