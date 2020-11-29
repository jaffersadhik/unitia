package smppclient;

import java.util.Map;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.Address;
import com.winnovature.unitia.util.redis.RedisReader;

public class Session extends Thread{
	
	private RedisReader reader=new RedisReader();

	private SmppSessionConfiguration config=null;
	
	private SmppSession session=null;
	
	private String bindresponse=null;

	private String kannelid=null;

	private String smscid=null;
	
	private String queuename=null;
	
	private int sessionid=0;
	
	private Handler handler=null;
	
	private String bindType=null;
	
	public Session(SmppSessionConfiguration config,String queuename,String smscid,int sessionid,String kannelid){
	
		this.bindType=config.getType().toString();
		
		this.kannelid=kannelid;
		
		this.sessionid=sessionid;
		
		this.config=config;
		
		this.smscid=smscid;
		
		this.queuename=queuename;
		
		this.handler	=new Handler(queuename, smscid, config.getSystemId(),kannelid);

		this.start();
			
	}

	public void init() {
		
		SmppClient client=new DefaultSmppClient();

		
				
		
		while(true){
				
		
		try {
			session=client.bind(config,handler);
			bindresponse="OK";
			System.out.println(bindresponse);

			break;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			bindresponse=e.getMessage();
			System.out.println(bindresponse);

			gotosleep(2000L);
		} 	
		

		}
	}
	
	private void gotosleep(long l) {
		
		try{
			
			Thread.sleep(l);
		}catch(Exception e){
			
		}
		
		
	}

	public void run(){
		
		
		init();
		
		if(!SmppBindType.RECEIVER.equals(bindType)){
			while(true){
				send();	
			}
		}
	}

	private void send() {
		
	
	try {
		
		Map<String,Object> data=null;

		
		//	data=reader.getData(smscid,redisid);

			
		
		if(data!=null){
			
		}
		SubmitSm submit=getSubmitSm();

		boolean bindStatus=session.isBound();
		System.out.println(bindStatus);
		if(bindStatus){
			SubmitSmResp response=session.submit(submit, 3000L);
			
			if(response.getCommandStatus()==SmppConstants.STATUS_OK){
				
				TPS.getInstance(smscid).incrementSMS();

				System.out.println(response.getMessageId());

				handler.updateTime();
			}else{
				System.out.println(response.getCommandStatus());

			}
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	}

	private SubmitSm getSubmitSm() {

		SubmitSm submit=new SubmitSm();
		
		Address dest=new Address((byte)1,(byte)0,"919787660738");
		Address source=new Address((byte)5,(byte)0,"WECARE");


		submit.setDestAddress(dest);
		submit.setSourceAddress(source);
		
		return submit;
	}
	
	
	public void rebind(){
		
		init();
	}
	public void sendEnquireLink(){
		
		long diff=System.currentTimeMillis()-handler.getLastUpdateTime();
		
		if(diff>18){
			
			if(session!=null&&session.isBound()){
				
				try {
					EnquireLinkResp resp=session.enquireLink(new EnquireLink(), 500L);
					
					if(resp.getCommandStatus()==SmppConstants.STATUS_OK){
					
						handler.updateTime();
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
	}
}
