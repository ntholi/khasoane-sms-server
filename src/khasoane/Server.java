package khasoane;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;

import khasoane.credits.Credits;
import khasoane.datasource.DAO;

@Path("sms")
public class Server {

	protected final Logger logger = LogManager.getLogger(this.getClass());
	private DAO<Credits> creditsDAO = new DAO<>(Credits.class);
	String sender = "KhasoaneFX";
	
	@POST @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response send(Message msg){
		int nCredits = getCredits();
		if(nCredits < msg.getRecipients().size()) {
			Response.status(401, "Insufficient credits, you want to send a message to "
					+ msg.getRecipients().size() + " recipients, your balance is "+ nCredits);
		}
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
//					HttpRequest request = HttpRequest.post(url, params, true);
//					request.code();
				}
			}
			respose = Response.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			respose = Response.status(500).entity(e.getMessage()).build();
		}
		finally {
			msg.setRecipientCount(count);
			Credits credits = creditsDAO.get(clientId());
			credits.deductCredits(count);
			creditsDAO.save(credits);
			try {
				new DAO<>(Message.class).save(msg);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return respose;
	}

	@GET @Path("/credits")
	public int getCredits() {
		
		Credits credits = creditsDAO.get(clientId());
		try {
			HttpRequest request = HttpRequest.get("https://breakoutms-web-credits.firebaseapp.com/credits.json");
			String json = request.body();
			ObjectMapper mapper = new ObjectMapper();
			Credits[] list = mapper.readValue(json, Credits[].class);
			for(Credits c: list) {
				if(c.getClientId() == clientId()) {
					if(credits == null) {
						credits = c;
					}
					else if(c.getTimestamp() > credits.getTimestamp()) {
						credits.addCredits(c.getCredits());
						credits.setTimestamp(c.getTimestamp());
					}
					creditsDAO.save(credits);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return credits.getCredits();
	}

	private int clientId() {
		return 1;
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
