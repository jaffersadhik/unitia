package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class SMSPatternAllowedB {

	private static boolean isTableAvailable=false;

	private static SMSPatternAllowedB obj=null;
	
	private Map<String,List<Map<String,String>>> smspatternallowedset=new HashMap<String,List<Map<String,String>>>();

	
	private SMSPatternAllowedB(){
	
		reload();
	}
	
	public static SMSPatternAllowedB getInstance(){
		
		if(obj==null){
			
			obj=new SMSPatternAllowedB();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,List<Map<String,String>>> patternset=new HashMap<String,List<Map<String,String>>>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "allowed_smspattern")){
					
					if(table.create(connection, "create table allowed_smspattern(pattern_id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),smspattern varchar(700) ,unique(username,smspattern))  ENGINE=InnoDB DEFAULT CHARSET=utf8", false)){
					
						table.create(connection, "insert  into allowed_smspattern(username ,smspattern) values('testuser','.*')", false);
						table.create(connection, "insert  into allowed_smspattern(username ,smspattern) values('unitia','.*')", false);

						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select pattern_id,username,smspattern from allowed_smspattern  where senderid is null");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String username=resultset.getString("username");
				
				List<Map<String,String>> patternlist=patternset.get(username);
				
				if(patternlist==null){
					
					patternlist=new ArrayList<Map<String,String>>();
					patternset.put(username, patternlist);
				}
				
				Map<String,String> data=new HashMap<String,String>();
				data.put("smspattern",resultset.getString("smspattern").toLowerCase());
				data.put("pattern_id", resultset.getString("pattern_id"));
				patternlist.add(data);
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		smspatternallowedset=patternset;
	}
	
	
	public List<Map<String,String>> getAllowedPaternSet(String username){
		
		return smspatternallowedset.get(username);
	}
	
	

	
}
