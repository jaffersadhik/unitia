package dnquerylog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread {


	public static boolean GRACESTOP=false;


	String poolname=null;	
	
	String redisid=null;
	
	public RedisReceiver(String poolname,String redisid){
	
		this.redisid=redisid;
		
		this.poolname=poolname;
		
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		
		List<Map<String,Object> > datalist=new ArrayList<Map<String,Object>>();
		long start=System.currentTimeMillis();
		while(!GRACESTOP){
			
				
					
			Map<String,Object>  msgmap=reader.getData(poolname,redisid);
			
			if(msgmap!=null){
				
				
				datalist.add(msgmap);
				
				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					untilPersist(datalist);
					
						
					log(datalist);
					
					datalist=new ArrayList<Map<String,Object>>();
					
				}
				
				
			}else{
				

				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					untilPersist(datalist);
					
					
					log(datalist);


					datalist=new ArrayList<Map<String,Object>>();
				}
				
				
				gotosleep();		
				
				
			}
			
			}
			
		
			
		}
		
	
	private void untilPersist(List<Map<String, Object>> datalist) {


		if(datalist==null || datalist.size()<1){
			
			return;
		}
		while(true){
			
			if(new ReportDAO().insertDNQueryLog("reportlog_dnquerylog",datalist)){
			
				return;
			}else{
				
				gotosleep();
			}
		}
			
		
	}
	
	
	
private void log(List<Map<String, Object>> datalist) {
		
	
	for(int i=0;i<datalist.size();i++){
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", "requestlog");
		logmap.put("logname", "requestlog");
		logmap.putAll(datalist.get(i));
        new FileWrite().write(logmap);

	}
		
	}

	

	private void gotosleep() {
		
		try{

			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
}
