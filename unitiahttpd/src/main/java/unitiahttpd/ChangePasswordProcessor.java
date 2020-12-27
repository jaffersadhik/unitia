package unitiahttpd;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.WhiteListedIP;
import com.winnovature.unitia.util.http.IHTTPParams;
import com.winnovature.unitia.util.misc.CreditProcessor;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.misc.WinDate;


public class ChangePasswordProcessor 
{
	Map<String,Object> msgmap=null;
	
	public ChangePasswordProcessor(){
		
	}
	
	public String processRequest( HttpServletRequest request,Map<String,Object> msgmap)
	{		
			this.msgmap=msgmap;
			String password		=	request.getParameter(IHTTPParams.PIN);
			String username		=	request.getParameter(IHTTPParams.USERNAME);
			String custIP		=	request.getHeader("X-FORWARDED-FOR");
			String newpassword		=	request.getParameter("newpassword");
			String confirmpassword		=	request.getParameter("confirmpassword");

			
			if(custIP==null){
				custIP=request.getRemoteHost();
			}
			
			if(custIP==null){
				custIP="";
			}
			
			if(newpassword==null){
				newpassword="";
			}
			
			if(confirmpassword==null){
				confirmpassword="";
			}
			
			if(username==null){
				username="";
			}
			
			msgmap.put(MapKeys.USERNAME, username.toLowerCase());

			Map<String,String> partnerMap =PushAccount.instance().getPushAccount(username);
			if(partnerMap == null || partnerMap.isEmpty())
				return getRejectedResponse(MessageStatus.INVALID_USERNAME);// invalid credentials

		
		
			
			if(password==null||!password.equals(partnerMap.get("password"))){
			
				return getRejectedResponse(MessageStatus.INVALID_PASSWORD);// invalid credentials

			}
		
		

			if(!WhiteListedIP.getInstance().isWhiteListedIP(msgmap.get(MapKeys.USERNAME).toString(), custIP)){
				
				return getRejectedResponse(MessageStatus.INVALID_IP);
			}
			
			if(newpassword.length()<1||newpassword.length()>8){
				
				return getRejectedInvalidPassword();
			}
			
			if(!newpassword.equals(confirmpassword)){
				
				return getRejectedPasswordMismatch();

			}
				
			if(newpassword.equals(password)){
				
				return getRejectedPasswordSame();

			}
			
			PushAccount.instance().changePassword(username, password, newpassword);
			
			return getWhitelableAcceptedResponse();
		
	}
	
	
	private String getRejectedPasswordSame() {

		Map status=new HashMap();
		status.put("code", "303");
		status.put("status", "change Password same as current Password");
		status.put("rtime",new WinDate().getLogDate());
		return ToJsonString.toString(status);
	}

	private String getRejectedPasswordMismatch() {

		Map status=new HashMap();
		status.put("code", "302");
		status.put("status", "change Password and confirm Password mismatch");
		status.put("rtime",new WinDate().getLogDate());
		return ToJsonString.toString(status);

	}

	private String getRejectedInvalidPassword()
	{
	
		Map status=new HashMap();
		status.put("code", "301");
		status.put("status", "Invalid Change Password,Password length greater than one and less than 9");
		status.put("rtime",new WinDate().getLogDate());



		return ToJsonString.toString(status);
	}
	
	private String getRejectedResponse(int statusId)
	{
	
		Map status=new HashMap();
		status.put("code", ""+statusId);
		status.put("status", MessageStatus.getInstance().getStatus(statusId));
		status.put("rtime",new WinDate().getLogDate());



		return ToJsonString.toString(status);
	}
	
	
	private String getWhitelableAcceptedResponse()
	{
		Map status=new HashMap();
		status.put("code", "100");
		status.put("rtime",new WinDate().getLogDate());
		status.put("username", msgmap.get(MapKeys.USERNAME));
		status.put("status","password changed successfully" );


		

		return ToJsonString.toString(status);
	}

	
			
		
	
}
