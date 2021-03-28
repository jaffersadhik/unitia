package smpp2;

import java.util.Map;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;

public class DNTempBean {
	
	WindowFuture<Integer, PduRequest, PduResponse> future;
	Map<String, Object> dnMap;
	
	public WindowFuture<Integer, PduRequest, PduResponse> getFuture() {
		return future;
	}
	public void setFuture(WindowFuture<Integer, PduRequest, PduResponse> future) {
		this.future = future;
	}
	public Map<String, Object> getDnMap() {
		return dnMap;
	}
	public void setDnMap(Map<String, Object> dnMap) {
		this.dnMap = dnMap;
	}
	@Override
	public String toString() {
		return "DNTempBean [future=" + future +", dnMap=" + dnMap + "]";
	}

}
