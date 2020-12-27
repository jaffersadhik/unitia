package unitiatablereader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.CampaignDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.FileWrite;

public class PollerStartup {

	static String SQL=null;
	
	static {
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("CREATE TABLE {0}(");
		sb.append("id varchar(50) NOT NULL,");
		sb.append("campaign_activity_id varchar(200) NOT NULL,");
		sb.append("username varchar(20) NOT NULL,");
		sb.append("execution_date decimal(13,0) NOT NULL,");
		sb.append("msg_type varchar(11) NOT NULL,");
		sb.append("content varchar(4000) NOT NULL,");
		sb.append("sender_id varchar(11) NOT NULL,");
		sb.append("pstatus int(2) NOT NULL DEFAULT 0,");
		sb.append("mobile varchar(20) NOT NULL");
		sb.append(");");  
  
		SQL=sb.toString();
	}
	static Map<String,PollerStartup> instance=new HashMap<String,PollerStartup>();
	

	
	Set<String> runninguser=new HashSet<String>();
	
	String tablename=null;
	
	private PollerStartup(){
		
	}
	private PollerStartup(String tablename){
	
		this.tablename=tablename;
	
		init();
	}
	
	
	private void init() {
		
		TableExsists table=new TableExsists();
		Connection connection=null;
		try{
			connection=CampaignDBConnection.getInstance().getConnection();
			
			if(!table.isExsists(connection, "uiq_campaign."+tablename)){
			
				table.create(connection, getSQL("uiq_campaign."+tablename), false);
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
		
	}
	private static String getSQL(String tablename2) {
		
		String param[]={tablename2};
		
		return MessageFormat.format(SQL, param) ;
	}
	public static PollerStartup getInstance(String poolname) {
		
		if(!instance.containsKey(poolname)){
			
			instance.put(poolname, new PollerStartup(poolname));
		}
		
		return instance.get(poolname);
		
	}
	
	public static void updateUsers() {
		
		TableExsists table=new TableExsists();
		Connection connection=null;
		try{
			
		
			connection=CampaignDBConnection.getInstance().getConnection();
			
		Iterator itr=instance.keySet().iterator();
		
		while(itr.hasNext()){
			
			String tablename=itr.next().toString();
			
			Set<String> availableuser = getAvailableUser(tablename);
			
			Iterator itr1=availableuser.iterator();
			
			while(itr1.hasNext()){
				
				Map<String,Object> logmap=new HashMap<String,Object>();

				String username=itr1.next().toString();
				logmap.put("username",username);
				logmap.put("tablereaderlog","y");
				logmap.put("tablename",tablename);

			if(instance.get(tablename).isRunningUser(username)){
				logmap.put("status","poller already running ,skip the start poller ");
				new FileWrite().write(logmap);

				continue;
			}
			
			

			if(PushAccount.instance().getPushAccount(username)!=null){
				
				logmap.put("status","the start poller ");
				new FileWrite().write(logmap);
				new DBReceiver(tablename, username).start();
			
				instance.get(tablename).runninguser.add(username);
			}else{
				
				logmap.put("status","username not available in users table ,skip the start poller ");
				new FileWrite().write(logmap);
			}
			
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}
	private boolean isRunningUser(String username) {
		return runninguser.contains(username);
	}
	private static Set<String> getAvailableUser(String tablename) {
		
		Set<String> availableuser=new HashSet<String>();

		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection=CampaignDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select distinct username from uiq_campaign."+tablename);
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				availableuser.add(resultset.getString("username").toLowerCase());
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return availableuser;
		
	}

}
