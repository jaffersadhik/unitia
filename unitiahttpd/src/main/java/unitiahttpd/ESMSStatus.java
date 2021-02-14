package unitiahttpd;

import java.util.HashMap;
import java.util.Map;

public class ESMSStatus {

	public static int CREDENTIAL_ERROR= 401;
	
	public static int SUBMIT_SUCCESS= 402;

	public static int CREDITS_NOT_AVILABLE= 403;
	
	public static int INTERNAL_DATABASE_ERROR= 404;
	
	public static int INTERNAL_NETWORK_ERROR= 405;
	
	public static int INVALID_OR_DUOLICATE_NUMBER= 406;
	
	public static int NETWORK_ERROR_ON_SMSC_1= 407;
	
	public static int NETWORK_ERROR_ON_SMSC_2= 408;
	
	public static int INTERNAL_LIMIT_EXCEED= 410;

	public static int SENDERID_NOT_APPROVED_1= 411;

	public static int SENDERID_NOT_APPROVED_2= 412;

	public static int SPAM_MESSAGE= 413;
	
	public static int REJECTED_BY_OPERATOR= 414;
	
	public static int SECURE_KEY_NOT_AVAILABLE= 415;

	public static int HASH_NOT_MATCH=416;
	
	public static int DAILY_LIMIT_EXCEEDED=418;
	
	public static Map<String,String> status=new HashMap<String,String>();
	
	static {
		
		status.put("401", "Credentials Error, may be invalid username or password");
		status.put("402", "Submit Success");
		status.put("403", "Credits not available");
		status.put("404", "Internal Database Error");
		status.put("405", "Internal Networking Error or Invalid IP Address");
		status.put("406", "Invalid or Duplicate numbers");
		status.put("407", "Network Error on SMSC");
		status.put("408", "Network Error on SMSC");
		status.put("409", "SMSC response timed out, message will be submitted");
		status.put("410", "Internal Limit Exceeded, Contact support");
		status.put("411", "Sender ID not approved");
		status.put("412", "Sender ID not approved");
		status.put("413", "Suspect Spam, we do not accept these messages.");
		status.put("414", "Rejected by various reasons by the operator such as DND, SPAM");
		status.put("415", "Secure Key not available");

		status.put("416", "Hash doesn’t match");
		status.put("418", "Daily Limit Exceeded ");
	

	}
	
	 
	 public static String get(int statusid){
		 
		 return status.get(""+statusid);
	 }
	 
	 
	 
	

}
