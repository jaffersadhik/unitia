package unitiahttpd;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.Log;

import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletException;

public class App {
 
  //Entry point for the application
    public static void main(String[] args) {
 
      //Start the server
        try {
        	new T().start();
            int port = 8080;
            int numThreads = 50;
            javaxt.http.Server server = new javaxt.http.Server(port, numThreads, new TestServlet());
            server.start();
        }
        catch (Exception e) {
            System.out.println("Server could not start because of an " + e.getClass());
            System.exit(1);
        }
    }
 


  //Custom Servlet
    private static class TestServlet extends HttpServlet {
 
        public void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException {
 

            Map<String,String> msgmap=getMap(request);

            Map<String, String> logmap = new HashMap<String,String>();
    		RequestProcessor processor = new RequestProcessor();
    		Bean.setDefaultValues(msgmap);
    		String responsestring=processor.processRequest(msgmap, logmap);		
    		logmap.putAll(msgmap);
    		new Log().log(logmap);
            response.getWriter().println(responsestring);

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
}