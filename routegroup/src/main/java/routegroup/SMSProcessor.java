package routegroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.misc.SpecialCharacters;
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



	public void doFeatureCodeIndentification()  throws Exception{

		if(isfurtherprocess){

			if(msgmap.get(MapKeys.FEATURECODE)==null){
				
				String msgtype=msgmap.get(MapKeys.MSGTYPE).toString();
				
				if(msgtype.equals(MessageType.EM)||msgtype.equals(MessageType.PEM)||msgtype.equals(MessageType.EF)){
					
					if(isSplitRequired()){
						
						if(msgtype.equals(MessageType.EM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EMC);

						}else if(msgtype.equals(MessageType.PEM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PEMC);

							
						}else if(msgtype.equals(MessageType.EF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EFC);

							
						}

					}else{
						
						if(msgtype.equals(MessageType.EM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EMS);

						}else if(msgtype.equals(MessageType.PEM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PEMS);

							
						}else if(msgtype.equals(MessageType.EF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EFS);

							
						}
					}
				}else{
			
					int messagelength=msgmap.get(MapKeys.FULLMSG).toString().length();
					
					if(messagelength>280){
						
						if(msgtype.equals(MessageType.UM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UMC);
							
						}else if(msgtype.equals(MessageType.UF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UFC);

						}else if(msgtype.equals(MessageType.PUM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PUMC);
						}else if(msgtype.equals(MessageType.BM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.BMC);
						}
					}else{
						
						if(msgtype.equals(MessageType.UM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UMS);
							
						}else if(msgtype.equals(MessageType.UF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UFS);

						}else if(msgtype.equals(MessageType.PUM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PUMS);
						}else if(msgtype.equals(MessageType.BM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.BMS);
						}
					}
				}
				
				if(msgmap.get(MapKeys.FEATURECODE)==null){
					
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.UNEBALE_TO_PREDICT_FEATURECODE);
				
					isfurtherprocess=false;
				}
			}
					
					
		}
		return ;
	}
	
	
	

	private boolean isSplitRequired()  throws Exception
	{
		int msgLength = msgmap.get(MapKeys.FULLMSG).toString().length();
		int noSplChar = 0;
		
		char strChar[] = msgmap.get(MapKeys.FULLMSG).toString().toCharArray();
		List splCharList = SpecialCharacters.instance().getSplCharacters();
		
		for(int i=0; i < strChar.length; i++)
		{
			if(splCharList.contains(""+strChar[i]))
			{
				noSplChar++;
			}
		}
		
	
		int totLength = (msgLength + noSplChar);
		
		
		if(totLength > 160)
			return true;
		else
			return false;
		
	}


	public void doRouteGroupAvailable() throws Exception {
		

		if(isfurtherprocess){

		String username=msgmap.get(MapKeys.USERNAME).toString();
		String superadmin=PushAccount.instance().getPushAccount(username).get(MapKeys.SUPERADMIN);
		String admin=PushAccount.instance().getPushAccount(username).get(MapKeys.ADMIN);

		String operator=(String)msgmap.get(MapKeys.OPERATOR);
		String circle=(String)msgmap.get(MapKeys.CIRCLE);

		String routegroup=MobileRouting.getInstance().getRouteGroup(msgmap.get(MapKeys.MOBILE).toString(), msgmap.get(MapKeys.ROUTECLASS).toString());
		
		if(routegroup!=null&&routegroup.trim().length()>0) {
			
			msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
			
			msgmap.put(MapKeys.ROUTEKEY, "mobile");
			
			msgmap.put(MapKeys.ROUTELOGIC, "0");
			
			return ;
			
		}
		
		if(!msgmap.get(MapKeys.COUNTRYCODE).toString().startsWith("91")){


			for(int logic=1;logic<9;logic++) {
				
				String key=getKey(superadmin,admin,username,msgmap.get(MapKeys.COUNTRYCODE).toString(),logic);
				
				routegroup=InternationalRoute.getInstance().getRouteGroup(key);
				
				if(routegroup!=null&&routegroup.trim().length()>0) {
					
					msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
					
					msgmap.put(MapKeys.ROUTEKEY, key);
					
					msgmap.put(MapKeys.ROUTELOGIC, "11"+logic);
					
					return ;
					
				}else{
					
					msgmap.put(MapKeys.ROUTEKEY, key);

				}
				
			}

		}
		
		if(msgmap.get(MapKeys.COUNTRYCODE).toString().startsWith("91")){

			for(int logic=1;logic<5;logic++) {
				
				String key=getKey(operator,circle,msgmap.get(MapKeys.SENDERID).toString(),logic);
				
				routegroup=SenderidRouting.getInstance().getRouteGroup(key, msgmap.get(MapKeys.SENDERID).toString());
				
				if(routegroup!=null&&routegroup.trim().length()>0) {
					
					msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
					
					msgmap.put(MapKeys.ROUTEKEY, key);
					
					msgmap.put(MapKeys.ROUTELOGIC, "12"+logic);
					
					return ;
					
				}else{
					
					msgmap.put(MapKeys.ROUTEKEY, key);

				}
			}

		for(int logic=1;logic<17;logic++) {
			
			String key=getKey(superadmin,admin,username,operator,circle,logic);
			
			routegroup=Route.getInstance().getRouteGroup(key,msgmap.get(MapKeys.ROUTECLASS).toString());
			
			if(routegroup!=null&&routegroup.trim().length()>0) {
				
				msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
				
				msgmap.put(MapKeys.ROUTEKEY, key);
				
				msgmap.put(MapKeys.ROUTELOGIC, ""+logic);
				
				return ;
				
			}else{
				
				msgmap.put(MapKeys.ROUTEKEY, key);

			}
		}
		}
	
		isfurtherprocess=false;

		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.ROUTEGROUP_NOT_FOUND);
	
		}
		
		
		return ;
	}


	

	private String getKey(String operator, String circle, String senderid, int logic) {

		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		}
		
		return "";
		
	}


	private String getKey(String superadmin,String admin,String username, String operator, String circle, int logic) {

		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 5:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 6:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 7:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 8:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 9:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 10:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 11:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 12:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 13:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 14:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 15:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 16:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
	
				 
		
		}
		
		return "";
	}


	private String getKey(String superadmin,String admin,String username, String countrycode, int logic) {

		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 5:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 6:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 7:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 8:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
			
		}
		
		return "";
	}



	public void sentToNextLevel() throws Exception {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.putAll(msgmap);
		logmap.put("module", "SMSProcessor");
		logmap.put("logname", logname);
	
		if(isfurtherprocess){
			

				sendTOCommonPool("commonpool",logmap);

				
			
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
