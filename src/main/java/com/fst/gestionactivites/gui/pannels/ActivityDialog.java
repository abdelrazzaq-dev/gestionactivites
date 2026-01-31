package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.repository.ActivityRepository;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for creating and editing activities
 */
public class ActivityDialog extends JDialog {
    private final ActivityRepository activityRepository;
    private final Activity activity;
    private boolean saved = false;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> typeCombo;
    private JComboBox<String> statusCombo;
    private JTextField professorField;
    private JTextField deadlineField;
    private JButton saveButton, cancelButton;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ActivityDialog(Frame parent, Activity activity) {
        super(parent, activity == null ? "Nouvelle Activité" : "Modifier Activité", true);
        this.activityRepository = ActivityRepository.getInstance();
        this.activity = activity;

        initComponents();
        if (activity != null) {
            loadActivityData();
        }
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(600, 500);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Titre *:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Type *:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        typeCombo = new JComboBox<>(new String[]{
                "Cours", "Devoir", "Projet", "Quiz", "Discussion", "Travaux pratiques", "Séminaire"
        });
        formPanel.add(typeCombo, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Statut *:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        statusCombo = new JComboBox<>(new String[]{
                "Planifiée", "En cours", "Terminée", "Annulée"
        });
        formPanel.add(statusCombo, gbc);

        // Professor
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Professeur:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        professorField = new JTextField(30);
        formPanel.add(professorField, gbc);

        // Deadline
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Échéance (JJ/MM/AAAA):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        deadlineField = new JTextField(15);
        formPanel.add(deadlineField, gbc);

        // Help text
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JLabel helpLabel = new JLabel("* Champs obligatoires");
        helpLabel.setForeground(Color.GRAY);
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(helpLabel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        saveButton = new JButton("Enregistrer");
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveActivity());

        cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }

    private void loadActivityData() {
        titleField.setText(activity.getTitle());
        descriptionArea.setText(activity.getDescription());
        typeCombo.setSelectedItem(activity.getType().getLabel());
        statusCombo.setSelectedItem(activity.getStatus().getLabel());
        professorField.setText(activity.getProfessor());
        if (activity.getDeadline() != null) {
            deadlineField.setText(activity.getDeadline().format(DATE_FORMATTER));
        }
    }

    private void saveActivity() {
        // Validation
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le titre est obligatoire",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return;
        }

        try {
            // Parse deadline if provided
            LocalDate deadline = null;
            String deadlineText = deadlineField.getText().trim();
            if (!deadlineText.isEmpty()) {
                try {
                    deadline = LocalDate.parse(deadlineText, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this,
                            "Format de date invalide. Utilisez JJ/MM/AAAA",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE);
                    deadlineField.requestFocus();
                    return;
                }
            }

            // Create or update activity
            Activity activityToSave = (activity != null) ? activity : new Activity();

            activityToSave.setTitle(titleField.getText().trim());
            activityToSave.setDescription(descriptionArea.getText().trim());
            activityToSave.setType(mapTypeFromLabel((String) typeCombo.getSelectedItem()));
            activityToSave.setStatus(mapStatusFromLabel((String) statusCombo.getSelectedItem()));
            activityToSave.setProfessor(professorField.getText().trim());
            activityToSave.setDeadline(deadline);

            // Save to database
            activityRepository.save(activityToSave);

            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Activité enregistrée avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private Activity.ActivityType mapTypeFromLabel(String label) {
        for (Activity.ActivityType type : Activity.ActivityType.values()) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        return Activity.ActivityType.LECTURE;
    }

    private Activity.ActivityStatus mapStatusFromLabel(String label) {
        for (Activity.ActivityStatus status : Activity.ActivityStatus.values()) {
            if (status.getLabel().equals(label)) {
                return status;
            }
        }
        return Activity.ActivityStatus.PLANNED;
    }
}
