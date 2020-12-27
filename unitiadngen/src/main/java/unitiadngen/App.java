package unitiadngen;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

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

        if(uri.startsWith("/api/dngen")){


		new DNGenProcess().doProcess(request, response);

        }else if(uri.startsWith("/api/clientdn")){


    		new ClientDNProcess().doProcess(request, response);

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



















