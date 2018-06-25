package khasoane;


import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.github.kevinsawicki.http.HttpRequest;

@Path("sms")
public class Server {

	protected final Logger logger = LogManager.getLogger(this.getClass());
	
	String sender = "Khasoane";
	
	@POST @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response send(Message msg){
		String url = "http://api.rmlconnect.net"
				+ "/bulksms/bulksms?username=m2camp"
				+ "&password=njm9tgy7"
				+ "&type=0&dlr=1"
				+ "&source="+sender;
		for(Recipient re: msg.getRecipients()){
			Map<String, String> data = new HashMap<String, String>();
			if(re.getPhoneNumber() != null){
				String message = msg.getMessage();
				message = addPlaceholders(re, message);
				data.put("destination", re.getPhoneNumber());
				data.put("message", message);
				int code = HttpRequest.post(url).form(data).code();
				System.out.println("Code: "+code);
//				System.out.println(re +"-> "+ message);
			}
		}
		return Response.ok().build();
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
