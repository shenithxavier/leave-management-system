package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.EmployeeDetails;
import model.LeaveDetails;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Utility;
import Request.EmployeeDetailsRequest;

public class EmployeeDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	Connection connection = null;
	PreparedStatement ps = null;
	Utility utility = new Utility();

	public EmployeeDetailsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		EmployeeDetailsRequest employeeDetailsRequest = objectMapper.readValue(request.getReader(),
				EmployeeDetailsRequest.class);

		String action = employeeDetailsRequest.getAction();
		Long id = employeeDetailsRequest.getId();
		log.info(id);

		try {
			connection = utility.establishDbConnection();
			log.info("Database connection established successfully!");

			String getEmployeeDetailsQuery = null;
			String jsonString = null;

			if (("getById".equals(action))) {
				getEmployeeDetailsQuery = "SELECT * FROM employeedetails WHERE Id = ?";

				// Create a prepare statement object for executing SQL queries
				ps = connection.prepareStatement(getEmployeeDetailsQuery);
				ps.setLong(1, id);
				ResultSet employeeDetailsRS = ps.executeQuery();
				if (employeeDetailsRS.next()) {
					EmployeeDetails employeeDetails=getEmployeeDetails(employeeDetailsRS);
					jsonString = objectMapper.writeValueAsString(employeeDetails);

				}else {
					response.setContentType("text/plain");
					response.getWriter().write("Invalid Id");
					return;	
				}
			} else {
				getEmployeeDetailsQuery = " select * from employeedetails";
				ps = connection.prepareStatement(getEmployeeDetailsQuery);

				// Execute the SQL query
				ResultSet employeeDetailsRS = ps.executeQuery();
				log.info("Executed the SQL query");
				List<EmployeeDetails> employeeDetailsList = new ArrayList<>();
				int count = 0;
				while (employeeDetailsRS.next()) {
					count++;
					EmployeeDetails employeeDetails = getEmployeeDetails(employeeDetailsRS);
					employeeDetailsList.add(employeeDetails);
				}
				if (count == 0) {
					response.setContentType("text/plain");
					response.getWriter().write("No Employee Details Available");
					return;
				}
				jsonString = objectMapper.writeValueAsString(employeeDetailsList);
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
		EmployeeDetails getEmployeeDetails(ResultSet employeeDetailsRS) throws SQLException {
			EmployeeDetails employeeDetails= new EmployeeDetails();
			employeeDetails.setId(employeeDetailsRS.getLong("Id"));
			employeeDetails.setEmployeeId(employeeDetailsRS.getString("Employee_ID"));
			employeeDetails.setEmployeeName(employeeDetailsRS.getString("Employee_Name"));
			employeeDetails.setEmail(employeeDetailsRS.getString("Email"));
			employeeDetails.setContactNumber(employeeDetailsRS.getLong("Contact_Number"));
			employeeDetails.setAddress(employeeDetailsRS.getString("Address"));
			employeeDetails.setStatus(employeeDetailsRS.getString("Status"));
			employeeDetails.setDob(employeeDetailsRS.getDate("Dob"));
			employeeDetails.setDoj(employeeDetailsRS.getDate("Doj"));
			employeeDetails.setDesignation(employeeDetailsRS.getString("Designation"));
			employeeDetails.setCreatedBy(employeeDetailsRS.getString("Created_dBy"));
			employeeDetails.setUpdatedBy(employeeDetailsRS.getString("Updated_By"));
			employeeDetails.setCreatedAt(employeeDetailsRS.getString("Created_At"));
			employeeDetails.setUpdatedAt(employeeDetailsRS.getString("Updated_At"));
			return employeeDetails;	
}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		EmployeeDetailsRequest employeeDetailsRequest = objectMapper.readValue(request.getReader(),
				EmployeeDetailsRequest.class);

		String employeeId = employeeDetailsRequest.getEmployeeId();
		String employeeName = employeeDetailsRequest.getEmployeeName();
		String email = employeeDetailsRequest.getEmail();
		Long contactNumber = employeeDetailsRequest.getContactNumber();
		String address = employeeDetailsRequest.getAddress();
		String status = employeeDetailsRequest.getStatus();
		Date dob = employeeDetailsRequest.getDob();
		Date doj = employeeDetailsRequest.getDoj();
		String designation = employeeDetailsRequest.getDesignation();
		String createdBy = "Admin";
		String createdAt = new Date().toString();
		String updatedBy = "Admin";
		String updatedAt = new Date().toString();

		try {
			connection = utility.establishDbConnection();

			String addEmployeeQuery = "INSERT INTO employeedetails (Employee_ID, Employee_Name, Email, Contact_Number, Address, Status,Dob,Doj,Designation,Created_dBy,Created_At,Updated_By,Updated_At) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// Create a PreparedStatement object for executing SQL queries
			ps = connection.prepareStatement(addEmployeeQuery);

			java.sql.Date Dob = new java.sql.Date(dob.getTime());
			java.sql.Date Doj = new java.sql.Date(doj.getTime());

			// Set parameters for the PreparedStatement
			ps.setString(1, employeeId);
			ps.setString(2, employeeName);
			ps.setString(3, email);
			ps.setLong(4, contactNumber);
			ps.setString(5, address);
			ps.setString(6, status);
			ps.setDate(7, Dob);
			ps.setDate(8, Doj);
			ps.setString(9, designation);
			ps.setString(10, createdBy);
			ps.setString(11, createdAt);
			ps.setString(12, updatedBy);
			ps.setString(13, updatedAt);

			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				log.info("Employee Details Added Successfully");
				response.setContentType("text/plain");
				response.getWriter().write("Employee Details Added Successfully");
				return;
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Failed to add Employee Details");
				return;			}

		} catch (SQLException e) {
			System.out.println("Error connecting to the database.");
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

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		EmployeeDetailsRequest employeeDetailsRequest = objectMapper.readValue(request.getReader(),
				EmployeeDetailsRequest.class);

		try {
			connection = utility.establishDbConnection();
			String query = "select * from employeedetails where Id=?";
			Long id = employeeDetailsRequest.getId();
			ps = connection.prepareStatement(query);
			ps.setLong(1, id);
			ResultSet employeeDetailsRs = ps.executeQuery();
			LeaveDetailServlet leaveDetailServlet = new LeaveDetailServlet();
			if (employeeDetailsRs.next()) {
				EmployeeDetails employeeDetails = leaveDetailServlet.getEmployeeDetails(employeeDetailsRs);


				String employeeId = employeeDetailsRequest.getEmployeeId() != null
						? employeeDetailsRequest.getEmployeeId()
						: employeeDetails.getEmployeeId();
				String employeeName = employeeDetailsRequest.getEmployeeName() != null
						? employeeDetailsRequest.getEmployeeName()
						: employeeDetails.getEmployeeName();
				String email = employeeDetailsRequest.getEmail() != null ? employeeDetailsRequest.getEmail()
						: employeeDetails.getEmail();
				Long contactNumber = employeeDetailsRequest.getContactNumber() != null
						? employeeDetailsRequest.getContactNumber()
						: employeeDetails.getContactNumber();
				String address = employeeDetailsRequest.getAddress() != null ? employeeDetailsRequest.getAddress()
						: employeeDetails.getAddress();
				String status = employeeDetailsRequest.getStatus() != null ? employeeDetailsRequest.getStatus()
						: employeeDetails.getStatus();
				Date dob = employeeDetailsRequest.getDob() != null ? employeeDetailsRequest.getDob()
						: employeeDetails.getDob();
				log.info("dob" + dob);
				Date doj = employeeDetailsRequest.getDoj() != null ? employeeDetailsRequest.getDoj()
						: employeeDetails.getDoj();
				String designation = employeeDetailsRequest.getDesignation() != null
						? employeeDetailsRequest.getDesignation()
						: employeeDetails.getDesignation();
				String updatedAt = new Date().toString();
				String updatedBy = "Admin";

				String updateEmployeeQuery = "UPDATE employeedetails SET Employee_Name=?, Email=?, Contact_Number=?, Address=?, Status=?, Dob=?,Doj=?, Designation=?, Updated_By=?, Updated_At=? WHERE Employee_ID=?";

				// Create a prepare statement object for executing SQL queries
				ps = connection.prepareStatement(updateEmployeeQuery);
				log.info("Created a prepare statement object for executing SQL queries");

				java.sql.Date dateOfBirth = new java.sql.Date(dob.getTime());
				java.sql.Date dateOfJoining = new java.sql.Date(doj.getTime());

				ps.setString(1, employeeName);
				ps.setString(2, email);
				ps.setLong(3, contactNumber);
				ps.setString(4, address);
				ps.setString(5, status);
				ps.setDate(6, dateOfBirth);
				ps.setDate(7, dateOfJoining);
				ps.setString(8, designation);
				ps.setString(9, updatedAt);
				ps.setString(10, updatedBy);
				ps.setString(11, employeeId);

				// Execute the SQL query
				int num = ps.executeUpdate();

				if (num > 0) {
					log.info("Employee details updated successfully");
					response.setContentType("text/plain");
					response.getWriter().write("Employee details updated successfully");
					return;
				} else {
					log.info("Failed to update employee details");
					response.setContentType("text/plain");
					response.getWriter().write("Failed to update employee details");
					return;
				}
			} else {
				log.info("invalid employee id");
				response.setContentType("text/plain");
				response.getWriter().write("invalid employee id");
				return;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		EmployeeDetailsRequest employeeDetailsRequest = objectMapper.readValue(request.getReader(),
				EmployeeDetailsRequest.class);

		String employeeId = employeeDetailsRequest.getEmployeeId();

		try {
			connection = utility.establishDbConnection();
			log.info("Database connection established successfully!");

			String deleteEmployeeQuery = "DELETE FROM employeedetails WHERE Employee_ID=?";

			// Create a prepare statement object for executing SQL queries
			ps = connection.prepareStatement(deleteEmployeeQuery);
			log.info("Created a statement object for executing SQL queries");
			ps.setString(1, employeeId);

			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				log.info("Employee deleted successfully");
				response.setContentType("text/plain");
				response.getWriter().write("Employee Deatils deleted successfully");
				return;
				
			} else {
				log.info("Failed to delete employee");
				response.setContentType("text/plain");
				response.getWriter().write("Failed to delete employee details");
				return;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
