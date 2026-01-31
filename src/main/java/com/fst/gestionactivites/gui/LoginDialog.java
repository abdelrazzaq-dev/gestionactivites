package com.fst.gestionactivites.gui;

import com.fst.gestionactivites.service.AuthenticationService;
import com.fst.gestionactivites.service.EmailService;

import javax.swing.*;
import java.awt.*;

/**
 * Login Dialog
 * Handles user authentication
 */
public class LoginDialog extends JDialog {
    private final AuthenticationService authService;
    private final EmailService emailService;

    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton forgotPasswordButton;
    private JLabel statusLabel;

    private boolean authenticated = false;

    public LoginDialog(Frame parent) {
        super(parent, "Connexion - Gestion des Activités", true);
        this.authService = AuthenticationService.getInstance();
        this.emailService = EmailService.getInstance();

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestion des Activités Pédagogiques");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Veuillez vous connecter");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel loginLabel = new JLabel("Identifiant:");
        formPanel.add(loginLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        loginField = new JTextField(20);
        loginField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(loginField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel passwordLabel = new JLabel("Mot de passe:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        loginButton = new JButton("Se connecter");
        loginButton.setPreferredSize(new Dimension(130, 35));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> handleLogin());

        cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(130, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Forgot Password Panel
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        forgotPasswordButton = new JButton("Mot de passe oublié?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(new Color(41, 128, 185));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(e -> handleForgotPassword());
        forgotPanel.add(forgotPasswordButton);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(forgotPanel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter key to login
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        // Disable button during authentication
        loginButton.setEnabled(false);
        statusLabel.setText("Authentification en cours...");
        statusLabel.setForeground(Color.BLUE);

        // Perform authentication in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return authService.login(login, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        authenticated = true;
                        dispose();
                    } else {
                        statusLabel.setText("Identifiant ou mot de passe incorrect");
                        statusLabel.setForeground(Color.RED);
                        passwordField.setText("");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Erreur d'authentification");
                    statusLabel.setForeground(Color.RED);
                    loginButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(
                this,
                "Entrez votre adresse email:",
                "Réinitialisation du mot de passe",
                JOptionPane.QUESTION_MESSAGE
        );

        if (email != null && !email.trim().isEmpty()) {
            String token = authService.generateResetToken(email.trim());
            if (token != null) {
                emailService.sendPasswordResetEmail(email.trim(), token);
                JOptionPane.showMessageDialog(
                        this,
                        "Un code de réinitialisation a été envoyé à votre adresse email.\n" +
                        "Veuillez vérifier votre boîte de réception.",
                        "Email envoyé",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Aucun compte associé à cette adresse email.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
