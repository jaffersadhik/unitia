package submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.SubmitUtility;
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

					new SubmitUtility().errorDNHandover(datalist);
					
					log(datalist);
					
					datalist=new ArrayList<Map<String,Object>>();
					
				}
				
				
			}else{
				

				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					untilPersist(datalist);
					
					stats(start,System.currentTimeMillis(),redisid,poolname,datalist.size());

					new SubmitUtility().errorDNHandover(datalist);
					
					
					log(datalist);


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

		logmap1.put("logname", "submissiondbtotaltime");


        new FileWrite().write(logmap1);
		
	}
	private void untilPersist(List<Map<String, Object>> datalist) {


		if(datalist==null || datalist.size()<1){
			
			return;
		}
		while(true){
			
			if(new ReportDAO().insert("reportlog_submit",datalist)){
			
				return;
			}else{
				
				gotosleep();
			}
		}
			
		
	}
	
	
	
private void log(List<Map<String, Object>> datalist) {
		
	
	for(int i=0;i<datalist.size();i++){
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", "submission");
		logmap.put("logname", "submission");
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
