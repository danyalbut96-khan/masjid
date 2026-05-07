package masjid.gui;

import masjid.manager.MasjidManager;
import masjid.model.DonationRecord;
import masjid.model.Donor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DonationPanel extends JPanel {

    private MasjidManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> donorCombo, purposeCombo;
    private JTextField amountField, dateField, searchField;
    private JLabel totalLabel;

    private static final String[] COLUMNS = {"ID", "Donor ID", "Donor Name", "Amount", "Date", "Purpose"};
    private static final String[] PURPOSES = {"Zakat", "Sadaqah", "Building Fund", "Ramadan", "General"};

    public DonationPanel(MasjidManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(0, 12));
        setBackground(StyleUtil.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel card = StyleUtil.createCardPanel("Record Donation");
        card.setLayout(new BorderLayout(0, 12));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(StyleUtil.BG_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        donorCombo = new JComboBox<>();
        donorCombo.setFont(StyleUtil.FONT_BODY);
        donorCombo.setPreferredSize(new Dimension(200, 34));
        refreshDonorCombo();

        amountField = StyleUtil.createTextField("Amount");
        dateField = StyleUtil.createTextField("YYYY-MM-DD");
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        purposeCombo = StyleUtil.createComboBox(PURPOSES);

        g.gridx = 0; g.gridy = 0;
        fields.add(StyleUtil.createLabel("Donor:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(donorCombo, g);
        g.gridx = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Amount ($):"), g);
        g.gridx = 3; g.weightx = 1.0;
        fields.add(amountField, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Date:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(dateField, g);
        g.gridx = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Purpose:"), g);
        g.gridx = 3; g.weightx = 1.0;
        fields.add(purposeCombo, g);

        card.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(StyleUtil.BG_CARD);

        JButton addBtn = StyleUtil.createButton("\u2795 Record", StyleUtil.PRIMARY);
        JButton delBtn = StyleUtil.createButton("\u2716 Delete", StyleUtil.DANGER);
        JButton refBtn = StyleUtil.createButton("\u27F3 Refresh", StyleUtil.TEXT_SECONDARY);

        addBtn.addActionListener(e -> addDonation());
        delBtn.addActionListener(e -> deleteDonation());
        refBtn.addActionListener(e -> refreshDonorCombo());

        btns.add(addBtn);
        btns.add(delBtn);
        btns.add(refBtn);
        card.add(btns, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = StyleUtil.createCardPanel("Donation History");
        card.setLayout(new BorderLayout(0, 8));

        searchField = StyleUtil.createTextField("Search donations...");
        searchField.setPreferredSize(new Dimension(250, 34));
        JButton sBtn = StyleUtil.createButton("\uD83D\uDD0D Search", StyleUtil.PRIMARY);
        JButton cBtn = StyleUtil.createButton("Show All", StyleUtil.TEXT_SECONDARY);
        sBtn.addActionListener(e -> searchDonations());
        cBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });
        searchField.addActionListener(e -> searchDonations());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(StyleUtil.BG_CARD);
        top.add(StyleUtil.createSearchPanel(searchField, sBtn, cBtn), BorderLayout.WEST);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(StyleUtil.SUCCESS);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        top.add(totalLabel, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        StyleUtil.styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR));
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private void addDonation() {
        if (donorCombo.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a donor.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive amount.");
            return;
        }
        String sel = (String) donorCombo.getSelectedItem();
        String dId = sel.split(" - ")[0].trim();
        String dName = sel.contains(" - ") ? sel.split(" - ", 2)[1].trim() : "";

        manager.addDonation(new DonationRecord("", dId, dName, amount,
                dateField.getText().trim(), (String) purposeCombo.getSelectedItem()));
        refreshTable();
        amountField.setText("");
        JOptionPane.showMessageDialog(this, "Donation recorded!");
    }

    private void deleteDonation() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a donation to delete."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this donation?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            manager.deleteDonation(id);
            refreshTable();
        }
    }

    private void searchDonations() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { refreshTable(); return; }
        populateTable(manager.searchDonations(kw));
    }

    public void refreshDonorCombo() {
        donorCombo.removeAllItems();
        for (Donor d : manager.getAll()) {
            donorCombo.addItem(d.getId() + " - " + d.getName());
        }
    }

    public void refreshTable() {
        populateTable(manager.getAllDonations());
        totalLabel.setText("Total: $" + String.format("%.2f", manager.getTotalDonations()));
    }

    private void populateTable(ArrayList<DonationRecord> records) {
        tableModel.setRowCount(0);
        for (DonationRecord r : records) {
            tableModel.addRow(new Object[]{r.getId(), r.getDonorId(), r.getDonorName(),
                    String.format("$%.2f", r.getAmount()), r.getDate(), r.getPurpose()});
        }
    }
}
