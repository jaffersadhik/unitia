package com.winnovature.unitia.util.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import com.winnovature.unitia.util.datacache.account.PushAccount;

public class Log {

	public void log(Map<String,String> logmap){
		
		Map<String,String> map=PushAccount.instance().getPushAccount(logmap.get(MapKeys.USERNAME));

		String logmode=ConfigParams.getInstance().getProperty(ConfigKey.LOGMODE);
		
		if(logmode.equals("y")){
		
			logAppslog(logmap);
		}
		
		if((map!=null&&map.get(MapKeys.LOGYN).equals("y"))){
		
			logUserLog(logmap);
		}
	}

	private void logUserLog(Map<String, String> logmap) {
		
		savefile(logmap.get(MapKeys.USERNAME), logmap);
		

		
	}

	private void logAppslog(Map<String, String> logmap) {
		
		savefile("commonapplog", logmap);
		
	}
	
	private void savefile(String name,Map<String,String> logmap){
		
	
		 try {
		      // Creates a FileWriter
			 String filename="/unitialogs/"+name+".log";
		      FileWriter file = new FileWriter(filename,true);

		      // Creates a BufferedWriter
		      BufferedWriter output = new BufferedWriter(file);

		      // Writes the string to the file
		      output.write(ToJsonString.toString(logmap));
		      output.newLine();

		      // Closes the writer
		      output.close();
		    }

		    catch (Exception e) {
		      e.getStackTrace();
		    }
	}
}
