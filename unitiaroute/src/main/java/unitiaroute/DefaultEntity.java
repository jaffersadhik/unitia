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

public class DefaultEntity {

	private static boolean isTableAvailable=false;

	private static DefaultEntity obj=null;
	

	private Map<String,Map<String,String>> whitelistedsenderid=new HashMap<String,Map<String,String>>();
	
	
	private DefaultEntity(){
	
		reload();
	}
	
	public static DefaultEntity getInstance(){
		
		if(obj==null){
			
			obj=new DefaultEntity();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> whitelistedsenderid=new HashMap<String,Map<String,String>>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
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
			
			statement =connection.prepareStatement("select username,senderid,entityid from dlt_entity");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				Map<String,String> senderidset=whitelistedsenderid.get(resultset.getString("username").toLowerCase());	
				if(senderidset==null){
					
					senderidset=new HashMap<String,String>();
					whitelistedsenderid.put(resultset.getString("username").toLowerCase(), senderidset);
				}
				senderidset.put(resultset.getString("senderid").toLowerCase(),resultset.getString("entityid").toLowerCase());
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
			
			this.whitelistedsenderid=whitelistedsenderid;
		
		
	}
	
	
	public String getEntity(String username,String senderid){
		
		if(whitelistedsenderid.containsKey(username)){
			
			return whitelistedsenderid.get(username).get(senderid.toLowerCase());
		}
		return null;
	}
	
	
	
}
