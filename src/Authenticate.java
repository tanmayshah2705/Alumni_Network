import java.sql.*;

public class Authenticate {

    // Method to authenticate admin based on username and password

    public static boolean authenticateAdmin(String username, String password) {
        String sql = "SELECT password FROM admin_credentials WHERE username = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.isBeforeFirst()) { // Checks if ResultSet is empty (table empty or no match)
                    System.out.println("No admin accounts found or incorrect username.");
                    return false;
                }

                if (rs.next()) {
                    if (rs.getString("password").equals(password)) {
                        return true; // Authentication successful
                    } else {
                        System.out.println("Incorrect password.");
                        return false; // Incorrect password
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred while authenticating admin.");
            e.printStackTrace();
        }
        return false; // Authentication failed
    }





// Login method for alumni
    public static boolean authenticateAlumni(String email, String password) {
        try (Connection conn = DBConnection.connect()) {

            // Get Alumni ID using Email
            String getIdSql = "SELECT alumni_id FROM alumni WHERE email = ?";
            try (PreparedStatement getIdStmt = conn.prepareStatement(getIdSql)) {
                getIdStmt.setString(1, email);
                ResultSet idResult = getIdStmt.executeQuery();

                if (idResult.next()) {
                    int alumniId = idResult.getInt("alumni_id");

                    // Verify Password
                    String checkPasswordSql = "SELECT 1 FROM alumni_passwords WHERE alumni_id = ? AND password = ?";
                    try (PreparedStatement checkPasswordStmt = conn.prepareStatement(checkPasswordSql)) {
                        checkPasswordStmt.setInt(1, alumniId);
                        checkPasswordStmt.setString(2, password);

                        ResultSet passwordResult = checkPasswordStmt.executeQuery();
                        if (passwordResult.next()) {
                            System.out.println("Login successful. Welcome!");
                            return true;
                        } else {
                            System.out.println("Invalid password. Please try again.");
                            return false;
                        }
                    }
                } else {
                    System.out.println("Email not found. Please register first.");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error during login. Please try again.");
            e.printStackTrace();
            return false;
        }
    }
}
