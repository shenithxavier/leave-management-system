package model;

public class LeaveBalance {
	private Long id;
	private String employeeId;
	private Long leaveType;
	private Long numberOfLeave;
	private String createdBy;
	private String createdAt;
	private String updatedBy;
	private String updatedAt;

	public LeaveBalance() {
		super();
	}

	public LeaveBalance(Long id, String employeeId, Long leaveType, Long numberOfLeave, String createdBy,
			String createdAt, String updatedBy, String updatedAt, String action) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.leaveType = leaveType;
		this.numberOfLeave = numberOfLeave;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.updatedBy = updatedBy;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Long getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Long leaveType) {
		this.leaveType = leaveType;
	}

	public Long getNumberOfLeave() {
		return numberOfLeave;
	}

	public void setNumberOfLeave(Long numberOfLeave) {
		this.numberOfLeave = numberOfLeave;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
}
