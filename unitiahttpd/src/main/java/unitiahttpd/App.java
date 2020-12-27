package unitiahttpd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
        
        if(uri.startsWith("/sendjson")){
        	
        	doProcessJson(request,response);
        	
        }else if(uri.startsWith("/send")){
        	
        	doProcessQS(request,response);
        	
        }else if(uri.startsWith("/balancecredits")){
        	
        	doProcessBalanceCredit(request,response);

        }else if(uri.startsWith("/changepassword")){
        	
        	doProcessChangePassword(request,response);

        }else if(uri.startsWith("/api/clientdn")){


            response.getWriter().println("received:ok");


        }else if(uri.startsWith("/missedcalls")){
        	
        	doProcessMissedCalls(request,response);

        }else if(uri.equals("/mo")){
        	
        	doProcessMO(request,response);

        }else{
        	
        	doProcessUnknownContext(request,response);
        }
        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    private void doProcessMO(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		ShortCodeProcessor processor = new ShortCodeProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		String responsestring=processor.processRequest(request,msgmap, logmap);
		logmap.put("module", "http receiver");
		if(responsestring.indexOf("100")>-1){
			logmap.put("logname", "shortcode");

		}else{
			logmap.put("logname", "shortcodeerrorresponse");

		}
		logmap.put("link", "mo");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println(responsestring);

		
		
	}

	private void doProcessMissedCalls(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
    	

    	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		MissedCallProcessor processor = new MissedCallProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		String responsestring=processor.processRequest(request,msgmap, logmap);
		logmap.put("module", "http receiver");
		if(responsestring.indexOf("100")>-1){
			logmap.put("logname", "missedcall");

		}else{
			logmap.put("logname", "missedcallerrorresponse");

		}
		logmap.put("link", "missedcalls");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println(responsestring);

		
	}

	private void doProcessUnknownContext(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	String custIP		=	request.getHeader("X-FORWARDED-FOR");

		if(custIP==null){
			custIP=request.getRemoteHost();
		}
		
		if(custIP==null){
			custIP="";
		}
    	
		Map<String, Object> logmap = new HashMap<String,Object>();

		logmap.put("username", "sys");
		logmap.put("module", "http receiver");
		logmap.put("logname", "unknowncntext");
		logmap.put("link", "unknowncntext");
		logmap.put("responsestring", "Context not Available");
		logmap.put("request",request.getParameterMap());
		logmap.put("custIP", custIP);
		new FileWrite().write(logmap);

    	
        response.getWriter().println("Context not Available");

    
		
	}

	private void doProcessChangePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

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
		
	}

	private void doProcessBalanceCredit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

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
		
	}

	private void doProcessQS(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	
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

        
		
	}

	private void doProcessJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		GsonProcessor processor = new GsonProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		logmap.put("logname", "httpinterface");

		String responsestring=processor.processRequest(request,msgmap, logmap);
		logmap.put("module", "http receiver");
		
		logmap.put("link", "send");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println(responsestring);

		
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

