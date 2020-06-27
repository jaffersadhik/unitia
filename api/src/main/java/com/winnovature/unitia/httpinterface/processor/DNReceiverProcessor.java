package com.winnovature.unitia.httpinterface.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.redis.QueueSender;

public class DNReceiverProcessor {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        PrintWriter out = response.getWriter();
        Map<String,String> msgmap=getMap(request);
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

		Map<String,String> requestmap=new HashMap<String,String>();
		Enumeration dd=request.getParameterNames();
		  if(dd.hasMoreElements()){
			  
			  String name=dd.nextElement().toString();
			  String value=request.getParameter(name);
			  requestmap.put(name, value);
		  }
		return requestmap;
	}
		
}
