package unitiaroute;

import org.apache.commons.lang.StringUtils;

public class T1 {

	public static void main(String args[]){
		
		System.out.println("======true=========");
		T1 obj=new T1();
		obj.m1();
		obj.m2();
		obj.m3();
		obj.m4(); 
		obj.m5();
		obj.m6();
		obj.m7();
		obj.m8();
		System.out.println("======false=========");
		obj.m9();
	}

	private  void m9() {
		String fullmsg="Dear test test, we are pleased to inform you that we have reduced our rates for the same coverage selected by you. We have initiated a partial refund of Rs 2000. Your new policy number is 222222 against Order ID 111111. Download your policy certificate by clicking on http:www.gmail.com Please call 18002666 for queries* https://bit.ly/39KROPo";
		String template="Dear {#var#}, your new policy number is {#var#} against Order ID {#var#}. Download your policy certificate by clicking on {#var#} Please call 18002666 for queries";
		
	System.out.println(isMatch(template,fullmsg));
}
	private  void m8() {
		String fullmsg="Hi Livin Udyavara raw, Thank  you for purchasing and registering with Yokohama India Online warranty system through JAES WHEEL  Your Warranty Registration number is S486483. Please save this for all future references. 1 T&C* https://bit.ly/39KROPo";
		String template="Hi {#var#}, Thank you for purchasing and registering with Yokohama India Online warranty system through {#var#} Your Warranty Registration number is {#var#} Please save this for all future references. {#var#}";
		
	System.out.println(isMatch(template,fullmsg));
}	
	private  void m7() {
		String fullmsg="Please share code 11111 with Sales Executive of MPS Jeep for conducting Test Drive. Please provide Test Drive Feedback - test test";
		String template ="Please share code {#var#} with Sales Executive of MPS Jeep for conducting Test Drive.";
	
	System.out.println(isMatch(template,fullmsg));
}

	private  void m6() {
			String fullmsg="Dear Praveen Your complaint is registered and your complaint ticket number is 22222 - Yokohama India";
		String template="Dear {#var#} Your complaint is registered and your complaint ticket number is {#var#} - Yokohama India";

		System.out.println(isMatch(template,fullmsg));
	}
	
	private  void m5() {
		String fullmsg ="Dear DC Reader, Greetings! Please click below to view and read your favourite DC http://dcnews.deccanchr";
		String template="Dear DC Reader, Greetings! Please click below to view and read your favourite DC {#var#}";

		System.out.println(isMatch(template,fullmsg));
	}
	private  void m4() {
		String fullmsg="Dear Subscriber, \n\n   Please verify OTP for Password change for Bhima Riddhi App.. OTP for verification is 123456";
		String template="Dear Subscriber, Please verify OTP for Password change for Bhima Riddhi App.. OTP for verification is Rs.{#var#}S ";

		System.out.println(isMatch(template,fullmsg));
	}
	private  void m1() {
		String template="Dear {#var#}, we are pleased to inform you that we have reduced our rates for the same coverage selected by you. We have initiated a partial refund of Rs {#var#}. Your new policy number is {#var#} against Order ID {#var#}. Download your policy certificate by clicking on {#var#} Please call 18002666 for queries";
		String fullmsg="Dear test test, we are pleased to inform you that we have reduced our rates for the same coverage selected by you. We have initiated a partial refund of Rs 2000. Your new policy number is 222222 against Order ID 111111. Download your policy certificate by clicking on http:www.gmail.com Please call 18002666 for queries";

		System.out.println(isMatch(template,fullmsg));
	}
	
	
	private  void m2() {
		String template ="Dear {#var#}, for issuance of a motor policy for Order ID {#var#}, your policy will not be generated until you fill the remaining details on {#var#} Your cover will not start tomorrow if you don't do so by 11:59 pm, tonight.";
		String fullmsg="Dear 11111, for issuance of a motor policy for Order ID 11111, your policy will not be generated until you fill the remaining details on 22222 Your cover will not start tomorrow if you don't do so by 11:59 pm, tonight.";

		System.out.println(isMatch(template,fullmsg));
	}
	
	private  void m3() {
			String template="Dear {#var#}, for issuance of a motor policy for Order ID {#var#}, your policy will not be generated until you fill the remaining details on {#var#}";
			String fullmsg="Dear 111111, for issuance of a motor policy for Order ID 22222, your policy will not be generated until you fill the remaining details on 22222";

		System.out.println(isMatch(template,fullmsg));
	}
public  boolean isMatch(String template,String fullmsg){
		
		String temp[]=StringUtils.split(template);
		String msg[]=StringUtils.split(fullmsg);

		int msgpointer=0;

		for(int i=0;i<temp.length;i++){
			
			if(msgpointer>=msg.length){
				
				return  false;
			}
			String m=msg[msgpointer];
			String t=temp[i];
			if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
				int lastTempPointer=getLastTempPointer(temp,i);
				int maxVarCharCount=getMaxVarcharCount(lastTempPointer,temp);
				int upcomingmsgpointer=getUpcomingMsgPointer(lastTempPointer,temp,msg,msgpointer);
				int varcharcount=getVarcharCount(msg,msgpointer,upcomingmsgpointer);
			/*
				System.out.println(" lastTempPointer "+ lastTempPointer);
				System.out.println(" maxVarCharCount "+ maxVarCharCount);
				System.out.println(" upcomingmsgpointer "+ upcomingmsgpointer);
				System.out.println(" varcharcount "+ varcharcount);
*/
				if(maxVarCharCount>varcharcount){
					msgpointer=upcomingmsgpointer;
				}else{
					return false;
				}
			}else if(! m.equalsIgnoreCase(t)){
				return false;
			}else{
				msgpointer++;
			}

			
		
		} 
		
		return true;

		
}

private int getVarcharCount(String[] msg, int msgpointer, int upcomingmsgpointer) {
	
	StringBuffer sb=new StringBuffer();
	for(int i=msgpointer;i<=upcomingmsgpointer;i++){
		
		sb.append(msg[i]).append(" ");
		
	}
	
	return sb.toString().trim().length();
}

private int getUpcomingMsgPointer(int lastTempPointer, String[] temp, String[] msg, int msgpointer) {
	
	int temppointer=lastTempPointer;
	
	temppointer++;
	
	if(temppointer<temp.length){
		
		String t=temp[temppointer];
		
		for(int i=msgpointer;i<msg.length;i++){
			
			String m=msg[i];
			
			if(m.equalsIgnoreCase(t)){
				
				return i;
			}
		}
	}
		
		return msg.length-1;
	
	
}

private int getMaxVarcharCount(int lastTempPointer, String[] temp) {

	int pointer=lastTempPointer;
	int count=1;
	while(pointer>-1){
		
		pointer--;
		
		if(pointer==-1){
			
			return (count*30)+1;
		}
		
		String t=temp[pointer];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			count++;
		}else{
			return (count*30)+1;
		}
	}

	return (count*30)+1;

	
}

private int getLastTempPointer(String[] temp, int i) {

	int pointer=i;
	int count=0;
	while(pointer<temp.length){
		
		pointer++;
		
		if(pointer==temp.length){
			
			return i+count;
		}
		
		String t=temp[pointer];
		if("{#var#}".equals(t) || t.indexOf("{#var#}")>-1 ){
			count++;
		}else{
			return count+i;
		}
	}

	return count+i;
}


}
