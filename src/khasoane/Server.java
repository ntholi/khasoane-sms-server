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

@Path("sms")
public class Server {

	protected final Logger logger = LogManager.getLogger(this.getClass());
	
	String sender = "Khasoane";
	
	@POST @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response send(Message msg){
		String url = "http://smsplus1.routesms.com:8080"
				+ "/bulksms/bulksms?username=m2camp"
				+ "&password=njm9tgy7"
				+ "&type=0&dlr=1"
				+ "&source="+sender;
		for(Recipient r: msg.getRecipients()){
			Map<String, String> data = new HashMap<String, String>();
			if(r.getPhoneNumber() != null){
				String message = msg.getMessage();
				message = message.replace("{firstName}", r.getFirstName());
				message = message.replace("{lastName}", r.getLastName());
				data.put("destination", r.getPhoneNumber());
				data.put("message", message);
//				int code = HttpRequest.post(url).form(data).code();
//				System.out.println("Code: "+code);
				System.out.println(r +"-> "+ message);
			}
		}
		return Response.ok().build();
	}
}
