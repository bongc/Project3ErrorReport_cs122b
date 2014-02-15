// JDBC Example - printing a database's metadata
// Coded by Chen Li/Kirill Petrov Winter, 2005
// Slightly revised for ICS185 Spring 2005, by Norman Jacobson

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class JDBCMain_39 {

	public static void main(String[] arg) throws Exception {

		// Incorporate mySQL driver
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		Scanner inputReader = new Scanner(System.in);
		System.out.println("Enter username:");
		String userName = inputReader.nextLine().trim();
		System.out.println("Enter password:");
		String password = inputReader.nextLine().trim();

		Connection connection = null;
		try {
			// Connect to the test database
			connection = DriverManager.getConnection("jdbc:mysql:///moviedb", userName, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("There was an error logging in. Check Logs.");
			if (inputReader != null) {
				inputReader.close();
			}
			return;
		}

		boolean programDone = false;
		while (!programDone) {
			System.out.println("Commands: PRINT, ADD STAR, ADD CUSTOMER, DELETE CUSTOMER, METADATA, RAW COMMAND, GENERATE REPORT, EXIT MENU, EXIT PROGRAM");

			String nextCommand = inputReader.nextLine().trim();
			if (nextCommand.toUpperCase().equals("EXIT PROGRAM")) {
				programDone = true;
			} else if (nextCommand.toUpperCase().equals("EXIT MENU")) {
				System.out.println("Enter username:");
				userName = inputReader.nextLine().trim();
				System.out.println("Enter password:");
				password = inputReader.nextLine().trim();
				try {
					// Connect to the test database
					connection = DriverManager.getConnection("jdbc:mysql:///moviedb", userName, password);
				} catch (SQLException e) {
					System.out.println("There was an error connecting to the database. Check logs.");
					System.out.println(e.getMessage());
					if (connection != null) {
						connection.close();
					}
					if (inputReader != null) {
						inputReader.close();
					}
					return;
				}
			} else if (nextCommand.toUpperCase().equals("PRINT")) {
				printCommand(inputReader, connection);
			} else if (nextCommand.toUpperCase().equals("ADD STAR")) {
				addStarCommand(inputReader, connection);
			} else if (nextCommand.toUpperCase().equals("ADD CUSTOMER")) {
				addCustomerCommand(inputReader, connection);
			} else if (nextCommand.toUpperCase().equals("DELETE CUSTOMER")) {
				System.out.println("Enter customer's id to delete:");
				String customerId = inputReader.nextLine().trim();
				if (QueryProcessor_39.deleteCustomer(customerId, connection))
					System.out.println("Customer deleted.");
			} else if (nextCommand.toUpperCase().equals("METADATA")) {
				QueryProcessor_39.showTableMetadata(connection);
			} else if (nextCommand.toUpperCase().equals("RAW COMMAND")) {
				rawCommand(inputReader, connection);
			} else if (nextCommand.toUpperCase().equals("GENERATE REPORT")){
				File f = new File("error.html");
				FileWriter fw = new FileWriter(f);
				fw.write(QueryProcessor_39.generateReport(connection));
				fw.flush();
				fw.close();
				System.out.println("Report saved to " + f.getAbsolutePath() + ".");
			}else {
				System.out.println("COMMAND WAS NOT RECOGNIZED!");
			}
			System.out.println("");
		}
		inputReader.close();
		connection.close();
		System.out.println("bye.");
	}

	private static void rawCommand(Scanner inputReader, Connection connection) throws SQLException {
		System.out.println("Enter SQL query:");
		String query = inputReader.nextLine().trim();
		boolean success = false;
		int resNum = -1;
		if (query.toUpperCase().contains("SELECT")) {
			success = QueryProcessor_39.selectRawQuery(connection, query);
			if (success) {
				System.out.println("Select successful");
			} else {
				System.out.println("Select unsuccessful, check logs.");
			}
		} else if (query.toUpperCase().contains("DELETE")) {
			resNum = QueryProcessor_39.deleteRawQuery(connection, query);
			if (resNum >= 1) {
				System.out.println("Deleted " + resNum + " row(s).");
			}
		} else if (query.toUpperCase().contains("UPDATE")) {
			resNum = QueryProcessor_39.updateRawQuery(connection, query);
			if (resNum >= 1) {
				System.out.println("Updated " + resNum + " row(s).");
			}
		} else if (query.toUpperCase().contains("INSERT")) {
			resNum = QueryProcessor_39.insertRawQuery(connection, query);
			if (resNum >= 1) {
				System.out.println("Inserted " + resNum + " row(s).");
			}
		}
	}

	private static void addStarCommand(Scanner inputReader, Connection connection) throws SQLException {
		System.out.println("Type the name of the star (First and/or Last):");
		String[] nameTokens = inputReader.nextLine().trim().split(" ");
		System.out.println("Optionally, give us a date of birth, press enter if unknown:");
		String dob = inputReader.nextLine().trim();
		System.out.println("Optionally, give us a photo URL, press enter if unknown:");
		String pUrl = inputReader.nextLine().trim();
		boolean success = false;
		if (nameTokens.length >= 2)
			success = QueryProcessor_39.addStar(connection, nameTokens[0], nameTokens[1], dob, pUrl);
		else if (nameTokens.length == 1)
			success = QueryProcessor_39.addStar(connection, "", nameTokens[0], dob, pUrl);
		if (success)
			System.out.println("Adding star successful!");
		else
			System.out.println("Adding star failed, please check output or error log.");
	}

	private static void printCommand(Scanner inputReader, Connection connection) throws SQLException {
		System.out.println("Would you like to query by name or ID?");
		if (inputReader.nextLine().trim().toUpperCase().equals("NAME")) {
			System.out.println("Enter the first and/or last name:");
			String[] nameTokens = inputReader.nextLine().trim().split(" ");
			if (nameTokens.length >= 2)
				QueryProcessor_39.printFromInputs(connection, nameTokens[0], nameTokens[1], null);
			else if (nameTokens.length == 1)
				QueryProcessor_39.printFromInputs(connection, null, nameTokens[0], null);
		} else {
			System.out.println("Enter the ID:");
			String ID = inputReader.nextLine().trim();
			QueryProcessor_39.printFromInputs(connection, null, null, ID);
		}
	}

	private static void addCustomerCommand(Scanner inputReader, Connection connection) throws SQLException {
		System.out.println("Type the name of the customer (First and/or Last):");
		String[] cNameTokens = inputReader.nextLine().trim().split(" ");
		System.out.println("Type customer credit card ID:");
		String cCCID = inputReader.nextLine().trim();
		System.out.println("Type customer address:");
		String cAddress = inputReader.nextLine().trim();
		System.out.println("Type customer email:");
		String cEmail = inputReader.nextLine().trim();
		System.out.println("Type customer password:");
		String cPassword = inputReader.nextLine().trim();
		if (cNameTokens[0].equals("") || cCCID.equals("") || cAddress.equals("") || cEmail.equals("")
				|| cPassword.equals("")) {
			// dont do anything
			System.out
					.println("Customer object is missing a field, make sure to fill everything out and try again.");
		} else {
			boolean success = false;
			if (cNameTokens.length >= 2)
				success = QueryProcessor_39.addCustomer(connection, cNameTokens[0], cNameTokens[1], cCCID,
						cAddress, cEmail, cPassword);
			else if (cNameTokens.length == 1)
				success = QueryProcessor_39.addCustomer(connection, "", cNameTokens[0], cCCID, cAddress,
						cEmail, cPassword);
			if (success)
				System.out.println("Adding customer successful!");
			else
				System.out.println("Adding customer failed, please check output or error log.");
		}
	}
}
