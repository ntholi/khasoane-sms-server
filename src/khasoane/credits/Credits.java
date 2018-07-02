package khasoane.credits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Credits {
	@Id
	private int clientId;
	private long timestamp;
	@Column(unique=true, nullable=false)
	private String clientName;
	private int credits;
	
	
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	
	public void addCredits(int credits) {
		this.credits += credits;
	}
	
	public void deductCredits(int credits) {
		this.credits -= credits;
	}
}
