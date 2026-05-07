package masjid.gui;

import masjid.manager.MasjidManager;
import masjid.model.Donor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Donor Management Panel with full CRUD operations.
 * Add, View, Update, Search, Delete donors.
 */
public class DonorPanel extends JPanel {

    private MasjidManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, addressField, searchField;
    private JComboBox<String> typeCombo;

    private static final String[] COLUMN_NAMES = {"ID", "Name", "Phone", "Address", "Type"};
    private static final String[] DONOR_TYPES = {"Regular", "One-time", "Monthly"};

    public DonorPanel(MasjidManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(0, 12));
        setBackground(StyleUtil.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Form Panel
        add(createFormPanel(), BorderLayout.NORTH);

        // Table Panel
        add(createTablePanel(), BorderLayout.CENTER);

        // Load initial data
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel card = StyleUtil.createCardPanel("Donor Information");
        card.setLayout(new BorderLayout(0, 12));

        // Input fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(StyleUtil.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = StyleUtil.createTextField("Full Name");
        phoneField = StyleUtil.createTextField("Phone Number");
        addressField = StyleUtil.createTextField("Address");
        typeCombo = StyleUtil.createComboBox(DONOR_TYPES);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(StyleUtil.createLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(nameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        fieldsPanel.add(StyleUtil.createLabel("Phone:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        fieldsPanel.add(phoneField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        fieldsPanel.add(StyleUtil.createLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        fieldsPanel.add(addressField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        fieldsPanel.add(StyleUtil.createLabel("Type:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        fieldsPanel.add(typeCombo, gbc);

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(StyleUtil.BG_CARD);

        JButton addBtn = StyleUtil.createButton("\u2795  Add", StyleUtil.PRIMARY);
        JButton updateBtn = StyleUtil.createButton("\u270F  Update", new Color(255, 143, 0));
        JButton deleteBtn = StyleUtil.createButton("\u2716  Delete", StyleUtil.DANGER);
        JButton clearBtn = StyleUtil.createButton("\u27F3  Clear", StyleUtil.TEXT_SECONDARY);

        addBtn.addActionListener(e -> addDonor());
        updateBtn.addActionListener(e -> updateDonor());
        deleteBtn.addActionListener(e -> deleteDonor());
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = StyleUtil.createCardPanel("Donor Records");
        card.setLayout(new BorderLayout(0, 8));

        // Search bar
        searchField = StyleUtil.createTextField("Search donors...");
        searchField.setPreferredSize(new Dimension(250, 34));
        JButton searchBtn = StyleUtil.createButton("\uD83D\uDD0D Search", StyleUtil.PRIMARY);
        JButton clearSearchBtn = StyleUtil.createButton("Show All", StyleUtil.TEXT_SECONDARY);

        searchBtn.addActionListener(e -> searchDonors());
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        // Press Enter to search
        searchField.addActionListener(e -> searchDonors());

        JPanel searchPanel = StyleUtil.createSearchPanel(searchField, searchBtn, clearSearchBtn);
        card.add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        StyleUtil.styleTable(table);

        // Click row to populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR));
        card.add(scrollPane, BorderLayout.CENTER);

        // Record count
        JLabel countLabel = new JLabel("Click a row to edit");
        countLabel.setFont(StyleUtil.FONT_SMALL);
        countLabel.setForeground(StyleUtil.TEXT_SECONDARY);
        card.add(countLabel, BorderLayout.SOUTH);

        return card;
    }

    private void addDonor() {
        if (!validateForm()) return;

        Donor donor = new Donor("", nameField.getText().trim(), phoneField.getText().trim(),
                addressField.getText().trim(), (String) typeCombo.getSelectedItem());
        manager.add(donor);
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Donor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateDonor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a donor to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Donor updated = new Donor(id, nameField.getText().trim(), phoneField.getText().trim(),
                addressField.getText().trim(), (String) typeCombo.getSelectedItem());
        manager.update(id, updated);
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Donor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteDonor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a donor to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete donor: " + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            manager.delete(id);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Donor deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchDonors() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }
        ArrayList<Donor> results = manager.searchByKeyword(keyword);
        populateTable(results);
    }

    private void populateFormFromSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
        phoneField.setText((String) tableModel.getValueAt(selectedRow, 2));
        addressField.setText((String) tableModel.getValueAt(selectedRow, 3));
        typeCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }
        return true;
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        typeCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    public void refreshTable() {
        populateTable(manager.getAll());
    }

    private void populateTable(ArrayList<Donor> donors) {
        tableModel.setRowCount(0);
        for (Donor d : donors) {
            tableModel.addRow(new Object[]{
                    d.getId(), d.getName(), d.getPhone(), d.getAddress(), d.getDonorType()
            });
        }
    }
}
