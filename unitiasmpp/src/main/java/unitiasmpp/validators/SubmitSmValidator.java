package unitiasmpp.validators;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.winnovature.unitia.util.http.Utility;
import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

import unitiasmpp.util.SubmitObject;

public class SubmitSmValidator {
	

	
	
	public SubmitSmValidator() {
	
	
	}
	
	public void validate(SubmitSm request,SubmitSmResp submitResponse,String systemId,String host) {

			Map<String,Object> logmap=new HashMap<String,Object>();
			Map<String,Object> msgmap=new HashMap<String,Object>();

			Bean.setDefaultValues(msgmap);
			msgmap.put(MapKeys.PROTOCOL, "smpp");
    		submitResponse.setMessageId(msgmap.get(MapKeys.ACKID).toString());
	    		
	    	String strDestAddr = request.getDestAddress().getAddress();
	    		
	    	strDestAddr=validateDestinationAddress(systemId,strDestAddr,submitResponse);
	    	
	    	msgmap.put(MapKeys.MOBILE, strDestAddr);
			msgmap.put(MapKeys.USERNAME, systemId);
			msgmap.put(MapKeys.CUSTOMERIP, host);

	    		//if valid destination address
	    		if(submitResponse.getCommandStatus()==0) {
	    			
	    				//extract request object
		    			SubmitObject so = new SubmitObject(	request,submitResponse);
		    			so.setValues(msgmap);

		    			int flag =	new Utility().validateScheduleBlockout((String)msgmap.get(MapKeys.SCHEDULE_TIME_STRING), msgmap);
						//if valid time to process persist for request router or concatenated processor processing
						if(flag==0) {
							deliverSubmitSm(msgmap,submitResponse,logmap);
						} else {
							submitResponse.setCommandStatus(SmppConstants.STATUS_INVSCHED);

						}
		        	
	        	}	

	    		logmap.putAll(msgmap);
	    		
				logmap.put("logname", "smpp_submit");

	    		logmap.put("smpp status ", submitResponse.getCommandStatus()+" ");
	    		
	    		new FileWrite().write(logmap);
	
		}
	
		
		private String validateDestinationAddress(String systemid,String strDestAddr,SubmitSmResp submitResponse) {
			
    		if( strDestAddr == null || strDestAddr.trim().length() < 8 
    				|| strDestAddr.trim().length() > 15 || StringUtils.isNumeric(StringUtils.stripStart(strDestAddr, "+"))==false)	{              						
		
    			submitResponse.setCommandStatus(SmppConstants.STATUS_INVDSTADR);
    			
    		}  else {//format destination address appending 91 if required
    			
    			strDestAddr = StringUtils.stripStart(strDestAddr, "+");
        		strDestAddr = StringUtils.stripStart(strDestAddr, "0");
        		
        
        		
        		strDestAddr = new com.winnovature.unitia.util.misc.Utility().prefix91(systemid, strDestAddr ); 

    		}
    		
    		return strDestAddr;
		}
		
		private void deliverSubmitSm(Map<String,Object> msgmap,SubmitSmResp submitResponse,Map<String,Object> logmap) {
				
				
					try
					{
						boolean pushed=false;
						
						pushed = new Utility().sendQueue(msgmap,logmap);
						
						
						if(!pushed) {							
							submitResponse.setCommandStatus(SmppConstants.STATUS_SYSERR);	
						}
						
					}
					catch(Exception e)
					{
						submitResponse.setCommandStatus(SmppConstants.STATUS_SYSERR);
					}		      
			
	
		}
		
	    
}
