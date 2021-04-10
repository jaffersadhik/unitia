package queuecheck;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.SMSCMaxQueue;

public class kannelMaxQueue {

	
	static{
		

			Connection connection=null;
			
			try{
				connection=CoreDBConnection.getInstance().getConnection();
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "queue_max_smscid")){
					
					table.create(connection, "create table queue_max_smscid(smscid varchar(50),queuecount numeric(10,0),updatetime numeric(13,0))", false);
				}
				
				
				
			}catch(Exception e){
				 e.printStackTrace();
			}finally{
			
				Close.close(connection);
			}
			
		
	}
	public static void reload() {
		
		
		Connection connection=null;
		
		try{

		connection=CoreDBConnection.getInstance().getConnection();
		
		Map<String,String> result=getMaxQueue(connection);
		
		Map<String,Map<String,String>> queuemap=kannelQueue.smscqueue;
		
		Iterator itr=queuemap.keySet().iterator();
		
		while(itr.hasNext()){
			
			String smscid=itr.next().toString();
			String count=queuemap.get(smscid).get("queued");
			
			if(result.containsKey(smscid)){
				
				long cc=Long.parseLong(count);
				
				long max=Long.parseLong(result.get(smscid));
				
				if(cc>max){
			
					result.put(smscid,count );
					
				}
			}else{
				result.put(smscid,count );
			}
		}

		insertQueueintoDB(connection,result);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
}
	
	
	
private static Map<String, String> getMaxQueue(Connection connection) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		Map<String,String> result=new HashMap<String,String>();
		
		try{
			statement=connection.prepareStatement("select smscid,queuecount from queue_max_smscid");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("smscid"), resultset.getString("queuecount"));
			}
		}catch(Exception e){
			
		}finally{
			Close.close(resultset);
			Close.close(statement);
		}
		return result;
	}
	
	
	
	
	
public static void insertQueueintoDB(Connection connection,Map<String,String> result) {
		
		
		try
		
		{
			

			
			Iterator itr=result.keySet().iterator();
			
			while(itr.hasNext()){
				
				String smscid=itr.next().toString();
				String queuecount=result.get(smscid);

				if(updateDB(connection, smscid, queuecount)<1){
					
					insertQueueintoDB(connection, smscid, queuecount);
				}

				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
	}
	}

public static void insertQueueintoDB(Connection connection,String smscid,String queuecount) {
		

		PreparedStatement insert=null;
		
		try
		
		{
						long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into queue_max_smscid(smscid,queuecount ,updatetime ) values(?,?,?)");

			insert.setString(1, smscid);
			insert.setString(2, queuecount);
			insert.setString(3, ""+updatetime);

			insert.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(insert);
		}
	}

	
	public static int updateDB(Connection connection,String smscid,String count) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update queue_max_smscid set queuecount=?,updatetime=? where smscid=? ");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, smscid);
			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}





	

	 
	 
	 
}

