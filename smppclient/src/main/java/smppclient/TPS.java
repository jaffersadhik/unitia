package smppclient;

public class TPS extends Thread{

	private static TPS obj=null;
	
	private long SMS=0;
	
	private long DN=0;
	
	private long tps=0;
	
	private long counter=0;

	private long dntps=0;
	
	private long dncounter=0;

	private String smscid=null;
	
	private TPS(String smscid){
		
		this.smscid=smscid;
	}
	
	public static TPS getInstance(String smscid){
		
		if(obj==null){
			
			obj=new TPS(smscid);
		}
		
		return obj;
	}
	
	
	public void run(){
		
		while(true){
			
			tps=counter;
			counter=0;
			
			dntps=dncounter;
			dncounter=0;
			
			gotosleep();
		}
	}

	public synchronized void incrementSMS(){
	
		SMS++;
		counter++;
	}
	
	public synchronized void incrementDN(){
		
		DN++;
		dncounter++;
	}
	private void gotosleep() {
		
		try {
			Thread.sleep(1000L);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
