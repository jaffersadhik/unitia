
package unitiasmpp.event.handlers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cloudhopper.smpp.PduAsyncResponse;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.redis.SmppBind;

import unitiasmpp.manager.SessionManager;
import unitiasmpp.validators.SubmitSmValidator;

public class SessionEventHandler implements SmppSessionHandler {
	
	private String systemId;
	private SmppServerSession session;
	private String sessionId;

	private Date lastUsedTime=new Date();
	private Date prevUsedTime;
	private SubmitSmValidator validator=new SubmitSmValidator();
	private boolean expired;
	private boolean isInUse;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public SessionEventHandler(String systemId,SmppServerSession session,SmppBindType bindType,String sessionId) throws Exception {
		this.systemId=systemId;
		this.session=session;
		this.sessionId=sessionId;
	
	}
	
	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	
	
	public SmppServerSession getSession() {
		return session;
	}

	public void setSession(SmppServerSession session) {
		this.session = session;
	}

	public Date getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Date lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}
	
	
	public boolean isInUse() {
		return isInUse;
	}

	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}

	@Override
	public String lookupResultMessage(int commandStatus) {
		return null;
	}

	@Override
	public String lookupTlvTagName(short tag) {
		return null;
	}

	@Override
	public void fireChannelUnexpectedlyClosed() {

		SmppBind.getInstance().getBindCount(systemId, -1);
	}

	@Override
	public PduResponse firePduRequestReceived(PduRequest pduRequest) {

				
		PduResponse response = pduRequest.createResponse();
	
		int commandId = pduRequest.getCommandId();
		
		switch(commandId) {

		case SmppConstants.CMD_ID_SUBMIT_SM:
			try {
				
				
				validator.validate((SubmitSm)pduRequest,(SubmitSmResp)response, systemId,session.getConfiguration().getHost());

			} catch (Exception e) {
			
				response.setCommandStatus(SmppConstants.STATUS_SYSERR);
			}
			break;
		case SmppConstants.CMD_ID_ENQUIRE_LINK:
			try {
				
				Map<String,Object> logmap=new HashMap<String,Object>();
				logmap.put("logname", "enqirelink_"+systemId);
				logmap.put("time", new Date());
	    		logmap.put("smpp status ", response.getCommandStatus()+" ");

	    		new FileWrite().write(logmap);

			} catch (Exception e) {
			
				response.setCommandStatus(SmppConstants.STATUS_SYSERR);
			}
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
		lastUsedTime=new Date();
		return response;
	}

	
	@Override
	public void firePduRequestExpired(PduRequest pduRequest) {
		
		lastUsedTime=new Date();
	}

	@Override
	public void fireExpectedPduResponseReceived(PduAsyncResponse pduAsyncResponse) {
		lastUsedTime=new Date();
	}

	@Override
	public void fireUnexpectedPduResponseReceived(PduResponse pduResponse) {
		lastUsedTime=new Date();
	}

	@Override
	public void fireUnrecoverablePduException(UnrecoverablePduException e) {
		e.printStackTrace();
	}

	@Override
	public void fireRecoverablePduException(RecoverablePduException e) {
	
	}

	@Override
	public void fireUnknownThrowable(Throwable t) {
		// TODO Auto-generated method stub
		try {
			session.close();
			session.destroy();
		} catch(Exception error) {
			SessionManager.getInstance().removeSession(session);
			error.printStackTrace();
		}
		//lastUsedTime=new Date();
	}

	public void updateLastUsedTime() {
		prevUsedTime=lastUsedTime;
		lastUsedTime=new Date();
	}
	
	public void resetLastUsedTime() {
		lastUsedTime=prevUsedTime;
	}

}
