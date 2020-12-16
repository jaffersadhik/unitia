package simulatar.validators;

import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.type.SmppProcessingException;



public class BindValidator {
	
	final static String className="[Authenticator]";
	
	public void validate(BaseBind bindRequest, String hostip, Map<String, Object> logmap) throws SmppProcessingException {
		
				String systemId = bindRequest.getSystemId().toLowerCase();
		    	String password = bindRequest.getPassword();
    			logmap.put("username", systemId);

	    		if(systemId == null || systemId.trim().length() == 0 || !systemId.equals("smppclient1")) {

	    			logmap.put("status", "Invalid System ID : systemid is :"+systemId);
	    			throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
	    		}

	    		
		    	if(password == null || password.trim().length() == 0 ||  !password.equals("password")) {
	    			logmap.put("status", "Invalid Password : systemid is :"+systemId+" given password is : "+password);
		    		throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
		    	}
		    	
	}

	
 
	
}
