package smpp2;

import java.util.HashMap;
import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;
import com.winnovature.unitia.util.misc.ErrorMessage;



public class DefaultSmppServerHandler implements SmppServerHandler {

    
    public void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration, final BaseBind bindRequest) throws SmppProcessingException {
        // test name change of sessions
        // this name actually shows up as thread context....
        sessionConfiguration.setName("Application.SMPP." + sessionConfiguration.getSystemId());
        
        

		
		Map<String,Object> logmap=new HashMap<String,Object>();				
		sessionConfiguration.setConnectTimeout(30000);
		sessionConfiguration.setBindTimeout(30000);
		sessionConfiguration.setWindowSize(10);
	
	

		try{
		
			new BindValidator().validate(bindRequest,sessionConfiguration.getHost(),logmap);
	
	
		}catch(Exception e){
			
			logmap.put("error msg",ErrorMessage.getMessage(e));
			
			new com.winnovature.unitia.util.misc.FileWrite().write(logmap);

			if(e instanceof SmppProcessingException){
				throw (SmppProcessingException)e;
			}else{
				throw new SmppProcessingException(SmppConstants.STATUS_BINDFAIL);			
			
			}
		}
		
		new com.winnovature.unitia.util.misc.FileWrite().write(logmap);

	

        //throw new SmppProcessingException(SmppConstants.STATUS_BINDFAIL, null);
    }

    
    public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse) throws SmppProcessingException {
        // need to do something it now (flag we're ready)
        session.serverReady(new SmppSessionHandler(session));
        SessionStore.getInstance().add(session);
    }

    
    public void sessionDestroyed(Long sessionId, SmppServerSession session) {
      
    	session.destroy();
        
      
    }

}
