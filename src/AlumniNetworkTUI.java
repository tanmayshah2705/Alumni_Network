import java.util.*;
import java.sql.*;


public class AlumniNetworkTUI {

    private static Scanner scanner = new Scanner(System.in);
    private static boolean isRunning = true;

    public static void main(String[] args) {
        while (isRunning) {
            System.out.println("\n===== Alumni Network System =====");
            System.out.println("||      1. Register Alumni     ||");
            System.out.println("||      2. Login Alumni        ||");
            System.out.println("||      3. Admin Login         ||");
            System.out.println("||      0. Exit                ||");
	    System.out.println("=================================");
            System.out.print("Choose an option: ");

            try
	    {
       	    int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
	
            switch (choice) {
                case 1 -> registerAlumni();
                case 2 -> alumniLogin();
                case 3 -> adminLogin();
                case 0 -> exitApplication();
                default -> System.out.println("Invalid option! Try again.");
            }
    	    }catch(InputMismatchException ime){
	         System.out.println("Invalid option! Try again.");
  	         scanner.nextLine();
 	    }
        }
    }

    // Main Menu
    
        
    

    // Register Alumni
    public static void registerAlumni() {
        System.out.println("\n===== Register Alumni =====");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.println("Enter Email (firstName +.+ lastName + last2DigitsOfYear + @ + collegeDomain + . + collegeDomainExtension): ");
        String email = scanner.nextLine();
        System.out.print("Enter Graduation Year: ");
        int gradYear = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Department: ");
        String department = scanner.nextLine();
        System.out.print("Enter DOB(YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        System.out.print("Enter Location: ");
        String location = scanner.nextLine();
        System.out.print("Enter Occupation: ");
        String occupation = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Call the AlumniClass.registerAlumni method
        AlumniClass.registerAlumni(name, email, gradYear, department, dob, location, occupation, password);
    }

    // Alumni Login
    private static void alumniLogin() {
        System.out.print("\nEnter Alumni Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (Authenticate.authenticateAlumni(email, password)) {
            System.out.println("Alumni login successful!");
            alumniMenu(email);
        } else {
            System.out.println("Invalid credentials. Returning to Main Menu.");
        }
    }

    // Admin Login
    private static void adminLogin() {
        System.out.print("\nEnter Admin Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (Authenticate.authenticateAdmin(username, password)) {
            System.out.println("Admin login successful!");
            adminMenu(username);
        } else {
            System.out.println("Invalid credentials. Returning to Main Menu.");
        }
    }

    // Exit Application
    private static void exitApplication() {
        System.out.println("Exiting Alumni Network System...");
        isRunning = false;
    }


    // Admin Dashboard Menu
    private static void adminMenu(String username) {
        boolean adminLoggedIn = true;

        while (adminLoggedIn) {
          System.out.println("\n======== Admin Dashboard ========");
            System.out.println("||     1. View All Alumni      ||");
            System.out.println("||     2. View Alumni (by ID)  ||");
            System.out.println("||     3. Add Alumni           ||");
            System.out.println("||     4. Edit Alumni          ||");
            System.out.println("||     5. Delete Alumni        ||");
            System.out.println("||     6. Change Password      ||");
            System.out.println("||     0. Logout               ||");
	    System.out.println("=================================");
            System.out.print("Choose an option: ");
	
	    try
	    {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> AdminClass.viewAllAlumni();
                case 2 -> {
                    System.out.print("Enter Alumni ID: ");
		    int alumniId = scanner.nextInt();
            	    scanner.nextLine();
		    AdminClass.searchSpecificAlumni(alumniId);
                }
                case 3 -> AdminClass.addAlumni();
                case 4 -> {
                    System.out.print("Enter Alumni ID: ");
		    int alumniId = scanner.nextInt();
            	    scanner.nextLine();
		    AdminClass.editAlumni(alumniId);
                }
                case 5 -> {
                    System.out.print("Enter Alumni ID to delete: ");
                    int alumniId = scanner.nextInt();
                    AdminClass.deleteAlumni(alumniId);
                }
                case 6 -> {
                    System.out.print("Enter New Password: ");
                    String newPassword = scanner.nextLine();
                    AdminClass.changeAdminPassword(username,newPassword);
                }
                case 0 -> {
                    System.out.println("Logging out... Returning to Main Menu.");
                    adminLoggedIn = false;
                }
                default -> System.out.println("Invalid option! Try again.");
            }
	    }catch(InputMismatchException ime){
		System.out.println("Invalid option! Try again.");
  	         scanner.nextLine();
 	    }
        }
    }

    // Alumni Dashboard Menu
    private static void alumniMenu(String email) {
        boolean alumniLoggedIn = true;

        while (alumniLoggedIn) {
          System.out.println("\n======== Alumni Dashboard =========");
            System.out.println("||     1. View My Profile        ||");
	    System.out.println("||     2. Edit Profile           ||");
            System.out.println("||     3. View All Alumni        ||");
            System.out.println("||     4. Search Alumni (by ID)  ||");
            System.out.println("||     5. Change Password        ||");
            System.out.println("||     0. Logout                 ||");
	    System.out.println("===================================");
            System.out.print("Choose an option: ");

	    try
	    {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> AlumniClass.viewAlumniProfile(email);
                case 2 -> AlumniClass.editProfile(email);
                case 3 -> AlumniClass.viewAllAlumni();
		case 4 -> {
		    System.out.print("Enter Alumni ID: ");
		    int alumniId = scanner.nextInt();
            	    scanner.nextLine();
		    AlumniClass.searchSpecificAlumni(alumniId);
		}
                case 5 -> {
                    System.out.print("Enter New Password: ");
                    String newPassword = scanner.nextLine();
                    AlumniClass.changePassword(email, newPassword);
                }
                case 0 -> {
                    System.out.println("Logging out... Returning to Main Menu.\n");
                    alumniLoggedIn = false;
                }
                default -> System.out.println("Invalid option! Try again.");
            }
	    }catch(InputMismatchException ime){
		System.out.println("Invalid option! Try again.");
  	         scanner.nextLine();
 	    }
        }
    }

    
   
}