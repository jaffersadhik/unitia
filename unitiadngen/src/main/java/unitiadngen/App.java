package unitiadngen;

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

import com.winnovature.unitia.util.misc.Bean;
import com.winnovature.unitia.util.misc.Log;

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

		new DNGenProcess().doProcess(request, response);

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception
    {
    	new T().start();

        Server server = new Server(8080);
        server.setHandler(new App());

        server.start();
        server.join();
    }
    
}

