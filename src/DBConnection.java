import java.sql.*;
import java.lang.*;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/miniProject_alumni_network?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";  // Update with your DB details
    private static final String DB_USER = "alumni_network_user";  // Replace with your DB username
    private static final String DB_PASSWORD = "alumni_network_pswd";  // Replace with your DB password
    
    // Establish and return a connection to the database
    public static Connection connect() throws SQLException {
	try{
	    Class.forName("com.mysql.cj.jdbc.Driver");
	    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	} catch(ClassNotFoundException cnfe){
	    System.out.println(cnfe);
	    return null;
	}
        
    }
}



