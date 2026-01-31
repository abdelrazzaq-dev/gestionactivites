package com.fst.gestionactivites.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Email Service
 * Handles sending emails for password reset and notifications
 */
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static EmailService instance;

    // Email configuration (should be moved to config file in production)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "noreply@universite.ma";
    private static final String FROM_PASSWORD = "your-password-here"; // Use app password for Gmail

    private EmailService() {
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    /**
     * Send password reset email
     */
    public boolean sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "Réinitialisation de mot de passe - Gestion des Activités";
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;

        String body = String.format(
                "Bonjour,\n\n" +
                "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
                "Veuillez utiliser le code suivant pour réinitialiser votre mot de passe :\n\n" +
                "Code: %s\n\n" +
                "Ce code est valide pendant 24 heures.\n\n" +
                "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.\n\n" +
                "Cordialement,\n" +
                "L'équipe Gestion des Activités",
                resetToken
        );

        return sendEmail(toEmail, subject, body);
    }

    /**
     * Send welcome email to new user
     */
    public boolean sendWelcomeEmail(String toEmail, String login) {
        String subject = "Bienvenue - Gestion des Activités";
        String body = String.format(
                "Bonjour %s,\n\n" +
                "Bienvenue sur la plateforme Gestion des Activités Pédagogiques!\n\n" +
                "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter avec votre identifiant.\n\n" +
                "Cordialement,\n" +
                "L'équipe Gestion des Activités",
                login
        );

        return sendEmail(toEmail, subject, body);
    }

    /**
     * Generic method to send email
     */
    private boolean sendEmail(String toEmail, String subject, String body) {
        try {
            // For development/testing: just log the email instead of actually sending it
            logger.info("=== EMAIL (Development Mode) ===");
            logger.info("To: {}", toEmail);
            logger.info("Subject: {}", subject);
            logger.info("Body:\n{}", body);
            logger.info("================================");

            // In production, uncomment this to actually send emails:
            /*
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            logger.info("Email sent successfully to: {}", toEmail);
            */

            return true;

        } catch (Exception e) {
            logger.error("Failed to send email to: {}", toEmail, e);
            return false;
        }
    }
}
