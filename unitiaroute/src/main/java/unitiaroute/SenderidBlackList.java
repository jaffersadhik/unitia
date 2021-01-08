package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class SenderidBlackList {

	private static boolean isTableAvailable=false;

	private static SenderidBlackList obj=null;
	
	private Set<String> senderidblacklistset=new HashSet<String>();
	
	
	private SenderidBlackList(){
	
		reload();
	}
	
	public static SenderidBlackList getInstance(){
		
		if(obj==null){
			
			obj=new SenderidBlackList();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Set<String> senderidset=new HashSet<String>();

		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "blacklist_senderid")){
					
					if(table.create(connection, " create table blacklist_senderid(senderid varchar(15) primary key)", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select senderid from blacklist_senderid");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			
				
				senderidset.add(resultset.getString("senderid"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
			
			return;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(senderidset!=null){
			
			senderidblacklistset=senderidset;
		}
		
	}
	
	
	public boolean isBalckList(String senderid){
		
		return senderidblacklistset.contains(senderid);
	}
	
	
	
}
