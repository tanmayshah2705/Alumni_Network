import java.sql.*;

 // proxy class                     
public class AdminClass {

    public static void viewAllAlumni() {
        AlumniClass.viewAllAlumni();
    }

    public static void searchSpecificAlumni(int alumniId) {
        AlumniClass.searchSpecificAlumni(alumniId);
    }

    public static void addAlumni() {
        AlumniNetworkTUI.registerAlumni();
    }

    public static void editAlumni(int alumniId) {
        String sql = "SELECT email FROM alumni WHERE alumni_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alumniId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
		AlumniClass.editProfile(email);
            } else {
                System.out.println("No alumni found with ID: " + alumniId);
            }
    } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAlumni(int alumniId) {
        AlumniClass.deleteAlumni(alumniId);
    }

    public static void changeAdminPassword(String username,String newPassword) {
        try (Connection conn = DBConnection.connect()) {
            String sql = "UPDATE admin_credentials SET password = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPassword);
                stmt.setString(2, username);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Password updated successfully.");
                } else {
                    System.out.println("Failed to update password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
              }
        } catch (SQLException e) {
            e.printStackTrace();
          }
    }


}
