package khasoane;


import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Message {

	@Id @GeneratedValue
	private long id;
	private LocalDateTime date;
	private int recipientCount;
	private String message;
	private String description;
	@Transient
	private List<Recipient> recipients;
	@ManyToOne
	private User user;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Recipient> getRecipients() {
		return recipients;
	}
	public void setRecipients(List<Recipient> recipients) {
		this.recipients = recipients;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getRecipientCount() {
		return recipientCount;
	}
	public void setRecipientCount(int recipientCount) {
		this.recipientCount = recipientCount;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
