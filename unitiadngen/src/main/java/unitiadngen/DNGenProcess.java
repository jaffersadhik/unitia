package unitiadngen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;

public class DNGenProcess {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        String dlrurl = request.getParameter("dlr-url");
    
        Map<String,Object> msgmap=new HashMap<String,Object>();
        msgmap.put("dlrurl", dlrurl);
        msgmap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());
        Map<String,Object> logmap=new HashMap() ;
        logmap.putAll(msgmap);
        logmap.put(MapKeys.USERNAME,"dngen");
        sendToQ(msgmap,logmap);
        logmap.put("module", "DNGenProcess");
        response.getWriter().println("sent");
        new FileWrite().write(logmap);
	
	}

	private void sendToQ(Map<String, Object> msgmap, Map<String, Object> logmap) {

   
        new QueueSender().sendL("dngenpool", msgmap, false,logmap);
        
		
	}
}
