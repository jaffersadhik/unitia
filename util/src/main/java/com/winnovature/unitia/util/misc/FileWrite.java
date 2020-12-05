package com.winnovature.unitia.util.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.redis.QueueSender;

public class FileWrite {

private static String MODE="";
private static Set<String> NON_LOGNAME=new HashSet<String>();

static{
	
	NON_LOGNAME.add("optin");
	NON_LOGNAME.add("optout");
	NON_LOGNAME.add("duplicate");
	NON_LOGNAME.add("countrycode");
	NON_LOGNAME.add("numberingplan");
	NON_LOGNAME.add("blacklistmobile");
	
	NON_LOGNAME.add("blacklistsms");
	NON_LOGNAME.add("blacklistsenderid");
	NON_LOGNAME.add("spamfilter");
	NON_LOGNAME.add("senderidcheck");
	NON_LOGNAME.add("templatecheck");
	NON_LOGNAME.add("dnd");
	
	
	NON_LOGNAME.add("dlt");
	NON_LOGNAME.add("routegroup");
}
	static {
		
		String mode=System.getenv("mode");
		
		if(mode==null||mode.trim().length()<1){
			
			MODE="production";
		}else{
			
			MODE=mode;
		}
		
		

	}
	public void write(Map<String,Object> logmap){
		
		try{	
			logmap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());
			
			String logname=(String)logmap.get("logname");

			if(logname==null||!NON_LOGNAME.contains(logname)){
			new QueueSender().sendL("logspool", logmap, false, logmap);
			}
					
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}

	

	private void logAppslog(Map<String, Object> logmap) {
		
		String logname=(String)logmap.get("logname");
		if(logname!=null&&logname.length()>1){
			savefile(logname, logmap,true);

		}else{
			savefile("commonapplog", logmap,true);

		}
		
	}
	
	private void savefile(String name,Map<String,Object> logmap,boolean isError){
		
	
		 try {
			 

		      if(isError){
		      // Creates a FileWriter
			 String filename="/unitia/"+MODE+"/logs/apps/"+name+".log";
		      FileWriter file = new FileWriter(filename,true);

		      // Creates a BufferedWriter
		      BufferedWriter output = new BufferedWriter(file);

		      // Writes the string to the file
		      output.write(ToJsonString.toString(logmap));
		      output.newLine();

		      // Closes the writer
		      output.close();
		      }
		      

		 }catch (Exception e) {
		      e.getStackTrace();
		    }
	}
	
	
	public void savekannelfile(String kannelid,String kannelfile){
		
		
		 try {
			 

		      
		      // Creates a FileWriter
			 String filename="/unitia/"+MODE+"/"+kannelid+"/kannel.conf";
		      FileWriter file = new FileWriter(filename,false);

		      // Creates a BufferedWriter
		      BufferedWriter output = new BufferedWriter(file);

		      // Writes the string to the file
		      output.write(kannelfile);
		      output.newLine();

		      // Closes the writer
		      output.close();
		   
		      

		 }catch (Exception e) {
		      e.getStackTrace();
		    }
	}


	public  void log(String logstaring){
	
		
	}
	public void logError(String module,Map<String,Object> msgmap,Exception e) {
		try{
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", module);
		logmap.putAll(msgmap);
		Map usermap=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
		logmap.put("useraccount map",usermap==null?"":usermap.toString() );
		logmap.put("Error Message",ErrorMessage.getMessage(e) );
		logmap.put("logname","error");
		write(logmap);
		}catch(Exception e1){
			
			e1.printStackTrace();
			e.printStackTrace();
		}
	}
}
