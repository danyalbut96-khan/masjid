package masjid;

import masjid.gui.*;
import masjid.manager.MasjidManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Masjid Management System - Main Application Entry Point.
 * 
 * OOP Concepts Used:
 * - Inheritance:       Donor, ImamStaff extend Person
 * - Encapsulation:     Private fields with getters/setters in all model classes
 * - Abstraction:       Person is abstract with abstract getRole() method
 * - Polymorphism:      getRole() and toString() overridden in subclasses
 * - Interface:         Manageable<T> with add, update, delete, search methods
 * - ArrayList:         Used for all data management collections
 * - File Handling:     CSV-based persistence with BufferedReader/Writer
 *
 * @author Masjid Management System
 * @version 1.0
 */
public class MasjidApp extends JFrame {

    private MasjidManager manager;
    private DashboardPanel dashboardPanel;
    private DonorPanel donorPanel;
    private DonationPanel donationPanel;
    private AnnouncementPanel announcementPanel;
    private StaffPanel staffPanel;

    public MasjidApp() {
        manager = new MasjidManager();
        initUI();
    }

    private void initUI() {
        setTitle("\u2726 Masjid Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Set application icon color scheme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Custom UI defaults
        UIManager.put("TabbedPane.selected", StyleUtil.PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", StyleUtil.BG_MAIN);
        UIManager.put("OptionPane.messageFont", StyleUtil.FONT_BODY);
        UIManager.put("OptionPane.buttonFont", StyleUtil.FONT_BUTTON);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(StyleUtil.BG_MAIN);
        tabbedPane.setForeground(StyleUtil.TEXT_PRIMARY);

        // Set tab insets for better spacing
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 20, 10, 20));

        // Create panels
        dashboardPanel = new DashboardPanel(manager);
        donorPanel = new DonorPanel(manager);
        donationPanel = new DonationPanel(manager);
        announcementPanel = new AnnouncementPanel(manager);
        staffPanel = new StaffPanel(manager);

        // Add tabs with icons (using Unicode)
        tabbedPane.addTab("✦ Dashboard", dashboardPanel);
        tabbedPane.addTab("✦ Donors", donorPanel);
        tabbedPane.addTab("✦ Donations", donationPanel);
        tabbedPane.addTab("✦ Announcements", announcementPanel);
        tabbedPane.addTab("✦ Staff", staffPanel);

        // Refresh data when switching tabs
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: dashboardPanel.refreshStats(); break;
                case 1: donorPanel.refreshTable(); break;
                case 2:
                    donationPanel.refreshDonorCombo();
                    donationPanel.refreshTable();
                    break;
                case 3: announcementPanel.refreshTable(); break;
                case 4: staffPanel.refreshTable(); break;
            }
        });

        // Create global main container with tabbedPane and footer
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(StyleUtil.BG_MAIN);
        mainContainer.add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        footerPanel.setBackground(StyleUtil.BG_MAIN);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel createdByLabel = new JLabel("Created with ");
        createdByLabel.setFont(StyleUtil.FONT_SMALL);
        createdByLabel.setForeground(StyleUtil.TEXT_SECONDARY);

        JLabel heartLabel = new JLabel("❤️");
        heartLabel.setForeground(Color.RED);
        heartLabel.setFont(StyleUtil.FONT_SMALL);

        JLabel byLabel = new JLabel(" by ");
        byLabel.setFont(StyleUtil.FONT_SMALL);
        byLabel.setForeground(StyleUtil.TEXT_SECONDARY);

        JLabel linkLabel = new JLabel("CloudExify");
        linkLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        linkLabel.setForeground(StyleUtil.PRIMARY);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setToolTipText("Visit https://cloudexify.site");
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI("https://cloudexify.site"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MasjidApp.this, "Could not open link: https://cloudexify.site", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                linkLabel.setForeground(StyleUtil.PRIMARY_DARK);
                linkLabel.setText("<html><u>CloudExify</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                linkLabel.setForeground(StyleUtil.PRIMARY);
                linkLabel.setText("CloudExify");
            }
        });

        footerPanel.add(createdByLabel);
        footerPanel.add(heartLabel);
        footerPanel.add(byLabel);
        footerPanel.add(linkLabel);

        mainContainer.add(footerPanel, BorderLayout.SOUTH);
        setContentPane(mainContainer);

        // Save data on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        MasjidApp.this,
                        "Save all data and exit?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.saveAllData();
                    System.exit(0);
                } else if (confirm == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
                // CANCEL: do nothing, stay in app
            }
        });

        // Initial refresh
        dashboardPanel.refreshStats();
    }

    /**
     * Main method - Application entry point.
     */
    public static void main(String[] args) {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MasjidApp app = new MasjidApp();
            app.setVisible(true);
        });
    }
}
