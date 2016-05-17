package com.ilabs.utterance;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/capture")
public class RESTApiService {

	@POST
	@Path("/requestData")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response crunchifyREST(InputStream incomingData) {
		StringBuilder outputBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				outputBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		//System.out.println("Data Received: " + outputBuilder.toString());
		String name,start_time,end_time,max_utt,max_utt_per_day,dnis,ani,logtag,grammar,utterance,devlog;
		String out[] = outputBuilder.toString().split("&");
		name = out[0].substring(out[0].indexOf('=')+1, out[0].length());
		start_time = out[1].substring(out[1].indexOf('=')+1, out[1].length());
		end_time = out[2].substring(out[2].indexOf('=')+1, out[2].length());
		max_utt = out[3].substring(out[3].indexOf('=')+1, out[3].length());
		max_utt_per_day = out[4].substring(out[4].indexOf('=')+1, out[4].length());
		dnis = out[5].substring(out[5].indexOf('=')+1, out[5].length());
		ani = out[6].substring(out[6].indexOf('=')+1, out[6].length());
		logtag = out[7].substring(out[7].indexOf('=')+1, out[7].length());
		grammar = out[8].substring(out[8].indexOf('=')+1, out[8].length());
		utterance = out[9].substring(out[9].indexOf('=')+1, out[9].length());
		devlog = out[10].substring(out[10].indexOf('=')+1, out[10].length());
		System.out.println(name+" "+start_time+" "+end_time+" "+max_utt+" "+max_utt_per_day+" "+dnis+" "+ani+" "+logtag+" "+grammar+" "+utterance+" "+devlog);
		
		if(grammar.equals("true"))
		MySqlQueries.call();
		if(utterance.equals("true"))
		JSONParser.call();
		// return HTTP response 200 in case of success
		return Response.status(200).entity(outputBuilder.toString()).build();
	}
 
}
