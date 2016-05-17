package com.ilabs.utterance;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class GenerateXML {

	public boolean writexmlcalllogs(String uuid,Chart  data,int v,String day)
	{
			
			LinkedList<Table> found_uuids = new LinkedList<Table>();
			LinkedList<Table> utts = new LinkedList<Table>();
			LinkedList<Table> transfers = new LinkedList<Table>();
			LinkedList<Table> attrib = new LinkedList<Table>();
			LinkedList<Table> grammar = new LinkedList<Table>();
			LinkedList<Table> result = new LinkedList<Table>();
			LinkedList<Table> slot = new LinkedList<Table>();
			LinkedList<Table> logtag = new LinkedList<Table>();
			
			
			
			found_uuids = data.tables.get(0);
			utts = data.tables.get(1);
			transfers = data.tables.get(2);
			attrib = data.tables.get(3);
			grammar = data.tables.get(4);
			result = data.tables.get(5);
			slot = data.tables.get(6);
			logtag = data.tables.get(7);
			
			
			//uuid is unique , multiple ocurrences are not there , so just one table will be present
			
			Table uuid_data = found_uuids.get(0);
			
			
			try {
				
				PrintWriter writer = new PrintWriter("calllogs_"+day+"/calllogs_"+uuid+".xml", "UTF-8");
				
				writer.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"yes\" ?>");
				writer.println("<log calluuid="+uuid+" ani="+ uuid_data.colmns.get(uuid_data.col_name.indexOf("ani"))+" ani2="+ 
								uuid_data.colmns.get(uuid_data.col_name.indexOf("ani2"))+
								" did="+ uuid_data.colmns.get(uuid_data.col_name.indexOf("did"))+
								" sip="+ uuid_data.colmns.get(uuid_data.col_name.indexOf("sip"))+
								" privacy="+ uuid_data.colmns.get(uuid_data.col_name.indexOf("privacy"))+
								" open_time="+ uuid_data.colmns.get(uuid_data.col_name.indexOf("open_time"))
								+" host="+ uuid_data.colmns.get(uuid_data.col_name.indexOf("host"))+
								">");
				if(utts != null)
				{
					
					for(Table utt : utts)
					{
						String utt_tm = utt.colmns.get(utt.col_name.indexOf("utt_tm"));
						String event = utt.colmns.get(utt.col_name.indexOf("event"));
						//System.out.println(uuid+" : "+event);
						if(event.equals("barge_in"))
							writer.println("<barge_in time="+utt_tm+" uttid="+utt.colmns.get(utt.col_name.indexOf("utt_id"))+"/>");
						else {
							String hash = utt.colmns.get(utt.col_name.indexOf("hash"));
							String mrcp_type ="";
							
							if(utt.colmns.get(utt.col_name.indexOf("recoserver"))!=null)
								mrcp_type = " protocol=\"mrcp\"";
							
							if(event.equals("no_speech"))
								writer.println("<no_speech"+mrcp_type+" time="+utt_tm+
											   " uttid="+utt.colmns.get(utt.col_name.indexOf("utt_id"))+
											   " reason="+utt.colmns.get(utt.col_name.indexOf("reason"))+
											   " field="+escapeEntities(utt.colmns.get(utt.col_name.indexOf("field")))+
											   " hash="+hash+"/>");
							else 
							{
								
								writer.println("<utterance_event"+mrcp_type+" time="+utt_tm+
									       " uttid="+utt.colmns.get(utt.col_name.indexOf("utt_id"))+
									       " status="+event+
									       " reason="+utt.colmns.get(utt.col_name.indexOf("reason"))+
									       " field="+escapeEntities(utt.colmns.get(utt.col_name.indexOf("field")))+
									       " hash="+hash+
									       " modelid="+utt.colmns.get(utt.col_name.indexOf("model_id")));
						
						if(utt.colmns.get(utt.col_name.indexOf("recoserver"))!=null)
							writer.println("recoserver="+utt.colmns.get(utt.col_name.indexOf("recoserver")));
						
						String wavfn = null;
						
						if(attrib != null)
						{
							
							for(Table attr : attrib)
							{
								String value = null;
								if(attr.colmns.get(attr.col_name.indexOf("value"))!= null)
								value = attr.colmns.get(attr.col_name.indexOf("value"));
								if(attr.colmns.get(attr.col_name.indexOf("value")) == null)
									value = "";
								if(attr.colmns.get(attr.col_name.indexOf("name")) == null || attr.colmns.get(attr.col_name.indexOf("name")).isEmpty())
									continue;
								if (attr.colmns.get(attr.col_name.indexOf("name")).equals("wave-location")) {
			                        wavfn = value;
			                        continue;
			                    }
								
								writer.println(attr.colmns.get(attr.col_name.indexOf("name"))+"="+escapeEntities(value));
								
							}
						}
						
						if(wavfn != null)
							writer.println("<speech wav=\\"+wavfn+"\"/>");
						
						if(grammar != null)
						{
							
							writer.println("<reco_grammars>");
							for(Table gram : grammar)
							{
							//	System.out.println(gram.col_name.indexOf("hash"));
								if(gram.colmns.get(gram.col_name.indexOf("hash"))!=null)
								writer.println("<reco_grammar hash="+gram.colmns.get(gram.col_name.indexOf("hash")));
								
								if(gram.colmns.get(gram.col_name.indexOf("url")) != null)
									writer.println(". url = "+escapeEntities(gram.colmns.get(gram.col_name.indexOf("url"))));
								if(gram.colmns.get(gram.col_name.indexOf("weight")) != null)
									writer.println(". weight = "+escapeEntities(gram.colmns.get(gram.col_name.indexOf("weight"))));
								if(gram.colmns.get(gram.col_name.indexOf("load_order")) != null)
									writer.println(". load_order = "+escapeEntities(gram.colmns.get(gram.col_name.indexOf("load_order"))));
								if(gram.colmns.get(gram.col_name.indexOf("type")) != null)
									writer.println(". grammar_type = "+escapeEntities(gram.colmns.get(gram.col_name.indexOf("type"))));
								if(gram.colmns.get(gram.col_name.indexOf("inline_grammar")) != null)
									writer.println(". inline_grammar = "+escapeEntities(gram.colmns.get(gram.col_name.indexOf("inline_grammar"))));
								else 
									writer.println("/>");
									
								writer.println("</reco_grammar>");
								
							}
							writer.println("</reco_grammars");
						}
							//get utt reco result information
							String result_word;
							int result_order = 0;
							
							if(result != null)
							{
								
								for(Table res : result)
								{
									result_order++;
									if(res.colmns.get(res.col_name.indexOf("words"))!=null)
									{
										result_word = res.colmns.get(res.col_name.indexOf("words"));
										result_word = result_word.replaceAll("&", "&amp;");
										result_word = result_word.replaceAll("<", "&lt;");
										result_word = result_word.replaceAll(">", "&gt;");
										result_word = result_word.replaceAll("\"", "'");
									}
									else {
										result_word = "no_reco_result";
									}
									String conf = res.colmns.get(res.col_name.indexOf("conf"));
									if(conf == null) conf = "";
									writer.println("            <nl_result result=\""+result_word+"\" confidence=\""+conf+"\""+" order=\""+result_order+"\">");
									
									
									int interpnum = 1;
									for(Table interp : slot)
									{
										String name = interp.colmns.get(interp.col_name.indexOf("name"));
										name = name.replaceAll("&", "&amp;");
										name = name.replaceAll("<", "&lt;");
										name = name.replaceAll(">", "&gt;");
										name = name.replaceAll("\"", "'");
										
										 writer.println("            <nl_interp"+" interp=\""+name+"\""+
												 		" order=\""+interpnum+"\"/>");
				                        interpnum++;

									}
									
									writer.println("        </nl_result>");
								}
							}
							
							
							writer.println("    </utterance_event>");
					
							}	
						}
					}
					
				}
				
				//transfers
				
				if(transfers != null)
				{
					
					Table tran = transfers.get(0);
					
					String start_tm = printableTime(tran.colmns.get(tran.col_name.indexOf("start_tm")));
					String request_tm = printableTime(tran.colmns.get(tran.col_name.indexOf("request_tm")));
					String hangup_tm = printableTime(tran.colmns.get(tran.col_name.indexOf("hangup_tm")));
					
					writer.println("    <transfer_request time="+request_tm+"/>");
					if(start_tm != null)
						writer.println("<conference_started time="+start_tm+" transfer_number="+
										tran.colmns.get(tran.col_name.indexOf("destination_number"))+
										"type ="+tran.colmns.get(tran.col_name.indexOf("type"))+"/>");
					
					String reas = tran.colmns.get(tran.col_name.indexOf("transfer_exit_code"));
					Integer reason = Integer.valueOf(reas);
					if(reason > 100)
					{
						reason = reason  - 100;
						writer.println("    <transfer_failed ");
						if(hangup_tm != null)
							writer.print("time ="+hangup_tm);
						writer.print("transfer_number="+tran.colmns.get(tran.col_name.indexOf("destination_number"))+" fail_reason="+reason+"/>");
						
					}
					else if(!tran.colmns.get(tran.col_name.indexOf("type")).equals("1"))
					{
						writer.println("    <conference_ended ");
						if(hangup_tm != null)
							writer.print("time ="+hangup_tm);
						writer.print("transfer_number="+tran.colmns.get(tran.col_name.indexOf("destination_number"))+" hangup_reason="+reason+"/>");
					}
					
					
				}
				
				//vxml logtags
				
				if(logtag != null)
				{
					
					
					for(Table vxml_log : logtag)
					{
						String event_tm = vxml_log.colmns.get(vxml_log.col_name.indexOf("readable_event_tm"));
						writer.println("    <log_tag time="+event_tm+" label="+vxml_log.colmns.get(vxml_log.col_name.indexOf("log_label")));
						if(vxml_log.colmns.get(vxml_log.col_name.indexOf("log_value")) != null)
						{
							 writer.println("message="+ escapeEntities(vxml_log.colmns.get(vxml_log.col_name.indexOf("log_value")))); 
							 writer.println(uuid+"/>");
						}
					}
				}
				
				
				//hangup time)
				if(!uuid_data.colmns.get(uuid_data.col_name.indexOf("hangup_time")).equals(null))
					writer.println("    <hangup time="+uuid_data.col_name.indexOf("hangup_time")+"/>");
		
				
				writer.println("</log>\n");
		
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		
		
	}
	public static String printableTime(String unixtime)
	{
		String val="";
		String time[] = new String[5];
		StringTokenizer tokens = new StringTokenizer(unixtime,",");
		int i =0;
		while(tokens.hasMoreTokens())
		{
			time[i] = tokens.nextToken();
			//
			i++;
		}
		//System.out.println(time[3]);
		int flip = 1;
		Integer t = Integer.valueOf(time[3]);
		while(t > 30)
		{
			//System.out.println(t);
			if(flip == 1|| flip == 3 || flip == 5 || flip == 7 || flip == 8 || flip == 10 || flip == 12)
			t = t - 31;
			else if(flip == 2 && Integer.valueOf(time[4])%4 == 0)
			t = t - 29;
			else if(flip == 2 && Integer.valueOf(time[4])%4 !=0)
			t = t - 28;
			else
			t = t - 30;
			flip++;
		}
		int month = flip;
		String monthString = new DateFormatSymbols().getMonths()[Integer.valueOf(month)-1];
		//System.out.println(monthString);
		
		String input_date=t+"/"+month+"/"+time[4];
		
		  SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
		  Date dt1;
		try {
			dt1 = format1.parse(input_date);
			DateFormat format2=new SimpleDateFormat("EEEE"); 
			  String finalDay=format2.format(dt1);
			  val = finalDay.substring(0, 3)+" "+ monthString.substring(0, 3) + " "+ t +" "+time[2]+":"+time[1]+":"+time[0]+" "+time[4]+" UTC";
			//  System.out.println(finalDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}
	public static String escapeEntities(String s)
	{
		String new_s;
		new_s = s.replaceAll("&", "&amp;");
		new_s = new_s.replaceAll("<", "&lt;");
		new_s = new_s.replaceAll(">", "&gt;");
		new_s = new_s.replaceAll("\"", "&quot;");
		return new_s;
	}
}
