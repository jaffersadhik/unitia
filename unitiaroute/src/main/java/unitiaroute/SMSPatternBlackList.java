package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class SMSPatternBlackList {
	
	private static boolean isTableAvailable=false;

	private static SMSPatternBlackList obj=null;
	
	private Set<String> smspatternblacklistset=new HashSet<String>();
	
	
	private SMSPatternBlackList(){
	
		reload();
	}
	
	public static SMSPatternBlackList getInstance(){
		
		if(obj==null){
			
			obj=new SMSPatternBlackList();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Set<String> senderidset=null;
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "blacklist_smspattern")){
					
					if(table.create(connection, " create table blacklist_smspattern(smspattern varchar(700) primary key)", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select smspattern from blacklist_smspattern");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				if(senderidset==null){
					
					senderidset=new HashSet<String>();
					
				}
				
				senderidset.add(resultset.getString("smspattern"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(senderidset!=null){
			
			smspatternblacklistset=senderidset;
		}
		
	}
	
	
	public Set<String> getBlacklistPaternSet(){
		
		return smspatternblacklistset;
	}
	
	
	
}
