package com.winnovature.unitia.util.misc;

public class Log {

	static FileWrite log =new FileWrite();

 public static void log(String logstring){
		
		log.log(logstring);
	}
}
