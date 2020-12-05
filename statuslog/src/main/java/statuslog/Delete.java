package statuslog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.StatusLogDBConnection;

public class Delete {

	public void doProcess(){
		
		Connection connection=null;
		
		try{
			connection = StatusLogDBConnection.getInstance().getConnection();
			
			List<String> ackidlist=getAckidList(connection);
			
			if(ackidlist.size()>0){
				
				delete(connection,ackidlist);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}

	private void delete(Connection connection, List<String> ackidlist) {
		
		PreparedStatement statement=null;
		
		try{
			connection.setAutoCommit(false);
			statement=connection.prepareStatement("delete from reportlog_status where ackid=?");
			
			for(int i=0;i<ackidlist.size();i++){
				
				statement.setString(1, ackidlist.get(i));
				
				statement.addBatch();
			}
			
			statement.executeBatch();
			connection.commit();
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
		
	}

	private List<String> getAckidList(Connection connection) {
		
		List<String> result=new ArrayList();
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			statement=connection.prepareStatement("select ackid from reportlog_status where nextlevel='final'");
		    resultset=statement.executeQuery();
		    
		    while(resultset.next()){
		    	
		    	result.add(resultset.getString("ackid"));
		    }
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
		}
		return result;
	}
}
