package delivery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.winnovature.unitia.util.db.Kannel;
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
			if(PollerStartup.getInstance(kannelid, smscid).isRunningUser(kannelid,smscid)){
				logmap.put("status","poller already running ,skip the start poller ");
				new FileWrite().write(logmap);

				continue;
			}
			
			Properties prop=Kannel.getInstance().getKannelmap().get(kannelid);
			
			if(prop.get("sqlbox").toString().equals("0")){
				
				logmap.put("status","sqlbox disabled ,skip the start poller ");
				new FileWrite().write(logmap);

				continue;
			}
			
			logmap.put("status","the start poller ");
			new FileWrite().write(logmap);

			new DBReceiver(kannelid, smscid).start();
		
			PollerStartup.getInstance(kannelid, smscid).runninguser.add(key);

			
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

}
