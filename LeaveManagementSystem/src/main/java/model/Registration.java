package model;

public class Registration {
	private Long id;
	private String userName;
	private String password;
	private String address;
	private String city;
	private String pin;
	private String contactNumber;
	private String email;

	public Registration() {
		super();
	}

	public Registration(Long id, String userName, String password, String address, String city, String pin,
			String contactNumber, String email) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.address = address;
		this.city = city;
		this.pin = pin;
		this.contactNumber = contactNumber;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
