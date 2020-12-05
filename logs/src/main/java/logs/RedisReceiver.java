package logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.FileWriteOrg;
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
			
				
			Map<String,Object> msgmap=null;
					
			msgmap=reader.getData(poolname,redisid);
			
			if(msgmap!=null){
				
				
				datalist.add(msgmap);
				
				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					untilPersist(datalist);
					
					stats(start,System.currentTimeMillis(),redisid,poolname,datalist.size());

					

					datalist=new ArrayList<Map<String,Object>>();
					
				}
				
				
			}else{
				

				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					untilPersist(datalist);
					
					stats(start,System.currentTimeMillis(),redisid,poolname,datalist.size());

					

					datalist=new ArrayList<Map<String,Object>>();
				}
				
				
				gotosleep();		
				
				
			}
			
			}
			
		
			
		}
		
	
	private void stats(long start, long end, String redisid2, String poolname2, int size) {
		
	Map<String,Object> logmap1=new HashMap<String,Object>();
		long diff=(end-start);
		logmap1.put("username", "sys");
		logmap1.put("totaltime",""+diff );
		if(diff!=0&&size!=0){
		logmap1.put("permessage",""+(diff/size) );
		}
		logmap1.put("queuename", poolname2);
		logmap1.put("redisid", redisid2);
		logmap1.put("recordcount", ""+size);

		logmap1.put("logname", "logwritetotaltime");


		//new FileWriteOrg().write(logmap1);
		
	}
	private void untilPersist(List<Map<String, Object>> datalist) {


		for(int i=0;i<datalist.size();i++){
			new FileWriteOrg().write(datalist.get(i));
		}
	}
	
	


	private void gotosleep() {
		
		try{

			Thread.sleep(10L);
		}catch(Exception e){
			
		}
	}
}
