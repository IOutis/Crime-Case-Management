import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Test {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/case_management";
        String user = "root";
        String password = "mmh13138";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully.");
            Statement stmt = conn.createStatement();

            // Execute a SELECT query to fetch all users
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users");
            while (rs.next()) {
                int userId = rs.getInt("User_ID");
                String name = rs.getString("Name");
                String specialization = rs.getString("Specialization");
                String gender = rs.getString("Gender");
                String login = rs.getString("Login");
                String user_password = rs.getString("Password");
                String role = rs.getString("Role");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
            
                // Construct a string to display the user data
                String userData = "User ID: " + userId + ", Name: " + name + ", Specialization: " + specialization +
                                  ", Gender: " + gender + ", Login: " + login + ", Role: " + role +
                                  ", Email: " + email + ", Phone: " + phone;
            
                // Print the user data
                System.out.println(userData);
            }

            // Close the result set, statement, and connection
            rs.close();
            stmt.close();
            conn.close();
            
            // Perform database operations here
            
        } catch (Exception e) {
            System.out.println("Error while connecting to MySQL Database :");
			e.printStackTrace();
        } 
      }
}