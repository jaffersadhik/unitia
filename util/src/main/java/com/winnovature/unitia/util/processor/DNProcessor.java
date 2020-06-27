package com.winnovature.unitia.util.processor;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dao.DNDAO;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.KannelMessageStatus;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.SlapCalculator;


public class DNProcessor
{
    
	
	Map<String, String> requestmap=null;
    
    public DNProcessor(Map<String, String> requestmap)
    {
    	
    	this.requestmap=requestmap;
    }
    
    public void doProcess()
    {
        
    	String statuscd=requestmap.get(MapKeys.DN_STATUSCD);
    	requestmap.put(MapKeys.ADJUSTMENT_INDICATOR, "0");

    	new SMSProcessor(requestmap)
    	.doCountryCodeCheck()
    	.doNumberingPlan();
    	
    	if(statuscd.equals("32")){
    		
    		requestmap.put(MapKeys.CARRIER_SUBMITTIME, requestmap.get(MapKeys.RTIME));
    		requestmap.put(MapKeys.CARRIER_DONETIME_ORG, requestmap.get(MapKeys.RTIME));
    		requestmap.put(MapKeys.CARRIER_DONETIME, requestmap.get(MapKeys.RTIME));

    	}else if(statuscd.equals("16")){
    		
    		requestmap.put(MapKeys.CARRIER_SUBMITTIME, requestmap.get(MapKeys.KTIME));
    		requestmap.put(MapKeys.CARRIER_DONETIME_ORG, requestmap.get(MapKeys.KTIME));
    		requestmap.put(MapKeys.CARRIER_DONETIME, requestmap.get(MapKeys.KTIME));

    		
    	}else{
    		
    		parseDliveryReceipt();
    		
    		requestmap.put(MapKeys.CARRIER_SUBMITTIME, ""+getTime(requestmap.get(MapKeys.CARRIER_SUBMITDATE)));
    		requestmap.put(MapKeys.CARRIER_DONETIME_ORG, ""+getTime(requestmap.get(MapKeys.CARRIER_DONEDATE)));
    		setTimeAdjust();
    	}
    	
    	setLatencySlap();
    	
    	setPlatformStatuscd();
    	
    	new DNDAO().insert(requestmap);
    
    	Map<String,String> logmap=new HashMap<String,String>();
    	
    	logmap.putAll(requestmap);
    	logmap.put("dn receiver status", "inserted into DN Table");
    	new Log().log(logmap);
    }
    
    
    
    private void setPlatformStatuscd() {
		
    	String statuscd=requestmap.get(MapKeys.DN_STATUSCD);
    	if(statuscd.equals("1")){
    		
    		requestmap.put(MapKeys.STATUSID, "000");
    		requestmap.put(MapKeys.STATUSID_ORG, "000");

    		
    	}else{
    
    		String smscid=requestmap.get(MapKeys.SMSCID);

    		if(statuscd.equals("2")){
    		
    			setMessageStatusId();

    		}else if(statuscd.equals("16")){
    		
    			setErrorCode();    			
    			setMessageStatusId();
    		}
    	}
    	
	}

	private void setErrorCode() {

		String errorcode=KannelMessageStatus.getInstance().getErrorCode(requestmap.get(MapKeys.DR));
		
		if(errorcode==null){
			
			KannelMessageStatus.getInstance().insert(requestmap.get(MapKeys.DR));
			KannelMessageStatus.getInstance().reload();
			errorcode=KannelMessageStatus.getInstance().getErrorCode(requestmap.get(MapKeys.DR));
    		
		}
		
		requestmap.put(MapKeys.CARRIER_ERR, errorcode);
	}

	private void setMessageStatusId() {

		String smscid=requestmap.get(MapKeys.SMSCID);

		String errorcode=requestmap.get(MapKeys.CARRIER_ERR);
		String statusid=MessageStatus.getInstance().getStatusid(smscid,errorcode);
		
		if(statusid==null){
			
			TableExsists table=new TableExsists();
			
			table.insertMessageStatus(smscid.toUpperCase(),errorcode,requestmap.get(MapKeys.CARRIER_STAT));
			MessageStatus.getInstance().reload();
			statusid=MessageStatus.getInstance().getStatusid(smscid,errorcode);
		}
		
		requestmap.put(MapKeys.STATUSID, statusid);
		requestmap.put(MapKeys.STATUSID_ORG, statusid);
	}

