package simulatar.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.type.SmppProcessingException;



public class BindValidator {
	
	final static String className="[Authenticator]";
	
	static List<String> users=new ArrayList<String>();
	
	static{
		users.add("smppclient1");
		users.add("smppclient2");
		users.add("smppclient3");
		users.add("smppclient4");
		users.add("smppclient5");
		users.add("smppclient6");
		users.add("smppclient7");
		users.add("smppclient8");
		users.add("smppclient9");
		users.add("smppclient10");
		users.add("smppclient11");
		users.add("smppclient12");
		users.add("smppclient13");
		users.add("smppclient14");
		users.add("smppclient15");
	}
	
	public void validate(BaseBind bindRequest, String hostip, Map<String, Object> logmap) throws SmppProcessingException {
		
				String systemId = bindRequest.getSystemId().toLowerCase();
		    	String password = bindRequest.getPassword();
    			logmap.put("username", systemId);

	    		if(systemId == null || systemId.trim().length() == 0 || !users.contains(systemId)) {

	    			logmap.put("status", "Invalid System ID : systemid is :"+systemId);
	    			throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
	    		}

	    		
		    	if(password == null || password.trim().length() == 0 ||  !password.equals("password")) {
	    			logmap.put("status", "Invalid Password : systemid is :"+systemId+" given password is : "+password);
		    		throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
		    	}
		    	
	}

	
 
	
}
