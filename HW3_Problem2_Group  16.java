//Import statements
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
//Class
public class sample {
	// Database credentials
	final static String HOSTNAME = "****-sql-server.database.windows.net";
	final static String DBNAME = "HW-2";
	final static String USERNAME = "*****";
	final static String PASSWORD = "****";
	// Database connection string
	final static String URL = String.format(
			"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
			HOSTNAME, DBNAME, USERNAME, PASSWORD);
	// Query templates
	final static String QUERY_TEMPLATE_1 = "{call insertintofaculty(?,?,?)}";
	final static String QUERY_TEMPLATE_2 = "{call insertintofaculty2(?,?,?,?)}";
	final static String QUERY_TEMPLATE_3 = "Select * from faculty";
	// User input prompt//
	final static String PROMPT = "\nPlease select one of the options below: \n" + "1) Insert new Faculty member: \n"
			+"2) Insert new Faculty member with exclude option \n" + "3) Display all faculty \n" + "4) Exit!";

	public static void main(String[] args) throws SQLException {
		System.out.println("Welcome to the sample application!");
		final Scanner sc = new Scanner(System.in); // Scanner is used to collect the user input
		String option = ""; // Initialize user option selection as nothing
		while (!option.equals("4")) { // Ask user for options until option 4 is selected
			System.out.println(PROMPT); // Print the available options
			option = sc.next(); // Read in the user option selection
			switch (option) { // Switch between different options
			case "1": // Insert a new Faculty option
				// Collect the new Faculty data from the user
				try {
				System.out.println("Please enter integer faculty ID:");
				final int fid = sc.nextInt(); // Read in the user input of faculty ID
				System.out.println("Please enter faculty name:");
				// We call nextLine to consume that newline character, so that subsequent
				// nextLine doesn't return nothing.
				sc.nextLine();
				final String fname = sc.nextLine(); // Read in user input of faculty Name (white-spaces allowed).
				System.out.println("Please enter department id:");
				// No need to call nextLine extra time here, because the preceding nextLine
				// consumed the newline character.
				final int deptid = sc.nextInt(); // Read in user input of faculty deptId (white-spaces allowed).
				System.out.println("Connecting to the database...");
				// Get a database connection and prepare a query statement
				try (final Connection connection = DriverManager.getConnection(URL)) {
					try (final CallableStatement statement = connection.prepareCall(QUERY_TEMPLATE_1)) {
						// Populate the query template with the data collected from the user
						statement.setInt(1,fid );
						statement.setString(2, fname);
						statement.setInt(3, deptid);
						System.out.println("Dispatching the query...");
						// Actually execute the populated query
						final int rows_inserted = statement.executeUpdate();
						System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
					}
				}
				}
				catch(InputMismatchException e)
				{
					System.out.println("Caught Input mismatch -- ");
					sc.nextLine();
					
				}
				catch (Exception e)
				{
					System.out.println("Couldn't execute the above query!");
					System.out.println("Reason -- ");
					System.out.println(e.getMessage());
					System.out.println("Try again !!!");
				}
				break;
			case "2": // Insert a new faculty with exclude department option
				// Collect the new faculty data from the user
				System.out.println("Please enter integer faculty ID:");
				try {
				final int fid_1 = sc.nextInt(); // Read in the user input of faculty ID
				System.out.println("Please enter faculty name:");
				// Preceding nextInt, nextFloar, etc. do not consume new line characters from
				// the user input.
				// We call nextLine to consume that newline character, so that subsequent
				// nextLine doesn't return nothing.
				sc.nextLine();
				final String fname_1 = sc.nextLine(); // Read in user input of faculty Name (white-spaces allowed).
				System.out.println("Please enter department id:");
				// No need to call nextLine extra time here, because the preceding nextLine
				// consumed the newline character.
				final int deptid_1 = sc.nextInt(); // Read in user input of faculty deptID.
				System.out.println("Please enter exclude department id:");
				sc.nextLine();
				// Preceding nextInt, nextFloar, etc. do not consume new line characters from
				// the user input.
				// We call nextLine to consume that newline character, so that subsequent
				// nextLine doesn't return nothing.
				final int exclude_deptid = sc.nextInt();
				
				System.out.println("Connecting to the database...");
				// Get a database connection and prepare a query statement
				try (final Connection connection = DriverManager.getConnection(URL)) {
					try (final CallableStatement statement = connection.prepareCall(QUERY_TEMPLATE_2)) {
						// Populate the query template with the data collected from the user
						statement.setInt(1,fid_1 );
						statement.setString(2, fname_1);
						statement.setInt(3, deptid_1);
						statement.setInt(4, exclude_deptid);
						System.out.println("Dispatching the query...");
						// Actually execute the populated query
						final int rows_inserted = statement.executeUpdate();
						System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
					}
				}
				}
				catch(InputMismatchException e)
				{
					System.out.println("Caught Input mismatch");
					sc.nextLine();
					break;
				}
				catch (Exception e)
				{
					System.out.println("Couldn't execute the above query!");
					System.out.println("Reason -- ");
					System.out.println(e.getMessage());
					System.out.println("Try again !!!");
				}
				break;
			case "3":
				System.out.println("Connecting to the database...");
				// Get the database connection, create statement and execute it right away, as
				// no user input need be collected
				try (final Connection connection = DriverManager.getConnection(URL)) {
					System.out.println("Dispatching the query...");
					try (final Statement statement = connection.createStatement();
							final ResultSet resultSet = statement.executeQuery(QUERY_TEMPLATE_3)) {
						System.out.println("Contents of the Faculty table:");
						System.out.println("fid | fname | deptid | salary ");
						// Unpack the tuples returned by the database and print them out to the user
						while (resultSet.next()) {
							System.out.println(
									String.format("%s | %s | %s | %s", resultSet.getInt(1), resultSet.getString(2),
											resultSet.getInt(3), resultSet.getDouble(4)));
						}
					}
				}
				break;
			case "4": // Do nothing, the while loop will terminate upon the next iteration
				System.out.println("Exiting! Goodbuy!");
				break;
			default: // Unrecognized option, re-prompt the user for the correct one
				System.out.println(String.format("Unrecognized option: %s\n" + "Please try again!", option));
				break;
			}
		}
		sc.close(); // Close the scanner before exiting the application
	}
}