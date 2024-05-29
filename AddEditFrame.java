import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AddEditFrame extends JFrame {
    private JTextField caseIdField, caseTitleField, caseTypeField, priorityLevelField, firNoField, statusField, officerField, judgeField, dateField, outcomeField, caseDetailsField;
    private JTable victimTable, suspectTable, witnessTable;
    private DefaultTableModel victimModel, suspectModel, witnessModel;
    private JButton saveButton;
    private boolean isEditMode;
    private int caseId;

    public AddEditFrame(boolean isEditMode, int caseId) {
        this.isEditMode = isEditMode;
        this.caseId = caseId;

        setTitle(isEditMode ? "Edit Case" : "Add Case");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();

        if (isEditMode) {
            loadCaseDetails(caseId);
            caseIdField.setEditable(false);
        } else {
            caseIdField.setEditable(true);
        }
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(12, 2));

        panel.add(new JLabel("Case ID:"));
        caseIdField = new JTextField();
        panel.add(caseIdField);

        panel.add(new JLabel("Case Title:"));
        caseTitleField = new JTextField();
        panel.add(caseTitleField);

        panel.add(new JLabel("Case Type:"));
        caseTypeField = new JTextField();
        panel.add(caseTypeField);

        panel.add(new JLabel("Priority Level:"));
        priorityLevelField = new JTextField();
        panel.add(priorityLevelField);

        panel.add(new JLabel("FIR NO:"));
        firNoField = new JTextField();
        panel.add(firNoField);

        panel.add(new JLabel("Status:"));
        statusField = new JTextField();
        panel.add(statusField);

        panel.add(new JLabel("Officer:"));
        officerField = new JTextField();
        panel.add(officerField);

        panel.add(new JLabel("Judge:"));
        judgeField = new JTextField();
        panel.add(judgeField);

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        panel.add(dateField);

        panel.add(new JLabel("Outcome:"));
        outcomeField = new JTextField();
        panel.add(outcomeField);

        panel.add(new JLabel("Case Details:"));
        caseDetailsField = new JTextField();
        panel.add(caseDetailsField);

        add(panel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new GridLayout(3, 1));

        victimModel = new DefaultTableModel(new String[]{"Victim Name", "Victim Gender"}, 0);
        victimTable = new JTable(victimModel);
        tablePanel.add(new JScrollPane(victimTable));
        JButton addVictimButton = new JButton("Add Victim");
        addVictimButton.addActionListener(e -> victimModel.addRow(new Object[]{"", ""}));
        tablePanel.add(addVictimButton);

        suspectModel = new DefaultTableModel(new String[]{"Suspect Name", "Suspect Gender"}, 0);
        suspectTable = new JTable(suspectModel);
        tablePanel.add(new JScrollPane(suspectTable));
        JButton addSuspectButton = new JButton("Add Suspect");
        addSuspectButton.addActionListener(e -> suspectModel.addRow(new Object[]{"", ""}));
        tablePanel.add(addSuspectButton);

        witnessModel = new DefaultTableModel(new String[]{"Witness Name", "Witness Gender"}, 0);
        witnessTable = new JTable(witnessModel);
        tablePanel.add(new JScrollPane(witnessTable));
        JButton addWitnessButton = new JButton("Add Witness");
        addWitnessButton.addActionListener(e -> witnessModel.addRow(new Object[]{"", ""}));
        tablePanel.add(addWitnessButton);

        add(tablePanel, BorderLayout.CENTER);

        saveButton = new JButton(isEditMode ? "Save Changes" : "Add Case");
        saveButton.addActionListener(e -> saveCaseDetails());
        add(saveButton, BorderLayout.SOUTH);
    }

    private void loadCaseDetails(int caseId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM cases WHERE Case_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, caseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                caseIdField.setText(String.valueOf(rs.getInt("Case_ID")));
                caseTitleField.setText(rs.getString("case_title"));
                caseTypeField.setText(rs.getString("Case_Type"));
                priorityLevelField.setText(rs.getString("Priority_Level"));
                firNoField.setText(rs.getString("FIR_NO"));
                statusField.setText(rs.getString("Status"));
                officerField.setText(rs.getString("User_ID"));
                judgeField.setText(rs.getString("Judge_ID"));
                dateField.setText(rs.getString("Filing_Date"));
                outcomeField.setText(rs.getString("Outcome"));
                caseDetailsField.setText(rs.getString("case_details"));
            }

            loadAssociatedDetails(caseId, "victims", victimModel);
            loadAssociatedDetails(caseId, "suspects", suspectModel);
            loadAssociatedDetails(caseId, "witnesses", witnessModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAssociatedDetails(int caseId, String tableName, DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE Case_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, caseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                String gender = rs.getString("Gender");
                model.addRow(new Object[]{name, gender});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveCaseDetails() {
        int caseId = Integer.parseInt(caseIdField.getText());
        String caseTitle = caseTitleField.getText();
        String caseType = caseTypeField.getText();
        String priorityLevel = priorityLevelField.getText();
        String firNo = firNoField.getText();
        String status = statusField.getText();
        String officer = officerField.getText();
        String judge = judgeField.getText();
        String date = dateField.getText();
        String outcome = outcomeField.getText();
        String caseDetails = caseDetailsField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (isEditMode) {
                String query = "UPDATE cases SET case_title = ?, Case_Type = ?, Priority_Level = ?, FIR_NO = ?, Status = ?, User_ID = ?, Judge_ID = ?, Filing_Date = ?, Outcome = ?, case_details = ? WHERE Case_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, caseTitle);
                stmt.setString(2, caseType);
                stmt.setString(3, priorityLevel);
                stmt.setString(4, firNo);
                stmt.setString(5, status);
                stmt.setString(6, officer);
                stmt.setString(7, judge);
                stmt.setString(8, date);
                stmt.setString(9, outcome);
                stmt.setString(10, caseDetails);
                stmt.setInt(11, caseId);
                stmt.executeUpdate();
            } else {
                String query = "INSERT INTO cases (Case_ID, case_title, Case_Type, Priority_Level, FIR_NO, Status, User_ID, Judge_ID, Filing_Date, Outcome, case_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, caseId);
                stmt.setString(2, caseTitle);
                stmt.setString(3, caseType);
                stmt.setString(4, priorityLevel);
                stmt.setString(5, firNo);
                stmt.setString(6, status);
                stmt.setString(7, officer);
                stmt.setString(8, judge);
                stmt.setString(9, date);
                stmt.setString(10, outcome);
                stmt.setString(11, caseDetails);
                stmt.executeUpdate();
            }

            saveAssociatedDetails(caseId, "victims", victimModel);
            saveAssociatedDetails(caseId, "suspects", suspectModel);
            saveAssociatedDetails(caseId, "witnesses", witnessModel);

            JOptionPane.showMessageDialog(this, isEditMode ? "Case updated successfully" : "Case added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving case details: " + e.getMessage());
        }
    }

    private void saveAssociatedDetails(int caseId, String tableName, DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete existing records for this case
            String deleteQuery = "DELETE FROM " + tableName + " WHERE Case_ID = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, caseId);
            deleteStmt.executeUpdate();

            // Insert new records
            String insertQuery = "INSERT INTO " + tableName + " (Case_ID, Name, Gender) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            for (int i = 0; i < model.getRowCount(); i++) {
                String name = (String) model.getValueAt(i, 0);
                String gender = (String) model.getValueAt(i, 1);
                insertStmt.setInt(1, caseId);
                insertStmt.setString(2, name);
                insertStmt.setString(3, gender);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddEditFrame frame = new AddEditFrame(false, 0);
            frame.setVisible(true);
        });
    }
}
