package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import Common.Utility;
import Request.AcceptRejectRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AcceptRejectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	Connection connection = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	
	Utility utility= new Utility();

	public AcceptRejectServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		AcceptRejectRequest acceptRejectRequest = objectMapper.readValue(request.getReader(),
				AcceptRejectRequest.class);

		connection=utility.establishDbConnection();
		
		boolean accept = acceptRejectRequest.isAccept();
		if (accept) {
			Long id = acceptRejectRequest.getId();
			log.info(id);
			String query = "select * from leave_details where id= ?";
			Utility utility = new Utility();
			connection=utility.establishDbConnection();
			try {
				ps = connection.prepareStatement(query);
				ps.setLong(1, id);
				rs = ps.executeQuery();
				if (rs.next()) {
				}
				Long numberofdays = rs.getLong("number_of_days");
				String status = "accepted";
				Long ID = rs.getLong("id");
				
				String query1 = "select * from leavebalance where employeeId = ? AND leaveType = ?";
				ps = connection.prepareStatement(query1);
				ps.setString(1, rs.getString("employee_id"));
				ps.setLong(2, rs.getLong("leave_type_id"));
				rs = ps.executeQuery();
				if (rs.next()) {

					Long numOfLeave = rs.getLong("numberOfLeave") - numberofdays;
					Long Id = rs.getLong("id");
					if (rs.getLong("numberOfLeave") >= numberofdays) {
						String updateQuery = "UPDATE leavebalance SET numberOfLeave =?  where  id = ?";

						ps = connection.prepareStatement(updateQuery);
						ps.setLong(1, numOfLeave);
						ps.setLong(2, Id);
						int i = ps.executeUpdate();
						if (i > 0) {

							String updatequery = "update leave_details set status=? where id=?";
							ps = connection.prepareStatement(updatequery);
							ps.setString(1, status);
							ps.setLong(2, ID);
							int j = ps.executeUpdate();
							if (j > 0) {
							}
							response.setContentType("text/plain");
							response.getWriter().write("Approved");
							return;
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Long id = acceptRejectRequest.getId();
		String query = "select * from leave_details where id= ?";
		try {
			
			ps = connection.prepareStatement(query);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
			}
			String status = "rejected";
			Long Id = rs.getLong("id");
			String updatequery = "update leave_details set status=? where id=?";
			ps = connection.prepareStatement(updatequery);
			ps.setString(1, status);
			ps.setLong(2, Id);
			int i = ps.executeUpdate();
			if (i > 0) {

			}
			response.setContentType("text/plain");
			response.getWriter().write("rejected");
			return;

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
