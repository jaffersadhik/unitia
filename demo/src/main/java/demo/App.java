package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;



public class App extends AbstractHandler
{
	
	static List<Processor> list=new ArrayList<Processor>();
	
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

        String uri=request.getRequestURI();
        
        if(uri.startsWith("/status")){
        	
        	StringBuffer sb=new StringBuffer();
        	
        	  Map<String, String> map = new HashMap<String, String>();

              Enumeration headerNames = request.getHeaderNames();
              while (headerNames.hasMoreElements()) {
                  String key = (String) headerNames.nextElement();
                  String value = request.getHeader(key);
                  map.put(key, value);
              }
              
              response.getWriter().write(map.toString());
        }
    }
	
	

	public static void main(String[] args) throws Exception
    {
		  Server server = new Server(8080);
	        server.setHandler(new App());

	        server.start();
	        server.join();
    }
	
    

}

