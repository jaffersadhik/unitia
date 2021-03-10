 package creditupdate;
 
 import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;

import unitiaroute.TableExsists;

 
 public class CreditDAO
 {
	 
	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
 
	 static boolean creditbalancetableAvilable=false;
 
	 static boolean credittopuphistorytableAvilable=false;
	 
	 static String SELECT = "SELECT distinct a.id,a.username,a.topup_credit from credit_topup_history a,users b,admin c,superadmin d  where a.status='INITIATE' and  a.username=b.username and b.admin_id=c.admin_id and c.superadmin_id=d.superadmin_id and d.mode=?";

	 static String UPDATE_HISTORY = "update credit_topup_history set status='ADDED',updatetime=? where id=?";

	 static String updatesql = "update credit_balance set balance_credit=?,updatetime=? where username=?";
	    
     static String insertsql = "insert into credit_balance(username, balance_credit,updatetime) values(?,?,?)";

	 static String CREDIT_BALANCE_SQL="create table credit_balance(username varchar(16) primary key,balance_credit numeric(10,2),updatetime datetime)";
   
	 static String CREDIT_TOPUP_HISTORY_SQL="create table credit_topup_history(id INT NOT NULL primary key AUTO_INCREMENT,username varchar(16) ,topup_credit numeric(10,2),inserttime datetime,updatetime datetime,status varchar(20) default 'INITIATE')";

	 public List<Map<String, String>> getCreditTopupInfo()
     throws Exception
   {
	   Connection connection = null;
	   PreparedStatement statement=null;
	   ResultSet resultset=null;
	   List<Map<String, String>> result = new ArrayList<Map<String, String>>();
     try
     {
    	 connection = CoreDBConnection.getInstance().getConnection();
    	 
    	 if(!credittopuphistorytableAvilable){
    		 TableExsists table=new TableExsists();
    		 if(!table.isExsists(connection, "credit_topup_history")){
    		 if( table.create(connection, CREDIT_TOPUP_HISTORY_SQL, true)){
       		  
    			 credittopuphistorytableAvilable=true;
       	  }
    		 }
    	 }
    	 
 
    	 connection.setAutoCommit(false);
			
			statement = connection.prepareStatement(" select * from credit_topup_history_lock for UPDATE");
			resultset = statement.executeQuery();
			if (resultset.next()) {

				return getRecords();
			}
    	 
    
     }
     catch (Exception e)
     {
       throw e;
     }
     finally
     {
    	 Close.close(resultset);
    	 Close.close(statement);
    	 Close.close(connection);
     }
     
 
    return result;
   }
 
 
 
 
   private List<Map<String, String>> getRecords() {
	
	   Connection connection = null;
	   PreparedStatement statement=null;
	   ResultSet resultset=null;
	   List<Map<String, String>> result = new ArrayList<Map<String, String>>();
     
	   try{
		   connection=CoreDBConnection.getInstance().getConnection();
		 statement= connection.prepareStatement(SELECT);
		 statement.setString(1, MODE);
    	 resultset = statement.executeQuery();
 
       while (resultset.next())
       {
         String id = resultset.getString("id");
         String topupcredit = resultset.getString("topup_credit");
         String username = resultset.getString("username");
 
         Map<String,String> topup=new HashMap<String,String>();
         topup.put("id",id);
         topup.put("topupcredit",topupcredit);
         topup.put("username",username);
         result.add(topup);
       }
 
	   }catch(Exception e){
		   e.printStackTrace();
	   }finally{
		   
		   Close.close(resultset);
	    	 Close.close(statement);
	    	 Close.close(connection);
	   }
	   
		if (result.size() > 0) {

			update( result);
		}

	   return result;
	}




private void update(List<Map<String, String>> result) {

	Connection connection = null;
	PreparedStatement statement = null;
	try {

		
			connection = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(" update credit_topup_history set status='FETCHED' where id = ?");

		
		connection.setAutoCommit(false);

	
		for (int i = 0; i < result.size(); i++) {

			Map<String, String> data = result.get(i);
			statement.setString(1, data.get("id"));
			statement.addBatch();
		}

		statement.executeBatch();

	} catch (Exception e) {
		e.printStackTrace();
	} finally {

		Close.close(statement);
		Close.close(connection);
	}

	
}




public void updateBalanceCount(Map<String, String> creditMap)
     throws Exception
   {
     Connection connection = null;
     try
     {
    	 DecimalFormat df = new DecimalFormat("#");
    	 df.setMaximumFractionDigits(2);
 
       connection =CoreDBConnection.getInstance().getConnection();
      
       if(!creditbalancetableAvilable){
    	   
    	   TableExsists table = new TableExsists();
    	   
    	   if(!table.isExsists(connection, "credit_balance")){
    	  if( new TableExsists().create(connection, CREDIT_BALANCE_SQL, false)){
    		  
    		  creditbalancetableAvilable=true;
    	  }
    	   }
       }
    
        Iterator loop = creditMap.keySet().iterator();
        
       while (loop.hasNext())
       {
         String username = (String)loop.next();
         String value = creditMap.get(username) == null ? "0" : (String)creditMap.get(username);
         double balance = Double.parseDouble(df.format(Double.parseDouble(value)));
 
         int count=updateBalance(connection,updatesql,username,balance);
         
         if(count>0){
        	
        	 continue;
         }
         
         insertBalance(connection,insertsql,username,balance);
       }
 
       
     }
     catch (Exception e)
     {
       throw e;
     }
     finally
     {
    	 Close.close(connection);
     }
   }




private void insertBalance(Connection connection, String insertsql, String username, double balance) {


	
	PreparedStatement statement=null;
	
	try{
		
		statement=connection.prepareStatement(insertsql);		
		statement.setString(1, username);
		statement.setDouble(2, balance);
		statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));		
		statement.executeUpdate();
		
	}catch(Exception e){
		
		e.printStackTrace();
	}finally{
		
		Close.close(statement);
	}

}




private int updateBalance(Connection connection, String updatesql, String username, double balance) {
	
	PreparedStatement statement=null;
	
	try{
		
		statement=connection.prepareStatement(updatesql);
		statement.setDouble(1, balance);
		statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
		statement.setString(3, username);
		
		return statement.executeUpdate();
		
	}catch(Exception e){
		
		e.printStackTrace();
	}finally{
		
		Close.close(statement);
	}
	return 0;
}




public int updateTopupHistory(String id) {
	
	Connection connection=null;
	PreparedStatement statement=null;
	
	try{
		
		connection=CoreDBConnection.getInstance().getConnection();
		statement=connection.prepareStatement(UPDATE_HISTORY);
		statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		statement.setString(2, id);
		
		return statement.executeUpdate();
		
	}catch(Exception e){
		
		e.printStackTrace();
	}finally{
		
		Close.close(statement);
	
		Close.close(connection);
	}
	
	return 0;
	
}
 
 
 }
