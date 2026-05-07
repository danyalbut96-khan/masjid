package masjid.gui;

import masjid.manager.MasjidManager;
import masjid.model.ImamStaff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StaffPanel extends JPanel {

    private MasjidManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, addressField, salaryField, joinDateField, searchField;
    private JComboBox<String> roleCombo;

    private static final String[] COLUMNS = {"ID", "Name", "Phone", "Address", "Role", "Salary", "Join Date"};
    private static final String[] ROLES = {"Imam", "Muezzin", "Teacher", "Cleaner", "Admin", "Other"};

    public StaffPanel(MasjidManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(0, 12));
        setBackground(StyleUtil.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel card = StyleUtil.createCardPanel("Staff Information");
        card.setLayout(new BorderLayout(0, 12));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(StyleUtil.BG_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        nameField = StyleUtil.createTextField("Full Name");
        phoneField = StyleUtil.createTextField("Phone Number");
        addressField = StyleUtil.createTextField("Address");
        roleCombo = StyleUtil.createComboBox(ROLES);
        salaryField = StyleUtil.createTextField("Salary");
        joinDateField = StyleUtil.createTextField("YYYY-MM-DD");
        joinDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        g.gridx = 0; g.gridy = 0;
        fields.add(StyleUtil.createLabel("Name:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(nameField, g);
        g.gridx = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Phone:"), g);
        g.gridx = 3; g.weightx = 1.0;
        fields.add(phoneField, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Address:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(addressField, g);
        g.gridx = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Role:"), g);
        g.gridx = 3; g.weightx = 1.0;
        fields.add(roleCombo, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Salary:"), g);
        g.gridx = 1; g.weightx = 1.0;
        fields.add(salaryField, g);
        g.gridx = 2; g.weightx = 0;
        fields.add(StyleUtil.createLabel("Join Date:"), g);
        g.gridx = 3; g.weightx = 1.0;
        fields.add(joinDateField, g);

        card.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setBackground(StyleUtil.BG_CARD);

        JButton addBtn = StyleUtil.createButton("\u2795 Add", StyleUtil.PRIMARY);
        JButton updBtn = StyleUtil.createButton("\u270F Update", new Color(255, 143, 0));
        JButton delBtn = StyleUtil.createButton("\u2716 Delete", StyleUtil.DANGER);
        JButton clrBtn = StyleUtil.createButton("\u27F3 Clear", StyleUtil.TEXT_SECONDARY);

        addBtn.addActionListener(e -> addStaff());
        updBtn.addActionListener(e -> updateStaff());
        delBtn.addActionListener(e -> deleteStaff());
        clrBtn.addActionListener(e -> clearForm());

        btns.add(addBtn);
        btns.add(updBtn);
        btns.add(delBtn);
        btns.add(clrBtn);
        card.add(btns, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = StyleUtil.createCardPanel("Staff Records");
        card.setLayout(new BorderLayout(0, 8));

        searchField = StyleUtil.createTextField("Search staff...");
        searchField.setPreferredSize(new Dimension(250, 34));
        JButton sBtn = StyleUtil.createButton("\uD83D\uDD0D Search", StyleUtil.PRIMARY);
        JButton cBtn = StyleUtil.createButton("Show All", StyleUtil.TEXT_SECONDARY);
        sBtn.addActionListener(e -> searchStaff());
        cBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });
        searchField.addActionListener(e -> searchStaff());
        card.add(StyleUtil.createSearchPanel(searchField, sBtn, cBtn), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        StyleUtil.styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(StyleUtil.BORDER_COLOR));
        card.add(sp, BorderLayout.CENTER);

        JLabel hint = new JLabel("Click a row to edit");
        hint.setFont(StyleUtil.FONT_SMALL);
        hint.setForeground(StyleUtil.TEXT_SECONDARY);
        card.add(hint, BorderLayout.SOUTH);

        return card;
    }

    private void addStaff() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }
        ImamStaff s = new ImamStaff("", nameField.getText().trim(), phoneField.getText().trim(),
                addressField.getText().trim(), (String) roleCombo.getSelectedItem(),
                salaryField.getText().trim(), joinDateField.getText().trim());
        manager.addStaff(s);
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Staff member added!");
    }

    private void updateStaff() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a staff member to update."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        ImamStaff s = new ImamStaff(id, nameField.getText().trim(), phoneField.getText().trim(),
                addressField.getText().trim(), (String) roleCombo.getSelectedItem(),
                salaryField.getText().trim(), joinDateField.getText().trim());
        manager.updateStaff(id, s);
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Staff member updated!");
    }

    private void deleteStaff() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a staff member to delete."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this staff member?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            manager.deleteStaff(id);
            refreshTable();
            clearForm();
        }
    }

    private void searchStaff() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { refreshTable(); return; }
        populateTable(manager.searchStaff(kw));
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String) tableModel.getValueAt(row, 1));
        phoneField.setText((String) tableModel.getValueAt(row, 2));
        addressField.setText((String) tableModel.getValueAt(row, 3));
        roleCombo.setSelectedItem(tableModel.getValueAt(row, 4));
        salaryField.setText((String) tableModel.getValueAt(row, 5));
        joinDateField.setText((String) tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        roleCombo.setSelectedIndex(0);
        salaryField.setText("");
        joinDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        table.clearSelection();
    }

    public void refreshTable() {
        populateTable(manager.getAllStaff());
    }

    private void populateTable(ArrayList<ImamStaff> list) {
        tableModel.setRowCount(0);
        for (ImamStaff s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getPhone(),
                    s.getAddress(), s.getRole(), s.getSalary(), s.getJoinDate()});
        }
    }
}
