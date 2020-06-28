package com.winnovature.unitia.httpinterface.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;

public class DNReceiverProcessor {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        PrintWriter out = response.getWriter();
        Map<String,String> msgmap=getMap(request);
        String dr=msgmap.get(MapKeys.DR);
        msgmap.put(MapKeys.DR,URLDecoder.decode(dr));
        Map<String,String> logmap=new HashMap();
        new QueueSender().sendL("dnreceiverpool", msgmap, false,logmap);
        out.print("Ok");
        out.flush();
        out.close();
        logmap.putAll(msgmap);
        logmap.put("pool","dnreceiver");
        
        new Log().log(logmap);
	
	}

	private Map<String, String> getMap(HttpServletRequest request) {
		
        final StringTokenizer st = new StringTokenizer(request.getQueryString(), "&");
        final HashMap reqParam = new HashMap();
        while (st.hasMoreTokens())
        {
            final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
            String key = "";
            String value = "";
            if (st2.hasMoreTokens())
            {
                key = st2.nextToken();
                if (st2.hasMoreTokens())
                {
                    value = st2.nextToken();
                }
            }
            reqParam.put(key, value);
        }
        return reqParam;
    }
		
}
