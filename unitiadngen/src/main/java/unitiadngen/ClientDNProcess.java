package unitiadngen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;

public class ClientDNProcess {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

    
    
        Map<String,Object> msgmap=new HashMap<String,Object>();
        msgmap.put(MapKeys.USERNAME, request.getParameter(MapKeys.USERNAME));
        msgmap.put(MapKeys.ACKID,request.getParameter(MapKeys.ACKID));
        msgmap.put(MapKeys.RTIME, request.getParameter(MapKeys.RTIME));
        msgmap.put("ctime", request.getParameter("ctime"));
        msgmap.put(MapKeys.STATUSID, request.getParameter(MapKeys.STATUSID));
        msgmap.put(MapKeys.PARAM1, request.getParameter(MapKeys.PARAM1));
        msgmap.put(MapKeys.PARAM2, request.getParameter(MapKeys.PARAM2));
        msgmap.put(MapKeys.PARAM3, request.getParameter(MapKeys.PARAM3));
        msgmap.put(MapKeys.PARAM4, request.getParameter(MapKeys.PARAM4));
        msgmap.put("totalsmscount", request.getParameter("totalsmscount"));
        msgmap.put("usedcredit", request.getParameter("usedcredit"));

        response.getWriter().println("received:ok");

        System.out.println(msgmap);
	
	}

	private void sendToQ(Map<String, Object> msgmap, Map<String, Object> logmap) throws IOException {
		

		
        new QueueSender().sendL("clientdnpool", msgmap, false,logmap);

		
	}
	
	
}
