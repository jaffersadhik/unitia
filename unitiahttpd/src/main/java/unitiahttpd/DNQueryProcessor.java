package unitiahttpd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.WhiteListedIP;
import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.http.IHTTPParams;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.misc.WinDate;



public class DNQueryProcessor 
{
	Map<String,Object> msgmap=null;
	Map<String,Object> logmap=null;
	
	public DNQueryProcessor(){
		
	}
	
	public String processRequest(HttpServletRequest request,Map<String,Object> msgmap,Map<String,Object> logmap) throws UnsupportedEncodingException
	{		
		
			try{
		String[] splittedMnumber = null;
		
			this.logmap=logmap;
			this.msgmap=msgmap;
		
			boolean isEmail = false;
			//RequestObject requestObj	=	null;
			String password		=	request.getParameter(IHTTPParams.PIN);
			String username		=	request.getParameter(IHTTPParams.USERNAME);
			String ackid		=	request.getParameter(MapKeys.ACKID);
			String custIP		=	request.getHeader("X-FORWARDED-FOR");
			if(custIP==null){
				custIP=request.getRemoteHost();
			}
			
			if(custIP==null){
				custIP="";
			}
			msgmap.put(MapKeys.ACKID, ackid);
			msgmap.put(MapKeys.CUSTOMERIP, custIP);
		    msgmap.put(MapKeys.CONTENT_LENGTH,request.getContentLength());
			
			logHeader(request,logmap);
			
			if(username==null){
				username="";
			}
			msgmap.put(MapKeys.USERNAME, username.toLowerCase());
			

			Map<String,String> partnerMap =PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
			if(partnerMap == null || partnerMap.isEmpty()){
				return getRejectedResponse(MessageStatus.INVALID_USERNAME);// invalid credentials
			}
			if(password==null||!password.equals(partnerMap.get("password"))){
			
				logmap.put("partnerMap ", partnerMap.toString());
				logmap.put("income password", password);
				return getRejectedResponse(MessageStatus.INVALID_PASSWORD);// invalid credentials

			}
			
			if(!WhiteListedIP.getInstance().isWhiteListedIP(msgmap.get(MapKeys.USERNAME).toString(), custIP)){
				logmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_IP);
				logmap.put("customerIP ",custIP);
				return getRejectedResponse(MessageStatus.INVALID_IP);
			}
			msgmap.put(MapKeys.SUPERADMIN,PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SUPERADMIN));
			msgmap.put(MapKeys.ADMIN,PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.ADMIN));

			/* Identifying account is Active/De-Active */
			String accStatus = partnerMap.get("status");
			if(accStatus.equals("0")) {
				return getRejectedResponse(MessageStatus.ACCOUNT_INACTIVATED);// Account inactivated
			}
			
			
				
			List<Map<String,String>> msglist=getMessageList(ackid);	
			
			Map<String,Object> result=new HashMap<String,Object>();
			
			result.put("ackid", ackid);
			result.put("username", username);

			if(msglist!=null&&msglist.size()>0){
				
				result.put("code", "100");
				result.put("status", "Record Available in msglist Attribute");
				result.put("msglist", msglist);

			}else{
				result.put("code", "99");
				result.put("status", "Record Not Available in Database");

			}
		
			return getWhitelableAcceptedResponse(result);

			}catch(Exception e){
				
				return getSystemErrorResponse(e);

			}
		
	}
	
	
	private List<Map<String, String>> getMessageList(String ackid) {
		
		List<Map<String, String>> result=new ArrayList<Map<String, String>>();
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			String sql="select rlog.msgid,rlog.senderid rsenderid,rlog.mobile,rsub.senderid ssenderid,rlog.rtime,rsub.statusid sstatusid,rdlv.carrier_dtime,rdlv.senderid dsenderid,rdlv.statusid dstatusid, rdlv.carrier_stat,rdlv.totalmsgcount dtotalmsgcount,rdlv.credit dcredit,rsub.totalmsgcount stotalmsgcount,rsub.credit scredit from reportlog_requestlog rlog left outer join reportlog_submit rsub on rlog.msgid=rsub.msgid left outer join  reportlog_delivery rdlv on rlog.msgid=rdlv.msgid where rlog.ackid=?";
			connection=BillingDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(sql);
			statement.setString(1, ackid);
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String msgid=resultset.getString("msgid");
				String senderid=resultset.getString("dsenderid");
				String mobile=resultset.getString("mobile");
				long rtime=resultset.getTimestamp("rime").getTime();
				String ctimestring=null;
				String credit=resultset.getString("dcredit");
				String totalmsgcount=resultset.getString("dtotalmsgcount");
				String stat=resultset.getString("carrier_stat");
				String statusid=resultset.getString("dstatusid");
				
				String statusdescription=null;
				
				if(statusid==null){
					
					statusid=resultset.getString("sstatusid");
					
					if(statusid==null){

						statusid="100";
					}
					
				}
				
				if(statusid.equals("000")){
					
					statusdescription="SMS Delivered to Handset Successfully";
				}else if(statusid.equals("100")){
					statusdescription="SMS Scheduled for Delivery";

				}else{
					
					statusdescription=MessageStatus.getInstance().getDescription(statusid);
				}
				if(credit==null){
					
					credit=resultset.getString("scredit");
				}
				
				if(totalmsgcount==null){
					
					credit=resultset.getString("stotalmsgcount");
				}
				
				Timestamp ctime=resultset.getTimestamp("ctime");
				
				if(ctime!=null){
					ctimestring=""+ctime.getTime();
				}
				if(senderid==null){
				
					senderid=resultset.getString("ssenderid");
					
					if(senderid==null){
						
						senderid=resultset.getString("rsenderid");
					}
				}
				
				Map<String,String> data=new HashMap<String,String>();
				
				data.put("msgid", msgid);
				data.put("mobile", mobile);
				data.put("senderid", senderid);
				data.put("totalmsgcount", totalmsgcount);
				data.put("credit", credit);
				data.put("stat", stat);
				data.put("statusid", statusid);
				data.put("statusdescription", statusdescription);
				data.put("rtime", new WinDate().getLogDate(rtime));

				if(ctimestring!=null){
					
					data.put("dtime", new WinDate().getLogDate(Long.parseLong(ctimestring)));

				}
				
				result.add(data);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);

		}
		return result;
	}

	private void logHeader(HttpServletRequest request, Map<String, Object> msgmap2) {
		
		
		Enumeration<String> head=request.getHeaderNames();
		
		while(head.hasMoreElements()){
			String key=head.nextElement();
			msgmap2.put(key.toLowerCase(), request.getHeader(key));
		}
		
	}

	public void replaceSpace(Map<String, Object> msgmap2) {
		
		
		try {
			String msg=msgmap2.get(MapKeys.FULLMSG).toString();

			String	str = URLEncoder.encode(msg, "UTF-8");
		
	       String replacemsg=URLEncoder.encode(" ", "UTF-8");
	       str=str.replace("%C2%A0", replacemsg);
	       
	       msgmap2.put(MapKeys.FULLMSG, URLDecoder.decode(str, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			
		}
	}

	public String getRejectedResponse(int statusId)
	{
	
		Map status=new HashMap();
		status.put("code", ""+statusId);
		status.put("status", MessageStatus.getInstance().getStatus(statusId));
		status.put("rtime",new WinDate().getLogDate());



		return ToJsonString.toString(status);
	}
	
	
	private String getWhitelableAcceptedResponse(Map<String,Object> result)
	{
	

		return ToJsonString.toString(result);
	}

	
	public String getSystemErrorResponse(Exception e)
	{
		Map status=new HashMap();
		status.put("ackid", msgmap.get(MapKeys.ACKID));
		status.put("code", "-1");
		status.put("status", "Invalid Request");
		status.put("rtime",new WinDate().getLogDate());
		status.put("error", ErrorMessage.getMessage(e));

		

		return ToJsonString.toString(status);
	}
	

	
}
