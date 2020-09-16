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

public class WhiteListedSenderid {

	private static boolean isTableAvailable=false;

	private static WhiteListedSenderid obj=null;
	

	private Map<String,Set<String>> whitelistedsenderid=new HashMap<String,Set<String>>();
	
	
	private WhiteListedSenderid(){
	
		reload();
	}
	
	public static WhiteListedSenderid getInstance(){
		
		if(obj==null){
			
			obj=new WhiteListedSenderid();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Set<String>> whitelistedsenderid=new HashMap<String,Set<String>>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "whitelisted_senderid")){
					
					if(table.create(connection, " create table whitelisted_senderid(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),senderid varchar(15))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select username,senderid from whitelisted_senderid");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				Set<String> senderidset=whitelistedsenderid.get(resultset.getString("username").toLowerCase());	
				if(senderidset==null){
					
					senderidset=new HashSet<String>();
					whitelistedsenderid.put(resultset.getString("username").toLowerCase(), senderidset);
				}
				senderidset.add(resultset.getString("senderid"));
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
	
	
	public boolean isWhiteListedSenderid(String username,String senderid){
		
		if(whitelistedsenderid.containsKey(username)){
			
			return whitelistedsenderid.get(username).contains(senderid);
		}
		return false;
	}
	
	
	
}
