import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaseManagementApp extends JFrame {
    private JTextField caseIdField, victimField, caseTypeField;
    private JButton addButton, viewButton;
    private JTextArea resultArea;

    public CaseManagementApp() {
        setTitle("Case Management");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        caseIdField = new JTextField(20);
        victimField = new JTextField(20);
        caseTypeField = new JTextField(20);
        addButton = new JButton("Add Case");
        viewButton = new JButton("View Cases");
        resultArea = new JTextArea(10, 30);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(new JLabel("Case ID:"));
        panel.add(caseIdField);
        panel.add(new JLabel("Victim:"));
        panel.add(victimField);
        panel.add(new JLabel("Case Type:"));
        panel.add(caseTypeField);
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(new JScrollPane(resultArea));

        add(panel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCase();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCases();
            }
        });
    }

    private void addCase() {
        String caseId = caseIdField.getText();
        String victim = victimField.getText();
        String caseType = caseTypeField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO cases (Case_ID, Victim, Case_Type) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(caseId));
            statement.setString(2, victim);
            statement.setString(3, caseType);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Case added successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding case.");
        }
    }

    private void viewCases() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM cases";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            resultArea.setText("");
            while (resultSet.next()) {
                resultArea.append("Case ID: " + resultSet.getInt("Case_ID") + ", Victim: " + resultSet.getString("Victim") + ", Case Type: " + resultSet.getString("Case_Type") + "\n");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving cases.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CaseManagementApp().setVisible(true);
            }
        });
    }
}
