package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.repository.ActivityRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing activities (CRUD operations, filtering, search)
 */
public class ActivitiesPanel extends JPanel {
    private final ActivityRepository activityRepository;
    private JTable activitiesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JComboBox<String> statusFilter, typeFilter;
    private JTextField searchField;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ActivitiesPanel() {
        this.activityRepository = ActivityRepository.getInstance();
        initComponents();
        loadActivities();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Filters and Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Filtres et Recherche"));

        topPanel.add(new JLabel("Statut:"));
        statusFilter = new JComboBox<>(new String[]{"Tous", "Planifiée", "En cours", "Terminée", "Annulée"});
        statusFilter.addActionListener(e -> filterActivities());
        topPanel.add(statusFilter);

        topPanel.add(new JLabel("Type:"));
        typeFilter = new JComboBox<>(new String[]{"Tous", "Cours", "Devoir", "Projet", "Quiz", "Discussion", "Travaux pratiques", "Séminaire"});
        typeFilter.addActionListener(e -> filterActivities());
        topPanel.add(typeFilter);

        topPanel.add(new JLabel("Recherche:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> filterActivities());
        topPanel.add(searchField);

        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> filterActivities());
        topPanel.add(searchButton);

        // Table
        String[] columnNames = {"ID", "Titre", "Type", "Statut", "Professeur", "Date création", "Échéance"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activitiesTable = new JTable(tableModel);
        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activitiesTable.setRowHeight(25);
        activitiesTable.getTableHeader().setReorderingAllowed(false);

        // Hide ID column but keep it in model
        activitiesTable.getColumnModel().getColumn(0).setMinWidth(0);
        activitiesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        activitiesTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(activitiesTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        addButton = new JButton("Ajouter");
        addButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
        addButton.addActionListener(e -> addActivity());

        editButton = new JButton("Modifier");
        editButton.setIcon(UIManager.getIcon("FileView.computerIcon"));
        editButton.addActionListener(e -> editActivity());

        deleteButton = new JButton("Supprimer");
        deleteButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        deleteButton.addActionListener(e -> deleteActivity());

        refreshButton = new JButton("Actualiser");
        refreshButton.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
        refreshButton.addActionListener(e -> loadActivities());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add all components
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadActivities() {
        try {
            List<Activity> activities = activityRepository.findAll();
            updateTable(activities);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des activités: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterActivities() {
        try {
            String statusText = (String) statusFilter.getSelectedItem();
            String typeText = (String) typeFilter.getSelectedItem();
            String searchText = searchField.getText().trim();

            Activity.ActivityStatus status = null;
            if (!"Tous".equals(statusText)) {
                status = mapStatusFromLabel(statusText);
            }

            Activity.ActivityType type = null;
            if (!"Tous".equals(typeText)) {
                type = mapTypeFromLabel(typeText);
            }

            List<Activity> activities;
            if (!searchText.isEmpty()) {
                activities = activityRepository.searchByTitle(searchText);
            } else if (status != null || type != null) {
                activities = activityRepository.advancedSearch(status, type, null);
            } else {
                activities = activityRepository.findAll();
            }

            updateTable(activities);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Activity> activities) {
        tableModel.setRowCount(0);
        for (Activity activity : activities) {
            Object[] row = {
                    activity.getId(),
                    activity.getTitle(),
                    activity.getType().getLabel(),
                    activity.getStatus().getLabel(),
                    activity.getProfessor(),
                    activity.getDateCreated().format(DATE_FORMATTER),
                    activity.getDeadline() != null ? activity.getDeadline().format(DATE_FORMATTER) : "N/A"
            };
            tableModel.addRow(row);
        }
    }

    private void addActivity() {
        ActivityDialog dialog = new ActivityDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadActivities();
        }
    }

    private void editActivity() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une activité à modifier",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long activityId = (Long) tableModel.getValueAt(selectedRow, 0);
        Activity activity = activityRepository.findById(activityId).orElse(null);

        if (activity != null) {
            ActivityDialog dialog = new ActivityDialog((Frame) SwingUtilities.getWindowAncestor(this), activity);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadActivities();
            }
        }
    }

    private void deleteActivity() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une activité à supprimer",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cette activité?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long activityId = (Long) tableModel.getValueAt(selectedRow, 0);
                Activity activity = activityRepository.findById(activityId).orElse(null);

                if (activity != null) {
                    activityRepository.delete(activity);
                    loadActivities();
                    JOptionPane.showMessageDialog(this,
                            "Activité supprimée avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Activity.ActivityStatus mapStatusFromLabel(String label) {
        for (Activity.ActivityStatus status : Activity.ActivityStatus.values()) {
            if (status.getLabel().equals(label)) {
                return status;
            }
        }
        return null;
    }

    private Activity.ActivityType mapTypeFromLabel(String label) {
        for (Activity.ActivityType type : Activity.ActivityType.values()) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        return null;
    }
}
