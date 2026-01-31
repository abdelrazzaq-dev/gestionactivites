/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.fst.gestionactivites.gui;

import com.fst.gestionactivites.gui.pannels.ActivitiesPanel;
import com.fst.gestionactivites.gui.pannels.ParticipationPanel;
import com.fst.gestionactivites.gui.pannels.StatisticsPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author abderrazzak
 */
public class MainFrame extends javax.swing.JFrame {

    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private ActivitiesPanel activitiesPanel;
    private ParticipationPanel participationPanel;
    private StatisticsPanel statisticsPanel;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        setTitle("Gestion des Activités Pédagogiques");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        initializeComponents();
    }

    private void initializeComponents() {
        // Create menu bar
        createMenuBar();

        // Create main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));

        // Create tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Initialize panels
        activitiesPanel = new ActivitiesPanel();
        participationPanel = new ParticipationPanel();
        statisticsPanel = new StatisticsPanel();

        // Add panels to tabbed pane
        tabbedPane.addTab("📚 Activités", new ImageIcon(), activitiesPanel, "Gérer les activités pédagogiques");
        tabbedPane.addTab("👥 Participation", new ImageIcon(), participationPanel, "Suivre la participation des étudiants");
        tabbedPane.addTab("📊 Statistiques", new ImageIcon(), statisticsPanel, "Analyser les statistiques d'engagement");

        // Add header panel
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Add tabbed pane
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185)); // Professional blue
        header.setPreferredSize(new Dimension(0, 80));
        header.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("📚 Gestion des Activités Pédagogiques");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel subtitleLabel = new JLabel("Organisez, suivez et analysez vos activités académiques");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 220, 240));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        return header;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Edit Menu
        JMenu editMenu = new JMenu("Édition");
        JMenuItem preferencesItem = new JMenuItem("Préférences");
        editMenu.add(preferencesItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Gestion des Activités Pédagogiques v1.0\n\n"
                + "Un système complet pour organiser, suivre et analyser\n"
                + "les activités pédagogiques et la participation des étudiants.\n\n"
                + "© 2024 - Tous droits réservés",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
