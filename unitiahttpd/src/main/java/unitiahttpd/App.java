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
	private static String PORT="";
	
	
	
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
        		
        }else if(uri.startsWith("/dntesta")){
        	
        	doProcessDNA(request,response);
        	
        }else if(uri.startsWith("/dntestb")){
        	
        	doProcessDNB(request,response);
        	
        }else if(uri.startsWith("/send")){
        	
        	doProcessQS(request,response);
        	
        }else if(uri.startsWith("/dnquery")){
        	
        	doProcessDNQuery(request,response);
        	
        }else if(uri.startsWith("/esms/sendsmsrequestDLT")){
        	
        	doProcessQS2(request,response);
        	
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
        	
        	doProcessUnknownContext(request,response,uri);
        }
        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    private void doProcessDNB(HttpServletRequest request, HttpServletResponse response) throws IOException {


    	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		logmap.put("module", "http receiver");
		
		logmap.put("logname", "dntestb");
		logmap.put("link", "dntestb");
		logmap.put("username", request.getParameter("username"));
		logmap.put("password", request.getParameter("password"));
		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println("OK");
		
	}

	private void doProcessDNA(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Map<String,Object> msgmap= new HashMap<String,Object>();

		GsonProcessor processor = new GsonProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		logmap.put("logname", "dntesta");

		String responsestring=processor.getRequestFromBody(request);
		
		logmap.put("link", "testdna");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println("OK");
		
	}

	private void doProcessQS2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
    	   Map<String,Object> msgmap= new HashMap<String,Object>();

   		RequestProcessor2 processor = new RequestProcessor2();
   		Bean.setDefaultValues(msgmap);
           Map<String, Object> logmap = new HashMap<String,Object>();
   		String responsestring=processor.processRequest(request,msgmap, logmap);
   		logmap.put("module", "http receiver");
   		if(responsestring.indexOf("402,")>-1){
   			logmap.put("logname", "esms_success");

   		}else{
   			logmap.put("logname", "esms_errorresponse");

   		}
   		logmap.put("link", "/esms/sendsmsrequestDLT");
   		logmap.put("responsestring", responsestring);

   		logmap.putAll(msgmap);
   		new FileWrite().write(logmap);
           response.getWriter().println(responsestring);
		
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

	private void doProcessUnknownContext(HttpServletRequest request, HttpServletResponse response,String uri) throws IOException {
		

    	String custIP		=	request.getHeader("X-FORWARDED-FOR");

		if(custIP==null){
			custIP=request.getRemoteHost();
		}
		
		if(custIP==null){
			custIP="";
		}
    	
		Map<String, Object> logmap = new HashMap<String,Object>();

		logmap.put("uri", uri);
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

	

	
	

	private void doProcessDNQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

    	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		DNQueryProcessor processor = new DNQueryProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		String responsestring=processor.processRequest(request,msgmap, logmap);
		String protocol="http";
		
		logmap.put("module", "dnqueryapi");
		
		String isSecure=(String)logmap.get("upgrade-insecure-requests");
		
		if(isSecure!=null&&isSecure.equals("1")){
			protocol="https";
		}
			logmap.put("logname", protocol+"_dnqueryapi_"+PORT);

		
		logmap.put("link", "dnquery");
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
		String protocol="http";
		
		logmap.put("module", "http receiver");
		
		String isSecure=(String)logmap.get("upgrade-insecure-requests");
		
		if(isSecure!=null&&isSecure.equals("1")){
			protocol="https";
		}
		if(responsestring.indexOf("100")>-1){
			logmap.put("logname", protocol+"_interface_"+PORT);

		}else{
			logmap.put("logname", protocol+"_errorresponse_"+PORT);

		}
		logmap.put("link", "send");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println(responsestring);

        
		
	}

	private void doProcessJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

		System.out.println("json Request");
    	
        Map<String,Object> msgmap= new HashMap<String,Object>();

		GsonProcessor processor = new GsonProcessor();
		Bean.setDefaultValues(msgmap);
        Map<String, Object> logmap = new HashMap<String,Object>();
		logmap.put("logname", "gsoninterface_"+PORT);

		String responsestring=processor.processRequestA(request,msgmap, logmap);
		logmap.put("module", "http receiver");
		
		logmap.put("link", "send");
		logmap.put("responsestring", responsestring);

		logmap.putAll(msgmap);
		new FileWrite().write(logmap);
        response.getWriter().println(responsestring);

		
	}

	public static void main(String[] args) throws Exception
    {
    	PORT=System.getenv("port");
    	Prop.getInstance();
    	new T().start();

        Server server = new Server(8080);
        server.setHandler(new App());

        server.start();
        server.join();
    }
    

}

