package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LeaveBalance;
import model.LeaveType;

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
import Request.LeaveTypeRequest;

public class LeaveTypeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	Connection connection = null;
	PreparedStatement ps = null;
	Utility utility = new Utility();

	public LeaveTypeServlet() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveTypeRequest leaveTypeRequest = objectMapper.readValue(request.getReader(), LeaveTypeRequest.class);

		try {
			String action = leaveTypeRequest.getAction();
			Long id = leaveTypeRequest.getId();
			connection = utility.establishDbConnection();
			String jsonString = null;
			String getLeaveTypeQuery = null;
			if ("getById".equals(action)) {

				getLeaveTypeQuery = " select * from leave_type where Id=?";

				// Create a prepared statement object for executing SQL queries
				ps = connection.prepareStatement(getLeaveTypeQuery);
				log.info("Created a prepared statement object for executing SQL queries");
				ps.setLong(1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					LeaveType leaveType = leaveTypeRecords(rs);
					jsonString = objectMapper.writeValueAsString(leaveType);
				} else {
					response.setContentType("text/plain");
					response.getWriter().write("Invalid Id");
					return;
				}
			} else {
				getLeaveTypeQuery = " select * from leave_type";
				ps = connection.prepareStatement(getLeaveTypeQuery);
				ResultSet rs = ps.executeQuery();
				List<LeaveType> leavetypeList = new ArrayList<>();
				int count = 0;
				while (rs.next()) {
					count++;
					LeaveType leaveType = leaveTypeRecords(rs);
					leavetypeList.add(leaveType);
				}
				if (count == 0) {
					response.setContentType("text/plain");
					response.getWriter().write("No Leave Balance Details Available");
					return;
				}
				jsonString = objectMapper.writeValueAsString(leavetypeList);
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

	LeaveType leaveTypeRecords(ResultSet rs) throws SQLException {
		LeaveType leaveType = new LeaveType();

		leaveType.setId(rs.getLong("id"));
		leaveType.setLeaveType(rs.getString("leave_type"));
		leaveType.setCreatedAt(rs.getString("created_at"));
		leaveType.setCreatedBy(rs.getString("created_by"));
		leaveType.setUpdatedBy(rs.getString("created_by"));
		leaveType.setUpdatedAt(rs.getString("updated_at"));
		return leaveType;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveTypeRequest leaveTypeRequest = objectMapper.readValue(request.getReader(), LeaveTypeRequest.class);

		String leaveType = leaveTypeRequest.getLeaveType();
		String createdBy = "Admin";
		String createdAt = new Date().toString();
		String updatedBy = "Admin";
		String updatedAt = new Date().toString();

		connection = utility.establishDbConnection();
		try {
			String query = "select * from leave_type where leave_type=?";
			ps = connection.prepareStatement(query);
			ps.setString(1, leaveType);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				response.setContentType("text/plain");
				response.getWriter().write(leaveType + " Already Added ");
				return;
			}
			String addLeaveTypeQuery = "INSERT INTO leave_type (Leave_Type, Created_By, Created_At, Updated_By, Updated_At) VALUES (?,?,?,?,?)";

			// Create a prepared statement object for executing SQL queries
			ps = connection.prepareStatement(addLeaveTypeQuery);
			log.info("Created a prepared statement object for executing SQL queries");

			ps.setString(1, leaveType);
			ps.setString(2, createdBy);
			ps.setString(3, createdAt);
			ps.setString(4, updatedBy);
			ps.setString(5, updatedAt);

			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				response.setContentType("text/plain");
				response.getWriter().write("Leave type Added Successfully");
				return;

			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Failed to Add Leave Type");
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
		log.info("inside doPut method");

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveTypeRequest leaveTypeRequest = objectMapper.readValue(request.getReader(), LeaveTypeRequest.class);

		try {
			connection = utility.establishDbConnection();
			String leavetype = leaveTypeRequest.getLeaveType();

			String query = "select * from leave_type where leave_type=?";
			ps = connection.prepareStatement(query);
			ps.setString(1, leavetype);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				response.setContentType("text/plain");
				response.getWriter().write(leavetype + " Already Added ");
				return;
			}

			String query1 = "select * from leave_type where id=?";
			Long Id = leaveTypeRequest.getId();
			ps = connection.prepareStatement(query1);
			ps.setLong(1, Id);
			ResultSet leavetypeRS = ps.executeQuery();
			LeaveTypeServlet leaveTypeServlet = new LeaveTypeServlet();
			if (leavetypeRS.next()) {
				LeaveType leaveType = leaveTypeServlet.leaveTypeRecords(leavetypeRS);

				Long id = leaveTypeRequest.getId() != null ? leaveTypeRequest.getId() : leaveType.getId();
				String leavetypes = leaveTypeRequest.getLeaveType();
				String updatedBy = "Admin";
				String updatedAt = new Date().toString();
				String updateLeaveTypeQuery = "UPDATE leave_type SET leave_type=?, updated_by=?,updated_at=? WHERE id=?";

				// Create a prepared statement object for executing SQL queries
				ps = connection.prepareStatement(updateLeaveTypeQuery);
				log.info("Created a prepared statement object for executing SQL queries");

				ps.setString(1, leavetypes);
				ps.setString(2, updatedBy);
				ps.setString(3, updatedAt);
				ps.setLong(4, id);

				// Execute the SQL query
				int num = ps.executeUpdate();

				if (num > 0) {
					response.setContentType("text/plain");
					response.getWriter().write("Leave type Updated Successfully");
					return;
				} else {
					response.setContentType("text/plain");
					response.getWriter().write("Failed to Update Leave type");
					return;
				}
			} else {
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
		log.info("inside doDelete method");

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveTypeRequest leaveTypeRequest = objectMapper.readValue(request.getReader(), LeaveTypeRequest.class);

		Long id = leaveTypeRequest.getId();

		try {
			connection = utility.establishDbConnection();
			log.info("Database connection established successfully!");

			String deleteLeaveTypeQuery = "DELETE FROM leave_type WHERE id=?";

			// Create a prepared statement object for executing SQL queries
			ps = connection.prepareStatement(deleteLeaveTypeQuery);
			ps.setLong(1, id);

			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				response.setContentType("text/plain");
				response.getWriter().write("Leave Type deleted successfully");
				return;
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Failed to delete leave Type");
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
}
