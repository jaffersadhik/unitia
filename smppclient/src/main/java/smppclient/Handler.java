package smppclient;

import com.cloudhopper.smpp.PduAsyncResponse;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public class Handler implements SmppSessionHandler {

	
		private String queuename=null;
		
		private String smscid=null;
	
		private String systemid=null;
		
		private long lastUpdateTime=System.currentTimeMillis();

		
		public Handler(String queuename,String smscid,String systemid){
			
			this.queuename=queuename;
			this.smscid=smscid;
			this.systemid=systemid;
		}
	
	
		
		@Override
		public String lookupTlvTagName(short tag) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String lookupResultMessage(int commandStatus) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void fireUnrecoverablePduException(UnrecoverablePduException e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void fireUnknownThrowable(Throwable t) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void fireUnexpectedPduResponseReceived(PduResponse pduResponse) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void fireRecoverablePduException(RecoverablePduException e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public PduResponse firePduRequestReceived(PduRequest pduRequest) {
			
			if(pduRequest instanceof EnquireLink){
				
				return pduRequest.createResponse();
				
			}else if(pduRequest instanceof DeliverSm){
				
				DeliverSm request=(DeliverSm)pduRequest;
				
				String dn=new String(request.getShortMessage());
				
				TPS.getInstance(smscid).incrementDN();
				
				updateTime();
				
				sendToQueue(dn);
				
				return pduRequest.createResponse();
			}else{
				
				pduRequest.setCommandStatus(SmppConstants.STATUS_INVCMDID);
				
				return pduRequest.createResponse();
			}
		}
		
		private void sendToQueue(String dn) {
			
			System.out.println(dn);
			
		}

		@Override
		public void firePduRequestExpired(PduRequest pduRequest) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void fireExpectedPduResponseReceived(PduAsyncResponse pduAsyncResponse) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void fireChannelUnexpectedlyClosed() {
			// TODO Auto-generated method stub
			
		}
	

		public long getLastUpdateTime() {
			return lastUpdateTime;
		}



		public void updateTime(){
			
			lastUpdateTime=System.currentTimeMillis();
		}
}
