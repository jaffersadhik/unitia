package com.winnovature.unitia.httpinterface.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.winnovature.unitia.util.redis.QueueSender;

public class DNReceiverProcessor {

	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

        PrintWriter out = response.getWriter();
        new QueueSender().sendL("dnreceiverpool", request.getParameterMap(), false,new HashMap());
        out.print("Ok");
        out.flush();
        out.close();

	
	}
		
}
