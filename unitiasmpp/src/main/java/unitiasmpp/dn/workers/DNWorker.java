package unitiasmpp.dn.workers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.DeliverSmResp;
import com.cloudhopper.smpp.pdu.GenericNack;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;


public class DNWorker 
{
	private static final String className = "[DNWorker]";

	public static final String SMPP_DATE_FORMAT = "yyMMddHHmm";


	private String systemId="";
	
	public DNWorker(String systemId){
		
		this.systemId=systemId;
	}

	public boolean sendMessage(SmppServerSession session,Map _deliverSMObj) 
	{
		
		boolean status = false;

		try {
					
			
			Byte esm=4;
			
			String dnMsg=getDnMessage(esm,_deliverSMObj);
			
			DeliverSm request=getDeliverSmRequest(esm,dnMsg,_deliverSMObj);
		
			try {
				
				_deliverSMObj.put("DNSTS", new Timestamp(System.currentTimeMillis()));
				WindowFuture<Integer,PduRequest,PduResponse> afuture=session.sendRequestPdu(request, 3000, true);
				
				
				afuture.await();
				
				PduResponse resp=afuture.getResponse();
				
				_deliverSMObj.put("DNRTS", new Timestamp(System.currentTimeMillis()));
				_deliverSMObj.put(MapKeys.CUSTOMERIP, session.getConfiguration().getHost());

			
				if(resp==null ) {
					_deliverSMObj.put("dn resp",""+resp);

				}else if( resp.getCommandStatus()==0){
					_deliverSMObj.put("class Name",""+resp.getClass().getName());

					_deliverSMObj.put("dn resp",""+resp.getCommandStatus());

					status=true;
				}else{
					_deliverSMObj.put("class Name",""+resp.getClass().getName());

					_deliverSMObj.put("dn resp",""+resp.getCommandStatus());

					status=false;
				}
				

			} catch (Exception e) {

				_deliverSMObj.put("dn resp error",""+ErrorMessage.getMessage(e));

			}
		} catch(Exception e1) {  
			_deliverSMObj.put("dn resp error",""+ErrorMessage.getMessage(e1));

			e1.printStackTrace();
		}


		return status;
	}
	
	
	private DeliverSm getDeliverSmRequest(byte esm,String dnmsg,Map<String,String> _deliverSMObj)
					throws SmppInvalidArgumentException {
		
		DeliverSm request=new DeliverSm();
		
		request.setServiceType("win");
		request.setEsmClass(esm);
		request.setProtocolId((byte)0x00);
		request.setPriority((byte)0x00);
		request.setRegisteredDelivery((byte)0x00);

		if(_deliverSMObj.get(MapKeys.MSGTYPE).equalsIgnoreCase(MessageType.UM)) {
			request.setDataCoding((byte)0x08);
		} else {
			request.setDataCoding((byte)0x00);
		}
		
		Address sourceAddress=new Address((byte)0x01,(byte)0x01,_deliverSMObj.get(MapKeys.MOBILE));
		request.setSourceAddress(sourceAddress);
		Address destinationAddress=new Address((byte)0x01,(byte)0x01,_deliverSMObj.get(MapKeys.SENDERID_ORG));
		request.setDestAddress(destinationAddress);
		
		
		if(_deliverSMObj.get(MapKeys.UDH)==null) {
			if(_deliverSMObj.get(MapKeys.MSGTYPE).equalsIgnoreCase(MessageType.EM))
				request.setShortMessage(dnmsg.getBytes());	
			else {
				request.setShortMessage(dnmsg.getBytes());
			}
		} else {
			String udh=_deliverSMObj.get(MapKeys.UDH).toString();
			if(_deliverSMObj.get(MapKeys.MSGTYPE).equalsIgnoreCase(MessageType.EM)) {	
				request.setShortMessage((udh +dnmsg).getBytes());
			} else {						
				request.setShortMessage((udh +dnmsg).getBytes());
			}
		}
		return request;
	}
	
	
	private String getDnMessage(Byte esm,Map<String,String> _deliverSMObj) {
		//if esm class is 4 it is dn message	
		
		String msg="";
		String dnMsg="";

		if(esm == 4) {
			
			int endIndex =0;
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
			
			String doneDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(Long.parseLong(_deliverSMObj.get(MapKeys.CARRIER_DONETIME))));

			dnMsg +=" done date:"+ doneDtStr;

			String statusid=_deliverSMObj.get(MapKeys.STATUSID);
			if(statusid!= null && statusid.equals("000")){
				dnMsg +=" stat:DELIVRD";
			}
			
			else{
				dnMsg +=" stat:"+MessageStatus.getInstance().getState(statusid);
			}
				dnMsg +=" err:"+statusid;
			

			dnMsg +=" Text: "+msg;   
			
		} else {
			dnMsg=(String)_deliverSMObj.get(MapKeys.DNMSG);
		}
		return dnMsg;
	}
}
