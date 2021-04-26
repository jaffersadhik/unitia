package com.winnovature.unitia.util.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import com.winnovature.unitia.util.account.DNSuccessMasking;
import com.winnovature.unitia.util.account.LatencySlap;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.Carrier;
import com.winnovature.unitia.util.misc.CreditProcessor;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.KannelMessageStatus;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.SlapCalculator;
import com.winnovature.unitia.util.redis.QueueSender;


public class DNProcessor
{
    
	
	Map<String, Object> msgmap=null;
    
	Map<String,Object> logmap=null;
    public DNProcessor(Map<String, Object> requestmap,Map<String,Object> logmap)
    {
    	this.logmap=logmap;
    	this.msgmap=requestmap;
    }
    
    public void doProcess()
    {
        
    
    	setTimeStamp();
    	
    	doDoneTimeAdjustment();
    	
    	doSubmitTimeAdjustemnt();
    	
    	setLatencySlap();
    	
    	setPlatformStatuscd();
    	
    	doSuccessMasking();
    
    	doCreditReturn();
    	
    	sendtoClientDnQueue();
        	
    	logmap.putAll(msgmap);
		logmap.put("logname", "deliverytable");

    	logmap.put("dn receiver status", "inserted into DN Table");
    	new FileWrite().write(logmap);
    }
    


	private void doCreditReturn() {
		
		   String rollback=(String)PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.CREDIT_ROLLBACK_YN);
	        if(rollback!=null && rollback.equals("1"))
	    	
