package countrycode;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;


public class SMSProcessor {
	
	String logname=null;
	Map<String,Object> msgmap=null;
	
	private boolean isfurtherprocess=true;
	
	public SMSProcessor(){
		
	}
	public SMSProcessor(String logname,Map<String,Object> msgmap,boolean isfurtherprocess){
		
		this.logname=logname;
		this.msgmap=msgmap;
		this.isfurtherprocess=isfurtherprocess;
	}


	public void doCountryCodeCheck() throws Exception{
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE).toString();

			if(mobile.startsWith("91")&&mobile.length()==12){
				
				msgmap.put(MapKeys.COUNTRYCODE, "91");
				
				String countryname=Countrycode.getInstance().getCountryName("91");

				msgmap.put(MapKeys.COUNTRYNAME, countryname);

				return ;
			}
			
		
			if(mobile.trim().length()<7){
		
				isfurtherprocess=false;
				
				msgmap.put(MapKeys.STATUSID,""+MessageStatus.INVALID_DESTINATION_ADDRESS);

				return;
			}
			
			for(int i=7;i>0;i--) {
				
				String series =mobile.substring(0, i);

				String countryname=Countrycode.getInstance().getCountryName(series);
				
				if(countryname!=null){
					
					msgmap.put(MapKeys.COUNTRYCODE, series)	;
					msgmap.put(MapKeys.COUNTRYNAME, countryname);
					msgmap.put(MapKeys.INTL, PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.INTL).toString());

					if(!series.startsWith("91")){
						
						if(msgmap.get(MapKeys.INTL).toString().equals("0")){
							
							isfurtherprocess=false;
							
							msgmap.put(MapKeys.STATUSID,""+MessageStatus.INTL_DELIVERY_DISABLED);
						}
					}
					return ;
				}
			}
			
			isfurtherprocess=false;
			
			msgmap.put(MapKeys.STATUSID,""+MessageStatus.INVALID_COUNTRYCODE);

		}
		
		return ;
	}


	

	
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(1000L);
			
		}catch(Exception e){
			
		}
		
	}

	public void sentToNextLevel() throws Exception {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.putAll(msgmap);
		logmap.put("module", "SMSProcessor");
		logmap.put("logname", logname);
	
		if(isfurtherprocess){
			
		
				sendTOCommonPool("numberingplan",logmap);
					
		}else{

			
		
			doBilling(logmap);
			

		}
		
		new FileWrite().write(logmap);

		
	}

	
	
	
	
		



	
	private void doBilling(Map<String,Object> logmap)  throws Exception {
	
			String queuename="submissionpool";
			
			String smscid="";
			
			if(msgmap.get(MapKeys.SMSCID)!=null){
				
				smscid=msgmap.get(MapKeys.SMSCID).toString();
			}
			
			if(smscid.equals("apps")||smscid.equals("reapps")){
				
				queuename="appspool";
			}
			if(new QueueSender().sendL(queuename, msgmap, false, logmap)){
				
				logmap.put("sms processor status", "Message Sent to billing Queue Successfully");
			
			}else{
				
				logmap.put("sms processor status", "Message Sent to billing Queue Failed message will be loss");

			}
		
		
		
	}
	
	private void sendTOCommonPool(String queuename,Map<String,Object> logmap)  throws Exception{
		
	
		
		if(new QueueSender().sendL(queuename, msgmap, false, logmap)){
			
			logmap.put("sms processor status", "Message Sent to "+queuename+" Queue Successfully");
		
		}else{
			
			logmap.put("sms processor status", "Message Sent to "+queuename+" Queue Failed message will be loss");

		}
	
	
	
}

}
