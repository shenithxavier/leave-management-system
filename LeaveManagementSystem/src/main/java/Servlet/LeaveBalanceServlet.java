package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.EmployeeDetails;
import model.LeaveBalance;
import model.LeaveDetails;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Utility;
import Request.EmployeeDetailsRequest;
import Request.LeaveBalanceRequest;

public class LeaveBalanceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	Connection connection = null;
	PreparedStatement ps = null;
	Utility utility = new Utility();

	public LeaveBalanceServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveBalanceRequest leaveBalanceRequest = objectMapper.readValue(request.getReader(),
				LeaveBalanceRequest.class);

		try {
			String action = leaveBalanceRequest.getAction();
			String employeeId = leaveBalanceRequest.getEmployeeId();
			Long leveType = leaveBalanceRequest.getLeaveType();
			String jsonString = null;
			connection = utility.establishDbConnection();

			String query = null;
			if ("getById".equals(action)) {
				query = " select * from leavebalance where employeeId=? and leaveType=?";

				// Create a prepared statement object for executing SQL queries
				ps = connection.prepareStatement(query);
				ps.setString(1, employeeId);
				ps.setLong(2, leveType);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					LeaveBalance leaveBalance = getLeaveBalance(rs);
					jsonString = objectMapper.writeValueAsString(leaveBalance);
				} else {
					response.setContentType("text/plain");
					response.getWriter().write("Invalid  Employee Id/Leave Type Id");
					return;
				}

			} else {
				query = " select * from leavebalance where employeeId=?";
				ps = connection.prepareStatement(query);
				ps.setString(1, employeeId);
				ResultSet rs = ps.executeQuery();
				List<LeaveBalance> leaveBalanceList = new ArrayList<>();
				int count = 0;
				while (rs.next()) {
					count++;
					LeaveBalance leaveBalance = getLeaveBalance(rs);
					leaveBalanceList.add(leaveBalance);
				}
				if (count == 0) {
					response.setContentType("text/plain");
					response.getWriter().write("No Leave Balance Details Available");
					return;
				}
				jsonString = objectMapper.writeValueAsString(leaveBalanceList);
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

	LeaveBalance getLeaveBalance(ResultSet rs) throws SQLException {
		LeaveBalance leaveBalance = new LeaveBalance();

		leaveBalance.setId(rs.getLong("id"));
		leaveBalance.setLeaveType(rs.getLong("leaveType"));
		leaveBalance.setNumberOfLeave(rs.getLong("numberOfLeave"));
		leaveBalance.setEmployeeId(rs.getString("employeeId"));
		leaveBalance.setCreatedAt(rs.getString("createdAt"));
		leaveBalance.setCreatedBy(rs.getString("createdBy"));
		leaveBalance.setUpdatedAt(rs.getString("updatedAt"));
		leaveBalance.setUpdatedBy(rs.getString("updatedBy"));

		return leaveBalance;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveBalanceRequest leaveBalanceRequest = objectMapper.readValue(request.getReader(),
				LeaveBalanceRequest.class);

		try {

			String employeeId = leaveBalanceRequest.getEmployeeId();

			String query = "SELECT * FROM employeedetails WHERE Employee_ID = ?";
			connection = utility.establishDbConnection();
			// Create a prepare statement object for executing SQL queries
			ps = connection.prepareStatement(query);
			ps.setString(1, employeeId);
			ResultSet employeeDetailsRS = ps.executeQuery();
			if (!employeeDetailsRS.next()) {
				response.setContentType("text/plain");
				response.getWriter().write("Invalid Employee Id");
				return;
			}

			Long leaveType = leaveBalanceRequest.getLeaveType();
			String leaveTypeQuery = " select * from leave_type where Id=?";

			// Create a prepared statement object for executing SQL queries
			ps = connection.prepareStatement(leaveTypeQuery);
			ps.setLong(1, leaveType);
			ResultSet leaveTypeRS = ps.executeQuery();
			if (!leaveTypeRS.next()) {
				response.setContentType("text/plain");
				response.getWriter().write("Invalid LeaveType Id");
				return;
			}
			Long numberOfLeave = leaveBalanceRequest.getNumberOfLeave();
			String createdBy = "Admin";
			String createdAt = new Date().toString();
			String updatedBy = "Admin";
			String updatedAt = new Date().toString();
			String addLeaveBalancequery = "INSERT INTO leavebalance (employeeId, leaveType, numberOfLeave, createdBy, createdAt, updatedBy, updatedAt) VALUES (?,?,?,?,?,?,?)";

			// Create a prepared statement object for executing SQL queries
			ps = connection.prepareStatement(addLeaveBalancequery);

			ps.setString(1, employeeId);
			ps.setLong(2, leaveType);
			ps.setLong(3, numberOfLeave);
			ps.setString(4, createdBy);
			ps.setString(5, createdAt);
			ps.setString(6, updatedBy);
			ps.setString(7, updatedAt);

			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				response.setContentType("text/plain");
				response.getWriter().write("Leave Balance Added Succesfully");
				return;
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Failed to add Leave Balance");
				return;
			}

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
		LeaveBalanceRequest leaveBalanceRequest = objectMapper.readValue(request.getReader(),
				LeaveBalanceRequest.class);

		try {
			connection = utility.establishDbConnection();
			String query = "select * from leavebalance where id=?";
			Long id = leaveBalanceRequest.getId();
			ps = connection.prepareStatement(query);
			ps.setLong(1, id);
			ResultSet leaveBalanceRS = ps.executeQuery();
			LeaveBalanceServlet leaveBalanceServlet = new LeaveBalanceServlet();
			if (leaveBalanceRS.next()) {
				LeaveBalance leaveBalance = leaveBalanceServlet.getLeaveBalance(leaveBalanceRS);
				Long Id = leaveBalanceRequest.getId() != null ? leaveBalanceRequest.getId() : leaveBalance.getId();
				Long numofleave = leaveBalanceRequest.getNumberOfLeave();
				String updatedby = "Admin";
				String updatedat = new Date().toString();

				String updateLeaveBalanceQuery = "UPDATE leavebalance SET numberOfLeave=?, updatedBy=?,updatedAt=? WHERE id=?";

				// Create a prepared statement object for executing SQL queries
				ps = connection.prepareStatement(updateLeaveBalanceQuery);

				ps.setLong(1, numofleave);
				ps.setString(2, updatedby);
				ps.setString(3, updatedat);
				ps.setLong(4, Id);
				// Execute the SQL query
				int num = ps.executeUpdate();

				if (num > 0) {
					response.setContentType("text/plain");
					response.getWriter().write("Leave Balance Updated Succesfully");
					return;
				} else {
					response.setContentType("text/plain");
					response.getWriter().write("Failed to Update Leave Balance");
					return;				}
			}else{
				response.setContentType("text/plain");
				response.getWriter().write("Invalid Id");
				return;			
			}
		} catch (SQLException e) {
			log.error("Error: " + e.getMessage());
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
		LeaveBalanceRequest leaveBalanceRequest = objectMapper.readValue(request.getReader(),
				LeaveBalanceRequest.class);

		try {
			connection = utility.establishDbConnection();
			log.info("Database connection established successfully!");

			String query = "DELETE FROM leavebalance WHERE id=?";
			// Create a prepared statement object for executing SQL queries
			ps = connection.prepareStatement(query);
			log.info("Created a prepared statement object for executing SQL queries");
			Long id=leaveBalanceRequest.getId();
			ps.setLong(1, id);
			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				response.setContentType("text/plain");
				response.getWriter().write("Leave Balance Deleted successfully");
				return;
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Failed to Delete Leave Balance");
				return;			}

		} catch (SQLException e) {
			log.error("Error: " + e.getMessage());
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
