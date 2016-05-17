package com.ilabs.utterance;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONParser {

	static int status  = 0;
	static int MAX_CALLS = 250;
	static int page = 1;
	static int limit = 20;
	
	static boolean cont = true;
	
	static String BASE_URL = "http://index-recent02.dev.sv2.247-inc.net/callsearch";
	static String url;
	
	static JSONObject jObj ;
	static JSONObject callData[];
	static JSONArray jArray;
	
	static HashMap<String,LinkedList<String>> h;
	static LinkedList<String> dnisList;
	static LinkedList<String> logTags;
	static LinkedList<String> ani;
	static Semaphore semaphore = new Semaphore(limit);
	
	
	public static void call() {
		// TODO Auto-generated method stub

		//Get the date of the previous day 
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
				
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
		String s = dateFormat.format(cal.getTime());
		        
		//Extract year, month and day from received date
		String y  = s.substring(0,4);
		String mo = s.substring(5,7);
		String da = s.substring(8,10);
		System.out.println(y+" "+mo+ " "+da);
		//Get the name of the month from the number
		String monthString = new DateFormatSymbols().getMonths()[Integer.valueOf(mo)-1];
		        
		//Stores host::list of uuids as key-value pairs
		h = new HashMap<String,LinkedList<String>>();
				
		// JDBC driver name and database URL
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://provdb02.dev.shared.sv2.tellme.com:3306/cpm_development";
		Connection conn = null;
		Statement stmt = null;
		   
		try {
			  Class.forName(JDBC_DRIVER);
			  System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,"root","");
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT * FROM request where from_unixtime(start_date) like '"+y+"-"+mo+"-"+da+"%';";
		      System.out.println(sql);
		      ResultSet rs = stmt.executeQuery(sql);
		    
		      
		      LinkedList<String> request = new LinkedList<String>();
		      while(rs.next())
		      {
		    	  request.add(""+rs.getInt("request_id"));

		      }
		      
		      //System.out.println(request.size());
		      if(request.size() == 0)
		    	  System.out.println("No calls for this day");
		      int n=0;
		      while(request.size()> 0 && n<request.size())
		      {
		    	  
		    	  dnisList = new LinkedList<String>();
			      logTags = new LinkedList<String>();
			      ani = new LinkedList<String>();
			      
		    	  String request_id = request.get(n);
		    	  System.out.println(request_id);
		    	  sql = "SELECT ani FROM ani where request_id="+request_id;
			      rs = stmt.executeQuery(sql);
			      
			      while(rs.next()){
			          //Retrieve by column name
			        //  System.out.println(dnisList.getLast());
			          if(rs.getString("ani") == null)
			        	  ani.add("null");
			          else 
			        	  ani.add(rs.getString("ani"));
			         
			       }
			      sql = "SELECT dnis FROM dnis where request_id="+request_id;
			      rs = stmt.executeQuery(sql);
			      while(rs.next()){
			          //Retrieve by column name
			        //  System.out.println(dnisList.getLast());
			          if(rs.getString("dnis") == null)
			        	  dnisList.add("null");
			          else 
			        	  dnisList.add(rs.getString("dnis"));
			         
			       }
			      sql = "SELECT log_tag FROM log_tag where request_id="+request_id;
			      rs = stmt.executeQuery(sql);
			      
			      while(rs.next()){
			          //Retrieve by column name
			        //  System.out.println(dnisList.getLast());
			          if(rs.getString("log_tag") == null)
			        	  logTags.add("null");
			          else 
			        	  logTags.add(rs.getString("log_tag"));
			         
			       }
		      
		int k = 0;
		Thread threads[] = new Thread[dnisList.size()];
		//Separate thread for each dnis
		int m = 0;
		String ani_query = null;
		while(m<ani.size() || k< dnisList.size())
		{
			if(m<ani.size())
				ani_query = ani.get(m);
			while(k<dnisList.size())
			{
				try {
				//	System.out.println(JSONParser.semaphore.availablePermits());
					if(dnisList.size() > limit)
					semaphore.acquire();
				
				cont = true;
				if(logTags.size()>0 && logTags.get(k) == "null")
					{
						url  = BASE_URL+"?daterange="+y+""+mo+""+da+"T000000.."+y+""+mo+""+da+"T235959&tz=GMT&locale=en_US&dnis="+dnisList.get(k)+"&maxcalls="+MAX_CALLS+"&page="+page+"&output=json&ll="+logTags.get(k);
						if(ani_query != null)
							url = BASE_URL+"?daterange="+y+""+mo+""+da+"T000000.."+y+""+mo+""+da+"T235959&tz=GMT&locale=en_US&ani="+ani_query+"&dnis="+dnisList.get(k)+"&maxcalls="+MAX_CALLS+"&page="+page+"&output=json&ll="+logTags.get(k);
					}
				else
					{
						url = BASE_URL+"?daterange="+y+""+mo+""+da+"T000000.."+y+""+mo+""+da+"T235959&tz=GMT&locale=en_US&dnis="+dnisList.get(k)+"&maxcalls="+MAX_CALLS+"&page="+page+"&output=json";
						if(ani_query != null)
							url = BASE_URL+"?daterange="+y+""+mo+""+da+"T000000.."+y+""+mo+""+da+"T235959&tz=GMT&locale=en_US&ani="+ani_query+"&dnis="+dnisList.get(k)+"&maxcalls="+MAX_CALLS+"&page="+page+"&output=json";
					}
				
					
				//System.out.println(url);
				Runnable r = new FormUrl(url);
				threads[k] = new Thread(r);
				threads[k].start();
				System.out.println("Thread starting..");
				k++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			m++;
		}
		
		
		// Wait for threads to complete execution in order to proceed
		for(k = 0;k<dnisList.size();k++)
		{
			try {
				threads[k].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		n++;
		
		      }
		      
		      rs.close();
	          stmt.close();
	          conn.close();
			} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			} catch (SQLException e) {
			// TODO Auto-generated catch block
				//System.out.println(e.getErrorCode());
			e.printStackTrace();
			}finally{
					//finally block used to close resource
					try{
							if(stmt!=null)
								stmt.close();
					   }catch(SQLException se2){
					}// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   
		  String uuid = null; 
		System.out.println(h.size());
		try {
			Runtime.getRuntime().exec("mkdir "+y+"_"+mo+"_"+da);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		for( String x : h.keySet())
		{
			String cmd = null;
			ProcessBuilder build = new ProcessBuilder("ssh", "root@"+x, "cd /var/tellme/log ; find . -name calllogs");
			build.redirectErrorStream();
			Process findDirectory;
			try {
				findDirectory = build.start();
				InputStream inputStream1 = findDirectory.getInputStream();
				BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream1));
				String line1 = null;
				if((line1 = reader1.readLine() )!= null)
					cmd = "cd /var/tellme/log/calllogs/"+y+"/"+mo+""+monthString+"/"+da;
				else 
					cmd = "cd /var/tellme/log/calllogs-"+x.substring(0,5)+"/"+y+"/"+mo+""+monthString+"/"+da;
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//loop for each host in the HashMap
			//form the command based on Date
			for(int i=0;i<h.get(x).size();i++)
			{
				//loop for each uuid per host
				try {
			        	String z = h.get(x).get(i);
			        	//Run multiple commands
			        	//ssh access the host
			        	ProcessBuilder pb = new ProcessBuilder("ssh", "root@"+x, cmd+"; find . -name *"+z);
						pb.redirectErrorStream();
						Process process = pb.start();
						InputStream inputStream = process.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
						String line = null;
					
						if((line = reader.readLine())!= null) {
							//line will contain the path for the utterance files
							String sub_path = line.substring(1,line.length());
							String path = cmd.substring(3,cmd.length())+sub_path;
							System.out.println(path);
							
							String copy = "scp -r root@"+x+":"+path+" "+y+"_"+mo+"_"+da;
							//scp files to your host 
							Runtime.getRuntime().exec(copy);
							//tar the directory
							int  uuid_index = sub_path.lastIndexOf("/");
							uuid = sub_path.substring(uuid_index + 1, sub_path.length());
							System.out.println(uuid);
							//Runtime.getRuntime().exec("mkdir "+y+"_"+mo+"_"+da+"_tar_files");
							//Runtime.getRuntime().exec("cd "+y+"_"+mo+"_"+da+"_");
							
							
							
					}
					//if data for a particular uuid is not present	
					if(line == null) System.out.println("No data for "+z);
					
					} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
			}
			
//			//Delete folder
//			ProcessBuilder p = new ProcessBuilder("rm","-rf","/var/tmp/tdey/"+y+""+mo+""+da);
//			p.redirectErrorStream();
//			p.start();
//			//String del_files = "rm -r "+uuid.trim()+"/*";
//			//Runtime.getRuntime().exec(del_files);
			
				
			
		}
		String tar_cmd = "tar -zcvf "+y+"_"+mo+"_"+da+".tar.gz "+y+"_"+mo+"_"+da;
		try {
			System.out.println("attempt to delete");
			Runtime.getRuntime().exec(tar_cmd);
			String del_dir = "rm -R "+y+"_"+mo+"_"+da+"/"+uuid+"/*";
			System.out.println(del_dir);
			Runtime.getRuntime().exec(del_dir);
			del_dir = "rm -R "+y+"_"+mo+"_"+da+"/"+uuid;
			System.out.println(del_dir);
			Runtime.getRuntime().exec(del_dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static boolean HTTPGetRequest(String url)
	{
		URL u;
		HttpURLConnection c = null;
		
		try {
			u = new URL(url);
			c = (HttpURLConnection) u.openConnection();
			//Specify request type
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            //Establish Connection
            c.connect();
            //get response code
            status = c.getResponseCode();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return parseData(status,c);
	}

	public static boolean parseData(int status, HttpURLConnection c)
	{
		 switch (status) {
         case 200:
         case 201:
          
			try {
				//Parse data in the url
				BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
	             StringBuilder sb = new StringBuilder();
	             String line;
				while ((line = br.readLine()) != null) {
	                 sb.append(line+"\n");
	             }
	             br.close();
	            String json = sb.toString();
	            
	            //Covert to JSON object
	            jObj = new JSONObject(json);
				jArray = new JSONArray();
				//Get data for calls 
				jArray = jObj.getJSONArray("calls");
				int len = jArray.length();
				//System.out.println("len : " + len);
				callData = new JSONObject[len];
				if(len == 0) return false;
				else{
					
					for(int i=0;i<len;i++)
					{
						callData[i] = jArray.getJSONObject(i);
						String host = callData[i].getString("host");
						//Fill map with host-uuid pairs
						if(h.get(host)!=null)
						{
							LinkedList<String> l = new LinkedList<String>();
							l = h.get(host);
							l.add(callData[i].getString("uuid"));
							h.put(host, l);
						}
						else
						{
							LinkedList<String> l = new LinkedList<String>();
							l.add(callData[i].getString("uuid"));
							h.put(host, l);
						}
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
	            if (c != null) {
	                try {
	                    c.disconnect();
	                } catch (Exception ex) {
	                   ex.printStackTrace();
	                }
	            }
	        }
          
     }
		 if(status !=200 || status != 201) return false;
		return true;
	}
}

class FormUrl implements Runnable{

	String url;
	public FormUrl(String url)
	{
		this.url = url;
	}
	@Override
	public void run() {

		// TODO Auto-generated method stub
		
		
		while(JSONParser.cont)
  		{
  			//loop till data is received
  			JSONParser.cont = JSONParser.HTTPGetRequest(url);
  			JSONParser.page++;
  		}
		
		if(JSONParser.dnisList.size() > JSONParser.limit)
		JSONParser.semaphore.release();
		//System.out.println(JSONParser.semaphore.availablePermits());
		
	}
	
}