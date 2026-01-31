package com.fst.gestionactivites.gui.pannels;

import com.fst.gestionactivites.model.Activity;
import com.fst.gestionactivites.repository.ActivityRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying statistics and reports
 */
public class StatisticsPanel extends JPanel {
    private final ActivityRepository activityRepository;
    private JPanel chartsPanel;
    private JTextArea summaryArea;

    public StatisticsPanel() {
        this.activityRepository = ActivityRepository.getInstance();
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Summary
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Résumé Général"));

        summaryArea = new JTextArea(8, 40);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        topPanel.add(summaryScroll, BorderLayout.CENTER);

        // Charts Panel
        chartsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartsPanel.setBorder(BorderFactory.createTitledBorder("Graphiques"));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> loadStatistics());
        buttonPanel.add(refreshButton);

        // Add components
        add(topPanel, BorderLayout.NORTH);
        add(chartsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStatistics() {
        try {
            // Load data from database
            List<Activity> activities = activityRepository.findAll();

            // Calculate statistics
            double overallParticipationRate = calculateOverallParticipationRate(activities);
            double overallAverageScore = calculateOverallAverageScore(activities);
            Map<String, Integer> activitiesByType = getActivitiesByTypeCount(activities);

            // Update summary
            StringBuilder summary = new StringBuilder();
            summary.append("=== STATISTIQUES GÉNÉRALES ===\n\n");
            summary.append(String.format("Nombre total d'activités: %d\n", activities.size()));
            summary.append(String.format("Taux de participation global: %.2f%%\n", overallParticipationRate));
            summary.append(String.format("Moyenne générale des notes: %.2f/100\n\n", overallAverageScore));

            summary.append("Répartition par statut:\n");
            for (Activity.ActivityStatus status : Activity.ActivityStatus.values()) {
                long count = activities.stream()
                        .filter(a -> a.getStatus() == status)
                        .count();
                summary.append(String.format("  - %s: %d\n", status.getLabel(), count));
            }

            summary.append("\nRépartition par type:\n");
            activitiesByType.forEach((type, count) ->
                    summary.append(String.format("  - %s: %d\n", type, count)));

            summaryArea.setText(summary.toString());

            // Update charts
            chartsPanel.removeAll();

            // Pie Chart - Activities by Type
            JFreeChart pieChart = createActivitiesByTypeChart(activitiesByType);
            ChartPanel pieChartPanel = new ChartPanel(pieChart);
            chartsPanel.add(pieChartPanel);

            // Bar Chart - Participation Rates
            JFreeChart barChart = createParticipationChart(activities);
            ChartPanel barChartPanel = new ChartPanel(barChart);
            chartsPanel.add(barChartPanel);

            chartsPanel.revalidate();
            chartsPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des statistiques: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateOverallParticipationRate(List<Activity> activities) {
        if (activities.isEmpty()) return 0.0;
        return activities.stream()
                .mapToDouble(Activity::getParticipationRate)
                .average()
                .orElse(0.0);
    }

    private double calculateOverallAverageScore(List<Activity> activities) {
        if (activities.isEmpty()) return 0.0;
        return activities.stream()
                .mapToDouble(Activity::getAverageScore)
                .average()
                .orElse(0.0);
    }

    private Map<String, Integer> getActivitiesByTypeCount(List<Activity> activities) {
        Map<String, Integer> counts = new HashMap<>();
        activities.forEach(a ->
                counts.merge(a.getType().getLabel(), 1, Integer::sum)
        );
        return counts;
    }

    private JFreeChart createActivitiesByTypeChart(Map<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);

        return ChartFactory.createPieChart(
                "Répartition des Activités par Type",
                dataset,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );
    }

    private JFreeChart createParticipationChart(List<Activity> activities) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Activity activity : activities) {
            if (activity.getTitle().length() > 20) {
                String shortTitle = activity.getTitle().substring(0, 17) + "...";
                dataset.addValue(activity.getParticipationRate(), "Taux (%)", shortTitle);
            } else {
                dataset.addValue(activity.getParticipationRate(), "Taux (%)", activity.getTitle());
            }
        }

        return ChartFactory.createBarChart(
                "Taux de Participation par Activité",
                "Activité",
                "Taux de Participation (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );
    }
}
