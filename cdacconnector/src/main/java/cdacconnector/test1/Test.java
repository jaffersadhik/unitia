package cdacconnector.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Test {
public static void main(String arsg[]){
	
	SMSServices sms=new SMSServices();
	//formUnicode(new HashMap());
//	sms.sendUnicodeOtpSMS("testdemo", "Ftain!3", "நண�?பர�?கள�?", "NXTDGT", "919487660738,919487660739",  "aaa-aaa-aaa", "1");
	//sms.sendOtpSMS("testdemo", "Ftain!3", "test", "NXTDGT", "919487660738,919487660739",  "aaa-aaa-aaa", "1");
//	sms.sendBulkSMS("testdemo", "Ftain!3", "test", "NXTDGT", "919487660738,919487660739",  "aaa-aaa-aaa", "1");
	//sms.sendUnicodeSMS("testdemo", "Ftain!3", "நண�?பர�?கள�?", "NXTDGT", "919487660738,919487660739",  "aaa-aaa-aaa", "1");
	//sms.sendSingleSMS("testdemo", "Ftain!3", "test message", "NXTDGT", "919487660738", "aaa-aaa-aaa", "1");
//	sms.sendDNTestA("testdemo", "Ftain!3", "test message", "NXTDGT", "919487660738", "aaa-aaa-aaa", "1");
	//sms.sendDNTestB("testdemo", "Ftain!3", "test message", "NXTDGT", "919487660738", "aaa-aaa-aaa", "1");

	
	//sms.sendUnicodeSMS("Mobile_1-GOKOTP", "Gokotp@1234", "நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? நண�?பர�?கள�? more2", "GOKOTP", "919487660738", "7c8fa3c0-a908-4aaf-b5ab-095a780dc981", "0123456789123456789");
	
	//sms.sendUnicodeSMS("Mobile_1-GOKOTP", "Gokotp@1234", "நண�?பர�?கள�?", "GOKOTP", "919487660738", "7c8fa3c0-a908-4aaf-b5ab-095a780dc981", "0123456789123456789");
	//	sms.sendSingleSMS("Mobile_1-GOKOTP", "Gokotp@1234", "test message", "GOKOTP", "919487660738", "7c8fa3c0-a908-4aaf-b5ab-095a780dc981", "0123456789123456789");

	sms.sendSingleSMS("Mobile_1-GOKOTP", "Gokotp@1234", "test message more that 160 test test m test message more that 160 testtest message more that 160 testtest message more that 160 testtest message more that 160 testessage more that 160 test", "GOKOTP", "919487660738", "7c8fa3c0-a908-4aaf-b5ab-095a780dc981", "0123456789123456789");

}


private static void formUnicode(Map<String, Object> msgmap2) {
	
	
	String fullmsg="&#2984;&#2979;&#3021;&#2986;&#2992;&#3021;&#2965;&#2995;&#3021;";
	
	StringTokenizer st=new StringTokenizer(fullmsg,";&#");
	
	StringBuffer sb=new StringBuffer();
	
	
	while(st.hasMoreTokens()){
		
		sb.append((char)Integer.parseInt(st.nextToken()));
	}

}
}
