package Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utility {
	private static final Logger log = LogManager.getLogger(Utility.class);

	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/Register";
	private static final String USER = "root";
	private static final String PASS = "root";

	Connection connection = null;

	 public Connection establishDbConnection() {
		try {
			// Load the MySQL JDBC driver
			Class.forName(JDBC_DRIVER);

			// Establish the database connection
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			log.info("Database connection established successfully!");

		} catch (ClassNotFoundException | SQLException e) {
			log.error("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}

}
