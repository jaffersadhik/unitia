package reroutekannelinsert;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.dao.Insert;
import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.processor.DNProcessor;
import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread {

	public static boolean GRACESTOP=false;


	String poolname=null;
	
	
	String redisid=null;
	
	RedisReader reader=new RedisReader();

	
	public RedisReceiver(String poolname,String redisid){
		
		this.poolname=poolname;
		
		
		this.redisid=redisid;
		
	}
	public void run(){
		
		
		List<Map<String,Object> > datalist=new ArrayList<Map<String,Object>>();
		long start=System.currentTimeMillis();

		while(!GRACESTOP){
			
				
			Map<String,Object> msgmap=null;
					
				
			try {
				msgmap=reader.getData(poolname,redisid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			} 
			
			if(msgmap!=null){
				
				String tname=msgmap.get(MapKeys.SMSCID).toString();
				msgmap.put(MapKeys.POLLER_USERNAME, tname);
			
				datalist.add(msgmap);
				
				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					
					untilPersist(datalist);
					
					datalist=new ArrayList<Map<String,Object>>();
				}
				
				
			}else{
				

				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					
					untilPersist(datalist);
					
					datalist=new ArrayList<Map<String,Object>>();
				}
				
				
				gotosleep();		
				
				
			}
			
			}
			
		
			
		}	
	
	private void untilPersist(List<Map<String, Object>> datalist) {


		while(true){
			
			if(datalist==null || datalist.size()<1){
				
				return;
			}
			
			boolean result=new Insert().insert("reroute_kannel", datalist);
			
			if(result){
			
				return;
			}else{
				
				gotosleep();
			}
		}
			
	}

	private void gotosleep() {
		
		try{

			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
}
