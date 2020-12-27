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


public class CreditBalanceProcessor 
{
	Map<String,Object> msgmap=null;
	
	public CreditBalanceProcessor(){
		
	}
	
	public String processRequest( HttpServletRequest request,Map<String,Object> msgmap)
	{		
			this.msgmap=msgmap;
			String password		=	request.getParameter(IHTTPParams.PIN);
			String username		=	request.getParameter(IHTTPParams.USERNAME);
			String custIP		=	request.getHeader("X-FORWARDED-FOR");

			if(username==null){
				username="";
			}
			if(custIP==null){
				custIP=request.getRemoteHost();
			}
			
			if(custIP==null){
				custIP="";
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
			
					
			return getWhitelableAcceptedResponse();
		
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
		try {
			status.put("balance_credits",new CreditProcessor().getBalance(msgmap.get(MapKeys.USERNAME).toString()) );
		} catch (Exception e) {
			status.put("balance_credits","0" );

		}

		

		return ToJsonString.toString(status);
	}

	
			
		
	
}
