package unitiahttpd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.Log;

public class App extends AbstractHandler
{
    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
        ServletException
    {
        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8");

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        
        Map<String,String> msgmap=getMap(request);

        Map<String, String> logmap = new HashMap<String,String>();
		RequestProcessor processor = new RequestProcessor();
		Bean.setDefaultValues(msgmap);
		String responsestring=processor.processRequest(msgmap, logmap);		
		logmap.putAll(msgmap);
		new Log().log(logmap);
        response.getWriter().println(responsestring);

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception
    {
    	new T().start();

        Server server = new Server(8080);
        server.setHandler(new App());

        server.start();
        server.join();
    }
    

	private Map<String, String> getMap(HttpServletRequest request) {
		
        final StringTokenizer st = new StringTokenizer(request.getQueryString(), "&");
        final HashMap reqParam = new HashMap();
        while (st.hasMoreTokens())
        {
            final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
            String key = "";
            String value = "";
            if (st2.hasMoreTokens())
            {
                key = st2.nextToken();
                if (st2.hasMoreTokens())
                {
                    value = st2.nextToken();
                }
            }
            reqParam.put(key, value);
        }
        return reqParam;
    }
		

}

