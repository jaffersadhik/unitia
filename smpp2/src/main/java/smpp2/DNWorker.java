package smpp2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.redis.QueueSender;

public class DNWorker {

	public static final String SMPP_DATE_FORMAT = "yyMMddHHmm";

	public void doProcess(String systemid,List<Map<String, Object>> dnlist) {
		
		List<DNTempBean> dnbeanlist=new ArrayList<DNTempBean>();
		for(int i=0;i<dnlist.size();i++){
			
			DNTempBean bean=new DNTempBean();
			
			bean.setDnMap(dnlist.get(i));
			bean.setFuture(send(systemid,dnlist.get(i)));
			dnbeanlist.add(bean);
		}
		
		for(int i=0;i<dnbeanlist.size();i++){
			
			DNTempBean bean=dnbeanlist.get(i);
			
			if(bean.getFuture()==null){
				
				writeResponse(bean.getDnMap(),null);

			}else{
				
				sendQueue(bean);
			}
			
		}
	}

	private void sendQueue(DNTempBean bean) {
		

		
		
		while(!bean.getFuture().isDone()){
			
			try{
				Thread.sleep(1L);
					
			}catch(Exception e){
				
			}
		}
			
			PduResponse response=bean.getFuture().getResponse();
			
			if(response!=null) {			
				if(response.getCommandStatus()==SmppConstants.STATUS_OK){
					
					writeResponse(bean.getDnMap(),0);	

					
				}else{
					
					writeResponse(bean.getDnMap(),response.getCommandStatus());	
				}
			} else {
				response=bean.getFuture().getResponse();
				
				if(response==null) {
					writeResponse(bean.getDnMap(),null);
					
				} else {
					writeResponse(bean.getDnMap(),response.getCommandStatus());
				}
			}

	
		
	}

	private WindowFuture<Integer, PduRequest, PduResponse> send(String systemid, Map<String, Object> _deliverSMObj) {

		
		WindowFuture<Integer,PduRequest,PduResponse> afuture=null;

		try {
			String dnMsg = "";	
			String msg = "";
			Byte esm=4;
			dnMsg=getDnMessage(esm,_deliverSMObj);
			
			DeliverSm request=getDeliverSmRequest(esm,dnMsg,_deliverSMObj);
		
			try {
				
				_deliverSMObj.put("DNSTS", new Timestamp(System.currentTimeMillis()));
			
				SmppSession session=SessionStore.getInstance().getSession(systemid);
				if(session!=null){
				afuture=session.sendRequestPdu(request, 250, true);
				}
					
				
			} catch (Exception e) {
				
			}
		} catch(Exception e1) {                
			
		}


		return afuture;
	
	}
	
	
	
	private DeliverSm getDeliverSmRequest(byte esm,String dnmsg,Map<String,Object> _deliverSMObj)
			throws SmppInvalidArgumentException {

DeliverSm request=new DeliverSm();

request.setServiceType("win");
request.setEsmClass(esm);
request.setProtocolId((byte)0x00);
request.setPriority((byte)0x00);
request.setRegisteredDelivery((byte)0x00);

if(_deliverSMObj.get(MapKeys.MSGTYPE).toString().equalsIgnoreCase(MessageType.UM)) {
	request.setDataCoding((byte)0x08);
} else {
	request.setDataCoding((byte)0x00);
}

Address sourceAddress=new Address((byte)0x01,(byte)0x01,_deliverSMObj.get(MapKeys.MOBILE).toString());
request.setSourceAddress(sourceAddress);
Address destinationAddress=new Address((byte)0x01,(byte)0x01,_deliverSMObj.get(MapKeys.SENDERID_ORG).toString());
request.setDestAddress(destinationAddress);


if(_deliverSMObj.get(MapKeys.UDH)==null) {
	if(_deliverSMObj.get(MapKeys.MSGTYPE).toString().equalsIgnoreCase(MessageType.EM))
		request.setShortMessage(dnmsg.getBytes());	
	else {
		request.setShortMessage(dnmsg.getBytes());
	}
} else {
	String udh=_deliverSMObj.get(MapKeys.UDH).toString();
	if(_deliverSMObj.get(MapKeys.MSGTYPE).toString().equalsIgnoreCase(MessageType.EM)) {	
		request.setShortMessage((udh +dnmsg).getBytes());
	} else {						
		request.setShortMessage((udh +dnmsg).getBytes());
	}
}
return request;
}

	
	
	
	
	private String getDnMessage(Byte esm,Map<String,Object> _deliverSMObj) {
		//if esm class is 4 it is dn message	
		
		String msg="";
		String dnMsg="";

		if(esm == 4) {
			
			int endIndex =0;
			if(_deliverSMObj.get(MapKeys.DNMSG) != null) {
				msg = _deliverSMObj.get(MapKeys.DNMSG).toString();
				endIndex = msg.length()>=20?19:msg.length();
			}
			
			msg = msg.substring(0, endIndex);
			
			dnMsg +="id:"+_deliverSMObj.get(MapKeys.ACKID);

			dnMsg +=" sub:1";

			dnMsg +=" dlvrd:1";

			String subDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(Long.parseLong(_deliverSMObj.get(MapKeys.RTIME).toString())));

			dnMsg +=" submit date:"+ subDtStr;
			
			String doneDtStr = new SimpleDateFormat(SMPP_DATE_FORMAT).format(new Date(Long.parseLong(_deliverSMObj.get(MapKeys.CARRIER_DONETIME).toString())));

			dnMsg +=" done date:"+ doneDtStr;

			String statusid=_deliverSMObj.get(MapKeys.STATUSID).toString();
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
	
	
private void writeResponse(Map<String,Object> aDn,Integer status) {
		
		try {	
				
			if(status==null&&aDn!=null)
				aDn.remove("STATUS");
			else if (status.equals(0))
				aDn.put("STATUS","SUCCESS");
			else if (status.equals(-2))
				aDn.put("STATUS","EXPIRED");
			else
				aDn.put("STATUS","FAILED");
			
			String msg_status=(String)aDn.get("STATUS");
			aDn.put(MapKeys.DNPOSTSTATUS, msg_status);
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.putAll(aDn);
			
			if(msg_status!=null&&msg_status.equals("SUCCESS")){
			new QueueSender().sendL("dnpostpool",aDn, false,logmap);
			logmap.put("smppdn_ status", "send to dnpostpool redis queue");

			}else{
				
				String queuename="smppdn_"+aDn.get(MapKeys.USERNAME).toString();
				new QueueSender().sendL(queuename, aDn, false,logmap);
				logmap.put("smppdn_ status", "send to "+queuename+" redis queue");

			}
			
			
			logmap.put("logname", "smpp_dlr_post");
			
			new FileWrite().write(logmap);

			
		} catch(Exception exp) {
			
			exp.printStackTrace();
		}
	}

}
