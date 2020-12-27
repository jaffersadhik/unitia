package smppclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionConfiguration;

public class Processor {
	
	private Map<String,String> data=null;
	
	private List<Session> sessionlist=new ArrayList<Session>();
	
	public Processor(Map<String,String> data){
		
		this.data=data;
		
		String trx=data.get("is_trx").toString();
		
		int sessioncount=1;
		
		try{
			
			sessioncount=Integer.parseInt(data.get("sessioncount").toString());
			
		}catch(Exception e){
			
		}
		if(trx.equals("1")){
			
			SmppSessionConfiguration config =getTRXConfig();
			
			for(int i=0,max=sessioncount;i<max;i++){
				
				sessionlist.add(new Session(config,data.get("mysqlid").toString(),data.get("smscid").toString(),(i+1),data.get("mysqlid").toString()));
			}
			
		}else{
			

			SmppSessionConfiguration configTX =getTXConfig();
			
			SmppSessionConfiguration configRX =getRXConfig();

			for(int i=0,max=sessioncount;i<max;i++){
				
				sessionlist.add(new Session(configTX,data.get("mysqlid").toString(),data.get("smscid").toString(),(i+1),data.get("mysqlid").toString()));
			
				sessionlist.add(new Session(configRX,data.get("mysqlid").toString(),data.get("smscid").toString(),(i+1),data.get("mysqlid").toString()));

			}
			
		}
		
		
	}
	
	public SmppSessionConfiguration getTRXConfig(){
	
		SmppSessionConfiguration config=new SmppSessionConfiguration(SmppBindType.TRANSCEIVER, data.get("username").toString(), data.get("password").toString());

		config.setHost(data.get("ip").toString());
		config.setPort(Integer.parseInt(data.get("port")));
		
		return config;
	}
	
	public SmppSessionConfiguration getTXConfig(){
		
		SmppSessionConfiguration config=new SmppSessionConfiguration(SmppBindType.TRANSMITTER, data.get("username").toString(), data.get("password").toString());

		config.setHost(data.get("ip").toString());
		config.setPort(Integer.parseInt(data.get("port")));
		
		return config;
	}
	
	public SmppSessionConfiguration getRXConfig(){
		
		SmppSessionConfiguration config=new SmppSessionConfiguration(SmppBindType.RECEIVER, data.get("username").toString(), data.get("password").toString());

		config.setHost(data.get("ip").toString());
		config.setPort(Integer.parseInt(data.get("port")));
		
		return config;
	}


}
