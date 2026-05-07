package masjid.gui;

import masjid.manager.MasjidManager;
import masjid.model.DonationRecord;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Dashboard panel showing overview statistics, welcome message,
 * interactive congregation prayer timings, and a donation collection calendar.
 */
public class DashboardPanel extends JPanel {

    private MasjidManager manager;
    private JLabel donorCountLabel;
    private JLabel donationCountLabel;
    private JLabel donationTotalLabel;
    private JLabel announcementCountLabel;
    private JLabel staffCountLabel;

    // Namaz Timings Fields
    private java.util.HashMap<String, JTextField> prayerFields = new java.util.HashMap<>();

    // Calendar Fields
    private JPanel calendarGrid;
    private JLabel monthYearLabel;
    private JLabel calCollectionTotalLabel;
    private JTextArea calDonationDetailsArea;
    private int calYear = java.time.LocalDate.now().getYear();
    private int calMonth = java.time.LocalDate.now().getMonthValue();
    private int calSelectedDay = java.time.LocalDate.now().getDayOfMonth();

    public DashboardPanel(MasjidManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_MAIN);

        // Create main scroll pane so it is fully responsive even on smaller monitors
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(StyleUtil.BG_MAIN);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(headerPanel);
        mainContent.add(Box.createVerticalStrut(16));

        // Stats Cards
        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(statsPanel);
        mainContent.add(Box.createVerticalStrut(16));

        // Bottom Split Panel (Prayer Timings & Collections Calendar)
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBackground(StyleUtil.BG_MAIN);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottomPanel.add(createNamazPanel());
        bottomPanel.add(createCalendarPanel());

        mainContent.add(bottomPanel);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(StyleUtil.BG_MAIN);

        add(scrollPane, BorderLayout.CENTER);

