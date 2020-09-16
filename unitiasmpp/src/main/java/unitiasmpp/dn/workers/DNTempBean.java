package unitiasmpp.dn.workers;

import java.util.Map;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;

import unitiasmpp.event.handlers.SessionEventHandler;

public class DNTempBean {
	
	WindowFuture<Integer, PduRequest, PduResponse> future;
	SessionEventHandler eventHandler;
	Map<String, Object> dnMap;
	
	public WindowFuture<Integer, PduRequest, PduResponse> getFuture() {
		return future;
	}
	public void setFuture(WindowFuture<Integer, PduRequest, PduResponse> future) {
		this.future = future;
	}
	public SessionEventHandler getEventHandler() {
		return eventHandler;
	}
	public void setEventHandler(SessionEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	public Map<String, Object> getDnMap() {
		return dnMap;
	}
	public void setDnMap(Map<String, Object> dnMap) {
		this.dnMap = dnMap;
	}
	@Override
	public String toString() {
		return "DNTempBean [future=" + future + ", eventHandler="
				+ eventHandler + ", dnMap=" + dnMap + "]";
	}

}
