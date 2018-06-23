package khasoane;


public class Recipient {

	private String phoneNumber;
	private String firstName;
	private String lastName;
	
	public String getPhoneNumber() {
		return phoneNumber;
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
	public String getFullName() {
		String res = firstName;
		if(lastName != null) {
			res = firstName + lastName;
		}
		return res;
	}
	
	public boolean hasPhoneNumber(){
		return phoneNumber != null || !phoneNumber.trim().isEmpty();
	}
	
	@Override
	public String toString() {
		return phoneNumber;
	}
}
