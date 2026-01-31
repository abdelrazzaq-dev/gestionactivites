package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.Student;
import com.fst.gestionactivites.repository.StudentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for managing students
 */
public class StudentPanel extends JPanel {
    private final StudentRepository studentRepository;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JTextField searchField;
    private JComboBox<String> levelFilter, statusFilter;

    public StudentPanel() {
        this.studentRepository = StudentRepository.getInstance();
        initComponents();
        loadStudents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Search and Filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Recherche et Filtres"));

        topPanel.add(new JLabel("Recherche:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchStudents());
        topPanel.add(searchField);

        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> searchStudents());
        topPanel.add(searchButton);

        topPanel.add(new JLabel("Niveau:"));
        levelFilter = new JComboBox<>();
        levelFilter.addItem("Tous");
        for (Student.AcademicLevel level : Student.AcademicLevel.values()) {
            levelFilter.addItem(level.getDisplayName());
        }
        levelFilter.addActionListener(e -> applyFilters());
        topPanel.add(levelFilter);

        topPanel.add(new JLabel("Statut:"));
        statusFilter = new JComboBox<>();
        statusFilter.addItem("Tous");
        for (Student.StudentStatus status : Student.StudentStatus.values()) {
            statusFilter.addItem(status.getDisplayName());
        }
        statusFilter.addActionListener(e -> applyFilters());
        topPanel.add(statusFilter);

        // Table
        String[] columnNames = {"ID", "Matricule", "Nom", "Prénom", "Email", "Téléphone", "Département", "Niveau", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);

        // Hide ID column
        studentTable.getColumnModel().getColumn(0).setMinWidth(0);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        addButton = new JButton("Ajouter Étudiant");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addStudent());

        editButton = new JButton("Modifier");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(e -> editStudent());

        deleteButton = new JButton("Supprimer");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteStudent());

        refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> loadStudents());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        try {
            var students = studentRepository.findAll();
            for (Student student : students) {
                Object[] row = {
                        student.getId(),
                        student.getRegistrationNumber(),
                        student.getLastName(),
                        student.getFirstName(),
                        student.getEmail(),
                        student.getPhone() != null ? student.getPhone() : "-",
                        student.getDepartment() != null ? student.getDepartment() : "-",
                        student.getAcademicLevel() != null ? student.getAcademicLevel().getDisplayName() : "-",
                        student.getStatus() != null ? student.getStatus().getDisplayName() : "-"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudents();
            return;
        }

        tableModel.setRowCount(0);
        try {
            var students = studentRepository.searchByName(searchTerm);
            for (Student student : students) {
                Object[] row = {
                        student.getId(),
                        student.getRegistrationNumber(),
                        student.getLastName(),
                        student.getFirstName(),
                        student.getEmail(),
                        student.getPhone() != null ? student.getPhone() : "-",
                        student.getDepartment() != null ? student.getDepartment() : "-",
                        student.getAcademicLevel() != null ? student.getAcademicLevel().getDisplayName() : "-",
                        student.getStatus() != null ? student.getStatus().getDisplayName() : "-"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la recherche: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        String selectedLevel = (String) levelFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        if ("Tous".equals(selectedLevel) && "Tous".equals(selectedStatus)) {
            loadStudents();
            return;
        }

        tableModel.setRowCount(0);
        try {
            var students = studentRepository.findAll();
            for (Student student : students) {
                boolean matchLevel = "Tous".equals(selectedLevel) ||
                        (student.getAcademicLevel() != null && student.getAcademicLevel().getDisplayName().equals(selectedLevel));
                boolean matchStatus = "Tous".equals(selectedStatus) ||
                        (student.getStatus() != null && student.getStatus().getDisplayName().equals(selectedStatus));

                if (matchLevel && matchStatus) {
                    Object[] row = {
                            student.getId(),
                            student.getRegistrationNumber(),
                            student.getLastName(),
                            student.getFirstName(),
                            student.getEmail(),
                            student.getPhone() != null ? student.getPhone() : "-",
                            student.getDepartment() != null ? student.getDepartment() : "-",
                            student.getAcademicLevel() != null ? student.getAcademicLevel().getDisplayName() : "-",
                            student.getStatus() != null ? student.getStatus().getDisplayName() : "-"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStudent() {
        StudentDialog dialog = new StudentDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadStudents();
        }
    }

    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student != null) {
            StudentDialog dialog = new StudentDialog((Frame) SwingUtilities.getWindowAncestor(this), student);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                loadStudents();
            }
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cet étudiant?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
            Student student = studentRepository.findById(studentId).orElse(null);

            if (student != null) {
                try {
                    studentRepository.delete(student);
                    JOptionPane.showMessageDialog(this,
                            "Étudiant supprimé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadStudents();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la suppression: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
