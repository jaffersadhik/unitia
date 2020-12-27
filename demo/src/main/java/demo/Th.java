package demo;

import java.util.regex.Pattern;

public class Th extends Thread{

	public void run(){
		
		while(true){
			
			
			gotosleep();
			
			boolean status=Pattern.compile(new SMSPatternAllowed().getPattern(), Pattern.CASE_INSENSITIVE).matcher("test \n test").matches();

			System.out.println(status);
		}
	}

	private void gotosleep() {
		
		try{
			
			Thread.sleep(300000L);
		}catch(Exception e){
			
		}
		
	}
}
