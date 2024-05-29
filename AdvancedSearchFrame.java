import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class AdvancedSearchFrame extends JFrame {
    private JTextField suspectNameField, suspectGenderField, victimNameField, victimGenderField, witnessNameField, witnessGenderField, statusField;
    private JTextField caseTypeField, priorityLevelField, officerField, dateField;
    private JButton searchButton;
    private JTable resultsTable;
    private JScrollPane scrollPane;
    private JTextField caseIdField;

    public AdvancedSearchFrame() {
        setTitle("Advanced Search");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Suspect Name:"), gbc);
        gbc.gridx = 1;
        suspectNameField = new JTextField(15);
        panel.add(suspectNameField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Suspect Gender:"), gbc);
        gbc.gridx = 3;
        suspectGenderField = new JTextField(15);
        panel.add(suspectGenderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Victim Name:"), gbc);
        gbc.gridx = 1;
        victimNameField = new JTextField(15);
        panel.add(victimNameField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Victim Gender:"), gbc);
        gbc.gridx = 3;
        victimGenderField = new JTextField(15);
        panel.add(victimGenderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Witness Name:"), gbc);
        gbc.gridx = 1;
        witnessNameField = new JTextField(15);
        panel.add(witnessNameField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Witness Gender:"), gbc);
        gbc.gridx = 3;
        witnessGenderField = new JTextField(15);
        panel.add(witnessGenderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Case Type:"), gbc);
        gbc.gridx = 1;
        caseTypeField = new JTextField(15);
        panel.add(caseTypeField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Priority Level:"), gbc);
        gbc.gridx = 3;
        priorityLevelField = new JTextField(15);
        panel.add(priorityLevelField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Officer:"), gbc);
        gbc.gridx = 1;
        officerField = new JTextField(15);
        panel.add(officerField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        dateField = new JTextField(15);
        panel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusField = new JTextField(15);
        panel.add(statusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Case ID:"), gbc);
        gbc.gridx = 1;
        caseIdField = new JTextField(15);
        panel.add(caseIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        panel.add(searchButton, gbc);

        add(panel, BorderLayout.NORTH);

        resultsTable = new JTable();
        resultsTable.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
        scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch() {
        String suspectName = suspectNameField.getText();
        String suspectGender = suspectGenderField.getText();
        String victimName = victimNameField.getText();
        String victimGender = victimGenderField.getText();
        String witnessName = witnessNameField.getText();
        String witnessGender = witnessGenderField.getText();
        String caseType = caseTypeField.getText();
        String priorityLevel = priorityLevelField.getText();
        String officer = officerField.getText();
        String date = dateField.getText();
        String status = statusField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT cases.*, " +
                           "GROUP_CONCAT(DISTINCT CONCAT(victims.Name, ' (', victims.Gender, ')') SEPARATOR '; ') AS Victims, " +
                           "GROUP_CONCAT(DISTINCT CONCAT(suspects.Name, ' (', suspects.Gender, ')') SEPARATOR '; ') AS Suspects, " +
                           "GROUP_CONCAT(DISTINCT CONCAT(witnesses.Name, ' (', witnesses.Gender, ')') SEPARATOR '; ') AS Witnesses " +
                           "FROM cases " +
                           "LEFT JOIN victims ON cases.Case_ID = victims.Case_ID " +
                           "LEFT JOIN suspects ON cases.Case_ID = suspects.Case_ID " +
                           "LEFT JOIN witnesses ON cases.Case_ID = witnesses.Case_ID " +
                           "WHERE 1=1";

            if (!caseIdField.getText().isEmpty()) {
                query += " AND cases.Case_ID = ?";
            }
            if (!suspectName.isEmpty()) {
                query += " AND EXISTS (SELECT 1 FROM suspects WHERE cases.Case_ID = suspects.Case_ID AND suspects.Name = ?)";
            }
            if (!suspectGender.isEmpty()) {
                query += " AND EXISTS (SELECT 1 FROM suspects WHERE cases.Case_ID = suspects.Case_ID AND suspects.Gender = ?)";
            }
            if (!victimName.isEmpty()) {
                query += " AND EXISTS (SELECT 1 FROM victims WHERE cases.Case_ID = victims.Case_ID AND victims.Name = ?)";
            }
            if (!victimGender.isEmpty()) {
                query += " AND EXISTS (SELECT 1 FROM victims WHERE cases.Case_ID = victims.Case_ID AND victims.Gender = ?)";
            }
            if (!witnessName.isEmpty()) {
                query += " AND EXISTS (SELECT 1 FROM witnesses WHERE cases.Case_ID = witnesses.Case_ID AND witnesses.Name = ?)";
            }
            if (!witnessGender.isEmpty()) {
                query += " AND EXISTS (SELECT 1 FROM witnesses WHERE cases.Case_ID = witnesses.Case_ID AND witnesses.Gender = ?)";
            }
            if (!caseType.isEmpty()) {
                query += " AND cases.Case_Type = ?";
            }
            if (!priorityLevel.isEmpty()) {
                query += " AND cases.Priority_Level = ?";
            }
            if (!officer.isEmpty()) {
                query += " AND cases.Officer_ID = ?";
            }
            if (!date.isEmpty()) {
                query += " AND cases.Filing_Date = ?";
            }
            if (!status.isEmpty()) {
                query += " AND cases.Status = ?";
            }

            query += " GROUP BY cases.Case_ID";

            PreparedStatement stmt = conn.prepareStatement(query);

            int paramIndex = 1;

            if (!caseIdField.getText().isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(caseIdField.getText()));
            }
            if (!suspectName.isEmpty()) {
                stmt.setString(paramIndex++, suspectName);
            }
            if (!suspectGender.isEmpty()) {
                stmt.setString(paramIndex++, suspectGender);
            }
            if (!victimName.isEmpty()) {
                stmt.setString(paramIndex++, victimName);
            }
            if (!victimGender.isEmpty()) {
                stmt.setString(paramIndex++, victimGender);
            }
            if (!witnessName.isEmpty()) {
                stmt.setString(paramIndex++, witnessName);
            }
            if (!witnessGender.isEmpty()) {
                stmt.setString(paramIndex++, witnessGender);
            }
            if (!caseType.isEmpty()) {
                stmt.setString(paramIndex++, caseType);
            }
            if (!priorityLevel.isEmpty()) {
                stmt.setString(paramIndex++, priorityLevel);
            }
            if (!officer.isEmpty()) {
                stmt.setString(paramIndex++, officer);
            }
            if (!date.isEmpty()) {
                stmt.setString(paramIndex++, date);
            }
            if (!status.isEmpty()) {
                stmt.setString(paramIndex++, status);
            }

            ResultSet rs = stmt.executeQuery();
            resultsTable.setModel(new ResultSetTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdvancedSearchFrame frame = new AdvancedSearchFrame();
            frame.setVisible(true);
        });
    }
}

class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {
    public MultiLineTableCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setText(value != null ? value.toString() : "");
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        if (table.getRowHeight(row) != getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height);
        }
        return this;
    }
}
