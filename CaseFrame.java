import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;

public class CaseFrame extends JFrame {
    private JTable caseTable;
    private ResultSetTableModel tableModel;

    public CaseFrame() {
        setTitle("Case Management Portal - Cases");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        caseTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(caseTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadCases();
    }

    private void loadCases() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM cases";
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            tableModel = new ResultSetTableModel(rs);
            caseTable.setModel(tableModel);

            // Close the ResultSet and Statement as the data is now cached in the table model
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error");
        } finally {
            // Ensure resources are closed if an exception occurs
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
