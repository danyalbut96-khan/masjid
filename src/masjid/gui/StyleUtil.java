package masjid.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Utility class providing consistent styling across all panels.
 * Defines the Masjid Management System design language.
 */
public class StyleUtil {

    public static final Color PRIMARY = new Color(0, 191, 165);        // Original Teal
    public static final Color PRIMARY_DARK = new Color(0, 150, 136);    // Original Dark Teal
    public static final Color PRIMARY_LIGHT = new Color(178, 223, 219); // Original Light Teal
    public static final Color ACCENT = new Color(255, 179, 0);         // Original Amber
    public static final Color BG_MAIN = Color.WHITE;                    // White Background
    public static final Color BG_CARD = Color.WHITE;                    // White Card Background
    public static final Color TEXT_PRIMARY = Color.BLACK;               // Black Font
    public static final Color TEXT_SECONDARY = new Color(55, 71, 79);  // Clean secondary gray
    public static final Color DANGER = new Color(211, 47, 47);
    public static final Color SUCCESS = new Color(56, 142, 60);
    public static final Color TABLE_HEADER_BG = new Color(0, 150, 136);
    public static final Color TABLE_STRIPE = new Color(245, 247, 250); // Soft clean stripe
    public static final Color BORDER_COLOR = new Color(207, 216, 220); // Clean soft light border

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT = new Font("Segoe UI", Font.BOLD, 32);

    /**
     * Create a styled button with the given text and color.
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));

        // Hover effect
        Color hoverColor = bgColor.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    /**
     * Create a styled text field.
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(15);
        field.setFont(FONT_BODY);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setToolTipText(placeholder);
        return field;
    }

    /**
     * Create a styled combo box.
     */
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(160, 34));
        return combo;
    }

    /**
     * Create a titled panel with consistent styling.
     */
    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        Border roundedBorder = new LineBorder(BORDER_COLOR, 1, true);
        Border marginBorder = BorderFactory.createEmptyBorder(16, 20, 16, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(roundedBorder, marginBorder));

        if (title != null && !title.isEmpty()) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                            new LineBorder(PRIMARY_LIGHT, 1, true),
                            " " + title + " ",
                            TitledBorder.LEFT, TitledBorder.TOP,
                            FONT_SUBTITLE, PRIMARY_DARK
                    ),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
        }
        return panel;
    }

    /**
     * Style a JTable with the application theme.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE_BODY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header styling
        table.getTableHeader().setFont(FONT_TABLE_HEADER);
        table.getTableHeader().setBackground(TABLE_HEADER_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setReorderingAllowed(false);

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : TABLE_STRIPE);
                    c.setForeground(TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    /**
     * Create the search panel with a search field and button.
     */
    public static JPanel createSearchPanel(JTextField searchField, JButton searchBtn, JButton clearBtn) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setBackground(BG_CARD);

        JLabel lbl = new JLabel("✦ Search:");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SECONDARY);

        panel.add(lbl);
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(clearBtn);

        return panel;
    }

    /**
     * Create a form label with consistent styling.
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
}
