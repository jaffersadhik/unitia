package unitiadnreceiver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.processor.DNProcessor;
import com.winnovature.unitia.util.redis.OtpMessageDNRegister;
import com.winnovature.unitia.util.redis.QueueSender;

import unitiaroute.ReRouting;

public class DNReceiverProcessor {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Map<String,Object> logmap=new HashMap();
		  
		Map<String,Object> msgmap=null;
		try{
			msgmap =getMap(request);
			logmap.putAll(msgmap);
        String dr=msgmap.get(MapKeys.DR).toString();
        
        if(!dr.equals("ACK/")){
        msgmap.put(MapKeys.DR,URLDecoder.decode(dr));
        
		new DNProcessor(msgmap,logmap).parseDliveryReceipt(msgmap);
		msgmap.put(MapKeys.INSERT_TYPE, "dn");

		if(!msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString().equals("1")){
			
				if(!msgmap.get(MapKeys.SPLIT_SEQ).toString().equals("1")){
					
					return;
				}

			
		}
		
		
		registerOTPDNMessage(msgmap);
        
        doDNRetry(msgmap,logmap);
     
        logmap.putAll(msgmap);

        sendToQ(msgmap,logmap);

        }
        response.getWriter().println("Ok");
        logmap.put("module","dnreceiver");
		logmap.put("logname", "dnreceiver");

        
        new FileWrite().write(logmap);
	
		}catch(Exception e){
			
			new FileWrite().logError("dnreceiver", msgmap, e);
			
		}
	}
	
	private boolean isDelayDN(Map<String, Object> msgmap) {
		
		long diff=0;
		try{
			diff=System.currentTimeMillis()-Long.parseLong(msgmap.get(MapKeys.KTIME).toString());
		}catch(Exception e){
			
		}
		return diff>(5*60*1000);
	}

	private void sendToQ(Map<String, Object> msgmap, Map<String, Object> logmap) {
		
	    new QueueSender().sendL("dnreceiverpool", msgmap, false,logmap);
		
	}
	
	/*
	private void sendToSplitupDNQ(Map<String, Object> msgmap, Map<String, Object> logmap) {
		
	    new QueueSender().sendL("dndelaypool", msgmap, false,logmap);
		
	}*/

	private void doDNRetry(Map<String, Object> msgmap1,Map<String,Object> logmap) {
		
		if(msgmap1.get(MapKeys.ATTEMPT_TYPE).toString().equals("0") && PushAccount.instance().getPushAccount(msgmap1.get(MapKeys.USERNAME).toString()).get(MapKeys.DN_RETRY_YN).equals("1")){
			
			Map<String,Object> msgmap=new HashMap(msgmap1);
			
			
			String smscid=ReRouting.getInstance().getReRouteSmscid(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.SMSCID_ORG).toString());

			msgmap1.put("founding reroute smscid", smscid);
			
			msgmap.put(MapKeys.SMSCID, smscid);

			if(isFailureErrorCode(msgmap)&&FeatureCode.isDNRetry(msgmap.get(MapKeys.FEATURECODE).toString())&&smscid!=null){
			
				msgmap1.put("sending to dnretrypool", "yes");

				new QueueSender().sendL("dnretrypool", msgmap, false,logmap);

			}
		}
		
	}

	private boolean isFailureErrorCode(Map<String, Object> msgmap) {

		if( (msgmap.get(MapKeys.CARRIER_ERR)!=null && !msgmap.get(MapKeys.CARRIER_ERR).toString().equals("000"))){
			
			return true;
		}
	
		String statusidorg=(String)msgmap.get(MapKeys.STATUSID_ORG);
		String statusid=(String)msgmap.get(MapKeys.STATUSID);
		
		if(statusid!=null&&(""+MessageStatus.KANNEL_RESPONSE_FAILED).equals(statusid)){
			
			if(statusidorg!=null && !statusidorg.equals("401") ){
				
				return true;
			}
		}
		
		return false;
	}

	private void registerOTPDNMessage(Map<String, Object> msgmap) {
		
		if(isOtpDN(msgmap)){
		
			new OtpMessageDNRegister().register(msgmap.get(MapKeys.MSGID).toString());
		}
		
	}

	private boolean isOtpDN(Map<String, Object> msgmap) {
		
		return (msgmap.get(MapKeys.ATTEMPT_TYPE).toString().equals("0") && PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.OTP_RETRY_YN).equals("1"));
	}

	private Map<String, Object> getMap(HttpServletRequest request) {
		
        final StringTokenizer st = new StringTokenizer(request.getQueryString(), "&");
        final HashMap reqParam = new HashMap();
        while (st.hasMoreTokens())
        {
            final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
            String key = "";
            String value = "";
            if (st2.hasMoreTokens())
            {
                key = st2.nextToken();
                if (st2.hasMoreTokens())
                {
                    value = st2.nextToken();
                }
            }
            reqParam.put(key, value);
        }
        return reqParam;
    }
		
}
