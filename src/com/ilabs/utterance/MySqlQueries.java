package com.ilabs.utterance;

import java.sql.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
public class MySqlQueries {

	static String DB_OWNER;
	static String DB_USER ;
	static String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static String DB_URL;
	static String username;
	static String password;
	static String day;
	static String dbtype;
	static String failover;
	static String BASE_URL = "http://cdblog01.qa.shared.sv2.tellme.com/get_cluster_roles.pl";
	
	public static void call()
	{
		run("ok");
	}
	
	public static void run(String args) {
		// TODO Auto-generated method stub
		
		System.out.println("I started");
		 //if date has been passed as an argument use it else get the system date and time
		 // getDate() sets day to system date
		 
//		if(args.length > 0)
//			day = args[0];
//		else
		    getDate();
		//System.out.println(day);
		//configuration file contains data about some fixed parameters
		//like dbtype, username , password etc.
		//this method reads the configuration file and populates respective fields
		
		readConfigurationFile();
		
		//a list of the all the hosts to be queried is extracted from an api
		getHosts();
        	
		
	}
	
	public static void getDate()
	{
		//cal is an instance of calendar that gets the date for the previous day "-1"
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		
		//specify the format in which the date and time is required
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
		String s = dateFormat.format(cal.getTime());
		        
		//Extract year, month and day from received date
		String y  = s.substring(0,4);
		String mo = s.substring(5,7);
		String da = s.substring(8,10);
		
		//day is of the form YYYYMMDD
		day = y+""+mo+""+da;
	}
	
