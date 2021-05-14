package smppclient;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
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

		
			data=reader.getData(smscid,RedisList.getInstance().getRedisId());

			
		
		if(data!=null){
			
		
			setSubmitSm(data);

		boolean bindStatus=session.isBound();
		System.out.println(bindStatus);
		if(bindStatus){
			/*SubmitSmResp response=session.submit(submit, 60000L);
			
			if(response.getCommandStatus()==SmppConstants.STATUS_OK){
				
				TPS.getInstance(smscid).incrementSMS();

				System.out.println(response.getMessageId());

				handler.updateTime();
			}else{
				System.out.println(response.getCommandStatus());

			}*/
		}
		
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	}

	private void setSubmitSm(Map<String,Object> data) throws Exception {

		
		List<Map<String,Object>> msgmaplist=(List<Map<String,Object>> )data.get(MapKeys.MSGLIST);
		
		if(msgmaplist==null){
		
			SubmitSm submit=new SubmitSm();
			
			Address dest=new Address((byte)1,(byte)0,data.get(MapKeys.MOBILE).toString());
			Address source=new Address((byte)5,(byte)0,data.get(MapKeys.SENDERID).toString());
			submit.setDestAddress(dest);
			submit.setSourceAddress(source);
			submit.setRegisteredDelivery((byte)1);

			setDataSingle(submit,data);
			setDLTValue(submit,data);
			data.put("submitsm", submit);
		}else{
			
			
			for(int i=0;i<msgmaplist.size();i++){
				
				SubmitSm submit=new SubmitSm();
				
				Address dest=new Address((byte)1,(byte)0,data.get(MapKeys.MOBILE).toString());
				Address source=new Address((byte)5,(byte)0,data.get(MapKeys.SENDERID).toString());
				submit.setDestAddress(dest);
				submit.setSourceAddress(source);
				submit.setRegisteredDelivery((byte)1);

				String featurecd=data.get(MapKeys.FEATURECODE).toString();

				Map<String,Object> splitup=msgmaplist.get(i);
				splitup.put("submitsm", submit);
				setDLTValue(submit,data);

				setDataMultiple(submit,splitup,featurecd);
				

			}
		}

		
	}
	
	
	private void setDLTValue(SubmitSm submit,Map<String,Object> data) {
		
		Tlv entityid=new Tlv((short)0x1400, data.get(MapKeys.ENTITYID).toString().getBytes());
		Tlv templateid=new Tlv((short)0x1401, data.get(MapKeys.TEMPLATEID).toString().getBytes());

		submit.setOptionalParameter(entityid);
		submit.setOptionalParameter(templateid);
	}

	private void setDataSingle(SubmitSm submit, Map<String, Object> data) throws Exception{
		
		
		String featurecd=data.get(MapKeys.FEATURECODE).toString();
		
		submit.setShortMessage(data.get(MapKeys.FULLMSG).toString().getBytes());

		if(featurecd.equals(FeatureCode.EMS)){
			submit.setDataCoding((byte)0);
		}else if(featurecd.equals(FeatureCode.EFS)){
			submit.setDataCoding((byte)16);
		
		}else if(featurecd.equals(FeatureCode.UMS)){
			submit.setDataCoding((byte)8);

		}else if(featurecd.equals(FeatureCode.UFS)){
			submit.setDataCoding((byte)18);

		}
	}

	private void setDataMultiple(SubmitSm submit, Map<String, Object> data,String featurecd) throws Exception{
		
		
		
		submit.setShortMessage(getMessage(data,featurecd));

		submit.setEsmClass((byte)40);
		
		if(featurecd.equals(FeatureCode.EMC)){
			submit.setDataCoding((byte)0);
		}else if(featurecd.equals(FeatureCode.EFC)){
			submit.setDataCoding((byte)16);
		
		}else if(featurecd.equals(FeatureCode.UMC)){
			submit.setDataCoding((byte)8);

		}else if(featurecd.equals(FeatureCode.UFC)){
			submit.setDataCoding((byte)18);

		}
	}

	 private byte[] getMessage(Map<String, Object> data,String featurecd) {
		String msg=data.get(MapKeys.UDH).toString();
		
		if(FeatureCode.isHexa(featurecd)) 		
		{
			msg+=data.get(MapKeys.FULLMSG).toString();
		}else{
			String temp=toHexStringASCII(data.get(MapKeys.FULLMSG).toString());
			temp=temp.replaceAll("2019", "27");
			temp=temp.replaceAll("60", "27");
			msg+=temp;
				
		}
		return msg.getBytes();
	}

	public  String toHexStringASCII(String str) {
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < str.length(); i++) {
		      sb.append(toHexString(str.charAt(i)));
		    }
		    return sb.toString().replaceAll("00", "");
		  }
	  
	  public  String toHexString(char ch) {
		    String hex = Integer.toHexString((int) ch);
		    while (hex.length() < 4) {
		      hex = "0" + hex;
		    }
		    return hex;
		  
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
