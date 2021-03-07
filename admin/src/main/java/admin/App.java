package admin;

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

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
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
        String uri=request.getRequestURI();

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);
        if(uri.startsWith("/admin/login")){


        	String username=request.getParameter("username");
        	
        	Map<String,String> data=PushAccount.instance().getPushAccount(username.toLowerCase());
        	
        	if(data==null){
        		
        	}else{
        		
        		String password=request.getParameter("username");
        	
        		String dbpassword=data.get(MapKeys.PASSWORD).toString();
        		
        		if(password.equals(dbpassword)){
        			
        		}else{
        			
        		}
        				
        	
        	}
        	
            response.getWriter().println("OK");

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

