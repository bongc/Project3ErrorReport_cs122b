import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryProcessor_39 {

	public static boolean printFromInputs(Connection connection, String first, String last, String ID) throws SQLException {
		try {
			Statement select = connection.createStatement();
			ResultSet result = null;
			if (ID == null) {
				if (first != null)
					result = select.executeQuery("Select * from stars WHERE first_name = " + first + " AND " + " last_name=" + last);
				else
					result = select.executeQuery("Select * from stars WHERE last_name = '" + last + "' OR first_name = '" + last + "'");
			} else {
				result = select.executeQuery("Select * from stars WHERE id = '" + ID + "'");
			}
			ResultSetMetaData metadata = result.getMetaData();
			printMetadataHeader(metadata);
			StringBuilder resultRows = new StringBuilder();
			getResultsIntoSB(result, resultRows, metadata);
			System.out.println(resultRows.toString());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Print had an error, check logs!");
			return false;
		}
		return true;
	}

	private static void getResultsIntoSB(ResultSet result, StringBuilder resultRows, ResultSetMetaData metadata) throws SQLException {
		while (result.next()) {
			String format = "";
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				int padding = 20;
				if (result.getString(i) == null) {
					format += addPadding("", padding);
				} else if (result.getString(i).toUpperCase().contains("URL")) {
					padding *= 3;
					format += addPadding(result.getString(i), padding);
				} else {
					format += addPadding(result.getString(i), padding);
				}
			}
			resultRows.append(format + "\n");
		}
	}

	private static void printMetadataHeader(ResultSetMetaData metadata) throws SQLException {
		printBars(metadata);
		String format = "";
		// Print type of each attribute
		for (int i = 1; i <= metadata.getColumnCount(); i++) {
			String temp = metadata.getColumnName(i);
			int padding = 20;
			if (temp.toUpperCase().contains("URL"))
				padding *= 2;
			format += addPadding(temp, padding);
		}
		System.out.println(format);
		printBars(metadata);
	}

	private static void printBars(ResultSetMetaData metadata) throws SQLException {
		for (int i = 1; i <= metadata.getColumnCount(); i++) {
			for (int j = 0; j < 35; j++)
				System.out.print("-");
		}
		System.out.println("");
	}

	public static String addPadding(String name, int max) {
		for (int i = name.length(); i <= max; i++) {
			name += " ";
		}
		return name;
	}

	public static boolean addStar(Connection connection, String fname, String lname, String dob, String pUrl) throws SQLException {
		String query = "INSERT INTO stars (id, first_name, last_name";
		if (!dob.equals(""))
			query += ", dob";
		if (!pUrl.equals(""))
			query += ", photo_url";
		query += ") VALUES (DEFAULT, ";
		query += "'" + fname + "', '" + lname;
		if (!dob.equals(""))
			query += "', '" + dob;
		if (!pUrl.equals(""))
			query += "', '" + pUrl;
		query += "')";
		int result = 0;
		try {
			Statement select = connection.createStatement();
			result = select.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return result >= 1;
	}

	public static boolean addCustomer(Connection connection, String fname, String lname, String cCCID, String cAddress, String cEmail,
			String cPassword) throws SQLException {
		String query = "INSERT INTO customers (id, first_name, last_name, cc_id, address, email, password) VALUES(";
		query += "DEFAULT";
		query += ",'" + fname;
		query += "','" + lname;
		query += "','" + cCCID;
		query += "','" + cAddress;
		query += "','" + cEmail;
		query += "','" + cPassword + "')";
		int result = 0;
		try {
			Statement select = connection.createStatement();
			result = select.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return result >= 1;
	}

	public static boolean deleteCustomer(String customerId, Connection con) throws SQLException {
		Statement delete = con.createStatement();
		return delete.executeUpdate("DELETE FROM customers WHERE id = " + customerId) >= 1;
	}

	public static String getMetadata(String table, Connection con) throws SQLException {
		String metadata = "";
		Statement select = con.createStatement();
		ResultSet rs = select.executeQuery("SELECT * FROM " + table);
		ResultSetMetaData data = rs.getMetaData();

		metadata += ("Metadata for table: " + table.toUpperCase());
		metadata += ("\nThere are " + data.getColumnCount() + " columns.");
		metadata += ("\nThese column names are: ");

		for (int i = 1; i <= data.getColumnCount(); i++) {
			metadata += "\n " + i + "." + data.getColumnName(i) + " -- of type " + data.getColumnTypeName(i);
		}

		return metadata;
	}

	public static boolean selectRawQuery(Connection connection, String query) {
		try {
			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(query);
			ResultSetMetaData metadata = result.getMetaData();
			printMetadataHeader(metadata);
			StringBuilder resultRows = new StringBuilder();
			getResultsIntoSB(result, resultRows, metadata);
			System.out.println(resultRows.toString());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public static void showTableMetadata(Connection connection) {
		DatabaseMetaData md;
		try {
			md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				System.out.println("\n" + QueryProcessor_39.getMetadata(rs.getString(3), connection));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int deleteRawQuery(Connection con, String query) {
		Statement delete;
		try {
			delete = con.createStatement();
			return delete.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public static int updateRawQuery(Connection con, String query) throws SQLException {
		Statement update;
		try {
			update = con.createStatement();
			return update.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public static int insertRawQuery(Connection con, String query) throws SQLException {
		Statement update;
		try {
			update = con.createStatement();
			return update.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public static boolean checkIfEmployeeExists(Connection con, String email, String password) {
		try {
			Statement select = con.createStatement();
			String query = "SELECT * FROM EMPLOYEES WHERE email='" + email + "' AND password='" + password + "'";
			ResultSet result = select.executeQuery(query);
			return result.first();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

}
