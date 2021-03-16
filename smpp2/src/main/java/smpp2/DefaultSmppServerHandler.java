package smpp2;

import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;


public class DefaultSmppServerHandler implements SmppServerHandler {

    
    public void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration, final BaseBind bindRequest) throws SmppProcessingException {
        // test name change of sessions
        // this name actually shows up as thread context....
        sessionConfiguration.setName("Application.SMPP." + sessionConfiguration.getSystemId());

        //throw new SmppProcessingException(SmppConstants.STATUS_BINDFAIL, null);
    }

    
    public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse) throws SmppProcessingException {
        // need to do something it now (flag we're ready)
        session.serverReady(new SmppSessionHandler(session));
        SessionStore.getInstance().add(session);
    }

    
    public void sessionDestroyed(Long sessionId, SmppServerSession session) {
        // print out final stats
        if (session.hasCounters()) {
         //   logger.info(" final session rx-submitSM: {}", session.getCounters().getRxSubmitSM());
        }
        
        // make sure it's really shutdown
        session.destroy();
        
        session.getConfiguration();
    }

}
