package khasoane;


import java.net.UnknownHostException;
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
	String sender = "KhasoaneFX".toUpperCase();
	
	@POST @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response send(Message msg){
		int nCredits = getCredits();
		if(nCredits < msg.getRecipients().size()) {
			return Response.status(401).entity("Insufficient credits, trying to send message to "
					+ msg.getRecipients().size() + " recipient(s)\nBalance is "+ nCredits)
					.build();
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
//					System.out.println(request.body());
					System.out.println("-> "+ message);
				}
			}
			respose = Response.ok().build();
		}
		catch (HttpRequest.HttpRequestException e) {
			respose = Response.status(500)
					.entity("Unable to connect to external SMS server, "
							+ "please check your Internet connection")
					.build();
		}
		catch (Exception e) {
			respose = Response.status(500).entity(e.getMessage())
					.build();
			e.printStackTrace();
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
			System.err.println(e);
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
			msg = msg.replace("{firstname}", firstName);
		}
		else {
			msg = msg.replace("{firstName}", "");
			msg = msg.replace("{firstname}", "");
		}
		if(lastName != null && !lastName.trim().isEmpty()) {
			msg = msg.replace("{lastName}", lastName);
			msg = msg.replace("{lastname}", lastName);
			msg = msg.replace("{surname}", lastName);
		}
		else {
			msg = msg.replace("{lastName}", "");
			msg = msg.replace("{lastname}", "");
			msg = msg.replace("{surname}", "");
		}
		int i1 = msg.indexOf('{');
		int i2 = msg.indexOf('}');
		if(i1 != -1 && i2 != -1) {
			String error = msg.substring(i1, i2+1);
			msg = msg.replace(error, "");
		}
		return msg;
	}
}
