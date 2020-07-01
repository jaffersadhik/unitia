package unitiadngen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;

public class DNGenProcess {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        String dlrurl = request.getParameter("dlr-url");
    
        Map<String,String> msgmap=new HashMap<String,String>();
        msgmap.put("dlrurl", dlrurl);
        msgmap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());
        msgmap.put(MapKeys.USERNAME, request.getParameter("username"));
        Map<String,String> logmap=new HashMap() ;
        new QueueSender().sendL("dngenpool", msgmap, false,logmap);
        logmap.putAll(msgmap);
        logmap.put("status", "dngen receiver");
        response.getWriter().println("Sent.");


        new Log().log(logmap);
	
	}
}
