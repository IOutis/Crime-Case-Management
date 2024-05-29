import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewDataFrame extends JFrame {
    private JTable dataTable;
    private JScrollPane scrollPane;

    public ViewDataFrame(String dataType) {
        setTitle("View " + dataType.substring(0, 1).toUpperCase() + dataType.substring(1));
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI(dataType);
    }

    private void initUI(String dataType) {
        dataTable = new JTable();
        scrollPane = new JScrollPane(dataTable);
        add(scrollPane, BorderLayout.CENTER);

        loadData(dataType);
    }

    private void loadData(String dataType) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + dataType;
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            dataTable.setModel(new ResultSetTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
