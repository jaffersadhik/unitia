package demo;

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

public class SMSPatternAllowed {

	
	public SMSPatternAllowed(){
	
	}
	
	public String getPattern(){
		
	
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =CoreDBConnection.getInstance().getConnection();
			statement =connection.prepareStatement("select pattern_id,username,smspattern from allowed_smspattern_bkup where pattern_id=233");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			return resultset.getString("smspattern").toLowerCase();
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
	
		return null;
	
	}
	
	
	

	
}
