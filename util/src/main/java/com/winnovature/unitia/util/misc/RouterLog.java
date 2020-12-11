package com.winnovature.unitia.util.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.winnovature.unitia.util.account.PushAccount;

public class RouterLog {

private static String MODE="";
	
	static {
		
		String mode=System.getenv("mode");
		
		if(mode==null||mode.trim().length()<1){
			
			MODE="production";
		}else{
			
			MODE=mode;
		}
		
		

	}

	static Map<String,Logger> LOGSNAME=new HashMap<String,Logger>();
	
	
	
	public void write(Map<String,Object> logmap){
		try{
			
			logmap.put("logdate", new Date().toString());
			
			String username=(String)logmap.get(MapKeys.USERNAME);
		String logmode=ConfigParams.getInstance().getProperty(ConfigKey.LOGMODE);
		String statusid=(String)logmap.get(MapKeys.STATUSID);
		
		if(statusid!=null&&statusid.equals(""+MessageStatus.INVALID_IP)){
		
			savefile("invalidiplist", logmap);

		}
		
		String bindlog=(String)logmap.get("bindlog");
		
		if(bindlog!=null&&bindlog.equals("y")){
			
			savefile("bindlog", logmap);

		}
		
		
		String invalidresponse=(String)logmap.get("invalidresponse");
		
		if(invalidresponse!=null&&invalidresponse.equals("y")){
			
			savefile("invalidresponse", logmap);

		}


		String tablereaderlog=(String)logmap.get("tablereaderlog");
		
		if(tablereaderlog!=null&&tablereaderlog.equals("y")){
			
			savefile("tablereaderlog", logmap);

		}
		if(logmode.equals("1")){
		
			if(username!=null&&username.equals("kannelretry")){
				
				savefile(username, logmap);

			}else{
		
				logAppslog(logmap);
			}
		
		}
		
		
		}catch(Exception e){
			
			e.printStackTrace();
		}
	}

	

	private void logAppslog(Map<String, Object> logmap) {
		
		String logname=(String)logmap.get("logname");
		if(logname!=null&&logname.length()>1){
			savefile(logname, logmap);

		}else{
			savefile("commonapplog", logmap);

		}
		
	}
	
	private void savefile(String name,Map<String,Object> logmap){
		
	

		      // Creates a FileWriter
		      String filename="/logs/"+MODE+"/apps/"+name+".log";
		   try{
		      FileWriter file = new FileWriter(filename,true);

		      // Creates a BufferedWriter
		      BufferedWriter output = new BufferedWriter(file);

		      // Writes the string to the file
		      output.write(ToJsonString.toString(logmap));
		      output.newLine();

		      // Closes the writer
		      output.close();
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		      
}
	
	public void routerlog(String redis,String tname,String logmessage){
		
		
/*
	      String filename="/logs/"+MODE+"/apps/"+redis+"_"+tname+".log";
	   try{
	      FileWriter file = new FileWriter(filename,true);

	      // Creates a BufferedWriter
	      BufferedWriter output = new BufferedWriter(file);

	      // Writes the string to the file
	      output.write(logmessage);
	      output.newLine();

	      // Closes the writer
	      output.close();
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	*/	      
}


	
	private Logger getLogger(String filename) {
		
		 Logger logger=LOGSNAME.get(filename);
		 
		 if(logger==null){
			 logger= Logger.getLogger(filename);

		      FileHandler fh=null;
			try {
				fh = fh = new FileHandler(filename,102400,1);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		      logger.addHandler(fh);
		      logger.setLevel(java.util.logging.Level.INFO);
		      SimpleFormatter formatter = new SimpleFormatter();  
		      fh.setFormatter(formatter);
		      
				logger.setUseParentHandlers(false);
				LogManager.getLogManager().reset();
				Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
				globalLogger.setLevel(java.util.logging.Level.OFF);
		      LOGSNAME.put(filename, logger);
		 }
		 

		      return logger;
	}



	public void savekannelfile(String kannelfile){
		
		
		 try {
			 

		      
		      // Creates a FileWriter
			 String filename="/unitia/"+MODE+"/kannel/kannel.conf";
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
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", module);
		logmap.putAll(msgmap);
		Map usermap=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
		logmap.put("useraccount map",usermap==null?"":usermap.toString() );
		logmap.put("Error Message",ErrorMessage.getMessage(e) );

		savefile("error", logmap);
		
	}
}
