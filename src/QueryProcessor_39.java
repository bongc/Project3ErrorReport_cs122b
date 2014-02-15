import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
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
	
	public static String generateReport(Connection con){
		String report = "<!DOCTYPE html><html><style>" +
				"body{color: white; background-color: black;}" +
				"table{border:white;}" +
				"</style><body><h1>Error Report</h1>";

		
		String error1 = "SELECT id, title " +
				"FROM movies " +
				"WHERE movies.id " +
				"NOT IN (SELECT movies.id " +
				"FROM movies, stars_in_movies " +
				"WHERE movies.id = stars_in_movies.movie_id);";
		
		String error2 = "SELECT id, first_name, last_name " +
				"FROM stars " +
				"WHERE stars.id " +
				"NOT IN (SELECT stars.id " +
				"FROM stars, stars_in_movies " +
				"WHERE stars.id = stars_in_movies.star_id);";
		
		String error3 = "SELECT id, name " +
				"FROM genres " +
				"WHERE id " +
				"NOT IN (SELECT genres.id " +
				"FROM genres, genres_in_movies " +
				"WHERE genres.id = genres_in_movies.genre_id) " +
				"ORDER BY genres.name;";
		
		String error4 = "SELECT id, title " +
				"FROM movies " +
				"WHERE id " +
				"NOT IN (SELECT movies.id " +
				"FROM movies, genres_in_movies " +
				"WHERE movies.id = genres_in_movies.movie_id);";
		
		String error5 = "SELECT * " +
				"FROM stars " +
				"WHERE first_name IS NULL " +
				"OR first_name = '' " +
				"OR last_name IS NULL " +
				"OR last_name = '';";
		
		String error6 = "SELECT creditcards.id, creditcards.expiration " +
				"FROM customers, creditcards " +
				"WHERE creditcards.id = customers.cc_id AND creditcards.expiration < CURDATE() " +
				"ORDER BY creditcards.expiration;";
		
		String error7 = "SELECT a.id, a.title, a.year " +
				"FROM movies AS a, movies AS b " +
				"WHERE a.id<>b.id " +
				"AND a.year = b.year " +
				"AND a.title = b.title " +
				"GROUP BY a.id " +
				"ORDER BY a.title, a.year;";
		
		String error8 = "SELECT a.id, a.first_name, a.last_name, a.dob " +
				"FROM stars AS a, stars AS b " +
				"WHERE a.id<>b.id " +
				"AND a.first_name = b.first_name " +
				"AND a.last_name = b.last_name " +
				"AND a.dob = b.dob " +
				"GROUP BY a.id " +
				"ORDER BY a.first_name, a.last_name;";
		
		String error9 = "SELECT a.id, a.name " +
				"FROM genres AS a, genres AS b " +
				"WHERE a.id<>b.id " +
				"AND a.name = b.name " +
				"GROUP BY a.id " +
				"ORDER BY a.name;";
		
		String error10 = "SELECT id, first_name, last_name, dob FROM stars WHERE dob > CURDATE() OR YEAR(dob) < '1900';";
		
		String error11 = "SELECT id, email FROM customers WHERE email NOT LIKE '%@%';";
		
		try {
			Statement select1= con.createStatement();
			Statement select2= con.createStatement();
			Statement select3= con.createStatement();
			Statement select4= con.createStatement();
			Statement select5= con.createStatement();
			Statement select6= con.createStatement();
			Statement select7= con.createStatement();
			Statement select8= con.createStatement();
			Statement select9= con.createStatement();
			Statement select10 = con.createStatement();
			Statement select11 = con.createStatement();
			
			ResultSet rs1 = select1.executeQuery(error1);
			ResultSet rs2 = select2.executeQuery(error2);
			ResultSet rs3 = select3.executeQuery(error3);
			ResultSet rs4 = select4.executeQuery(error4);
			ResultSet rs5 = select5.executeQuery(error5);
			ResultSet rs6 = select6.executeQuery(error6);
			ResultSet rs7 = select7.executeQuery(error7);
			ResultSet rs8 = select8.executeQuery(error8);
			ResultSet rs9 = select9.executeQuery(error9);
			ResultSet rs10 = select10.executeQuery(error10);
			ResultSet rs11 = select11.executeQuery(error11);
			
			report += tableMaker(rs1, "\n<caption>The following list shows movies missing stars</caption>");
			report += tableMaker(rs2, "\n<caption>The following list shows stars in no movies</caption>");
			report += tableMaker(rs3, "\n<caption>The following list shows genres that do not have movies</caption>");
			report += tableMaker(rs4, "\n<caption>The following list shows movies without genres</caption>");
			report += tableMaker(rs5, "\n<caption>The following list shows stars with no first name OR no last name</caption>");
			report += tableMaker(rs6, "\n<caption>The following list shows expired credit cards for existing customers</caption>");
			report += tableMaker(rs7, "\n<caption>The following list shows movies that are the same with same year and same title</caption>");
			report += tableMaker(rs8, "\n<caption>The following list shows stars that are the same with same first name, last name, and date of birth</caption>");
			report += tableMaker(rs9, "\n<caption>The following list shows genres that are the same</caption>");
			report += tableMaker(rs10, "\n<caption>The following list shows stars with birth dates greater than today or earlier than 1900</caption>");
			report += tableMaker(rs11, "\n<caption>The following list shows customers that have emails with no '@' sign</caption>");
			
			rs1.close();
			rs2.close();
			rs3.close();
			rs4.close();
			rs5.close();
			rs6.close();
			rs7.close();
			rs8.close();
			rs9.close();
			rs10.close();
			rs11.close();
			select1.close();
			select2.close();
			select3.close();
			select4.close();
			select5.close();
			select6.close();
			select7.close();
			select8.close();
			select9.close();
			select10.close();
			select11.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		report += "\n</html></body>";
		
		return report;
	}
	
	private static String tableMaker(ResultSet rs, String caption){
		String report = "\n<table border=\"1\">";
		report += caption;
		int resultCount =0;
		try {
			report += "\n<tr>";
			ResultSetMetaData data = rs.getMetaData();
			for(int i = 1; i <=data.getColumnCount(); i++){
				report += "\n<th>" + data.getColumnName(i) + "</th>";
			}
			report += "\n</tr>";
			while(rs.next()){
				report += "<tr>";
				for(int i = 1; i <=data.getColumnCount(); i++){
					report += "\n<td>" + rs.getString(data.getColumnName(i)) + "</td>";
				}
				resultCount++;
				report += "</tr>";
				
			}
		
		report += "\n</table>";
		report += "<b>" + resultCount + " results returned.</b>";
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
		return report;
	}
}
