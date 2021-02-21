package queuecheck;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.SMSCMaxQueue;

public class kannelQueue {

	static Map<String,Map<String,String>> smscqueue=new HashMap<String,Map<String,String>>();
	
	static{
		

			Connection connection=null;
			
			try{
				connection=CoreDBConnection.getInstance().getConnection();
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "queue_count_smscid")){
					
					table.create(connection, "create table queue_count_smscid(smscid varchar(50),queuecount numeric(10,0),status varchar(20),updatetime numeric(13,0))", false);
				}
				
				
				
			}catch(Exception e){
				 e.printStackTrace();
			}finally{
			
				Close.close(connection);
			}
			
		
	}
	public static void reload() {
		
try{
		Map<String,Properties> mapprop=Kannel.getInstance().getKannelmap();
		
		Iterator itr=mapprop.keySet().iterator();
		
		Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();
		while(itr.hasNext()){
			Properties prop=mapprop.get(itr.next());
			String version=getVersion(prop.getProperty("kannel_status"));
			
			if(version==null){
				version="1.5.0";
			}
			if(version.indexOf("1.5.0")>-1){
				
				result.putAll( getStatusV2(prop.getProperty("kannel_status")));

			}else{
				
				result.putAll( getStatusV1(prop.getProperty("kannel_status")));

			}
		}
		

		smscqueue=result;
		insertQueueintoDB();

}catch(Exception e){
	e.printStackTrace();
}
}
	
	
public static void insertQueueintoDB() {
		
		Connection connection=null;
		
		try
		
		{
			

			
			connection =CoreDBConnection.getInstance().getConnection();
			Iterator itr=smscqueue.keySet().iterator();
			
			while(itr.hasNext()){
				
				String smscid=itr.next().toString();
				String queuecount=smscqueue.get(smscid).get("queued");
				String status=smscqueue.get(smscid).get("status");

				if(updateDB(connection, smscid, queuecount,status)<1){
					
					insertQueueintoDB(connection, smscid, queuecount,status);
				}

				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}

public static void insertQueueintoDB(Connection connection,String smscid,String queuecount,String status) {
		

		PreparedStatement insert=null;
		
		try
		
		{
						long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into queue_count_smscid(smscid,queuecount ,updatetime,status ) values(?,?,?,?)");
			insert.setString(1, smscid);
			insert.setString(2, queuecount);
			insert.setString(3, ""+updatetime);
			insert.setString(4, status);

			insert.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(insert);
		}
	}

	
	public static int updateDB(Connection connection,String smscid,String count, String status) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update queue_count_smscid set queuecount=?,updatetime=?,status=? where smscid=? ");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, ""+status);
			update.setString(4, smscid);
			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}


	private static String getVersion(String url) throws Exception {

		 String xmlString = getStringXML(url);

		 if(xmlString!=null&&xmlString.trim().length()>0){
		return xmlString.substring(xmlString.indexOf("<version>")+9, xmlString.indexOf("</version>")).split(" ")[3];
	
		 }else{
			 
			 return null;
		 }
		}
	public static Map<String,Map<String,String>> getStatusV1(String url){
		
		
		  HashMap<String, String> values = new HashMap<String, String>();
	        String xmlString=null;
			try {
				xmlString = getStringXML(url);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(xmlString==null||xmlString.trim().length()<1){
				xmlString="";
			}
	        xmlString= xmlString.replaceAll("online ", "online_");
	        Document xml = convertStringToDocument(xmlString);
	        if(xml!=null){
	        Node user = xml.getFirstChild();
	        NodeList childs = user.getChildNodes();
	        Node child;
	        for (int i = 0; i < childs.getLength(); i++) {
	            child = childs.item(i);
	             values.put(child.getNodeName(), child.getTextContent());
	        }
	        }
		
	     Iterator itr=values.keySet().iterator();
	     
	     while(itr.hasNext()){
	    	 
	 		itr.next();

	     }
	     
	 	String smscs=	values.get("smscs");
	     Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();

	 	if(smscs!=null){
	     StringTokenizer st=new StringTokenizer(values.get("smscs"),"\t");
	     
	     
	     st.nextToken();
	     
	     int i=0;
	     while(st.hasMoreTokens()){

		     
	    	 ++i;
		     
		     st.nextToken();
		     String smscid=st.nextToken();
		     
		     Map<String,String> data=result.get(smscid);
		     
		     if(data==null){
		    	 
		    	 data=getMap();
		    	 
		    	 result.put(smscid, data);
		    	 
		     }
		     st.nextToken();
		     String status=st.nextToken();

		     if(status.startsWith("online")){
		    	 data.put("status","up");
		     }else{
		    	 continue;
		     }
		     
		     String failed=st.nextToken();
		     
		     try{
		    	 
		    	 int iF=Integer.parseInt(failed);
		    	 iF=iF+Integer.parseInt(data.get("failed"));
		    	 data.put("failed", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		     
		     
		     String queued=st.nextToken();
		     
		     try{
		    	 
		    	 int iF=Integer.parseInt(queued);
		    	 iF=iF+Integer.parseInt(data.get("queued"));
		    	 data.put("queued", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		     st.nextToken();
		     String sent=st.nextToken();
		     
		     try{
		    	 
		    	 int iF=Integer.parseInt(sent);
		    	 iF=iF+Integer.parseInt(data.get("sent"));
		    	 data.put("sent", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		     st.nextToken();
		     
		     String senttps=st.nextToken();
		     
		     try{
		    	 
		    	 double iF=Double.parseDouble(senttps);
		    	 iF=iF+Double.parseDouble(data.get("senttps"));
		    	 data.put("senttps", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		     String received=st.nextToken();
		     
		     try{
		    	 
		    	 int iF=Integer.parseInt(received);
		    	 iF=iF+Integer.parseInt(data.get("received"));
		    	 data.put("received", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		     
		     st.nextToken();
		 	
		    
		     String receicvedtps=st.nextToken();
		     
		     
	     try{
		    	 
		    	 double iF=Double.parseDouble(receicvedtps);
		    	 iF=iF+Double.parseDouble(data.get("receicvedtps"));
		    	 data.put("receicvedtps", ""+iF);
		     }catch(Exception e){
		    	 
		     }
	     st.nextToken();
		     
	     }
	 	}
	     return result;
	}
	
	
	
	
	public static Map<String,Map<String,String>> getStatusV2(String url) {
		
		
		  HashMap<String, String> values = new HashMap<String, String>();
	        String xmlString=null;
			try {
				xmlString = getStringXML(url);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	        
	        if(xmlString==null||xmlString.trim().length()<1){
	        	xmlString="";
	        }
	        xmlString= xmlString.replaceAll("online ", "online_");
	        Document xml = convertStringToDocument(xmlString);

	        if(xml!=null){
	        Node user = xml.getFirstChild();
	        NodeList childs = user.getChildNodes();
	        Node child;
	        for (int i = 0; i < childs.getLength(); i++) {
	            child = childs.item(i);
	             values.put(child.getNodeName(), child.getTextContent());
	        }

	        }
	     Iterator itr=values.keySet().iterator();
	     
	     while(itr.hasNext()){
	    	 
	 		itr.next();

	     }
	     
	 	String smscs=	values.get("smscs");
	     Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();

	 	if(smscs!=null){
	     StringTokenizer st=new StringTokenizer(values.get("smscs"),"\t");
	     
	     
	     st.nextToken();
	     
	     int i=0;
	     while(st.hasMoreTokens()){
	    	 ++i;
		    
		     st.nextToken();
		     String smscid=st.nextToken();
		     
		     Map<String,String> data=result.get(smscid);
		     
		     if(data==null){
		    	 
		    	 data=getMap();
		    	 
		    	 result.put(smscid, data);
		    	 
		     }
		     st.nextToken();
		     String status=st.nextToken();

		     if(status.startsWith("online")){
		    	 data.put("status","up");
		     }else{
		    	 
		    	 for(int j=0;j<4;j++){
		    	 try{
		    		 st.nextToken();
		    	 }catch(Exception e1){
		    		 
		    	 }
		    	 }
		    	 continue;
		     }
		 
		     String received=st.nextToken();
		     received=received.substring(1);
		     
		     
		     try{
		    	 
		    	 int iF=Integer.parseInt(received);
		    	 iF=iF+Integer.parseInt(data.get("received"));
		    	 data.put("received", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		     String sent=st.nextToken();
		     sent=sent.substring(0, sent.length()-1);
		     try{
		    	 
		    	 int iF=Integer.parseInt(sent);
		    	 iF=iF+Integer.parseInt(data.get("sent"));
		    	 data.put("sent", ""+iF);
		     }catch(Exception e){
		    	 
		     }
		       String failed=st.nextToken();
	     
	     try{
	    	 
	    	 int iF=Integer.parseInt(failed);
	    	 iF=iF+Integer.parseInt(data.get("failed"));
	    	 data.put("failed", ""+iF);
	     }catch(Exception e){
	    	 
	     }
	     
	     
	     String queued=st.nextToken();
	     
	     try{
	    	 
	    	 int iF=Integer.parseInt(queued);
	    	 iF=iF+Integer.parseInt(data.get("queued"));
	    	 data.put("queued", ""+iF);
	     }catch(Exception e){
	    	 
	     }
	    
	         
	     }
	 	}
	     return result;
	}
	
	private static Map<String, String> getMap() {

		Map<String,String> data=new HashMap<String,String>();
		data.put("status", "down");
		data.put("queued", "0");
		data.put("sent", "0");
		data.put("senttps", "0.0");

		data.put("failed", "0");
		data.put("received", "0");	
		data.put("receivedtps", "0.0");		

		return data;
	}


	public static String getStringXML(String url) throws Exception{
		
		String xml=connectKannel(url);
		
		return xml;
	}
	

	public static String connectKannel(String sUrl)  {

		String response = "";

		BufferedReader in = null;
		try {
			int httpConnectionTimeout = 300000;
			int httpResponseTimeout = 300000;
			URL url = new URL(sUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setConnectTimeout(httpConnectionTimeout);
			connection.setReadTimeout(httpResponseTimeout);

			int iGetResultCode = connection.getResponseCode();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer decodedString = new StringBuffer();
			String temp = null;

			while ((temp = in.readLine()) != null) {
				decodedString.append(temp);
			}

			if (iGetResultCode == 200 || iGetResultCode == 202) {

				if (decodedString.toString().length() != 0){
					response = decodedString.toString().trim();
				}
				return response;
			} else {
				

						}

		} catch (Exception e) {
			
						
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
		return response;
	}

	

	 private static Document convertStringToDocument(String xmlStr) {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder;
	        try {
	            builder = factory.newDocumentBuilder();
	            Document doc = builder.parse(new InputSource(new StringReader(
	                    xmlStr)));
	            return doc;
	        } catch (Exception e) {
	            
	        }
	        return null;
	    }
	 
	 
	 
	 public static boolean isQueued(String smscid){
		 
		 Map<String,String> data=smscqueue.get(smscid);
		 
		 if(data==null){
			 
			 return false;
		 }
		 
		 String status=data.get("status");
		 
		 if(status==null){
			 
			 return false;
		 }
		 
		 
		 if(status.equals("down")){
			 
			 return true;
		 }
		 
		 
		 String queued=data.get("queued");
		 
		 if(queued==null){
			 
			 return false;
		 }
		 
		 
		 String maxqueue=SMSCMaxQueue.getInstance().getQueue(smscid);
		 
		 long lMaxQueue=Long.parseLong(maxqueue);
		 
		 long lQueued=Long.parseLong(queued);
		 
		 return lQueued>lMaxQueue;
	 }
}

