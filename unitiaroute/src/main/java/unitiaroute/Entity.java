package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class Entity {

	private static boolean isTableAvailable=false;

	private static boolean isDefaultTableAvailable=false;

	
	private static Entity obj=null;
	

	private Map<String,Map<String,String>> whitelistedsenderid=new HashMap<String,Map<String,String>>();
	


	private Map<String,Map<String,String>> defaultdlt=new HashMap<String,Map<String,String>>();

	private Entity(){
	
		reload();
	}
	
	public static Entity getInstance(){
		
		if(obj==null){
			
			obj=new Entity();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> whitelistedsenderid=new HashMap<String,Map<String,String>>();

		Map<String,Map<String,String>> defaultdlt=new HashMap<String,Map<String,String>>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		PreparedStatement statement1=null;
		ResultSet resultset1=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "dlt_entity")){
					
					if(table.create(connection, " create table dlt_entity(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),senderid varchar(15),entityid varchar(30))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			if(!isDefaultTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "dlt_entity_default")){
					
					if(table.create(connection, " create table dlt_entity_default(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16) unique key,senderid varchar(15),entityid varchar(30))", false)){
					
						isDefaultTableAvailable=true;
					}
				}else{
					
					isDefaultTableAvailable=true;
				}
			}
			statement =connection.prepareStatement("select username,senderid,entityid from dlt_entity");
			resultset=statement.executeQuery();
			while(resultset.next()){
				if(resultset.getString("username")==null||resultset.getString("senderid")==null||resultset.getString("entityid")==null){
					continue;
				}
				Map<String,String> senderidset=whitelistedsenderid.get(resultset.getString("username").toLowerCase());	
				if(senderidset==null){
					
					senderidset=new HashMap<String,String>();
					whitelistedsenderid.put(resultset.getString("username").toLowerCase(), senderidset);
				}
				senderidset.put(resultset.getString("senderid").toLowerCase(),resultset.getString("entityid").toLowerCase());
			}
			
			statement1 =connection.prepareStatement("select username,senderid,entityid from dlt_entity_default");
			resultset1=statement1.executeQuery();
			while(resultset1.next()){
				
				Map<String,String> senderidset=defaultdlt.get(resultset1.getString("username").toLowerCase());	
				if(senderidset==null){
					
					senderidset=new HashMap<String,String>();
					defaultdlt.put(resultset1.getString("username").toLowerCase(), senderidset);
				}
				senderidset.put("senderid",resultset1.getString("senderid"));
				senderidset.put("entityid",resultset1.getString("entityid"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(resultset1);
			Close.close(statement1);
			Close.close(connection);
		}
		
		
			
			this.whitelistedsenderid=whitelistedsenderid;
			this.defaultdlt=defaultdlt;
		
	}
	
	
	public String getEntity(String username,String senderid){
		if(whitelistedsenderid.containsKey(username)){
			
			
			
		return whitelistedsenderid.get(username).get(senderid.toLowerCase());
		}
		return null;
	}

	public Map<String,String> getEntity(String username) {

		return defaultdlt.get(username);
	}
	
	
	
}
