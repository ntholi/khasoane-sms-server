package khasoane;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.kevinsawicki.http.HttpRequest;

import khasoane.datasource.DAO;

@Path("sms")
public class Server {

	protected final Logger logger = LogManager.getLogger(this.getClass());
	
	String sender = "Khasoane";
	
	@POST @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response send(Message msg){
		Response respose = null;
		String url = "http://api.rmlconnect.net"
				+ "/bulksms/bulksms?username=m2camp"
				+ "&password=njm9tgy7"
				+ "&type=0&dlr=1"
				+ "&source="+sender;
		List<Recipient> res = msg.getRecipients();
		msg.setDate(LocalDateTime.now());
		int count = 0;
		try {
			for (; count < res.size(); count++) {
				Recipient re = res.get(count);
				Map<String, String> params = new HashMap<String, String>();
				if (re.getPhoneNumber() != null) {
					String message = msg.getMessage();
					message = addPlaceholders(re, message);
					params.put("destination", re.getPhoneNumber());
					params.put("message", message);
					HttpRequest request = HttpRequest.post(url, params, true);
//					System.out.println("Code: " + request.code());
//					System.out.println("Body: " + request.body());
//					System.out.println("Request: " + request.url());
				}
			}
			respose = Response.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			respose = Response.status(500).entity(e.getMessage()).build();
		}
		finally {
			msg.setRecipientCount(count);
			try {
				new DAO<>(Message.class).save(msg);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return respose;
	}

	private String addPlaceholders(Recipient re, String msg) {
		String firstName = re.getFirstName();
		String lastName = re.getLastName();
		if(firstName != null && !firstName.trim().isEmpty()) {
			msg = msg.replace("{firstName}", firstName);
		}
		else {
			msg = msg.replace("{firstName}", "");
		}
		if(lastName != null && !lastName.trim().isEmpty()) {
			msg = msg.replace("{lastName}", lastName);
		}
		else {
			msg = msg.replace("{lastName}", "");
		}
		return msg;
	}
}
