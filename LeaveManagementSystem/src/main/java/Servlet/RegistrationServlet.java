package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Utility;
import Request.LeaveBalanceRequest;
import Request.RegistrationRequest;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LeaveBalance;
import model.Registration;

public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L; // why this added?

	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);


	Connection connection = null;
	ResultSet rs = null;
	Statement statement = null;
	Utility utility = new Utility();


	public RegistrationServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		ObjectMapper objectMapper = new ObjectMapper();
		RegistrationRequest registrationRequest = objectMapper.readValue(request.getReader(),
				RegistrationRequest.class);
		try {
			String action = registrationRequest.getAction();
			Long id = registrationRequest.getId();
			String jsonString = null;
			connection = utility.establishDbConnection();

			if ("getById".equals(action)) {
				String getRegistrationQuery = "SELECT * FROM registration_form where id=" +id;
				// Create a statement object for executing SQL queries
				statement = connection.createStatement();
				log.info("Created a statement object for executing SQL queries");

				// Execute the SQL query
				rs = statement.executeQuery(getRegistrationQuery);
				log.info("Executed the SQL query");
				if (rs.next()) {
					log.info("UserName is "+rs.getString("username"));
					Registration registration = getRegistration(rs);
					jsonString = objectMapper.writeValueAsString(registration);
				}else {
					response.setContentType("text/plain");
					response.getWriter().write("Invalid Id");
					return;
				}

			} else {
				String query = "SELECT * FROM registration_form";
				statement = connection.createStatement();
				rs = statement.executeQuery(query);
				List<Registration> registrationList = new ArrayList<>();

				while (rs.next()) {
					Registration registration = getRegistration(rs);
					registrationList.add(registration);
				}
				jsonString = objectMapper.writeValueAsString(registrationList);
			}
			
			response.setContentType("text/plain");
			response.getWriter().write(jsonString);
			return;
		} catch (SQLException e) {
			log.info("Error connecting to the database.");
			e.printStackTrace();
		} finally {
			// Close the database connection
			try {
				if (connection != null) {
					connection.close();
					log.info("Connection closed");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	Registration getRegistration(ResultSet rs) throws SQLException {
		Registration registration = new Registration();
		
		registration.setId(rs.getLong("id"));
		registration.setUserName(rs.getString("username"));
		registration.setPassword(rs.getString("password"));
		registration.setAddress(rs.getString("address"));
		registration.setCity(rs.getString("city"));
		registration.setPin(rs.getString("pin"));
		registration.setContactNumber(rs.getString("contactnumber"));
		registration.setEmail(rs.getString("email"));

		return registration;

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		System.out.println("inside doPost method");

		String UserName = request.getParameter("username");
		String Password = request.getParameter("password");
		String Address = request.getParameter("address");
		String City = request.getParameter("city");
		String Pin = request.getParameter("pin");
		String ContactNumber = request.getParameter("contactNumber");
		String Email = request.getParameter("email");

		try {
			connection = utility.establishDbConnection();
			// Create a statement object for executing SQL queries
			statement = connection.createStatement();

			String addRegisterQuery = "INSERT INTO registration_form (username, password, address, city, pin, contactnumber, email) "
					+ "VALUES ('" + UserName + "', '" + Password + "', '" + Address + "', '" + City + "', '" + Pin
					+ "', '" + ContactNumber + "', '" + Email + "')";

			// Execute the SQL query
			int num = statement.executeUpdate(addRegisterQuery);

			if (num > 0) {
				RequestDispatcher rd = request.getRequestDispatcher("Success.jsp");
				rd.forward(request, response);
				log.info("Registration Success");

			} else {
				RequestDispatcher rd = request.getRequestDispatcher("Failed.jsp");
				rd.forward(request, response);
				log.info("Registration Failed");
			}

		} catch (SQLException e) {
			log.info("Error connecting to the database.");
			e.printStackTrace();
		} finally {
			// Close the database connection
			try {
				if (connection != null) {
					connection.close();
					log.info("Connection closed");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