	        	if(!msgmap.get(MapKeys.STATUSID).toString().equals("000")){
				
	        		String credit=(String)msgmap.get(MapKeys.CREDIT);
	        		if(credit!=null){
	        		
	        			try{
	        		
	        				double creditDouble=Double.parseDouble(credit);
	        				if(creditDouble>0){
	        				new CreditProcessor().returnCredit(msgmap.get(MapKeys.USERNAME).toString(), creditDouble);
	        				msgmap.put(MapKeys.CREDIT, "0.0");
	        				}
	        			}catch(Exception e){
	        				
	        		}
	        	}
	        }
		
	}

    
    
    
    private void doSubmitTimeAdjustemnt() {
		
    	long submittime=Long.parseLong(msgmap.get(MapKeys.CARRIER_SUBMITTIME).toString());
		
    	long donetime=Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME).toString());
    	
    	if(submittime>donetime){
    		
        	long rtime=Long.parseLong(msgmap.get(MapKeys.RTIME).toString());

        	int difference=(int)(donetime-rtime);
        	if(difference<0){
        		difference=2;
        	}
  		   	Random r = new Random();
  		   	try{
  		   	submittime=rtime+r.nextInt(difference);	
  		   	}catch(Exception e){
  		   	submittime=rtime;
  		   	}
  		    msgmap.put(MapKeys.CARRIER_SUBMITTIME, ""+submittime);
  		   	
    	}

	}

	private void doDoneTimeAdjustment() {
		
    	String username=msgmap.get(MapKeys.USERNAME).toString();
    	
    	if(LatencySlap.getInstance().isExsistingUser(username)){

	    	Random random =new Random();

    		LatencySlap.getInstance().incrementTotalCountHistory(username);
    		
    		long donetime=Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME_ORG).toString());
    		
    		long rtime=Long.parseLong(msgmap.get(MapKeys.RTIME).toString());

    		long timedifference=donetime-rtime;
    		
    		if(timedifference<0){

    			donetime=rtime+random.nextInt(50);

    			msgmap.put(MapKeys.CARRIER_DONETIME, ""+donetime);
    		
    			LatencySlap.getInstance().incrementCountHistory(username, "1");
    		
    			return;
        		
    		}else{
    		
        		String slap=LatencySlap.getInstance().getSlap(username, timedifference);
        		
        		int slapInt=Integer.parseInt(slap);
        		
        		if(slapInt==1){
        			
        			LatencySlap.getInstance().incrementCountHistory(username, slap);
        			
        			return;
        			
        		}else{
        			
        			if(slapAvailable(slap)){
        			
            			LatencySlap.getInstance().incrementCountHistory(username, slap);
     			
            			return;
        			}else{
        				
        				doSlapSearchAndAdjust();
        			}
        		}
    		}
    	}
		
	}

	private void doSlapSearchAndAdjust() {
		
		String username=msgmap.get(MapKeys.USERNAME).toString();
		
		int maxslap=LatencySlap.getInstance().getMaxSlap(username);
		
		for(int i=1;i<=maxslap;i++){
			
			String slap=""+i;
			if(slapAvailable(slap)){
				
    			LatencySlap.getInstance().incrementCountHistory(username, slap);

    			adjustDoneTime(slap);
    			
    			return;
			}
		}
		
	}

	private void adjustDoneTime(String slap) {
		
		int randomSecond = LatencySlap.getInstance().getMaixmumRandomSeed(msgmap.get(MapKeys.USERNAME).toString(),slap);

		long rtime=Long.parseLong(msgmap.get(MapKeys.RTIME).toString())+(randomSecond*1000);
	
		msgmap.put(MapKeys.CARRIER_DONETIME, ""+rtime);
	}

	private boolean slapAvailable(String slab) {
		
		boolean result = false;
        
	        
	        Map<String,String> history = LatencySlap.getInstance().getHistory(msgmap.get(MapKeys.USERNAME).toString());
	        
	        double scntD = LatencySlap.getInstance().getTotalCount(msgmap.get(MapKeys.USERNAME).toString());
	        double tcntD = LatencySlap.getInstance().getCount(msgmap.get(MapKeys.USERNAME).toString(),slab);
	        
	        result = (Math.floor(( LatencySlap.getInstance().getParcentage(msgmap.get(MapKeys.USERNAME).toString(),slab) * tcntD)/100)) > Math.floor((scntD));
	        
	       return result;
	}

	private void setTimeStamp() {
    	
    	String statuscd=msgmap.get(MapKeys.DN_STATUSCD).toString();
    	
    	msgmap.put(MapKeys.ADJUSTMENT_INDICATOR, "0");

    	
    	if(statuscd.equals("32")){
    		
    		msgmap.put(MapKeys.STATUSID, msgmap.get(MapKeys.STATUSID_PLATFORM));
    		msgmap.put(MapKeys.STATUSID_ORG, msgmap.get(MapKeys.STATUSID_ORG));
    		msgmap.put(MapKeys.KTIME, msgmap.get(MapKeys.RTIME));
    		msgmap.put(MapKeys.CARRIER_SUBMITTIME_ORG, msgmap.get(MapKeys.RTIME));
    		msgmap.put(MapKeys.CARRIER_SUBMITTIME, msgmap.get(MapKeys.RTIME));
    		msgmap.put(MapKeys.CARRIER_DONETIME_ORG, msgmap.get(MapKeys.RTIME));
    		msgmap.put(MapKeys.CARRIER_DONETIME, msgmap.get(MapKeys.RTIME));

    	}else if(statuscd.equals("16")){
    		
    		msgmap.put(MapKeys.CARRIER_SUBMITTIME_ORG, msgmap.get(MapKeys.KTIME));
    		msgmap.put(MapKeys.CARRIER_SUBMITTIME, msgmap.get(MapKeys.KTIME));
    		msgmap.put(MapKeys.CARRIER_DONETIME_ORG, msgmap.get(MapKeys.KTIME));
    		msgmap.put(MapKeys.CARRIER_DONETIME, msgmap.get(MapKeys.KTIME));

    		
    	}else{
    		
    		parseDliveryReceipt(msgmap);
    		
    		msgmap.put(MapKeys.CARRIER_SUBMITTIME, ""+getTime(msgmap.get(MapKeys.CARRIER_SUBMITDATE).toString()));
    		msgmap.put(MapKeys.CARRIER_DONETIME, ""+getTime(msgmap.get(MapKeys.CARRIER_DONEDATE).toString()));
    	    msgmap.put(MapKeys.CARRIER_SUBMITTIME_ORG, ""+getTime(msgmap.get(MapKeys.CARRIER_SUBMITDATE).toString()));
    		msgmap.put(MapKeys.CARRIER_DONETIME_ORG, ""+getTime(msgmap.get(MapKeys.CARRIER_DONEDATE).toString()));
    		//setTimeAdjust();
    	}

		
	}

	private void sendtoClientDnQueue() {
		
     Map<String,String> accountinfo=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
		
     	String protocol=msgmap.get(MapKeys.PROTOCOL).toString();


		msgmap.put(MapKeys.DLR_POST_YN, accountinfo.get(MapKeys.DLR_POST_YN));
		
    	if(accountinfo.get(MapKeys.DLR_POST_YN)!=null&&accountinfo.get(MapKeys.DLR_POST_YN).equals("1")){
    		
    		
    		if(protocol!=null){
    			
    			if(protocol.equalsIgnoreCase("smpp")){
    				String queuename="smppdn_"+msgmap.get(MapKeys.USERNAME).toString();
    				new QueueSender().sendL(queuename, msgmap, false,logmap);
    				logmap.put("smppdn_ status", "send to "+queuename+" redis queue");

    			}else if(protocol.equalsIgnoreCase("http")){
    				

    				new QueueSender().sendL("httpdn", msgmap, false,logmap);
    				logmap.put("httpdn status", "send to httpdn redis queue");
    			}
    		}
    		
    	}
		
	}

	private void doSuccessMasking() {
		
    	if(DNSuccessMasking.getInstance().isExsistingUser(msgmap.get(MapKeys.USERNAME).toString())){
    		
    		boolean  isSuccess=msgmap.get(MapKeys.STATUSID).toString().equals("000");
    		
    		DNSuccessMaskingProcessor.getInstance().incrementHistory(msgmap.get(MapKeys.USERNAME).toString(), isSuccess);
    	
    	
    		if(!isSuccess){
    			
    			if(MessageStatus.getInstance().isSuccessMasking(msgmap.get(MapKeys.STATUSID).toString())){
    				
    				 boolean result = false;
    			        
    			        
    			        Map history = DNSuccessMaskingProcessor.getInstance().getHistory(msgmap.get(MapKeys.USERNAME).toString());
    			        
    			        double scntD = Double.parseDouble(history.get(DNSuccessMaskingProcessor.S_CNT).toString());
    			        double tcntD = Double.parseDouble(history.get(DNSuccessMaskingProcessor.T_CNT).toString());
    			        
    			        result = (Math.floor((DNSuccessMasking.getInstance().getParcentage(msgmap.get(MapKeys.USERNAME).toString())  * tcntD)/100)) > Math.floor((scntD));
    			        
    			        if(result){
    			        	
    			        	msgmap.put(MapKeys.STATUSID, "000");
    			        	
    			        	DNSuccessMaskingProcessor.getInstance().incrementHistoryForSuccess(msgmap.get(MapKeys.USERNAME).toString());
    			        }
    			}
    		}
    	}
		
	}

	private void setPlatformStatuscd() {
		
    	String statuscd=msgmap.get(MapKeys.DN_STATUSCD).toString();
    	if(statuscd.equals("1")){
    		
    		msgmap.put(MapKeys.STATUSID, "000");
    		msgmap.put(MapKeys.STATUSID_ORG, "000");

    		
    	}else{
    
    		
    		if(statuscd.equals("2")){
    		
    			setMessageStatusId();

    		}else if(statuscd.equals("16")){
    		
    			setErrorCode();    			
    			setMessageStatusId();
    		}else if(statuscd.equals("32")){
    		
    			
    		}
    	}
    	
	}

	private void setErrorCode() {

		String errorcode=KannelMessageStatus.getInstance().getErrorCode(msgmap.get(MapKeys.DR).toString());
		
		if(errorcode==null){
			
			KannelMessageStatus.getInstance().insert(msgmap.get(MapKeys.DR).toString());
			KannelMessageStatus.getInstance().reload();
			errorcode=KannelMessageStatus.getInstance().getErrorCode(msgmap.get(MapKeys.DR).toString());
    		
		}
		
		msgmap.put(MapKeys.CARRIER_ERR, errorcode);
	}

	private void setMessageStatusId() {

		String smscid=msgmap.get(MapKeys.SMSCID_ORG).toString();
		String carrier=Carrier.getInstance().getCarrier(smscid);
		
		if(carrier==null){
			carrier="UNKNOWN";
		}
		String errorcode=msgmap.get(MapKeys.CARRIER_ERR).toString();
		String statusid=MessageStatus.getInstance().getStatusid(carrier,errorcode);
		String stat=(String)msgmap.get(MapKeys.CARRIER_STAT);
		
		if(stat==null){
		
			stat="UNKNOWN";
		}
		
		if(stat.trim().length()>50){
			
			stat=stat.substring(0, 50);
		}
		
		
		if(errorcode.trim().length()>5){
			
			errorcode=errorcode.substring(0, 5);
		}
		if(statusid==null){
			
			TableExsists table=new TableExsists();
			
			table.insertMessageStatus(carrier.toUpperCase(),errorcode,stat);
			MessageStatus.getInstance().reload();
			statusid=MessageStatus.getInstance().getStatusid(carrier,errorcode);
		}
		
		msgmap.put(MapKeys.STATUSID, statusid);
		msgmap.put(MapKeys.STATUSID_ORG, statusid);
	}

	private void setLatencySlap() {

    	long rtime=Long.parseLong(msgmap.get(MapKeys.RTIME).toString())/1000;
    	long ktime=System.currentTimeMillis();
    	if(msgmap.get(MapKeys.KTIME)!=null){
    		ktime=Long.parseLong(msgmap.get(MapKeys.KTIME).toString())/1000;
    	}
    	long dtime=Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME).toString())/1000;
    	long dtime_org=Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME_ORG).toString())/1000;
    	long ctime=Long.parseLong(msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG).toString())/1000;

    	SlapCalculator slap=new SlapCalculator();
    	
    	int smslatency_org=slap.getSlap(dtime_org-rtime);
    	
    	int smslatency=slap.getSlap(dtime-rtime);
    	
    	int carrierlatency=slap.getSlap(dtime_org-ctime);
    	
    	int paltformlatency=slap.getSlap(ktime-rtime);

    	msgmap.put(MapKeys.SMS_LATENCY, smslatency+"");
    	msgmap.put(MapKeys.SMS_LATENCY_ORG, smslatency_org+"");
    	msgmap.put(MapKeys.CARRIER_LATENCY, carrierlatency+"");    	
    	msgmap.put(MapKeys.PLATFORM_LATENCY, paltformlatency+"");

    	
    }
	
	private long getTime(String drdate) 
	{
    	String dateformat="";
    	
    	if(drdate.trim().length()==10){
    		
    		dateformat="yyMMddHHmm";
    		
    	}else if(drdate.trim().length()==14){
    		
    		dateformat="yyyyMMddHHmmss";

    	}else{
    		/*
    		String year=drdate.substring(0,4);
    		
    		int yearInt=Integer.parseInt(year);
    		
    		if(yearInt>2019&&yearInt<2099){
    			
        		dateformat="yyyyMMddHHmm";

    		}else{
    			
        		
    		}
    		*/
    		
    		dateformat="yyMMddHHmmss";

    	}
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		try {
			return sdf.parse(drdate).getTime()	;
		} catch (ParseException e) {
			return System.currentTimeMillis();
		}

	}

	public void parseDliveryReceipt(Map<String,Object> requestmap) 
	{		
	
		try{
    	String statuscd=requestmap.get(MapKeys.DN_STATUSCD).toString();

    	if(statuscd.equals("1")||statuscd.equals("2")){
    		
    	
		String dr=requestmap.get(MapKeys.DR).toString().toUpperCase();
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
       try{
            if(dr.indexOf("ERR:")!=-1)
                            err = dr.substring(dr.indexOf("ERR:") + 4, dr.indexOf(" ",dr.indexOf("ERR:") + 4)).trim();
            }catch(Exception e1){
            	   if(dr.indexOf("ERR:")!=-1)
                       err = dr.substring(dr.indexOf("ERR:") + 4, dr.length()).trim();
     
            }

        
        if(stat.indexOf(":")!=-1)
            stat=stat.substring(0,stat.indexOf(":"));

        
        requestmap.put(MapKeys.CARRIER_MSGID, id);
        requestmap.put(MapKeys.CARRIER_SUBMITDATE, submitDate);
        requestmap.put(MapKeys.CARRIER_DONEDATE, doneDate);
        requestmap.put(MapKeys.CARRIER_STAT, stat);
        requestmap.put(MapKeys.CARRIER_ERR, err);
	
    	}
		}catch(Exception e){
			
			e.printStackTrace();
			
			System.err.println(requestmap);
			
			System.err.println("System going to down");
			System.exit(-1);
		}
    	}


	public void parseDliveryReceipt32(Map<String,Object> requestmap) 
	{		
	
		SimpleDateFormat d=new SimpleDateFormat("yyMMddHHmmss");

		String da=d.format(new Date(System.currentTimeMillis()));
        
        requestmap.put(MapKeys.CARRIER_MSGID, "1");
        requestmap.put(MapKeys.CARRIER_SUBMITDATE, da);
        requestmap.put(MapKeys.CARRIER_DONEDATE, da);
        
        if(requestmap.get(MapKeys.STATUSID).toString().equals("200")){
        	
            requestmap.put(MapKeys.CARRIER_STAT, "DELIVERED");

        }else{
            requestmap.put(MapKeys.CARRIER_STAT, "FAILED");

        }
        requestmap.put(MapKeys.CARRIER_ERR, requestmap.get(MapKeys.STATUSID).toString());
	
    	}
    	
    	
	
	
	
	public static void main(String args[]){
		
		Random random=new Random();
		
		while(true){
			
			System.out.println(random.nextInt(50));
		}
	}
    
}
