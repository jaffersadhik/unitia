package delivery;

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

	
	static Map<String,PollerStartup> instance=new HashMap<String,PollerStartup>();
	

	String kannelid=null;
	String smscid=null;
	Set<String> runninguser=new HashSet<String>();
	
	
	private PollerStartup(){
		
	}
	private PollerStartup(String kannelid,String smscid){
	
		this.kannelid=kannelid;
		this.smscid=smscid;
	
	}
	
	
	
	public static PollerStartup getInstance(String kannelid,String smscid) {
		
		String key=kannelid+"~"+smscid;
		if(!instance.containsKey(key)){
			
			instance.put(key, new PollerStartup(kannelid,smscid));
		}
		
		return instance.get(key);
		
	}
	
	public static void updateUsers() {
		
		TableExsists table=new TableExsists();
		try{
			
		
			Map<String,Map<String,String>> kannelmap=DLRCount.getInstance().getKannelmap();
			
			
		Iterator itr=kannelmap.keySet().iterator();
		
		while(itr.hasNext()){
			
			String kannelid=itr.next().toString();
			
			Map<String,String> smscidmap=kannelmap.get(kannelid);
			
			Iterator itr1=smscidmap.keySet().iterator();
			
			while(itr1.hasNext()){
				
				Map<String,Object> logmap=new HashMap<String,Object>();

				String smscid=itr1.next().toString();
				logmap.put("username","sys");
				logmap.put("kannelid",kannelid);
				logmap.put("smscid",smscid);
				logmap.put("logname", "sqlboxdnpoller");

				String key=kannelid+"~"+smscid;
			if(instance.get(key).isRunningUser(kannelid,smscid)){
				logmap.put("status","poller already running ,skip the start poller ");
				new FileWrite().write(logmap);

				continue;
			}
			
			
			logmap.put("status","the start poller ");
			new FileWrite().write(logmap);
			instance.get(key).runninguser.add(key);

			new DBReceiver(kannelid, smscid).start();
		
		
			
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
	}
	private boolean isRunningUser(String kannelid,String smscid) {
		String key=kannelid+"~"+smscid;
		return runninguser.contains(key);
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
