package unitiahttpd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;

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
        if(uri.startsWith("/send")){
        	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		RequestProcessor processor = new RequestProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		String responsestring=processor.processRequest(request,msgmap, logmap);
		logmap.put("module", "http receiver");
		if(responsestring.indexOf("100")>-1){
			logmap.put("logname", "httpinterface");

		}else{
			logmap.put("logname", "httperrorresponse");

		}
		logmap.put("link", "send");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println(responsestring);

        }else if(uri.startsWith("/balancecredits")){
        	CreditBalanceProcessor processor = new CreditBalanceProcessor();
            Map<String,Object> msgmap= new HashMap<String,Object>();

    		String responsestring=processor.processRequest(request,msgmap);
    		Map<String, Object> logmap = new HashMap<String,Object>();

    		logmap.put("module", "http receiver");
    		logmap.put("logname", "balancecredits");
    		logmap.put("link", "balancecredits");
    		logmap.put("responsestring", responsestring);

    		logmap.putAll(msgmap);
    		new FileWrite().write(logmap);

    		
    		response.getWriter().println(responsestring);

        }else if(uri.startsWith("/changepassword")){
        	ChangePasswordProcessor processor = new ChangePasswordProcessor();
            Map<String,Object> msgmap= new HashMap<String,Object>();

    		String responsestring=processor.processRequest(request,msgmap);		
    		
    		Map<String, Object> logmap = new HashMap<String,Object>();

    		logmap.put("module", "http receiver");
    		logmap.put("logname", "changepassword");
    		logmap.put("link", "changepassword");
    		logmap.put("responsestring", responsestring);

    		logmap.putAll(msgmap);
    		new FileWrite().write(logmap);
            response.getWriter().println(responsestring);

        }else{
        	String custIP		=	request.getHeader("X-FORWARDED-FOR");

			if(custIP==null){
				custIP=request.getRemoteHost();
			}
			
			if(custIP==null){
				custIP="";
			}
        	
    		Map<String, Object> logmap = new HashMap<String,Object>();

    		logmap.put("module", "http receiver");
    		logmap.put("logname", "unknowncntext");
    		logmap.put("link", "unknowncntext");
    		logmap.put("responsestring", "Context not Available");
    		logmap.put("request",request.getParameterMap());
    		logmap.put("custIP", custIP);
    		new FileWrite().write(logmap);

        	
            response.getWriter().println("Context not Available");

        }
        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception
    {
    	
    	Prop.getInstance();
    	new T().start();

        Server server = new Server(8080);
        server.setHandler(new App());

        server.start();
        server.join();
    }
    

}

