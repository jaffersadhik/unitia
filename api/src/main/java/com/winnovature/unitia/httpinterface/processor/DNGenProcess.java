package com.winnovature.unitia.httpinterface.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.redis.QueueSender;

public class DNGenProcess {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        PrintWriter out = response.getWriter();
        String dlrurl = request.getParameter("dlr-url");
    
        Map<String,String> msgmap=new HashMap<String,String>();
        msgmap.put("dlrurl", dlrurl);
        
        new QueueSender().sendL("dngenpool", msgmap, false,new HashMap());
        
        out.print("Sent.");
        out.flush();
        out.close();

	
	}
}
