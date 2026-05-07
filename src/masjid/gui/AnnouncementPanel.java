package masjid.gui;

import masjid.manager.MasjidManager;
import masjid.model.Announcement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AnnouncementPanel extends JPanel {

    private MasjidManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField titleField, dateField, searchField;
    private JTextArea descArea;
    private JComboBox<String> categoryCombo;

    private static final String[] COLUMNS = {"ID", "Title", "Category", "Date", "Description"};
    private static final String[] CATEGORIES = {"Juma", "Event", "Ramadan", "General"};

    public AnnouncementPanel(MasjidManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(0, 12));
        setBackground(StyleUtil.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel card = StyleUtil.createCardPanel("New Announcement");
        card.setLayout(new BorderLayout(0, 12));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(StyleUtil.BG_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        titleField = StyleUtil.createTextField("Announcement Title");
        dateField = StyleUtil.createTextField("YYYY-MM-DD");
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        categoryCombo = StyleUtil.createComboBox(CATEGORIES);

        descArea = new JTextArea(3, 20);
        descArea.setFont(StyleUtil.FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        g.gridx = 0; g.gridy = 0;
        fields.add(StyleUtil.createLabel("Title:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(titleField, g);
        g.gridx = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Category:"), g);
        g.gridx = 3; g.weightx = 1.0;
        fields.add(categoryCombo, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Date:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(dateField, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Description:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1.0;
        fields.add(new JScrollPane(descArea), g);

        card.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(StyleUtil.BG_CARD);

        JButton addBtn = StyleUtil.createButton("\u2795 Add", StyleUtil.PRIMARY);
        JButton delBtn = StyleUtil.createButton("\u2716 Delete", StyleUtil.DANGER);
        JButton clrBtn = StyleUtil.createButton("\u27F3 Clear", StyleUtil.TEXT_SECONDARY);

        addBtn.addActionListener(e -> addAnnouncement());
        delBtn.addActionListener(e -> deleteAnnouncement());
        clrBtn.addActionListener(e -> clearForm());

        btns.add(addBtn);
        btns.add(delBtn);
        btns.add(clrBtn);
        card.add(btns, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = StyleUtil.createCardPanel("Announcements");
        card.setLayout(new BorderLayout(0, 8));

        searchField = StyleUtil.createTextField("Search announcements...");
        searchField.setPreferredSize(new Dimension(250, 34));
        JButton sBtn = StyleUtil.createButton("\uD83D\uDD0D Search", StyleUtil.PRIMARY);
        JButton cBtn = StyleUtil.createButton("Show All", StyleUtil.TEXT_SECONDARY);
        sBtn.addActionListener(e -> searchAnnouncements());
        cBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });
        searchField.addActionListener(e -> searchAnnouncements());
        card.add(StyleUtil.createSearchPanel(searchField, sBtn, cBtn), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        StyleUtil.styleTable(table);
        // Make description column wider
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR));
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private void addAnnouncement() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.");
            return;
        }
        Announcement a = new Announcement("", titleField.getText().trim(),
                descArea.getText().trim(), (String) categoryCombo.getSelectedItem(),
                dateField.getText().trim());
        manager.addAnnouncement(a);
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Announcement added!");
    }

    private void deleteAnnouncement() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an announcement to delete."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this announcement?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            manager.deleteAnnouncement(id);
            refreshTable();
        }
    }

    private void searchAnnouncements() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { refreshTable(); return; }
        populateTable(manager.searchAnnouncements(kw));
    }

    private void clearForm() {
        titleField.setText("");
        descArea.setText("");
        categoryCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    public void refreshTable() {
        populateTable(manager.getAllAnnouncements());
    }

    private void populateTable(ArrayList<Announcement> list) {
        tableModel.setRowCount(0);
        for (Announcement a : list) {
            tableModel.addRow(new Object[]{a.getId(), a.getTitle(), a.getCategory(),
                    a.getDate(), a.getDescription()});
        }
    }
}
