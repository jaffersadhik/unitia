package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    	list.add(new Processor("msg1"));

    	list.add(new Processor("msg2"));
    	
    	list.add(new Processor("msg3"));
    	
    	if(args==null||args.length==0){
        Server server = new Server(8080);
        server.setHandler(new App());

        server.start();
        server.join();
        
    	}else if(args[0].equals("msg1")){
    		new Processor(args[0]);
    	}else if(args[0].equals("msg2")){
    		new Processor(args[0]);
    	}else if(args[0].equals("msg3")){
    		new Processor(args[0]);
    	}else{
    	
    		Runtime.getRuntime().exec("java -jar /unitiad.jar -Xms=180M -Xmx=180M msg1");
    		Runtime.getRuntime().exec("java -jar /unitiad.jar -Xms=180M -Xmx=180M msg2");
    		Runtime.getRuntime().exec("java -jar /unitiad.jar -Xms=180M -Xmx=180M msg3");
    	}
        
        
    }
    

}

