package demo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class Processor  extends Process{
	
	boolean STATUS=true;
	
	String msg=null;
	
	public Processor(String msg){
	
		this.msg=msg;

		new T().start();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int exitValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InputStream getErrorStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int waitFor() throws InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	class T extends Thread{
		
		public void run(){
		while(true){
	
			int minute=Calendar.getInstance().get(Calendar.MINUTE);
			
			if(minute%2==0){
				STATUS=true;
			}else{
				STATUS=false;
			}
			
			gotosleep();
		}
		}
		
		public void gotosleep() {
			try{
				Thread.sleep(30000L);
			}catch(Exception e){
				
			}
			
		}
	}

	
	public String getStatus(){
		
		return msg+" "+STATUS;
	}

}
