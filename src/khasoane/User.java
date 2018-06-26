package khasoane;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class User {

	public enum Permissions{
		Read, Write, Update, Delete;
	}

	@Id @GeneratedValue
	private Long id;
	private String firstName;
	private String lastName;
	@Enumerated
	private Permissions permissions;
	@Column(unique=true, nullable=false)
	private String username;
	@Column(unique=true, nullable=false)
	private String password;
	private boolean isAdmin;
	private boolean hasOtp;
	private boolean makePayments;
	private boolean viewReports;
	private boolean sendSMS;

	
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	public User(){
	}
	
	public Long getId(){
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	@JsonProperty("isAdmin")
	public boolean isAdmin() {
		return isAdmin;
	}
	
	public boolean getIsAdmin() {
		return isAdmin;
	}
	
	@JsonProperty("isAdmin")
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public Permissions getPermissions() {
		return permissions;
	}
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
	public boolean hasOtp() {
		return hasOtp;
	}
	public boolean isHasOtp() {
		return hasOtp;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setHasOtp(boolean hasOtp) {
		this.hasOtp = hasOtp;
	}

	public boolean getMakePayments() {
		return makePayments || isAdmin;
	}

	public void setMakePayments(boolean canMakePayments) {
		this.makePayments = canMakePayments;
	}

	public boolean getViewReports() {
		return viewReports || isAdmin;
	}

	public void setViewReports(boolean canViewReports) {
		this.viewReports = canViewReports;
	}

	public boolean canSendSMS() {
		return sendSMS;
	}
	
	public boolean getSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(boolean canSendSMS) {
		this.sendSMS = canSendSMS;
	}
}
