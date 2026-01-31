package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.Student;
import com.fst.gestionactivites.repository.StudentRepository;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding/editing students
 */
public class StudentDialog extends JDialog {
    private final StudentRepository studentRepository;
    private final Student student;
    private boolean saved = false;

    private JTextField regNumberField, firstNameField, lastNameField, emailField;
    private JTextField phoneField, departmentField, specializationField;
    private JTextField enrollmentYearField, dateOfBirthField;
    private JTextArea addressField;
    private JComboBox<String> academicLevelCombo, statusCombo;
    private JButton saveButton, cancelButton;

    public StudentDialog(Frame parent, Student student) {
        super(parent, student == null ? "Ajouter Étudiant" : "Modifier Étudiant", true);
        this.studentRepository = StudentRepository.getInstance();
        this.student = student;

        initComponents();
        if (student != null) {
            loadStudentData();
        }
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(600, 650);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Registration Number
        addFormField(formPanel, gbc, row++, "Matricule:", regNumberField = new JTextField(20));

        // First Name
        addFormField(formPanel, gbc, row++, "Prénom:", firstNameField = new JTextField(20));

        // Last Name
        addFormField(formPanel, gbc, row++, "Nom:", lastNameField = new JTextField(20));

        // Email
        addFormField(formPanel, gbc, row++, "Email:", emailField = new JTextField(20));

        // Phone
        addFormField(formPanel, gbc, row++, "Téléphone:", phoneField = new JTextField(20));

        // Date of Birth
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateOfBirthField = new JTextField(15);
        dobPanel.add(dateOfBirthField);
        dobPanel.add(new JLabel(" (JJ/MM/AAAA)"));
        addFormField(formPanel, gbc, row++, "Date de naissance:", dobPanel);

        // Department
        addFormField(formPanel, gbc, row++, "Département:", departmentField = new JTextField(20));

        // Specialization
        addFormField(formPanel, gbc, row++, "Spécialisation:", specializationField = new JTextField(20));

        // Academic Level
        academicLevelCombo = new JComboBox<>();
        for (Student.AcademicLevel level : Student.AcademicLevel.values()) {
            academicLevelCombo.addItem(level.getDisplayName());
        }
        addFormField(formPanel, gbc, row++, "Niveau:", academicLevelCombo);

        // Enrollment Year
        addFormField(formPanel, gbc, row++, "Année d'inscription:", enrollmentYearField = new JTextField(20));

        // Status
        statusCombo = new JComboBox<>();
        for (Student.StudentStatus status : Student.StudentStatus.values()) {
            statusCombo.addItem(status.getDisplayName());
        }
        addFormField(formPanel, gbc, row++, "Statut:", statusCombo);

        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Adresse:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        addressField = new JTextArea(3, 20);
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(addressField);
        formPanel.add(scrollPane, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        saveButton = new JButton("Enregistrer");
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveStudent());

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

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void loadStudentData() {
        regNumberField.setText(student.getRegistrationNumber());
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());

        if (student.getPhone() != null) {
            phoneField.setText(student.getPhone());
        }
        if (student.getDateOfBirth() != null) {
            dateOfBirthField.setText(student.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (student.getDepartment() != null) {
            departmentField.setText(student.getDepartment());
        }
        if (student.getSpecialization() != null) {
            specializationField.setText(student.getSpecialization());
        }
        if (student.getAcademicLevel() != null) {
            academicLevelCombo.setSelectedItem(student.getAcademicLevel().getDisplayName());
        }
        if (student.getEnrollmentYear() != null) {
            enrollmentYearField.setText(String.valueOf(student.getEnrollmentYear()));
        }
        if (student.getStatus() != null) {
            statusCombo.setSelectedItem(student.getStatus().getDisplayName());
        }
        if (student.getAddress() != null) {
            addressField.setText(student.getAddress());
        }
    }

    private void saveStudent() {
        try {
            // Validate required fields
            if (regNumberField.getText().trim().isEmpty() ||
                    firstNameField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Les champs Matricule, Prénom, Nom et Email sont obligatoires",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate email format
            String email = emailField.getText().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this,
                        "Format d'email invalide",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse date of birth
            LocalDate dateOfBirth = null;
            String dobText = dateOfBirthField.getText().trim();
            if (!dobText.isEmpty()) {
                try {
                    dateOfBirth = LocalDate.parse(dobText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this,
                            "Format de date invalide. Utilisez JJ/MM/AAAA",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Parse enrollment year
            Integer enrollmentYear = null;
            String yearText = enrollmentYearField.getText().trim();
            if (!yearText.isEmpty()) {
                try {
                    enrollmentYear = Integer.parseInt(yearText);
                    if (enrollmentYear < 1900 || enrollmentYear > 2100) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Année d'inscription invalide",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Get academic level
            Student.AcademicLevel academicLevel = null;
            String selectedLevel = (String) academicLevelCombo.getSelectedItem();
            for (Student.AcademicLevel level : Student.AcademicLevel.values()) {
                if (level.getDisplayName().equals(selectedLevel)) {
                    academicLevel = level;
                    break;
                }
            }

            // Get status
            Student.StudentStatus status = null;
            String selectedStatus = (String) statusCombo.getSelectedItem();
            for (Student.StudentStatus st : Student.StudentStatus.values()) {
                if (st.getDisplayName().equals(selectedStatus)) {
                    status = st;
                    break;
                }
            }

            // Create or update student
            Student studentToSave;
            if (student == null) {
                studentToSave = new Student(
                        regNumberField.getText().trim(),
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        email
                );
            } else {
                studentToSave = student;
                studentToSave.setRegistrationNumber(regNumberField.getText().trim());
                studentToSave.setFirstName(firstNameField.getText().trim());
                studentToSave.setLastName(lastNameField.getText().trim());
                studentToSave.setEmail(email);
            }

            studentToSave.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            studentToSave.setDateOfBirth(dateOfBirth);
            studentToSave.setDepartment(departmentField.getText().trim().isEmpty() ? null : departmentField.getText().trim());
            studentToSave.setSpecialization(specializationField.getText().trim().isEmpty() ? null : specializationField.getText().trim());
            studentToSave.setAcademicLevel(academicLevel);
            studentToSave.setEnrollmentYear(enrollmentYear);
            studentToSave.setStatus(status);
            studentToSave.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());

            studentRepository.save(studentToSave);

            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Étudiant enregistré avec succès",
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
