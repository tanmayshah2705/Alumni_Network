import java.sql.*;
import java.util.Scanner;

public class AlumniClass 
{
    private static final Scanner scanner = new Scanner(System.in);

    // register user 
    public static void registerAlumni(String name, String email, int graduationYear, String department, String dob, String location, String occupation, String password) 
    {
        try (Connection conn = DBConnection.connect()) {

            // Generate Alumni ID
            int alumniId = generateAlumniId(graduationYear, conn);

            // Insert Alumni Details
            String alumniSql = "INSERT INTO alumni (alumni_id, name, email, graduation_year, "
                             + "department, dob, location, occupation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement alumniStmt = conn.prepareStatement(alumniSql)) {
                alumniStmt.setInt(1, alumniId);
                alumniStmt.setString(2, name);
                alumniStmt.setString(3, email);
                alumniStmt.setInt(4, graduationYear);
                alumniStmt.setString(5, department);
                alumniStmt.setString(6, dob);
                alumniStmt.setString(7, location);
                alumniStmt.setString(8, occupation);
                alumniStmt.executeUpdate();
            }

            // Insert Password
            String passwordSql = "INSERT INTO alumni_passwords (alumni_id, password) VALUES (?, ?)";
            try (PreparedStatement passwordStmt = conn.prepareStatement(passwordSql)) {
                passwordStmt.setInt(1, alumniId);
                passwordStmt.setString(2, password);
                passwordStmt.executeUpdate();
            }

            // Insert Initial Entries in Education and Career Tables
            try (PreparedStatement eduStmt = conn.prepareStatement("INSERT INTO education (alumni_id) VALUES (?)");
                 PreparedStatement careerStmt = conn.prepareStatement("INSERT INTO career (alumni_id) VALUES (?)")) {

                eduStmt.setInt(1, alumniId);
                careerStmt.setInt(1, alumniId);

                eduStmt.executeUpdate();
                careerStmt.executeUpdate();
            }

            System.out.println("User registered successfully with Alumni ID: " + alumniId);

        } catch (SQLException e) {
            System.out.println("Error during registration. Please try again.");
            e.printStackTrace();
        }
    }

    // Example Alumni ID Generator
    private static int generateAlumniId(int graduationYear, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM alumni WHERE graduation_year = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, graduationYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int rollNumber = rs.getInt("count") + 1;
                return Integer.parseInt(graduationYear + String.format("%03d", rollNumber));
            } else {
                return Integer.parseInt(graduationYear + "001");
            }
        }
    }



    // View profile by alumni ID
    public static void viewAlumniProfile(String femail) {
        try (Connection conn = DBConnection.connect()) {
		
	    int alumniId=0;
	    String sql2 = "SELECT alumni_id FROM alumni WHERE email = ?";
	    try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt2.setString(1, femail);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                alumniId = rs2.getInt("alumni_id");
            } else {
                System.out.println("Alumni not found with this email.");
                return;
            }
	}

            // Display alumni details
            String sql = "SELECT * FROM alumni WHERE alumni_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, alumniId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("=================== Alumni Profile =====================\n");    
		    System.out.println("             Alumni ID - " + rs.getInt("alumni_id"));
                    System.out.println("                  Name - " + rs.getString("name"));
                    System.out.println("                 Email - " + rs.getString("email"));
                    System.out.println("       Graduation Year - " + rs.getInt("graduation_year"));
                    System.out.println("            Department - " + rs.getString("department"));
                    System.out.println("         Date of birth - " + rs.getDate("dob"));
                    System.out.println("              Location - " + rs.getString("location"));
                    System.out.println("            Occupation - " + rs.getString("occupation")); 

                    // Display educational details
                    System.out.println("\n                  Education:-");
                    displayEducationDetails(conn, alumniId);

                    // Display career details
                    System.out.println("\n                   Career:-");
                    displayCareerDetails(conn, alumniId);

                } else {
                    System.out.println("Alumni not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching profile details: " + e.getMessage());
        }
    }

    // Helper method to display Education details
    private static void displayEducationDetails(Connection conn, int alumniId) throws SQLException {
        String eduSql = "SELECT institution, duration FROM education WHERE alumni_id = ?";
        try (PreparedStatement eduStmt = conn.prepareStatement(eduSql)) {
            eduStmt.setInt(1, alumniId);
            ResultSet eduRs = eduStmt.executeQuery();
            if (!eduRs.isBeforeFirst()) {
                System.out.println("No education details available.");
            } else {
                while (eduRs.next()) {
                    System.out.println("           Institution - " + eduRs.getString("institution"));
                    System.out.println("                 Years - " + eduRs.getString("duration"));
                }
            }
        }
    }

    // Helper method to display Career details
    private static void displayCareerDetails(Connection conn, int alumniId) throws SQLException {
        String careerSql = "SELECT company, job_title, duration FROM career WHERE alumni_id = ?";
        try (PreparedStatement careerStmt = conn.prepareStatement(careerSql)) {
            careerStmt.setInt(1, alumniId);
            ResultSet careerRs = careerStmt.executeQuery();
            if (!careerRs.isBeforeFirst()) {
                System.out.println("No career details available.");
            } else {
                while (careerRs.next()) {
                    System.out.println("               Company - " + careerRs.getString("company"));
                    System.out.println("             Job Title - " + careerRs.getString("job_title"));
                    System.out.println("                 Years - " + careerRs.getString("duration"));
		    System.out.println("========================================================");
                }
            }
        }
    }

    // Edit profile by alumni ID
    public static void editProfile(String email) {
        System.out.println("==== Edit Profile ====");
        System.out.println("1. Edit Personal Details");
        System.out.println("2. Add/Edit Educational Details");
        System.out.println("3. Add/Edit Career Details");
        System.out.println("0. Go back");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline character

        switch (choice) {
            case 1:
                editPersonalDetails(email);
                break;
            case 2:
                addEditEducationDetails(email);
                break;
            case 3:
                addEditCareerDetails(email);
                break;
	    case 0:
		return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Edit personal details
    private static void editPersonalDetails(String femail ) {
        try (Connection conn = DBConnection.connect()) {
            
	    int alumniId=0;
	    String sql2 = "SELECT alumni_id FROM alumni WHERE email = ?";
	    try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt2.setString(1, femail);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                    alumniId = rs2.getInt("alumni_id");
                } else {
                    System.out.println("No alumni found with the provided email.");
                    return;
                }
	}	    

	    System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Department: ");
            String department = scanner.nextLine();
            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine();
            System.out.print("Location: ");
            String location = scanner.nextLine();
            System.out.print("Occupation: ");
            String occupation = scanner.nextLine();

            String sql = "UPDATE alumni SET name = ?, email = ?, department = ?, dob = ?, location = ?, occupation = ? WHERE alumni_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, department);
                stmt.setDate(4, Date.valueOf(dob));
                stmt.setString(5, location);
                stmt.setString(6, occupation);
                stmt.setInt(7, alumniId);
                stmt.executeUpdate();
                System.out.println("Personal details updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating personal details: " + e.getMessage());
        }
    }

    // Add or Edit educational details
    private static void addEditEducationDetails(String femail) {
        try (Connection conn = DBConnection.connect()) {
            
	    int alumniId=0;
	    String sql2 = "SELECT alumni_id FROM alumni WHERE email = ?";
	    try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt2.setString(1, femail);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                    alumniId = rs2.getInt("alumni_id");
                } else {
                    System.out.println("No alumni found with the provided email.");
                    return;
                }
	    }

	    System.out.print("Institution Name: ");
            String institution = scanner.nextLine();
            System.out.print("Year(s) of Completion (e.g., 2011-2014): ");
            String duration = scanner.nextLine();

	    String sql = "UPDATE education SET institution = ?, duration = ? WHERE alumni_id = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    		stmt.setString(1, institution);
    		stmt.setString(2, duration);
    		stmt.setInt(3, alumniId);

    		int rowsUpdated = stmt.executeUpdate();
    		if (rowsUpdated > 0) {
        	    System.out.println("Education details updated successfully.");
    		} else {
        	    System.out.println("No existing education record found for this alumni. Please check registration.");
    		}
	    }
        } catch (SQLException e) {
            System.out.println("Error updating education details: " + e.getMessage());
        }
    }

    // Add or Edit career details
    private static void addEditCareerDetails(String femail) {
        try (Connection conn = DBConnection.connect()) {
            
	    int alumniId=0;
	    String sql2 = "SELECT alumni_id FROM alumni WHERE email = ?";
	    try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                stmt2.setString(1, femail);
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    alumniId = rs2.getInt("alumni_id");
                } else {
                    System.out.println("No alumni found with the provided email.");
                    return;
                }
	    }

	    System.out.print("Company Name: ");
            String company = scanner.nextLine();
            System.out.print("Job Title: ");
            String jobTitle = scanner.nextLine();
            System.out.print("Years of Experience (e.g., 2020-Present): ");
            String duration = scanner.nextLine();

            String sql = "UPDATE career SET company = ?, job_title = ?, duration = ? WHERE alumni_id = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    		stmt.setString(1, company);
    		stmt.setString(2, jobTitle);
    		stmt.setString(3, duration);
    		stmt.setInt(4, alumniId);

    		int rowsUpdated = stmt.executeUpdate();
    		if (rowsUpdated > 0) {
        	    System.out.println("Career details updated successfully.");
    		} else {
        	    System.out.println("No existing career record found for this alumni. Please check registration.");
    		}
	    }
        } catch (SQLException e) {
            System.out.println("Error updating career details: " + e.getMessage());
        }
    }   
   
    // 5. View all alumni (for alumni user)
    public static void viewAllAlumni() {
    try (Connection conn = DBConnection.connect()) {
        String sql = "SELECT alumni_id, name, email, graduation_year, department, location FROM alumni";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n======================= All Alumni =======================\n");

            // Header
            String header = String.format("%-5s    %-25s%-35s %-10s %-25s %-20s",
                    "ID", "Name", "Email", "Year", "Department", "Location");
            System.out.println(header);
            System.out.println("-------------------------------------------------------------------------------------------------------------------");

            // Each row
            while (rs.next()) {
                String row = String.format("%-5d  %-25s%-35s %-10d %-25s %-20s",
                        rs.getInt("alumni_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("graduation_year"),
                        rs.getString("department"),
                        rs.getString("location"));
                System.out.println(row);
            }

            System.out.println(); // Blank line for neatness
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving alumni list: " + e.getMessage());
    }
}



    // 7. Search for a specific alumni by ID
    public static void searchSpecificAlumni(int alumniId) {
            String sql = "SELECT email FROM alumni WHERE alumni_id = ?";
            String femail="";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
             stmt.setInt(1,alumniId);
             ResultSet rs = stmt.executeQuery();
            
             if (rs.next()) {
                 femail = rs.getString("email");
  		 viewAlumniProfile(femail);
             } else {
                 System.out.println("No alumni found with ID: " + alumniId);
             }
        } catch (SQLException e) {
            System.out.println("Error retrieving email from database.");
            e.printStackTrace();
        }
    }

    // 8. Change password for the logged-in alumni
    public static void changePassword(String femail, String newPassword) {
        try (Connection conn = DBConnection.connect()) {

	    int alumniId=0;
	    String sql2 = "SELECT alumni_id FROM alumni WHERE email = ?";
	    try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt2.setString(1, femail);
            ResultSet rs2 = stmt2.executeQuery();
            alumniId = rs2.getInt("alumni_id");
	}

            String sql = "UPDATE alumni_passwords SET password = ? WHERE alumni_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPassword);
                stmt.setInt(2, alumniId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Password updated successfully.");
                } else {
                    System.out.println("Failed to update password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //9. delete alumni
    public static void deleteAlumni(int alumniId) {
        String sql = "DELETE FROM alumni WHERE alumni_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alumniId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Alumni deleted successfully.");
            } else {
                System.out.println("No alumni found with the provided ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error occurred while deleting alumni.");
            e.printStackTrace();
        }
    }

}



