package unitiasmpp.validators;

import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.type.SmppProcessingException;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.WhiteListedIP;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.SmppBind;



public class BindValidator {
	
	final static String className="[Authenticator]";
	
	public void validate(BaseBind bindRequest, String hostip, Map<String, Object> logmap) throws SmppProcessingException {
		
				String systemId = bindRequest.getSystemId().toLowerCase();
		    	String password = bindRequest.getPassword();
    			logmap.put("username", systemId);

    			String bindType="";
    			
    			if(SmppConstants.CMD_ID_BIND_TRANSCEIVER==bindRequest.getCommandId()){
    				bindType="TRX";

    			}else if(SmppConstants.CMD_ID_BIND_TRANSMITTER==bindRequest.getCommandId()){
    			
    				bindType="TR";


    			}else if(SmppConstants.CMD_ID_BIND_RECEIVER==bindRequest.getCommandId()){
    			
    				bindType="RX";

    			}
	    		if(systemId == null || systemId.trim().length() == 0 || PushAccount.instance().getPushAccount(systemId)==null) {

	    			logmap.put("status", "Invalid System ID : systemid is :"+systemId);
	    			throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
	    		}

	    		systemId=systemId.toLowerCase();
	    		String dbpassword=PushAccount.instance().getPushAccount(systemId).get(MapKeys.PASSWORD);
	    		
		    	if(password == null || password.trim().length() == 0 || dbpassword ==null || !dbpassword.equals(password)) {
	    			logmap.put("status", "Invalid Password : systemid is :"+systemId+" given password is : "+password);
		    		throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
		    	}
		    	
		    	if(!WhiteListedIP.getInstance().isWhiteListedIP(systemId, hostip)){
	    			logmap.put("status", "Invalid Whitelisted IP : systemid is :"+systemId+" given ip is : "+hostip);

		    		throw new SmppProcessingException(MessageStatus.INVALID_IP);

		    	}
		    	int maxbind=Integer.parseInt(PushAccount.instance().getPushAccount(systemId).get(MapKeys.SMPP_MAXBIND));
		    	int currentbind=SmppBind.getInstance().getBindCount(systemId,bindType);
		    	try {
			    		
		    			if(maxbind<currentbind) {
		    				
			    			logmap.put("status", "Maximum Bind Count Reached : systemid is :"+systemId);

							throw new SmppProcessingException(SmppConstants.STATUS_ALYBND);
							}
		     	} catch(Exception e) {
		     		
		    		logmap.put("status", "System Error : systemid is :"+systemId+" Error Message : "+ErrorMessage.getMessage(e));

		    		if(!(e  instanceof SmppProcessingException)){		    		
		    			throw new SmppProcessingException(SmppConstants.STATUS_SYSERR);		    		
		    		}
		    		throw (SmppProcessingException)e;		    		
		    	}

		    }

	
 
	
}
