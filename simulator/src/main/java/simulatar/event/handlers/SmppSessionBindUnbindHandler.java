package simulatar.event.handlers;


import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;

import simulatar.manager.SessionManager;
import simulatar.server.SmppSessionHandlerInterface;

public class SmppSessionBindUnbindHandler implements SmppSessionHandlerInterface {
	
	
	public SmppSessionBindUnbindHandler() {
		
	}
	@Override
	public synchronized com.cloudhopper.smpp.SmppSessionHandler sessionCreated(
			Long sessionId, SmppServerSession session,
			BaseBindResp preparedBindResponse) throws SmppProcessingException {

		
		String remoteIPAddr = session.getConfiguration().getHost();
		
		
		SmppBindType bindType=session.getBindType();
		SessionEventHandler asessionHandler=null;
		
		try {
			String systemId=session.getConfiguration().getSystemId();
			
			asessionHandler=new SessionEventHandler(systemId,session,session.getBindType(),sessionId.toString());
			int bindTypeId=0;
			if(session.getBindType().equals(SmppBindType.RECEIVER))
				bindTypeId=SmppConstants.CMD_ID_BIND_RECEIVER;
			else if(session.getBindType().equals(SmppBindType.TRANSMITTER))
				bindTypeId=SmppConstants.CMD_ID_BIND_TRANSMITTER;
			else
				bindTypeId=SmppConstants.CMD_ID_BIND_TRANSCEIVER;
			
					
			
				SessionManager.getInstance().addSession(asessionHandler);
			
			
			preparedBindResponse.setSystemId(asessionHandler.getSystemId());
			
		} catch(Exception exp) {
			
			if(!(exp instanceof SmppProcessingException))
				throw new SmppProcessingException(SmppConstants.STATUS_SYSERR);
			else 
				throw (SmppProcessingException)exp;
		}
		
		return asessionHandler ;
	}

	@Override
	public synchronized void sessionDestroyed(Long sessionId, SmppServerSession session) {
		try {

			SessionManager.getInstance().removeSession(session);
			
			
		} catch(Exception exp) {
			exp.printStackTrace();
		}
	}

}

