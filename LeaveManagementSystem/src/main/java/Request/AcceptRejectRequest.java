package Request;

public class AcceptRejectRequest {

	private Long id;
	private boolean accept;

	public AcceptRejectRequest() {
		super();
	}

	public AcceptRejectRequest(Long id, boolean accept) {
		super();
		this.id = id;
		this.accept = accept;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

}