	public static void readConfigurationFile()
	{
		//Read configuration from file
		BufferedReader br = null;
		try {

			String sCurrentLine;
			//Read the contents of the file configuration
			//MySqlQueries class was used as FileReader cannot be accessed from within a .jar file
			System.out.println("here");

			//System.setProperty("user.dir", "C:\\Users\\trishu.d\\Documents\\Work\\workspace\\UtteranceCapture\\src");
			System.out.println(System.getProperty("user.dir"));
			InputStream input = MySqlQueries.class.getResourceAsStream("configuration");
			System.out.println(input);
			br = new BufferedReader(new InputStreamReader(input));
			
			//loop through each line of the configuration file and
			//populate the respective fields
			
			while ((sCurrentLine = br.readLine()) != null) {
				int in = sCurrentLine.indexOf("=");
				if(sCurrentLine.substring(0, in).equals("DB_OWNER"))
					DB_OWNER = sCurrentLine.substring(in+1,sCurrentLine.length());
				else if (sCurrentLine.substring(0, in).equals("DB_USER"))
					DB_USER = sCurrentLine.substring(in+1,sCurrentLine.length());
				else if (sCurrentLine.substring(0, in).equals("username"))
					username = sCurrentLine.substring(in+1,sCurrentLine.length());
				else if (sCurrentLine.substring(0, in).equals("password"))
					password = sCurrentLine.substring(in+1,sCurrentLine.length());
				else if (sCurrentLine.substring(0, in).equals("dbtype"))
					dbtype= sCurrentLine.substring(in+1,sCurrentLine.length());
				else if (sCurrentLine.substring(0, in).equals("failover"))
					failover = sCurrentLine.substring(in+1,sCurrentLine.length());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
	}
	
	public static void getHosts()
	{
		InputStream inputXml = null;
        try
        {
           //get xml data about hosts from api
           inputXml  = new URL(BASE_URL+"?user=+"+username+"&dbtype="+dbtype+"&failover="+failover).openConnection().getInputStream();

           DocumentBuilderFactory factory = DocumentBuilderFactory.
                                            newInstance();
           DocumentBuilder builder = factory.newDocumentBuilder();
           Document doc = builder.parse(inputXml);
           NodeList node = doc.getElementsByTagName("role");
           System.out.println(node.getLength());
           int i = 0;
           //from each line extract the host name
           while (node.getLength() > i)
           {


              Element element = (Element)node.item(i);
              DB_URL = "jdbc:mysql://"+element.getAttribute("host")+":3306/";
            //  DB_URL = "jdbc:mysql://dblog01.dev.sv2.247-inc.net:3306/";
            //  DB_URL = "jdbc:mysql://dblog01.qa.shared.sv2.tellme.com:3306/";
              System.out.println("DB link: " + DB_URL);
              
              //run query for each of the hosts
              query();
              
              i++;

            }
        }
        catch (Exception ex)
        {
           System.out.println(ex.getMessage());
        }
        finally
        {
           try
           {
              if (inputXml != null)
              inputXml.close();
           }
           catch (IOException ex)
           {
              System.out.println(ex.getMessage());
           }
        }
	}
	public static void query()
	{
		Connection conn = null;
		Statement stmt = null;
		
		
		try {
			// JDBC driver name and database URL
			//connect to the databse using jdbc
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
		    conn = DriverManager.getConnection(DB_URL,username,password);
		    System.out.println("Creating statement...");
		    stmt = conn.createStatement();
		    
		    
			
			//maps to store query results
		    HashMap<String,LinkedList<Table>> found_uuids = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_transfer_hash = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_utt_hash = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_utt_attr_hash = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_utt_grammar_hash = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_utt_result_hash = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_utt_slot_hash = new HashMap<String,LinkedList<Table>>();
		    HashMap<String,LinkedList<Table>> call_vxml_log_hash = new HashMap<String,LinkedList<Table>>();
		    
		  
		    
		    
		    //get uuids for temp table
		    do_load_uuids(conn,stmt,day);
		    
		    //create temp table 
		    create_temporary_table(conn,stmt,day);
		    
		    //get basic data about calls
		    found_uuids = do_call_query(conn,stmt,day);
		    
		    //get call transfer data
		    call_transfer_hash = do_call_transfer_query(conn,stmt,day);
		    
		    //get data about utts in calls
		    call_utt_hash = do_call_utt_query(conn,stmt,day);
		    
		    // get attribs of utts in calls
		    call_utt_attr_hash = do_call_utt_attr_query(conn,stmt,day);
		    
		    //get grammars of utts in calls
		    call_utt_grammar_hash = do_call_utt_grammar_query(conn,stmt,day);
		    
		    //get results of utts in calls
		    call_utt_result_hash = do_call_utt_result_query(conn,stmt,day);
		    
		    //get interps/slots of utts in calls
		    call_utt_slot_hash = do_call_utt_slot_query(conn,stmt,day);
		    
		    //get vxml log (tags) data for calls
		    call_vxml_log_hash = do_call_vxml_log_query(conn,stmt,day);
		    
		    int numuuidsprocessed = 0;
		    
		    try {
				Runtime.getRuntime().exec("mkdir calllogs_"+day);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //for each uuid from calls table generate xml 
		    for(String uuid : found_uuids.keySet())
		    {
		    	
		    	numuuidsprocessed++;
		        if (numuuidsprocessed % 1000 == 0) {
		            System.out.println("Processed "+numuuidsprocessed +" uuids");
		        }
		        //To join all the tables together for xml generation
		        Chart data = new Chart();
		        data.col_name = new LinkedList<String>();
		        data.tables = new LinkedList<LinkedList<Table>>();
		        
		        data.col_name.add("found_uuids");
		        data.col_name.add("utts");
		        data.col_name.add("transfer");
		        data.col_name.add("attr");
		        data.col_name.add("grammar");
		        data.col_name.add("result");
		        data.col_name.add("slot");
		        data.col_name.add("logtag");
		        
		        
		        
		        
		        LinkedList<Table> temp2 = new LinkedList<Table>();
		        temp2 = found_uuids.get(uuid);
		 
		        data.tables.add(temp2);
		        
		        LinkedList<Table> temp1 = new LinkedList<Table>();
		        if(call_utt_hash == null || call_utt_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_utt_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        if(call_transfer_hash == null || call_transfer_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_transfer_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        if(call_utt_attr_hash == null || call_utt_attr_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_utt_attr_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        if(call_utt_grammar_hash == null || call_utt_grammar_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_utt_grammar_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        
		        if(call_utt_result_hash == null || call_utt_result_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_utt_result_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        if(call_utt_slot_hash == null || call_utt_slot_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_utt_slot_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        if(call_vxml_log_hash == null || call_vxml_log_hash.get(uuid) == null)
		        	temp1 = null;
		        else
		        	temp1 = call_vxml_log_hash.get(uuid);
		        
		        data.tables.add(temp1);
		        
		        
		        
		        //make utterances list to pass to writexmlcalllog
		        
		        
		       GenerateXML xml = new GenerateXML();
		       xml.writexmlcalllogs(uuid,data,numuuidsprocessed,day);
		       
		        
		        
		    }
		    
	        
		    stmt.close();
	        conn.close(); 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//get all the uuids from calls and store in a file 
	public static void do_load_uuids(Connection conn,Statement stmt,String day)
	{
		String sql;
		sql = "SELECT uuid from "+DB_OWNER+".calls_"+day;
		
		try{
			PrintWriter writer = new PrintWriter("uuid_file.txt", "UTF-8");
			
			ResultSet rs = stmt.executeQuery(sql);
			int i = 0;
			while(rs.next())
			{
				if(i!=0)writer.println("");
				String uuid = rs.getString("uuid");
				writer.print(uuid.trim());
				i++;
				
			}
			//System.out.println("size:"+i);
			writer.close();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// create a temporary table and load data of the uuid file into the table
	public static void create_temporary_table(Connection conn, Statement stmt, String day)
	{
		String sql = " CREATE TEMPORARY TABLE IF NOT EXISTS "+DB_USER+".uuid_temp( `uuid` char(36) BINARY NOT NULL, UNIQUE(uuid) ) ENGINE=HEAP";
		
		try {
			stmt.executeUpdate(sql);
			stmt = conn.createStatement(
				    ResultSet.TYPE_SCROLL_SENSITIVE,
				    ResultSet.CONCUR_UPDATABLE);
				 
			sql = "LOAD DATA LOCAL INFILE '"+"uuid_file.txt"+"' INTO TABLE "+DB_USER+".uuid_temp (uuid);";
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// get basic data about calls, like ani, did, etc.
	// return via hash ref provided: keys=uuids, vals=hashes with call attribs

	public static HashMap<String,LinkedList<Table>> do_call_query(Connection conn,Statement stmt,String day)
	{
		System.out.println("do call query");
		HashMap<String,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		String sql;
		
	      sql = "SELECT c.uuid, c.ani, c.ani_ii AS ani2, c.dnis AS did, c.request_uri_host, c.request_uri_user, c.ani_private AS privacy,@{[ sqlDateField('from_unixtime(c.start_tm)') ]}  AS open_time,t.name AS host,@{[ sqlDateField('FROM_UNIXTIME(c.hangup_ut)') ]} AS hangup_time FROM "+DB_OWNER+".calls_"+day+" c, heap.telboxes t,"+DB_USER+".uuid_temp u WHERE c.telbox_id = t.telbox_id AND c.uuid = u.uuid AND c.call_completion_id is not null;";
	  
	      try {
			ResultSet rs = stmt.executeQuery(sql);
			
			
			int num_rows = 0;
			
			Table t = new Table();
		    
			//process call query results, populating found_uuids
			while(rs.next()){
				LinkedList<Table> list = new LinkedList<Table>();
		          //Retrieve by column name
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				  String uuid = rs.getString("uuid");
				  t.col_name = new LinkedList<String>();
				  t.colmns = new LinkedList<String>();
				  
				  t.col_name.add("sip");
				  t.col_name.add("ani");
				  t.col_name.add("ani2");
				  t.col_name.add("did");
				  t.col_name.add("privacy");
				  t.col_name.add("open_time");
				  t.col_name.add("host");
				  t.col_name.add("hangup_time");
				  
				  t.colmns.add(rs.getString("request_uri_user")+"@"+rs.getString("request_uri_host"));
				  t.colmns.add(rs.getString("ani"));
				  t.colmns.add(""+rs.getInt("ani2"));
				  t.colmns.add(rs.getString("did"));
				  t.colmns.add(""+rs.getInt("privacy"));
				  t.colmns.add(""+rs.getDate("open_time"));
				  t.colmns.add(rs.getString("host"));
				  t.colmns.add(""+rs.getDate("hangup_time"));
				  
				  if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
					  
				  num_rows++;
		         
		       }
			
			System.out.println("Number of hosts processed " + num_rows);
		      rs.close();
	          
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		return h;
		
	}
	
	// query for info about transfers in calls.
	//return data via provided hash ref; keys=uuids, vals=hashes with transfer data
	
	public static HashMap<String,LinkedList<Table>> do_call_transfer_query(Connection conn, Statement stmt, String day)
	{
		HashMap<String,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		String sql;
		sql = "select m.uuid, @{[ sqlDateField('from_unixtime(f.request_ut)') ]} request_tm, @{[ sqlDateField('from_unixtime(f.request_ut)') ]} start_tm, @{[ sqlDateField('from_unixtime(f.hangup_ut)') ]} hangup_tm, f.destination_number,f.transfer_exit_code, CASE WHEN f.is_blind THEN 1 ELSE 0 END as transfer_type_id  FROM "+DB_OWNER+".transfers_"+day+" f,"+DB_USER+".uuid_temp m WHERE f.uuid = m.uuid AND f.transfer_completion_id is not null";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			
			int num_rows = 0;
			Table t = new Table();
		    
			
			while(rs.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String uuid = rs.getString("uuid");
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				
				
				t.col_name.add("request_tm");
				t.colmns.add(""+rs.getDouble("request_tm"));
				t.col_name.add("start_tm");
				t.colmns.add(""+rs.getDouble("start_tm"));
				t.col_name.add("hangup_tm");
				t.colmns.add(""+rs.getDouble("hangup_tm"));
				t.col_name.add("destination_number");
				t.colmns.add(rs.getString("destination_number"));
				t.col_name.add("exit_code");
				t.colmns.add(""+rs.getInt("transfer_exit_code"));
				t.col_name.add("type");
				t.colmns.add(""+rs.getInt("transfer_type_id"));
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
				num_rows++;
			}
			
			System.out.println("Number of hosts processed " + num_rows);
		}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return h;
	}
	// query to find utterance events for calls
	// return data via provided hash ref.
	// keys=uuids; vals=list of hashes, each of these hashes has data about one utt
	public static HashMap<String,LinkedList<Table>> do_call_utt_query(Connection conn, Statement stmt,String day)
	{
		HashMap<String ,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		
		String sql;
		sql = "select m.uuid, u.utt_call_id, lower(s.name) event, @{[ sqlDateField('from_unixtime(u.event_ut)') ]} utt_tm, u.utt_id,u.reason,u.field,u.hash,u.model_id, r.name as recoserver from "+DB_OWNER+".utts_"+day+" u, "+DB_OWNER+".utt_states s,"+DB_USER+".uuid_temp m LEFT JOIN "+DB_OWNER+".reco_servers r ON u.reco_server_id = r.reco_server_id where u.utt_state = s.utt_state AND u.uuid = m.uuid ORDER BY u.event_ut, u.utt_call_id";
		try {
			ResultSet rs = stmt.executeQuery(sql);

		    // process call_utt query results, creating
		    // call_utt_hash: indexed by uuid will contain array of rows in event_tm order
			int num_rows = 0;
			Table t = new Table();
		    
			while(rs.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String uuid = rs.getString("uuid");
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				t.col_name.add("utt_call_id");
				t.colmns.add(""+rs.getInt("utt_call_id"));
				t.col_name.add("event");
				t.colmns.add(""+rs.getString("event"));
				t.col_name.add("utt_tm");
				t.colmns.add(""+rs.getDouble("utt_tm"));
				t.col_name.add("utt_id");
				t.colmns.add(""+rs.getInt("utt_id"));
				t.col_name.add("reason");
				t.colmns.add(""+rs.getInt("reason"));
				t.col_name.add("field");
				t.colmns.add(rs.getString("field"));
				t.col_name.add("hash");
				t.colmns.add(rs.getString("hash"));
				t.col_name.add("model_id");
				t.colmns.add(rs.getString("model_id"));
				t.col_name.add("recoserver");
				t.colmns.add(rs.getString("recoserver"));
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
				num_rows++;
			}
			
			System.out.println("Number of hosts processed " + num_rows);
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return h;
	}
	// get attributes of utterances events.
	// return data via hash ref provided.
	// keys=utt_call_id (integer identifier of utterance)
	// vals=list of hashes, each containing key=val pairs describing
	// one attribute of the utterance, in fields "name" and "value",
	// plus event type in field "event"

	public static HashMap<String, LinkedList<Table>> do_call_utt_attr_query(Connection conn,Statement stmt,String day)
	{
		HashMap<String ,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		
		String sql;
		sql = "SELECT STRAIGHT_JOIN u.uuid, u.utt_call_id,lower(s.name) as event,av.utt_attr_id,a.name,av.value from "+DB_USER+".uuid_temp m,"+DB_OWNER+".utts_"+day+" u,"+DB_OWNER+".utt_states s,"+DB_OWNER+".utt_attr_values_"+day+" av, "+DB_OWNER+".utt_attrs a WHERE u.utt_state = s.utt_state AND u.uuid = m.uuid AND u.utt_call_id = av.utt_call_id AND av.utt_attr_id = a.utt_attr_id ORDER BY u.utt_call_id, av.utt_attr_id";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			
			int num_rows = 0;
			Table t = new Table();
		    
			while(rs.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String uuid = rs.getString("uuid");
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				t.col_name.add("utt_call_id");
				t.colmns.add(""+rs.getInt("utt_call_id"));
				t.col_name.add("utt_attr_id");
				t.colmns.add(""+rs.getInt("utt_attr_id"));
				t.col_name.add("value");
				t.colmns.add(""+rs.getString("value"));
				t.col_name.add("name");
				t.colmns.add(""+rs.getString("name"));
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
				num_rows++;
				
			}
			
			System.out.println("Number of hosts processed " + num_rows);
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return h;
	}
	// get grammars used to process utterances events.
	// return data via hash ref provided.
	// keys=utt_call_id (integer identifier of utterance)
	// vals=list of hashes, each containing key=val pairs describing
	// one attribute of the utterance, (hash, url, inline_grammar)
	// in fields "name" and "value"
	public static HashMap<String,LinkedList<Table>> do_call_utt_grammar_query(Connection conn, Statement stmt, String day)
	{
		HashMap<String,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		HashMap<String,Table> grammar_cache = new HashMap<String,Table>();
		
		String sql;
		sql = "SELECT * FROM "+DB_OWNER+".grammars_"+day;
		int num_rows = 0;
		
		
		try {
			ResultSet rs = stmt.executeQuery(sql);
			
			String grammar_type = null;
			
			Table t = new Table();
		    
			
			while(rs.next())
			{
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String type = rs.getString("grammar_version").substring(0, 1);
				if(type.equals("0"))
					grammar_type = "inline";
				else if(type.equals("1"))
					grammar_type = "url phase 1";
				
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				
				t.col_name.add("inline grammar");
				t.col_name.add("url");
				t.col_name.add("type");
				
				t.colmns.add(rs.getString("inline_grammar"));
				t.colmns.add(rs.getString("url"));
				t.colmns.add(grammar_type);
				
				grammar_cache.put(rs.getString("md5_hash"), t);
				
				
				
				num_rows++;
					
			}
			System.out.println("Number of hosts processed " + num_rows);
			num_rows = 0;
			
			String utt_grammar_sql = "SELECT STRAIGHT_JOIN u.uuid, ug.weight, ug.load_order, u.utt_call_id, ug.md5_hash as hash  , g.url, g.inline_grammar FROM "+DB_USER+".uuid_temp m,"+DB_OWNER+".utts_"+day+" u, "+DB_OWNER+".utt_grammars_"+day+" ug , "+DB_OWNER+".grammars_"+day+" g WHERE u.uuid = m.uuid AND u.uuid = ug.uuid AND u.utt_id=ug.utt_id AND ug.md5_hash=g.md5_hash ORDER BY u.utt_call_id, u.utt_id, load_order";
			ResultSet rs1 = stmt.executeQuery(utt_grammar_sql);
			
			
			Table t1 = new Table();
		    
			while(rs1.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String uuid = ""+rs1.getString("uuid");
				//System.out.println(uuid);
				t1.col_name = new LinkedList<String>();
				t1.colmns = new LinkedList<String>();
				t1.col_name.add("weight");
				t1.col_name.add("load_order");
				t1.col_name.add("utt_call_id");
				t1.col_name.add("hash");
				t1.col_name.add("url");
				t1.col_name.add("inline_grammar");
				t1.col_name.add("type");
				
				t1.colmns.add(""+rs1.getDouble("weight"));
				t1.colmns.add(""+rs1.getInt("load_order"));
				t1.colmns.add(""+rs1.getInt("utt_call_id"));
				t1.colmns.add(rs1.getString("hash"));
				Table temp = grammar_cache.get(rs1.getString("hash"));
				t1.colmns.add(temp.colmns.get(1));
				t1.colmns.add(temp.colmns.get(0));
				t1.colmns.add(temp.colmns.get(2));
				num_rows++;
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t1);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp1 = h.get(uuid);
					  temp1.add(t1);
					  h.put(uuid,temp1);
				  }
			}
			System.out.println("Number of hosts processed " + num_rows);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return h;
	}
	// get reco results (recognized text and confidence) in utts in targetted calls
	// return data via provided hash ref. keys=utt_call_id; vals=list of hashes,
	// describing nbest results per utt; each hash has fields "words", "conf",
	// and "utt_result_id", used as xref with nl-interp (slot key/val) data
	public static HashMap<String,LinkedList<Table>> do_call_utt_result_query(Connection conn, Statement stmt, String day)
	{
		HashMap<String,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		String sql;
		sql = "SELECT distinct u.uuid, u.utt_call_id,r.utt_result_id,r.result as words,r.confidence as conf FROM "+DB_OWNER+".utts_"+day+" u, "+DB_OWNER+".utt_results_"+day+" r, "+DB_USER+".uuid_temp m WHERE u.uuid = m.uuid AND u.utt_call_id = r.utt_call_id ORDER BY r.utt_result_id";
		
		try {
			ResultSet rs = stmt.executeQuery(sql);
			
			int num_rows = 0;
			Table t = new Table();
		    
			while(rs.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String uuid = ""+rs.getString("uuid");
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				
				t.col_name.add("utt_call_id");
				t.col_name.add("utt_result_id");
				t.col_name.add("words");
				t.col_name.add("conf");
				
				t.colmns.add(""+rs.getInt("utt_call_id"));
				t.colmns.add(""+rs.getInt("utt_result_id"));
				t.colmns.add(""+rs.getString("words"));
				t.colmns.add(""+rs.getInt("conf"));
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
				num_rows++;
			}
			System.out.println("Number of hosts processed " + num_rows);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return h;
	}
	// get slot names and values for reco results of utts in targeted calls.
	// return data via provided hash ref. keys=utt_result_id (xref with
	// utt_result_id attrib of reco results data pulled earlier);
	//vals=list of hashes, each describing one slot key/val;
	// fields of these hashes are "name" and "value" (of the slot),
	// and "interp_id"--a reco result could have multiple interps,
	// this describes which interp (0,1,...etc) this slot key/val
	// pair is part of
	public static HashMap<String,LinkedList<Table>> do_call_utt_slot_query(Connection conn,Statement stmt, String day) 
	{
		HashMap<String,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		String sql;
		sql = "SELECT DISTINCT u.uuid, u.utt_call_id,s.utt_result_id, s.interp_id,s.name,s.value from "+DB_OWNER+".utts_"+day+" u , "+DB_OWNER+".utt_slots_"+day+" s , "+DB_USER+".uuid_temp m WHERE u.uuid = m.uuid AND u.utt_call_id = s.utt_call_id order by s.utt_call_id";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			int num_rows = 0;
			Table t = new Table();
		    
			while(rs.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				String uuid = ""+rs.getString("uuid");
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				
				t.col_name.add("utt_call_id");
				t.col_name.add("utt_result_id");
				t.col_name.add("interp_id");
				t.col_name.add("name");
				t.col_name.add("value");
				
				t.colmns.add(""+rs.getInt("utt_call_id"));
				t.colmns.add(""+rs.getInt("utt_result_id"));
				t.colmns.add(""+rs.getInt("interp_id"));
				t.colmns.add(""+rs.getString("name"));
				
				String val = rs.getString("value");
				int first_quote = val.indexOf("\"");
				int end_quote = val.indexOf("\"",val.indexOf("\""));
				
				if(first_quote != end_quote)
					t.colmns.add(val.substring(first_quote+1, end_quote));
				else
					t.colmns.add(val);
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
				num_rows++;
			}
			System.out.println("Number of hosts processed " + num_rows);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return h;
	}

	// query for log tag data for requested calls. data returned via provided hash ref.
	// keys=call uuids; vals=list of hashes, each hash describes one logtag;
	// fields are "log_label", "log_value", and "readable_event_tm" (aka, time)
	public static HashMap<String,LinkedList<Table>> do_call_vxml_log_query(Connection conn, Statement stmt,String day)
	{
		
		HashMap<String,LinkedList<Table>> h = new HashMap<String,LinkedList<Table>>();
		String sql;
		sql = "SELECT STRAIGHT_JOIN v.uuid, l.log_label,v.log_value, @{[ sqlDateField('from_unixtime(event_ut)')]} as readable_event_tm from "+DB_USER+".uuid_temp m, "+DB_OWNER+".vxml_logs_"+day+" v, heap.log_labels l where v.log_label_id = l.log_label_id AND m.uuid = v.uuid order by event_ut,vxml_log_id";
		 
		try {
		    stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			int num_rows = 0;
			Table t = new Table();
			
			while(rs.next())
			{
				LinkedList<Table> list = new LinkedList<Table>();
				if(num_rows == 0) System.out.println("Query done\nProcessing call query results");
				
				String uuid = rs.getString("uuid");
				
				t.col_name = new LinkedList<String>();
				t.colmns = new LinkedList<String>();
				
				t.col_name.add("log_label");
				t.col_name.add("log_value");
				t.col_name.add("readable_event_tm");
				
				t.colmns.add(rs.getString("log_label"));
				t.colmns.add(rs.getString("log_value"));
				t.colmns.add(rs.getString("readable_event_tm"));
		
				
				if(!h.containsKey(uuid))
				  {
					  list.add(t);
					  h.put(uuid,list);
				  }
				  else
				  {
					  LinkedList<Table> temp = h.get(uuid);
					  temp.add(t);
					  h.put(uuid,temp);
				  }
				num_rows++;
			}
			System.out.println("Number of hosts processed " + num_rows);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return h;
	}
	//a bit of sql that we repeat a lot, to get dates returned in a specific parseable fmt
	public static String sqlDateField(String unix_time)
	{
		return "DATE_FORMAT("+unix_time+", '%S,%i,%H,%j,%Y')";
	}
	
	//convert date format from way sql formats it to way we like
	
}

class Chart {
	LinkedList<String> col_name;
	LinkedList<LinkedList<Table>> tables;
}
class Table{
	 LinkedList<String> col_name;
	 LinkedList<String> colmns;
}
