package reroutekannelselect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dao.Table;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.queue.kannelQueue;

public class PollerStartup {

	
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
			connection=QueueDBConnection.getInstance().getConnection();
			
			if(!Table.getInstance().isAvailableTable(tablename)){
				
				Table.getInstance().addTable(tablename);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
		
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
			
		
			connection=QueueDBConnection.getInstance().getConnection();
			
		Iterator itr=instance.keySet().iterator();
		
		while(itr.hasNext()){
			
			String tablename=itr.next().toString();
			
			Set<String> availableuser = getAvailableUser(tablename);
			
			Iterator itr1=availableuser.iterator();
			
			while(itr1.hasNext()){
				
				Map<String,Object> logmap=new HashMap<String,Object>();

				String username=itr1.next().toString();
				logmap.put("username",username);
				logmap.put("tablename",tablename);
				logmap.put("logname","reroutekannelselectpoller");

			if(instance.get(tablename).isRunningUser(username)){
				logmap.put("status","poller already running ,skip the start poller ");
				new FileWrite().write(logmap);

				continue;

			}
			

			if(kannelQueue.isQueued(username,true)){
				
				logmap.put("status","the start poller ");
				new FileWrite().write(logmap);
				new DBReceiver(tablename, username).start();
			
				instance.get(tablename).runninguser.add(username);
			}else{
				
				logmap.put("status","smscid is not available ,Queued in kannel ,skip the start poller ");
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
			
			connection=QueueDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select distinct username from "+tablename);
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
