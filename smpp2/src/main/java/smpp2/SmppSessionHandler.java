package smpp2;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.winnovature.unitia.util.misc.FileWrite;

public  class SmppSessionHandler extends DefaultSmppSessionHandler {
    
    private WeakReference<SmppSession> sessionRef;
    
    private SubmitSmValidator validator=new SubmitSmValidator();
    
    public SmppSessionHandler(SmppSession session) {
        this.sessionRef = new WeakReference<SmppSession>(session);
    }
    
    @Override
    public PduResponse firePduRequestReceived(PduRequest pduRequest) {
    	
        SmppSession session = sessionRef.get();
        


		
		PduResponse response = pduRequest.createResponse();
	
		int commandId = pduRequest.getCommandId();
		
		switch(commandId) {

		case SmppConstants.CMD_ID_SUBMIT_SM:
			try {
				
	    		SessionStore.getInstance().lastPDUTime(session);

				validator.validate((SubmitSm)pduRequest,(SubmitSmResp)response, session.getConfiguration().getSystemId(),session.getConfiguration().getHost());

			} catch (Exception e) {
			
				response.setCommandStatus(SmppConstants.STATUS_SYSERR);
			}
			break;
		case SmppConstants.CMD_ID_ENQUIRE_LINK:
			doEnquireLink(response,session);
			break;	
		case SmppConstants.CMD_ID_UNBIND:
			doUnbind(response,session);
			break;	
		case SmppConstants.CMD_ID_SUBMIT_MULTI:
			response.setCommandStatus(SmppConstants.STATUS_INVCMDID);
			break;
		case SmppConstants.CMD_ID_DELIVER_SM:
			//response.setCommandStatus(Data.ESME_RINVCMDID); 				
			response.setCommandStatus(SmppConstants.STATUS_INVCMDID);
			break;

		case SmppConstants.CMD_ID_DATA_SM:
			response.setCommandStatus(SmppConstants.STATUS_INVCMDID);
			break;

		case SmppConstants.CMD_ID_QUERY_SM:
			response.setCommandStatus(SmppConstants.STATUS_INVCMDID);			
			break;
		case SmppConstants.CMD_ID_CANCEL_SM:
			response.setCommandStatus(SmppConstants.STATUS_INVCMDID);					
			break;
		case SmppConstants.CMD_ID_REPLACE_SM:
			response.setCommandStatus(SmppConstants.STATUS_INVCMDID);
			break;
		}
		
		return response;
	
    }

	private void doEnquireLink(PduResponse response, SmppSession session) {
		try {
			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.put("logname", "enqirelink_"+session.getConfiguration().getSystemId());
    		logmap.put("smpp status ", response.getCommandStatus()+" ");
    		logmap.put("host ", session.getConfiguration().getHost()+" ");

    		SessionStore.getInstance().lastPDUTime(session);
    		
    		new FileWrite().write(logmap);

		} catch (Exception e) {
		
			response.setCommandStatus(SmppConstants.STATUS_SYSERR);
		}
		
	}

	private void doUnbind(PduResponse response,SmppSession session) {
		
		try {
			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.put("logname", "unbind_"+session.getConfiguration().getSystemId());
    		logmap.put("smpp status ", response.getCommandStatus()+" ");
    		logmap.put("host ",session.getConfiguration().getHost()+" ");

    		SessionStore.getInstance().remove(session);
    		
    		new FileWrite().write(logmap);

		} catch (Exception e) {
		
			response.setCommandStatus(SmppConstants.STATUS_SYSERR);
		}
		
	}
    
    
    
    
    
    
    
    
    
    
}