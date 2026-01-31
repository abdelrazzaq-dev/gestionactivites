package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.StudentParticipation;
import com.fst.gestionactivites.repository.ParticipationRepository;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for editing student participation records
 */
public class ParticipationDialog extends JDialog {
    private final ParticipationRepository participationRepository;
    private final StudentParticipation participation;
    private boolean saved = false;

    private JLabel studentLabel;
    private JCheckBox participatedCheckBox;
    private JTextField scoreField;
    private JTextArea feedbackArea;
    private JButton saveButton, cancelButton;

    public ParticipationDialog(Frame parent, StudentParticipation participation) {
        super(parent, "Modifier Participation", true);
        this.participationRepository = ParticipationRepository.getInstance();
        this.participation = participation;

        initComponents();
        loadParticipationData();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(500, 400);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Étudiant:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        studentLabel = new JLabel();
        studentLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(studentLabel, gbc);

        // Participated checkbox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        participatedCheckBox = new JCheckBox("L'étudiant a participé");
        formPanel.add(participatedCheckBox, gbc);

        // Score
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Note (0-100):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        scoreField = new JTextField(10);
        formPanel.add(scoreField, gbc);

        // Feedback
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Commentaire:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        feedbackArea = new JTextArea(5, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        formPanel.add(scrollPane, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        saveButton = new JButton("Enregistrer");
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveParticipation());

        cancelButton = new JButton("Annuler");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveButton);
    }

    private void loadParticipationData() {
        studentLabel.setText(participation.getStudentName() + " (" + participation.getStudentEmail() + ")");
        participatedCheckBox.setSelected(participation.isParticipated());
        if (participation.getScore() != null) {
            scoreField.setText(String.valueOf(participation.getScore()));
        }
        if (participation.getFeedback() != null) {
            feedbackArea.setText(participation.getFeedback());
        }
    }

    private void saveParticipation() {
        try {
            // Validate score
            Double score = null;
            String scoreText = scoreField.getText().trim();
            if (!scoreText.isEmpty()) {
                try {
                    score = Double.parseDouble(scoreText);
                    if (score < 0 || score > 100) {
                        JOptionPane.showMessageDialog(this,
                                "La note doit être entre 0 et 100",
                                "Validation",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Format de note invalide",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Update participation
            participation.setParticipated(participatedCheckBox.isSelected());
            participation.setScore(score);
            participation.setFeedback(feedbackArea.getText().trim());

            participationRepository.save(participation);

            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Participation mise à jour avec succès",
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
}