	private void setLatencySlap() {

    	long rtime=Long.parseLong(requestmap.get(MapKeys.RTIME));
    	long ktime=Long.parseLong(requestmap.get(MapKeys.KTIME));
    	long dtime=Long.parseLong(requestmap.get(MapKeys.CARRIER_DONETIME));
    	long dtime_org=Long.parseLong(requestmap.get(MapKeys.CARRIER_DONETIME_ORG));
    	long ctime=Long.parseLong(requestmap.get(MapKeys.CARRIER_SUBMITTIME));

    	SlapCalculator slap=new SlapCalculator();
    	
    	int smslatency_org=slap.getSlap(dtime_org-rtime);
    	
    	int smslatency=slap.getSlap(dtime-rtime);
    	
    	int carrierlatency=slap.getSlap(dtime_org-ctime);
    	
    	int paltformlatency=slap.getSlap(ktime-rtime);

    	requestmap.put(MapKeys.SMS_LATENCY, smslatency+"");
    	requestmap.put(MapKeys.SMS_LATENCY_ORG, smslatency_org+"");
    	requestmap.put(MapKeys.CARRIER_LATENCY, carrierlatency+"");    	
    	requestmap.put(MapKeys.PLATFORM_LATENCY, paltformlatency+"");

    	
    }

	private void setTimeAdjust() {
		
    	long ktime=Long.parseLong(requestmap.get(MapKeys.KTIME));
    	long dtime=Long.parseLong(requestmap.get(MapKeys.CARRIER_DONETIME_ORG));
    	long diff=dtime-ktime;
    	
    	long latency=Long.parseLong(PushAccount.instance().getPushAccount(requestmap.get(MapKeys.USERNAME)).get(MapKeys.SMS_LATENCY_ADJUST));
    	Random random =new Random();
    	if(ktime>dtime ){
    		
    		dtime=ktime+random.nextInt(50);
    		
        	requestmap.put(MapKeys.ADJUSTMENT_INDICATOR, "-1");

    		
    	}else if(latency!=0&&diff>latency){
    		
    		dtime=ktime+random.nextInt(50);

        	requestmap.put(MapKeys.ADJUSTMENT_INDICATOR, "1");

    	}
    	
    	requestmap.put(MapKeys.CARRIER_DONETIME, ""+dtime);
    }

	
	
	private long getTime(String drdate) 
	{
    	String dateformat="";
    	
    	if(drdate.trim().length()==10){
    		
    		dateformat="yyMMddHHmm";
    		
    	}else if(drdate.trim().length()==14){
    		
    		dateformat="yyyyMMddHHmmss";

    	}else{
    		
    		String year=drdate.substring(0,4);
    		
    		int yearInt=Integer.parseInt(year);
    		
    		if(yearInt>2019){
    			
        		dateformat="yyyyMMddHHmm";

    		}else{
    			
        		dateformat="yyMMddHHmmss";

    		}
    	}
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		try {
			return sdf.parse(drdate).getTime()	;
		} catch (ParseException e) {
			return System.currentTimeMillis();
		}

	}

	public void parseDliveryReceipt() 
	{		
	
		
		String dr=requestmap.get(MapKeys.DR).toUpperCase();
        String id=null;
        String submitDate=null;
        String doneDate =null;
        String stat=null;
        String err=null;

        if(dr.indexOf("ID:")!=-1)
                        id=dr.substring(dr.indexOf("ID:")+3, dr.indexOf(" ",dr.indexOf("ID:")+3));
        if(dr.indexOf("SUBMIT DATE:")!=-1)
                        submitDate = dr.substring(dr.indexOf("SUBMIT DATE:") + 12, dr.indexOf("DONE DATE:")).trim();
        if(dr.indexOf("DONE DATE:")!=-1)
                        doneDate = dr.substring(dr.indexOf("DONE DATE:") + 10, dr.indexOf("STAT:")).trim();
        if(dr.indexOf("STAT:")!=-1)
                        stat = dr.substring(dr.indexOf("STAT:") + 5, dr.indexOf(" ",dr.indexOf("STAT:") + 5)).trim();
        if(dr.indexOf("ERR:")!=-1)
                        err = dr.substring(dr.indexOf("ERR:") + 4, dr.indexOf(" ",dr.indexOf("ERR:") + 4)).trim();


        
        if(stat.indexOf(":")!=-1)
            stat=stat.substring(0,stat.indexOf(":"));

        
        requestmap.put(MapKeys.CARRIER_MSGID, id);
        requestmap.put(MapKeys.CARRIER_SUBMITDATE, submitDate);
        requestmap.put(MapKeys.CARRIER_DONEDATE, doneDate);
        requestmap.put(MapKeys.CARRIER_STAT, stat);
        requestmap.put(MapKeys.CARRIER_ERR, err);
	}

	
	public static void main(String args[]){
		
		Random random=new Random();
		
		while(true){
			
			System.out.println(random.nextInt(50));
		}
	}
    
}
