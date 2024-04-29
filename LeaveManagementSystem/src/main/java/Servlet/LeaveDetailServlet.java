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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Utility;
import Request.LeaveDetailsRequest;

public class LeaveDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	Connection connection = null;
	PreparedStatement ps = null;
	Utility utility = new Utility();

	public LeaveDetailServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveDetailsRequest leaveDetailsRequest = objectMapper.readValue(request.getReader(),
				LeaveDetailsRequest.class);

		try {
			String action = leaveDetailsRequest.getAction();
			Long id = leaveDetailsRequest.getId();
			connection = utility.establishDbConnection();
			String getLeaveDetailsQuery = null;
			String jsonString = null;

			if ("getById".equals(action)) {
				getLeaveDetailsQuery = " select * from leave_details where Id=?";

				// Create a prepared statement object for executing SQL queries
				ps = connection.prepareStatement(getLeaveDetailsQuery);
				log.info("Created a prepared statement object for executing SQL queries");
				ps.setLong(1, id);
				ResultSet leaveDetailsRS = ps.executeQuery();

				if (leaveDetailsRS.next()) {
					LeaveDetails leaveDeatails = getLeaveDetails(leaveDetailsRS);
					jsonString = objectMapper.writeValueAsString(leaveDeatails);
				}else {
					response.setContentType("text/plain");
					response.getWriter().write("Invalid Id");
					return;
				}
			} else {
				getLeaveDetailsQuery = " select * from leave_details";
				ps = connection.prepareStatement(getLeaveDetailsQuery);

				// Execute the SQL query
				ResultSet leaveDetailsRS = ps.executeQuery();
				log.info("Executed the SQL query");
				List<LeaveDetails> leaveDetailsList = new ArrayList<>();

				int count = 0;
				while (leaveDetailsRS.next()) {
					count++;
					LeaveDetails leaveDeatils = getLeaveDetails(leaveDetailsRS);
					leaveDetailsList.add(leaveDeatils);
					jsonString = objectMapper.writeValueAsString(leaveDetailsList);
				}
				if (count == 0) {
					response.setContentType("text/plain");
					response.getWriter().write("No Leave Details Details Available");
					return;
				}
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

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveDetailsRequest leaveDetailsRequest = objectMapper.readValue(request.getReader(),
				LeaveDetailsRequest.class);

		try {
			connection = utility.establishDbConnection();

			String query = "select * from leave_details where id=?";
			Long id = leaveDetailsRequest.getId();
			ps = connection.prepareStatement(query);
			ps.setLong(1, id);
			ResultSet leavedetailsRS = ps.executeQuery();
			LeaveDetailServlet leaveDetailServlet = new LeaveDetailServlet();
			if (leavedetailsRS.next()) {
				LeaveDetails leaveDetails = leaveDetailServlet.getLeaveDetails(leavedetailsRS);
				long Id = leaveDetailsRequest.getId() != null ? leaveDetailsRequest.getId() : leaveDetails.getId();
				String employeeID = leaveDetailsRequest.getEmployeeId() != null ? leaveDetailsRequest.getEmployeeId()
						: leaveDetails.getEmployeeId();
				Date fromdate = leaveDetailsRequest.getFromDate() != null ? leaveDetailsRequest.getFromDate()
						: leaveDetails.getFromDate();
				Date todate = leaveDetailsRequest.getToDate() != null ? leaveDetailsRequest.getToDate()
						: leaveDetails.getToDate();
				Long leavetype = leaveDetailsRequest.getLeaveType() != null ? leaveDetailsRequest.getLeaveType()
						: leaveDetails.getLeaveType();
				Long managerid = leaveDetailsRequest.getManagerId() != null ? leaveDetailsRequest.getManagerId()
						: leaveDetails.getManagerId();
				String Reason = leaveDetailsRequest.getReason() != null ? leaveDetailsRequest.getReason()
						: leaveDetails.getReason();
				Long numofdays = leaveDetailsRequest.getNumberOfDays() != null ? leaveDetailsRequest.getNumberOfDays()
						: leaveDetails.getNumberOfDays();
				String updatedby = "Admin";
				String updatedat = new Date().toString();

				String updateLeaveDetailsQuery = "UPDATE leave_details SET from_date=?,to_date=?,leave_type_id=?,manager_id=?,reason=?,number_of_days=?, updated_by=?, updated_at=? where employee_id=? and id=?";

				// Create a prepared statement object for executing SQL queries
				ps = connection.prepareStatement(updateLeaveDetailsQuery);
				log.info("Created a prepared statement object for executing SQL queries");

				java.sql.Date FromDate = new java.sql.Date(fromdate.getTime());
				java.sql.Date ToDate = new java.sql.Date(todate.getTime());

				ps.setDate(1, FromDate);
				ps.setDate(2, ToDate);
				ps.setLong(3, leavetype);
				ps.setLong(4, managerid);
				ps.setString(5, Reason);
				ps.setLong(6, numofdays);
				ps.setString(7, updatedby);
				ps.setString(8, updatedat);
				ps.setString(9, employeeID);
				ps.setLong(10, Id);

				// Execute the SQL query
				int num = ps.executeUpdate();

				if (num > 0) {
					response.setContentType("text/plain");
					response.getWriter().write("Leave Details updated successfully");
					return;
				} else {
					response.setContentType("text/plain");
					response.getWriter().write("Failed to update Leave Details");
					return;
				}
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
		LeaveDetailsRequest leaveDetailsRequest = objectMapper.readValue(request.getReader(),
				LeaveDetailsRequest.class);

		Long id = leaveDetailsRequest.getId();

		try {
			connection = utility.establishDbConnection();

			String deleteLeaveDetailsQuery = "DELETE FROM leave_details WHERE id=?";

			// Create a prepared statement object for executing SQL queries
			ps = connection.prepareStatement(deleteLeaveDetailsQuery);
			log.info("Created a prepared statement object for executing SQL queries");

			ps.setLong(1, id);

			// Execute the SQL query
			int num = ps.executeUpdate();

			if (num > 0) {
				response.setContentType("text/plain");
				response.getWriter().write("Leave details Deleted successfully");
				return;
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Failed to Delete Leave details");
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		LeaveDetailsRequest leaveDetailsRequest = objectMapper.readValue(request.getReader(),
				LeaveDetailsRequest.class);
		String empId = leaveDetailsRequest.getEmployeeId();

		connection = utility.establishDbConnection();
		String employeequery = "select * from employeedetails where Employee_ID = ?";
		EmployeeDetails employeeDetails = null;
		try {
			ps = connection.prepareStatement(employeequery);
			ps.setString(1, empId);
			ResultSet employeeDetailsRS = ps.executeQuery();
			if (employeeDetailsRS.next()) {
				employeeDetails = getEmployeeDetails(employeeDetailsRS);
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Invalid Employee Id");
				return;
			}

			Long id = leaveDetailsRequest.getLeaveType();
			String leaveTypeQuery = "select * from leave_type where id = ?";
			ps = connection.prepareStatement(leaveTypeQuery);
			ps.setLong(1, id);
			ResultSet leaveTypeRS = ps.executeQuery();
			if (leaveTypeRS.next()) {
				log.info(leaveTypeRS.getString("leave_type"));
			}
			String s = "Casual Leave";
			if (s.equals(leaveTypeRS.getString("leave_type"))) {
				if (leaveDetailsRequest.getNumberOfDays() > 1) {
					response.setContentType("text/plain");
					response.getWriter().write("More than one casual leave is not applicable");
					return;
				}
				Calendar cal = Calendar.getInstance();
				cal.setTime(leaveDetailsRequest.getFromDate());
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
				java.sql.Date firstDay = new java.sql.Date(cal.getTimeInMillis()); // Proper conversion
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				java.sql.Date lastDay = new java.sql.Date(cal.getTimeInMillis()); // Proper conversion
				Long type = leaveDetailsRequest.getLeaveType();
				String query = "SELECT * FROM leave_details " + "WHERE employee_id = ? " + "AND leave_type_id = ? "
						+ "AND from_date >= ? " + "AND to_date <= ?";
				ps = connection.prepareStatement(query);
				ps.setString(1, employeeDetails.getEmployeeId());
				ps.setLong(2, type);
				ps.setDate(3, firstDay);
				ps.setDate(4, lastDay);
				ResultSet leaveDetailsRS = ps.executeQuery();
				if (leaveDetailsRS.next()) {

					response.setContentType("text/plain");
					response.getWriter().write("Casual Leave Already Taken");
					return;
				}
				log.info("Casual Leave for this month is Available");
			}
			String query = "SELECT * FROM leavebalance " + "WHERE employeeId = ? " + "AND leaveType = ? ";
			ps = connection.prepareStatement(query);
			ps.setString(1, employeeDetails.getEmployeeId());
			ps.setLong(2, leaveDetailsRequest.getLeaveType());
			ResultSet leaveBalanceRS = ps.executeQuery();
			if (leaveBalanceRS.next()) {
				if (leaveBalanceRS.getLong("numberOfLeave") >= leaveDetailsRequest.getNumberOfDays()
						&& leaveDetailsRequest.getNumberOfDays() < 3) {

					String query1 = "insert into leave_details(employee_id,from_date,to_date,leave_type_id,manager_id,reason,number_of_days,created_by,created_at,updated_by,updated_at)values(?,?,?,?,?,?,?,?,?,?,?)";
					ps = connection.prepareStatement(query1);
					ps.setString(1, leaveDetailsRequest.getEmployeeId());
					ps.setDate(2, new java.sql.Date(leaveDetailsRequest.getFromDate().getTime()));
					ps.setDate(3, new java.sql.Date(leaveDetailsRequest.getToDate().getTime()));
					ps.setLong(4, leaveDetailsRequest.getLeaveType());
					ps.setLong(5, leaveDetailsRequest.getManagerId());
					ps.setString(6, leaveDetailsRequest.getReason());
					ps.setLong(7, leaveDetailsRequest.getNumberOfDays());
					ps.setString(8, "Admin");
					ps.setString(9, new Date().toString());
					ps.setString(10, "Admin");
					ps.setString(11, new Date().toString());

					int i = ps.executeUpdate();
					if (i > 0) {
						response.setContentType("text/plain");
						response.getWriter().write("Leave Details Added Successfully");
						return;
					} else {
						response.setContentType("text/plain");
						response.getWriter().write("Leave Details Not Added ");
						return;
					}

				} else {
					response.setContentType("text/plain");
					response.getWriter().write("Leave Not Available or Cannot Apply For 3 consecutive Leave");
					return;
				}
			} else {
				response.setContentType("text/plain");
				response.getWriter().write("Leave Not Available");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	EmployeeDetails getEmployeeDetails(ResultSet resultSet) {
		log.info("inside employee details");
		EmployeeDetails employeeDetails = new EmployeeDetails();
		try {
			employeeDetails.setId(resultSet.getLong("id"));
			employeeDetails.setEmployeeId(resultSet.getString("employee_Id"));
			employeeDetails.setEmployeeName(resultSet.getString("Employee_Name"));
			employeeDetails.setDesignation(resultSet.getString("Designation"));
			employeeDetails.setDob(resultSet.getDate("Dob"));
			log.info("datebirth " + resultSet.getDate("Dob"));
			employeeDetails.setEmail(resultSet.getString("Email"));
			employeeDetails.setDoj(resultSet.getDate("Doj"));
			employeeDetails.setAddress(resultSet.getString("Address"));
			employeeDetails.setContactNumber(resultSet.getLong("Contact_Number"));
			employeeDetails.setStatus(resultSet.getString("Status"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeeDetails;
	}

	LeaveDetails getLeaveDetails(ResultSet resultSet) {
		LeaveDetails leaveDetails = new LeaveDetails();
		try {
			leaveDetails.setId(resultSet.getLong("id"));
			leaveDetails.setEmployeeId(resultSet.getString("employee_id"));
			leaveDetails.setFromDate(resultSet.getDate("from_date"));
			leaveDetails.setToDate(resultSet.getDate("to_date"));
			leaveDetails.setLeaveType(resultSet.getLong("leave_type_id"));
			leaveDetails.setManagerId(resultSet.getLong("manager_id"));
			leaveDetails.setReason(resultSet.getString("reason"));
			leaveDetails.setNumberOfDays(resultSet.getLong("number_of_days"));
			leaveDetails.setCreatedAt(resultSet.getString("created_at"));
			leaveDetails.setCreatedBy(resultSet.getString("created_by"));
			leaveDetails.setUpdatedAt(resultSet.getString("updated_at"));
			leaveDetails.setUpdatedBy(resultSet.getString("updated_by"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return leaveDetails;

	}
}