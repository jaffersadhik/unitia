
package simulatar.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;
import com.winnovature.unitia.util.misc.MapKeys;

import simulatar.validators.BindValidator;


public class DefaultSmppServerHandler implements SmppServerHandler, Serializable {

	private static final long serialVersionUID = 1L;


	private transient SmppSessionHandlerInterface smppSessionHandlerInterface = null;
	

	public DefaultSmppServerHandler() {
	}


	public SmppSessionHandlerInterface getSmppSessionHandlerInterface() {
		return smppSessionHandlerInterface;
	}

	public void setSmppSessionHandlerInterface(SmppSessionHandlerInterface smppSessionHandlerInterface) {
		this.smppSessionHandlerInterface = smppSessionHandlerInterface;
	}

	@Override
	public synchronized void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration,
			final BaseBind bindRequest) throws SmppProcessingException {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("logname", "bindlog");
		
		if(SmppConstants.CMD_ID_BIND_TRANSCEIVER==bindRequest.getCommandId()){
			logmap.put("bind Type","transiver" );

		}else if(SmppConstants.CMD_ID_BIND_TRANSMITTER==bindRequest.getCommandId()){
		
			logmap.put("bind Type","transmitter" );

		}else if(SmppConstants.CMD_ID_BIND_RECEIVER==bindRequest.getCommandId()){
		
			logmap.put("bind Type","receiver" );
		}

		if (this.smppSessionHandlerInterface == null) {
			logmap.put("error", "this.smppSessionHandlerInterface == null");

			new com.winnovature.unitia.util.misc.FileWrite().write(logmap);

			throw new SmppProcessingException(SmppConstants.STATUS_BINDFAIL);			
		}
		
		sessionConfiguration.setConnectTimeout(30000);
		sessionConfiguration.setBindTimeout(30000);
		sessionConfiguration.setWindowSize(1);
	
	

		try{
		new BindValidator().validate(bindRequest,sessionConfiguration.getHost(),logmap);
	
	
		}catch(Exception e){
			new com.winnovature.unitia.util.misc.FileWrite().write(logmap);

			 throw e;
		}
		
		new com.winnovature.unitia.util.misc.FileWrite().write(logmap);

	}

	@Override
	public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse)
			throws SmppProcessingException {
		
		if (this.smppSessionHandlerInterface == null) {
			throw new SmppProcessingException(SmppConstants.STATUS_BINDFAIL);
		}

		

		SmppSessionHandler smppSessionHandler = this.smppSessionHandlerInterface.sessionCreated(sessionId, session,
				preparedBindResponse);
		// need to do something it now (flag we're ready)
		session.serverReady(smppSessionHandler);
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put(MapKeys.USERNAME, session.getConfiguration().getSystemId());
		logmap.put("session", "created");
		logmap.put("bindlog","y");
		new com.winnovature.unitia.util.misc.FileWrite().write(logmap);
	}

	@Override
	public synchronized void sessionDestroyed(Long sessionId, SmppServerSession session) {
	
		if (this.smppSessionHandlerInterface != null) {
			this.smppSessionHandlerInterface.sessionDestroyed(sessionId, session);
		}
	
		session.destroy();
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put(MapKeys.USERNAME, session.getConfiguration().getSystemId());
		logmap.put("session", "destroyed");
		new com.winnovature.unitia.util.misc.FileWrite().write(logmap);
	}

}
