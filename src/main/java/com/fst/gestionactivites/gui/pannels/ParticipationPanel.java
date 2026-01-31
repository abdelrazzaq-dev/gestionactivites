package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.model.StudentParticipation;
import com.fst.gestionactivites.repository.ActivityRepository;
import com.fst.gestionactivites.repository.ParticipationRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing student participation in activities
 */
public class ParticipationPanel extends JPanel {
    private final ActivityRepository activityRepository;
    private final ParticipationRepository participationRepository;
    private JComboBox<String> activityCombo;
    private JTable participationTable;
    private DefaultTableModel tableModel;
    private JButton editButton, refreshButton;
    private JLabel statsLabel;

    public ParticipationPanel() {
        this.activityRepository = ActivityRepository.getInstance();
        this.participationRepository = ParticipationRepository.getInstance();
        initComponents();
        loadActivities();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Sélection d'activité"));

        topPanel.add(new JLabel("Activité:"));
        activityCombo = new JComboBox<>();
        activityCombo.setPreferredSize(new Dimension(400, 25));
        activityCombo.addActionListener(e -> loadParticipations());
        topPanel.add(activityCombo);

        // Stats Label
        statsLabel = new JLabel(" ");
        statsLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topPanel.add(statsLabel);

        // Table
        String[] columnNames = {"ID", "Étudiant", "Email", "Participé", "Note", "Commentaire"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        participationTable = new JTable(tableModel);
        participationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        participationTable.setRowHeight(25);

        // Hide ID column
        participationTable.getColumnModel().getColumn(0).setMinWidth(0);
        participationTable.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(participationTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        editButton = new JButton("Modifier Participation");
        editButton.addActionListener(e -> editParticipation());

        refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> loadParticipations());

        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadActivities() {
        activityCombo.removeAllItems();
        List<Activity> activities = activityRepository.findAll();
        for (Activity activity : activities) {
            activityCombo.addItem(activity.getId() + " - " + activity.getTitle());
        }
        if (activityCombo.getItemCount() > 0) {
            activityCombo.setSelectedIndex(0);
        }
    }

    private void loadParticipations() {
        tableModel.setRowCount(0);
        statsLabel.setText(" ");

        if (activityCombo.getSelectedItem() == null) {
            return;
        }

        try {
            String selected = (String) activityCombo.getSelectedItem();
            Long activityId = Long.parseLong(selected.split(" - ")[0]);

            Activity activity = activityRepository.findById(activityId).orElse(null);
            if (activity != null) {
                List<StudentParticipation> participations = participationRepository.findByActivity(activity);

                for (StudentParticipation p : participations) {
                    Object[] row = {
                            p.getId(),
                            p.getStudentName(),
                            p.getStudentEmail(),
                            p.isParticipated() ? "Oui" : "Non",
                            p.getScore() != null ? String.format("%.2f", p.getScore()) : "-",
                            p.getFeedback() != null ? p.getFeedback() : ""
                    };
                    tableModel.addRow(row);
                }

                // Update stats
                double rate = participationRepository.getParticipationRateByActivity(activity);
                double avgScore = participationRepository.getAverageScoreByActivity(activity);
                statsLabel.setText(String.format("Taux: %.1f%% | Moyenne: %.2f/100", rate, avgScore));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editParticipation() {
        int selectedRow = participationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une participation",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long participationId = (Long) tableModel.getValueAt(selectedRow, 0);
        StudentParticipation participation = participationRepository.findById(participationId).orElse(null);

        if (participation != null) {
            ParticipationDialog dialog = new ParticipationDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), participation);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadParticipations();
            }
        }
    }
}
