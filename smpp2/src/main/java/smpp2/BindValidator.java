package smpp2;

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
	
	
	private String getBindType(BaseBind bindRequest){
		

		String bindType="TRX";
		if(SmppConstants.CMD_ID_BIND_TRANSCEIVER==bindRequest.getCommandId()){
			bindType="TRX";

		}else if(SmppConstants.CMD_ID_BIND_TRANSMITTER==bindRequest.getCommandId()){
		
			bindType="TR";


		}else if(SmppConstants.CMD_ID_BIND_RECEIVER==bindRequest.getCommandId()){
		
			bindType="RX";

		}
		
		return bindType;
	}
	
	public void validate(BaseBind bindRequest, String hostip, Map<String, Object> logmap) throws SmppProcessingException {
		
				String systemId = bindRequest.getSystemId().toLowerCase();
		    	String password = bindRequest.getPassword();
    			String bindType=getBindType(bindRequest);

    			logmap.put("username", systemId);
    			logmap.put("logname", "bindlog_"+systemId);    			
    			logmap.put("bind Type",bindType );

	    		doUserNameValidation(systemId,logmap);
	    		
	    		doPasswordValidation(systemId,password,logmap);
		    	
	    		doIpWhiteListValidation(systemId,hostip,logmap);
	    		
	    		doSessionCountValidation(systemId,logmap);
	    		
	}
	
	
	private void doUserNameValidation(String systemId,Map<String, Object> logmap) throws SmppProcessingException {
		if(systemId == null || systemId.trim().length() == 0 || PushAccount.instance().getPushAccount(systemId)==null) {

			logmap.put("status", "Invalid System ID : systemid is :"+systemId);
			throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
		}

		
	}

	private void doPasswordValidation(String systemId,String password,Map<String, Object> logmap) throws SmppProcessingException 
	{
	
		String dbpassword=PushAccount.instance().getPushAccount(systemId).get(MapKeys.PASSWORD);
		
    	if(password == null || password.trim().length() == 0 || dbpassword ==null || !dbpassword.equals(password)) {
			logmap.put("status", "Invalid Password : systemid is :"+systemId+" given password is : "+password);
    		throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
    	}

	}
	
	private void doIpWhiteListValidation(String systemId,String hostip,Map<String, Object> logmap) throws SmppProcessingException {
		
		if(!WhiteListedIP.getInstance().isWhiteListedIP(systemId, hostip)){
			logmap.put("status", "Invalid Whitelisted IP : systemid is :"+systemId+" given ip is : "+hostip);

    		throw new SmppProcessingException(MessageStatus.INVALID_IP);

    	}
    	

	}
	
	private void doSessionCountValidation(String systemId,Map<String, Object> logmap) throws SmppProcessingException{
		

		int maxbind=Integer.parseInt(PushAccount.instance().getPushAccount(systemId).get(MapKeys.SMPP_MAXBIND));
    	int currentbind=SessionCount.getInstance().getCount(systemId);
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
