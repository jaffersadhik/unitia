package dnsql;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.dngen.ErrorCodeType;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.processor.DNProcessor;
import com.winnovature.unitia.util.redis.RedisReader;

public class AppsReceiver extends Thread {

	public static boolean GRACESTOP=false;


	String poolname=null;
	
	
	String redisid=null;
	
	public AppsReceiver(String poolname,String redisid){
		
		this.poolname=poolname;
		
		
		this.redisid=redisid;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		
		List<Map<String,Object> > datalist=new ArrayList<Map<String,Object>>();
		long start=System.currentTimeMillis();

		while(!GRACESTOP){
			
				
			Map<String,Object> msgmap=null;
					
				
			msgmap=reader.getData(poolname,redisid);
			
			if(msgmap!=null){
				
			
				setDNValue(msgmap);
				
				datalist.add(msgmap);
				
				long diff=System.currentTimeMillis()-start;
				
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					
					start=System.currentTimeMillis();
					
					updateMap(datalist);
					
					untilPersist(datalist);
					
					stats(start,System.currentTimeMillis(),redisid,poolname,datalist.size());

					datalist=new ArrayList<Map<String,Object>>();
				}
				
				
			}else{
				

				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					updateMap(datalist);
					
					untilPersist(datalist);
					
					stats(start,System.currentTimeMillis(),redisid,poolname,datalist.size());

					datalist=new ArrayList<Map<String,Object>>();
				}
				
				
				gotosleep();		
				
				
			}
			
			}
			
		
			
		}

	
	
	 private void setDNValue(Map<String, Object> msgmap) {
			
	        final Map drmap = getDR( msgmap.get("username").toString(),  msgmap.get("dnmsg").toString());

	        final String err = (String) drmap.get("err");
	        
	        if (err.equalsIgnoreCase("000"))
	        {
	        	
				msgmap.put(MapKeys.DN_STATUSCD, "1");

	        }
	        else
	        {
	        	
				msgmap.put(MapKeys.DN_STATUSCD, "2");

	        }

			msgmap.put(MapKeys.CARRIER_DR, (String) drmap.get("dlr"));


			msgmap.put(MapKeys.CARRIER_SYSTEMID, "appssystemid");


		
	}
	private Map getDR(final String username, final String msg)
	    {
	        final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmm");
	        final HashMap<String,String> drmap = new HashMap<String,String>();
	        String date = null;
	        if( ErrorCodeType.getInstance().isLatencyAdjustment(username)){
	        	date= df.format(new Date(System.currentTimeMillis()+2*60*60*1000));
	        }else{
	       
	        	date= df.format(new Date());
	        }
	        
	        String dnMsg = "id:1";
	        dnMsg = String.valueOf(dnMsg) + " sub:001";
	        dnMsg = String.valueOf(dnMsg) + " dlvrd:001";
	        dnMsg = String.valueOf(dnMsg) + " submit date:" + date;
	        dnMsg = String.valueOf(dnMsg) + " done date:" + date;
	        if ( ErrorCodeType.getInstance().isDnRetry(username) ||  ErrorCodeType.getInstance().isSuccessMasking(username))
	        {
	            dnMsg = String.valueOf(dnMsg) + " stat:UNDELIV";

	            drmap.put("err", "003");

	        }
	        else
	        {
	        	 dnMsg = String.valueOf(dnMsg) + " stat:DELIVRD";
	             drmap.put("err", "000");

	        }
	        dnMsg = String.valueOf(dnMsg) + " err:" + drmap.get("err") + " ";
	        dnMsg = String.valueOf(dnMsg) + " Text:" + msg;
	        drmap.put("dlr", dnMsg);
	        return drmap;
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

			logmap1.put("logname", "deliverydbtotaltime");


	        new FileWrite().write(logmap1);
			
		}
	
	
	private void log(List<Map<String, Object>> datalist) {
		
		
		for(int i=0;i<datalist.size();i++){
			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.put("module", "submission");
			logmap.put("logname", "submission");
			logmap.putAll(datalist.get(i));
		}
			
		}
	
	
	private void untilPersist(List<Map<String, Object>> datalist) {


		while(true){
			
			if(datalist==null || datalist.size()<1){
				
				return;
			}
			
			while(true){
				
				if(new ReportDAO().insert("reportlog_submit",datalist)){
				
					log(datalist);
					
					break;
				}else{
					
					gotosleep();
				}
			}
			
			if(new ReportDAO().insert("reportlog_delivery",datalist)){
			
				return;
			}else{
				
				gotosleep();
			}
		}
			
	}

private void updateMap(List<Map<String, Object>> datalist) {
		
		
		for(int i=0,max=datalist.size();i<max;i++){
			
			Map<String, Object> data=datalist.get(i);
			
			new DNProcessor(data,new HashMap()).doProcess();
			
		
		}
		
		
	}
	private void gotosleep() {
		
		try{

			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
}
