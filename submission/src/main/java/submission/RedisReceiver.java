package submission;

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

	private static String DLR_URL="http://{0}:{1}/api/dnreceiver?username={2}&senderidorg={3}&dnmsg={4}&ackid={5}&msgid={6}&mobile={7}&smscidorg={8}&rtime={9}&ktime={10}&carriersystemid={11}&carrierdr={12}&statuscd={13}&operator={14}&circle={15}&countrycode={16}&protocol={17}&msgtype={18}&featurecd={19}&fullmsg={20}&param1={21}&param2={22}&param3={23}&param4={24}&routeclass={25}&attempttype={26}&totalmsgcount={27}&splitseq={28}&statusidfromplatform={29}&statusid_org={30}&smscid={31}&statusid={32}&entityid={33}&templateid={34}";

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

					errorDNHandover(datalist);
					
					log(datalist);
					
					datalist=new ArrayList<Map<String,Object>>();
					
				}
				
				
			}else{
				

				long diff=System.currentTimeMillis()-start;
				if((datalist.size()>100 || diff > 350)&&datalist.size()!=0){
					start=System.currentTimeMillis();
					
					untilPersist(datalist);
					
					stats(start,System.currentTimeMillis(),redisid,poolname,datalist.size());

					errorDNHandover(datalist);;
					
					
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
private void errorDNHandover(List<Map<String, Object>> datalist) {
		
		for(int i=0,max=datalist.size();i<max;i++){
			try{
			Map<String, Object> data=datalist.get(i);
			
			if(data.get(MapKeys.STATUSID).equals(""+MessageStatus.KANNEL_SUBMIT_SUCCESS)){
			
				String statusidorg=(String)data.get(MapKeys.STATUSID_ORG);
				
				if(statusidorg!=null&&statusidorg.equals("402")){
					
				}else{
				
					continue;
				}
				
			}
			
			
				setDLRURL(data);
				
				deliverThroughURL(new URL(data.get(MapKeys.DLR_URL).toString()));
				
			} catch (Exception e) {
				
				
			}
		}
		
	}
	
	private void deliverThroughURL(final URL url)
    {
        HttpURLConnection httpConnection = null;
        final int responseCode = 0;
        String urlResponse = "";
        BufferedReader reader = null;
        final OutputStream outStream = null;
        try
        {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setRequestMethod("GET");
            reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            int val = 0;
            while ((val = reader.read()) > -1)
            {
                urlResponse = String.valueOf(urlResponse) + (char) val;
            }
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (Exception ex)
            {}
        }
        try
        {
            reader.close();
        }
        catch (Exception ex2)
        {}
    }
    
	
public void setDLRURL(Map<String , Object> msgmap)  throws Exception{
		
		msgmap.put(MapKeys.KTIME, ""+System.currentTimeMillis());
		String statusid=msgmap.get(MapKeys.STATUSID_ORG)==null?"":msgmap.get(MapKeys.STATUSID_ORG).toString();
		
		if(statusid.equals("")){
			
			statusid=msgmap.get(MapKeys.STATUSID)==null?"":msgmap.get(MapKeys.STATUSID).toString();
			
		}
		String [] params=
		{
				URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_IP)),//0
				URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_PORT)),//1
				URLEncoder.encode(msgmap.get(MapKeys.USERNAME)==null?"":msgmap.get(MapKeys.USERNAME).toString()),//2
				URLEncoder.encode(msgmap.get(MapKeys.SENDERID_ORG)==null?"":msgmap.get(MapKeys.SENDERID_ORG).toString()),//3
				URLEncoder.encode(msgmap.get(MapKeys.DNMSG)==null?"":msgmap.get(MapKeys.DNMSG).toString()),//4
				URLEncoder.encode(msgmap.get(MapKeys.ACKID)==null?"":msgmap.get(MapKeys.ACKID).toString()),//5
				URLEncoder.encode(msgmap.get(MapKeys.MSGID)==null?"":msgmap.get(MapKeys.MSGID).toString()),//6
				URLEncoder.encode(msgmap.get(MapKeys.MOBILE)==null?"":msgmap.get(MapKeys.MOBILE).toString()),//7
				URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG)==null?"":msgmap.get(MapKeys.SMSCID_ORG).toString()),//8
				URLEncoder.encode(msgmap.get(MapKeys.RTIME)==null?"":msgmap.get(MapKeys.RTIME).toString()),//9
				URLEncoder.encode(msgmap.get(MapKeys.KTIME)==null?"":msgmap.get(MapKeys.KTIME).toString()),//10
				URLEncoder.encode(""),//11
				URLEncoder.encode(""),//12
				URLEncoder.encode("32"),//13
				URLEncoder.encode(msgmap.get(MapKeys.OPERATOR)==null?"":msgmap.get(MapKeys.OPERATOR).toString()),//14
				URLEncoder.encode(msgmap.get(MapKeys.CIRCLE)==null?"":msgmap.get(MapKeys.CIRCLE).toString()),//15
				URLEncoder.encode(msgmap.get(MapKeys.COUNTRYCODE)==null?"":msgmap.get(MapKeys.COUNTRYCODE).toString()),//16
				URLEncoder.encode(msgmap.get(MapKeys.PROTOCOL)==null?"":msgmap.get(MapKeys.PROTOCOL).toString()),//17
				URLEncoder.encode(msgmap.get(MapKeys.MSGTYPE)==null?"":msgmap.get(MapKeys.MSGTYPE).toString()),//18
				URLEncoder.encode(msgmap.get(MapKeys.FEATURECODE)==null?"":msgmap.get(MapKeys.FEATURECODE).toString()),//19
				URLEncoder.encode(msgmap.get(MapKeys.FULLMSG)==null?"":msgmap.get(MapKeys.FULLMSG).toString()),//20
				URLEncoder.encode(msgmap.get(MapKeys.PARAM1)==null?"":msgmap.get(MapKeys.PARAM1)==null?"":msgmap.get(MapKeys.PARAM1).toString()),//21
				URLEncoder.encode(msgmap.get(MapKeys.PARAM2)==null?"":msgmap.get(MapKeys.PARAM2)==null?"":msgmap.get(MapKeys.PARAM2).toString()),//22
				URLEncoder.encode(msgmap.get(MapKeys.PARAM3)==null?"":msgmap.get(MapKeys.PARAM3)==null?"":msgmap.get(MapKeys.PARAM3).toString()),//23
				URLEncoder.encode(msgmap.get(MapKeys.PARAM4)==null?"":msgmap.get(MapKeys.PARAM4)==null?"":msgmap.get(MapKeys.PARAM4).toString()),//24
				URLEncoder.encode(msgmap.get(MapKeys.ROUTE_CLASS)==null?"":msgmap.get(MapKeys.ROUTE_CLASS).toString()),//25
				URLEncoder.encode(msgmap.get(MapKeys.ATTEMPT_TYPE)==null?"0":msgmap.get(MapKeys.ATTEMPT_TYPE).toString()),//26
				URLEncoder.encode(msgmap.get(MapKeys.TOTAL_MSG_COUNT)==null?"":msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString()),//27
				URLEncoder.encode("1"),//28
				URLEncoder.encode(statusid),//29
				URLEncoder.encode(msgmap.get(MapKeys.STATUSID_ORG)==null?"":msgmap.get(MapKeys.STATUSID_ORG).toString()),//30
				URLEncoder.encode(msgmap.get(MapKeys.SMSCID)==null?"":msgmap.get(MapKeys.SMSCID).toString()),//31
				URLEncoder.encode(msgmap.get(MapKeys.STATUSID)==null?"":msgmap.get(MapKeys.STATUSID).toString()),//32
				URLEncoder.encode(msgmap.get(MapKeys.ENTITYID)==null?"":msgmap.get(MapKeys.ENTITYID).toString()),//33
				URLEncoder.encode(msgmap.get(MapKeys.TEMPLATEID)==null?"":msgmap.get(MapKeys.TEMPLATEID).toString()),//34

		};
		
		String dlrurl=MessageFormat.format(DLR_URL, params);
		msgmap.put(MapKeys.DLR_URL, dlrurl);
		
		
	}
	

	private void gotosleep() {
		
		try{

			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
}
