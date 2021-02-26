package unitiasmpp.event.handlers;


import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.SmppBind;

import unitiasmpp.manager.SessionManager;
import unitiasmpp.server.SmppSessionHandlerInterface;

public class SmppSessionBindUnbindHandler implements SmppSessionHandlerInterface {
	
	
	public SmppSessionBindUnbindHandler() {
		
	}
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
			String bindTypestr="";
			if(session.getBindType().equals(SmppBindType.RECEIVER)){
				bindTypeId=SmppConstants.CMD_ID_BIND_RECEIVER;
				bindTypestr="RX";
			}
			else if(session.getBindType().equals(SmppBindType.TRANSMITTER)){
				bindTypeId=SmppConstants.CMD_ID_BIND_TRANSMITTER;
				bindTypestr="TX";
			}
			else{
				bindTypeId=SmppConstants.CMD_ID_BIND_TRANSCEIVER;
				bindTypestr="TRX";
			}
			int maxbind =  Integer.parseInt(PushAccount.instance().getPushAccount(systemId).get(MapKeys.SMPP_MAXBIND)); 
			int maxConnectionSize=500;
			
			int currentbind=(int) SmppBind.getInstance().getBindCount(asessionHandler.getSystemId(),1,session.getConfiguration().getHost(),bindTypestr);

			//max bind check
			if(currentbind>maxbind) {				
			
					throw new SmppProcessingException(SmppConstants.STATUS_ALYBND);
			}			
			
			if(SessionManager.getInstance().getTotalBindCount()<maxConnectionSize){
				SessionManager.getInstance().addSession(asessionHandler);
			}else{
				throw new SmppProcessingException(SmppConstants.STATUS_ALYBND);
			}
			
			
			preparedBindResponse.setSystemId(asessionHandler.getSystemId());
			
		} catch(Exception exp) {
			
			if(!(exp instanceof SmppProcessingException))
				throw new SmppProcessingException(SmppConstants.STATUS_SYSERR);
			else 
				throw (SmppProcessingException)exp;
		}
		
		return asessionHandler ;
	}

	public synchronized void sessionDestroyed(Long sessionId, SmppServerSession session) {
		try {

			SessionManager.getInstance().removeSession(session);
			
			 SmppBind.getInstance().getBindCount(session.getConfiguration().getSystemId(), -1);
			
		} catch(Exception exp) {
			exp.printStackTrace();
		}
	}

}

