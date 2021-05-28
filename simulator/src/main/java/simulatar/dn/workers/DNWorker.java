package simulatar.dn.workers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;

import simulatar.util.MapKeys;


public class DNWorker 
{
	private static final String className = "[DNWorker]";

	public static final String SMPP_DATE_FORMAT = "yyMMddHHmm";


	private String systemId="";
	
	public DNWorker(String systemId){
		
		this.systemId=systemId;
	}
	
	
		
	
	
	public WindowFuture<Integer,PduRequest,PduResponse> sendMessage(SmppServerSession session,Map _deliverSMObj) 
	{
		
		WindowFuture<Integer,PduRequest,PduResponse> afuture=null;

		try {
			String dnMsg = "";	
			String msg = "";
			Byte esm=4;
			
			String username=(String)_deliverSMObj.get(MapKeys.USERNAME);
			if(username==null||username.trim().length()<1){
				
				username="dummy";
			}
			if(username.equals("smppclient1")){
				
				dnMsg=getDnMessageErr(esm,_deliverSMObj);

			}else{
				dnMsg=getDnMessage(esm,_deliverSMObj);

			}
			
			DeliverSm request=getDeliverSmRequest(esm,dnMsg,_deliverSMObj);
		
			try {
				
				_deliverSMObj.put("DNSTS", new Timestamp(System.currentTimeMillis()));
			
				afuture=session.sendRequestPdu(request, 250, true);
				
					
				//afuture.await();
				
				//DeliverSmResp resp=(DeliverSmResp)afuture.getResponse();
			//	_deliverSMObj.put("DNRTS", new Timestamp(System.currentTimeMillis()));
/*				if(resp!=null && resp.getCommandStatus()==0) {
					status=true;
					if(logger.isDebugEnabled())
						logger.debug("afuture.isSuccess()="+afuture.isSuccess()+"\nresponse="+afuture.getResponse());					
				} else {					
					logger.warn("failed status="+(resp!=null?resp.getCommandStatus():"null status response"));
				}*/
			} catch (Exception e) {
				
			}
		} catch(Exception e1) {                
			
		}


		return afuture;
	}
	

	private DeliverSm getDeliverSmRequest(byte esm,String dnmsg,Map<String,String> _deliverSMObj)
					throws SmppInvalidArgumentException {
		
		DeliverSm request=new DeliverSm();
		
		request.setServiceType("win");
		request.setEsmClass(esm);
		request.setProtocolId((byte)0x00);
		request.setPriority((byte)0x00);
		request.setRegisteredDelivery((byte)0x00);

		request.setDataCoding((byte)0x00);
		
		Address sourceAddress=new Address((byte)0x01,(byte)0x01,_deliverSMObj.get(MapKeys.MOBILE));
		request.setSourceAddress(sourceAddress);
		Address destinationAddress=new Address((byte)0x01,(byte)0x01,_deliverSMObj.get(MapKeys.SENDERID_ORG));
		request.setDestAddress(destinationAddress);
		
		
		request.setShortMessage(dnmsg.getBytes());	
		
		return request;
	}
	
	
	private String getDnMessage(Byte esm,Map<String,String> _deliverSMObj) {
		//if esm class is 4 it is dn message	
		
		String msg="";
		String dnMsg="";

		
			
			int endIndex =0;
			_deliverSMObj.put(MapKeys.DNMSG, "test message");
			if(_deliverSMObj.get(MapKeys.DNMSG) != null) {
				msg = _deliverSMObj.get(MapKeys.DNMSG);
				endIndex = msg.length()>=20?19:msg.length();
			}
			
			msg = msg.substring(0, endIndex);
			
			dnMsg +="id:"+_deliverSMObj.get(MapKeys.ACKID);

			dnMsg +=" sub:1";

			dnMsg +=" dlvrd:1";

			String subDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(Long.parseLong(_deliverSMObj.get(MapKeys.RTIME))));

			dnMsg +=" submit date:"+ subDtStr;
			
			String doneDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(System.currentTimeMillis()));

			dnMsg +=" done date:"+ doneDtStr;

			dnMsg +=" stat:DELIVRD";
			
			dnMsg +=" err:000";

			dnMsg +=" Text: "+msg;   
			
		
		return dnMsg;
	}

	private String getDnMessageErr(Byte esm,Map<String,String> _deliverSMObj) {
		//if esm class is 4 it is dn message	
		
		String msg="";
		String dnMsg="";

		
			
			int endIndex =0;
			_deliverSMObj.put(MapKeys.DNMSG, "test message");
			if(_deliverSMObj.get(MapKeys.DNMSG) != null) {
				msg = _deliverSMObj.get(MapKeys.DNMSG);
				endIndex = msg.length()>=20?19:msg.length();
			}
			
			msg = msg.substring(0, endIndex);
			
			dnMsg +="id:"+_deliverSMObj.get(MapKeys.ACKID);

			dnMsg +=" sub:1";

			dnMsg +=" dlvrd:1";

			String subDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(Long.parseLong(_deliverSMObj.get(MapKeys.RTIME))));

			dnMsg +=" submit date:"+ subDtStr;
			
			String doneDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(System.currentTimeMillis()));

			dnMsg +=" done date:"+ doneDtStr;

			dnMsg +=" stat:FAILED";
			
			dnMsg +=" err:101";

			dnMsg +=" Text: "+msg;   
			
		
		return dnMsg;
	}


}
