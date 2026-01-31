package com.fst.gestionactivites;

import com.fst.gestionactivites.data.DatabaseManager;
import com.fst.gestionactivites.gui.LoginDialog;
import com.fst.gestionactivites.gui.MainFrame;
import com.fst.gestionactivites.service.AuthenticationService;
import com.fst.gestionactivites.service.DataInitializationService;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main Application Entry Point
 * Gestion des Activités Pédagogiques
 */
public class GestionActivites {
    private static final Logger logger = LoggerFactory.getLogger(GestionActivites.class);

    public static void main(String[] args) {
        // Set modern look and feel
        try {
            FlatLightLaf.setup();
            logger.info("FlatLaf Look and Feel initialized");
        } catch (Exception ex) {
            logger.warn("Failed to initialize FlatLaf, using system L&F", ex);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                logger.error("Failed to set Look and Feel", e);
            }
        }

        // Initialize database
        try {
            DatabaseManager.getInstance();
            logger.info("Database initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            System.exit(1);
        }

        // Initialize default admin user
        AuthenticationService authService = AuthenticationService.getInstance();
        authService.initializeDefaultAdmin();

        // Initialize test data
        DataInitializationService dataInitService = DataInitializationService.getInstance();
        dataInitService.initializeTestData();

        // Create and display application
        java.awt.EventQueue.invokeLater(() -> {
            try {
                // Show login dialog
                LoginDialog loginDialog = new LoginDialog(null);
                loginDialog.setVisible(true);

                // If authenticated, show main window
                if (loginDialog.isAuthenticated()) {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);

                    // Add shutdown hook
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        logger.info("Shutting down application...");
                        DatabaseManager.getInstance().shutdown();
                    }));
                } else {
                    logger.info("User cancelled login, exiting application");
                    System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Error starting application", e);
                System.exit(1);
            }
        });
    }
}
