package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(LoginServlet.class);

	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/Register";
	private static final String USER = "root";
	private static final String PASS = "root";

	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");


		try {
			// Load the MySQL JDBC driver
			Class.forName(JDBC_DRIVER);

			// Establish the database connection
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			// Create a statement object for executing SQL queries
			statement = connection.createStatement();

			String getLoginQuery = "SELECT * FROM registration_form WHERE username='" + username + "' AND password='"
					+ password + "'";

			// Execute the SQL query
			resultSet = statement.executeQuery(getLoginQuery);

			if (resultSet.next()) {
				RequestDispatcher rd = request.getRequestDispatcher("RegistrationSuccess.jsp");
				rd.forward(request, response);
				log.info("Login success");

			} else {
				RequestDispatcher rd = request.getRequestDispatcher("RegistrationFailed.jsp");
				rd.forward(request, response);
				log.info("Login failed");
			}

		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC driver not found.");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Error connecting to the database or executing the query.");
			e.printStackTrace();
		} finally {
			// Close the database connection
			try {
				if (connection != null) {
					connection.close();
					System.out.println("Connection closed");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}