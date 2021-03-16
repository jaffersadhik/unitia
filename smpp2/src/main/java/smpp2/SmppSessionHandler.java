package smpp2;

import java.lang.ref.WeakReference;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;

public  class SmppSessionHandler extends DefaultSmppSessionHandler {
    
    private WeakReference<SmppSession> sessionRef;
    
    public SmppSessionHandler(SmppSession session) {
        this.sessionRef = new WeakReference<SmppSession>(session);
    }
    
    @Override
    public PduResponse firePduRequestReceived(PduRequest pduRequest) {
        SmppSession session = sessionRef.get();
        
        // mimic how long processing could take on a slower smsc
        try {
            //Thread.sleep(50);
        } catch (Exception e) { }
        
        return pduRequest.createResponse();
    }
    
    
    
    
    
    
    
    
    
    
}