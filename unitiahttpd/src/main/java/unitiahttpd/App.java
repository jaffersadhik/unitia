package unitiahttpd;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.http.RequestProcessor;
import com.winnovature.unitia.util.misc.Log;

import fi.iki.elonen.NanoHTTPD;


public class App extends NanoHTTPD {

    public App() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {

    	Map<String, String> msgmap = session.getParms();
    	Map<String, String> logmap = new HashMap<String,String>();
		RequestProcessor processor = new RequestProcessor();
		String responsestring=processor.processRequest(msgmap, logmap);		
		logmap.putAll(msgmap);
		new Log().log(logmap);
        return newFixedLengthResponse(responsestring);
    }
}
