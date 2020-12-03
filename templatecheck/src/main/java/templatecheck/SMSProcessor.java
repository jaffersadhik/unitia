package templatecheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.Convertor;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
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
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(1000L);
			
		}catch(Exception e){
			
		}
		
	}

	
	
	public void doAllowedSMSPatternCheck() throws Exception {
		

		String msgclass=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.MSGCLASS);

		
		if(isfurtherprocess){

			String templateid=(String)msgmap.get(MapKeys.TEMPLATEID);

			if(msgclass!=null&&msgclass.equals("3")){
				
				msgmap.put(MapKeys.ROUTECLASS, "1");
				
				if(templateid!=null&&templateid.trim().length()>0){
					
					msgmap.put(MapKeys.DLT_TYPE, "customer");
					msgmap.put(MapKeys.ROUTECLASS, "1");

				}
				return ;
				
			}

			

			if(templateid!=null&&templateid.trim().length()>0){
				
				msgmap.put(MapKeys.DLT_TYPE, "customer");
				msgmap.put(MapKeys.ROUTECLASS, "1");

				return ;
			}
			
			String fullmsg=(String)msgmap.get(MapKeys.FULLMSG);
			

			try{
				if(MessageType.isHexa( (String)msgmap.get(MapKeys.MSGTYPE))){
					
					fullmsg=Convertor.getMessage(fullmsg);
				}
			}catch(Exception e){
				
			}
			List<Map<String,String>>  patternset=SMSPatternAllowed.getInstance().getAllowedPaternSet(msgmap.get(MapKeys.USERNAME).toString());
			

			if(patternset!=null){
			
			for(int i=0,max=patternset.size();i<max;i++){
			
				Map<String,String> data=patternset.get(i);
				String spamPattern=data.get("smspattern");
			

				
					boolean status=Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(fullmsg).matches();
				if(status)
				{
					msgmap.put(MapKeys.ROUTECLASS, "1");
					msgmap.put(MapKeys.ALLOWED_PATTERN_ID, data.get("pattern_id"));
					msgmap.put(MapKeys.TEMPLATEID, data.get("pattern_id"));
					msgmap.put(MapKeys.DLT_TYPE, "unitia");

					return ;
				}
				
				
			}
			
			}
		}
		

		String promorejectyn=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.PROMO_REJECT_YN);

		
		if(msgclass!=null&&msgclass.equals("1")&&promorejectyn!=null&&promorejectyn.equals("1")){
			
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.PROMO_DELIVERY_DISBALED);
			
			isfurtherprocess=false;
			
		}
		msgmap.put(MapKeys.ROUTECLASS, "2");

		return ;
	
		
	}

	public void sentToNextLevel() throws Exception {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.putAll(msgmap);
		logmap.put("module", "SMSProcessor");
		logmap.put("logname", logname);
	
		if(isfurtherprocess){
			
			String duplicatetype=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.DUPLICATE_TYPE);
			
			if(duplicatetype.equals("1")){
				sendTOCommonPool("duplicate",logmap);
			}else{
				sendTOCommonPool("commonpool",logmap);

			}
			
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
