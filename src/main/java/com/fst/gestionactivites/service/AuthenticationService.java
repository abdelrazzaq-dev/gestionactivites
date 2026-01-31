package com.fst.gestionactivites.service;

import com.fst.gestionactivites.model.User;
import com.fst.gestionactivites.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Authentication Service
 * Handles user authentication, password management, and session management
 */
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static AuthenticationService instance;
    private final UserRepository userRepository;
    private User currentUser;

    private AuthenticationService() {
        this.userRepository = UserRepository.getInstance();
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Authenticate a user with login and password
     * @param login
     * @param password
     * @return 
     */
    public boolean login(String login, String password) {
        try {
            Optional<User> userOpt = userRepository.findByLogin(login);

            if (userOpt.isEmpty()) {
                logger.warn("Login failed: User not found - {}", login);
                return false;
            }

            User user = userOpt.get();

            if (!user.isActive()) {
                logger.warn("Login failed: User account is not active - {}", login);
                return false;
            }

            if (!verifyPassword(password, user.getPassword())) {
                logger.warn("Login failed: Invalid password for user - {}", login);
                return false;
            }

            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            this.currentUser = user;
            logger.info("User logged in successfully: {}", login);
            return true;

        } catch (Exception e) {
            logger.error("Login error for user: {}", login, e);
            return false;
        }
    }

    /**
     * Logout the current user
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getLogin());
            currentUser = null;
        }
    }

    /**
     * Get the currently logged-in user
     * @return 
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is currently logged in
     * @return 
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if the current user has admin role
     * @return 
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Check if the current user has professor role
     * @return 
     */
    public boolean isProfessor() {
        return currentUser != null && currentUser.isProfessor();
    }

    /**
     * Check if the current user has student role
     * @return 
     */
    public boolean isStudent() {
        return currentUser != null && currentUser.isStudent();
    }

    /**
     * Register a new user
     * @param login
     * @param password
     * @param email
     * @param role
     * @return 
     */
    public boolean register(String login, String password, String email, User.UserRole role) {
        try {
            // Check if login already exists
            if (userRepository.existsByLogin(login)) {
                logger.warn("Registration failed: Login already exists - {}", login);
                return false;
            }

            // Check if email already exists
            if (userRepository.existsByEmail(email)) {
                logger.warn("Registration failed: Email already exists - {}", email);
                return false;
            }

            // Create new user
            User user = new User();
            user.setLogin(login);
            user.setPassword(hashPassword(password));
            user.setEmail(email);
            user.setRole(role);
            user.setStatus(User.UserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);
            logger.info("User registered successfully: {}", login);
            return true;

        } catch (Exception e) {
            logger.error("Registration error for user: {}", login, e);
            return false;
        }
    }

    /**
     * Change password for the current user
     * @param oldPassword
     * @param newPassword
     * @return 
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }

        try {
            if (!verifyPassword(oldPassword, currentUser.getPassword())) {
                logger.warn("Change password failed: Invalid old password for user - {}", currentUser.getLogin());
                return false;
            }

            currentUser.setPassword(hashPassword(newPassword));
            userRepository.save(currentUser);
            logger.info("Password changed successfully for user: {}", currentUser.getLogin());
            return true;

        } catch (Exception e) {
            logger.error("Change password error for user: {}", currentUser.getLogin(), e);
            return false;
        }
    }

    /**
     * Generate a password reset token
     * @param email
     * @return 
     */
    public String generateResetToken(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                logger.warn("Reset token generation failed: Email not found - {}", email);
                return null;
            }

            User user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours

            userRepository.save(user);
            logger.info("Password reset token generated for user: {}", user.getLogin());
            return token;

        } catch (Exception e) {
            logger.error("Error generating reset token for email: {}", email, e);
            return null;
        }
    }

    /**
     * Reset password using a reset token
     * @param token
     * @param newPassword
     * @return 
     */
    public boolean resetPassword(String token, String newPassword) {
        try {
            Optional<User> userOpt = userRepository.findByResetToken(token);

            if (userOpt.isEmpty()) {
                logger.warn("Password reset failed: Invalid token");
                return false;
            }

            User user = userOpt.get();

            if (!user.isResetTokenValid()) {
                logger.warn("Password reset failed: Token expired for user - {}", user.getLogin());
                return false;
            }

            user.setPassword(hashPassword(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);

            userRepository.save(user);
            logger.info("Password reset successfully for user: {}", user.getLogin());
            return true;

        } catch (Exception e) {
            logger.error("Error resetting password with token", e);
            return false;
        }
    }

    /**
     * Hash a password using BCrypt
     * @param password
     * @return 
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Verify a password against a hash
     * @param password
     * @param hash
     * @return 
     */
    public boolean verifyPassword(String password, String hash) {
        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }

    /**
     * Initialize with a default admin user if no users exist
     */
    public void initializeDefaultAdmin() {
        try {
            if (userRepository.findAll().isEmpty()) {
                register("admin", "admin123", "admin@universite.ma", User.UserRole.ADMIN);
                logger.info("Default admin user created");
            }
        } catch (Exception e) {
            logger.error("Error creating default admin", e);
        }
    }
}
