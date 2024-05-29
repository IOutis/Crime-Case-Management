import java.awt.*;
import javax.swing.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        JButton addButton = new JButton("Add Case");
        addButton.addActionListener(e -> {
            AddEditFrame addFrame = new AddEditFrame(false, 0);
            addFrame.setVisible(true);
        });
        panel.add(addButton);

        JButton editButton = new JButton("Edit Case");
        editButton.addActionListener(e -> {
            String caseIdStr = JOptionPane.showInputDialog(this, "Enter Case ID to Edit:");
            if (caseIdStr != null && !caseIdStr.isEmpty()) {
                try {
                    int caseId = Integer.parseInt(caseIdStr);
                    AddEditFrame editFrame = new AddEditFrame(true, caseId);
                    editFrame.setVisible(true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Case ID");
                }
            }
        });
        panel.add(editButton);

        JButton searchButton = new JButton("Advanced Search");
        searchButton.addActionListener(e -> {
            AdvancedSearchFrame searchFrame = new AdvancedSearchFrame();
            searchFrame.setVisible(true);
        });
        panel.add(searchButton);

        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame frame = new DashboardFrame();
            frame.setVisible(true);
        });
    }
}
