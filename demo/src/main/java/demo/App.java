package demo;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;


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

        String uri=request.getRequestURI();
        
        if(uri.startsWith("/status")){
        	
        	doProcessJson(request,response);
        	
        }
    }
	private void doProcessJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	
   
        response.getWriter().println("Allahu Akbar");

		
	}

	public static void main(String[] args) throws Exception
    {
   
        System.out.print("System.lineSeparator()" +System.lineSeparator() );

        Server server = new Server(8080);
        server.setHandler(new App());

        server.start();
        server.join();
    }
	private static void startprocessor() throws IOException {
		
		
		boolean status=Pattern.compile(new SMSPatternAllowed().getPattern(), Pattern.CASE_INSENSITIVE).matcher("test \n test").matches();

		System.out.println(status);
		
	}
    

}

