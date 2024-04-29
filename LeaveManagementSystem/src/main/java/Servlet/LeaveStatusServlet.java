package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Utility;
import Request.StatusRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LeaveDetails;

public class LeaveStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	Connection connection = null;
	PreparedStatement ps = null;

	public LeaveStatusServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		StatusRequest statusRequest = objectMapper.readValue(request.getReader(), StatusRequest.class);

		String query = "select * from employeedetails where Employee_ID=?";
		String employeeid = statusRequest.getEmployeeId();

		try {
			Utility utility=new Utility();

			connection = utility.establishDbConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, employeeid);
			ResultSet employeeDetailsRs = ps.executeQuery();

			String employeeId = statusRequest.getEmployeeId();
			String status = statusRequest.getStatus();
			if (employeeDetailsRs.next() && status != null) {
				String query1 = "SELECT * FROM leave_details WHERE employee_id=? AND status=?";

				ps = connection.prepareStatement(query1);
				ps.setString(1, employeeId);
				ps.setString(2, status);
				ResultSet leaveDetailsRS = ps.executeQuery();

				List<LeaveDetails> leaveDetailsList = new ArrayList<>();
				while (leaveDetailsRS.next()) {
					LeaveDetails leaveDeatils = new LeaveDetails();
					leaveDeatils.setEmployeeId(leaveDetailsRS.getString("employee_id"));
					leaveDeatils.setId(leaveDetailsRS.getLong("id"));
					leaveDeatils.setFromDate(leaveDetailsRS.getDate("from_date"));
					leaveDeatils.setToDate(leaveDetailsRS.getDate("to_date"));
					leaveDeatils.setLeaveType(leaveDetailsRS.getLong("leave_type_id"));
					leaveDeatils.setManagerId(leaveDetailsRS.getLong("manager_id"));
					leaveDeatils.setReason(leaveDetailsRS.getString("reason"));
					leaveDeatils.setNumberOfDays(leaveDetailsRS.getLong("number_of_days"));
					leaveDetailsList.add(leaveDeatils);
				}
				log.info(leaveDetailsList.size());
				String jsonString = objectMapper.writeValueAsString(leaveDetailsList);
				response.setContentType("text/plain");
				response.getWriter().write(jsonString);
				return;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}