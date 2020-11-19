package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.winnovature.unitia.util.misc.ToJsonString;


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
        	
        	doProcessJson(request,response);
        	
        }
    }
	private void doProcessJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	
   
        response.getWriter().println(getStatus());

		
	}
	
	private String getStatus(){
		
		List<String> status=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
		status.add(list.get(i).getStatus());
		}
		
		return ToJsonString.toString(status);
	}

	public static void main(String[] args) throws Exception
    {
   
        startprocessor();
        
        new Th().start();
        
    }
	private static void startprocessor() throws IOException {
		
		
		boolean status=Pattern.compile(new SMSPatternAllowed().getPattern(), Pattern.CASE_INSENSITIVE).matcher("test \n test").matches();

		System.out.println(status);
		
	}
    

}

