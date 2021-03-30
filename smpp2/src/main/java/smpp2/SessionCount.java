package smpp2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

public class SessionCount {

	private static String SELECT_SQL="select username,count(*) cnt smpp_bindlog group by username" ;
	
	
	private static SessionCount obj=new SessionCount();
	
	Map<String,String> sessioncount=new HashMap<String,String>();
	
	private SessionCount(){
		
		init();
		reload();
	}

	public void reload() {
		
		Map<String,String> temp=getSessionCount();
		
		if(temp!=null){
			
			sessioncount=temp;
		}
	}

	private Map<String, String> getSessionCount() {
		
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		Map<String,String> temp=new HashMap<String,String>();

		
		try{
			
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(SELECT_SQL);
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				temp.put(resultset.getString("username"), resultset.getString("cnt"));
			}
			
		}catch(Exception e){
			 e.printStackTrace();
			 temp=null;
		}finally{
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
	
		return temp;
	}

	private void init() {
		
		BindDAO.getInstance().insert();
	}
	
	public static SessionCount getInstance(){
		
		if(obj==null){
			
			obj=new SessionCount();
		}
		
		return obj;
	}
	
	public int getCount(String username){
		
		if(sessioncount.containsKey(username)){
		
			try{
				
				return Integer.parseInt(sessioncount.get(username));
				
			}catch(Exception e){
				
			}
		}
		
		return 0;
	}
}
