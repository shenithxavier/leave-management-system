package Request;

public class StatusRequest {

	private String employeeId;
	private String status;

	public StatusRequest() {
		super();
	}

	public StatusRequest(String employeeId, String status) {
		super();
		this.employeeId = employeeId;
		this.status = status;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