        // Initial refresh of stats & calendar
        refreshStats();
        renderCalendar();
        updateDetailsForSelectedDate();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtil.PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel titleLabel = new JLabel("✦ Masjid Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Central dashboard for managing donors, daily donations, congregation prayer times, announcements, and staff");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(178, 223, 219));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(StyleUtil.BG_MAIN);

        JLabel sectionTitle = new JLabel("  System Overview");
        sectionTitle.setFont(StyleUtil.FONT_SUBTITLE);
        sectionTitle.setForeground(StyleUtil.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        outerPanel.add(sectionTitle, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 14, 0));
        cardsPanel.setBackground(StyleUtil.BG_MAIN);

        donorCountLabel = new JLabel("0", SwingConstants.CENTER);
        donationCountLabel = new JLabel("0", SwingConstants.CENTER);
        donationTotalLabel = new JLabel("$0.00", SwingConstants.CENTER);
        announcementCountLabel = new JLabel("0", SwingConstants.CENTER);
        staffCountLabel = new JLabel("0", SwingConstants.CENTER);

        cardsPanel.add(createStatCard("✦ Donors", donorCountLabel, StyleUtil.PRIMARY));
        cardsPanel.add(createStatCard("✦ Donations", donationCountLabel, new Color(56, 142, 60)));
        cardsPanel.add(createStatCard("✦ Total Amount", donationTotalLabel, new Color(255, 143, 0)));
        cardsPanel.add(createStatCard("✦ Announcements", announcementCountLabel, new Color(123, 31, 162)));
        cardsPanel.add(createStatCard("✦ Staff", staffCountLabel, new Color(21, 101, 192)));

        outerPanel.add(cardsPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionsPanel.setBackground(StyleUtil.BG_MAIN);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JButton refreshBtn = StyleUtil.createButton("↻  Refresh Stats", StyleUtil.PRIMARY);
        refreshBtn.addActionListener(e -> {
            refreshStats();
            renderCalendar();
            updateDetailsForSelectedDate();
        });

        actionsPanel.add(refreshBtn);

        JLabel lastUpdated = new JLabel("   Click Refresh to update system statistics and calendar markers");
        lastUpdated.setFont(StyleUtil.FONT_SMALL);
        lastUpdated.setForeground(StyleUtil.TEXT_SECONDARY);
        actionsPanel.add(lastUpdated);

        outerPanel.add(actionsPanel, BorderLayout.SOUTH);

        return outerPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        Border roundedBorder = new LineBorder(StyleUtil.BORDER_COLOR, 1, true);
        Border marginBorder = BorderFactory.createEmptyBorder(16, 16, 16, 16);
        card.setBorder(BorderFactory.createCompoundBorder(roundedBorder, marginBorder));

        // Color accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        accentBar.setPreferredSize(new Dimension(0, 4));
        accentBar.setBackground(accentColor);
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accentBar);
        card.add(Box.createVerticalStrut(10));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(StyleUtil.FONT_SMALL);
        titleLbl.setForeground(StyleUtil.TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));

        valueLabel.setFont(StyleUtil.FONT_STAT);
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLabel);

        return card;
    }

    private JPanel createNamazPanel() {
        JPanel card = StyleUtil.createCardPanel("Congregation Prayer Timings");
        card.setLayout(new BorderLayout(0, 12));

        JPanel grid = new JPanel(new GridLayout(6, 2, 16, 10));
        grid.setBackground(StyleUtil.BG_CARD);

        java.util.LinkedHashMap<String, String> timings = manager.getNamazTimings();
        for (String prayer : timings.keySet()) {
            JLabel label = StyleUtil.createLabel("  " + prayer);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(StyleUtil.PRIMARY_DARK);

            JTextField field = StyleUtil.createTextField("");
            field.setText(timings.get(prayer));
            field.setHorizontalAlignment(JTextField.CENTER);
            prayerFields.put(prayer, field);

            grid.add(label);
            grid.add(field);
        }
        card.add(grid, BorderLayout.CENTER);

        JButton saveBtn = StyleUtil.createButton("Save Timings", StyleUtil.PRIMARY);
        saveBtn.addActionListener(e -> {
            for (String prayer : prayerFields.keySet()) {
                String newTime = prayerFields.get(prayer).getText().trim();
                if (!newTime.isEmpty()) {
                    manager.updateNamazTiming(prayer, newTime);
                }
            }
            JOptionPane.showMessageDialog(this, "Prayer timings updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(StyleUtil.BG_CARD);
        btnPanel.add(saveBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCalendarPanel() {
        JPanel card = StyleUtil.createCardPanel("Donation Collections Calendar");
        card.setLayout(new BorderLayout(0, 10));

        // Header
        JPanel calHeader = new JPanel(new BorderLayout());
        calHeader.setBackground(StyleUtil.BG_CARD);

        JButton prevBtn = StyleUtil.createButton("◀", StyleUtil.PRIMARY);
        prevBtn.setPreferredSize(new Dimension(50, 30));
        JButton nextBtn = StyleUtil.createButton("▶", StyleUtil.PRIMARY);
        nextBtn.setPreferredSize(new Dimension(50, 30));

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(StyleUtil.FONT_SUBTITLE);
        monthYearLabel.setForeground(StyleUtil.TEXT_PRIMARY);

        calHeader.add(prevBtn, BorderLayout.WEST);
        calHeader.add(monthYearLabel, BorderLayout.CENTER);
        calHeader.add(nextBtn, BorderLayout.EAST);

        card.add(calHeader, BorderLayout.NORTH);

        // Grid Panel (Days of Week + Days of Month)
        JPanel gridContainer = new JPanel(new BorderLayout(0, 4));
        gridContainer.setBackground(StyleUtil.BG_CARD);

        JPanel dayNamesPanel = new JPanel(new GridLayout(1, 7));
        dayNamesPanel.setBackground(StyleUtil.BG_CARD);
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String d : daysOfWeek) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(StyleUtil.ACCENT);
            dayNamesPanel.add(l);
        }
        gridContainer.add(dayNamesPanel, BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        calendarGrid.setBackground(StyleUtil.BG_CARD);
        gridContainer.add(calendarGrid, BorderLayout.CENTER);

        card.add(gridContainer, BorderLayout.CENTER);

        // Selected Day Details Panel
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 4));
        detailsPanel.setBackground(StyleUtil.BG_MAIN);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel detailsTitle = new JLabel("Collections for Selected Day:");
        detailsTitle.setFont(StyleUtil.FONT_SMALL);
        detailsTitle.setForeground(StyleUtil.TEXT_SECONDARY);

        calCollectionTotalLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        calCollectionTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        calCollectionTotalLabel.setForeground(StyleUtil.SUCCESS);

        JPanel detailsHeader = new JPanel(new BorderLayout());
        detailsHeader.setBackground(StyleUtil.BG_MAIN);
        detailsHeader.add(detailsTitle, BorderLayout.WEST);
        detailsHeader.add(calCollectionTotalLabel, BorderLayout.EAST);

        calDonationDetailsArea = new JTextArea(3, 20);
        calDonationDetailsArea.setFont(StyleUtil.FONT_SMALL);
        calDonationDetailsArea.setEditable(false);
        calDonationDetailsArea.setBackground(StyleUtil.BG_MAIN);
        calDonationDetailsArea.setLineWrap(true);
        calDonationDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(calDonationDetailsArea);
        detailsScroll.setBorder(null);

        detailsPanel.add(detailsHeader, BorderLayout.NORTH);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);

        card.add(detailsPanel, BorderLayout.SOUTH);

        // Wire Up Nav Buttons
        prevBtn.addActionListener(e -> {
            if (calMonth == 1) {
                calMonth = 12;
                calYear--;
            } else {
                calMonth--;
            }
            calSelectedDay = 1;
            renderCalendar();
            updateDetailsForSelectedDate();
        });

        nextBtn.addActionListener(e -> {
            if (calMonth == 12) {
                calMonth = 1;
                calYear++;
            } else {
                calMonth++;
            }
            calSelectedDay = 1;
            renderCalendar();
            updateDetailsForSelectedDate();
        });

        return card;
    }

    private void renderCalendar() {
        calendarGrid.removeAll();

        // Update month year label
        java.time.Month monthEnum = java.time.Month.of(calMonth);
        String monthName = monthEnum.name().substring(0, 1) + monthEnum.name().substring(1).toLowerCase();
        monthYearLabel.setText(monthName + " " + calYear);

        java.time.YearMonth yearMonth = java.time.YearMonth.of(calYear, calMonth);
        int daysInMonth = yearMonth.lengthOfMonth();
        java.time.LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekVal = firstOfMonth.getDayOfWeek().getValue(); // 1 = Mon, 7 = Sun
        int startOffset = dayOfWeekVal == 7 ? 0 : dayOfWeekVal;

        // Fill empty buttons before start of month
        for (int i = 0; i < startOffset; i++) {
            JLabel emptyLabel = new JLabel("");
            calendarGrid.add(emptyLabel);
        }

        // Fill days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            String dateStr = String.format("%04d-%02d-%02d", calYear, calMonth, currentDay);

            // Calculate total collections for this date
            double tempDayTotal = 0.0;
            ArrayList<DonationRecord> recs = new ArrayList<>();
            for (DonationRecord r : manager.getAllDonations()) {
                if (r.getDate().equals(dateStr)) {
                    tempDayTotal += r.getAmount();
                    recs.add(r);
                }
            }
            final double dayTotal = tempDayTotal;

            JButton dayBtn = new JButton(String.valueOf(day)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            dayBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dayBtn.setFocusPainted(false);
            dayBtn.setBorderPainted(false);
            dayBtn.setContentAreaFilled(false);
            dayBtn.setOpaque(false);
            dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (dayTotal > 0) {
                dayBtn.setBackground(new Color(27, 94, 32)); // Darker success green for dark mode
                dayBtn.setForeground(Color.WHITE);
                dayBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                dayBtn.setToolTipText("Collections: $" + String.format("%.2f", dayTotal));
            } else {
                dayBtn.setBackground(StyleUtil.BG_CARD);
                dayBtn.setForeground(StyleUtil.TEXT_PRIMARY);
                dayBtn.setBorder(BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR, 1));
            }

            // If selected
            if (day == calSelectedDay) {
                dayBtn.setBackground(StyleUtil.PRIMARY);
                dayBtn.setForeground(Color.WHITE);
            }

            dayBtn.addActionListener(e -> {
                calSelectedDay = currentDay;
                renderCalendar();
                showDayDetails(dateStr, dayTotal, recs);
            });

            calendarGrid.add(dayBtn);
        }

        // Fill remaining spaces
        int totalSlots = startOffset + daysInMonth;
        int remaining = 42 - totalSlots;
        for (int i = 0; i < remaining; i++) {
            JLabel emptyLabel = new JLabel("");
            calendarGrid.add(emptyLabel);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void showDayDetails(String dateStr, double dayTotal, ArrayList<DonationRecord> recs) {
        calCollectionTotalLabel.setText("$" + String.format("%.2f", dayTotal));
        if (recs.isEmpty()) {
            calDonationDetailsArea.setText("Date: " + dateStr + "\nNo donations recorded for this day.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Date: ").append(dateStr).append("\n");
            sb.append("Donation Records:\n");
            for (DonationRecord r : recs) {
                sb.append("  • ").append(r.getDonorName())
                  .append(" (").append(r.getPurpose()).append("): $")
                  .append(String.format("%.2f", r.getAmount())).append("\n");
            }
            calDonationDetailsArea.setText(sb.toString());
        }
    }

    private void updateDetailsForSelectedDate() {
        String dateStr = String.format("%04d-%02d-%02d", calYear, calMonth, calSelectedDay);
        double dayTotal = 0.0;
        ArrayList<DonationRecord> recs = new ArrayList<>();
        for (DonationRecord r : manager.getAllDonations()) {
            if (r.getDate().equals(dateStr)) {
                dayTotal += r.getAmount();
                recs.add(r);
            }
        }
        showDayDetails(dateStr, dayTotal, recs);
    }

    /**
     * Refresh dashboard statistics.
     */
    public void refreshStats() {
        donorCountLabel.setText(String.valueOf(manager.getTotalDonorCount()));
        donationCountLabel.setText(String.valueOf(manager.getTotalDonationCount()));
        donationTotalLabel.setText("$" + String.format("%.2f", manager.getTotalDonations()));
        announcementCountLabel.setText(String.valueOf(manager.getTotalAnnouncementCount()));
        staffCountLabel.setText(String.valueOf(manager.getTotalStaffCount()));
    }
}
